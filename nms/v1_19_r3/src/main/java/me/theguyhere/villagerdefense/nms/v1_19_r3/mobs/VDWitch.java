package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.v1_19_r3.goals.VDRangedAttackGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class VDWitch extends Raider implements RangedAttackMob {
	public VDWitch(Location location) {
		super(EntityType.WITCH, ((CraftWorld) location.getWorld()).getHandle());
		getCommandSenderWorld().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		setPos(location.getX(), location.getY(), location.getZ());
	}

	// Customize goals
	@Override
	protected void registerGoals() {
		// Behavior
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new VDRangedAttackGoal<>(this, Constants.ATTACK_INTERVAL_VERY_SLOW, 1,
			(float) (Constants.TARGET_RANGE_MODERATE)));
		goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(4, new RandomLookAroundGoal(this));

		// Target priorities
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractGolem.class, true));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wolf.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cat.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Horse.class, true,
			VDMobPredicates.isVisible()));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Villager.class, true));
	}

	// Customized attributes
	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(Monster
			.createMonsterAttributes()
			.add(Attributes.FOLLOW_RANGE, Constants.TARGET_RANGE_MODERATE)
			.add(Attributes.MOVEMENT_SPEED, Constants.SPEED_SLOW)
			.add(Attributes.ATTACK_KNOCKBACK, Constants.KNOCKBACK_NONE)
			.add(Attributes.KNOCKBACK_RESISTANCE, Constants.WEIGHT_MEDIUM)
			.add(Attributes.ATTACK_SPEED, Constants.ATTACK_INTERVAL_VERY_SLOW)
			.build());
	}

	// Override ai
	public void aiStep() {
		if (!level.isClientSide && isAlive()) {
			if (random.nextFloat() < 7.5E-4F) {
				level.broadcastEntityEvent(this, (byte) 15);
			}
		}

		super.aiStep();
	}

	// Normal witch methods
	protected SoundEvent getAmbientSound() {
		return SoundEvents.WITCH_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damagesource) {
		return SoundEvents.WITCH_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WITCH_DEATH;
	}

	public SoundEvent getCelebrateSound() {
		return SoundEvents.WITCH_CELEBRATE;
	}

	public void handleEntityEvent(byte b0) {
		if (b0 == 15) {
			for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
				this.level.addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * 0.12999999523162842, this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.12999999523162842, this.getZ() + this.random.nextGaussian() * 0.12999999523162842, 0.0, 0.0, 0.0);
			}
		} else {
			super.handleEntityEvent(b0);
		}
	}

	protected float getStandingEyeHeight(Pose entitypose, EntityDimensions entitysize) {
		return 1.62F;
	}

	public void applyRaidBuffs(int i, boolean flag) {
	}

	public boolean canBeLeader() {
		return false;
	}

	// Prevent picking up items
	@Override
	public boolean canPickUpLoot() {
		return false;
	}

	// Prevent joining raids
	@Override
	public boolean canJoinRaid() {
		return false;
	}

	@Override
	public void setCanJoinRaid(boolean flag) {
	}

	// Prevent being raid leader
	@Override
	public boolean isPatrolLeader() {
		return false;
	}

	@Override
	public void setPatrolLeader(boolean var0) {
	}

	// Override range attack
	@Override
	public void performRangedAttack(LivingEntity target, float f) {
		Vec3 vec3d = target.getDeltaMovement();
		double d0 = target.getX() + vec3d.x - getX();
		double d1 = target.getEyeY() - 1.100000023841858 - getY();
		double d2 = target.getZ() + vec3d.z - getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);

		ThrownPotion potion = new ThrownPotion(level, this);
		potion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.MUNDANE));
		potion.setXRot(potion.getXRot() + 20.0F);
		potion.shoot(d0, d1 + d3 * 0.2, d2, 0.75F, 8.0F);
		if (!isSilent()) {
			level.playSound(
				null, getX(), getY(), getZ(), SoundEvents.WITCH_THROW, getSoundSource(), 1.0F,
				0.8F + random.nextFloat() * 0.4F
			);
		}

		level.addFreshEntity(potion);
	}
}
