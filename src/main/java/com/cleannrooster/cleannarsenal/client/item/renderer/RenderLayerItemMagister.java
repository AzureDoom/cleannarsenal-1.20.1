package com.cleannrooster.cleannarsenal.client.item.renderer;

import com.cleannrooster.cleannarsenal.entities.SpinAttack;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.BlockAndItemGeoLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RenderLayerItemMagister extends BlockAndItemGeoLayer<SpinAttack> {
    public RenderLayerItemMagister(GeoRenderer<SpinAttack> entityRendererIn) {
        super(entityRendererIn);
    }

    private static final String LEFT_HAND = "leftItem";
    private static final String RIGHT_HAND = "rightItem";

    @Override
    protected ItemStack getStackForBone(GeoBone bone, SpinAttack animatable) {
        // Retrieve the items in the entity's hands for the relevant bone
        if (bone.getName().equals(RIGHT_HAND)) {
            if (animatable.getOwner() instanceof LivingEntity living) {
                return living.getMainHandStack();
            }
            else{
                return new ItemStack(Items.IRON_SWORD);

            }
        }
        if (bone.getName().equals(LEFT_HAND)) {
            if (animatable.getOwner() instanceof LivingEntity living) {

                return living.getOffHandStack();
            }
            else{
                return new ItemStack(Items.IRON_SWORD);
            }

        }
        return ItemStack.EMPTY;
    }
    @Override
    protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, SpinAttack animatable) {
        // Apply the camera transform for the given hand
        return switch (bone.getName()) {
            case LEFT_HAND, RIGHT_HAND -> ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
            default -> ModelTransformationMode.NONE;
        };
    }

    // Do some quick render modifications depending on what the item is
    @Override
    protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, SpinAttack animatable,
                                      VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
       /* if (stack == animatable.getMainHandStack()) {
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

            if (stack.getItem() instanceof ShieldItem)
                poseStack.translate(0, 0.125, -0.25);
        }
        else if (stack == animatable.getOffHandStack()) {
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

            if (stack.getItem() instanceof ShieldItem) {
                poseStack.translate(0, 0.125, 0.25);
                poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            }
        }
*/
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }

}
