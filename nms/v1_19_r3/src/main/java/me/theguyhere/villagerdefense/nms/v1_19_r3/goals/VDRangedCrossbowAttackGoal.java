package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.Constants;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class VDRangedCrossbowAttackGoal<T extends Monster & RangedAttackMob> extends Goal {
	public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
	private final T mob;
	private final double attackIntervalSeconds;
	private CrossbowState crossbowState;
	private final double speedModifier;
	private final float attackRadiusSqr;
	private long nextAttackTimeMillis = System.currentTimeMillis();
	private int seeTimeTicks;
	private int updatePathDelay;

	public VDRangedCrossbowAttackGoal(T mob, double attackIntervalSeconds, double speedModifier, float attackRadius) {
		crossbowState = CrossbowState.UNCHARGED;
		this.mob = mob;
		this.attackIntervalSeconds = attackIntervalSeconds * Constants.ATTACK_SPEED_RANGED_MULTIPLIER;
		this.speedModifier = speedModifier;
		attackRadiusSqr = (float) (attackRadius * attackRadius * Constants.CROSSBOW_ATTACK_RANGE_MULTIPLIER *
			Constants.CROSSBOW_ATTACK_RANGE_MULTIPLIER);
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return isValidTarget() && isHoldingCrossbow();
	}

	protected boolean isHoldingCrossbow() {
		return mob.isHolding(Items.CROSSBOW);
	}

	@Override
	public boolean canContinueToUse() {
		return isValidTarget() && (canUse() || !mob.getNavigation().isDone()) && isHoldingCrossbow();
	}

	private boolean isValidTarget() {
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
		((CrossbowAttackMob) mob).setChargingCrossbow(false);
		CrossbowItem.setCharged( mob.getUseItem(), false);
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

			// If outside target range or just seen, navigate to the target, otherwise stop navigation
			if (targetDistance > (double) attackRadiusSqr || seeTimeTicks < 5) {
				updatePathDelay--;
				if (updatePathDelay <= 0) {
					mob.getNavigation().moveTo(target, canRun() ? speedModifier : speedModifier * 0.5);
					updatePathDelay = PATHFINDING_DELAY_RANGE.sample(mob.getRandom());
				}
			}
			else {
				updatePathDelay = 0;
				mob.getNavigation().stop();
			}

			// Look at the target
			mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

			// Start charging when in range and seen for a while
			if (crossbowState == CrossbowState.UNCHARGED) {
				if (targetDistance <= (double) attackRadiusSqr && seeTimeTicks > 20) {
					mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
					crossbowState = CrossbowState.CHARGING;
					((CrossbowAttackMob) mob).setChargingCrossbow(true);
				}
			}

			// Charge for 3/4 of the attack time
			else if (crossbowState == CrossbowState.CHARGING) {
				if (!mob.isUsingItem()) {
					crossbowState = CrossbowState.UNCHARGED;
				}

				int useTimeTicks = mob.getTicksUsingItem();
				if (useTimeTicks >= Calculator.secondsToTicks(attackIntervalSeconds * 0.75)) {
					mob.releaseUsingItem();
					crossbowState = CrossbowState.CHARGED;
					((CrossbowAttackMob) mob).setChargingCrossbow(false);
				}
			}

			// Attack if ready and has line of sight
			else if (crossbowState == CrossbowState.CHARGED && hasLineOfSight &&
				nextAttackTimeMillis <= System.currentTimeMillis()) {
				mob.performRangedAttack(target, 1.0F);
				ItemStack crossbow = mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(mob, Items.CROSSBOW));
				CrossbowItem.setCharged(crossbow, false);
				crossbowState = CrossbowState.UNCHARGED;
				nextAttackTimeMillis = System.currentTimeMillis() + Calculator.secondsToMillis(attackIntervalSeconds);
			}
		}
	}

	private boolean canRun() {
		return crossbowState == CrossbowState.UNCHARGED;
	}

	enum CrossbowState {
		UNCHARGED,
		CHARGING,
		CHARGED;

		CrossbowState() {
		}
	}
}
