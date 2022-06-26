package me.theguyhere.villagerdefense.plugin.game.models;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaSpawn;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaSpawnType;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class Mobs {
    private static void setMinion(Arena arena, LivingEntity livingEntity) {
        Team monsters = Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard()
                .getTeam("monsters"));

        monsters.addEntry(livingEntity.getUniqueId().toString());
        livingEntity.setCustomName(healthBar(1, 1, 5));
        livingEntity.setCustomNameVisible(true);
        livingEntity.setMetadata("VD", new FixedMetadataValue(Main.plugin, arena.getId()));
        livingEntity.setMetadata("game", new FixedMetadataValue(Main.plugin, arena.getGameID()));
        livingEntity.setMetadata("wave", new FixedMetadataValue(Main.plugin, arena.getCurrentWave()));
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCanPickupItems(false);
        if (livingEntity.isInsideVehicle())
            Objects.requireNonNull(livingEntity.getVehicle()).remove();
        for (Entity passenger : livingEntity.getPassengers())
            passenger.remove();

        // Set attribute modifiers
        double difficulty = arena.getCurrentDifficulty();
        for (int i = 0; i < 3; i++) {
            double boost;
            if (difficulty < 5)
                boost = 0;
            else boost = difficulty - 5;
            switch (i) {
                case 0:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                            .addModifier(new AttributeModifier(
                                "hpBoost", boost / 3, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 1:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
                            .addModifier(new AttributeModifier(
                                "attBoost", boost / 4, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 2:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                            .addModifier(new AttributeModifier(
                                "spdBoost", boost / 120, AttributeModifier.Operation.ADD_NUMBER
                            ));
            }
        }
    }

    private static void setBoss(Arena arena, LivingEntity livingEntity) {
        Team monsters = Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard()
                .getTeam("monsters"));

        monsters.addEntry(livingEntity.getUniqueId().toString());
        livingEntity.setMetadata("VD", new FixedMetadataValue(Main.plugin, arena.getId()));
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCanPickupItems(false);

        // Set attribute modifiers
        double difficulty = arena.getCurrentDifficulty();
        for (int i = 0; i < 3; i++) {
            double boost;
            if (difficulty < 10)
                boost = 0;
            else boost = difficulty - 10;
            switch (i) {
                case 0:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                            .addModifier(new AttributeModifier(
                                    "hpBoost", boost / 3, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 1:
                    if (livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null)
                        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
                                .addModifier(new AttributeModifier(
                                        "attBoost", boost / 4, AttributeModifier.Operation.ADD_NUMBER
                                ));
                    break;
                case 2:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                            .addModifier(new AttributeModifier(
                                    "spdBoost", boost / 120, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
            }
        }
    }

    private static void setLargeMinion(Arena arena, LivingEntity livingEntity) {
        livingEntity.setCustomName(healthBar(1, 1, 10));
        livingEntity.setCustomNameVisible(true);
        livingEntity.setMetadata("VD", new FixedMetadataValue(Main.plugin, arena.getId()));
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCanPickupItems(false);

        // Set attribute modifiers
        double difficulty = arena.getCurrentDifficulty();
        for (int i = 0; i < 3; i++) {
            double boost;
            if (difficulty < 8)
                boost = 0;
            else boost = difficulty - 8;
            switch (i) {
                case 0:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                            .addModifier(new AttributeModifier(
                                "hpBoost", boost / 3, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 1:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
                            .addModifier(new AttributeModifier(
                                "attBoost", boost / 4, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 2:
                    Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                            .addModifier(new AttributeModifier(
                                "spdBoost", boost / 120, AttributeModifier.Operation.ADD_NUMBER
                            ));
            }
        }
    }

    private static void setBaby(Arena arena, Ageable ageable) {
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        if (r.nextDouble() < .25 / (1 + Math.pow(Math.E, - (difficulty - 8) / 2)))
            ageable.setBaby();
        else ageable.setAdult();
    }

    private static void setSize(Arena arena, Slime slime) {
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        // Set size
        switch ((int) difficulty) {
            case 1:
            case 2:
                slime.setSize(1);
                break;
            case 3:
            case 4:
            case 5:
                if (r.nextDouble() < (difficulty - 3) / 3)
                    slime.setSize(2);
                else slime.setSize(1);
                break;
            case 6:
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 6) / 3)
                    slime.setSize(3);
                else slime.setSize(2);
                break;
            case 9:
            case 10:
            case 11:
                if (r.nextDouble() < (difficulty - 9) / 3)
                    slime.setSize(4);
                else slime.setSize(3);
                break;
            default:
                slime.setSize(4);
        }
    }

    private static void setSword(Arena arena, Monster monster) {
        EntityEquipment equipment = Objects.requireNonNull(monster.getEquipment());
        equipment.setItemInMainHand(getSword(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setAxe(Arena arena, Monster monster) {
        EntityEquipment equipment = Objects.requireNonNull(monster.getEquipment());
        equipment.setItemInMainHand(getAxe(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setBow(Arena arena, Monster monster) {
        EntityEquipment equipment = Objects.requireNonNull(monster.getEquipment());
        equipment.setItemInMainHand(getBow(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setCrossbow(Arena arena, Monster monster) {
        EntityEquipment equipment = Objects.requireNonNull(monster.getEquipment());
        equipment.setItemInMainHand(getCrossbow(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setTrident(Arena arena, Monster monster) {
        EntityEquipment equipment = Objects.requireNonNull(monster.getEquipment());
        equipment.setItemInMainHand(getTrident(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setArmor(Arena arena, Monster monster) {
        EntityEquipment equipment = Objects.requireNonNull(monster.getEquipment());
        equipment.setHelmet(getHelmet(arena), true);
        equipment.setHelmetDropChance(0);
        equipment.setChestplate(getChestplate(arena), true);
        equipment.setChestplateDropChance(0);
        equipment.setLeggings(getLeggings(arena), true);
        equipment.setLeggingsDropChance(0);
        equipment.setBoots(getBoots(arena), true);
        equipment.setBootsDropChance(0);
    }

    private static ItemStack getSword(Arena arena) {
        Random r = new Random();
        Material mat;
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set material
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                if (r.nextDouble() < (difficulty - 1) / 2)
                    mat = Material.WOODEN_SWORD;
                else return null;
                break;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    mat = Material.STONE_SWORD;
                else mat = Material.WOODEN_SWORD;
                break;
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    mat = Material.IRON_SWORD;
                else mat = Material.STONE_SWORD;
                break;
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    mat = Material.DIAMOND_SWORD;
                else mat = Material.IRON_SWORD;
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    mat = Material.NETHERITE_SWORD;
                else mat = Material.DIAMOND_SWORD;
                break;
            default:
                mat = Material.NETHERITE_SWORD;
        }

        // Set sharpness
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                break;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 1);
                break;
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 2);
                else enchants.put(Enchantment.DAMAGE_ALL, 1);
                break;
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 7) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 3);
                else enchants.put(Enchantment.DAMAGE_ALL, 2);
                break;
            case 9:
            case 10:
                if (r.nextDouble() < (difficulty - 9) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 4);
                else enchants.put(Enchantment.DAMAGE_ALL, 3);
                break;
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 11) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 5);
                else enchants.put(Enchantment.DAMAGE_ALL, 4);
                break;
            default:
               enchants.put(Enchantment.DAMAGE_ALL, 5);
        }

        // Set knockback
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 5) / 4)
                    enchants.put(Enchantment.KNOCKBACK, 1);
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 9) / 4)
                    enchants.put(Enchantment.KNOCKBACK, 2);
                else enchants.put(Enchantment.KNOCKBACK, 1);
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                if (r.nextDouble() < (difficulty - 13) / 5)
                    enchants.put(Enchantment.KNOCKBACK, 3);
                else enchants.put(Enchantment.KNOCKBACK, 2);
                break;
            default:
                enchants.put(Enchantment.KNOCKBACK, 3);
        }

        // Set fire aspect
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 6) / 4)
                    enchants.put(Enchantment.FIRE_ASPECT, 1);
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    enchants.put(Enchantment.FIRE_ASPECT, 2);
                else enchants.put(Enchantment.FIRE_ASPECT, 1);
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                if (r.nextDouble() < (difficulty - 15) / 5)
                    enchants.put(Enchantment.FIRE_ASPECT, 3);
                else enchants.put(Enchantment.FIRE_ASPECT, 2);
                break;
            default:
                enchants.put(Enchantment.FIRE_ASPECT, 3);
        }

        // Check if no enchants
        if (enchants.isEmpty())
            enchants = null;

        return ItemManager.createItem(mat, null, null, enchants);
    }

    private static ItemStack getAxe(Arena arena) {
        Random r = new Random();
        Material mat;
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set material
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                if (r.nextDouble() < (difficulty - 1) / 2)
                    mat = Material.WOODEN_AXE;
                else return null;
                break;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    mat = Material.STONE_AXE;
                else mat = Material.WOODEN_AXE;
                break;
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    mat = Material.IRON_AXE;
                else mat = Material.STONE_AXE;
                break;
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    mat = Material.DIAMOND_AXE;
                else mat = Material.IRON_AXE;
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    mat = Material.NETHERITE_AXE;
                else mat = Material.DIAMOND_AXE;
                break;
            default:
                mat = Material.NETHERITE_AXE;
        }

        // Set sharpness
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                break;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 1);
                break;
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 2);
                else enchants.put(Enchantment.DAMAGE_ALL, 1);
                break;
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 7) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 3);
                else enchants.put(Enchantment.DAMAGE_ALL, 2);
                break;
            case 9:
            case 10:
                if (r.nextDouble() < (difficulty - 9) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 4);
                else enchants.put(Enchantment.DAMAGE_ALL, 3);
                break;
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 11) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 5);
                else enchants.put(Enchantment.DAMAGE_ALL, 4);
                break;
            default:
                enchants.put(Enchantment.DAMAGE_ALL, 5);
        }

        // Set fire aspect
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 6) / 4)
                    enchants.put(Enchantment.FIRE_ASPECT, 1);
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    enchants.put(Enchantment.FIRE_ASPECT, 2);
                else enchants.put(Enchantment.FIRE_ASPECT, 1);
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                if (r.nextDouble() < (difficulty - 15) / 5)
                    enchants.put(Enchantment.FIRE_ASPECT, 3);
                else enchants.put(Enchantment.FIRE_ASPECT, 2);
                break;
            default:
                enchants.put(Enchantment.FIRE_ASPECT, 3);
        }

        // Check if no enchants
        if (enchants.isEmpty())
            enchants = null;

        return ItemManager.createItem(mat, null, null, enchants);
    }

    private static ItemStack getBow(Arena arena) {
        Random r = new Random();
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set power
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                break;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    enchants.put(Enchantment.ARROW_DAMAGE, 1);
                break;
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    enchants.put(Enchantment.ARROW_DAMAGE, 2);
                else enchants.put(Enchantment.ARROW_DAMAGE, 1);
                break;
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 7) / 2)
                    enchants.put(Enchantment.ARROW_DAMAGE, 3);
                else enchants.put(Enchantment.ARROW_DAMAGE, 2);
                break;
            case 9:
            case 10:
                if (r.nextDouble() < (difficulty - 9) / 2)
                    enchants.put(Enchantment.ARROW_DAMAGE, 4);
                else enchants.put(Enchantment.ARROW_DAMAGE, 3);
                break;
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 11) / 2)
                    enchants.put(Enchantment.ARROW_DAMAGE, 5);
                else enchants.put(Enchantment.ARROW_DAMAGE, 4);
                break;
            default:
                enchants.put(Enchantment.ARROW_DAMAGE, 5);
        }

        // Set punch
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 5) / 4)
                    enchants.put(Enchantment.ARROW_KNOCKBACK, 1);
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 9) / 4)
                    enchants.put(Enchantment.ARROW_KNOCKBACK, 2);
                else enchants.put(Enchantment.ARROW_KNOCKBACK, 1);
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                if (r.nextDouble() < (difficulty - 13) / 5)
                    enchants.put(Enchantment.ARROW_KNOCKBACK, 3);
                else enchants.put(Enchantment.ARROW_KNOCKBACK, 2);
                break;
            default:
                enchants.put(Enchantment.ARROW_KNOCKBACK, 3);
        }

        // Set flame
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                if (r.nextDouble() < (difficulty - 6) / 5)
                    enchants.put(Enchantment.ARROW_FIRE, 1);
                break;
            default:
                enchants.put(Enchantment.ARROW_FIRE, 1);
        }

        // Check if no enchants
        if (enchants.isEmpty())
            enchants = null;

        return ItemManager.createItem(Material.BOW, null, null, enchants);
    }

    private static ItemStack getCrossbow(Arena arena) {
        Random r = new Random();
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set piercing
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                break;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    enchants.put(Enchantment.PIERCING, 1);
                break;
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    enchants.put(Enchantment.PIERCING, 2);
                else enchants.put(Enchantment.PIERCING, 1);
                break;
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 7) / 2)
                    enchants.put(Enchantment.PIERCING, 3);
                else enchants.put(Enchantment.PIERCING, 2);
                break;
            case 9:
            case 10:
                if (r.nextDouble() < (difficulty - 9) / 2)
                    enchants.put(Enchantment.PIERCING, 4);
                else enchants.put(Enchantment.PIERCING, 3);
                break;
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 11) / 2)
                    enchants.put(Enchantment.PIERCING, 5);
                else enchants.put(Enchantment.PIERCING, 4);
                break;
            default:
                enchants.put(Enchantment.PIERCING, 5);
        }

        // Set quick charge
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 5) / 4)
                    enchants.put(Enchantment.QUICK_CHARGE, 1);
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 9) / 4)
                    enchants.put(Enchantment.QUICK_CHARGE, 2);
                else enchants.put(Enchantment.QUICK_CHARGE, 1);
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                if (r.nextDouble() < (difficulty - 13) / 5)
                    enchants.put(Enchantment.QUICK_CHARGE, 3);
                else enchants.put(Enchantment.QUICK_CHARGE, 2);
                break;
            default:
                enchants.put(Enchantment.QUICK_CHARGE, 3);
        }

        // Set multishot
        switch ((int) difficulty) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                if (r.nextDouble() < (difficulty - 6) / 5)
                    enchants.put(Enchantment.MULTISHOT, 1);
                break;
            default:
                enchants.put(Enchantment.MULTISHOT, 1);
        }

        // Check if no enchants
        if (enchants.isEmpty())
            enchants = null;

        return ItemManager.createItem(Material.CROSSBOW, null, null, enchants);
    }

    private static ItemStack getTrident(Arena arena) {
        Random r = new Random();
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set sharpness
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                break;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 1);
                break;
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 2);
                else enchants.put(Enchantment.DAMAGE_ALL, 1);
                break;
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 7) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 3);
                else enchants.put(Enchantment.DAMAGE_ALL, 2);
                break;
            case 9:
            case 10:
                if (r.nextDouble() < (difficulty - 9) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 4);
                else enchants.put(Enchantment.DAMAGE_ALL, 3);
                break;
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 11) / 2)
                    enchants.put(Enchantment.DAMAGE_ALL, 5);
                else enchants.put(Enchantment.DAMAGE_ALL, 4);
                break;
            default:
                enchants.put(Enchantment.DAMAGE_ALL, 5);
        }

        // Set knockback
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 5) / 4)
                    enchants.put(Enchantment.KNOCKBACK, 1);
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                if (r.nextDouble() < (difficulty - 9) / 4)
                    enchants.put(Enchantment.KNOCKBACK, 2);
                else enchants.put(Enchantment.KNOCKBACK, 1);
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                if (r.nextDouble() < (difficulty - 13) / 5)
                    enchants.put(Enchantment.KNOCKBACK, 3);
                else enchants.put(Enchantment.KNOCKBACK, 2);
                break;
            default:
                enchants.put(Enchantment.KNOCKBACK, 3);
        }

        // Set fire aspect
        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 6) / 4)
                    enchants.put(Enchantment.FIRE_ASPECT, 1);
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    enchants.put(Enchantment.FIRE_ASPECT, 2);
                else enchants.put(Enchantment.FIRE_ASPECT, 1);
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                if (r.nextDouble() < (difficulty - 15) / 5)
                    enchants.put(Enchantment.FIRE_ASPECT, 3);
                else enchants.put(Enchantment.FIRE_ASPECT, 2);
                break;
            default:
                enchants.put(Enchantment.FIRE_ASPECT, 3);
        }

        // Check if no enchants
        if (enchants.isEmpty())
            enchants = null;

        return ItemManager.createItem(Material.TRIDENT, null, null, enchants);
    }

    private static ItemStack getHelmet(Arena arena) {
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                if (r.nextDouble() < (difficulty - 1) / 2)
                    return new ItemStack(Material.LEATHER_HELMET);
                else return null;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    return new ItemStack(Material.CHAINMAIL_HELMET);
                else return new ItemStack(Material.LEATHER_HELMET);
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    return new ItemStack(Material.IRON_HELMET);
                else return new ItemStack(Material.CHAINMAIL_HELMET);
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    return new ItemStack(Material.DIAMOND_HELMET);
                else return new ItemStack(Material.IRON_HELMET);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    return new ItemStack(Material.NETHERITE_HELMET);
                else return new ItemStack(Material.DIAMOND_HELMET);
            default:
                return new ItemStack(Material.NETHERITE_HELMET);
        }
    }

    private static ItemStack getChestplate(Arena arena) {
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                if (r.nextDouble() < (difficulty - 1) / 2)
                    return new ItemStack(Material.LEATHER_CHESTPLATE);
                else return null;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                else return new ItemStack(Material.LEATHER_CHESTPLATE);
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    return new ItemStack(Material.IRON_CHESTPLATE);
                else return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    return new ItemStack(Material.DIAMOND_CHESTPLATE);
                else return new ItemStack(Material.IRON_CHESTPLATE);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    return new ItemStack(Material.NETHERITE_CHESTPLATE);
                else return new ItemStack(Material.DIAMOND_CHESTPLATE);
            default:
                return new ItemStack(Material.NETHERITE_CHESTPLATE);
        }
    }

    private static ItemStack getLeggings(Arena arena) {
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                if (r.nextDouble() < (difficulty - 1) / 2)
                    return new ItemStack(Material.LEATHER_LEGGINGS);
                else return null;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    return new ItemStack(Material.CHAINMAIL_LEGGINGS);
                else return new ItemStack(Material.LEATHER_LEGGINGS);
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    return new ItemStack(Material.IRON_LEGGINGS);
                else return new ItemStack(Material.CHAINMAIL_LEGGINGS);
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    return new ItemStack(Material.DIAMOND_LEGGINGS);
                else return new ItemStack(Material.IRON_LEGGINGS);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    return new ItemStack(Material.NETHERITE_LEGGINGS);
                else return new ItemStack(Material.DIAMOND_LEGGINGS);
            default:
                return new ItemStack(Material.NETHERITE_LEGGINGS);
        }
    }

    private static ItemStack getBoots(Arena arena) {
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        switch ((int) difficulty) {
            case 0:
            case 1:
            case 2:
                if (r.nextDouble() < (difficulty - 1) / 2)
                    return new ItemStack(Material.LEATHER_BOOTS);
                else return null;
            case 3:
            case 4:
                if (r.nextDouble() < (difficulty - 3) / 2)
                    return new ItemStack(Material.CHAINMAIL_BOOTS);
                else return new ItemStack(Material.LEATHER_BOOTS);
            case 5:
            case 6:
                if (r.nextDouble() < (difficulty - 5) / 2)
                    return new ItemStack(Material.IRON_BOOTS);
                else return new ItemStack(Material.CHAINMAIL_BOOTS);
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    return new ItemStack(Material.DIAMOND_BOOTS);
                else return new ItemStack(Material.IRON_BOOTS);
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    return new ItemStack(Material.NETHERITE_BOOTS);
                else return new ItemStack(Material.DIAMOND_BOOTS);
            default:
                return new ItemStack(Material.NETHERITE_BOOTS);
        }
    }

    public static void setVillager(Arena arena, Villager villager) {
        Team villagers = Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager())
                .getMainScoreboard().getTeam("villagers"));

        villagers.addEntry(villager.getUniqueId().toString());
        villager.setCustomName(healthBar(1, 1, 5));
        villager.setCustomNameVisible(true);
        villager.setMetadata("VD", new FixedMetadataValue(Main.plugin, arena.getId()));
    }

    public static void setZombie(Arena arena, Zombie zombie) {
        setMinion(arena, zombie);
        setSword(arena, zombie);
        setArmor(arena, zombie);
        setBaby(arena, zombie);
    }

    public static void setHusk(Arena arena, Husk husk) {
        setMinion(arena, husk);
        setSword(arena, husk);
        setArmor(arena, husk);
        setBaby(arena, husk);
    }

    public static void setWitherSkeleton(Arena arena, WitherSkeleton witherSkeleton) {
        setMinion(arena, witherSkeleton);
        setSword(arena, witherSkeleton);
        setArmor(arena, witherSkeleton);
    }

    public static void setBrute(Arena arena, PiglinBrute brute) {
        setMinion(arena, brute);
        setAxe(arena, brute);
        setArmor(arena, brute);
        setBaby(arena, brute);
        brute.setImmuneToZombification(true);
    }

    public static void setVindicator(Arena arena, Vindicator vindicator) {
        setMinion(arena, vindicator);
        setAxe(arena, vindicator);
        vindicator.setPatrolLeader(false);
        vindicator.setCanJoinRaid(false);
    }

    public static void setSpider(Arena arena, Spider spider) {
        setMinion(arena, spider);
    }

    public static void setCaveSpider(Arena arena, CaveSpider caveSpider) {
        setMinion(arena, caveSpider);
    }

    public static void setWitch(Arena arena, Witch witch) {
        setMinion(arena, witch);
    }

    public static void setSkeleton(Arena arena, Skeleton skeleton) {
        setMinion(arena, skeleton);
        setBow(arena, skeleton);
        setArmor(arena, skeleton);
    }

    public static void setStray(Arena arena, Stray stray) {
        setMinion(arena, stray);
        setBow(arena, stray);
        setArmor(arena, stray);
    }

    public static void setDrowned(Arena arena, Drowned drowned) {
        setMinion(arena, drowned);
        setTrident(arena, drowned);
        setArmor(arena, drowned);
        setBaby(arena, drowned);
    }

    public static void setBlaze(Arena arena, Blaze blaze) {
        setMinion(arena, blaze);
    }

    public static void setGhast(Arena arena, Ghast ghast) {
        setMinion(arena, ghast);
    }

    public static void setPillager(Arena arena, Pillager pillager) {
        setMinion(arena, pillager);
        setCrossbow(arena, pillager);
        pillager.setPatrolLeader(false);
        pillager.setCanJoinRaid(false);
    }

    public static void setSlime(Arena arena, Slime slime) {
        setMinion(arena, slime);
        setSize(arena, slime);
    }

    public static void setMagmaCube(Arena arena, MagmaCube magmaCube) {
        setMinion(arena, magmaCube);
        setSize(arena, magmaCube);
    }

    public static void setCreeper(Arena arena, Creeper creeper) {
        setMinion(arena, creeper);
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        // Set charged
        switch ((int) difficulty) {
            case 1:
            case 2:
            case 3:
            case 4:
                return;
            case 5:
            case 6:
            case 7:
            case 8:
                if (r.nextDouble() < (difficulty - 5) / 4)
                    creeper.setPowered(true);
                return;
            default:
                creeper.setPowered(true);
        }

    }

    public static void setPhantom(Arena arena, Phantom phantom) {
        setMinion(arena, phantom);
    }

    public static void setEvoker(Arena arena, Evoker evoker) {
        setMinion(arena, evoker);
        evoker.setCanJoinRaid(false);
        evoker.setPatrolLeader(false);
    }

    public static void setZoglin(Arena arena, Zoglin zoglin) {
        setMinion(arena, zoglin);
    }

    public static void setRavager(Arena arena, Ravager ravager) {
        setLargeMinion(arena, ravager);
    }

    public static void setWither(Arena arena, Wither wither) {
        setBoss(arena, wither);
    }

    public static void setWolf(Main plugin, Arena arena, VDPlayer vdPlayer, Wolf wolf) {
        wolf.setAdult();
        wolf.setOwner(vdPlayer.getPlayer());
        wolf.setBreed(false);
        wolf.setMetadata("VD", new FixedMetadataValue(plugin, arena.getId()));
        wolf.setCustomName(vdPlayer.getPlayer().getName() + "'s Wolf");
        wolf.setCustomNameVisible(true);
        vdPlayer.incrementWolves();

        // Set attribute modifiers
        double difficulty = arena.getCurrentDifficulty();
        for (int i = 0; i < 3; i++) {
            double boost;
            if (difficulty < 5)
                boost = 0;
            else boost = difficulty - 5;
            switch (i) {
                case 0:
                    Objects.requireNonNull(wolf.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                            .addModifier(new AttributeModifier(
                                "hpBoost", boost / 3, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 1:
                    Objects.requireNonNull(wolf.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
                            .addModifier(new AttributeModifier(
                                "attBoost", boost / 4, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 2:
                    Objects.requireNonNull(wolf.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                            .addModifier(new AttributeModifier(
                                "spdBoost", boost / 120, AttributeModifier.Operation.ADD_NUMBER
                            ));
            }
        }
    }

    public static void setGolem(Main plugin, Arena arena, IronGolem ironGolem) {
        ironGolem.setMetadata("VD", new FixedMetadataValue(plugin, arena.getId()));
        ironGolem.setCustomName(healthBar(1, 1, 10));
        ironGolem.setCustomNameVisible(true);
        arena.incrementGolems();

        // Set attribute modifiers
        double difficulty = arena.getCurrentDifficulty();
        for (int i = 0; i < 3; i++) {
            double boost;
            if (difficulty < 5)
                boost = 0;
            else boost = difficulty - 5;
            switch (i) {
                case 0:
                    Objects.requireNonNull(ironGolem.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                            .addModifier(new AttributeModifier(
                                "hpBoost", boost / 3, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 1:
                    Objects.requireNonNull(ironGolem.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
                            .addModifier(new AttributeModifier(
                                "attBoost", boost / 4, AttributeModifier.Operation.ADD_NUMBER
                            ));
                    break;
                case 2:
                    Objects.requireNonNull(ironGolem.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                            .addModifier(new AttributeModifier(
                                "spdBoost", boost / 120, AttributeModifier.Operation.ADD_NUMBER
                            ));
            }
        }
    }

    // Returns a formatted health bar
    public static String healthBar(double max, double remaining, int size) {
        String toFormat;
        double healthLeft = remaining / max;
        int healthBars = (int) (healthLeft * size + .99);
        if (healthBars < 0) healthBars = 0;

        if (healthLeft > .5)
            toFormat = "&a";
        else if (healthLeft > .25)
            toFormat = "&e";
        else toFormat = "&c";

        return CommunicationManager.format(toFormat +
                new String(new char[healthBars]).replace("\0", "\u2592") +
                new String(new char[size - healthBars]).replace("\0", "  "));
    }

    // Spawns villagers randomly
    public static void spawnVillagers(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager("spawnTables/" + arena.getPath() + ".yml");
        else data = new DataManager("spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave))
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";

        // Get count multiplier
        double countMultiplier = Math.log((arena.getActiveCount() + 7) / 10d) + 1;
        if (!arena.hasDynamicCount())
            countMultiplier = 1;

        int toSpawn = Math.max((int) (data.getConfig().getInt(wave + ".count.v") * countMultiplier), 1)
                - arena.getVillagers();
        List<Location> spawns = arena.getVillagerSpawns().stream().map(ArenaSpawn::getLocation)
                .collect(Collectors.toList());

        for (int i = 0; i < toSpawn; i++) {
            Location spawn = spawns.get(r.nextInt(spawns.size()));
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setVillager(arena,
                    (Villager) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.VILLAGER)
            ), delay);
            delay += r.nextInt(spawnDelay(i));

            // Manage spawning state
            if (i + 1 >= toSpawn)
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningVillagers(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningVillagers(true), delay);
        }
    }

    // Spawns monsters randomly
    public static void spawnMonsters(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager("spawnTables/" + arena.getPath() + ".yml");
        else data = new DataManager("spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave))
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";

        // Check for greater than 0 count
        if (data.getConfig().getInt(wave + ".count.m") == 0)
            return;

        // Calculate count multiplier
        double countMultiplier = Math.log((arena.getActiveCount() + 7) / 10d) + 1;
        if (!arena.hasDynamicCount())
            countMultiplier = 1;

        String path = wave + ".mtypes";
        List<String> typeRatio = new ArrayList<>();

        // Split spawns by type
        List<Location> grounds = new ArrayList<>();
        for (ArenaSpawn arenaSpawn : arena.getMonsterSpawns()) {
            if (arenaSpawn.getSpawnType() != ArenaSpawnType.MONSTER_AIR)
                grounds.add(arenaSpawn.getLocation());
        }

        List<Location> airs = new ArrayList<>();
        for (ArenaSpawn arenaSpawn : arena.getMonsterSpawns()) {
            if (arenaSpawn.getSpawnType() != ArenaSpawnType.MONSTER_GROUND)
                airs.add(arenaSpawn.getLocation());
        }

        // Default to all spawns if dedicated spawns are empty
        if (grounds.isEmpty())
            grounds = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation).collect(Collectors.toList());
        if (airs.isEmpty())
            airs = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation).collect(Collectors.toList());

        // Get monster type ratio
        Objects.requireNonNull(data.getConfig().getConfigurationSection(path)).getKeys(false)
                .forEach(type -> {
                    for (int i = 0; i < data.getConfig().getInt(path + "." + type); i++)
                        typeRatio.add(type);
                });

        // Spawn monsters
        for (int i = 0; i < Math.max((int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier), 1); i++) {
            // Get spawn locations
            Location ground = grounds.get(r.nextInt(grounds.size()));
            Location air = airs.get(r.nextInt(airs.size()));

            // Update delay
            delay += r.nextInt(spawnDelay(i));

            switch (typeRatio.get(r.nextInt(typeRatio.size()))) {
                case "zomb":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setZombie(arena,
                            (Zombie) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.ZOMBIE)
                    ), delay);
                    break;
                case "husk":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setHusk(arena,
                            (Husk) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.HUSK)
                    ), delay);
                    break;
                case "wskl":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setWitherSkeleton(arena,
                            (WitherSkeleton) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.WITHER_SKELETON)
                    ), delay);
                    break;
                case "brut":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setBrute(arena,
                            (PiglinBrute) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.PIGLIN_BRUTE)
                    ), delay);
                    break;
                case "vind":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setVindicator(arena,
                            (Vindicator) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.VINDICATOR)
                    ), delay);
                    break;
                case "spid":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setSpider(arena,
                            (Spider) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SPIDER)
                    ), delay);
                    break;
                case "cspd":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setCaveSpider(arena,
                            (CaveSpider) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.CAVE_SPIDER)
                    ), delay);
                    break;
                case "wtch":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setWitch(arena,
                            (Witch) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.WITCH)
                    ), delay);
                    break;
                case "skel":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setSkeleton(arena,
                            (Skeleton) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SKELETON)
                    ), delay);
                    break;
                case "stry":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setStray(arena,
                            (Stray) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.STRAY)
                    ), delay);
                    break;
                case "drwd":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setDrowned(arena,
                            (Drowned) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.DROWNED)
                    ), delay);
                    break;
                case "blze":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setBlaze(arena,
                            (Blaze) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.BLAZE)
                    ), delay);
                    break;
                case "ghst":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setGhast(arena,
                            (Ghast) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.GHAST)
                    ), delay);
                    break;
                case "pill":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setPillager(arena,
                            (Pillager) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.PILLAGER)
                    ), delay);
                    break;
                case "slim":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setSlime(arena,
                            (Slime) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SLIME)
                    ), delay);
                    break;
                case "mslm":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setMagmaCube(arena,
                            (MagmaCube) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.MAGMA_CUBE)
                    ), delay);
                    break;
                case "crpr":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setCreeper(arena,
                            (Creeper) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.CREEPER)
                    ), delay);
                    break;
                case "phtm":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setPhantom(arena,
                            (Phantom) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.PHANTOM)
                    ), delay);
                    break;
                case "evok":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setEvoker(arena,
                            (Evoker) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.EVOKER)
                    ), delay);
                    break;
                case "hgln":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setZoglin(arena,
                            (Zoglin) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.ZOGLIN)
                    ), delay);
                    break;
                case "rvgr":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setRavager(arena,
                            (Ravager) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.RAVAGER)
                    ), delay);
            }

            // Manage spawning state
            if (i + 1 >= (int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier))
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(true), delay);
        }
    }

    // Spawn bosses randomly
    public static void spawnBosses(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager("spawnTables/" + arena.getPath() + ".yml");
        else data = new DataManager("spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave))
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";

        // Check for greater than 0 count
        if (data.getConfig().getInt(wave + ".count.b") == 0)
            return;

        String path = wave + ".btypes";
        List<Location> spawns = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation)
                .collect(Collectors.toList());
        List<String> typeRatio = new ArrayList<>();

        // Get monster type ratio
        Objects.requireNonNull(data.getConfig().getConfigurationSection(path)).getKeys(false)
                .forEach(type -> {
                    for (int i = 0; i < data.getConfig().getInt(path + "." + type); i++)
                        typeRatio.add(type);
                });

        // Spawn bosses
        for (int i = 0; i < data.getConfig().getInt(wave + ".count.b"); i++) {
            Location spawn = spawns.get(r.nextInt(spawns.size()));

            // Update delay
            delay += r.nextInt(spawnDelay(i)) * 10;

            switch (typeRatio.get(r.nextInt(typeRatio.size()))) {
                case "w":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setWither(arena,
                            (Wither) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.WITHER)
                    ), delay);
                    break;
            }

            // Manage spawning state
            if (i + 1 >= data.getConfig().getInt(wave + ".count.b"))
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(true), delay);
        }
    }

    // Function for spawn delay
    private static int spawnDelay(int index) {
        int result = (int) (60 * Math.pow(Math.E, - index / 60D));
        return result == 0 ? 1 : result;
    }
}
