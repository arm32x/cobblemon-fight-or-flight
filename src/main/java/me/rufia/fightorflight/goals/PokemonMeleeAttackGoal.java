package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.config.FightOrFlightCommonConfigs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;


public class PokemonMeleeAttackGoal extends MeleeAttackGoal {
    public int ticksUntilNewAngerParticle = 0;

    public PokemonMeleeAttackGoal(PathAwareEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    public void tick() {
        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;
        LivingEntity owner = pokemonEntity.getOwner();
        if (owner == null){
            if (ticksUntilNewAngerParticle < 1) {
                CobblemonFightOrFlight.PokemonEmoteAngry(this.mob);
                ticksUntilNewAngerParticle = 10;
            }
            else { ticksUntilNewAngerParticle = ticksUntilNewAngerParticle - 1; }
        }

        super.tick();


        if (!CobblemonFightOrFlight.CONFIG.DO_POKEMON_ATTACK_IN_BATTLE()){
            if (isTargetInBattle()){
                this.mob.getNavigation().setSpeed(0);//.setSpeedModifier(0);
            }
        }
    }
    public boolean isTargetInBattle(){
        if (this.mob.getTarget() instanceof ServerPlayerEntity){
            ServerPlayerEntity targetAsPlayer = (ServerPlayerEntity) this.mob.getTarget();
            if (BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(targetAsPlayer) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldFightTarget(){
        //if (CobblemonFightOrFlight.CONFIG.DO_POKEMON_ATTACK() == false) { return false; }

        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;

        if (pokemonEntity.getPokemon().getLevel() < CobblemonFightOrFlight.CONFIG.MINIMUM_ATTACK_LEVEL()) { return false; }

        LivingEntity owner = pokemonEntity.getOwner();
        if (owner != null){
            if (CobblemonFightOrFlight.CONFIG.DO_POKEMON_DEFEND_OWNER() == false) { return false; }
            if (this.mob.getTarget() == null || this.mob.getTarget() == owner) { return false; }

            if (this.mob.getTarget() instanceof PokemonEntity){
                PokemonEntity targetPokemon = (PokemonEntity)this.mob.getTarget();
                LivingEntity targetOwner = targetPokemon.getOwner();
                if (targetOwner != null){
                    if (targetOwner == owner) { return false; }
                    if (CobblemonFightOrFlight.CONFIG.DO_PLAYER_POKEMON_ATTACK_OTHER_PLAYER_POKEMON() == false) {
                        return false;
                    }
                }
            }
            if (this.mob.getTarget() instanceof PlayerEntity){
                if (CobblemonFightOrFlight.CONFIG.DO_PLAYER_POKEMON_ATTACK_OTHER_PLAYERS() == false){
                    return false;
                }
            }

        } else {
            if (this.mob.getTarget() != null){
                if (CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) <= 0) { return false; }
            }
        }
        //if (pokemonEntity.getPokemon().isPlayerOwned()) { return false; }

        return !pokemonEntity.isBusy();
    }

    public boolean canStart() {
        return shouldFightTarget() && super.canStart();
    }

    public boolean shouldContinue() {
        return shouldFightTarget() && super.shouldContinue();
    }

    protected void attack(LivingEntity target, double distanceToSqr) {
        double d0 = this.getSquaredMaxAttackDistance(target);
        if (distanceToSqr <= d0 && this.getCooldown()/*.getTicksUntilNextAttack()*/ <= 0) {
            this.resetCooldown();//.resetAttackCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);//.swing(InteractionHand.MAIN_HAND);
            pokemonDoHurtTarget(target);
        }
    }

    public boolean pokemonDoHurtTarget(Entity hurtTarget) {
        if (!CobblemonFightOrFlight.CONFIG.DO_POKEMON_ATTACK_IN_BATTLE()) {
            if (isTargetInBattle()) { return false; }
        }
        PokemonEntity pokemonEntity = (PokemonEntity)this.mob;
        Pokemon pokemon = pokemonEntity.getPokemon();

        if (!pokemonTryForceEncounter(pokemonEntity, hurtTarget)){

            int pkmLevel = pokemon.getLevel();
            float maxAttack = Math.max(pokemonEntity.getPokemon().getAttack(), pokemonEntity.getPokemon().getSpecialAttack());

            ElementalType primaryType = pokemon.getPrimaryType();

            //LogUtils.getLogger().info("target took " + primaryType.getName() + " damage");


            float hurtDamage = maxAttack / 10f;
            float hurtKnockback = 1.0f;

            if (hurtTarget instanceof LivingEntity) {
                LivingEntity livingHurtTarget = (LivingEntity)hurtTarget;
                int effectStrength = Math.max(pkmLevel / 10, 1);

                switch (primaryType.getName()) {
                    case "fire":
                        livingHurtTarget.setOnFireFor(effectStrength);//.setSecondsOnFire(effectStrength);
                        break;
                    case "ice":
                        livingHurtTarget.setFrozenTicks(/*setTicksFrozen*/(livingHurtTarget.getFrozenTicks()/*getTicksFrozen()*/ + effectStrength * 30));
                        break;
                    case "poison":
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, effectStrength * 20, 0), this.mob);
                        break;
                    case "psychic":
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, effectStrength * 20, 0), this.mob);
                        break;
                    case "fairy":
                    case "fighting":
                    case "steel":
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, effectStrength * 20, 0), this.mob);
                        break;
                    case "ghost":
                    case "dark":
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, (effectStrength + 2) * 25, 0), this.mob);
                        break;
                    case "ground":
                    case "rock":
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, (effectStrength + 2) * 25, 0), this.mob);
                        break;
                    case "electric":
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, (effectStrength + 2) * 25, 0), this.mob);
                        break;
                    case "bug":
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, (effectStrength + 2) * 25, 0), this.mob);
                        break;
                    case "grass":
                        this.mob.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, (effectStrength + 2) * 20, 0), this.mob);
                    case "dragon":
                        hurtDamage = hurtDamage + 3;
                    case "flying":
                        hurtKnockback = hurtKnockback * 2;
                    case "water":
                        hurtKnockback = hurtKnockback * 2;
                        livingHurtTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, (effectStrength + 2) * 25, 0), this.mob);

                    default:
                        break;
                }
            }

            DamageSource damageSource = this.mob.getDamageSources().mobAttack(this.mob);
            boolean flag = hurtTarget.damage(damageSource, hurtDamage);
            if (flag) {
                if (hurtKnockback > 0.0F && hurtTarget instanceof LivingEntity) {
//                    ((LivingEntity)hurtTarget).takeKnockback((double)(hurtKnockback * 0.5F), (double) Math.sin(this.mob.getYaw() * ((float)Math.PI / 180F)), (double)(-Math.cos(this.mob.getYaw() * ((float)Math.PI / 180F))));
//                    this.mob.setVelocity(this.mob.getVelocity().multiply(0.6D, 1.0D, 0.6D));//.setDeltaMovement(this.mob.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                    ((LivingEntity)hurtTarget).takeKnockback(hurtKnockback * 0.5f, MathHelper.sin(this.mob.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(this.mob.getYaw() * ((float)Math.PI / 180)));
                    this.mob.setVelocity(this.mob.getVelocity().multiply(0.6, 1.0, 0.6));
                }

                this.mob.onAttacking(hurtTarget);//.setLastHurtMob(hurtTarget);
            }

            return flag;
        }

        return false;
    }

    public boolean pokemonTryForceEncounter(PokemonEntity attackingPokemon, Entity hurtTarget){
        if (hurtTarget instanceof PokemonEntity)
        {
            PokemonEntity defendingPokemon = (PokemonEntity) hurtTarget;
            if (attackingPokemon.getPokemon().isPlayerOwned()){
                if (defendingPokemon.getPokemon().isPlayerOwned()){
                    if (CobblemonFightOrFlight.CONFIG.FORCE_PLAYER_BATTLE_ON_POKEMON_HURT()) {
                        return pokemonForceEncounterPvP(attackingPokemon, defendingPokemon);
                    }
                } else {
                    if (CobblemonFightOrFlight.CONFIG.FORCE_WILD_BATTLE_ON_POKEMON_HURT()) {
                        return pokemonForceEncounterPvE(attackingPokemon, defendingPokemon);
                    }
                }
            } else if (defendingPokemon.getPokemon().isPlayerOwned()) {
                if (CobblemonFightOrFlight.CONFIG.FORCE_WILD_BATTLE_ON_POKEMON_HURT()) {
                    return pokemonForceEncounterPvE(defendingPokemon, attackingPokemon);
                }
            }
        }
        return false;
    }

    public boolean pokemonForceEncounterPvP(PokemonEntity playerPokemon, PokemonEntity opponentPokemon){
        if (playerPokemon.getOwner() instanceof ServerPlayerEntity
        && opponentPokemon.getOwner() instanceof ServerPlayerEntity){
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)playerPokemon.getOwner();
            ServerPlayerEntity serverOpponent = (ServerPlayerEntity)opponentPokemon.getOwner();

            if (serverPlayer == serverOpponent // I don't see why this should ever happen, but probably best to account for it
                    || !canBattlePlayer(serverPlayer)
                    || !canBattlePlayer(serverOpponent)) {
                return false;
            }

            BattleBuilder.INSTANCE.pvp1v1(serverPlayer,
                    serverOpponent,
                    null,
                    null,
                    BattleFormat.Companion.getGEN_9_SINGLES(),
                    false,
                    false,
                    (ServerPlayer) -> Cobblemon.INSTANCE.getStorage().getParty(ServerPlayer));
        }
        return false;
    }
    public boolean pokemonForceEncounterPvE(PokemonEntity playerPokemon, PokemonEntity wildPokemon){
        if (playerPokemon.getOwner() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)playerPokemon.getOwner();

            if (!canBattlePlayer(serverPlayer)) {
                return false;
            }

            BattleBuilder.INSTANCE.pve(serverPlayer,
                    wildPokemon,
                    playerPokemon.getPokemon().getUuid(),
                    BattleFormat.Companion.getGEN_9_SINGLES(),
                    false,
                    false,
                    Cobblemon.config.getDefaultFleeDistance(),
                    Cobblemon.INSTANCE.getStorage().getParty(serverPlayer));
        }
        return false;
    }

    public boolean canBattlePlayer(ServerPlayerEntity serverPlayer){
        boolean playerHasAlivePokemon = false;
        for (Pokemon pokemon : Cobblemon.INSTANCE.getStorage().getParty(serverPlayer)) {
            if (!pokemon.isFainted()) {
                playerHasAlivePokemon = true;
                break;
            }
        }

        if (BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(serverPlayer) != null
            || !playerHasAlivePokemon
            || !serverPlayer.isAlive()) {
            return false;
        }

        return true;
    }
}
