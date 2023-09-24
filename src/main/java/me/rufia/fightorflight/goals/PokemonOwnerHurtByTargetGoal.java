package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.config.FightOrFlightCommonConfigs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

import java.util.EnumSet;

public class PokemonOwnerHurtByTargetGoal extends TrackTargetGoal {
    private final PokemonEntity pokemonEntity;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public PokemonOwnerHurtByTargetGoal(PokemonEntity pokemonEntity) {
        super(pokemonEntity, false);
        this.pokemonEntity = pokemonEntity;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    public boolean canStart() {
        if (CobblemonFightOrFlight.CONFIG.DO_POKEMON_DEFEND_OWNER() == false) { return false; }

        LivingEntity owner = this.pokemonEntity.getOwner();
//        if (owner != null) {
//            LogUtils.getLogger().info("playerOwnerHurtByTargetGoal");
//        }

        if (owner != null && !this.pokemonEntity.isBusy()) {
            this.ownerLastHurtBy = owner.getAttacker();//.getLastHurtByMob();
            int i = owner.getLastAttackedTime();//.getLastHurtByMobTimestamp();
            return i != this.timestamp &&
                    this.canTrack(this.ownerLastHurtBy, TargetPredicate.DEFAULT) && this.pokemonEntity.canAttackWithOwner(this.ownerLastHurtBy, owner);
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity owner = this.pokemonEntity.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastAttackedTime();//.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
