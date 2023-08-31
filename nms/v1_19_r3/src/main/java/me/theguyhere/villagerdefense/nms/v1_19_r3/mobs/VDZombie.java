package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDZombieAttackGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;

public class VDZombie extends Zombie {
	public VDZombie(EntityType<? extends Zombie> type, Location location) {
		super(type, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
	}

	public VDZombie(Location location) {
		super(((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
		setBaby(false);
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new VDZombieAttackGoal(this, Constants.ATTACK_SPEED_MODERATE, 1));
		goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(4, new RandomLookAroundGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
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
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_MODERATE)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_SLOW)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_MODERATE)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_MEDIUM)
			.build());
	}

	// Override baby behavior
	@Override
	public void setBaby(boolean flag) {
		super.setBaby(flag);
		if (level != null && !level.isClientSide) {
			AttributeInstance attributeSpeed = getAttribute(Attributes.MOVEMENT_SPEED);
			assert attributeSpeed != null;
			attributeSpeed.removeModifier(new AttributeModifier(UUID.fromString("B9766B59-9566-4402-BC1F" +
				"-2EE2A276D836"), "Baby speed boost", 0.5, AttributeModifier.Operation.MULTIPLY_BASE
			));
		}
	}

	// Disable conversions
	@Override
	public void startUnderWaterConversion(int i) {
	}

	@Override
	protected void doUnderWaterConversion() {
	}

	@Override
	protected boolean convertsInWater() {
		return false;
	}

	@Override
	protected void convertToZombieType(EntityType<? extends Zombie> entitytypes) {
	}

	// Stop burning in the sun
	@Override
	protected boolean isSunSensitive() {
		return false;
	}

	@Override
	protected boolean isSunBurnTick() {
		return false;
	}

	// Stop reinforcement behavior
	@Override
	protected void randomizeReinforcementsChance() {
	}

	// Prevent picking up items
	@Override
	public boolean canPickUpLoot() {
		return false;
	}
}
