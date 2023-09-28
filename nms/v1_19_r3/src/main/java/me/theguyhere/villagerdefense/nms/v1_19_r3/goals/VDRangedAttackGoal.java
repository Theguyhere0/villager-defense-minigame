package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.Constants;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class VDRangedAttackGoal<T extends Mob & RangedAttackMob> extends Goal {
	private final T mob;
	private final double attackIntervalSeconds;

	private final double speedModifier;
	private long nextAttackTimeMillis = System.currentTimeMillis();

	private final float attackRadiusSqr;

	public VDRangedAttackGoal(T mob, double attackIntervalSeconds, double speedModifier, float attackRadius) {
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.attackIntervalSeconds = attackIntervalSeconds * Constants.ATTACK_INTERVAL_RANGED_MULTIPLIER;
		attackRadiusSqr = (float) (attackRadius * attackRadius * Constants.THROW_RANGE_MULTIPLIER *
			Constants.THROW_RANGE_MULTIPLIER);
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity target = mob.getTarget();
		if (target == null) {
			return false;
		} else if (!target.isAlive()) {
			return false;
		}
		else if (target
			.getActiveEffects()
			.stream()
			.anyMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY)) {
			return false;
		}
		else {
			return !(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative();
		}
	}

	@Override
	public boolean canContinueToUse() {
		return canUse() && !mob.getNavigation().isDone();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity target = mob.getTarget();
		double targetDistance = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
		boolean hasLineOfSight = mob
			.getSensing()
			.hasLineOfSight(target);

		// Stop navigating when within attack radius
		if (targetDistance <= (double) attackRadiusSqr) {
			mob.getNavigation().stop();
		} else {
			mob.getNavigation().moveTo(target, speedModifier);
		}

		// Look at target
		mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

		// Attack cooldown is up, ready to attack
		if (nextAttackTimeMillis <= System.currentTimeMillis()) {
			if (!hasLineOfSight) {
				return;
			}

			float distanceRatio = (float)Math.sqrt(targetDistance / attackRadiusSqr);
			float power = Mth.clamp(distanceRatio, 0.1F, 1.0F);
			mob.performRangedAttack(target, power);
			nextAttackTimeMillis = System.currentTimeMillis() + Calculator.secondsToMillis(attackIntervalSeconds);
		}
	}
}
