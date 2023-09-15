package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.config.FightOrFlightCommonConfigs;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

import java.util.function.Predicate;


public class PokemonNearestAttackableTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
    public int ticksUntilNewAngerParticle = 0;
    public boolean generateAngerParticles = false;
    public float safeDistanceSqr = 36;
    public PokemonNearestAttackableTargetGoal(MobEntity mob, Class<T> targetType, float safeDistanceSqr, boolean mustSee, boolean mustReach) {
        super(mob, targetType, mustSee, mustReach);
        this.safeDistanceSqr = safeDistanceSqr;
    }

    public boolean canStart() {
        //if (this.mob.getTarget() != null) { return false; }
        if (CobblemonFightOrFlight.CONFIG.DO_POKEMON_ATTACK_UNPROVOKED() == false) { return false; }

        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;


        if (pokemonEntity.getPokemon().getLevel() < CobblemonFightOrFlight.CONFIG.MINIMUM_ATTACK_UNPROVOKED_LEVEL()) { return false; }

        if (pokemonEntity.getPokemon().isPlayerOwned()) { return false; }
        if (CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) <= CobblemonFightOrFlight.AUTO_AGGRO_THRESHOLD)
        {
            generateAngerParticles = false;
            return false;
        }
        else{
            generateAngerParticles = true;
//          if (generateAngerParticles){
            if (ticksUntilNewAngerParticle < 1) {
                CobblemonFightOrFlight.PokemonEmoteAngry(this.mob);
                ticksUntilNewAngerParticle = 25;
            }
            else { ticksUntilNewAngerParticle = ticksUntilNewAngerParticle - 1; }
//          }
        }

        return super.canStart();
    }
//    public void tick() {
////        if (generateAngerParticles){
//        if (ticksUntilNewAngerParticle < 1) {
//            PokemonEmoteAngry(this.mob);
//            ticksUntilNewAngerParticle = 30;
//        }
//        else { ticksUntilNewAngerParticle = ticksUntilNewAngerParticle - 1; }
////        }
//        super.tick();
//    }
    protected void findClosestTarget() {
        super.findClosestTarget();
        if (this.target != null && this.target.squaredDistanceTo(this.mob) > safeDistanceSqr) {
            this.target = null;
        }
    }
}
