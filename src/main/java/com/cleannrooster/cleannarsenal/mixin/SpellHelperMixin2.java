package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SpellHelper.class)
public class SpellHelperMixin2 {
    @Inject(at = @At("HEAD"), method = "performSpell", cancellable = true)

    private static void performSpellStop(World world, PlayerEntity player, Identifier spellId, List<Entity> targets, SpellCast.Action action, float progress, CallbackInfo info) {
        if(spellId.equals(new Identifier(Cleannarsenal.MODID,"fmj"))){
            info.cancel();
        }

    }
}
