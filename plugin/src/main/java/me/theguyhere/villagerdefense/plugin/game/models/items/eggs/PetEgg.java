package me.theguyhere.villagerdefense.plugin.game.models.items.eggs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.pets.VDDog;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PetEgg extends VDEgg {
    @NotNull
    public static ItemStack create(int level, PetEggType type) {
        List<String> lores = new ArrayList<>();

        // Set material
        Material mat;
        switch (type) {
            case DOG:
                mat = Material.WOLF_SPAWN_EGG;
                break;
            default:
                mat = Material.EGG;
        }

        // Set name
        String name;
        switch (type) {
            case DOG:
                name = CommunicationManager.format(new ColoredMessage(LanguageManager.messages.petName),
                        new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
                        new ColoredMessage(LanguageManager.mobs.dog));
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (type) {
            case DOG:
                description = LanguageManager.mobLore.dog;
                break;
            default:
                description = "";
        }
        if (!description.isEmpty())
            lores.addAll(CommunicationManager.formatDescriptionList(
                    ChatColor.GRAY, description, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set health
        int health;
        switch (type) {
            case DOG:
                health = VDDog.getHealth(level);
                break;
            default:
                health = 0;
        }
        if (health > 0)
            lores.add(new ColoredMessage(ChatColor.RED, Utils.HP + " " + health).toString());

        // Set armor
        int armor;
        switch (type) {
            case DOG:
                armor = VDDog.getArmor(level);
                break;
            default:
                armor = 0;
        }
        if (armor > 0)
            lores.add(new ColoredMessage(ChatColor.AQUA, Utils.ARMOR + " " + armor).toString());

        // Set toughness
        int toughness;
        switch (type) {
            case DOG:
                toughness = (int) (VDDog.getToughness(level) * 100);
                break;
            default:
                toughness = 0;
        }
        if (toughness > 0)
            lores.add(new ColoredMessage(ChatColor.DARK_AQUA, Utils.TOUGH + " " + toughness + "%").toString());

        // Set attack
        int[] attack = new int[2];
        switch (type) {
            case DOG:
                attack[0] = (int) (VDDog.getDamage(level) * .9);
                attack[1] = (int) (VDDog.getDamage(level) * 1.1);
                break;
        }
        if (attack[0] > 0)
            lores.add(new ColoredMessage(ChatColor.GREEN, Utils.DAMAGE + " " + attack[0] + "-" + attack[1])
                    .toString());

        // Set price
        int price;
        switch (type) {
            case DOG:
                switch (level) {
                    case 1:
                        price = 250;
                        break;
                    case 2:
                        price = 350;
                        break;
                    case 3:
                        price = 500;
                        break;
                    case 4:
                        price = 750;
                        break;
                    case 5:
                        price = 1000;
                        break;
                    default:
                        price = -1;
                }
                break;
            default: price = -1;
        }
        if (price >= 0) {
            lores.add("");
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));
        }

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

    public enum PetEggType {
        DOG,
    }
}
