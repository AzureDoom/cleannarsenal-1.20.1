package com.cleannrooster.cleannarsenal.spells;

import carpet.script.language.Sys;
import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.spell_engine.particle.ParticleHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShadeWalk extends CustomStatusEffect {
    public ShadeWalk(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        if(entity instanceof PlayerInterface playerInterface){
            System.out.println("hey");
            playerInterface.voidPlaces();
            if(entity.getWorld() instanceof ServerWorld world){
                Collection<ServerPlayerEntity> players = PlayerLookup.tracking(entity);
                for(ServerPlayerEntity player : players){
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(entity.getId());
                    ServerPlayNetworking.send(player,new Identifier(Cleannarsenal.MODID,"void"),buf);
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(entity.getId());
                ServerPlayNetworking.send((ServerPlayerEntity) entity,new Identifier(Cleannarsenal.MODID,"void"),buf);

            }
        }
    }
}
