package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDMeleeAttackGoal;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
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

public class VDBrute extends PiglinBrute {
	@Nullable
	private LivingEntity target;

	public VDBrute(Location location) {
		super(EntityType.PIGLIN_BRUTE, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());

		// Override behaviors
		setCanPickUpLoot(false);
		if (GoalUtils.hasGroundPathNavigation(this)) {
			((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(false);
		}
		setImmuneToZombification(true);
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new VDMeleeAttackGoal(this, false, Constants.ATTACK_INTERVAL_SLOW, 1.5));
		goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(4, new RandomLookAroundGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
			VDMobPredicates.isVisible().and(VDMobPredicates.isMelee())
		));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Wolf.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Cat.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Horse.class, true,
			VDMobPredicates.isVisible()));
	}

	// Customized attributes
	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(PiglinBrute
			.createAttributes()
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_CLOSE)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_FAST)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_LOW)
			.add(Attributes.ATTACK_SPEED, Constants.ATTACK_INTERVAL_SLOW)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_HEAVY)
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
}
