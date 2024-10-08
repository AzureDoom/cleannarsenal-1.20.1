package com.cleannrooster.cleannarsenal.entities;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FakePlayerTemporary extends FakePlayer {
    public LivingEntity owner = null;

    protected FakePlayerTemporary(ServerWorld world, GameProfile profile,LivingEntity owner, float yaw, float pitch) {
        super(world, profile);
        this.owner = owner;
        this.getDataTracker().set(YAW,yaw);
        this.getDataTracker().set(PITCH,pitch);
    }
    public float prevYaw =         this.getDataTracker().get(YAW);;
    public float prevPitch = this.getDataTracker().get(PITCH);
    public float pitch =         this.getDataTracker().get(YAW);;
    public float yaw = this.getDataTracker().get(PITCH);


    private static final TrackedData<Float> YAW;

    private static final TrackedData<Float> PITCH;

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(YAW, 0.0F);
        this.dataTracker.startTracking(PITCH, 0F);
    }
    static{
        YAW = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
        PITCH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

    }


    public static FakePlayerTemporary get(ServerWorld world, GameProfile profile, LivingEntity owner, float yaw, float pitch) {
        Objects.requireNonNull(world, "World may not be null.");
        Objects.requireNonNull(profile, "Game profile may not be null.");

       return new FakePlayerTemporary(world, profile, owner, yaw, pitch);
    }
    @Override
    public void tick() {
        baseTick();
        if(this.age > 200){
            this.discard();
        }
    }

    @Override
    public AttributeContainer getAttributes() {
        if(this.owner != null){
            return owner.getAttributes();
        }
        return super.getAttributes();
    }

    @Override
    public boolean isTeammate(Entity other) {
        if(this.owner != null){
            return this.owner.isTeammate(other);
        }
        return super.isTeammate(other);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
    }


    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {

    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public float getPitch() {
        return this.getDataTracker().get(PITCH);
    }
    @Override
    public float getYaw() {
        return this.getDataTracker().get(YAW);
    }
    @Override
    public boolean isTeamPlayer(AbstractTeam team) {
        if(this.owner != null){
            return this.owner.isTeamPlayer(team);
        }
        return super.isTeamPlayer(team);
    }

    @Override
    public @Nullable AbstractTeam getScoreboardTeam() {
        if(this.owner != null){
            return this.owner.getScoreboardTeam();
        }
        return super.getScoreboardTeam();
    }


}
