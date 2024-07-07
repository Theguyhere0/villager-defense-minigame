package me.theguyhere.villagerdefense.nms.v1_19_r3.mobs;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.function.Predicate;

public class VDMobPredicates {
	private static final Item[] MELEE_WEAPONS = {Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
		Items.DIAMOND_SWORD,
		Items.NETHERITE_SWORD,
		Items.GOLDEN_SWORD,
		Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
		Items.DIAMOND_AXE,
		Items.NETHERITE_AXE,
		Items.GOLDEN_AXE,
		Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE,
		Items.DIAMOND_HOE,
		Items.NETHERITE_HOE,
		Items.GOLDEN_HOE};

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

	public static Predicate<LivingEntity> isMelee() {
		return ent -> Arrays.stream(MELEE_WEAPONS).anyMatch(item -> ent.getMainHandItem().getItem() == item);
	}
}
