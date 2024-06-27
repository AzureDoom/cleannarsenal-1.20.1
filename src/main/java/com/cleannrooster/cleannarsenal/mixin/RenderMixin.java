package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class RenderMixin<E extends Entity>{

    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;
@Shadow
private  BufferBuilderStorage bufferBuilders;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"), method = "render", cancellable = true)
    public void renderCleann(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo info) {
        WorldRenderer renderer = (WorldRenderer) (Object) this;
        if (MinecraftClient.getInstance().world != null) {
                Entity entity = MinecraftClient.getInstance().cameraEntity;
                if (entity instanceof PlayerInterface playerInterface && entity instanceof PlayerEntity player && player.hasStatusEffect(Cleannarsenal.SHADEWALK)) {

                    for (int ii = 0; ii < playerInterface.getPositions().size() ; ii++) {
                        Vec3d vec3d = playerInterface.getPositions().get(ii);
                        if (!(MinecraftClient.getInstance().options.getPerspective().equals(Perspective.FIRST_PERSON) &&
                                entity.equals(MinecraftClient.getInstance().cameraEntity) &&
                                vec3d.distanceTo(MinecraftClient.getInstance().cameraEntity.getPos()) < 2
                        )) {

                            double j = vec3d.getX() - camera.getPos().getX();
                            double k = vec3d.getY() - camera.getPos().getY();
                            double l = vec3d.getZ() - camera.getPos().getZ();
                            float g = entity.getYaw();
                            entityRenderDispatcher.render((PlayerEntity) entity, j, k, l, g, 0, matrices, bufferBuilders.getEntityVertexConsumers(), 8);
                        }



                }

            }
        }

    }
}
