package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Armors.Armors;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import com.cleannrooster.cleannarsenal.api.Attributes;
import com.cleannrooster.cleannarsenal.entities.FakeClientConnection;
import com.cleannrooster.cleannarsenal.entities.FakePlayerTemporary;
import com.cleannrooster.cleannarsenal.spells.Spells;
import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.sun.jna.platform.KeyboardUtils;
import io.netty.buffer.Unpooled;
import net.bettercombat.client.animation.AnimationRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.client.input.SpellHotbar;
import net.spell_engine.internals.SpellCastSyncHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.WorldScheduler;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterClient;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.mixin.client.ClientPlayerEntityMixin;
import net.spell_engine.network.Packets;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.AnimationHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.Supplier;

import static com.cleannrooster.cleannarsenal.Cleannarsenal.MODID;
import static net.spell_engine.internals.SpellHelper.*;

@Mixin(value = SpellCastSyncHelper.class,priority = 1)
public class SpellHelperMixin {

        @Inject(at = @At("HEAD"), method = "clearCasting", cancellable = true)
    private static void clearFMJ(PlayerEntity player,  CallbackInfo info) {
            Spell spell = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "fmj"));
            Spell spell2 = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "fmjinstant"));
            Spell spell3 = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "earthquake"));
            Spell spell4 = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "roll"));

            if (player instanceof SpellCasterEntity entity && player instanceof PlayerInterface playerInterface && entity.getCurrentSpell() != null &&
                    entity.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "magicmissile")))) {
                Spells.shootMagicMissiles(player, entity, playerInterface.getTargetEntities());

            }

            if (player instanceof SpellCasterEntity entity &&
                    ( !entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "fmjinstant")) ||
                            !entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "fmj"))) &&
                    entity.getCurrentSpell() != null && entity.getSpellCastProcess() != null &&
                    entity.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "fmj")))) {

                SpellHelper.AmmoResult ammoResult = ammoForSpell(player, spell, player.getMainHandStack());
                if (ammoResult.satisfied()) {
                    Entity living = TargetHelper.targetFromRaycast(player, spell.range, target -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, player, target));
                    List<Entity> list = new ArrayList<Entity>();
                    if(living != null) {
                        list.add(living);
                    }
                    SpellHelper.performSpell(player.getWorld(),player,new Identifier(Cleannarsenal.MODID, "fmjinstant"),list, SpellCast.Action.CHANNEL,1.0F);
                    ParticleHelper.sendBatches(player, spell.release.particles);
                    SoundHelper.playSound(player.getWorld(),player,spell.release.sound);
                    if(player.getWorld() instanceof ServerWorld serverWorld) {
                        AnimationHelper.sendAnimation(player,PlayerLookup.tracking(player), SpellCast.Animation.RELEASE,spell.release.animation,1.0F);
                        AnimationHelper.sendAnimation(player,List.of((ServerPlayerEntity) player), SpellCast.Animation.RELEASE,spell.release.animation,1.0F);

                    }
                    imposeCooldown(player, new Identifier(Cleannarsenal.MODID, "fmjinstant"), spell4, entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio());
                    imposeCooldown(player, new Identifier(Cleannarsenal.MODID, "fmj"), spell4, entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio());

                }
                if(!(player instanceof FakePlayerTemporary) && player.getWorld() instanceof ServerWorld serverWorld && player.getRandom().nextFloat() < player.getAttributeValue(Attributes.ECHO)*0.01-1 ) {
                    Identifier spellId = new Identifier(Cleannarsenal.MODID, "fmjinstant");

                    ((WorldScheduler) serverWorld).schedule(10, () -> {
                 /*       serverWorld.getServer().getPlayerManager().loadPlayerData(fake);
                        onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), fake);

                        fake.setServerWorld(serverWorld);*/

                    });
                    int offset = 10;
                    if(SpellRegistry.getSpell(spellId).release.animation != null && AnimationRegistry.animations.get( SpellRegistry.getSpell(spellId).release.animation) != null){
                        offset = AnimationRegistry.animations.get( SpellRegistry.getSpell(spellId).release.animation).endTick;
                    }
                    /*
                        fake.server.getPlayerManager().remove(fake);
*/
                }

            }
            if (player instanceof SpellCasterEntity entity &&
                    (
                            !entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "roll"))) &&
                    entity.getCurrentSpell() != null && entity.getSpellCastProcess() != null &&
                    entity.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "roll")))){
                imposeCooldown(player, new Identifier(Cleannarsenal.MODID, "roll"), spell4, entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio());

            }

    }
