package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.goals.*;
import me.rufia.fightorflight.interfaces.IEntityAddGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector;


@Mixin(PokemonEntity.class)
public abstract class PokemonEntityInitGoalMixin extends MobEntityMixin implements IEntityAddGoal {


    @Inject(at = @At("TAIL"), method = "initGoals")
    private void injectMethod(CallbackInfo info) {
        // This code is injected into the start of MinecraftServer.loadWorld()V

        float fleeSpeed = 1.5f;
        float pursuitSpeed = 1.2f;

        this.goalSelector.add(3, new PokemonAvoidGoal((PokemonEntity) (Object) this, 48.0f, 1.0f, fleeSpeed));
        this.goalSelector.add(3, new PokemonMeleeAttackGoal((PokemonEntity) (Object) this, pursuitSpeed, true));
        this.goalSelector.add(4, new PokemonPanicGoal((PokemonEntity) (Object) this, fleeSpeed));

        this.targetSelector.add(1, new PokemonOwnerHurtByTargetGoal((PokemonEntity) (Object) this));
        this.targetSelector.add(2, new PokemonOwnerHurtTargetGoal((PokemonEntity) (Object) this));
        this.targetSelector.add(3, new RevengeGoal((PokemonEntity) (Object) this));
        this.targetSelector.add(4, new CaughtByTargetGoal((PokemonEntity) (Object) this));
        this.targetSelector.add(5, new PokemonNearestAttackableTargetGoal<>((PokemonEntity) (Object) this, PlayerEntity.class, 48.0f, true,true));

    }

}
