package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.common.Reflection;
import me.theguyhere.villagerdefense.nms.v1_19_r3.mobs.VDBlaze;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.SmallFireball;

import java.util.EnumSet;

public class VDBlazeAttackGoal extends Goal {
	private final VDBlaze blaze;
	private int attackStep;
	private int attackTime;
	private final int attackSpeed;
	private int lastSeen;

	public VDBlazeAttackGoal(VDBlaze blaze, int attackSpeed) {
		this.blaze = blaze;
		this.attackSpeed = attackSpeed;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity target = blaze.getTarget();
		return target != null && target
			.getActiveEffects()
			.stream()
			.noneMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY) && target.isAlive() &&
			blaze.canAttack(target);
	}

	@Override
	public void start() {
		attackStep = 0;
	}

	@Override
	public void stop() {
		Reflection.invokeMethod(blaze, Blaze.class, "w", new Class[]{boolean.class}, false);
		lastSeen = 0;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		--attackTime;
		LivingEntity target = blaze.getTarget();
		if (canUse()) {
			boolean hasLineOfSight = blaze
				.getSensing()
				.hasLineOfSight(target);
			if (hasLineOfSight) {
				lastSeen = 0;
			}
			else {
				++lastSeen;
			}

			double targetDistance = blaze.distanceToSqr(target);
			if (targetDistance < 4.0) {
				if (!hasLineOfSight) {
					return;
				}

				if (attackTime <= 0) {
					attackTime = attackSpeed;
					blaze.doHurtTarget(target);
				}

				blaze
					.getMoveControl()
					.setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
			}
			else if (targetDistance < getFollowDistance() * getFollowDistance() && hasLineOfSight) {
				double deltaX = target.getX() - blaze.getX();
				double deltaY = target.getY(0.5) - blaze.getY(0.5);
				double deltaZ = target.getZ() - blaze.getZ();
				if (attackTime <= 0) {
					++attackStep;
					if (attackStep == 1) {
						attackTime = attackSpeed;
						Reflection.invokeMethod(blaze, Blaze.class, "w", new Class[]{boolean.class}, true);
					}
					else if (attackStep <= 4) {
						attackTime = attackSpeed / 3;
					}
					else {
						attackTime = attackSpeed;
						attackStep = 0;
						Reflection.invokeMethod(blaze, Blaze.class, "w", new Class[]{boolean.class}, false);
					}

					if (attackStep > 1) {
						double var10 = Math.sqrt(Math.sqrt(targetDistance)) * 0.5;
						if (!blaze.isSilent()) {
							blaze.level.levelEvent(null, 1018, blaze.blockPosition(), 0);
						}

						for (int var12 = 0; var12 < 1; ++var12) {
							SmallFireball var13 = new SmallFireball(blaze.level, blaze, blaze
								.getRandom()
								.triangle(deltaX, 2.297 * var10), deltaY, blaze
								.getRandom()
								.triangle(deltaZ, 2.297 * var10));
							var13.setPos(var13.getX(), blaze.getY(0.5) + 0.5, var13.getZ());
							blaze.level.addFreshEntity(var13);
						}
					}
				}

				blaze
					.getLookControl()
					.setLookAt(target, 10.0F, 10.0F);
			}
			else if (lastSeen < 5) {
				blaze
					.getMoveControl()
					.setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
			}

			super.tick();
		}
	}

	private double getFollowDistance() {
		return blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
	}
}
