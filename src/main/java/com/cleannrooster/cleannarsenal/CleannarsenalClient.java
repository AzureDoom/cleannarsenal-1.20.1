package com.cleannrooster.cleannarsenal;

import com.cleannrooster.cleannarsenal.Items.Items;
import com.cleannrooster.cleannarsenal.client.item.renderer.ThrownRendererEmissive;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellContainerHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.casting.SpellCasterClient;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_power.api.SpellPowerMechanics;

import java.util.Objects;

public class CleannarsenalClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Cleannarsenal.MODID,"places"), (client, handler, buf, responseSender) -> {

                int i = buf.readInt();
                Entity entity = handler.getWorld().getEntityById(i);
                if(entity != null && entity.isAlive() && entity instanceof PlayerInterface playerInterface){
                    playerInterface.addNewPosition(entity.getPos());
                }

        });
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Cleannarsenal.MODID,"void"), (client, handler, buf, responseSender) -> {
            int i = buf.readInt();
            Entity entity = handler.getWorld().getEntityById(i);
            if(entity != null && entity.isAlive() && entity instanceof PlayerInterface playerInterface){
                System.out.println("asdf");
                playerInterface.voidPlaces();
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(server -> {
                    PlayerEntity player = server.player;
                    World level = server.world;

                    if (player != null && level != null) {
                        double speed = player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * player.getAttributeValue(SpellPowerMechanics.HASTE.attribute) * 0.01 * 4;
                        BlockHitResult result = level.raycast(new RaycastContext(player.getPos(), player.getPos().add(0, -2, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, player));
                        if (player.isSneaking()) {
                            speed *= 0;
                        }
                        double modifier = 0;
                        if (result.getType() == HitResult.Type.BLOCK) {
                            modifier = 1;
                        }

                        if (player instanceof SpellCasterEntity caster && SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "roll")) != null &&  Objects.equals(caster.getCurrentSpell(), SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "roll"))) ) {
                            Spell spell = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "roll"));

                            speed *= 1.5;
                            player.setVelocity(player.getRotationVec(1).subtract(0, player.getRotationVec(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0, player.getVelocity().y, 0));
                        }
                    }
                }
        );

        ModelPredicateProviderRegistry.register(Items.LASERBOW.item, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if( entity instanceof SpellCasterClient caster && caster.getCurrentSpell() != null &&
                    caster.getSpellCastProgress() != null &&
                    caster.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID,"barrage")))){
                return ((float)(caster.getSpellCastProcess().progress(entity.getWorld().getTime()).ratio()*((caster.getCurrentSpell().cast.duration*20)) % caster.getCurrentSpell().cast.channel_ticks))/(float)caster.getCurrentSpell().cast.channel_ticks;
            }
            return (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f;
        });
        ModelPredicateProviderRegistry.register(Items.LASERBOW.item, new Identifier("pulling"), (stack, world, entity, seed) -> entity != null && entity instanceof SpellCasterClient client && client.isCastingSpell() ? 1.0f : 0.0f);
        EntityRendererRegistry.register(Cleannarsenal.LASERARROW, (context) -> new ThrownRendererEmissive(context,2.0F,true));

    }
}
