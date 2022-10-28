package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.Team;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;
import java.util.Random;

public abstract class VDMinion extends VDMob {
    protected VDMinion(Arena arena, Mob minion, String name, String lore, int level, AttackType attackType) {
        super(lore, level, attackType);
        mob = minion;
        id = minion.getUniqueId();
        minion.setMetadata(TEAM, Team.MONSTER.getValue());
        minion.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        hpBarSize = 2;
        minion.setRemoveWhenFarAway(false);
        minion.setCanPickupItems(false);
        if (minion.isInsideVehicle())
            Objects.requireNonNull(minion.getVehicle()).remove();
        for (Entity passenger : minion.getPassengers())
            passenger.remove();
        minion.setHealth(2);
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                .addModifier(new AttributeModifier(
                        "custom",
                        2 - Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                                .getBaseValue(),
                        AttributeModifier.Operation.ADD_NUMBER
                ));
        minion.setCustomNameVisible(true);
    }

    @Override
    protected void updateNameTag() {
        super.updateNameTag(ChatColor.RED);
    }

    protected void setArmorEquipment() {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        Random r = new Random();
        int armorLevel = Math.max(level + r.nextInt(4) - 2, 0);
        Material mat;

        // Helmet
        if (armorLevel < 5)
            mat = Material.AIR;
        else if (armorLevel < 10)
            mat = Material.LEATHER_HELMET;
        else if (armorLevel < 15)
            mat = Material.CHAINMAIL_HELMET;
        else if (armorLevel < 20)
            mat = Material.IRON_HELMET;
        else if (armorLevel < 30)
            mat = Material.DIAMOND_HELMET;
        else mat = Material.NETHERITE_HELMET;
        equipment.setHelmet(new ItemStack(mat));

        // Chestplate
        armorLevel = Math.max(level + r.nextInt(4) - 2, 0);
        if (armorLevel < 5)
            mat = Material.AIR;
        else if (armorLevel < 10)
            mat = Material.LEATHER_CHESTPLATE;
        else if (armorLevel < 15)
            mat = Material.CHAINMAIL_CHESTPLATE;
        else if (armorLevel < 20)
            mat = Material.IRON_CHESTPLATE;
        else if (armorLevel < 30)
            mat = Material.DIAMOND_CHESTPLATE;
        else mat = Material.NETHERITE_CHESTPLATE;
        equipment.setChestplate(new ItemStack(mat));

        // Leggings
        armorLevel = Math.max(level + r.nextInt(4) - 2, 0);
        if (armorLevel < 5)
            mat = Material.AIR;
        else if (armorLevel < 10)
            mat = Material.LEATHER_LEGGINGS;
        else if (armorLevel < 15)
            mat = Material.CHAINMAIL_LEGGINGS;
        else if (armorLevel < 20)
            mat = Material.IRON_LEGGINGS;
        else if (armorLevel < 30)
            mat = Material.DIAMOND_LEGGINGS;
        else mat = Material.NETHERITE_LEGGINGS;
        equipment.setLeggings(new ItemStack(mat));

        // Boots
        armorLevel = Math.max(level + r.nextInt(4) - 2, 0);
        if (armorLevel < 5)
            mat = Material.AIR;
        else if (armorLevel < 10)
            mat = Material.LEATHER_BOOTS;
        else if (armorLevel < 15)
            mat = Material.CHAINMAIL_BOOTS;
        else if (armorLevel < 20)
            mat = Material.IRON_BOOTS;
        else if (armorLevel < 30)
            mat = Material.DIAMOND_BOOTS;
        else mat = Material.NETHERITE_BOOTS;
        equipment.setBoots(new ItemStack(mat));
    }

    protected void setSword() {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        Random r = new Random();
        int swordLevel = Math.max(level + r.nextInt(4) - 2, 0);
        Material mat;

        if (swordLevel < 5)
            mat = Material.AIR;
        else if (swordLevel < 10)
            mat = Material.WOODEN_SWORD;
        else if (swordLevel < 15)
            mat = Material.STONE_SWORD;
        else if (swordLevel < 20)
            mat = Material.IRON_SWORD;
        else if (swordLevel < 30)
            mat = Material.DIAMOND_SWORD;
        else mat = Material.NETHERITE_SWORD;
        equipment.setItemInMainHand(new ItemStack(mat));
    }

    protected void setAxe() {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        Random r = new Random();
        int axeLevel = Math.max(level + r.nextInt(4) - 2, 0);
        Material mat;

        if (axeLevel < 5)
            mat = Material.AIR;
        else if (axeLevel < 10)
            mat = Material.WOODEN_AXE;
        else if (axeLevel < 15)
            mat = Material.STONE_AXE;
        else if (axeLevel < 20)
            mat = Material.IRON_AXE;
        else if (axeLevel < 30)
            mat = Material.DIAMOND_AXE;
        else mat = Material.NETHERITE_AXE;
        equipment.setItemInMainHand(new ItemStack(mat));
    }

    protected void setScythe() {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        Random r = new Random();
        int scytheLevel = Math.max(level + r.nextInt(4) - 2, 0);
        Material mat;

        if (scytheLevel < 5)
            mat = Material.AIR;
        else if (scytheLevel < 10)
            mat = Material.WOODEN_HOE;
        else if (scytheLevel < 15)
            mat = Material.STONE_HOE;
        else if (scytheLevel < 20)
            mat = Material.IRON_HOE;
        else if (scytheLevel < 30)
            mat = Material.DIAMOND_HOE;
        else mat = Material.NETHERITE_HOE;
        equipment.setItemInMainHand(new ItemStack(mat));
    }

    protected void setBow() {
        Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(new ItemStack(Material.BOW));
    }

    protected void setCrossbow() {
        Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(new ItemStack(Material.CROSSBOW));
    }

    protected void setTrident() {
        Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(new ItemStack(Material.TRIDENT));
    }
}
