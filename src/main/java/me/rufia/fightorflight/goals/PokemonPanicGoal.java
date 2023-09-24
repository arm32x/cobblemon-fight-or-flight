package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class PokemonPanicGoal extends EscapeDangerGoal {
    public PokemonPanicGoal(PathAwareEntity mob, double speedModifier) {
        super(mob, speedModifier);
    }

    private LivingEntity lastCaughtByMob;
    private int lastCaughtByMobTimestamp;
    // Lazy implementation of just tracking this in both CaughtByTargetGoal and here,
    // because I can't be bothered to implement a globalfeature right now.
    // should probably fix


    protected boolean isInDanger() {
        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;
        if (pokemonEntity.isBusy()) { return false; }

        if (this.mob.isOnFire() || this.mob.shouldEscapePowderSnow()){
            return true;
        }
        if (this.mob.getAttacker()/*.getLastHurtByMob()*/ != null) {
            if (CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) > 0) { return false; }
            return true;
        }
        return false;
        //return super.shouldPanic();
    }
}
