package com.cleannrooster.cleannarsenal;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public interface PlayerInterface {

    float getFMJModifier();
    void setFMJModifier(float fmj);
    void EntityAdd(Entity entity);
    void clearEntities();
    void nextSwing();
    boolean isSecondSwing();
    boolean isrenderedalready();
    void setRendered();
    void voidPlaces();


    List<Entity> getTargetEntities();
    List<Vec3d> getPositions();
    List<Float> getYaws();
    void addNewPosition(Vec3d vec3d);
    void addNewYaw(Float vec3d);

}
