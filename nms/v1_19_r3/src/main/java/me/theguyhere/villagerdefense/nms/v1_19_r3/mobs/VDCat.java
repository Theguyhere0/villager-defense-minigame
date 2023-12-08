package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VDCat extends Cat {
	public VDCat(Location location) {
		super(EntityType.CAT, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
		setBaby(false);
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 6, 1, 1));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ghast.class, 6, 1, 1));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Phantom.class, 6, 1, 1));
		goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

	// Customized attributes
	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(Monster
			.createMonsterAttributes()
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_FAST)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_VERY_LIGHT)
			.build());
	}
}
