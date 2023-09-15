package me.rufia.fightorflight.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

public class EntityLoadHandler implements ServerEntityEvents.Load {
    @Override
    public void onLoad(Entity entity, ServerWorld world) {

//        if (entity instanceof PokemonEntity) {
//            PokemonEntity pokemonEntity = (PokemonEntity)entity;
//            //LOGGER.info("onEntityJoined -> instanceOf PokemonEntity");
//
//            float fleeSpeed = 1.5f;
//            float pursuitSpeed = 1.2f;
//
//            pokemonEntity
//                    .goalSelector.addGoal(3, new PokemonAvoidGoal(pokemonEntity, 48.0f, 1.0f, fleeSpeed));
//            pokemonEntity.goalSelector.addGoal(3, new PokemonMeleeAttackGoal(pokemonEntity, pursuitSpeed, true));
//            pokemonEntity.goalSelector.addGoal(4, new PokemonPanicGoal(pokemonEntity, fleeSpeed));
//
//            pokemonEntity.targetSelector.addGoal(1, new PokemonOwnerHurtByTargetGoal(pokemonEntity));
//            pokemonEntity.targetSelector.addGoal(2, new PokemonOwnerHurtTargetGoal(pokemonEntity));
//            pokemonEntity.targetSelector.addGoal(3, new HurtByTargetGoal(pokemonEntity));
//            pokemonEntity.targetSelector.addGoal(4, new CaughtByTargetGoal(pokemonEntity));
//            pokemonEntity.targetSelector.addGoal(5, new PokemonNearestAttackableTargetGoal<>(pokemonEntity, Player.class, 48.0f, true,true));
//
//
//        }
    }
}
