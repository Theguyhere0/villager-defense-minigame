package me.theguyhere.villagerdefense.plugin.items;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.items.weapons.VDWeapon;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class VDItem {
    protected static final ColoredMessage DURABILITY = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.durability);

    // Gaussian level randomization for most ordinary stuff
    protected static int getLevel(double difficulty) {
        Random r = new Random();
        return Math.max((int) (Math.max(difficulty, 1.5) * (1 + .2 * Math.max(Math.min(r.nextGaussian(), 3), -3)) + .5),
                1); // Mean 100%, SD 50%, restrict 40% - 160%, min mean 3
    }

    public static boolean matches(ItemStack toCheck) {
        return VDAbility.matches(toCheck) || VDArmor.matches(toCheck) || VDFood.matches(toCheck) ||
                VDWeapon.matches(toCheck);
    }

    public static boolean updateDurability(ItemStack item) {
        return updateDurability(item, -1);
    }

    public static boolean updateDurability(ItemStack item, double damagePercent) {
        // Filter for weapons and armor
        if (!(VDWeapon.matches(item) || VDArmor.matches(item)))
            return false;

        // Get data
        AtomicInteger maxDur = new AtomicInteger();
        AtomicInteger durability = new AtomicInteger();
        AtomicInteger durIndex = new AtomicInteger();
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        List<String > lores = Objects.requireNonNull(meta.getLore());
        lores.forEach(lore -> {
            if (lore.contains(LanguageManager.messages.durability
                    .replace("%s", ""))) {
                String[] dur = lore.substring(2 + LanguageManager.messages.durability.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(ChatColor.WHITE.toString(), "")
                        .split(" / ");
                maxDur.set(Integer.parseInt(dur[1]));
                durability.set(Integer.parseInt(dur[0]));
                durIndex.set(lores.indexOf(lore));
            }
        });

        // Filter items without custom durability
        if (maxDur.get() == 0)
            return true;

        // Update and check for used up item
        if (damagePercent < 0)
            durability.addAndGet(-1);
        else durability.addAndGet((int) Math.round(damagePercent * maxDur.get()));
        Damageable damage = (Damageable) meta;
        if (durability.get() <= 0)
            return false;

        // Set new lore
        ChatColor color = durability.get() >= .75 * maxDur.get() ? ChatColor.GREEN :
                (durability.get() <= .25 * maxDur.get() ? ChatColor.RED : ChatColor.YELLOW);
        lores.set(durIndex.get(), CommunicationManager.format(DURABILITY,
                new ColoredMessage(color,
                        Integer.toString(durability.get())).toString() +
                        new ColoredMessage(ChatColor.WHITE, " / " + maxDur.get())));
        meta.setLore(lores);

        // Set damage indicator
        damage.setDamage((int) item.getType().getMaxDurability() - (int) (durability.get() * 1. / maxDur.get() *
                item.getType().getMaxDurability()));

        item.setItemMeta(meta);
        return true;
    }

    public static String formatName(ChatColor color, String name, Tier tier) {
        return CommunicationManager.format(
                new ColoredMessage(color, name),
                tier.getLabel()
        );
    }

    public static String formatName(String name, Tier tier) {
        return CommunicationManager.format(
                new ColoredMessage(name),
                tier.getLabel()
        );
    }

    public enum Tier {
        SET(new ColoredMessage(ChatColor.YELLOW, "[S]")),
        T0(new ColoredMessage(ChatColor.AQUA, "[T0]")),
        T1(new ColoredMessage(ChatColor.AQUA, "[T1]")),
        T2(new ColoredMessage(ChatColor.AQUA, "[T2]")),
        T3(new ColoredMessage(ChatColor.AQUA, "[T3]")),
        T4(new ColoredMessage(ChatColor.AQUA, "[T4]")),
        T5(new ColoredMessage(ChatColor.AQUA, "[T5]")),
        T6(new ColoredMessage(ChatColor.AQUA, "[T6]")),
        UNIQUE(new ColoredMessage(ChatColor.LIGHT_PURPLE, "[U]"));

        private final ColoredMessage label;

        Tier(ColoredMessage label) {
            this.label = label;
        }

        public ColoredMessage getLabel() {
            return label;
        }
    }

    public enum MetaKey {
        DAMAGE,
        PER_BLOCK,
        ORIGIN_LOCATION,
        ATTACK_SPEED,
        DUMMY
    }
}
