package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ghast;

import java.util.EnumSet;

public class VDRandomFloatAroundGoal extends Goal {
	private final Ghast ghast;

	public VDRandomFloatAroundGoal(Ghast ghast) {
		this.ghast = ghast;
		setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		MoveControl moveControl = ghast.getMoveControl();
		if (!moveControl.hasWanted()) {
			return true;
		}
		else {
			double deltaX = moveControl.getWantedX() - ghast.getX();
			double deltaY = moveControl.getWantedY() - ghast.getY();
			double deltaZ = moveControl.getWantedZ() - ghast.getZ();
			double distanceSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
			return distanceSqr < 1.0 || distanceSqr > 3600.0;
		}
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void start() {
		RandomSource randomsource = ghast.getRandom();
		double destinationX = ghast.getX() + (double) ((randomsource.nextFloat() * 2 - 1) * 16);
		double destinationY = ghast.getY() + (double) ((randomsource.nextFloat() * 2 - 1) * 6);
		double destinationZ = ghast.getZ() + (double) ((randomsource.nextFloat() * 2 - 1) * 16);
		ghast
			.getMoveControl()
			.setWantedPosition(destinationX, destinationY, destinationZ, 1.0);
	}
}
