package me.theguyhere.villagerdefense.nms.v1_19_r3.goals;

import me.theguyhere.villagerdefense.nms.v1_19_r3.mobs.VDPhantom;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VDPhantomSweepAttackGoal extends VDPhantomMoveTargetGoal {
	private static final int CAT_SEARCH_TICK_DELAY = 20;
	private boolean isScaredOfCat;
	private int catSearchTick;
	private final int attackRange;

	public VDPhantomSweepAttackGoal(VDPhantom phantom, int attackRange) {
		super(phantom);
		this.attackRange = attackRange;
	}

	public boolean canUse() {
		return phantom.getTarget() != null && phantom.getAttackPhase() == VDPhantom.AttackPhase.SWOOP;
	}

	public boolean canContinueToUse() {
		LivingEntity target = phantom.getTarget();
		if (target == null) {
			return false;
		} else if (!target.isAlive()) {
			return false;
		} else {
			// Avoid targeting spectators or creative players
			if (target instanceof Player) {
				Player player = (Player) target;
				if (target.isSpectator() || player.isCreative()) {
					return false;
				}
			}

			// Make phantom scared of cats within 1/4 distance of attack range
			if (!this.canUse()) {
				return false;
			} else {
				if (phantom.tickCount > this.catSearchTick) {
					this.catSearchTick = phantom.tickCount + CAT_SEARCH_TICK_DELAY;
					List<Cat> list = phantom.level.getEntitiesOfClass(Cat.class,
						phantom.getBoundingBox().inflate((double) attackRange / 4),
						EntitySelector.ENTITY_STILL_ALIVE);

					for (Cat entitycat : list) {
						entitycat.hiss();
					}

					this.isScaredOfCat = !list.isEmpty();
				}

				return !this.isScaredOfCat;
			}
		}
	}

	public void start() {
	}

	public void stop() {
		phantom.setTarget(null);
		phantom.setAttackPhase(VDPhantom.AttackPhase.CIRCLE);
	}

	public void tick() {
		LivingEntity target = phantom.getTarget();
		if (target != null) {
			phantom.setMoveTargetPoint(new Vec3(target.getX(), target.getY(0.5), target.getZ()));

			// Attack
			if (phantom.getBoundingBox().inflate(0.20000000298023224).intersects(target.getBoundingBox())) {
				phantom.doHurtTarget(target);
				phantom.setAttackPhase(VDPhantom.AttackPhase.CIRCLE);
				if (!phantom.isSilent()) {
					phantom.level.levelEvent(1039, phantom.blockPosition(), 0);
				}
			}

			// Leave attack if hit something or hurt
			else if (phantom.horizontalCollision || phantom.hurtTime > 0) {
				phantom.setAttackPhase(VDPhantom.AttackPhase.CIRCLE);
			}
		}
	}
}
