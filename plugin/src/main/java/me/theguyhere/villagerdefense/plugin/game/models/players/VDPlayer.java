package me.theguyhere.villagerdefense.plugin.game.models.players;

import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * A class holding data about players in a Villager Defense game.
 */
public class VDPlayer {
    /** UUID of corresponding {@link Player}.*/
    private final UUID player;
    /** Status of the this {@link VDPlayer}.*/
    private PlayerStatus status;
    /** The arena that this {@link VDPlayer} belongs in.*/
    private final Arena arena;
    /** Gem balance.*/
    private int gems;
    /** Kill count.*/
    private int kills;
    /** Wolf count.*/
    private int wolves;
    /** The wave at which the player joined the game as an active player.*/
    private int joinedWave;
    /** The number of times this player violated arena boundaries.*/
    private int infractions;
    /** The {@link Kit} the player will play with.*/
    private Kit kit;
    /** A possible second {@link Kit} the player can play with.*/
    private Kit kit2;
    /** The list of {@link Challenge}'s the player will take on.*/
    private List<Challenge> challenge = new ArrayList<>();
    /** The list of UUIDs of those that damaged the player.*/
    private final List<UUID> enemies = new ArrayList<>();
    /** Helmet {@link ItemStack} held for ninja ability.*/
    private ItemStack helmet;
    /** Chestplate {@link ItemStack} held for ninja ability.*/
    private ItemStack chestplate;
    /** Leggings {@link ItemStack} held for ninja ability.*/
    private ItemStack leggings;
    /** Boots {@link ItemStack} held for ninja ability.*/
    private ItemStack boots;
    /** Whether permanent boosts are on or not.*/
    private boolean boost = true;
    /** Number of gems to be converted from crystals.*/
    private int gemBoost = 0;
    /** Whether effect kits are shared or not.*/
    private boolean share = false;

    public VDPlayer(Player player, Arena arena, boolean spectating) {
        this.player = player.getUniqueId();
        this.arena = arena;
        if (spectating)
            status = PlayerStatus.SPECTATOR;
        else status = PlayerStatus.ALIVE;
        gems = 0;
        kills = 0;
        wolves = 0;
        joinedWave = 0;
        infractions = 0;
        kit = Kit.none();
    }

