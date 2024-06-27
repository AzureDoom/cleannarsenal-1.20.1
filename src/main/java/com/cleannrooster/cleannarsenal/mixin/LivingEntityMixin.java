package com.cleannrooster.cleannarsenal.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.utils.TargetHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

import static com.cleannrooster.cleannarsenal.Cleannarsenal.MODID;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(at = @At("HEAD"), method = "isBlocking", cancellable = true)
    private void blocking( final CallbackInfoReturnable<Boolean> info) {
        LivingEntity player2 = ((LivingEntity) (Object) this);
        if (player2 instanceof ServerPlayerEntity actualplayer && actualplayer instanceof SpellCasterEntity caster) {

            if (SpellRegistry.getSpell(new Identifier(MODID, "riposte")) != null && Objects.equals(caster.getCurrentSpell(), SpellRegistry.getSpell(new Identifier(MODID, "riposte")))) {
                info.setReturnValue(true);
            }

            if (SpellRegistry.getSpell(new Identifier(MODID, "roll")) != null && Objects.equals(caster.getCurrentSpell(), SpellRegistry.getSpell(new Identifier(MODID, "roll")))) {
                info.setReturnValue(true);
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void hurtreal(final DamageSource player, float f, final CallbackInfoReturnable<Boolean> info) {

        LivingEntity player2 = ((LivingEntity) (Object) this);

        if (player2 instanceof ServerPlayerEntity actualplayer && actualplayer instanceof SpellCasterEntity caster) {
            {
                ItemStack stack = actualplayer.getMainHandStack();

                if (SpellRegistry.getSpell(new Identifier(MODID, "riposte")) != null && Objects.equals(caster.getCurrentSpell(), SpellRegistry.getSpell(new Identifier(MODID, "riposte")))) {
                    Spell spell = caster.getCurrentSpell();
                    if (player2.blockedByShield(player) && player2.getMainHandStack() != null && player.getAttacker() != null && player2.blockedByShield(player)) {
                        caster.setSpellCastProcess(null);
                        List<Entity> list = TargetHelper.targetsFromArea(player2, spell.range, spell.release.target.area, null);
                        if(player.getSource() != null && !list.contains(player.getSource()) && player.getSource() instanceof LivingEntity living && living.distanceTo(player2) <= 3){
                            list.add(player.getSource());
                        }
                        SpellHelper.performSpell(player2.getWorld(), actualplayer, new Identifier(MODID, "riposte"), list,
                                 SpellCast.Action.RELEASE, 1F);
                        info.cancel();

                    }
                }
                if (SpellRegistry.getSpell(new Identifier(MODID, "roll")) != null && Objects.equals(caster.getCurrentSpell(), SpellRegistry.getSpell(new Identifier(MODID, "roll")))) {
                    Spell spell = caster.getCurrentSpell();
                    if (player2.getMainHandStack() != null && player.getAttacker() != null) {
                        if (player.getSource() instanceof LivingEntity living) {
                            caster.setSpellCastProcess(null);
                            List<Entity> entities = TargetHelper.targetsFromArea(player2, spell.range, spell.release.target.area, null);
                            if (!entities.contains(living) && living.distanceTo(player2) <= 3) {
                                entities.add(living);
                            }
                            SpellHelper.performSpell(player2.getWorld(), actualplayer, new Identifier(MODID, "roll"), entities,
                                    SpellCast.Action.RELEASE, 1F);
                        }
                        info.cancel();

                    }
                }
            }
        }

    }
}
