package com.cleannrooster.cleannarsenal.client.item.renderer;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.client.item.model.SpinModel;
import com.cleannrooster.cleannarsenal.entities.SpinAttack;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.DynamicGeoEntityRenderer;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

public class SpinRenderer<T extends SpinAttack> extends DynamicGeoEntityRenderer<SpinAttack> {


    private static final Identifier DEFAULT_LOCATION = new Identifier(Cleannarsenal.MODID,"textures/mob/hexblade_none.png");





    public SpinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SpinModel<>());
        addRenderLayer(new RenderLayerItemMagister(this));
        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }



    @Override
    protected int getBlockLight(SpinAttack entity, BlockPos pos) {
        return 4;
    }
    @Override
    protected int getSkyLight(SpinAttack entity, BlockPos pos) {
        return 4;
    }

    public Identifier getTextureLocation(SpinAttack p_114891_) {

        return DEFAULT_LOCATION;
    }





}