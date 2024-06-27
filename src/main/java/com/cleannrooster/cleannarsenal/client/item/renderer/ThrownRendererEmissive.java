package com.cleannrooster.cleannarsenal.client.item.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static com.cleannrooster.cleannarsenal.Items.Items.LASERARROWITEM;

public class ThrownRendererEmissive<T extends Entity & FlyingItemEntity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    public ThrownRendererEmissive(EntityRendererFactory.Context context, float f, boolean bl) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
        this.shadowOpacity = 0.75F;
    }

    public void render(T itemEntity, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i) {

        if(itemEntity.age <= 2){
            return;
        }
        poseStack.push();
        ItemStack itemStack = new ItemStack(LASERARROWITEM.item);
        int j = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.getWorld(), (LivingEntity)null, itemEntity.getId());
        boolean bl = bakedModel.hasDepth();
        int k = 1;
        float h = 0.25F;
        float l = 0;
        float m = bakedModel.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
        poseStack.translate(0.0D, (double)(l + 0.25F * m), 0.0D);
        float o = bakedModel.getTransformation().ground.scale.x();
        float p = bakedModel.getTransformation().ground.scale.y();
        float q = bakedModel.getTransformation().ground.scale.z();
        float s;
        float t;
        if (!bl) {
            float r = -0.0F * (float)(k - 1) * 0.5F * o;
            s = -0.0F * (float)(k - 1) * 0.5F * p;
            t = -0.09375F * (float)(k - 1) * 0.5F * q;
            poseStack.translate((double)r, (double)s, (double)t);
        }
        double y = itemEntity.getYaw();
        double x = itemEntity.getPitch();


        for(int u = 0; u < k; ++u) {
            poseStack.push();
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) (y-180)));
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) (x)));

            poseStack.scale(1.5F,1.5F,1.5F);
            this.itemRenderer.renderItem(new ItemStack(LASERARROWITEM.item), ModelTransformationMode.GROUND, false, poseStack, multiBufferSource, i, OverlayTexture.DEFAULT_UV, bakedModel);
            poseStack.pop();
            if (!bl) {
                poseStack.translate((double)(0.0F * o), (double)(0.0F * p), (double)(0.09375F * q));
            }
        }

        poseStack.pop();
    }


    @Override
    public Identifier getTexture(T entity) {
        return null;
    }
}