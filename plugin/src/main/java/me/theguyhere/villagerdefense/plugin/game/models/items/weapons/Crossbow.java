package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Crossbow extends VDWeapon {
    @NotNull
    public static ItemStack create(double difficulty) {
        int level = Math.max(getLevel(difficulty) * 2 - 3, 1);
        List<String> lores = new ArrayList<>();

        // Add space in lore from name
        lores.add("");

        // Set attack type
        lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

        // Set range damage
        int damageLow = 120 + 10 * ((level - 1) / 2);
        int damageHigh = 180 + 15 * (level / 2);
        lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                damageLow + "-" + damageHigh)));

        // Set pierce
        lores.add(CommunicationManager.format(PIERCE, new ColoredMessage(ChatColor.GOLD,
                Integer.toString(2))));

        // Set attack speed
        lores.add(CommunicationManager.format(SPEED, Double.toString(0.65)));

        // Set ammo cost
        lores.add(CommunicationManager.format(AMMO_COST, new ColoredMessage(ChatColor.RED, Integer.toString(2))));

        // Set price
        int price = (int) (200 + 60 * level * Math.pow(Math.E, (level - 1) / 50d));
        lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                price));

        // Set name, make unbreakable, and return
        return ItemManager.makeUnbreakable(ItemManager.createItem(Material.CROSSBOW, CommunicationManager.format(
                        new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.crossbow), Integer.toString(level)),
                ItemManager.BUTTON_FLAGS, null, lores));
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
        return toCheck.getType() == Material.CROSSBOW && lore.stream().anyMatch(line -> line.contains(
                RANGE_DAMAGE.toString().replace("%s", "")));
    }

    public static int getPierce(ItemStack crossbow) {
        AtomicInteger pierce = new AtomicInteger();
        Objects.requireNonNull(Objects.requireNonNull(crossbow.getItemMeta()).getLore()).forEach(lore -> {
            if (lore.contains(LanguageManager.messages.pierce.replace("%s", "")))
                pierce.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.pierce.length()).
                        replace(ChatColor.BLUE.toString(), "")));
        });
        return pierce.get();
    }
}
