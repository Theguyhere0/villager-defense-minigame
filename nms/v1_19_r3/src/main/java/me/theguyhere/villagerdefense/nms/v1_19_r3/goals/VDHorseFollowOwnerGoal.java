package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.EnumSet;

public class VDHorseFollowOwnerGoal extends Goal {
	private static final int TELEPORT_WHEN_DISTANCE_IS = 15;

	private final AbstractHorse horse;
	private LivingEntity owner;
	private final LevelReader level;
	private final double speedModifier;
	private final PathNavigation navigation;
	private int timeToRecalcPath;
	private final float stopDistance;
	private final float startDistance;
	private float oldWaterCost;

	public VDHorseFollowOwnerGoal(AbstractHorse horse) {
		this.horse = horse;
		level = horse.level;
		speedModifier = 1;
		navigation = horse.getNavigation();
		startDistance = 12;
		stopDistance = 4;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public boolean canUse() {
		LivingEntity living = horse.getOwner();
		if (living == null) {
			return false;
		} else if (living.isSpectator()) {
			return false;
		} else if (unableToMove()) {
			return false;
		} else if (horse.distanceToSqr(living) < (double)(startDistance * startDistance)) {
			return false;
		} else {
			owner = living;
			return true;
		}
	}

	public boolean canContinueToUse() {
		return !navigation.isDone() &&
			(!unableToMove() && horse.distanceToSqr(owner) > (double) (stopDistance * stopDistance));
	}

	private boolean unableToMove() {
		return horse.isPassenger() || horse.isLeashed();
	}

	public void start() {
		timeToRecalcPath = 0;
		oldWaterCost = horse.getPathfindingMalus(BlockPathTypes.WATER);
		horse.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
	}

	public void stop() {
		owner = null;
		navigation.stop();
		horse.setPathfindingMalus(BlockPathTypes.WATER, oldWaterCost);
	}

	public void tick() {
		horse.getLookControl().setLookAt(owner, 10.0F, (float) horse.getMaxHeadXRot());
		if (--timeToRecalcPath <= 0) {
			timeToRecalcPath = adjustedTickDelay(10);
			if (horse.distanceToSqr(owner) >= TELEPORT_WHEN_DISTANCE_IS * TELEPORT_WHEN_DISTANCE_IS) {
				teleportToOwner();
			} else {
				navigation.moveTo(owner, speedModifier);
			}
		}

	}

	private void teleportToOwner() {
		BlockPos blockPos = owner.blockPosition();

		for(int i = 0; i < 10; ++i) {
			int j = randomIntInclusive(-3, 3);
			int k = randomIntInclusive(-1, 1);
			int l = randomIntInclusive(-3, 3);
			boolean flag = maybeTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
			if (flag) {
				return;
			}
		}

	}

	private boolean maybeTeleportTo(int i, int j, int k) {
		if (Math.abs((double) i - owner.getX()) < 2.0 && Math.abs((double) k - owner.getZ()) < 2.0) {
			return false;
		} else if (!canTeleportTo(new BlockPos(i, j, k))) {
			return false;
		} else {
			CraftEntity entity = horse.getBukkitEntity();
			Location to = new Location(entity.getWorld(), (double) i + 0.5, j, (double) k + 0.5, horse.getYRot(), horse.getXRot());
			EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
			horse.level.getCraftServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			} else {
				to = event.getTo();
				horse.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
				navigation.stop();
				return true;
			}
		}
	}

	private boolean canTeleportTo(BlockPos blockPos) {
		BlockPathTypes pathType = WalkNodeEvaluator.getBlockPathTypeStatic(level, blockPos.mutable());
		if (pathType != BlockPathTypes.WALKABLE) {
			return false;
		} else {
			BlockState blockData = level.getBlockState(blockPos.below());
			if (blockData.getBlock() instanceof LeavesBlock) {
				return false;
			} else {
				BlockPos newBlockPos = blockPos.subtract(horse.blockPosition());
				return level.noCollision(horse, horse.getBoundingBox().move(newBlockPos));
			}
		}
	}

	private int randomIntInclusive(int i, int j) {
		return horse.getRandom().nextInt(j - i + 1) + i;
	}
}
