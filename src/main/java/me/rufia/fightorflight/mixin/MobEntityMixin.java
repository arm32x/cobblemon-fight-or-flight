package me.rufia.fightorflight.mixin;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Shadow
    @Final
    protected GoalSelector goalSelector;
    @Shadow
    @Final
    protected GoalSelector targetSelector;

}
