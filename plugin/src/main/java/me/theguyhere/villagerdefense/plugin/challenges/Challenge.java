package me.theguyhere.villagerdefense.plugin.challenges;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.managers.ItemManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
 *     <li>Explosive</li>
 *     <li>Naked</li>
 *     <li>Blind</li>
 * </ul>
 */
public class Challenge {
    /** The name of the challenge.*/
    private final String name;
    /** The ID of the challenge.*/
    private final String ID;
    /** The main description for the challenge.*/
    private final List<String> masterDescription = new ArrayList<>();
    /** The material used for GUI buttons relating to this challenge.*/
    private final Material buttonMaterial;
    /** The crystal bonus for accepting this challenge, in percentage points.*/
    private final int bonus;

    public Challenge(String name, String ID, Material buttonMaterial, int bonus) {
        this.name = name;
        this.ID = ID;
        this.buttonMaterial = buttonMaterial;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return bonus == challenge.bonus && Objects.equals(name, challenge.name) && Objects.equals(ID, challenge.ID) &&
                Objects.equals(masterDescription, challenge.masterDescription) &&
                buttonMaterial == challenge.buttonMaterial;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ID, masterDescription, buttonMaterial, bonus);
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
    @NotNull
    public ItemStack getButton(boolean active) {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        List<String> lores = new ArrayList<>(masterDescription);

        return ItemManager.createItem(buttonMaterial,
                CommunicationManager.format((active ? "&d&l" : "&5&l") + name), ItemManager.BUTTON_FLAGS,
                active ? enchants : null, lores);
    }

    /**
     * Attempts to return a {@link Challenge} based on the challenge's name.
     * @param challengeName Name to check.
     * @return Challenge or null.
     */
    public static Challenge getChallengeByName(String challengeName) {
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
        else if (explosive().getName().equals(challengeName))
            return explosive();
        else if (naked().getName().equals(challengeName))
            return naked();
        else if (blind().getName().equals(challengeName))
            return blind();
        else return null;
    }

    /**
     * Attempts to return a {@link Challenge} based on the challenge's ID.
     * @param challengeID ID to check.
     * @return Challenge or null.
     */
    public static Challenge getChallengeByID(String challengeID) {
        if (none().getID().equals(challengeID))
            return none();
        else if (amputee().getID().equals(challengeID))
            return amputee();
        else if (clumsy().getID().equals(challengeID))
            return clumsy();
        else if (featherweight().getID().equals(challengeID))
            return featherweight();
        else if (pacifist().getID().equals(challengeID))
            return pacifist();
        else if (dwarf().getID().equals(challengeID))
            return dwarf();
        else if (uhc().getID().equals(challengeID))
            return uhc();
        else if (explosive().getID().equals(challengeID))
            return explosive();
        else if (naked().getID().equals(challengeID))
            return naked();
        else if (blind().getID().equals(challengeID))
            return blind();
        else return null;
    }

    @NotNull
    public static Challenge none() {
        return new Challenge(LanguageManager.names.none, "none", Material.LIGHT_GRAY_CONCRETE, 1);
    }

    @NotNull
    public static Challenge amputee() {
        int bonus = 10;

        Challenge challenge = new Challenge(LanguageManager.challenges.amputee.name, "amputee", Material.BAMBOO,
                bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.amputee.description1).toString());
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GOLD,
                LanguageManager.challenges.amputee.description2).toString());
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge clumsy() {
        int bonus = 15;

        Challenge challenge = new Challenge(LanguageManager.challenges.clumsy.name, "clumsy", Material.ICE, bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.clumsy.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                String.format(LanguageManager.challenges.clumsy.description2, "2%")));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge featherweight() {
        int bonus = 20;

        Challenge challenge = new Challenge(LanguageManager.challenges.featherweight.name, "featherweight",
                Material.FEATHER, bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.featherweight.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                String.format(LanguageManager.challenges.featherweight.description2, "5")));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge pacifist() {
        int bonus = 25;

        Challenge challenge = new Challenge(LanguageManager.challenges.pacifist.name, "pacifist",
                Material.TURTLE_HELMET, bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.pacifist.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                LanguageManager.challenges.pacifist.description2));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge dwarf() {
        int bonus = 40;

        Challenge challenge = new Challenge(LanguageManager.challenges.dwarf.name, "dwarf", Material.DEAD_BUSH,
                bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.dwarf.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                LanguageManager.challenges.dwarf.description2));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge uhc() {
        int bonus = 50;

        Challenge challenge = new Challenge(LanguageManager.challenges.uhc.name, "uhc", Material.GOLDEN_APPLE,
                bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.uhc.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                LanguageManager.challenges.uhc.description2));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge explosive() {
        int bonus = 60;

        Challenge challenge = new Challenge(LanguageManager.challenges.explosive.name, "explosive", Material.TNT,
                bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.explosive.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                LanguageManager.challenges.explosive.description2));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge naked() {
        int bonus = 75;

        Challenge challenge = new Challenge(LanguageManager.challenges.naked.name, "naked", Material.ARMOR_STAND,
                bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.naked.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                LanguageManager.challenges.naked.description2));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
    @NotNull
    public static Challenge blind() {
        int bonus = 120;

        Challenge challenge = new Challenge(LanguageManager.challenges.blind.name, "blind", Material.INK_SAC,
                bonus);
        challenge.addMasterDescription(new ColoredMessage(ChatColor.GRAY,
                LanguageManager.challenges.blind.description1).toString());
        challenge.addMasterDescription(CommunicationManager.format("&6" +
                LanguageManager.challenges.blind.description2));
        challenge.addMasterDescription(CommunicationManager.format("&a" +
                String.format(LanguageManager.messages.crystalBonus, bonus + "%", LanguageManager.names.crystal)));

        return challenge;
    }
}
