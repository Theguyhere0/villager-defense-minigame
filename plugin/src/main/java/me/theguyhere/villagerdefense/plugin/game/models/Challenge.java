package me.theguyhere.villagerdefense.plugin.game.models;

import me.theguyhere.villagerdefense.plugin.tools.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A class representing challenge modes in Villager Defense. Comes with static methods for the following challenges:<br/>
 * <ul>
 *     <li>Amputee</li>
 *     <li>Clumsy</li>
 *     <li>Featherweight</li>
 *     <li>Pacifist</li>
 *     <li>Dwarf</li>
 *     <li>UHC</li>
 *     <li>Naked</li>
 *     <li>Blind</li>
 * </ul>
 */
public class Challenge {
    /** The name of the challenge.*/
    private final String name;
    /** The main description for the challenge.*/
    private final List<String> masterDescription = new ArrayList<>();
    /** The material used for GUI buttons relating to this challenge.*/
    private final Material buttonMaterial;
    /** The crystal bonus for accepting this challenge, in percentage points.*/
    private final int bonus;

    public Challenge(String name, Material buttonMaterial, int bonus) {
        this.name = name;
        this.buttonMaterial = buttonMaterial;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Double.compare(challenge.bonus, bonus) == 0 && Objects.equals(name, challenge.name) &&
                Objects.equals(masterDescription, challenge.masterDescription) &&
                buttonMaterial == challenge.buttonMaterial;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, masterDescription, buttonMaterial, bonus);
    }

    /**
     * Adds a line into the master description for the challenge.
     * @param line Line to add to the description.
     */
    public void addMasterDescription(String line) {
        masterDescription.add(line);
    }

    /**
     * Returns an {@link ItemStack} for a GUI button.
     *
     * @param active Whether the kit is active or not.
     * @return GUI button.
     */
    public ItemStack getButton(boolean active) {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        List<String> lores = new ArrayList<>(masterDescription);

        return ItemManager.createItem(buttonMaterial, CommunicationManager.format((active ? "&d&l" : "&5&l") + name), Utils.BUTTON_FLAGS,
                active ? enchants : null, lores);
    }

    /**
     * Attempts to return a {@link Challenge} based on the challenge's name.
     * @param challengeName Name to check.
     * @return Challenge or null.
     */
    public static Challenge getChallenge(String challengeName) {
        if (none().getName().equals(challengeName))
            return none();
        else if (amputee().getName().equals(challengeName))
            return amputee();
        else if (clumsy().getName().equals(challengeName))
            return clumsy();
        else if (featherweight().getName().equals(challengeName))
            return featherweight();
        else if (pacifist().getName().equals(challengeName))
            return pacifist();
        else if (dwarf().getName().equals(challengeName))
            return dwarf();
        else if (uhc().getName().equals(challengeName))
            return uhc();
        else if (naked().getName().equals(challengeName))
            return naked();
        else if (blind().getName().equals(challengeName))
            return blind();
        else return null;
    }

    public static Challenge none() {
        return new Challenge("None", Material.LIGHT_GRAY_CONCRETE, 1);
    }

    public static Challenge amputee() {
        int bonus = 10;

        Challenge challenge = new Challenge("Amputee", Material.BAMBOO, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7Where's my arm?"));
        challenge.addMasterDescription(CommunicationManager.format("&6No dual-wielding"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
    public static Challenge clumsy() {
        int bonus = 15;

        Challenge challenge = new Challenge("Clumsy", Material.ICE, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7I'm losing my marbles"));
        challenge.addMasterDescription(CommunicationManager.format("&6Held items have a 2% chance to drop upon use"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
    public static Challenge featherweight() {
        int bonus = 20;

        Challenge challenge = new Challenge("Featherweight", Material.FEATHER, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7WHEEEEEE"));
        challenge.addMasterDescription(CommunicationManager.format("&6Take x5 knockback"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
    public static Challenge pacifist() {
        int bonus = 25;

        Challenge challenge = new Challenge("Pacifist", Material.TURTLE_HELMET, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7Don't hurt me!"));
        challenge.addMasterDescription(CommunicationManager.format("&6Only hurt monsters after they hurt you"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
    public static Challenge dwarf() {
        int bonus = 40;

        Challenge challenge = new Challenge("Dwarf", Material.DEAD_BUSH, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7Short people unite!"));
        challenge.addMasterDescription(CommunicationManager.format("&6Max health is cut in half"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
    public static Challenge uhc() {
        int bonus = 50;

        Challenge challenge = new Challenge("UHC", Material.GOLDEN_APPLE, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7A true classic"));
        challenge.addMasterDescription(CommunicationManager.format("&6No natural healing"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
    public static Challenge naked() {
        int bonus = 75;

        Challenge challenge = new Challenge("Naked", Material.ARMOR_STAND, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7All natural"));
        challenge.addMasterDescription(CommunicationManager.format("&6No armor"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
    public static Challenge blind() {
        int bonus = 120;

        Challenge challenge = new Challenge("Blind", Material.INK_SAC, bonus);
        challenge.addMasterDescription(CommunicationManager.format("&7I hope you have good headphones"));
        challenge.addMasterDescription(CommunicationManager.format("&6Permanent blindness effect"));
        challenge.addMasterDescription(CommunicationManager.format("&a+" + bonus + "% crystal bonus"));

        return challenge;
    }
}
