package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.nms.v1_19_r3.mobs.VDDweller;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;

public class VDMoveBackToSpawnGoal<T extends PathfinderMob & VDDweller> extends RandomStrollGoal {
	private final T mob;

	public VDMoveBackToSpawnGoal(T mob) {
		super(mob, 1, 2);
		this.mob = mob;
	}

	@Override
	public boolean canUse() {
		if (canContinueToUse()) {
			if (!forceTrigger) {
				if (mob.getNoActionTime() >= 100) {
					return false;
				}

				if (mob.isHome()) {
					return false;
				}
			}

			Vec3 destination = mob.getFlatRandomLocationInHome();
			if (destination == null) {
				return false;
			} else {
				wantedX = destination.x;
				wantedY = destination.y;
				wantedZ = destination.z;
				forceTrigger = false;
				return true;
			}
		} else {
			return false;
		}
	}
}
