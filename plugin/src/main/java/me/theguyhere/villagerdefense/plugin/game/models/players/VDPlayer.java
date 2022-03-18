package me.theguyhere.villagerdefense.plugin.game.models.players;

import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A class holding data about players in a Villager Defense game.
 */
public class VDPlayer {
    /** UUID of corresponding {@link Player}.*/
    private final UUID player;
    /** Status of the this {@link VDPlayer}.*/
    private PlayerStatus status;
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

    public VDPlayer(Player player, boolean spectating) {
        this.player = player.getUniqueId();
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
}
