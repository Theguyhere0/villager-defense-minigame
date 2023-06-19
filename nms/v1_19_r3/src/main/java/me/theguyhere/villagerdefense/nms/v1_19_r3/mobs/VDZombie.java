package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDZombieAttackGoal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;

public class VDZombie extends Zombie {
	public VDZombie(Location location) {
		super(((CraftWorld) location.getWorld()).getHandle());
		this.getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new VDZombieAttackGoal(this, 1.0, Calculator.secondsToTicks(0.7), 1));
		goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
		goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Villager.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wolf.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cat.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Horse.class, true));
	}

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
}
