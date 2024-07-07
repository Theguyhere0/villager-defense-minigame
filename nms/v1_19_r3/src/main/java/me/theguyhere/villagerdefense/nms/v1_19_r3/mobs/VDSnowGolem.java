package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDMoveBackToSpawnGoal;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDRangedAttackGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VDSnowGolem extends SnowGolem implements VDDweller {
	private final Vec3 home;
	private final int HOME_RADIUS = 7;

	public VDSnowGolem(Location location) {
		super(EntityType.SNOW_GOLEM, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
		home = new Vec3(location.getX(), location.getY(), location.getZ());
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new VDRangedAttackGoal<>(this, Constants.ATTACK_INTERVAL_SLOW, 1,
			(float) (Constants.TARGET_RANGE_MODERATE * Constants.CROSSBOW_ATTACK_RANGE_MULTIPLIER)));
		goalSelector.addGoal(3, new VDMoveBackToSpawnGoal<>(this));
		goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Ghast.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Phantom.class, true));
	}

	// Customized attributes
	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(Monster
			.createMonsterAttributes()
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_MODERATE)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_SLOW)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_LOW)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_HEAVY)
			.add(Attributes.ATTACK_SPEED, Constants.ATTACK_INTERVAL_SLOW)
			.build());
	}

	@Override
	public Vec3 getFlatRandomLocationInHome() {
		return home.add(random.nextInt(-HOME_RADIUS, HOME_RADIUS), 0, random.nextInt(-HOME_RADIUS, HOME_RADIUS));
	}

	@Override
	public boolean isHome() {
		return distanceToSqr(home) <= HOME_RADIUS * HOME_RADIUS;
	}

	// Make it immune to normal water damage
	@Override
	public boolean isSensitiveToWater() {
		return false;
	}
}
