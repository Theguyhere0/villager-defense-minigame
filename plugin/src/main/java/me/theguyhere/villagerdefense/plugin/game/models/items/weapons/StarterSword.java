package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.game.models.items.ItemMetaKey;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class StarterSword extends VDWeapon {
    @NotNull
    public static ItemStack create() {
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

        // Add space in lore from name
        lores.add("");

        // Set attack type
        lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

        // Set main damage
        lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
                Integer.toString(35))));

        // Set crit damage
        lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
                Integer.toString(50))));

        // Set sweep damage
        lores.add(CommunicationManager.format(SWEEP_DAMAGE, new ColoredMessage(ChatColor.LIGHT_PURPLE,
                Integer.toString(10))));

        // Set attack speed
        attributes.put(Attribute.GENERIC_ATTACK_SPEED,
                new AttributeModifier(ItemMetaKey.ATTACK_SPEED.name(), -2.5,
                        AttributeModifier.Operation.ADD_NUMBER));
        lores.add(CommunicationManager.format(SPEED, Double.toString(1.5)));

        // Set dummy damage
        attributes.put(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(ItemMetaKey.DUMMY.name(), 0,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        // Set name, make unbreakable, and return
        return ItemManager.makeUnbreakable(ItemManager.createItem(Material.WOODEN_SWORD,
                new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.starterSword).toString(),
                ItemManager.BUTTON_FLAGS, null, lores, attributes));
    }

    public static boolean matches(ItemStack toCheck) {
        return create().equals(toCheck);
    }
}
