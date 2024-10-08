package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.cleannrooster.cleannarsenal.api.Attributes.ECHO;


@Mixin(EntityAttributes.class)
public class EntityAttributeMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void static_tail_Arsenal(CallbackInfo ci) {

        Registry.register(Registries.ATTRIBUTE,new Identifier("cleannarsenal","echo"),ECHO);

    }
}
