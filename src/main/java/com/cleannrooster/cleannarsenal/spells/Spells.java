package com.cleannrooster.cleannarsenal.spells;

import com.cleannrooster.cleannarsenal.Cleannarsenal;
import com.cleannrooster.cleannarsenal.PlayerInterface;
import com.cleannrooster.cleannarsenal.entities.LaserArrow;
import com.cleannrooster.cleannarsenal.entities.SpinAttack;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.Function;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.spell_engine.api.item.AttributeResolver;
import net.spell_engine.api.spell.*;
import net.spell_engine.internals.SpellContainerHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.WorldScheduler;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.network.Packets;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.AnimationHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.cleannrooster.cleannarsenal.Cleannarsenal.LASERARROW;
import static com.cleannrooster.cleannarsenal.Cleannarsenal.MODID;
import static net.spell_engine.internals.SpellHelper.shootProjectile;
import static net.spell_engine.particle.ParticleHelper.play;

public class Spells {
    public static final ArrayList<Spell> entries = new ArrayList<>();
    private static Spell entry(String namespace, String name, Function<CustomSpellHandler.Data, Boolean> handler ) {
        var entry = new Spell(namespace, name, handler);
        entries.add(entry);

        return entry;
    }
    public static class Spell {
        private final String namespace;
        private final String name;

