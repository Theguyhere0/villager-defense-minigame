package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.common.Calculator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class VDGhastAttackGoal extends Goal {
	private final Ghast ghast;
	private final int targetRange;
	private final int attackSpeedTicks;
	private int chargeTime;
	private int ticksUntilNextDestinationRecalculation;

	public VDGhastAttackGoal(Ghast ghast, int targetRange, double attackSpeedSeconds) {
		this.ghast = ghast;
		this.targetRange = targetRange;
		attackSpeedTicks = Calculator.secondsToTicks(attackSpeedSeconds);
		setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return ghast.getTarget() != null && ghast
			.getTarget()
			.getActiveEffects()
			.stream()
			.noneMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY);
	}

	@Override
	public void start() {
		chargeTime = 0;
		ticksUntilNextDestinationRecalculation = 0;
	}

	@Override
	public void stop() {
		ghast.setCharging(false);
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity target = ghast.getTarget();
		if (canUse()) {
			ticksUntilNextDestinationRecalculation--;
			if (ghast.distanceToSqr(target) < targetRange * targetRange && ghast.hasLineOfSight(target)) {
				Level world = ghast.level;
				++chargeTime;

				// Scream 1 second before shooting
				if (chargeTime == attackSpeedTicks - Calculator.secondsToTicks(1) && !ghast.isSilent()) {
					world.levelEvent(null, 1015, ghast.blockPosition(), 0);
				}

				// Shoot
				if (chargeTime == attackSpeedTicks) {
					Vec3 vec3d = ghast.getViewVector(1.0F);
					double deltaX = target.getX() - (ghast.getX() + vec3d.x * 4.0);
					double deltaY = target.getY(0.5) - (0.5 + ghast.getY(0.5));
					double deltaZ = target.getZ() - (ghast.getZ() + vec3d.z * 4.0);
					if (!ghast.isSilent())
						world.levelEvent(null, 1016, ghast.blockPosition(), 0);

					LargeFireball entitylargefireball = new LargeFireball(world, ghast, deltaX, deltaY, deltaZ,
						ghast.getExplosionPower()
					);
					entitylargefireball.bukkitYield = (float) (entitylargefireball.explosionPower =
						ghast.getExplosionPower());
					entitylargefireball.setPos(
						ghast.getX() + vec3d.x * 4.0,
						ghast.getY(0.5) + 0.5, entitylargefireball.getZ() + vec3d.z * 4.0
					);
					world.addFreshEntity(entitylargefireball);
					chargeTime = 0;
				}

				if (ticksUntilNextDestinationRecalculation <= 0) {
					// Recalculate destination
					RandomSource randomsource = ghast.getRandom();
					double destinationX = target.getX() + (double) ((randomsource.nextFloat() * 2 - 1) * 12);
					double destinationY = target.getY() + (double) (randomsource.nextFloat() * 5 + 1);
					double destinationZ = target.getZ() + (double) ((randomsource.nextFloat() * 2 - 1) * 12);
					ghast
						.getMoveControl()
						.setWantedPosition(destinationX, destinationY, destinationZ, 1.0);

					double targetDistance = ghast.distanceToSqr(target);
					ticksUntilNextDestinationRecalculation = 4 + ghast
						.getRandom()
						.nextInt(7);
					if (targetDistance > 1024.0) {
						ticksUntilNextDestinationRecalculation += 10;
					}
					else if (targetDistance > 256.0) {
						ticksUntilNextDestinationRecalculation += 5;
					}
				}
			}
			else if (chargeTime > 0) {
				--chargeTime;
			}

			ghast.setCharging(chargeTime > 10);
		}

	}
}
