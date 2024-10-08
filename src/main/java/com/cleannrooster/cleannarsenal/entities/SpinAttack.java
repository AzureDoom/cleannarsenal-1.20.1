package com.cleannrooster.cleannarsenal.entities;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.google.gson.Gson;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.render.SpellProjectileRenderer;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static net.minecraft.item.Items.IRON_SWORD;

public class SpinAttack extends PathAwareEntity implements InventoryOwner, GeoEntity {
    public   Entity caster = null;
    private int ownerUuid;

    private SimpleInventory inventory;
    public float range;
    private  DefaultedList<ItemStack> handItems ;

    private Spell spell;
    private SpellHelper.ImpactContext context;
    private Entity followedTarget;
    public Vec3d previousVelocity;
    private Spell.ProjectileData clientSyncedData;
    private static String NBT_SPELL_DATA = "Spell.Data";
    private static String NBT_IMPACT_CONTEXT = "Impact.Context";
    private AnimatableInstanceCache factory = AzureLibUtil.createInstanceCache(this);
    public static final TrackedData<Integer> OWNER;

    static {
        OWNER = DataTracker.registerData(SpinAttack.class, TrackedDataHandlerRegistry.INTEGER);

    }

    public static final RawAnimation SPIN = RawAnimation.begin().thenPlay("animation.hexblade.spin");
    public SpinAttack(EntityType<? extends SpinAttack> entityType, World world) {
        super(entityType, world);
        this.prevPitch = 0;
        this.prevYaw = 0;
        this.setYaw(0);
        this.setPitch(0);
        this.handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);

        this.range = 128.0F;
    }

    public SpinAttack(World world, LivingEntity owner) {
        super(Cleannarsenal.SPINATTACK, world);

        this.range = 128.0F;
        this.setRotation(owner.getYaw(),owner.getPitch());
    }

    public SpinAttack(World world, LivingEntity caster, double x, double y, double z, SpellProjectile.Behaviour behaviour, Spell spell, Entity target, SpellHelper.ImpactContext context) {
        this(world, caster);
        this.setPosition(x, y, z);
        this.spell = spell;
        Gson gson = new Gson();
        this.context = context;
       // this.setFollowedTarget(target);
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OWNER, -1);

    }
    @Override
    public void tick() {
        System.out.println(this.getOwner());
        if(this.getOwner() != null&& getOwner() instanceof LivingEntity living && living.hasStatusEffect(Cleannarsenal.SHADEWALK)){

                this.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,this.getOwner().getEyePos().add(living.getRotationVector().multiply(8)));

        }

        else{
            if(!this.getWorld().isClient()) {
                this.discard();
            }
        }
        updateTrackedPositionAndAngles(this.getX(),this.getY(),this.getZ(),this.getYaw(),this.getPitch(),1,true);
        baseTick();

    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        return false;
    }




    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "baseAnim", event -> PlayState.CONTINUE)
                        .triggerableAnim("spin", SPIN));
    }







    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }



    @Override
    public void onPlayerCollision(PlayerEntity player) {

    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }



    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getId();
            this.caster = entity;
            this.dataTracker.set(OWNER,entity.getId());
        }
    }

    @Nullable
    public Entity getOwner() {
        if(this.dataTracker.get(OWNER) != -1) {
            return this.getWorld().getEntityById(this.dataTracker.get(OWNER));
        }else{
            return null;
        }
    }
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Owner", this.ownerUuid);


    }
    public void readCustomDataFromNbt(NbtCompound nbt) {

        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getInt("Owner");
            this.caster = null;
        }

    }

}
