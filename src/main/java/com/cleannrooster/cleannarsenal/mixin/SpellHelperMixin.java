package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import com.cleannrooster.cleannarsenal.spells.Spells;
import com.google.common.base.Suppliers;
import com.sun.jna.platform.KeyboardUtils;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
                    (!entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "fmjinstant")) ||
                            !entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "fmj"))) &&
                    entity.getCurrentSpell() != null && entity.getSpellCastProcess() != null &&
                    entity.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "fmj")))) {

                SpellHelper.AmmoResult ammoResult = ammoForSpell(player, spell, player.getMainHandStack());
                if (ammoResult.satisfied()) {
                    Entity living = TargetHelper.targetFromRaycast(player, spell.range, target -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, player, target));
                    ArrayList<Entity> list = new ArrayList<>();
                    if (living != null) {
                        list.add(living);

                    }
                    float modifier = 1;
                    modifier -= Math.abs(entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio() - 0.75F);
                    modifier = (float) Math.min(1.25, modifier);
                    if (player instanceof PlayerInterface playerInterface) {
                        playerInterface.setFMJModifier(modifier);
                    }
                    shootProjectile(player.getWorld(), player, living, new SpellInfo(spell2, new Identifier(Cleannarsenal.MODID, "fmjinstant")), new ImpactContext());
                    ParticleHelper.sendBatches(player, spell.release.particles);
                    SoundHelper.playSound(player.getWorld(), player, spell.release.sound);
                    Supplier<Collection<ServerPlayerEntity>> trackingPlayers = Suppliers.memoize(() -> {
                        return PlayerLookup.tracking(player);
                    });
                    AnimationHelper.sendAnimation(player, (Collection) trackingPlayers.get(), SpellCast.Animation.RELEASE, spell.release.animation, 1.0F);
                    imposeCooldown(player, new Identifier(Cleannarsenal.MODID, "fmjinstant"), spell2, entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio());
                    imposeCooldown(player, new Identifier(Cleannarsenal.MODID, "fmj"), spell, entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio());

                    player.addExhaustion(spell.cost.exhaust * SpellEngineMod.config.spell_cost_exhaust_multiplier);
                    if (SpellEngineMod.config.spell_cost_durability_allowed && spell.cost.durability > 0) {

                        player.getMainHandStack().damage(spell.cost.durability, player, (playerObj) -> {
                            playerObj.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                            playerObj.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND);
                        });
                    }

                    if (ammoResult.ammo() != null && spell.cost.consume_item) {
                        for (int i = 0; i < player.getInventory().size(); ++i) {
                            ItemStack stack = player.getInventory().getStack(i);
                            if (stack.isOf(ammoResult.ammo().getItem())) {
                                stack.decrement(1);
                                if (stack.isEmpty()) {
                                    player.getInventory().removeOne(stack);
                                }
                                break;
                            }
                        }
                    }
                }

            }
            if (player instanceof SpellCasterEntity entity &&
                    (
                            !entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "roll"))) &&
                    entity.getCurrentSpell() != null && entity.getSpellCastProcess() != null &&
                    entity.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "roll")))){
                imposeCooldown(player, new Identifier(Cleannarsenal.MODID, "roll"), spell4, entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio());

            }
                if (player instanceof SpellCasterEntity entity &&
                    (
                            !entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "earthquake"))) &&
                    entity.getCurrentSpell() != null && entity.getSpellCastProcess() != null &&
                    entity.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "earthquake"))))  {
                if(entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio() > 0.5F) {
                    player.getWorld().createExplosion(player,player.getX(),player.getY(),player.getZ(),(float)( SpellPower.getSpellPower(ExternalSpellSchools.PHYSICAL_MELEE,player).nonCriticalValue()*0.1), World.ExplosionSourceType.NONE);
                    double playerx = player.getX();
                    double playery = player.getBoundingBox().getCenter().getY();
                    double playerz = player.getZ();
                    if (entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio() > 0.75F) {
                        ((WorldScheduler)player.getWorld()).schedule(20,()-> {
                            if(player.getWorld() instanceof ServerWorld serverWorld) {
                                for(ParticleBatch batck : SpellRegistry.getSpell(new Identifier(MODID,"earthquake")).release.particles) {
                                    PacketByteBuf packet = (new Packets.ParticleBatches(Packets.ParticleBatches.SourceType.COORDINATE,
                                            List.of(new Packets.ParticleBatches.Spawn(player.getId(), player.getYaw(), player.getPitch(), new Vec3d(playerx, playery, playerz),batck)))

                                            .write(2));
                                    if (player instanceof ServerPlayerEntity) {
                                        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                                        try {
                                            if (ServerPlayNetworking.canSend(serverPlayer, Packets.ParticleBatches.ID)) {
                                                ServerPlayNetworking.send(serverPlayer, Packets.ParticleBatches.ID, packet);
                                            }
                                        } catch (Exception var3) {
                                            var3.printStackTrace();
                                        }
                                        Supplier<Collection<ServerPlayerEntity>> trackers = Suppliers.memoize(() -> {
                                            return PlayerLookup.tracking(player);
                                        });
                                        trackers.get().forEach((serverPlayerx) -> {
                                            try {
                                                if (ServerPlayNetworking.canSend(serverPlayer, Packets.ParticleBatches.ID)) {
                                                    ServerPlayNetworking.send(serverPlayer, Packets.ParticleBatches.ID, packet);
                                                }
                                            } catch (Exception var3) {
                                                var3.printStackTrace();
                                            }
                                        });
                                    }
                                }

                            }
                            player.getWorld().createExplosion(player, playerx, playery, playerz, (float) (2 * SpellPower.getSpellPower(ExternalSpellSchools.PHYSICAL_MELEE, player).nonCriticalValue() * 0.1), World.ExplosionSourceType.NONE);

                        });
                    }
                    ParticleHelper.sendBatches(player, spell3.release.particles);
                    SoundHelper.playSound(player.getWorld(), player, spell3.release.sound);
                    imposeCooldown(player, new Identifier(Cleannarsenal.MODID, "earthquake"), spell3, entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio());
                    player.addExhaustion(spell.cost.exhaust * SpellEngineMod.config.spell_cost_exhaust_multiplier);
                    if (SpellEngineMod.config.spell_cost_durability_allowed && spell.cost.durability > 0) {

                        player.getMainHandStack().damage(spell3.cost.durability, player, (playerObj) -> {
                            playerObj.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                            playerObj.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND);
                        });
                    }
                }
                Supplier<Collection<ServerPlayerEntity>> trackingPlayers = Suppliers.memoize(() -> {
                    return PlayerLookup.tracking(player);
                });


                AnimationHelper.sendAnimation(player, (Collection) trackingPlayers.get(), SpellCast.Animation.RELEASE, spell3.release.animation, 1.0F);

            }
    }

}
