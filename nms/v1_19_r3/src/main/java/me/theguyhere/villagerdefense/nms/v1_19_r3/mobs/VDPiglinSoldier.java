package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDMeleeAttackGoal;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class VDPiglinSoldier extends Piglin {
	@Nullable
	private LivingEntity target;

	public VDPiglinSoldier(Location location) {
		super(EntityType.PIGLIN, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());

		// Override behaviors
		setCanPickUpLoot(false);
		if (GoalUtils.hasGroundPathNavigation(this)) {
			((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(false);
		}
		setImmuneToZombification(true);
		setBaby(false);
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new VDMeleeAttackGoal(this, false, Constants.ATTACK_SPEED_MODERATE, 1.5));
		goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(4, new RandomLookAroundGoal(this));

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
		return new AttributeMap(PiglinBrute
			.createAttributes()
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_MODERATE)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_MEDIUM)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_HIGH)
			.add(Attributes.ATTACK_SPEED, Constants.ATTACK_SPEED_MODERATE)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_MEDIUM)
			.build());
	}

	// Override methods using brains
	@Override
	protected void sendDebugPackets() {
		DebugPackets.sendGoalSelector(level, this, goalSelector);
	}

	@Override
	@Nullable
	public LivingEntity getTarget() {
		return target;
	}

	@Override
	public boolean setTarget(LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
		if (getTarget() == target) {
			return false;
		}
		else {
			if (fireEvent) {
				if (reason == EntityTargetEvent.TargetReason.UNKNOWN && getTarget() != null && target == null) {
					reason = getTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET :
						EntityTargetEvent.TargetReason.TARGET_DIED;
				}

				if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
					level
						.getCraftServer()
						.getLogger()
						.log(java.util.logging.Level.WARNING, "Unknown target reason, please report on the issue " +
							"tracker", new Exception());
				}

				CraftLivingEntity ctarget = null;
				if (target != null) {
					ctarget = (CraftLivingEntity) target.getBukkitEntity();
				}

				EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(),
					ctarget, reason);
				level
					.getCraftServer()
					.getPluginManager()
					.callEvent(event);
				if (event.isCancelled()) {
					return false;
				}

				if (event.getTarget() != null) {
					target = ((CraftLivingEntity) event.getTarget()).getHandle();
				}
				else {
					target = null;
				}
			}

			this.target = target;
			return true;
		}
	}

	@Override
	protected void customServerAiStep() {
	}

	// Stop conversion shivers
	@Override
	public boolean isConverting() {
		return false;
	}

	// Prevent picking up items
	@Override
	public boolean canPickUpLoot() {
		return false;
	}

	// Override baby behavior
	@Override
	public void setBaby(boolean flag) {
		super.setBaby(flag);
		if (level != null && !level.isClientSide) {
			AttributeInstance attributeSpeed = getAttribute(Attributes.MOVEMENT_SPEED);
			assert attributeSpeed != null;
			attributeSpeed.removeModifier(new AttributeModifier(UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667"),
				"Baby speed boost", 0.20000000298023224, AttributeModifier.Operation.MULTIPLY_BASE
			));
		}
	}
}
