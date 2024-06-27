package com.cleannrooster.cleannarsenal.entities;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.Items.Items;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_engine.utils.VectorHelper;
import net.spell_power.api.SpellPower;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static com.cleannrooster.cleannarsenal.Cleannarsenal.LASERARROW;
import static net.spell_engine.internals.SpellHelper.impactTargetingMode;

public class LaserArrow extends PersistentProjectileEntity implements FlyingItemEntity {

    public LivingEntity target = null;
    public int chaining = 0;
    public boolean burst;
    private Entity cachedTarget;
    private UUID targetUUID;
    private LivingEntity old;

    public void setTarget(@Nullable Entity entity) {
        if (entity != null) {
            this.targetUUID = entity.getUuid();
            this.cachedTarget = entity;
        }

    }
    @Nullable
    public Entity getTarget() {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
            return this.cachedTarget;
        } else if (this.targetUUID != null && this.getWorld() instanceof ServerWorld) {
            this.cachedTarget = ((ServerWorld)this.getWorld()).getEntity(this.targetUUID);
            return this.cachedTarget;
        } else {
            return null;
        }
    }
    public void writeCustomDataToNbt(NbtCompound compoundTag) {
        if (this.target != null) {
            compoundTag.putUuid("Target", this.targetUUID);
        }
        super.writeCustomDataToNbt(compoundTag);

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if(!this.getWorld().isClient() && entityHitResult.getEntity() != null && entityHitResult.getEntity() instanceof LivingEntity living && living != this.getOwner() && living != this.old && this.getOwner() instanceof PlayerEntity player){
            living.timeUntilRegen = 0;
            Predicate<Entity> selectionPredicate = (target) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                        );
            };
            this.chaining--;
            List<LivingEntity> list = this.getWorld().getTargets(LivingEntity.class,TargetPredicate.DEFAULT,living,this.getBoundingBox().expand(6));
            list.removeIf(entity -> !selectionPredicate.test(entity));
            if(!list.isEmpty() &&this.chaining > 0 && this.getWorld().getClosestEntity(list, TargetPredicate.DEFAULT,living,living.getX(),living.getY(),living.getZ()) != null){
                LaserArrow laserArrow = new LaserArrow(LASERARROW, player.getWorld());
                laserArrow.setOwner(player);
                laserArrow.setTarget(this.getWorld().getClosestEntity(list, TargetPredicate.DEFAULT,living,living.getX(),living.getY(),living.getZ()));
                laserArrow.old = living;
                laserArrow.setDamage(this.getDamage());
                laserArrow.setPunch(this.getPunch());
                if(this.isOnFire()){
                    laserArrow.setOnFireFor(100);
                }
                laserArrow.chaining = this.chaining;
                laserArrow.setPosition(living.getX(),living.getY()+living.getHeight()/2,living.getZ());
                laserArrow.setVelocity(laserArrow.getTarget().getX()-living.getX(),laserArrow.getTarget().getY()-living.getY(),laserArrow.getTarget().getZ()-living.getZ(),2.0F,1.0F);
                living.getWorld().spawnEntity(laserArrow);
            }
            PlayerEntity player1 = player;
            ItemStack stack = player1.getMainHandStack();
            if(this.burst) {
                    Spell spell = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "frostbloomproc"));


                    if (player1 instanceof SpellCasterEntity entity && !entity.getCooldownManager().isCoolingDown(new Identifier(Cleannarsenal.MODID, "frostbloomproc"))) {
                        List<Entity> targets = player1.getWorld().getOtherEntities((LivingEntity) null, living.getBoundingBox().expand(6), selectionPredicate);
                        int i = 0;
                        if (entity.getCurrentSpell() != null && entity.getSpellCastProcess() != null) {
                            i = (int) (entity.getSpellCastProcess().progress(player.getWorld().getTime()).ratio() * SpellHelper.getCastDuration(player1, entity.getCurrentSpell()));
                        }
                        if (spell != null) {
                            SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1.0F, 1.0F, (Vec3d) null, SpellPower.getSpellPower(spell.school, player1), impactTargetingMode(spell));

                            if (SpellHelper.ammoForSpell(player1, SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "frostbloomproc")), stack).satisfied()) {
                                for (Entity target1 : targets) {
                                    SpellHelper.performImpacts(player1.getWorld(), player1, target1, player1, new SpellInfo(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "frostbloomproc")),new Identifier(Cleannarsenal.MODID, "frostbloomproc")), new SpellHelper.ImpactContext());
                                }
                                entity.getCooldownManager().set(new Identifier(Cleannarsenal.MODID, "frostbloomproc"), (int) (20 * SpellHelper.getCooldownDuration(player1, spell)));
                                ParticleHelper.sendBatches(living, spell.release.particles);

                                if (SpellHelper.ammoForSpell(player1, SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "frostbloomproc")), stack).ammo() != null && spell.cost.consume_item) {
                                    for(int ii = 0; ii < player.getInventory().size(); ++ii) {
                                        ItemStack stack2 = player.getInventory().getStack(ii);
                                        if (stack2.isOf(SpellHelper.ammoForSpell(player1, SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "frostbloomproc")), stack2).ammo().getItem())) {
                                            stack2.decrement(1);
                                            if (stack2.isEmpty()) {
                                                player.getInventory().removeOne(stack2);
                                            }
                                            break;
                                        }
                                    }
                                }                            }
                        }

                }

            }
            if(entityHitResult.getEntity() != null){
                SpellHelper.performImpacts(player1.getWorld(), player1, entityHitResult.getEntity(), player1, new SpellInfo(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "barrage")),new Identifier(Cleannarsenal.MODID, "barrage")), new SpellHelper.ImpactContext());

            }


            this.discard();
        }


    }

    public void readCustomDataFromNbt(NbtCompound compoundTag) {
        if (compoundTag.containsUuid("Target")) {
            this.targetUUID = compoundTag.getUuid("Target");
        }
        super.readCustomDataFromNbt(compoundTag);
    }
    public LaserArrow(EntityType<? extends PersistentProjectileEntity> entityType, World level) {
        super(entityType, level);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public ItemStack getStack() {
        return new ItemStack(Items.LASERARROWITEM.item);
    }

    @Override
    public void tick() {
        this.getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK,this.getX(),this.getY(),this.getZ(),-this.getVelocity().getX(),-this.getVelocity().getY(),-this.getVelocity().z);
    if(!this.getWorld().isClient()  && this.age > 80){
        this.discard();
    }
    if(getTarget() != null) {
        Vec3d distanceVector = getTarget().getPos().add(0.0D, (double) (getTarget().getHeight() / 2.0F), 0.0D).subtract(this.getPos().add(0.0D, (double) (this.getHeight() / 2.0F), 0.0D));
        Vec3d newVelocity = VectorHelper.rotateTowards(this.getVelocity(), distanceVector, (double) (this.age));

        if (newVelocity.lengthSquared() > 0.0D) {
            this.setVelocity(newVelocity);
            this.velocityDirty = true;
        }
    }
        //ProjectileUtil.rotateTowardsMovement(this,0.5F);

        super.tick();

    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.discard();
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }
}
