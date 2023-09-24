package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CaughtByTargetGoal extends TrackTargetGoal {
    private static final TargetPredicate HURT_BY_TARGETING = TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
    private static final int ALERT_RANGE_Y = 10;
    private LivingEntity lastCaughtByMob;
    private int lastCaughtByMobTimestamp;

    public CaughtByTargetGoal(MobEntity mob) {
        super(mob, true);
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    public boolean canStart() {
        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;
        List<Object> busyLocks = pokemonEntity.getBusyLocks();
        for (int i = 0; i < busyLocks.size(); i++){
            if (busyLocks.get(i) instanceof EmptyPokeBallEntity){
                LogUtils.getLogger().info("Pokemon in process of being caught");
                EmptyPokeBallEntity pokeBallEntity = (EmptyPokeBallEntity)busyLocks.get(i);

                if (pokeBallEntity.getOwner() instanceof LivingEntity){
                    lastCaughtByMob = (LivingEntity)pokeBallEntity.getOwner();
                    lastCaughtByMobTimestamp = this.mob.age;//.tickCount;
                }
            }
        }

        if (lastCaughtByMob != null) {
            if (lastCaughtByMob.getType() == EntityType.PLAYER && this.mob.getWorld().getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
                return false;
            } else {
                return this.canTrack(lastCaughtByMob, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(lastCaughtByMob);
        this.target = this.mob.getTarget();
        this.mob.setAttacker(this.mob.getTarget());
        if (this.mob.getTarget() instanceof PlayerEntity){
            this.mob.setAttacking/*setLastHurtByPlayer*/((PlayerEntity)this.mob.getTarget());
        }
//        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.maxTimeWithoutVisibility = 300;//.unseenMemoryTicks = 300;
//        if (this.alertSameType) {
//            this.alertOthers();
//        }

        super.start();
    }
}
