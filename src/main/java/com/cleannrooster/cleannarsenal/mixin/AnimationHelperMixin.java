package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.utils.AnimationHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Objects;

@Mixin(AnimationHelper.class)
public class AnimationHelperMixin {
    @Inject(at = @At("HEAD"), method = "sendAnimation", cancellable = true)

    private static  void sendAnimationCleann(PlayerEntity animatedPlayer, Collection<ServerPlayerEntity> trackingPlayers, SpellCast.Animation type, String name, float speed, CallbackInfo info) {
        if(name != null && animatedPlayer instanceof PlayerInterface playerInterface&&  name.equals(new Identifier(Cleannarsenal.MODID,"sword_swing_first").toString())){
            if(playerInterface.isSecondSwing()) {
                AnimationHelper.sendAnimation(animatedPlayer, trackingPlayers, type, new Identifier(Cleannarsenal.MODID, "sword_swing_second").toString(), speed);
                info.cancel();
            }
            playerInterface.nextSwing();
        }
        if(name != null && animatedPlayer instanceof SpellCasterEntity caster){
            if(!Objects.equals(name, new Identifier(Cleannarsenal.MODID, "winddown").toString()) && caster.getCurrentSpell() != null && caster.getCurrentSpell().equals(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID,"earthquake"))) && caster.getSpellCastProcess().progress(animatedPlayer.getWorld().getTime()).ratio() < 0.5){
                AnimationHelper.sendAnimation(animatedPlayer, trackingPlayers, type, new Identifier(Cleannarsenal.MODID, "winddown").toString(), speed);
                info.cancel();
            }
        }
    }
}
