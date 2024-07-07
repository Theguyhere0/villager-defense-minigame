package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class VDGhastLookGoal extends Goal {
	private final Ghast ghast;
	private final int targetRange;

	public VDGhastLookGoal(Ghast ghast, int targetRange) {
		this.ghast = ghast;
		this.targetRange = targetRange;
		setFlags(EnumSet.of(Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return true;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity target = ghast.getTarget();
		if (target == null) {
			Vec3 vec3d = ghast.getDeltaMovement();
			ghast.setYRot(-((float) Mth.atan2(vec3d.x, vec3d.z)) * 57.295776F);
			ghast.yBodyRot = ghast.getYRot();
		}
		else {
			if (target.distanceToSqr(ghast) < targetRange * targetRange) {
				double deltaX = target.getX() - ghast.getX();
				double deltaZ = target.getZ() - ghast.getZ();
				ghast.setYRot(-((float) Mth.atan2(deltaX, deltaZ)) * 57.295776F);
				ghast.yBodyRot = ghast.getYRot();
			}
		}

	}
}
