package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;

public class VDSwellGoal extends SwellGoal {
	private final Creeper creeper;

	public VDSwellGoal(Creeper creeper) {
		super(creeper);
		this.creeper = creeper;
	}

	@Override
	public boolean canUse() {
		LivingEntity target = creeper.getTarget();
		return super.canUse() && target != null && target
			.getActiveEffects()
			.stream()
			.noneMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY);
	}
}
