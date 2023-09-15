package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.nms.v1_19_r3.mobs.VDPhantom;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class VDPhantomMoveTargetGoal extends Goal {
	final VDPhantom phantom;

	public VDPhantomMoveTargetGoal(VDPhantom phantom) {
		setFlags(EnumSet.of(Flag.MOVE));
		this.phantom = phantom;
	}

	protected boolean touchingTarget() {
		double TOUCH_RADIUS = 2;
		return phantom
			.getMoveTargetPoint()
			.distanceToSqr(phantom.getX(), phantom.getY(), phantom.getZ()) < TOUCH_RADIUS * TOUCH_RADIUS;
	}
}