private static void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
        GameProfile gameProfile = player.getGameProfile();
        UserCache userCache = player.server.getUserCache();
        String string;
        if (userCache != null) {
            Optional<GameProfile> optional = userCache.getByUuid(gameProfile.getId());
            string = (String)optional.map(GameProfile::getName).orElse(gameProfile.getName());
            userCache.add(gameProfile);
        } else {
            string = gameProfile.getName();
        }

        NbtCompound nbtCompound = player.server.getPlayerManager().loadPlayerData(player);
        RegistryKey var24;
        if (nbtCompound != null) {
            DataResult var10000 = DimensionType.worldFromDimensionNbt(new Dynamic(NbtOps.INSTANCE, nbtCompound.get("Dimension")));
        } else {
            var24 = World.OVERWORLD;
        }

        ServerWorld serverWorld = player.getServerWorld();
        ServerWorld serverWorld2;
        if (serverWorld == null) {
            serverWorld2 = player.server.getOverworld();
        } else {
            serverWorld2 = serverWorld;
        }

        player.setServerWorld(serverWorld2);
        String string2 = "local";
        if (connection.getAddress() != null) {
            string2 = connection.getAddress().toString();
        }

        WorldProperties worldProperties = serverWorld2.getLevelProperties();
        player.setGameMode(nbtCompound);
        ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(player.server, connection, player);
        GameRules gameRules = serverWorld2.getGameRules();
        boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
/*
        serverPlayNetworkHandler.sendPacket(new GameJoinS2CPacket(player.getId(), worldProperties.isHardcore(), player.interactionManager.getGameMode(), player.interactionManager.getPreviousGameMode(), player.server.getWorldRegistryKeys(), player.server.getPlayerManager().syncedRegistryManager, serverWorld2.getDimensionKey(), serverWorld2.getRegistryKey(), BiomeAccess.hashSeed(serverWorld2.getSeed()), this.getMaxPlayerCount(), this.viewDistance, this.simulationDistance, bl2, !bl, serverWorld2.isDebugWorld(), serverWorld2.isFlat(), player.getLastDeathPos(), player.getPortalCooldown()));
*/
        serverPlayNetworkHandler.sendPacket(new FeaturesS2CPacket(FeatureFlags.FEATURE_MANAGER.toId(serverWorld2.getEnabledFeatures())));
        serverPlayNetworkHandler.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, (new PacketByteBuf(Unpooled.buffer())).writeString(player.server.getServerModName())));
        serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
        serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.getAbilities()));
        serverPlayNetworkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot));
        serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(player.server.getRecipeManager().values()));
        player.server.getPlayerManager().sendCommandTree(player);
        player.getStatHandler().updateStatSet();
        player.getRecipeBook().sendInitRecipesPacket(player);
        player.server.forcePlayerSampleUpdate();
        MutableText mutableText;
        if (player.getGameProfile().getName().equalsIgnoreCase(string)) {
            mutableText = Text.translatable("multiplayer.player.joined", new Object[]{player.getDisplayName()});
        } else {
            mutableText = Text.translatable("multiplayer.player.joined.renamed", new Object[]{player.getDisplayName(), string});
        }

        //serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        ServerMetadata serverMetadata = player.server.getServerMetadata();
        if (serverMetadata != null) {
            player.sendServerMetadata(serverMetadata);
        }

        player.networkHandler.sendPacket(PlayerListS2CPacket.entryFromPlayer(List.of(player)));
        player.server.getPlayerManager().getPlayerList().add(player);/*
        player.server.getPlayerManager().players.put(player.getUuid(), player);*/
        player.server.getPlayerManager().sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(player)));
        player.server.getPlayerManager().sendWorldInfo(player, serverWorld2);
        serverWorld2.onPlayerConnected(player);
        player.server.getBossBarManager().onPlayerConnect(player);
        player.server.getResourcePackProperties().ifPresent((properties) -> {
            player.sendResourcePackUrl(properties.url(), properties.hash(), properties.isRequired(), properties.prompt());
        });
        Iterator var18 = player.getStatusEffects().iterator();

        while(var18.hasNext()) {
            StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var18.next();
            serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), statusEffectInstance));
        }

        if (nbtCompound != null && nbtCompound.contains("RootVehicle", 10)) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound("RootVehicle");
            Entity entity = EntityType.loadEntityWithPassengers(nbtCompound2.getCompound("Entity"), serverWorld2, (vehicle) -> {
                return !serverWorld2.tryLoadEntity(vehicle) ? null : vehicle;
            });
            if (entity != null) {
                UUID uUID;
                if (nbtCompound2.containsUuid("Attach")) {
                    uUID = nbtCompound2.getUuid("Attach");
                } else {
                    uUID = null;
                }

                Iterator var21;
                Entity entity2;
                if (entity.getUuid().equals(uUID)) {
                    player.startRiding(entity, true);
                } else {
                    var21 = entity.getPassengersDeep().iterator();

                    while(var21.hasNext()) {
                        entity2 = (Entity)var21.next();
                        if (entity2.getUuid().equals(uUID)) {
                            player.startRiding(entity2, true);
                            break;
                        }
                    }
                }

                if (!player.hasVehicle()) {
                    entity.discard();
                    var21 = entity.getPassengersDeep().iterator();

                    while(var21.hasNext()) {
                        entity2 = (Entity)var21.next();
                        entity2.discard();
                    }
                }
            }
        }

        player.onSpawn();
    }

}
