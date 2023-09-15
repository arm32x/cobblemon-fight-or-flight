package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import java.util.EnumSet;
import java.util.function.Predicate;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.Nullable;

public class PokemonAvoidGoal extends Goal {
    protected final PathAwareEntity mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    @Nullable
    protected LivingEntity toAvoid;
    protected final float maxDist;
    @Nullable
    protected Path path;
    protected final EntityNavigation pathNav;
    //protected final Class<LivingEntity> avoidClass;
    //protected final Predicate<LivingEntity> avoidPredicate;
    //protected final Predicate<LivingEntity> predicateOnAvoidEntity;
    private final TargetPredicate avoidEntityTargeting;

//    public PokemonAvoidGoal(PathfinderMob p_25040_, Class<T> p_25041_, Predicate<LivingEntity> p_25042_, float p_25043_, double p_25044_, double p_25045_, Predicate<LivingEntity> p_25046_) {
//        this.mob = p_25040_;
//        this.avoidClass = p_25041_;
//        this.avoidPredicate = p_25042_;
//        this.maxDist = p_25043_;
//        this.walkSpeedModifier = p_25044_;
//        this.sprintSpeedModifier = p_25045_;
//        this.predicateOnAvoidEntity = p_25046_;
//        this.pathNav = p_25040_.getNavigation();
//        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
//        this.avoidEntityTargeting = TargetingConditions.forCombat().range((double)p_25043_).selector(p_25046_.and(p_25042_));
//    }
    public PokemonAvoidGoal(PathAwareEntity mob, float maxDist, float walkSpeedModifier, float sprintSpeedModifier){
        this.mob = mob;
        this.maxDist = maxDist;
        this.walkSpeedModifier = walkSpeedModifier;
        this.sprintSpeedModifier = sprintSpeedModifier;
        this.pathNav = this.mob.getNavigation();//.getNavigation();
        this.avoidEntityTargeting = TargetPredicate.createAttackable().setBaseMaxDistance((double)maxDist);
    }


    public boolean canStart() {
        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;
        if (pokemonEntity.getPokemon().isPlayerOwned()) { return false; }
        if (pokemonEntity.isBusy()) { return false; }

        if (this.mob.getTarget() != null) {
            if (CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) > 0) { return false; }

            if (this.mob.getTarget().squaredDistanceTo(this.mob) < maxDist) {
                toAvoid = this.mob.getTarget();
            }

            //this.toAvoid = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(toAvoid.getClass(), this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0D, (double)this.maxDist))
            //        , this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        }
        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3d vec3 = NoPenaltyTargeting.findFrom(this.mob, 16, 7, this.toAvoid.getPos());
            if (vec3 == null) {
                return false;
            } else if (this.toAvoid.squaredDistanceTo(vec3.x, vec3.y, vec3.z) < this.toAvoid.squaredDistanceTo(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.findPathTo(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }


//        this.toAvoid = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.avoidClass, this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0D, (double)this.maxDist), (p_148078_) -> {
//            return true;
//        }), this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
//        if (this.toAvoid == null) {
//            return false;
//        } else {
//            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
//            if (vec3 == null) {
//                return false;
//            } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.mob)) {
//                return false;
//            } else {
//                this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
//                return this.path != null;
//            }
//        }
    }

    public boolean shouldContinue() {
        return !this.pathNav.isIdle();
    }

    public void start() {
        this.pathNav.startMovingAlong(this.path, this.walkSpeedModifier);
    }

    public void stop() {
        this.toAvoid = null;
    }

    public void tick() {
        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;
        LogUtils.getLogger().info(pokemonEntity.getPokemon().getSpecies().getName() + " is running away " + this.mob.squaredDistanceTo(this.toAvoid) + " distanceSqr from here");

        if (this.mob.squaredDistanceTo(this.toAvoid) < (maxDist * 0.5)) {
            this.mob.getNavigation().setSpeed(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeed(this.walkSpeedModifier);
        }

    }
}