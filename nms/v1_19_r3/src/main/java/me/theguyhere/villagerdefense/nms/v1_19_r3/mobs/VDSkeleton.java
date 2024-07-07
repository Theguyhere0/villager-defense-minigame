package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDRangedBowAttackGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VDSkeleton extends Skeleton {
	public VDSkeleton(Location location) {
		super(EntityType.SKELETON, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Wolf.class, 6, 1, 1));
		goalSelector.addGoal(3, new VDRangedBowAttackGoal<>(this, Constants.ATTACK_INTERVAL_SLOW,
			Constants.TARGET_RANGE_MODERATE));
		goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Villager.class, true));
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
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_MODERATE)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_MEDIUM)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_LOW)
			.add(Attributes.ATTACK_SPEED, Constants.ATTACK_INTERVAL_SLOW * Constants.ATTACK_INTERVAL_RANGED_MULTIPLIER)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_LIGHT)
			.build());
	}

	// Prevent any goal switching
	@Override
	public void reassessWeaponGoal() {
	}

	// Protect from sun
	@Override
	protected boolean isSunBurnTick() {
		return false;
	}

	// Prevent picking up items
	@Override
	public boolean canPickUpLoot() {
		return false;
	}

	// Prevent transforming
	@Override
	protected void doFreezeConversion() {
	}

	@Override
	public void startFreezeConversion(int i) {
	}
}