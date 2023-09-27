package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.Constants;
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
	private final double attackIntervalSeconds;
	private final float attackRadiusSqr;
	private long nextAttackTimeMillis = System.currentTimeMillis();
	private int seeTimeTicks;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTimeTicks = -1;

	public VDRangedBowAttackGoal(T mob, double attackIntervalSeconds, float attackRadius) {
		this.mob = mob;
		this.attackIntervalSeconds = attackIntervalSeconds * Constants.ATTACK_SPEED_RANGED_MULTIPLIER;
		attackRadiusSqr = (float) (attackRadius * attackRadius * Constants.BOW_ATTACK_RANGE_MULTIPLIER *
			Constants.BOW_ATTACK_RANGE_MULTIPLIER);
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
		seeTimeTicks = 0;
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

			// Increase see time if target is visible, otherwise decrease, and make sure see time starts at 0 every time
			if (hasLineOfSight != seeTimeTicks > 0) {
				seeTimeTicks = 0;
			}

			if (hasLineOfSight) {
				++seeTimeTicks;
			}
			else {
				--seeTimeTicks;
			}

			// If within range and seen for a while, start strafing, otherwise navigate to the target
			if (targetDistance <= (double) attackRadiusSqr && seeTimeTicks >= 20) {
				mob
					.getNavigation()
					.stop();
				++strafingTimeTicks;
			}
			else {
				mob
					.getNavigation()
					.moveTo(target, 1);
				strafingTimeTicks = -1;
			}

			// Possibly change strafe direction every 20 ticks
			if (strafingTimeTicks >= 20) {
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

				strafingTimeTicks = 0;
			}

			// Strafe to keep between 75% and 25% of attack radius sqr
			if (strafingTimeTicks > -1) {
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

			// Make mob spend half the time using the item before attacking
			if (mob.isUsingItem()) {
				if (!hasLineOfSight && seeTimeTicks < -Calculator.secondsToTicks(attackIntervalSeconds)) {
					mob.stopUsingItem();
				}
				else if (hasLineOfSight) {
					int ticksUsingItem = mob.getTicksUsingItem();
					if (ticksUsingItem >= Calculator.secondsToTicks(attackIntervalSeconds) / 2) {
						mob.stopUsingItem();
						mob.performRangedAttack(target, BowItem.getPowerForTime(ticksUsingItem));
						nextAttackTimeMillis =
							System.currentTimeMillis() + Calculator.secondsToMillis(attackIntervalSeconds);
					}
				}
			}
			else if (nextAttackTimeMillis - Calculator.secondsToMillis(attackIntervalSeconds) / 2 <=
				System.currentTimeMillis() && seeTimeTicks >= -60) {
				mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(mob, Items.BOW));
			}
		}
	}
}
