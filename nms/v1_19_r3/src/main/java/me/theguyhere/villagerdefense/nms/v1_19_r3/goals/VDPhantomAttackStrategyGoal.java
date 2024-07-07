package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.nms.v1_19_r3.mobs.VDPhantom;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.levelgen.Heightmap;

public class VDPhantomAttackStrategyGoal extends Goal {
	private final VDPhantom phantom;
	private int nextSweepTick;
	private final int attackSpeedTicks;
	private final int HOVER_AVERAGE = 12;
	private final int HOVER_RANGE = 5;

	public VDPhantomAttackStrategyGoal(VDPhantom phantom, double attackSpeedSeconds) {
		this.phantom = phantom;
		attackSpeedTicks = Calculator.secondsToTicks(attackSpeedSeconds);
	}

	public boolean canUse() {
		LivingEntity target = phantom.getTarget();
		return target != null && phantom.canAttack(target, TargetingConditions.DEFAULT);
	}

	public void start() {
		// Set initial sweep delay to half attack speed
		nextSweepTick = attackSpeedTicks / 2;

		// Start in circling phase
		phantom.setAttackPhase(VDPhantom.AttackPhase.CIRCLE);

		// Set anchor
		setAnchorAboveTarget();
	}

	public void stop() {
		// Set anchor point somewhere above obstacles at the original anchor point
		phantom.setAnchorPoint(
			phantom.level
				.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, phantom.getAnchorPoint())
				.above(HOVER_AVERAGE + phantom
					.getRandom()
					.nextInt(HOVER_RANGE)));
	}

	public void tick() {
		// Only tick while circling
		if (phantom.getAttackPhase() == VDPhantom.AttackPhase.CIRCLE) {
			--nextSweepTick;

			// End of circling phase
			if (nextSweepTick <= 0) {
				phantom.setAttackPhase(VDPhantom.AttackPhase.SWOOP);
				setAnchorAboveTarget();
				nextSweepTick = attackSpeedTicks;
				phantom.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + phantom.getRandom().nextFloat() * 0.1F);
			}
		}
	}

	private void setAnchorAboveTarget() {
		phantom.setAnchorPoint(phantom
			.getTarget()
			.blockPosition()
			.above(HOVER_AVERAGE + phantom
				.getRandom()
				.nextInt(HOVER_RANGE)));
	}
}
