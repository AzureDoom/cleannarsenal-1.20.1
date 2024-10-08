package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Armors.Armors;
import com.cleannrooster.cleannarsenal.api.Attributes;
import com.cleannrooster.cleannarsenal.entities.FakeClientConnection;
import com.cleannrooster.cleannarsenal.entities.FakePlayerTemporary;
import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import net.bettercombat.client.animation.AnimationRegistry;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.dimension.DimensionType;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellCastSyncHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.WorldScheduler;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.utils.AnimationHelper;
import net.spell_engine.utils.TargetHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.*;
import java.util.function.Supplier;

import static net.spell_engine.internals.SpellHelper.channelValueMultiplier;
import static net.spell_engine.internals.SpellHelper.isChanneled;

@Mixin(SpellHelper.class)
public class SpellHelperMixin2 {
    @Inject(at = @At("HEAD"), method = "performSpell", cancellable = true)
    private static void performSpellStop(World world, PlayerEntity player, Identifier spellId, List<Entity> targets, SpellCast.Action action, float progress, CallbackInfo info) {
        float castingSpeed = ((SpellCasterEntity)player).getCurrentCastingSpeed();
        Spell spell = SpellRegistry.getSpell(spellId);


        if(spellId.equals(new Identifier(Cleannarsenal.MODID,"fmj"))){
            info.cancel();
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

       // serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
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
