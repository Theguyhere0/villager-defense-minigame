package me.theguyhere.villagerdefense.plugin.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MageAbility extends VDAbility {
    private static final ColoredMessage ATTACK_TYPE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackType);
    private static final ColoredMessage ATTACK_TYPE_NORMAL = new ColoredMessage(ChatColor.GREEN,
            LanguageManager.names.normal);
    private static final ColoredMessage RANGE_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackRangeDamage);

    @NotNull
    public static ItemStack create(Tier tier) {
        // Set name
        String name;
        switch (tier) {
            case T0:
                name = formatName(LanguageManager.itemLore.essences.t0.name, LanguageManager.kits.mage.name, tier);
                break;
            case T1:
                name = formatName(LanguageManager.itemLore.essences.t1.name, LanguageManager.kits.mage.name, tier);
                break;
            case T2:
                name = formatName(LanguageManager.itemLore.essences.t2.name, LanguageManager.kits.mage.name, tier);
                break;
            case T3:
                name = formatName(LanguageManager.itemLore.essences.t3.name, LanguageManager.kits.mage.name, tier);
                break;
            case T4:
                name = formatName(LanguageManager.itemLore.essences.t4.name, LanguageManager.kits.mage.name, tier);
                break;
            case T5:
                name = formatName(LanguageManager.itemLore.essences.t5.name, LanguageManager.kits.mage.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        List<String> lores = new ArrayList<>(getDescription(tier));

        // Add space in lore from name, followed by instructions for usage
        lores.add("");
        lores.add(new ColoredMessage(LanguageManager.messages.rightClick).toString());

        // Set effect
        String effect;
        switch (tier) {
            case T0:
            case T1:
            case T2:
            case T3:
            case T4:
            case T5:
                effect = LanguageManager.kits.mage.effect;
                break;
            default:
                effect = null;
        }
        if (effect != null)
            lores.addAll(CommunicationManager.formatDescriptionList(ChatColor.LIGHT_PURPLE,
                    CommunicationManager.format(EFFECT, new ColoredMessage(ChatColor.LIGHT_PURPLE,
                            effect)), Utils.LORE_CHAR_LIMIT));

        // Set attack type
        lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

        // Set damage
        int damageLow, damageHigh;
        switch (tier) {
            case T0:
                damageLow = 30;
                damageHigh = 50;
                break;
            case T1:
                damageLow = 45;
                damageHigh = 75;
                break;
            case T2:
                damageLow = 60;
                damageHigh = 100;
                break;
            case T3:
                damageLow = 75;
                damageHigh = 120;
                break;
            case T4:
                damageLow = 85;
                damageHigh = 140;
                break;
            case T5:
                damageLow = 90;
                damageHigh = 150;
                break;
            default:
                damageLow = damageHigh = 0;
        }
        if (damageLow == damageHigh)
            lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                    Integer.toString(damageLow))));
        else lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                damageLow + "-" + damageHigh)));

        // Set cooldown
        double cooldown;
        switch (tier) {
            case T0:
                cooldown = 12;
                break;
            case T1:
                cooldown = 11.3;
                break;
            case T2:
                cooldown = 10.4;
                break;
            case T3:
                cooldown = 8.8;
                break;
            case T4:
                cooldown = 6.5;
                break;
            case T5:
                cooldown = 3;
                break;
            default:
                cooldown = 0;
        }
        if (cooldown > 0)
            lores.add(CommunicationManager.format(COOLDOWN, Double.toString(cooldown)));

        // Set price
        int price = getPrice(tier);
        if (price >= 0) {
            lores.add("");
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));
        }

        // Create item
        return ItemFactory.createItem(
                Material.PURPLE_DYE,
                name,
                ItemFactory.HIDE_ENCHANT_FLAGS,
                ItemFactory.glow(),
                lores
        );
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
        return toCheck.getType() == Material.PURPLE_DYE && lore.stream().anyMatch(line -> line.contains(
                EFFECT.toString().replace("%s", "")));
    }
}