    public UUID getID() {
        return player;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public Arena getArena() {
        return arena;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public int getGems() {
        return gems;
    }

    public int getKills() {
        return kills;
    }

    public void addGems(int change) {
        gems += change;
    }

    /**
     * Checks whether the player can afford a shop item.
     * @param cost Item cost.
     * @return Boolean indicating whether the item was affordable.
     */
    public boolean canAfford(int cost) {
        return cost <= gems;
    }

    public void incrementKills() {
        kills++;
    }

    public Kit getKit() {
        return kit;
    }

    public Kit getKit2() {
        return kit2;
    }

    public List<Challenge> getChallenges() {
        return challenge;
    }

    public void addChallenge(Challenge toBeAdded) {
        if (!challenge.contains(toBeAdded))
            challenge.add(toBeAdded);
    }

    public void removeChallenge(Challenge toBeRemoved) {
        challenge.remove(toBeRemoved);
    }

    public void resetChallenges() {
        challenge = new ArrayList<>();
    }

    public List<UUID> getEnemies() {
        return enemies;
    }

    public void addEnemy(UUID toBeAdded) {
        if (!enemies.contains(toBeAdded))
            enemies.add(toBeAdded);
    }

    public boolean isBoosted() {
        return boost;
    }

    public void toggleBoost() {
        boost = !boost;
    }

    public int getGemBoost() {
        return gemBoost;
    }

    public void setGemBoost(int gemBoost) {
        this.gemBoost = gemBoost;
    }

    public boolean isSharing() {
        return share;
    }

    public void toggleShare() {
        share = !share;
    }

    public int getWolves() {
        return wolves;
    }

    public void incrementWolves() {
        wolves++;
    }

    public void decrementWolves() {
        wolves--;
    }

    public int getJoinedWave() {
        return joinedWave;
    }

    public void setJoinedWave(int joinedWave) {
        this.joinedWave = joinedWave;
    }

    public int incrementInfractions() {
        return ++infractions;
    }

    public void resetInfractions() {
        infractions = 0;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public void setKit2(Kit kit2) {
        this.kit2 = kit2;
    }

    /**
     * Removes armor from the player while they are invisible under the ninja ability.
     */
    public void hideArmor() {
        helmet = getPlayer().getInventory().getHelmet();
        getPlayer().getInventory().setHelmet(null);
        chestplate = getPlayer().getInventory().getChestplate();
        getPlayer().getInventory().setChestplate(null);
        leggings = getPlayer().getInventory().getLeggings();
        getPlayer().getInventory().setLeggings(null);
        boots = getPlayer().getInventory().getBoots();
        getPlayer().getInventory().setBoots(null);
    }

    /**
     * Returns armor to the player after the ninja ability wears out.
     */
    public void exposeArmor() {
        getPlayer().getInventory().setHelmet(helmet);
        getPlayer().getInventory().setChestplate(chestplate);
        getPlayer().getInventory().setLeggings(leggings);
        getPlayer().getInventory().setBoots(boots);
    }

    /**
     * Gives items on spawn or respawn based on kit selected
     */
    public void giveItems() {
        for (ItemStack item: getKit().getItems()) {
            EntityEquipment equipment = getPlayer().getEquipment();

            // Equip armor if possible, otherwise put in inventory, otherwise drop at feet
            if (Arrays.stream(GameItems.HELMET_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                    Objects.requireNonNull(equipment).getHelmet() == null)
                equipment.setHelmet(item);
            else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                    Objects.requireNonNull(equipment).getChestplate() == null)
                equipment.setChestplate(item);
            else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                    Objects.requireNonNull(equipment).getLeggings() == null)
                equipment.setLeggings(item);
            else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                    Objects.requireNonNull(equipment).getBoots() == null)
                equipment.setBoots(item);
            else PlayerManager.giveItem(getPlayer(), item, LanguageManager.errors.inventoryFull);
        }
        if (getKit2() != null)
            for (ItemStack item: getKit2().getItems()) {
                EntityEquipment equipment = getPlayer().getEquipment();

                // Equip armor if possible, otherwise put in inventory, otherwise drop at feet
                if (Arrays.stream(GameItems.HELMET_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                        Objects.requireNonNull(equipment).getHelmet() == null)
                    equipment.setHelmet(item);
                else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                        Objects.requireNonNull(equipment).getChestplate() == null)
                    equipment.setChestplate(item);
                else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                        Objects.requireNonNull(equipment).getLeggings() == null)
                    equipment.setLeggings(item);
                else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
                        Objects.requireNonNull(equipment).getBoots() == null)
                    equipment.setBoots(item);
                else PlayerManager.giveItem(getPlayer(), item, LanguageManager.errors.inventoryFull);
            }
        PlayerManager.giveItem(getPlayer(), GameItems.shop(), LanguageManager.errors.inventoryFull);
    }

    /**
     * Sets up attributes properly after dying or first spawning.
     */
    public void setupAttributes() {
        Random r = new Random();

        // Set health for people with giant kits
        if ((Kit.giant().setKitLevel(1).equals(getKit()) ||
                Kit.giant().setKitLevel(1).equals(getKit2())) && !isSharing())
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant1", 2,
                            AttributeModifier.Operation.ADD_NUMBER));
        else if ((Kit.giant().setKitLevel(2).equals(getKit()) ||
                Kit.giant().setKitLevel(2).equals(getKit2())) && !isSharing())
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant2", 4,
                            AttributeModifier.Operation.ADD_NUMBER));
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT2))) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant2", 4,
                            AttributeModifier.Operation.ADD_NUMBER));
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT1))) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant1", 2,
                            AttributeModifier.Operation.ADD_NUMBER));
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }

        // Set health for people with health boost and are boosted
        if (isBoosted() && PlayerManager.hasAchievement(getID(), Achievement.topWave9().getID()))
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("HealthBoost", 2,
                            AttributeModifier.Operation.ADD_NUMBER));

        // Set health for people with dwarf challenge
        if (getChallenges().contains(Challenge.dwarf()))
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Dwarf", -.5,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        // Make sure new health is set up correctly
        getPlayer().setHealth(
                Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .getValue());

        // Give blindness to people with that challenge
        if (getChallenges().contains(Challenge.blind()))
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 0));
    }
}
