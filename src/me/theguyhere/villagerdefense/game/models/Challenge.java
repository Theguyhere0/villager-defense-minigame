package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.tools.Utils;
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
    /** The gem multiplier for accepting this challenge.*/
    private final double multiplier;

    public Challenge(String name, Material buttonMaterial, double multiplier) {
        this.name = name;
        this.buttonMaterial = buttonMaterial;
        this.multiplier = multiplier;
    }

    public String getName() {
        return name;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Double.compare(challenge.multiplier, multiplier) == 0 && Objects.equals(name, challenge.name) &&
                Objects.equals(masterDescription, challenge.masterDescription) &&
                buttonMaterial == challenge.buttonMaterial;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, masterDescription, buttonMaterial, multiplier);
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

        return Utils.createItem(buttonMaterial, Utils.format((active ? "&d&l" : "&5&l") + name), Utils.BUTTON_FLAGS,
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
        double multiplier = 1.1;

        Challenge challenge = new Challenge("Amputee", Material.BAMBOO, multiplier);
        challenge.addMasterDescription(Utils.format("&7Where's my arm?"));
        challenge.addMasterDescription(Utils.format("&6No dual-wielding"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
    public static Challenge clumsy() {
        double multiplier = 1.15;

        Challenge challenge = new Challenge("Clumsy", Material.ICE, multiplier);
        challenge.addMasterDescription(Utils.format("&7I'm losing my marbles"));
        challenge.addMasterDescription(Utils.format("&6Held items have a chance to drop upon use"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
    public static Challenge featherweight() {
        double multiplier = 1.2;

        Challenge challenge = new Challenge("Featherweight", Material.FEATHER, multiplier);
        challenge.addMasterDescription(Utils.format("&7WHEEEEEE"));
        challenge.addMasterDescription(Utils.format("&6Take increased knockback"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
    public static Challenge pacifist() {
        double multiplier = 1.25;

        Challenge challenge = new Challenge("Pacifist", Material.TURTLE_HELMET, multiplier);
        challenge.addMasterDescription(Utils.format("&7Don't hurt me!"));
        challenge.addMasterDescription(Utils.format("&6Only hurt monsters after they hurt you"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
    public static Challenge dwarf() {
        double multiplier = 1.4;

        Challenge challenge = new Challenge("Dwarf", Material.DEAD_BUSH, multiplier);
        challenge.addMasterDescription(Utils.format("&7Short people unite!"));
        challenge.addMasterDescription(Utils.format("&6Max health is cut in half"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
    public static Challenge uhc() {
        double multiplier = 1.5;

        Challenge challenge = new Challenge("UHC", Material.GOLDEN_APPLE, multiplier);
        challenge.addMasterDescription(Utils.format("&7A true classic"));
        challenge.addMasterDescription(Utils.format("&6No natural healing"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
    public static Challenge naked() {
        double multiplier = 1.75;

        Challenge challenge = new Challenge("Naked", Material.ARMOR_STAND, multiplier);
        challenge.addMasterDescription(Utils.format("&7All natural"));
        challenge.addMasterDescription(Utils.format("&6No armor"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
    public static Challenge blind() {
        double multiplier = 2.25;

        Challenge challenge = new Challenge("Blind", Material.INK_SAC, multiplier);
        challenge.addMasterDescription(Utils.format("&7I hope you have good headphones"));
        challenge.addMasterDescription(Utils.format("&6Permanent blindness effect"));
        challenge.addMasterDescription(Utils.format("&ax" + multiplier + " gem multiplier"));

        return challenge;
    }
}
