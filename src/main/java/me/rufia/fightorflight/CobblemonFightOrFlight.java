package me.rufia.fightorflight;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.config.FightOrFlightCommonConfigs;
import me.rufia.fightorflight.mixin.MobEntityAccessor;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.ai.goal.GoalSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonFightOrFlight implements ModInitializer {
	public static final String MODID = "fightorflight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final float AUTO_AGGRO_THRESHOLD = 50.0f;
	public static final FightOrFlightCommonConfigs CONFIG = FightOrFlightCommonConfigs.createAndLoad();

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world from Fight or Flight!");

//		ServerEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
//			if (entity instanceof PokemonEntity) {
//				PokemonEntity pokemonEntity = (PokemonEntity)entity;
//				//LOGGER.info("onEntityJoined -> instanceOf PokemonEntity");
//
//				float fleeSpeed = 1.5f;
//				float pursuitSpeed = 1.2f;
//
//				((MobEntityAccessor) (MobEntity)pokemonEntity).goalSelector().add(3, new EscapeDangerGoal(pokemonEntity, fleeSpeed));
//
////				pokemonEntity.goalSelector.addGoal(3, new PokemonAvoidGoal(pokemonEntity, 48.0f, 1.0f, fleeSpeed));
////				pokemonEntity.goalSelector.addGoal(3, new PokemonMeleeAttackGoal(pokemonEntity, pursuitSpeed, true));
////				pokemonEntity.goalSelector.addGoal(4, new PokemonPanicGoal(pokemonEntity, fleeSpeed));
////
////				pokemonEntity.targetSelector.addGoal(1, new PokemonOwnerHurtByTargetGoal(pokemonEntity));
////				pokemonEntity.targetSelector.addGoal(2, new PokemonOwnerHurtTargetGoal(pokemonEntity));
////				pokemonEntity.targetSelector.addGoal(3, new HurtByTargetGoal(pokemonEntity));
////				pokemonEntity.targetSelector.addGoal(4, new CaughtByTargetGoal(pokemonEntity));
////				pokemonEntity.targetSelector.addGoal(5, new PokemonNearestAttackableTargetGoal<>(pokemonEntity, Player.class, 48.0f, true,true));
//
//
//			}
//		}));
	}

	public static double getFightOrFlightCoefficient(PokemonEntity pokemonEntity){
		if (!CobblemonFightOrFlight.CONFIG.DO_POKEMON_ATTACK()) { return -100; }

		Pokemon pokemon = pokemonEntity.getPokemon();
		double pkmnLevel = pokemon.getLevel();
		//double levelAggressionCoefficient = (pokemon.getLevel() - 20);
		double lowStatPenalty = (pkmnLevel * 1.5)+30;
		double levelAggressionCoefficient = (pokemon.getAttack() + pokemon.getSpecialAttack()) - lowStatPenalty;
		double atkDefRatioCoefficient = (pokemon.getAttack() + pokemon.getSpecialAttack()) - (pokemon.getDefence() + pokemon.getSpecialDefence());
		double natureAggressionCoefficient = 0;
		switch (pokemon.getNature().getDisplayName().toLowerCase()){
			case "cobblemon.nature.docile":
			case "cobblemon.nature.timid":
			case "cobblemon.nature.gentle":
			case "cobblemon.nature.careful":
				natureAggressionCoefficient = -2;
				break;
			case "cobblemon.nature.relaxed":
			case "cobblemon.nature.lax":
			case "cobblemon.nature.quiet":
			case "cobblemon.nature.bashful":
			case "cobblemon.nature.calm":
				natureAggressionCoefficient = -1;
				break;
			case "cobblemon.nature.sassy":
			case "cobblemon.nature.hardy":
			case "cobblemon.nature.bold":
			case "cobblemon.nature.impish":
			case "cobblemon.nature.hasty":
				natureAggressionCoefficient = 1;
				break;
			case "cobblemon.nature.brave":
			case "cobblemon.nature.rash":
			case "cobblemon.nature.adamant":
			case "cobblemon.nature.naughty":
				natureAggressionCoefficient = 2;
				break;
			default:
				natureAggressionCoefficient = 0;
				break;
		}

		//Weights and Clamps:
		levelAggressionCoefficient = Math.max(-(pkmnLevel + 5), Math.min(pkmnLevel, 1.5d * levelAggressionCoefficient));//5.0d * levelAggressionCoefficient;
		atkDefRatioCoefficient = Math.max(-pkmnLevel, 1.0d * atkDefRatioCoefficient);
		natureAggressionCoefficient = (pkmnLevel * 0.5) * natureAggressionCoefficient;//25.0d * natureAggressionCoefficient;

		double finalResult = levelAggressionCoefficient + atkDefRatioCoefficient + natureAggressionCoefficient;


//        var pkmnString = "[" + pokemon.getSpecies().getName() + "]";
//        LOGGER.info(pkmnString + " levelAggressionCoefficient: " + levelAggressionCoefficient);
//        LOGGER.info(pkmnString + " atkDefRatioCoefficient: " + atkDefRatioCoefficient);
//        LOGGER.info(pkmnString + " natureAggressionCoefficient: " + natureAggressionCoefficient
//                + " (" + pokemon.getNature().getDisplayName().toLowerCase() + ")");
//
//        LOGGER.info("final FightOrFlightCoefficient: "
//                + levelAggressionCoefficient + "+" + atkDefRatioCoefficient + "+" + natureAggressionCoefficient
//                + " = " + finalResult);
		return finalResult;
	}

	public static void PokemonEmoteAngry(MobEntity mob){
		double particleSpeed = Math.random();
		double particleAngle = Math.random() * 2 * Math.PI;
		double particleXSpeed = Math.cos(particleAngle) * particleSpeed;
		double particleYSpeed = Math.sin(particleAngle) * particleSpeed;

		if (mob.getWorld() instanceof ServerWorld){
			((ServerWorld)mob.getWorld()).spawnParticles(ParticleTypes.ANGRY_VILLAGER,
					mob.getPos().x, mob.getBoundingBox().maxY, mob.getPos().z,
					1, //Amount?
					particleXSpeed,0.5d, particleYSpeed,
					1.0f); //Scale?
		}
		else{
			mob.getWorld().addParticle(ParticleTypes.ANGRY_VILLAGER,
					mob.getPos().x, mob.getBoundingBox().maxY, mob.getPos().z,
					particleXSpeed,0.5d, particleYSpeed);
		}
	}
}