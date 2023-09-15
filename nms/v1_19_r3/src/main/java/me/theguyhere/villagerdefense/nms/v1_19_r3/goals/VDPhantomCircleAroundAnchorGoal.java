package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.nms.v1_19_r3.mobs.VDPhantom;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VDPhantomCircleAroundAnchorGoal extends VDPhantomMoveTargetGoal {
	private float angle;
	private float distance;
	private float height;
	private float clockwise;

	public VDPhantomCircleAroundAnchorGoal(VDPhantom phantom) {
		super(phantom);
	}

	public boolean canUse() {
		return phantom.getTarget() == null || phantom.getAttackPhase() == VDPhantom.AttackPhase.CIRCLE;
	}

	public void start() {
		distance = 5.0F + phantom.getRandom().nextFloat() * 10.0F; // 5 to 15
		height = -4.0F + phantom.getRandom().nextFloat() * 9.0F; // -4 to 5
		clockwise = phantom.getRandom().nextBoolean() ? 1.0F : -1.0F; // 1 or -1
		selectNext();
	}

	public void tick() {
		// 1 in 350 chance to change height
		if (phantom.getRandom().nextInt(350) == 0) {
			height = -4.0F + phantom.getRandom().nextFloat() * 9.0F;
		}

		// 1 in 250 chance to increase distance up until 15, then reset back to 5 and change direction
		if (phantom.getRandom().nextInt(250) == 0) {
			++distance;
			if (distance > 15.0F) {
				distance = 5.0F;
				clockwise = -clockwise;
			}
		}

		// 1 in 450 chance to change angle
		if (phantom.getRandom().nextInt(450) == 0) {
			angle = phantom.getRandom().nextFloat() * 2.0F * 3.1415927F;
			selectNext();
		}

		// Change movement target if touching target
		if (touchingTarget()) {
			selectNext();
		}

		// Adjust height if phantom is above target and there is ground underneath
		if (phantom.getMoveTargetPoint().y < phantom.getY() && !phantom.level.isEmptyBlock(phantom.blockPosition().below(1))) {
			height = Math.max(1.0F, height);
			selectNext();
		}

		// Adjust height if phantom is below target and there is ceiling above
		if (phantom.getMoveTargetPoint().y > phantom.getY() && !phantom.level.isEmptyBlock(phantom.blockPosition().above(1))) {
			height = Math.min(-1.0F, height);
			selectNext();
		}
	}

	private void selectNext() {
		// Set anchor point to current position if not set
		if (BlockPos.ZERO.equals(phantom.getAnchorPoint())) {
			phantom.setAnchorPoint(phantom.blockPosition());
		}

		// Increase angle in proper direction
		angle += clockwise * 15.0F * 0.017453292F;
		phantom.setMoveTargetPoint(Vec3
			.atLowerCornerOf(phantom.getAnchorPoint()).add(
				distance * Mth.cos(angle),
				-4.0F + height, distance * Mth.sin(angle)
			));
	}
}
