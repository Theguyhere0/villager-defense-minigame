package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Random;

public class Mobs {
    private static void setMinion(Main plugin, Arena arena, LivingEntity livingEntity) {
        livingEntity.setCustomName(Utils.healthBar(1, 1, 5));
        livingEntity.setCustomNameVisible(true);
        livingEntity.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCanPickupItems(false);
        arena.incrementEnemies();
        Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
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
        EntityEquipment equipment = monster.getEquipment();
        equipment.setItemInMainHand(getSword(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setAxe(Arena arena, Monster monster) {
        EntityEquipment equipment = monster.getEquipment();
        equipment.setItemInMainHand(getAxe(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setBow(Arena arena, Monster monster) {
        EntityEquipment equipment = monster.getEquipment();
        equipment.setItemInMainHand(getBow(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setCrossbow(Arena arena, Monster monster) {
        EntityEquipment equipment = monster.getEquipment();
        equipment.setItemInMainHand(getCrossbow(arena), true);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHand(null);
    }

    private static void setArmor(Arena arena, Monster monster) {
        EntityEquipment equipment = monster.getEquipment();
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
            case 1:
                return null;
            case 2:
                if (r.nextDouble() < difficulty - 2)
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
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    mat = Material.DIAMOND_SWORD;
                else mat = Material.IRON_SWORD;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    mat = Material.NETHERITE_SWORD;
                else mat = Material.DIAMOND_SWORD;
            default:
                mat = Material.NETHERITE_SWORD;
        }

        // Set sharpness
        switch ((int) difficulty) {
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

        return Utils.createItem(mat, null, null, enchants);
    }

    private static ItemStack getAxe(Arena arena) {
        Random r = new Random();
        Material mat;
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set material
        switch ((int) difficulty) {
            case 1:
                return null;
            case 2:
                if (r.nextDouble() < difficulty - 2)
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
            case 7:
            case 8:
            case 9:
                if (r.nextDouble() < (difficulty - 7) / 3)
                    mat = Material.DIAMOND_AXE;
                else mat = Material.IRON_AXE;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                if (r.nextDouble() < (difficulty - 10) / 5)
                    mat = Material.NETHERITE_AXE;
                else mat = Material.DIAMOND_AXE;
            default:
                mat = Material.NETHERITE_AXE;
        }

        // Set sharpness
        switch ((int) difficulty) {
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

        return Utils.createItem(mat, null, null, enchants);
    }

    private static ItemStack getBow(Arena arena) {
        Random r = new Random();
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set power
        switch ((int) difficulty) {
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

        return Utils.createItem(Material.BOW, null, null, enchants);
    }

    private static ItemStack getCrossbow(Arena arena) {
        Random r = new Random();
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        double difficulty = arena.getCurrentDifficulty();

        // Set piercing
        switch ((int) difficulty) {
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

        return Utils.createItem(Material.CROSSBOW, null, null, enchants);
    }

    private static ItemStack getHelmet(Arena arena) {
        Random r = new Random();
        double difficulty = arena.getCurrentDifficulty();

        switch ((int) difficulty) {
            case 1:
                return null;
            case 2:
                if (r.nextDouble() < difficulty - 2)
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
            case 1:
                return null;
            case 2:
                if (r.nextDouble() < difficulty - 2)
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
            case 1:
                return null;
            case 2:
                if (r.nextDouble() < difficulty - 2)
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
            case 1:
                return null;
            case 2:
                if (r.nextDouble() < difficulty - 2)
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

    public static void setVillager(Main plugin, Arena arena, Villager villager) {
        villager.setCustomName(Utils.healthBar(1, 1, 5));
        villager.setCustomNameVisible(true);
        villager.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
        arena.incrementVillagers();
        Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
    }

    public static void setZombie(Main plugin, Arena arena, Zombie zombie) {
        setMinion(plugin, arena, zombie);
        setSword(arena, zombie);
        setArmor(arena, zombie);
    }

    public static void setHusk(Main plugin, Arena arena, Husk husk) {
        setMinion(plugin, arena, husk);
        setSword(arena, husk);
        setArmor(arena, husk);
    }

    public static void setWitherSkeleton(Main plugin, Arena arena, WitherSkeleton witherSkeleton) {
        setMinion(plugin, arena, witherSkeleton);
        setSword(arena, witherSkeleton);
        setArmor(arena, witherSkeleton);
    }

    public static void setBrute(Main plugin, Arena arena, PiglinBrute brute) {
        setMinion(plugin, arena, brute);
        setAxe(arena, brute);
        setArmor(arena, brute);
    }

    public static void setVindicator(Main plugin, Arena arena, Vindicator vindicator) {
        setMinion(plugin, arena, vindicator);
        setAxe(arena, vindicator);
    }

    public static void setSpider(Main plugin, Arena arena, Spider spider) {
        setMinion(plugin, arena, spider);
    }

    public static void setCaveSpider(Main plugin, Arena arena, CaveSpider caveSpider) {
        setMinion(plugin, arena, caveSpider);
    }

    public static void setWitch(Main plugin, Arena arena, Witch witch) {
        setMinion(plugin, arena, witch);
    }

    public static void setSkeleton(Main plugin, Arena arena, Skeleton skeleton) {
        setMinion(plugin, arena, skeleton);
        setBow(arena, skeleton);
        setArmor(arena, skeleton);
    }

    public static void setStray(Main plugin, Arena arena, Stray stray) {
        setMinion(plugin, arena, stray);
        setBow(arena, stray);
        setArmor(arena, stray);
    }

    public static void setBlaze(Main plugin, Arena arena, Blaze blaze) {
        setMinion(plugin, arena, blaze);
    }

    public static void setGhast(Main plugin, Arena arena, Ghast ghast) {
        setMinion(plugin, arena, ghast);
    }

    public static void setPillager(Main plugin, Arena arena, Pillager pillager) {
        setMinion(plugin, arena, pillager);
        setCrossbow(arena, pillager);
        pillager.setPatrolLeader(false);
        pillager.setCanJoinRaid(false);
    }

    public static void setSlime(Main plugin, Arena arena, Slime slime) {
        setMinion(plugin, arena, slime);
        setSize(arena, slime);
    }

    public static void setMagmaCube(Main plugin, Arena arena, MagmaCube magmaCube) {
        setMinion(plugin, arena, magmaCube);
        setSize(arena, magmaCube);
    }

    public static void setCreeper(Main plugin, Arena arena, Creeper creeper) {
        setMinion(plugin, arena, creeper);
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

    public static void setPhantom(Main plugin, Arena arena, Phantom phantom) {
        setMinion(plugin, arena, phantom);
    }

    public static void setEvoker(Main plugin, Arena arena, Evoker evoker) {
        setMinion(plugin, arena, evoker);
    }

    public static void setHoglin(Main plugin, Arena arena, Hoglin hoglin) {
        setMinion(plugin, arena, hoglin);
    }

    public static void setRavager(Main plugin, Arena arena, Ravager ravager) {
        setMinion(plugin, arena, ravager);
    }

    public static void setWither(Main plugin, Arena arena, Wither wither) {
        setMinion(plugin, arena, wither);
    }
}
