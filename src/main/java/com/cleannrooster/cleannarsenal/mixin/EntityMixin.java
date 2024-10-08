package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Items;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchools;
import net.spell_power.mixin.DamageSourcesAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LivingEntity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tickCleann(CallbackInfo info) {
        LivingEntity living = (LivingEntity) (Object) this;
        if(living instanceof PlayerInterface playerInterface && !living.getWorld().isClient() ) {
            if (living.age % 20 == 0 && living.hasStatusEffect(Cleannarsenal.SHADEWALK)) {
                if(living.getWorld() instanceof ServerWorld world){
                    Collection<ServerPlayerEntity> players = PlayerLookup.tracking(living);
                    for(ServerPlayerEntity player : players){
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(living.getId());
                        ServerPlayNetworking.send(player,new Identifier(Cleannarsenal.MODID,"places"),buf);
                    }
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(living.getId());
                    ServerPlayNetworking.send((ServerPlayerEntity) living,new Identifier(Cleannarsenal.MODID,"places"),buf);

                }
                playerInterface.addNewPosition(living.getPos());
            }
            if(living.age % 6 == 1){
            for(Vec3d vec3d : playerInterface.getPositions()) {
                produceParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, vec3d);
                produceParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, vec3d);
            }
            }
        }
        ;
    }
    protected void produceParticles(ParticleEffect parameters, Vec3d vec3d) {
        LivingEntity living = (LivingEntity) (Object) this;

        for(int i = 0; i < 5; ++i) {

            double d = living.getRandom().nextGaussian() * 0.01;
            double e = -living.getRandom().nextFloat() * 1;
            double f = living.getRandom().nextGaussian() * 0.01;
            if(living.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(parameters, vec3d.getX(), vec3d.y + living.getHeight()/2, vec3d.getZ(),1, 0,0, 0,0.01);
            }
        }

    }
        @Inject(at = @At("HEAD"), method = "dropLoot", cancellable = true)

    private void dropLootMagic(DamageSource damageSource, boolean causedByPlayer, CallbackInfo info) {
        LivingEntity living = (LivingEntity) (Object) this;
        Registry<DamageType> registry = ((DamageSourcesAccessor)living.getDamageSources()).getRegistry();

        if(damageSource.getAttacker() instanceof PlayerEntity player && player.getRandom().nextFloat() < 0.05F * living.getMaxHealth()/20 ) {
            if( damageSource.getType().equals(registry.entryOf(DamageTypes.MAGIC).value())){
                double total = 0;
                double value1 = SpellPower.getSpellPower(SpellSchools.ARCANE, player).randomValue();
                double value2 = SpellPower.getSpellPower(SpellSchools.FIRE, player).randomValue();
                double value3 = SpellPower.getSpellPower(SpellSchools.FROST, player).randomValue();
                if (value1 +
                        value2 +
                        value3 > 0) {
                    total += value1;
                    total += value2;
                    total += value3;
                }
                double seed = player.getRandom().nextDouble() * total;

                if (seed <= value1) {
                    living.dropItem(Items.STRANGE.item);
                }
                if (seed > value1 && seed <= value1 + value2) {
                    living.dropItem(Items.BURNT.item);

                }
                if (seed > value1 + value2) {
                    living.dropItem(Items.FROZEN.item);

                }
            }
            else if(player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) >= 10){
                living.dropItem(Items.HEAVY.item);

            }
        }
    }
}
