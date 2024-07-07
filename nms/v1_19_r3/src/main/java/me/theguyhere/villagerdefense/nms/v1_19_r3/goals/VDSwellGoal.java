package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;

public class VDSwellGoal extends SwellGoal {
	private final Creeper creeper;
	private final double attackReachSqr;

	public VDSwellGoal(Creeper creeper, double attackReachBlocks) {
		super(creeper);
		this.creeper = creeper;
		attackReachSqr = attackReachBlocks * attackReachBlocks;
	}

	@Override
	public boolean canUse() {
		LivingEntity target = creeper.getTarget();
		return super.canUse() && target != null && target.isAlive() && target
			.getActiveEffects()
			.stream()
			.noneMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY);
	}

	@Override
	public void tick() {
		LivingEntity target = creeper.getTarget();
		if (!canUse())
			creeper.setSwellDir(-1);
		else if (creeper.distanceToSqr(target) > attackReachSqr)
			creeper.setSwellDir(-1);
		else if (!creeper.getSensing().hasLineOfSight(target))
			creeper.setSwellDir(-1);
		else creeper.setSwellDir(1);
	}
}
