package me.theguyhere.villagerdefense.plugin.game.models.items.food;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopFood extends VDFood {
    @NotNull
    public static ItemStack create(Tier tier, ShopFoodType type) {
        List<String> lores = new ArrayList<>();

        // Set material
        Material mat;
        switch (type) {
            case CAPPLE:
                mat = Material.GOLDEN_APPLE;
                break;
            case GAPPLE:
                mat = Material.ENCHANTED_GOLDEN_APPLE;
                break;
            case TIERED:
                switch (tier) {
                    case T1:
                        mat = Material.BEETROOT;
                        break;
                    case T2:
                        mat = Material.CARROT;
                        break;
                    case T3:
                        mat = Material.BREAD;
                        break;
                    case T4:
                        mat = Material.MUTTON;
                        break;
                    case T5:
                        mat = Material.COOKED_BEEF;
                        break;
                    case T6:
                        mat = Material.GOLDEN_CARROT;
                        break;
                    default:
                        mat = Material.GUNPOWDER;
                }
                break;
            case TOTEM:
                mat = Material.TOTEM_OF_UNDYING;
                break;
            default:
                mat = Material.GUNPOWDER;
        }

        // Set name
        String name;
        switch (type) {
            case CAPPLE:
                name = formatName(LanguageManager.itemLore.shopFood.capple.name, tier);
                break;
            case GAPPLE:
                name = formatName(LanguageManager.itemLore.shopFood.gapple.name, tier);
                break;
            case TIERED:
                switch (tier) {
                    case T1:
                        name = formatName(LanguageManager.itemLore.shopFood.t1.name, tier);
                        break;
                    case T2:
                        name = formatName(LanguageManager.itemLore.shopFood.t2.name, tier);
                        break;
                    case T3:
                        name = formatName(LanguageManager.itemLore.shopFood.t3.name, tier);
                        break;
                    case T4:
                        name = formatName(LanguageManager.itemLore.shopFood.t4.name, tier);
                        break;
                    case T5:
                        name = formatName(LanguageManager.itemLore.shopFood.t5.name, tier);
                        break;
                    case T6:
                        name = formatName(LanguageManager.itemLore.shopFood.t6.name, tier);
                        break;
                    default:
                        name = "";
                }
                break;
            case TOTEM:
                name = formatName(LanguageManager.itemLore.shopFood.totem.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (type) {
            case CAPPLE:
                description = LanguageManager.itemLore.shopFood.capple.description;
                break;
            case GAPPLE:
                description = LanguageManager.itemLore.shopFood.gapple.description;
                break;
            case TIERED:
                switch (tier) {
                    case T1:
                        description = LanguageManager.itemLore.shopFood.t1.description;
                        break;
                    case T2:
                        description = LanguageManager.itemLore.shopFood.t2.description;
                        break;
                    case T3:
                        description = LanguageManager.itemLore.shopFood.t3.description;
                        break;
                    case T4:
                        description = LanguageManager.itemLore.shopFood.t4.description;
                        break;
                    case T5:
                        description = LanguageManager.itemLore.shopFood.t5.description;
                        break;
                    case T6:
                        description = LanguageManager.itemLore.shopFood.t6.description;
                        break;
                    default:
                        description = "";
                }
                break;
            case TOTEM:
                description = LanguageManager.itemLore.shopFood.totem.description;
                break;
            default:
                description = "";
        }
        if (!description.isEmpty())
            lores.addAll(CommunicationManager.formatDescriptionList(
                    ChatColor.GRAY, description, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set health heal
        int health;
        switch (type) {
            case CAPPLE:
                health = 125;
                break;
            case GAPPLE:
                health = 200;
                break;
            case TIERED:
                switch (tier) {
                    case T1:
                        health = 10;
                        break;
                    case T2:
                        health = 20;
                        break;
                    case T3:
                        health = 40;
                        break;
                    case T4:
                        health = 75;
                        break;
                    case T5:
                        health = 120;
                        break;
                    case T6:
                        health = 160;
                        break;
                    default:
                        health = 0;
                }
                break;
            default:
                health = 0;
        }
        if (health > 0)
            lores.add(new ColoredMessage(ChatColor.RED, "+" + health + " " + Utils.HP).toString());

        // Set absorption heal
        int absorption;
        switch (type) {
            case CAPPLE:
                absorption = 50;
                break;
            case GAPPLE:
                absorption = 80;
                break;
            case TOTEM:
                absorption = 400;
                break;
            default:
                absorption = 0;
        }
        if (absorption > 0)
            lores.add(new ColoredMessage(ChatColor.GOLD, "+" + absorption + " " + Utils.HP).toString());

        // Set hunger heal
        int hunger;
        switch (type) {
            case CAPPLE:
                hunger = 4;
                break;
            case GAPPLE:
                hunger = 7;
                break;
            case TIERED:
                switch (tier) {
                    case T1:
                        hunger = 1;
                        break;
                    case T2:
                        hunger = 2;
                        break;
                    case T3:
                        hunger = 3;
                        break;
                    case T4:
                        hunger = 5;
                        break;
                    case T5:
                        hunger = 6;
                        break;
                    case T6:
                        hunger = 8;
                        break;
                    default:
                        hunger = 0;
                }
                break;
            case TOTEM:
                hunger = 1;
                break;
            default:
                hunger = 0;
        }
        if (hunger > 0)
            lores.add(new ColoredMessage(ChatColor.BLUE, "+" + hunger + " " + Utils.HUNGER).toString());

        // Set price
        int price;
        switch (type) {
            case CAPPLE:
                price = 800;
                break;
            case GAPPLE:
                price = 1150;
                break;
            case TIERED:
                switch (tier) {
                    case T1:
                        price = 80;
                        break;
                    case T2:
                        price = 150;
                        break;
                    case T3:
                        price = 220;
                        break;
                    case T4:
                        price = 350;
                        break;
                    case T5:
                        price = 540;
                        break;
                    case T6:
                        price = 660;
                        break;
                    default: price = -1;
                }
                break;
            case TOTEM:
                price = 1550;
                break;
            default: price = -1;
        }
        if (price >= 0)
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));

        return ItemManager.createItem(mat, name, lores);
    }

    public static boolean matches(ItemStack toCheck) {
        if (toCheck == null)
            return false;
        ItemMeta meta = toCheck.getItemMeta();
        if (meta == null)
            return false;
        List<String> lore = meta.getLore();
        if (lore == null)
            return false;
        return lore.stream().anyMatch(line -> line.contains(Utils.HP) || line.contains(Utils.HUNGER));
    }

    public enum ShopFoodType{
        CAPPLE,
        GAPPLE,
        TIERED,
        TOTEM,
    }
}
