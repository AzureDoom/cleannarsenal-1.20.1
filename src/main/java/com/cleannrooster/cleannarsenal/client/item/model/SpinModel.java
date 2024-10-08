package com.cleannrooster.cleannarsenal.client.item.model;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.entities.SpinAttack;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class SpinModel<T extends SpinAttack> extends GeoModel<SpinAttack> {

    @Override
    public Identifier getModelResource(SpinAttack reaver) {
            return new Identifier(Cleannarsenal.MODID,"geo/arms.geo.json");
    }
    @Override
    public Identifier getTextureResource(SpinAttack reaver) {
        return new Identifier(Cleannarsenal.MODID,"textures/mob/hexblade_none.png");
    }

    @Override
    public Identifier getAnimationResource(SpinAttack reaver) {
        return new Identifier(Cleannarsenal.MODID,"animations/shade.animation.json");
    }

    public void setArmAngle(Arm humanoidArm, MatrixStack poseStack) {
        this.translateAndRotate(poseStack);
    }
    public void translateAndRotate(MatrixStack arg) {
        arg.translate((double)(1), (double)(0 / 16.0F), (double)(0 / 16.0F));
        arg.scale(2, 2, 2);



    }
}
