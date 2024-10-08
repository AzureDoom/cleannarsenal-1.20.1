package com.cleannrooster.cleannarsenal.mixin;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import com.cleannrooster.cleannarsenal.entities.SpinAttack;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellCastSyncHelper;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(value = PlayerEntity.class)
public class PlayerEntityMixin implements PlayerInterface {
    public List<Entity> targets = new ArrayList<>();
    public List<Float> yaws = new ArrayList<>();


    public boolean renderedalready = false;
    public boolean isrenderedalready(){
        return renderedalready;
    }

    @Override
    public void setRendered() {
        renderedalready = true;
    }

    @Override
    public void voidPlaces() {
        positions = new ArrayList<>();
    }

    public List<Vec3d> positions = new ArrayList<>();

    public float modifierfmj = 1;
    public boolean second = false;
    @Override
    public float getFMJModifier() {
        return modifierfmj;
    }

    @Override
    public void setFMJModifier(float fmj) {
        this.modifierfmj = fmj;

    }
    @Override
    public void nextSwing() {
        this.second = !this.second;

    }
    @Override
    public boolean isSecondSwing() {
        return this.second;

    }
    public void EntityAdd(Entity entity) {
        targets.add(entity);
    }

    @Override
    public void clearEntities() {
        targets = new ArrayList<>();
    }

    public List<Entity> getTargetEntities() {
        return targets;
    }
    public List<Vec3d> getPositions() {
        return positions;
    }

    @Override
    public List<Float> getYaws() {
        return yaws;
    }

    public void addNewPosition(Vec3d vec3d){
        positions.add(vec3d);
        PlayerEntity entity = (PlayerEntity)  (Object) this;
            SpinAttack spinAttack = new SpinAttack(entity.getWorld(),entity);
            spinAttack.setPosition(entity.getPos());
            spinAttack.setOwner(entity);
            entity.getWorld().spawnEntity(spinAttack);
    }

    @Override
    public void addNewYaw(Float vec3d) {
        yaws.add(vec3d);
    }



}
