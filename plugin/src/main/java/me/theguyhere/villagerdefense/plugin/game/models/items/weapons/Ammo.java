package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import com.google.common.util.concurrent.AtomicDouble;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Ammo extends VDWeapon {
    @NotNull
    public static ItemStack create(AmmoType type) {
        List<String> lores = new ArrayList<>();

        // Set name
        String name;
        switch (type) {
            case T1:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t1.name),
                        "[T1]"
                );
                break;
            case T2:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t2.name),
                        "[T2]"
                );
                break;
            case T3:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t3.name),
                        "[T3]"
                );
                break;
            case T4:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t4.name),
                        "[T4]"
                );
                break;
            case T5:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t5.name),
                        "[T5]"
                );
                break;
            case T6:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t6.name),
                        "[T6]"
                );
                break;
            case T7:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t7.name),
                        "[T7]"
                );
                break;
            case T8:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t8.name),
                        "[T8]"
                );
                break;
            case T9:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t9.name),
                        "[T9]"
                );
                break;
            case T10:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.ammo.t10.name),
                        "[T10]"
                );
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (type) {
            case T1:
                description = LanguageManager.itemLore.ammo.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.ammo.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.ammo.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.ammo.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.ammo.t5.description;
                break;
            case T6:
                description = LanguageManager.itemLore.ammo.t6.description;
                break;
            case T7:
                description = LanguageManager.itemLore.ammo.t7.description;
                break;
            case T8:
                description = LanguageManager.itemLore.ammo.t8.description;
                break;
            case T9:
                description = LanguageManager.itemLore.ammo.t9.description;
                break;
            case T10:
                description = LanguageManager.itemLore.ammo.t10.description;
                break;
            default:
                description = "";
        }
        if (!description.isEmpty())
            lores.addAll(CommunicationManager.formatDescriptionList(
                    ChatColor.GRAY, description, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set capacity
        int capacity;
        switch (type) {
            case T1:
                capacity = 8;
                break;
            case T2:
                capacity = 15;
                break;
            case T3:
                capacity = 25;
                break;
            case T4:
            case T6:
                capacity = 40;
                break;
            case T5:
                capacity = 30;
                break;
            case T7:
                capacity = 50;
                break;
            case T8:
                capacity = 60;
                break;
            case T9:
                capacity = 75;
                break;
            case T10:
                capacity = 90;
                break;
            default:
                capacity = 0;
        }
        lores.add(CommunicationManager.format(CAPACITY,
                new ColoredMessage(ChatColor.GREEN, Integer.toString(capacity)).toString() +
                new ColoredMessage(ChatColor.WHITE, " / " + capacity)));

        // Set refill rate
        double refill;
        switch (type) {
            case T5:
                refill = 7.5;
                break;
            case T6:
                refill = 6;
                break;
            case T7:
                refill = 5;
                break;
            case T8:
                refill = 4;
                break;
            case T9:
                refill = 3;
                break;
            case T10:
                refill = 2.5;
                break;
            default:
                refill = 0;
        }
        if (refill > 0)
            lores.add(CommunicationManager.format(REFILL, Double.toString(refill)));

        // Set price
        int price;
        switch (type) {
            case T1:
                price = 75;
                break;
            case T2:
                price = 150;
                break;
            case T3:
                price = 275;
                break;
            case T4:
                price = 450;
                break;
            case T5:
                price = 500;
                break;
            case T6:
                price = 650;
                break;
            case T7:
                price = 850;
                break;
            case T8:
                price = 1100;
                break;
            case T9:
                price = 1350;
                break;
            case T10:
                price = 1600;
                break;
            default: price = -1;
        }
        if (price >= 0)
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));

        // Create item
        return ItemManager.createItem(Material.NETHER_STAR, name, ItemManager.BUTTON_FLAGS, null, lores);
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

    public static boolean updateCapacity(ItemStack ammo, int delta) {
        // Check for ammo
        if (!matches(ammo))
            return false;

        AtomicInteger maxCap = new AtomicInteger();
        AtomicInteger capacity = new AtomicInteger();
        AtomicInteger capIndex = new AtomicInteger();
        AtomicBoolean refill = new AtomicBoolean();

        // Get data
        ItemMeta meta = Objects.requireNonNull(ammo.getItemMeta());
        List<String > lores = Objects.requireNonNull(meta.getLore());
        lores.forEach(lore -> {
            if (lore.contains(LanguageManager.messages.capacity
                    .replace("%s", ""))) {
                String[] cap = lore.substring(2 + LanguageManager.messages.capacity.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(ChatColor.WHITE.toString(), "")
                        .split(" / ");
                maxCap.set(Integer.parseInt(cap[1]));
                capacity.set(Integer.parseInt(cap[0]));
                capIndex.set(lores.indexOf(lore));
            }
            else if (lore.contains(LanguageManager.messages.refill.replace("%s", "")))
                refill.set(true);
        });

        // Check if item needs to be removed
        capacity.addAndGet(delta);
        if (capacity.get() <= 0 && !refill.get())
            return true;

        // Set new lore
        ChatColor color = capacity.get() >= .75 * maxCap.get() ? ChatColor.GREEN :
                (capacity.get() <= .25 * maxCap.get() ? ChatColor.RED : ChatColor.YELLOW);
        lores.set(capIndex.get(), CommunicationManager.format(CAPACITY,
                new ColoredMessage(color,
                        Integer.toString(capacity.get())).toString() +
                        new ColoredMessage(ChatColor.WHITE, " / " + maxCap.get())));
        meta.setLore(lores);
        ammo.setItemMeta(meta);
        return false;
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
                        .split(" / ");
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

        // Ignore if full or doesn't refill
        if (maxCap.get() == capacity.get() || refill.get() == 0)
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
                                new ColoredMessage(ChatColor.WHITE, " / " + maxCap.get())));
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

    public enum AmmoType{
        T1,
        T2,
        T3,
        T4,
        T5,
        T6,
        T7,
        T8,
        T9,
        T10
    }
}
