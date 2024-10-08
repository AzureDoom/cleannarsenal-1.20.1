package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import net.spell_power.api.SpellSchools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpellSchools.class)
public class SpellSchoolsMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void static_tail_Cleann(CallbackInfo ci) {
        if(Cleannarsenal.RANGED_FROST.attribute != null) {
            SpellSchools.register(Cleannarsenal.RANGED_FROST); // Trigger registration
        }
        if(Cleannarsenal.RANGED_FIRE.attribute != null) {

            SpellSchools.register(Cleannarsenal.RANGED_FIRE); // Trigger registration
        }
        if(Cleannarsenal.RANGED_FIRE_TIMING.attribute != null) {

            SpellSchools.register(Cleannarsenal.RANGED_FIRE_TIMING); // Trigger registration
        }

    }
}