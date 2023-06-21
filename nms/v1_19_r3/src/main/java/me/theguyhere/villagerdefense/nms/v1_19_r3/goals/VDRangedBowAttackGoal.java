package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class VDRangedBowAttackGoal<T extends Monster & RangedAttackMob> extends Goal {
	private final T mob;
	private final int attackIntervalMin;
	private final float attackRadiusSqr;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public VDRangedBowAttackGoal(T mob, int attackIntervalMin, float attackRadius) {
		this.mob = mob;
		this.attackIntervalMin = attackIntervalMin;
		attackRadiusSqr = attackRadius * attackRadius;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity target = mob.getTarget();
		if (target == null)
			return false;
		else if (target
			.getActiveEffects()
			.stream()
			.anyMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY)) {
			return false;
		}
		else return isHoldingBow();
	}

	protected boolean isHoldingBow() {
		return mob.isHolding(Items.BOW);
	}

	@Override
	public boolean canContinueToUse() {
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
			return (!(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative()) &&
				(canUse() || !mob
					.getNavigation()
					.isDone()) && isHoldingBow();
		}
	}

	@Override
	public void start() {
		super.start();
		mob.setAggressive(true);
	}

	@Override
	public void stop() {
		super.stop();
		mob.setAggressive(false);
		seeTime = 0;
		attackTime = -1;
		mob.stopUsingItem();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity target = mob.getTarget();
		if (target != null && canContinueToUse()) {
			double targetDistance = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
			boolean hasLineOfSight = mob
				.getSensing()
				.hasLineOfSight(target);
			if (hasLineOfSight != seeTime > 0) {
				seeTime = 0;
			}

			if (hasLineOfSight) {
				++seeTime;
			}
			else {
				--seeTime;
			}

			if (!(targetDistance > (double) attackRadiusSqr) && seeTime >= 20) {
				mob
					.getNavigation()
					.stop();
				++strafingTime;
			}
			else {
				mob
					.getNavigation()
					.moveTo(target, 1);
				strafingTime = -1;
			}

			if (strafingTime >= 20) {
				if ((double) mob
					.getRandom()
					.nextFloat() < 0.3) {
					strafingClockwise = !strafingClockwise;
				}

				if ((double) mob
					.getRandom()
					.nextFloat() < 0.3) {
					strafingBackwards = !strafingBackwards;
				}

				strafingTime = 0;
			}

			if (strafingTime > -1) {
				if (targetDistance > (double) (attackRadiusSqr * 0.75F)) {
					strafingBackwards = false;
				}
				else if (targetDistance < (double) (attackRadiusSqr * 0.25F)) {
					strafingBackwards = true;
				}

				mob
					.getMoveControl()
					.strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
				mob.lookAt(target, 30.0F, 30.0F);
			}
			else {
				mob
					.getLookControl()
					.setLookAt(target, 30.0F, 30.0F);
			}

			if (mob.isUsingItem()) {
				if (!hasLineOfSight && seeTime < -60) {
					mob.stopUsingItem();
				}
				else if (hasLineOfSight) {
					int ticksUsingItem = mob.getTicksUsingItem();
					if (ticksUsingItem >= 20) {
						mob.stopUsingItem();
						mob.performRangedAttack(target, BowItem.getPowerForTime(ticksUsingItem));
						attackTime = attackIntervalMin;
					}
				}
			}
			else if (--attackTime <= 0 && seeTime >= -60) {
				mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(mob, Items.BOW));
			}

		}
	}
}
