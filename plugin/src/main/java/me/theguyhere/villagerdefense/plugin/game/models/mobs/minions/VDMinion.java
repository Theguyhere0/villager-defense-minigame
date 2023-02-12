package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.Team;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Objects;

/**
 * The monsters of Villager Defense.
 */
public abstract class VDMinion extends VDMob {
    protected VDMinion(Arena arena, Mob minion, String name, String lore, AttackType attackType) {
        super(lore, attackType);
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

    protected void setArmorEquipment(boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.DURABILITY, 3);

        // Helmet
        if (helmet) {
            ItemStack armor;
            switch (level) {
                case 2:
                    armor = new ItemStack(Material.LEATHER_HELMET);
                    break;
                case 3:
                    armor = new ItemStack(Material.CHAINMAIL_HELMET);
                    break;
                case 4:
                    armor = new ItemStack(Material.IRON_HELMET);
                    break;
                case 5:
                    armor = new ItemStack(Material.DIAMOND_HELMET);
                    break;
                case 6:
                    armor = new ItemStack(Material.NETHERITE_HELMET);
                    break;
                case 7:
                    armor = ItemManager.createItem(Material.NETHERITE_HELMET, "", null, enchant);
                    break;
                default:
                    armor = new ItemStack(Material.AIR);
            }
            equipment.setHelmet(armor);
        }

        // Chestplate
        if (chestplate) {
            ItemStack armor;
            switch (level) {
                case 2:
                    armor = new ItemStack(Material.LEATHER_CHESTPLATE);
                    break;
                case 3:
                    armor = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                    break;
                case 4:
                    armor = new ItemStack(Material.IRON_CHESTPLATE);
                    break;
                case 5:
                    armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    break;
                case 6:
                    armor = new ItemStack(Material.NETHERITE_CHESTPLATE);
                    break;
                case 7:
                    armor = ItemManager.createItem(Material.NETHERITE_CHESTPLATE, "", null, enchant);
                    break;
                default:
                    armor = new ItemStack(Material.AIR);
            }
            equipment.setChestplate(armor);
        }

        // Leggings
        if (leggings) {
            ItemStack armor;
            switch (level) {
                case 2:
                    armor = new ItemStack(Material.LEATHER_LEGGINGS);
                    break;
                case 3:
                    armor = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                    break;
                case 4:
                    armor = new ItemStack(Material.IRON_LEGGINGS);
                    break;
                case 5:
                    armor = new ItemStack(Material.DIAMOND_LEGGINGS);
                    break;
                case 6:
                    armor = new ItemStack(Material.NETHERITE_LEGGINGS);
                    break;
                case 7:
                    armor = ItemManager.createItem(Material.NETHERITE_LEGGINGS, "", null, enchant);
                    break;
                default:
                    armor = new ItemStack(Material.AIR);
            }
            equipment.setLeggings(armor);
        }

        // Boots
        if (boots) {
            ItemStack armor;
            switch (level) {
                case 2:
                    armor = new ItemStack(Material.LEATHER_BOOTS);
                    break;
                case 3:
                    armor = new ItemStack(Material.CHAINMAIL_BOOTS);
                    break;
                case 4:
                    armor = new ItemStack(Material.IRON_BOOTS);
                    break;
                case 5:
                    armor = new ItemStack(Material.DIAMOND_BOOTS);
                    break;
                case 6:
                    armor = new ItemStack(Material.NETHERITE_BOOTS);
                    break;
                case 7:
                    armor = ItemManager.createItem(Material.NETHERITE_BOOTS, "", null, enchant);
                    break;
                default:
                    armor = new ItemStack(Material.AIR);
            }
            equipment.setBoots(armor);
        }
    }

    protected void setSword() {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.DURABILITY, 3);
        ItemStack item;

        switch (level) {
            case 2:
                item = new ItemStack(Material.WOODEN_SWORD);
                break;
            case 3:
                item = new ItemStack(Material.STONE_SWORD);
                break;
            case 4:
                item = new ItemStack(Material.IRON_SWORD);
                break;
            case 5:
                item = new ItemStack(Material.DIAMOND_SWORD);
                break;
            case 6:
                item = new ItemStack(Material.NETHERITE_SWORD);
                break;
            case 7:
                item = ItemManager.createItem(Material.NETHERITE_SWORD, "", null, enchant);
                break;
            default:
                item = new ItemStack(Material.AIR);
        }
        equipment.setItemInMainHand(item);
    }

    protected void setAxe() {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.DURABILITY, 3);
        ItemStack item;

        switch (level) {
            case 2:
                item = new ItemStack(Material.WOODEN_AXE);
                break;
            case 3:
                item = new ItemStack(Material.STONE_AXE);
                break;
            case 4:
                item = new ItemStack(Material.IRON_AXE);
                break;
            case 5:
                item = new ItemStack(Material.DIAMOND_AXE);
                break;
            case 6:
                item = new ItemStack(Material.NETHERITE_AXE);
                break;
            case 7:
                item = ItemManager.createItem(Material.NETHERITE_AXE, "", null, enchant);
                break;
            default:
                item = new ItemStack(Material.AIR);
        }
        equipment.setItemInMainHand(item);
    }

    protected void setScythe() {
        EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.DURABILITY, 3);
        ItemStack item;

        switch (level) {
            case 2:
                item = new ItemStack(Material.WOODEN_HOE);
                break;
            case 3:
                item = new ItemStack(Material.STONE_HOE);
                break;
            case 4:
                item = new ItemStack(Material.IRON_HOE);
                break;
            case 5:
                item = new ItemStack(Material.DIAMOND_HOE);
                break;
            case 6:
                item = new ItemStack(Material.NETHERITE_HOE);
                break;
            case 7:
                item = ItemManager.createItem(Material.NETHERITE_HOE, "", null, enchant);
                break;
            default:
                item = new ItemStack(Material.AIR);
        }
        equipment.setItemInMainHand(item);
    }

    protected void setBow() {
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.DURABILITY, 3);

        if (level == 7)
            Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(
                    ItemManager.createItem(Material.BOW, "", null, enchant)
            );
        else Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(new ItemStack(Material.BOW));
    }

    protected void setCrossbow() {
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.DURABILITY, 3);

        if (level == 7)
            Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(
                    ItemManager.createItem(Material.CROSSBOW, "", null, enchant)
            );
        else Objects.requireNonNull(mob.getEquipment()).setItemInMainHand(new ItemStack(Material.CROSSBOW));
    }

    /**
     * Calculates the value a minion has given its health, armor, toughness, damage, and custom multiplier.
     * @param health Health of minion.
     * @param armor Armor of minion.
     * @param toughness Toughness of minion.
     * @param damage Base damage dealt by minion.
     * @param customMultiplier Custom multiplier to account for unique mob characteristics.
     * @return Value of the minion.
     */
    protected static int getValue(int health, int armor, double toughness, int damage, double customMultiplier) {
        return (int) ((health + 3 * armor) / 10d / (1 - toughness * .6) * Math.pow(damage, .75) / 13d
                * customMultiplier);
    }
}
