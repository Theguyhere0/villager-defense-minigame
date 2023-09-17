package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDGhastAttackGoal;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDGhastLookGoal;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDRandomFloatAroundGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VDGhast extends Ghast {
	public VDGhast(Location location) {
		super(EntityType.GHAST, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new VDGhastAttackGoal(this, Constants.TARGET_RANGE_FAR,
			Constants.ATTACK_SPEED_VERY_SLOW
		));
		goalSelector.addGoal(2, new VDRandomFloatAroundGoal(this));
		goalSelector.addGoal(2, new VDGhastLookGoal(this, Constants.TARGET_RANGE_FAR));

		// Target priorities
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
			VDMobPredicates.isVisible()
		));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true, false));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Wolf.class, 10, true, false,
			VDMobPredicates.isVisible()
		));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Cat.class, 10, true, false,
			VDMobPredicates.isVisible()
		));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Horse.class, 10, true, false,
			VDMobPredicates.isVisible()
		));
	}

	// Customized attributes
	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(Monster
			.createMonsterAttributes()
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_FAR)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_MEDIUM)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_HIGH)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_HEAVY)
			.build());
	}
}
