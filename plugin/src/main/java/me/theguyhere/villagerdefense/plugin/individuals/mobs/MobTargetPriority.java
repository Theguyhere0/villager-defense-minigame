package me.theguyhere.villagerdefense.plugin.individuals.mobs;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.function.Predicate;

public enum MobTargetPriority {
    NONE((e) -> true),
    PLAYERS((e) -> e.getType().equals(EntityType.PLAYER)),
    MELEE_PLAYERS((e) -> e.getType().equals(EntityType.PLAYER)),
    RANGED_PLAYERS((e) -> e.getType().equals(EntityType.PLAYER)),
    GOLEMS((e) -> e.getType().equals(EntityType.IRON_GOLEM) || e.getType().equals(EntityType.SNOWMAN)),
    VILLAGERS((e) -> e.getType().equals(EntityType.VILLAGER)),
    PETS((e) -> {
        EntityType type = e.getType();
        return type.equals(EntityType.WOLF) || type.equals(EntityType.HORSE) || type.equals(EntityType.BEE) ||
                type.equals(EntityType.CAT);
    }),
    PETS_GOLEMS(PETS.test.and(GOLEMS.test)),
    PETS_GOLEMS_PLAYERS(PETS_GOLEMS.test.and(PLAYERS.test));

    private final Predicate<Entity> test;

    MobTargetPriority(Predicate<Entity> test) {
        this.test = test;
    }

    public Predicate<Entity> getTest() {
        return test;
    }
}