        private final Function<CustomSpellHandler.Data, Boolean> handler;
        public Spell(String namespace, String name, Function<CustomSpellHandler.Data, Boolean> handler) {
            this.namespace = namespace;
            this.name = name;
            this.handler = handler;
        }
    };
    public static Spell flourish(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell FLOURISH = flourish(Cleannarsenal.MODID,"flourish", (data) -> {

        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        net.spell_engine.api.spell.Spell spell = SpellRegistry.getSpell(new Identifier(MODID,"flourish"));
        List<Entity> entities = data1.targets();
        for(Entity entity : entities){
            SpellHelper.performImpacts(data1.caster().getWorld(), data1.caster(),entity, data1.caster(), new SpellInfo(spell,new Identifier(MODID,"flourish")),data1.impactContext());
        }

        if(data1.caster().getWorld() instanceof ServerWorld world) {
            if (data1.caster() instanceof PlayerInterface playerInterface && data1.caster().hasStatusEffect(Cleannarsenal.SHADEWALK)) {

                for (Vec3d position : playerInterface.getPositions()) {
                    List<LivingEntity> list ;
                    list = data1.caster().getWorld().getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), Box.of(position,7,3.5,7),(target) -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, data1.caster(), target));

                    for (Entity entity : list) {
                        BlockHitResult result = data1.caster().getWorld().raycast(new RaycastContext(position,entity.getBoundingBox().getCenter(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY,entity));
                        if(result != null && result.getPos() != null && !result.getType().equals(HitResult.Type.BLOCK)) {
                            SpellHelper.performImpacts(data1.caster().getWorld(), data1.caster(), entity, data1.caster(), new SpellInfo(spell, new Identifier(MODID, "flourish")), data1.impactContext());
                        }
                    }
                }
            }
            List<? extends SpinAttack> spins = world.getEntitiesByType(TypeFilter.instanceOf(SpinAttack.class), spin -> {
                if( spin.getOwner() != null && spin.getOwner().isAlive()){
                    return spin.getOwner().equals(data1.caster());
                }
                else{
                    return false;
                }
            });
            if(!spins.isEmpty()) {
                for (SpinAttack spin : spins) {
                    spin.triggerAnim("baseAnim", "spin");
                }
            }
        }


        return true;
    });
    public static Spell magicmissile(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell MAGICMISSILE = magicmissile(Cleannarsenal.MODID,"magicmissile", (data) -> {

        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        List<Entity> entities = data1.targets();
        for(Entity entity : entities ){
            if(data1.caster() instanceof PlayerInterface playerInterface && entity.isAlive()){
                playerInterface.EntityAdd(entity);
                SoundHelper.playSoundEvent(data1.caster().getWorld(),data1.caster(),  SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,1F,1F);

            }
        }

        return false;
    });
    public static Spell earthquake(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell EARTHQUAKE = earthquake(Cleannarsenal.MODID,"earthquake", (data) -> {
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        double playerx = data1.caster().getX();
        double playery = data1.caster().getBoundingBox().getCenter().getY();
        double playerz = data1.caster().getZ();
        data1.caster().getWorld().createExplosion(data1.caster(),data1.caster().getX(),data1.caster().getY(),data1.caster().getZ(),(float)(SpellPower.getSpellPower(ExternalSpellSchools.PHYSICAL_MELEE,data1.caster()).nonCriticalValue()*0.1), World.ExplosionSourceType.NONE);
        for(int i = 1; i < 3; i++){
            int finalI = i;
            ((WorldScheduler)data1.caster().getWorld()).schedule(20*i,()-> {
                if(data1.caster().getWorld() instanceof ServerWorld serverWorld) {
                    for (ParticleBatch batck : SpellRegistry.getSpell(new Identifier(MODID, "earthquake")).release.particles) {

                        PacketByteBuf packet = (new Packets.ParticleBatches(Packets.ParticleBatches.SourceType.COORDINATE,
                                List.of(new Packets.ParticleBatches.Spawn(data1.caster().getId(), data1.caster().getYaw(), data1.caster().getPitch(), new Vec3d(playerx, playery, playerz), batck)))

                                .write(2));
                        if (data1.caster() instanceof ServerPlayerEntity) {
                            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) data1.caster();
                            try {
                                if (ServerPlayNetworking.canSend(serverPlayer, Packets.ParticleBatches.ID)) {
                                    ServerPlayNetworking.send(serverPlayer, Packets.ParticleBatches.ID, packet);
                                }
                            } catch (Exception var3) {
                                var3.printStackTrace();
                            }
                            Supplier<Collection<ServerPlayerEntity>> trackers = Suppliers.memoize(() -> {
                                return PlayerLookup.tracking(data1.caster());
                            });
                            trackers.get().forEach((serverPlayerx) -> {
                                try {
                                    if (ServerPlayNetworking.canSend(serverPlayer, Packets.ParticleBatches.ID)) {
                                        ServerPlayNetworking.send(serverPlayer, Packets.ParticleBatches.ID, packet);
                                    }
                                } catch (Exception var3) {
                                    var3.printStackTrace();
                                }
                            });
                        }

                    }
                }
                data1.caster().getWorld().createExplosion(data1.caster(),playerx,playery,playerz,(float)((1+finalI) *SpellPower.getSpellPower(ExternalSpellSchools.PHYSICAL_MELEE,data1.caster()).nonCriticalValue()*0.1), World.ExplosionSourceType.NONE);
        });
        }
            return true;

    });
        public static Spell lightsOut(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell LIGHTSOUT = lightsOut(Cleannarsenal.MODID,"lightsout", (data) -> {

        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        List<Entity> entities = data1.targets();
        for(Entity entity : entities ){
            if(data1.caster() instanceof PlayerInterface playerInterface && !playerInterface.getTargetEntities().contains(entity) && playerInterface.getTargetEntities().size() < 6){
                playerInterface.EntityAdd(entity);
            }
        }
        if(data1.progress()>= 1.0 && data1.caster() instanceof SpellCasterEntity casterEntity && data1.caster() instanceof PlayerInterface playerInterface && !playerInterface.getTargetEntities().isEmpty()){
           shootFmjAtAll(data1,casterEntity, playerInterface.getTargetEntities());
        }
        return false;
    });
    public static void shootMagicMissiles(PlayerEntity player, SpellCasterEntity casterEntity,List<Entity> list){


        if(!list.isEmpty() && list.get(0).isAlive()) {

            shootProjectile(player.getWorld(), player, list.get(0), new SpellInfo(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "magicmissileactual")), new Identifier(Cleannarsenal.MODID, "magicmissileactual")), new SpellHelper.ImpactContext());

            Supplier<Collection<ServerPlayerEntity>> trackingPlayers = Suppliers.memoize(() -> {
                return PlayerLookup.tracking(player);
            });
            AnimationHelper.sendAnimation(player, (Collection) trackingPlayers.get(), SpellCast.Animation.RELEASE, SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "magicmissileactual")).release.animation, 1.0F);
            list.remove(0);
        }
        int i = 0;
        for(Entity entity: list){
            ((WorldScheduler)player.getWorld()).schedule(2+2*i,()-> {


                shootProjectile(player.getWorld(), player, entity, new SpellInfo(SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "magicmissileactual")), new Identifier(Cleannarsenal.MODID, "magicmissileactual")), new SpellHelper.ImpactContext());
                ParticleHelper.sendBatches(player, SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "magicmissileactual")).release.particles);
                SoundHelper.playSound(player.getWorld(), player, SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "magicmissileactual")).release.sound);
                Supplier<Collection<ServerPlayerEntity>> trackingPlayers = Suppliers.memoize(() -> {
                    return PlayerLookup.tracking(player);
                });

                AnimationHelper.sendAnimation(player, (Collection) trackingPlayers.get(), SpellCast.Animation.RELEASE, SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID, "magicmissileactual")).release.animation, 1.0F);
            });
            i++;
        }
        if (casterEntity instanceof PlayerInterface playerInterface) {
            playerInterface.clearEntities();
        }
    }

    public static void shootFmjAtAll(CustomSpellHandler.Data data1, SpellCasterEntity casterEntity,List<Entity> list){
        net.spell_engine.api.spell.Spell spell = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID,"fmj"));
        net.spell_engine.api.spell.Spell spell2 = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID,"fmjinstant"));

        Entity living = TargetHelper.targetFromRaycast(data1.caster(),spell2.range, target ->TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL,data1.caster(),target));
        ArrayList<Entity> listNew = new ArrayList<>(list);
        if(living != null){
            listNew.add(living);

        }
        if(!listNew.isEmpty() &&listNew.get(0).isAlive()) {
            float modifier = 1;
            data1.caster().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,listNew.get(0).getBoundingBox().getCenter());
            data1.caster().velocityDirty = true;

            modifier -= Math.abs(casterEntity.getSpellCastProcess().progress(data1.caster().getWorld().getTime()).ratio() - 0.75F);
            modifier = (float) Math.min(1.25, modifier);
            if (casterEntity instanceof PlayerInterface playerInterface) {
                playerInterface.setFMJModifier(modifier);
            }
            PlayerEntity player = data1.caster();
            Entity living2 = TargetHelper.targetFromRaycast(player, spell.range, target -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, player, target));
            List<Entity> list2 = new ArrayList<Entity>();
            if(living2 != null) {
                list2.add(living2);
            }
            SpellHelper.performSpell(player.getWorld(),player,new Identifier(Cleannarsenal.MODID, "fmjinstant"),list2, SpellCast.Action.CHANNEL,1.0F);
            ParticleHelper.sendBatches(player, spell.release.particles);
            SoundHelper.playSound(player.getWorld(),player,spell.release.sound);
            if(player.getWorld() instanceof ServerWorld serverWorld) {
                AnimationHelper.sendAnimation(player, PlayerLookup.tracking(player), SpellCast.Animation.RELEASE, spell.release.animation, 1.0F);
                AnimationHelper.sendAnimation(player, List.of((ServerPlayerEntity) player), SpellCast.Animation.RELEASE, spell.release.animation, 1.0F);


            }
            listNew.remove(0);
        }
        int i = 0;
        for(Entity entity: listNew){
            ((WorldScheduler)data1.caster().getWorld()).schedule(2+2*i,()-> {
                        float modifier = 1;
                        data1.caster().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getBoundingBox().getCenter());
                        data1.caster().velocityDirty = true;
                        if(casterEntity.getSpellCastProcess() != null) {
                            modifier -= Math.abs(casterEntity.getSpellCastProcess().progress(data1.caster().getWorld().getTime()).ratio() - 0.75F);
                        }
                        modifier = (float) Math.min(1.25, modifier);
                        if (casterEntity instanceof PlayerInterface playerInterface) {
                            playerInterface.setFMJModifier(modifier);
                        }
                PlayerEntity player = data1.caster();

                Entity living2 = TargetHelper.targetFromRaycast(player, spell.range, target -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, player, target));
                    List<Entity> list2 = new ArrayList<Entity>();
                    if(living2 != null) {
                        list.add(living2);
                    }
                    SpellHelper.performSpell(player.getWorld(),player,new Identifier(Cleannarsenal.MODID, "fmjinstant"),list2, SpellCast.Action.CHANNEL,1.0F);
                    ParticleHelper.sendBatches(player, spell.release.particles);
                    SoundHelper.playSound(player.getWorld(),player,spell.release.sound);
                    if(player.getWorld() instanceof ServerWorld serverWorld) {
                        AnimationHelper.sendAnimation(player,PlayerLookup.tracking(player), SpellCast.Animation.RELEASE,spell.release.animation,1.0F);
                        AnimationHelper.sendAnimation(player,List.of((ServerPlayerEntity) player), SpellCast.Animation.RELEASE,spell.release.animation,1.0F);

                    }
            });
            i++;
        }
        if (casterEntity instanceof PlayerInterface playerInterface) {
            playerInterface.clearEntities();
        }
    }
    public static Spell barrage(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell BARRAGE = barrage(Cleannarsenal.MODID,"barrage", (data) -> {

        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        double i = 30;
        PlayerEntity player = data1.caster();

        Predicate<Entity> selectionPredicate = (target) -> {
            return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
            );
        };
        if (!data1.targets().isEmpty() && data1.targets().get(0) instanceof LivingEntity living) {
            Entity target = data1.targets().get(0);
            LivingEntity add1 = null;
            LivingEntity add2 = null;
            List<LivingEntity> list = living.getWorld().getTargets(LivingEntity.class, TargetPredicate.createAttackable(), living, target.getBoundingBox().expand(6));
            list.removeIf((entity) -> !selectionPredicate.test(entity));
            add1 = living.getWorld().getClosestEntity(list, TargetPredicate.DEFAULT, living, living.getX(), living.getY(), living.getZ());
            list.remove(add1);
            if (add1 != null)
                add2 = add1.getWorld().getClosestEntity(list, TargetPredicate.DEFAULT, add1, add1.getX(), add1.getY(), add1.getZ());
            if (player instanceof SpellCasterEntity caster && SpellContainerHelper.getEquipped(player.getMainHandStack(), player) != null && SpellContainerHelper.getEquipped(player.getMainHandStack(), player).spell_ids.contains("cleannarsenal:multishot")) {
                if (add1 != null)
                    data1.targets().add(add1);
                if (add2 != null)
                    data1.targets().add(add2);
            }
            for (Entity living2 : data1.targets()) {
                if (living2 instanceof LivingEntity livingEntity) {
                    LaserArrow laserArrow = new LaserArrow(LASERARROW, data1.caster().getWorld());
                    if (player instanceof SpellCasterEntity caster && SpellContainerHelper.getEquipped(player.getMainHandStack(), player) != null && SpellContainerHelper.getEquipped(player.getMainHandStack(), player).spell_ids.contains("cleannarsenal:chain")) {

                        laserArrow.chaining = 2;
                    }
                    if (player instanceof SpellCasterEntity caster && SpellContainerHelper.getEquipped(player.getMainHandStack(), player) != null && SpellContainerHelper.getEquipped(player.getMainHandStack(), player).spell_ids.contains("cleannarsenal:frostbloom")) {
                        laserArrow.burst = true;

                    }
                    laserArrow.setOwner(player);
                    laserArrow.setTarget(living2);
                    i = living2.distanceTo(data1.caster());


                    i *= 0.5;
                    if (i > 30) {
                        i = 30;
                    }
                    laserArrow.setPosition(data1.caster().getEyePos().subtract(0, 0.10000000149011612D * (data1.caster().getHeight() / 1.8), 0));
                    int k = EnchantmentHelper.getLevel(Enchantments.POWER, data1.caster().getActiveItem());

                    int l = EnchantmentHelper.getLevel(Enchantments.PUNCH, data1.caster().getActiveItem());
                    if (l > 0) {
                    }

                    if (EnchantmentHelper.getLevel(Enchantments.FLAME, data1.caster().getActiveItem()) > 0) {
                        laserArrow.setOnFireFor(100);
                    }


                    data1.caster().getWorld().playSound((PlayerEntity) null, data1.caster().getX(), data1.caster().getY(), data1.caster().getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (data1.caster().getRandom().nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
                    laserArrow.setVelocity(data1.caster(), (float) (data1.caster().getPitch() - data1.caster().getRandom().nextDouble() * i), (float) (data1.caster().getYaw() + (-i + data1.caster().getRandom().nextDouble() * i * 2)), 0.0F, 1 * 2F, 1.0F);
                    data1.caster().getWorld().spawnEntity(laserArrow);
                }
            }
        } else {
            LaserArrow laserArrow = new LaserArrow(LASERARROW, data1.caster().getWorld());
            laserArrow.chaining = 2;
            laserArrow.setOwner(player);
            i = 60;


            i *= 0.5;
            if (i > 30) {
                i = 30;
            }
            laserArrow.setPosition(data1.caster().getEyePos().subtract(0, 0.10000000149011612D * (data1.caster().getHeight() / 1.8), 0));

            int l = EnchantmentHelper.getLevel(Enchantments.PUNCH, data1.caster().getActiveItem());


            if (EnchantmentHelper.getLevel(Enchantments.FLAME, data1.caster().getActiveItem()) > 0) {
                laserArrow.setOnFireFor(100);
            }


            data1.caster().getWorld().playSound((PlayerEntity) null, data1.caster().getX(), data1.caster().getY(), data1.caster().getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (data1.caster().getRandom().nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
            laserArrow.setVelocity(data1.caster(), (float) (data1.caster().getPitch() - data1.caster().getRandom().nextDouble() * i), (float) (data1.caster().getYaw() + (-i + data1.caster().getRandom().nextDouble() * i * 2)), 0.0F, 1 * 2F, 1.0F);
            data1.caster().getWorld().spawnEntity(laserArrow);
        }
        return false;
    }
    );

    public static Spell rumble(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell RUMBLE = barrage(Cleannarsenal.MODID,"rumble", (data) -> {
        net.spell_engine.api.spell.Spell spell = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID,"rumble"));
        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        if(!data1.targets().isEmpty()){
           for(Entity entity : data1.targets()){
            SpellHelper.performImpacts(entity.getWorld(),data1.caster(),entity, data1.caster(), new SpellInfo(spell,new Identifier(Cleannarsenal.MODID,"rumble")),data1.impactContext());
           }
        }
        ParticleHelper.sendBatches(data1.caster(), spell.release.particles,true);
        SoundHelper.playSound(data1.caster().getWorld() ,data1.caster(),spell.impact[0].sound);
        return false;
    });
    public static Spell rampage(String namespace, String name, Function<CustomSpellHandler.Data,Boolean> handler){
        return entry(namespace,name,handler);
    }
    public static final Spell RAMPAGE = rampage(Cleannarsenal.MODID,"rampage", (data) -> {
        net.spell_engine.api.spell.Spell spell = SpellRegistry.getSpell(new Identifier(Cleannarsenal.MODID,"rampage"));

        CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
        List<Entity> targets1 = TargetHelper.targetsFromArea(data1.caster(),data1.caster().getPos(),spell.range,spell.release.target.area,entity -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL,data1.caster(),entity));

        if (!targets1.isEmpty()) {
            for (Entity entity : targets1) {
                SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, data1.caster(), new SpellInfo(spell, new Identifier(Cleannarsenal.MODID, "rampage")), data1.impactContext());
            }
        }
        for(int i = 1; i < 10; i++) {
            int finalI = i;
            ((WorldScheduler) data1.caster().getWorld()).schedule(20 * i, () -> {
                List<Entity> targets = TargetHelper.targetsFromArea(data1.caster(),data1.caster().getPos(),spell.range,spell.release.target.area,entity -> TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL,data1.caster(),entity));

                if(data1.caster()instanceof ServerPlayerEntity playerEntity) {
                    ParticleHelper.sendBatches(playerEntity, spell.release.particles,true);

                }
                SoundHelper.playSound(data1.caster().getWorld() ,data1.caster(),spell.impact[0].sound);

                if (!targets.isEmpty()) {
                            for (Entity entity : targets) {
                                SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, data1.caster(), new SpellInfo(spell, new Identifier(Cleannarsenal.MODID, "rampage")), data1.impactContext());
                            }
                        }
                    }
            );
        }
        if(data1.caster()instanceof ServerPlayerEntity playerEntity) {
            ParticleHelper.sendBatches(playerEntity, spell.release.particles,true);

        }
        SoundHelper.playSound(data1.caster().getWorld() ,data1.caster(),spell.impact[0].sound);
        return true;
    });
    public static void initializeSpells(){

        for(Spell entry : entries){
            CustomSpellHandler.register(new Identifier(entry.namespace,entry.name),entry.handler);
        }
    }
}
