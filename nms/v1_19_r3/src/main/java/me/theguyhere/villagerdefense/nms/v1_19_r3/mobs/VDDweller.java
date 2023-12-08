package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import net.minecraft.world.phys.Vec3;

public interface VDDweller {
	Vec3 getFlatRandomLocationInHome();

	boolean isHome();
}
