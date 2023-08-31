package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.common.Calculator;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class VDMeleeAttackGoal extends Goal {
	protected final PathfinderMob mob;
	private final boolean followingTargetEvenIfNotSeen;
	private Path path;
	private double pathedTargetX;
	private double pathedTargetY;
	private double pathedTargetZ;
	private int ticksUntilNextPathRecalculation;
	private long nextAttackTimeMillis = System.currentTimeMillis();
	private final int attackIntervalMillis;
	private final double attackReachBlocks;
	private long lastCanUseCheck;

	public VDMeleeAttackGoal(PathfinderMob mob, boolean followingTargetEvenIfNotSeen, double attackIntervalSeconds,
		double attackReachBlocks
	) {
		this.mob = mob;
		this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
		this.attackIntervalMillis = Calculator.secondsToMillis(attackIntervalSeconds);
		this.attackReachBlocks = attackReachBlocks;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		long currentTime = mob.level.getGameTime();
		if (currentTime - lastCanUseCheck < 20L) {
			return false;
		} else {
			lastCanUseCheck = currentTime;
			LivingEntity target = mob.getTarget();
			if (target == null) {
				return false;
			} else if (!target.isAlive()) {
				return false;
			}
			else if (target
				.getActiveEffects()
				.stream()
				.anyMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY))
				return false;
			else {
				path = mob
					.getNavigation()
					.createPath(target, 0);
				if (path != null) {
					return true;
				}
				else {
					return getAttackReachSqr(target) >= mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
				}
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = mob.getTarget();
		if (target == null) {
			return false;
		}
		else if (!target.isAlive()) {
			return false;
		}
		else if (target
			.getActiveEffects()
			.stream()
			.anyMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY)) {
			return false;
		}
		else if (!followingTargetEvenIfNotSeen) {
			return !mob.getNavigation().isDone();
		}
		else if (!mob.isWithinRestriction(target.blockPosition())) {
			return false;
		}
		else {
			return !(target instanceof Player) || !target.isSpectator() && !((Player)target).isCreative();
		}
	}

	@Override
	public void start() {
		mob.getNavigation().moveTo(path, 1);
		mob.setAggressive(true);
		ticksUntilNextPathRecalculation = 0;
	}

	@Override
	public void stop() {
		LivingEntity target = mob.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
			mob.setTarget(null);
		}

		mob.setAggressive(false);
		mob.getNavigation().stop();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity target = mob.getTarget();
		if (canContinueToUse()) {
			mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
			double targetDistance = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
			ticksUntilNextPathRecalculation = Math.max(ticksUntilNextPathRecalculation - 1, 0);
			if ((followingTargetEvenIfNotSeen || mob
				.getSensing()
				.hasLineOfSight(target)) && ticksUntilNextPathRecalculation <= 0 &&
				(pathedTargetX == 0.0 && pathedTargetY == 0.0 && pathedTargetZ == 0.0 ||
					target.distanceToSqr(pathedTargetX, pathedTargetY, pathedTargetZ) >= 1.0 || mob
					.getRandom()
					.nextFloat() < 0.05F)) {
				pathedTargetX = target.getX();
				pathedTargetY = target.getY();
				pathedTargetZ = target.getZ();
				ticksUntilNextPathRecalculation = 4 + mob.getRandom().nextInt(7);
				if (targetDistance > 1024.0) {
					ticksUntilNextPathRecalculation += 10;
				} else if (targetDistance > 256.0) {
					ticksUntilNextPathRecalculation += 5;
				}
				if (!mob.getNavigation().moveTo(target, 1)) {
					ticksUntilNextPathRecalculation += 15;
				}
			}

			checkAndPerformAttack(target, targetDistance);
		}
	}

	protected void checkAndPerformAttack(LivingEntity target, double targetDistance) {
		double attackDistance = getAttackReachSqr(target);
		if (targetDistance <= attackDistance && nextAttackTimeMillis <= System.currentTimeMillis()) {
			resetAttackCooldown();
			mob.swing(InteractionHand.MAIN_HAND);
			mob.doHurtTarget(target);
		}
	}

	protected void resetAttackCooldown() {
		nextAttackTimeMillis += attackIntervalMillis;
	}

	protected int getMillisUntilNextAttack() {
		return (int) Math.max(System.currentTimeMillis() - nextAttackTimeMillis, 0);
	}

	protected int getAttackIntervalMillis() {
		return attackIntervalMillis;
	}

	protected double getAttackReachSqr(LivingEntity target) {
		return attackReachBlocks * attackReachBlocks + target.getBbWidth();
	}
}
