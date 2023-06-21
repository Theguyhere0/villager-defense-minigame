package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public class VDMobPredicates {
	public static Predicate<LivingEntity> isVisible() {
		return ent -> ent.getActiveEffects()
			.stream()
			.noneMatch(effect -> effect.getEffect() == MobEffects.INVISIBILITY);
	}

	public static Predicate<LivingEntity> isRanged() {
		return ent -> ent.getMainHandItem()
			.getItem() == Items.BOW || ent
			.getMainHandItem()
			.getItem() == Items.CROSSBOW;
	}
}
