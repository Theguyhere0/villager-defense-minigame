package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDPhantomAttackStrategyGoal;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDPhantomCircleAroundAnchorGoal;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDPhantomSweepAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VDPhantom extends Phantom {
	private AttackPhase attackPhase;
	private BlockPos anchorPoint;
	private Vec3 moveTargetPoint;

	public VDPhantom(Location location) {
		super(EntityType.PHANTOM, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
		attackPhase = AttackPhase.CIRCLE;
		anchorPoint = BlockPos.ZERO;
		moveControl = new VDPhantomMoveControl(this);
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new VDPhantomAttackStrategyGoal(this, Constants.ATTACK_SPEED_VERY_SLOW));
		goalSelector.addGoal(2, new VDPhantomSweepAttackGoal(this, Constants.TARGET_RANGE_FAR));
		goalSelector.addGoal(3, new VDPhantomCircleAroundAnchorGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Villager.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wolf.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cat.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Horse.class, true,
			VDMobPredicates.isVisible()));
	}

	// Customized attributes
	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(Monster
			.createMonsterAttributes()
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_FAR)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_MEDIUM)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_MODERATE)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_LIGHT)
			.build());
	}

	public AttackPhase getAttackPhase() {
		return attackPhase;
	}

	public void setAttackPhase(AttackPhase attackPhase) {
		this.attackPhase = attackPhase;
	}

	public BlockPos getAnchorPoint() {
		return anchorPoint;
	}

	public void setAnchorPoint(BlockPos anchorPoint) {
		this.anchorPoint = anchorPoint;
	}

	public Vec3 getMoveTargetPoint() {
		return moveTargetPoint;
	}

	public void setMoveTargetPoint(Vec3 moveTargetPoint) {
		this.moveTargetPoint = moveTargetPoint;
	}

	// Recreate private attack phase enums
	public enum AttackPhase {
		CIRCLE,
		SWOOP;

		AttackPhase() {
		}
	}

	private class VDPhantomMoveControl extends MoveControl {
		private float speed = 0.1F;

		public VDPhantomMoveControl(Mob entityInsentient) {
			super(entityInsentient);
		}

		public void tick() {
			if (horizontalCollision) {
				setYRot(getYRot() + 180.0F);
				this.speed = 0.1F;
			}

			double d0 = moveTargetPoint.x - getX();
			double d1 = moveTargetPoint.y - getY();
			double d2 = moveTargetPoint.z - getZ();
			double d3 = Math.sqrt(d0 * d0 + d2 * d2);
			if (Math.abs(d3) > 9.999999747378752E-6) {
				double d4 = 1.0 - Math.abs(d1 * 0.699999988079071) / d3;
				d0 *= d4;
				d2 *= d4;
				d3 = Math.sqrt(d0 * d0 + d2 * d2);
				double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
				float f = getYRot();
				float f1 = (float) Mth.atan2(d2, d0);
				float f2 = Mth.wrapDegrees(getYRot() + 90.0F);
				float f3 = Mth.wrapDegrees(f1 * 57.295776F);
				setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
				yBodyRot = getYRot();
				if (Mth.degreesDifferenceAbs(f, getYRot()) < 3.0F) {
					this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
				} else {
					this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
				}

				float f4 = (float)(-(Mth.atan2(-d1, d3) * 57.2957763671875));
				setXRot(f4);
				float f5 = getYRot() + 90.0F;
				double d6 = (double)(this.speed * Mth.cos(f5 * 0.017453292F)) * Math.abs(d0 / d5);
				double d7 = (double)(this.speed * Mth.sin(f5 * 0.017453292F)) * Math.abs(d2 / d5);
				double d8 = (double)(this.speed * Mth.sin(f4 * 0.017453292F)) * Math.abs(d1 / d5);
				Vec3 vec3d = getDeltaMovement();
				setDeltaMovement(vec3d.add((new Vec3(d6, d8, d7)).subtract(vec3d).scale(0.2)));
			}
		}
	}
}
