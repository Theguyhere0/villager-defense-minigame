package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import com.google.common.util.concurrent.AtomicDouble;
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

public abstract class Ammo extends VDWeapon {
    @NotNull
    public static ItemStack create(double difficulty) {
        int level = Math.max(getLevel(difficulty) - 2, 1);
        List<String> lores = new ArrayList<>();

        // Add space in lore from name
        lores.add("");

        // Set capacity
        int capacity = 6 + 2 * ((level - 1) / 3) + 2 * ((level + 1) / 3) + 2 * (Math.max(level - 42, 0) / 3);
        lores.add(CommunicationManager.format(CAPACITY,
                new ColoredMessage(ChatColor.GREEN, Integer.toString(capacity)).toString() +
                new ColoredMessage(ChatColor.WHITE, "/" + capacity)));

        // Set refill rate
        int refillBoost = level / 3;
        double refill = Math.max(8 - refillBoost * .5, 1);
        lores.add(CommunicationManager.format(REFILL, Double.toString(refill)));

        // Set price
        int price = (int) (100 + 50 * level * Math.pow(Math.E, (level - 1) / 50d));
        lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                price));

        // Set name and return
        return ItemManager.createItem(Material.NETHER_STAR, CommunicationManager.format(
                        new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.ammo), Integer.toString(level)),
                ItemManager.BUTTON_FLAGS, null, lores);
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
        return toCheck.getType() == Material.NETHER_STAR && lore.stream().anyMatch(line -> line.contains(
                CAPACITY.toString().replace("%s", "")));
    }

    public static void updateCapacity(ItemStack ammo, int delta) {
        // Check for ammo
        if (!matches(ammo))
            return;

        AtomicInteger maxCap = new AtomicInteger();
        AtomicInteger capacity = new AtomicInteger();
        AtomicInteger capIndex = new AtomicInteger();

        // Get data
        ItemMeta meta = Objects.requireNonNull(ammo.getItemMeta());
        List<String > lores = Objects.requireNonNull(meta.getLore());
        lores.forEach(lore -> {
            if (lore.contains(LanguageManager.messages.capacity
                    .replace("%s", ""))) {
                String[] cap = lore.substring(2 + LanguageManager.messages.capacity.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(ChatColor.WHITE.toString(), "")
                        .split("/");
                maxCap.set(Integer.parseInt(cap[1]));
                capacity.set(Integer.parseInt(cap[0]));
                capIndex.set(lores.indexOf(lore));
            }
        });

        // Set new lore
        capacity.addAndGet(delta);
        ChatColor color = capacity.get() >= .75 * maxCap.get() ? ChatColor.GREEN :
                (capacity.get() <= .25 * maxCap.get() ? ChatColor.RED : ChatColor.YELLOW);
        lores.set(capIndex.get(), CommunicationManager.format(CAPACITY,
                new ColoredMessage(color,
                        Integer.toString(capacity.get())).toString() +
                        new ColoredMessage(ChatColor.WHITE, "/" + maxCap.get())));
        meta.setLore(lores);
        ammo.setItemMeta(meta);
    }

    public static void updateRefill(ItemStack ammo, boolean fletcher) {
        // Check for ammo
        if (!matches(ammo))
            return;

        AtomicInteger maxCap = new AtomicInteger();
        AtomicInteger capacity = new AtomicInteger();
        AtomicInteger capIndex = new AtomicInteger();
        AtomicDouble refill = new AtomicDouble();
        AtomicDouble refillTimer = new AtomicDouble();
        AtomicInteger refillTimerIndex = new AtomicInteger();

        // Get data
        ItemMeta meta = Objects.requireNonNull(ammo.getItemMeta());
        List<String > lores = Objects.requireNonNull(meta.getLore());
        lores.forEach(lore -> {
            if (lore.contains(LanguageManager.messages.capacity
                    .replace("%s", ""))) {
                String[] cap = lore.substring(2 + LanguageManager.messages.capacity.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(ChatColor.WHITE.toString(), "")
                        .split("/");
                maxCap.set(Integer.parseInt(cap[1]));
                capacity.set(Integer.parseInt(cap[0]));
                capIndex.set(lores.indexOf(lore));
            }
            else if (lore.contains(LanguageManager.messages.nextRefill
                    .replace("%s", ""))) {
                refillTimer.set(Double.parseDouble(lore.substring(2 + LanguageManager.messages.nextRefill.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(LanguageManager.messages.seconds.replace("%s", ""), "")));
                refillTimerIndex.set(lores.indexOf(lore));
            }
            else if (lore.contains(LanguageManager.messages.refill
                    .replace("%s", ""))) {
                refill.set(Double.parseDouble(lore.substring(2 + LanguageManager.messages.refill.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(LanguageManager.messages.seconds.replace("%s", ""), "")));
            }
        });

        // Ignore if full
        if (maxCap.get() == capacity.get())
            return;

        // Set new refill if first cycle is over
        if (refillTimer.get() == 0)
            lores.add(CommunicationManager.format(NEXT_REFILL, Double.toString(refill.get() - .5)));

        // Update refill timer otherwise
        else {
            double updatedRefillTimer = refillTimer.get() - .5;
            // Timer finished
            if (updatedRefillTimer < .1) {
                capacity.addAndGet(maxCap.get() - 1 > capacity.get() && fletcher ? 2 : 1);
                ChatColor color = capacity.get() >= .75 * maxCap.get() ? ChatColor.GREEN :
                        (capacity.get() <= .25 * maxCap.get() ? ChatColor.RED : ChatColor.YELLOW);
                lores.set(capIndex.get(), CommunicationManager.format(CAPACITY,
                        new ColoredMessage(color,
                                Integer.toString(capacity.get())).toString() +
                                new ColoredMessage(ChatColor.WHITE, "/" + maxCap.get())));
                lores.remove(refillTimerIndex.get());
            }

            // Timer still going
            else lores.set(refillTimerIndex.get(),
                    CommunicationManager.format(NEXT_REFILL, Double.toString(refillTimer.get() - .5)));
        }

        // Perform updates
        meta.setLore(lores);
        ammo.setItemMeta(meta);
    }
}
