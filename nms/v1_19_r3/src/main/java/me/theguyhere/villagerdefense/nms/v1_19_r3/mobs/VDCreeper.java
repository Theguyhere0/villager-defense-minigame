package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDMeleeAttackGoal;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDSwellGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.Collection;

public class VDCreeper extends Creeper {
	public VDCreeper(Location location) {
		super(EntityType.CREEPER, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
		setPowered(false);
		maxSwell = Calculator.secondsToTicks(Constants.ATTACK_SPEED_SLOW);
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new VDSwellGoal(this));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6, 1, 1));
		goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6, 1, 1));
		goalSelector.addGoal(4, new VDMeleeAttackGoal(this, false,
			Calculator.secondsToTicks(Constants.ATTACK_SPEED_SLOW), 4));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
			VDMobPredicates.isVisible()));
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
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_MODERATE)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_SLOW)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_HIGH)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_LIGHT)
			.build());
	}

	// Customize explosion mechanics
	public void explodeCreeper() {
		if (!level.isClientSide) {
			float f = isPowered() ? 2.0F : 1.0F;
			ExplosionPrimeEvent event = new ExplosionPrimeEvent(getBukkitEntity(), (float) explosionRadius * f, false);
			level
				.getCraftServer()
				.getPluginManager()
				.callEvent(event);
			if (!event.isCancelled()) {
				level.explode(this, getX(), getY(), getZ(), event.getRadius(), event.getFire(),
					Level.ExplosionInteraction.MOB);
				spawnLingeringCloud();
				if (isPowered()) {
					dead = true;
					discard();
					return;
				}
			}
			swell = 0;
			setSwellDir(-1);
		}
	}

	private void spawnLingeringCloud() {
		Collection<MobEffectInstance> collection = getActiveEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloud entityareaeffectcloud = new AreaEffectCloud(level, getX(), getY(), getZ());
			entityareaeffectcloud.setOwner(this);
			entityareaeffectcloud.setRadius(2.5F);
			entityareaeffectcloud.setRadiusOnUse(-0.5F);
			entityareaeffectcloud.setWaitTime(10);
			entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
			entityareaeffectcloud.setRadiusPerTick(
				-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());

			for (MobEffectInstance mobeffect : collection) {
				entityareaeffectcloud.addEffect(new MobEffectInstance(mobeffect));
			}

			level.addFreshEntity(entityareaeffectcloud, CreatureSpawnEvent.SpawnReason.EXPLOSION);
		}
	}

	// Stop transformation
	@Override
	public void thunderHit(ServerLevel worldserver, LightningBolt entitylightning) {
		super.thunderHit(worldserver, entitylightning);
		setPowered(false);
	}
}
