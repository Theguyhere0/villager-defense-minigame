package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDMeleeAttackGoal;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDSwellGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
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
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;

public class VDChargedCreeper extends VDCreeper {
	public VDChargedCreeper(Location location) {
		super(location);
		setPowered(true);
		maxSwell = Calculator.secondsToTicks(Constants.ATTACK_SPEED_VERY_SLOW);
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new VDSwellGoal(this, 8));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6, 1, 1));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6, 1, 1));
		goalSelector.addGoal(4, new VDMeleeAttackGoal(this, true, Constants.ATTACK_SPEED_VERY_SLOW, 6));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Villager.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wolf.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Horse.class, true,
			VDMobPredicates.isVisible()));
	}

	// Customized attributes
	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(Monster
			.createMonsterAttributes()
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_UNBOUNDED)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_SLOW)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_VERY_HIGH)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_MEDIUM)
			.build());
	}

	// Stop transformation
	@Override
	public void thunderHit(ServerLevel worldserver, LightningBolt entitylightning) {
		super.thunderHit(worldserver, entitylightning);
		setPowered(true);
	}
}
