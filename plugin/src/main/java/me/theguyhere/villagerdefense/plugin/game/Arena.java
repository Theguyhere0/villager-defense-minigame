package me.theguyhere.villagerdefense.plugin.game;

import lombok.Getter;
import lombok.Setter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.data.*;
import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import me.theguyhere.villagerdefense.plugin.structures.*;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.items.ItemManager;
import me.theguyhere.villagerdefense.plugin.game.kits.Kit;
import me.theguyhere.villagerdefense.plugin.visuals.InventoryID;
import me.theguyhere.villagerdefense.plugin.visuals.InventoryType;
import me.theguyhere.villagerdefense.plugin.visuals.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.visuals.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.game.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.structures.events.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.plugin.game.events.WaveEndEvent;
import me.theguyhere.villagerdefense.plugin.data.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.game.exceptions.InvalidNameException;
import me.theguyhere.villagerdefense.plugin.entities.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class managing data about a Villager Defense arena.
 */
public class Arena {
    /** Arena id.*/
    @Getter
    private final int id;
    /** The tasks object for the arena.*/
    @Getter
    private final Tasks task;

    /** Status of the arena.*/
    @Getter
    private ArenaStatus status;
    /** Whether the arena is in the process of spawning monsters.*/
    @Getter
    @Setter
    private boolean spawningMonsters;
    /** Whether the arena is in the process of spawning villagers.*/
    @Getter
    @Setter
    private boolean spawningVillagers;
    /** The ID of the game currently in progress.*/
    @Getter
    private int gameID;
    /** Current wave of the active game.*/
    @Getter
    private int currentWave;
    /** Villager count.*/
    @Getter
    private int villagers;
    /** Enemy count.*/
    @Getter
    private int enemies;
    /** Iron golem count.*/
    @Getter
    private int golems;
    /** ID of task managing player spawn particles.*/
    private int playerParticlesID = 0;
    /** ID of task managing monster spawn particles.*/
    private int monsterParticlesID = 0;
    /** ID of task managing villager spawn particles.*/
    private int villagerParticlesID = 0;
    /** ID of task managing corner particles.*/
    private int cornerParticlesID = 0;
    /** A list of {@link VDPlayer} in the arena.*/
    @Getter
    private final List<VDPlayer> players = new ArrayList<>();
    /** Weapon shop inventory.*/
    @Getter
    @Setter
    private Inventory weaponShop;
    /** Armor shop inventory.*/
    @Setter
    @Getter
    private Inventory armorShop;
    /** Consumables shop inventory.*/
    @Setter
    @Getter
    private Inventory consumeShop;
    /** Community chest inventory.*/
    @Setter
    @Getter
    private Inventory communityChest;
    /** Time limit bar object.*/
    @Getter
    private BossBar timeLimitBar;
    /** Portal object for the arena.*/
    @Getter
    private Portal portal;
    /** The player spawn for the arena.*/
    @Getter
    private ArenaSpawn playerSpawn;
    /** The monster spawns for the arena.*/
    @Getter
    private final List<ArenaSpawn> monsterSpawns = new ArrayList<>();
    /** The villager spawns for the arena.*/
    @Getter
    private final List<ArenaSpawn> villagerSpawns = new ArrayList<>();
    /** Arena scoreboard object for the arena.*/
    @Getter
    private ArenaBoard arenaBoard;

    public Arena(int arenaID) {
        id = arenaID;
        task = new Tasks(this);
        currentWave = 0;
        villagers = 0;
        enemies = 0;
        status = ArenaStatus.WAITING;
        refreshArenaBoard();
        refreshPlayerSpawn();
        refreshMonsterSpawns();
        refreshVillagerSpawns();
        refreshPortal();
        checkClosedParticles();
        checkClose();
    }

    /**
     * Retrieves the name of the arena from the arena file.
     * @return Arena name.
     */
    public String getName() {
        try {
            return ArenaDataManager.getArenaName(id);
        }
        catch (NoSuchPathException e) {
            CommunicationManager.debugErrorShouldNotHappen();
            return null;
        }
    }

    /**
     * Writes the new name of the arena into the arena file.
     * @param name New arena name.
     */
    public void setName(String name) throws InvalidNameException {
        // Check if name is not empty
        if (name == null || name.isEmpty()) {
            throw new InvalidNameException("Empty");
        }

        // Check if name is the same as current
        else if (name.equals(getName())) {
            throw new InvalidNameException("Same");
        }

        else {
            // Check for duplicate name
            try {
                GameManager.getArena(name);
                throw new InvalidNameException("Duplicate");
            }
            catch (ArenaNotFoundException ignored) {}

            // Save name
            ArenaDataManager.setArenaName(id, name);
        }

        // Refresh portal
        if (getPortalLocation() != null)
            refreshPortal();
    }

    /**
     * Retrieves the difficulty label of the arena from the arena file.
     * @return Arena difficulty label.
     */
    public String getDifficultyLabel() {
        return ArenaDataManager.getDifficultyLabel(id);
    }

    /**
     * Writes the new difficulty label of the arena into the arena file.
     * @param label New difficulty label.
     */
    public void setDifficultyLabel(String label) {
        ArenaDataManager.setDifficultyLabel(id, label);
        refreshPortal();
    }

    /**
     * Retrieves the maximum player count of the arena from the arena file. Defaults to 12.
     * @return Maximum player count.
     */
    public int getMaxPlayers() {
        try {
            return ArenaDataManager.getMaxPlayers(id);
        }
        catch (NoSuchPathException e) {
            setMaxPlayers(12);
            return 12;
        }
    }

    /**
     * Writes the new maximum player count of the arena into the arena file.
     * @param maxPlayers New maximum player count.
     */
    public void setMaxPlayers(int maxPlayers) {
        ArenaDataManager.setMaxPlayers(id, maxPlayers);
    }

    /**
     * Retrieves the minimum player count of the arena from the arena file. Defaults to 1.
     * @return Minimum player count.
     */
    public int getMinPlayers() {
        try {
            return ArenaDataManager.getMinPlayers(id);
        }
        catch (NoSuchPathException e) {
            setMinPlayers(1);
            return 1;
        }
    }

    /**
     * Writes the new minimum player count of the arena into the arena file.
     * @param minPlayers New minimum player count.
     */
    public void setMinPlayers(int minPlayers) {
        ArenaDataManager.setMinPlayers(id, minPlayers);
    }

    /**
     * Retrieves the wolf cap per player of the arena from the arena file. Defaults to 5.
     * @return Wolf cap per player.
     */
    public int getWolfCap() {
        try {
            return ArenaDataManager.getWolfCap(id);
        }
        catch (NoSuchPathException e) {
            setMaxPlayers(5);
            return 5;
        }
    }

    /**
     * Writes the new wolf cap per player of the arena into the arena file.
     * @param wolfCap New wolf cap per player.
     */
    public void setWolfCap(int wolfCap) {
        ArenaDataManager.setWolfCap(id, wolfCap);
    }

    /**
     * Retrieves the iron golem cap of the arena from the arena file. Defaults to 2.
     * @return Iron golem cap.
     */
    public int getGolemCap() {
        try {
            return ArenaDataManager.getGolemCap(id);
        }
        catch (NoSuchPathException e) {
            setGolemCap(2);
            return 2;
        }
    }

    /**
     * Writes the new iron golem cap of the arena into the arena file.
     * @param golemCap New iron golem cap.
     */
    public void setGolemCap(int golemCap) {
        ArenaDataManager.setGolemCap(id, golemCap);
    }

    /**
     * Retrieves the maximum waves of the arena from the arena file. Defaults to -1.
     * @return Maximum waves.
     */
    public int getMaxWaves() {
        try {
            return ArenaDataManager.getMaxWaves(id);
        }
        catch (NoSuchPathException e) {
            setMaxWaves(-1);
            return -1;
        }
    }

    /**
     * Writes the new maximum waves of the arena into the arena file.
     * @param maxWaves New maximum waves.
     */
    public void setMaxWaves(int maxWaves) {
        ArenaDataManager.setMaxWaves(id, maxWaves);
    }

    /**
     * Retrieves the nominal time limit per wave of the arena from the arena file. Defaults to -1.
     * @return Nominal time limit per wave.
     */
    public int getWaveTimeLimit() {
        try {
            return ArenaDataManager.getWaveTimeLimit(id);
        }
        catch (NoSuchPathException e) {
            setWaveTimeLimit(-1);
            return -1;
        }
    }

    /**
     * Writes the new nominal time limit per wave of the arena into the arena file.
     * @param timeLimit New nominal time limit per wave.
     */
    public void setWaveTimeLimit(int timeLimit) {
        ArenaDataManager.setWaveTimeLimit(id, timeLimit);
    }

    /**
     * Retrieves the difficulty multiplier of the arena from the arena file. Defaults to 1.
     * @return Difficulty multiplier.
     */
    public int getDifficultyMultiplier() {
        try {
            return ArenaDataManager.getDifficultyMultiplier(id);
        }
        catch (NoSuchPathException e) {
            setDifficultyMultiplier(1);
            return 1;
        }
    }

    /**
     * Writes the new difficulty multiplier of the arena into the arena file.
     * @param multiplier New difficulty multiplier.
     */
    public void setDifficultyMultiplier(int multiplier) {
        ArenaDataManager.setDifficultyMultiplier(id, multiplier);
    }

    /**
     * Retrieves the waiting music of the arena from the arena file.
     * @return Waiting {@link Sound}.
     */
    public Sound getWaitingSound() {
        switch (getWaitingSoundCode()) {
            case "blocks": return Sound.MUSIC_DISC_BLOCKS;
            case "cat": return Sound.MUSIC_DISC_CAT;
            case "chirp": return Sound.MUSIC_DISC_CHIRP;
            case "far": return Sound.MUSIC_DISC_FAR;
            case "mall": return Sound.MUSIC_DISC_MALL;
            case "mellohi": return Sound.MUSIC_DISC_MELLOHI;
            case "otherside":
                if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1))
                    return Sound.valueOf("MUSIC_DISC_OTHERSIDE");
                else return null;
            case "pigstep": return Sound.MUSIC_DISC_PIGSTEP;
            case "stal": return Sound.MUSIC_DISC_STAL;
            case "strad": return Sound.MUSIC_DISC_STRAD;
            case "wait": return Sound.MUSIC_DISC_WAIT;
            case "ward": return Sound.MUSIC_DISC_WARD;
            default: return null;
        }
    }

    /**
     * Create the button for a given waiting music of the arena from the arena file.
     * @return A button for GUIs.
     */
    public ItemStack getWaitingSoundButton(String name) {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        String sound = getWaitingSoundCode();
        boolean selected;

        switch (name) {
            case "blocks":
                selected = "blocks".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_BLOCKS,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Blocks"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "cat":
                selected = "cat".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_CAT,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Cat"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "chirp":
                selected = "chirp".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_CHIRP,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Chirp"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "far":
                selected = "far".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_FAR,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Far"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "mall":
                selected = "mall".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_MALL,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Mall"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "mellohi":
                selected = "mellohi".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_MELLOHI,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Mellohi"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "otherside":
                if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1)) {
                    selected = "otherside".equals(sound);
                    return ItemManager.createItem(Material.valueOf("MUSIC_DISC_OTHERSIDE"),
                            CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Otherside"),
                            ItemManager.BUTTON_FLAGS, selected ? enchants : null);
                } else {
                    selected = !GameManager.getValidSounds().contains(sound);
                    return ItemManager.createItem(Material.LIGHT_GRAY_CONCRETE,
                            CommunicationManager.format((selected ? "&a&l" : "&4&l") + "None"),
                            ItemManager.BUTTON_FLAGS, selected ? enchants : null);
                }
            case "pigstep":
                selected = "pigstep".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_PIGSTEP,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Pigstep"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "stal":
                selected = "stal".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_STAL,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Stal"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "strad":
                selected = "strad".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_STRAD,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Strad"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "wait":
                selected = "wait".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_WAIT,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Wait"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            case "ward":
                selected = "ward".equals(sound);
                return ItemManager.createItem(Material.MUSIC_DISC_WARD,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Ward"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
            default:
                selected = !GameManager.getValidSounds().contains(sound);
                return ItemManager.createItem(Material.LIGHT_GRAY_CONCRETE,
                        CommunicationManager.format((selected ? "&a&l" : "&4&l") + "None"),
                        ItemManager.BUTTON_FLAGS, selected ? enchants : null);
        }
    }

    /**
     * Retrieves the waiting music title of the arena from the arena file.
     * @return Waiting music title.
     */
    public String getWaitingSoundName() {
        String sound = getWaitingSoundCode();
        if (GameManager.getValidSounds().contains(sound)) {
            assert sound != null;
            return sound.substring(0, 1).toUpperCase() + sound.substring(1);
        }
        else return "None";
    }

    /**
     * Retrieves the waiting music code of the arena into the arena file.
     * @return Waiting music code.
     */
    public String getWaitingSoundCode() {
        try {
            return ArenaDataManager.getWaitingSound(id);
        }
        catch (NoSuchPathException e) {
            setWaitingSound("none");
            return "none";
        }
    }

    /**
     * Writes the new waiting music of the arena into the arena file.
     * @param sound Numerical representation of the new waiting music.
     */
    public void setWaitingSound(String sound) {
        ArenaDataManager.setWaitingSound(id, sound);
    }

    public Location getPortalLocation() {
        return ArenaDataManager.getArenaPortal(id);
    }

    /**
     * Creates a new portal at the given location and deletes the old portal.
     * @param location New location
     */
    public void setPortal(Location location) {
        // Save config location
        ArenaDataManager.setArenaPortal(id, location);

        // Recreate the portal
        refreshPortal();
    }

    /**
     * Recreates the portal in game based on the location in the arena file.
     */
    public void refreshPortal() {
        // Try recreating the portal
        try {
            // Delete old portal if needed
            if (portal != null)
                portal.remove();

            // Create a new portal and display it
            portal = new Portal(Objects.requireNonNull(getPortalLocation()), this);
            portal.displayForOnline();
        } catch (Exception e) {
            CommunicationManager.debugError(CommunicationManager.DebugLevel.NORMAL, String.format("Invalid location for %s's portal ", getName()),
                !Main.releaseMode, e);
            CommunicationManager.debugInfo(CommunicationManager.DebugLevel.NORMAL, "Portal location data may be corrupt. If data cannot be manually " +
                    "corrected in arenaData.yml, please delete the portal location data for " + getName() + "."
            );
        }
    }

    /**
     * Centers the portal location along the x and z axis.
     */
    public void centerPortal() {
        try {
            // Center the location
            ArenaDataManager.centerArenaPortal(id);

            // Recreate the portal
            refreshPortal();
        }
        catch (BadDataException | NoSuchPathException ignored) {}
    }

    /**
     * Removes the portal from the game and from the arena file.
     */
    public void removePortal() {
        if (portal != null) {
            portal.remove();
            portal = null;
        }
        ArenaDataManager.removeArenaPortal(id);
        checkClose();
    }

    public Location getArenaBoardLocation() {
        return ArenaDataManager.getArenaBoard(id);
    }
    
    /**
     * Creates a new arena leaderboard at the given location and deletes the old arena leaderboard.
     * @param location New location
     */
    public void setArenaBoard(Location location) {
        // Save config location
        ArenaDataManager.setArenaBoard(id, location);

        // Recreate the board
        refreshArenaBoard();
    }

    /**
     * Recreates the arena leaderboard in game based on the location in the arena file.
     */
    public void refreshArenaBoard() {
        // Try recreating the board
        try {
            // Delete old board if needed
            if (arenaBoard != null)
                arenaBoard.remove();

            // Create a new board and display it
            arenaBoard = new ArenaBoard(Objects.requireNonNull(getArenaBoardLocation()), this);
            arenaBoard.displayForOnline();
        } catch (Exception e) {
            CommunicationManager.debugError(
                CommunicationManager.DebugLevel.NORMAL, String.format("Invalid location for %s's arena board ", getName()),
                !Main.releaseMode,
                    e
            );
            CommunicationManager.debugInfo(CommunicationManager.DebugLevel.NORMAL, "Arena board location data may be corrupt. If data cannot be " +
                            "manually corrected in arenaData.yml, please delete the arena board location data for " +
                            getName() + ".");
        }
    }

    /**
     * Centers the arena leaderboard location along the x and z axis.
     */
    public void centerArenaBoard() {
        try {
            // Center the location
            ArenaDataManager.centerArenaBoard(id);

            // Recreate the board
            refreshArenaBoard();
        }
        catch (NoSuchPathException | BadDataException ignored) {}
    }

    /**
     * Removes the arena board from the game and from the arena file.
     */
    public void removeArenaBoard() {
        if (arenaBoard != null) {
            arenaBoard.remove();
            arenaBoard = null;
        }
        ArenaDataManager.removeArenaBoard(id);
    }

    /**
     * Refreshes the player spawn of the arena.
     */
    public void refreshPlayerSpawn() {
        // Prevent refreshing player spawn when arena is open
        if (!isClosed() && Main.plugin.isLoaded())
            return;

        // Remove particles
        cancelSpawnParticles();

        // Attempt to fetch new player spawn
        try {
            playerSpawn = new ArenaSpawn(
                    Objects.requireNonNull(ArenaDataManager.getPlayerSpawn(id)),
                    ArenaSpawnType.PLAYER,
                    0);
        } catch (InvalidLocationException | NullPointerException e) {
            playerSpawn = null;
        }

        // Turn on particles if appropriate
        if (isClosed())
            startSpawnParticles();
    }

    /**
     * Writes the new player spawn location of the arena into the arena file.
     * @param location New player spawn location.
     */
    public void setPlayerSpawn(Location location) {
        ArenaDataManager.setPlayerSpawn(id, location);
        refreshPlayerSpawn();
    }

    /**
     * Centers the player spawn location of the arena along the x and z axis.
     */
    public void centerPlayerSpawn() {
        try {
            // Center the location
            ArenaDataManager.centerPlayerSpawn(id);

            // Recreate the board
            refreshPlayerSpawn();
        }
        catch (NoSuchPathException | BadDataException ignored) {}
    }

    /**
     * Retrieves the waiting room location of the arena from the arena file.
     * @return Player spawn location.
     */
    public Location getWaitingRoom() {
        return ArenaDataManager.getWaitingRoom(id);
    }

    /**
     * Writes the new waiting room location of the arena into the arena file.
     * @param location New player spawn location.
     */
    public void setWaitingRoom(Location location) {
        ArenaDataManager.setWaitingRoom(id, location);
    }

    /**
     * Centers the waiting room location of the arena along the x and z axis.
     */
    public void centerWaitingRoom() {
        try {
            // Center the location
            ArenaDataManager.centerWaitingRoom(id);

            // Recreate the board
            refreshPlayerSpawn();
        }
        catch (NoSuchPathException | BadDataException ignored) {}
    }

    /**
     * Refreshes the monster spawns of the arena.
     */
    public void refreshMonsterSpawns() {
        // Prevent refreshing monster spawns when arena is open
        if (!isClosed() && Main.plugin.isLoaded())
            return;

        // Close off any particles if they are on
        cancelMonsterParticles();

        // Attempt to fetch new monster spawns
        monsterSpawns.clear();
        ArenaDataManager.getMonsterSpawns(id).forEach((id, location) -> {
            try {
                ArenaSpawnType spawnType;
                switch (getMonsterSpawnType(id)) {
                    case 1:
                        spawnType = ArenaSpawnType.MONSTER_GROUND;
                        break;
                    case 2:
                        spawnType = ArenaSpawnType.MONSTER_AIR;
                        break;
                    default:
                        spawnType = ArenaSpawnType.MONSTER_ALL;
                }
                monsterSpawns.add(new ArenaSpawn(Objects.requireNonNull(location), spawnType, id));
            } catch (InvalidLocationException | NullPointerException ignored) {}
        });

        // Turn on particles if appropriate
        if (isClosed())
            startMonsterParticles();
    }

    /**
     * Retrieves a specific monster spawn of the arena.
     * @param monsterSpawnID Monster spawn ID.
     * @return Monster spawn.
     */
    public ArenaSpawn getMonsterSpawn(int monsterSpawnID) {
        List<ArenaSpawn> query = monsterSpawns.stream().filter(spawn -> spawn.getId() == monsterSpawnID)
                .collect(Collectors.toList());

        if (query.size() != 1)
            return null;
        else return query.get(0);
    }

    public void setMonsterSpawn(int monsterSpawnID, Location location) {
        ArenaDataManager.setMonsterSpawn(id, monsterSpawnID, location);
        refreshMonsterSpawns();
    }

    public void centerMonsterSpawn(int monsterSpawnID) {
        try {
            ArenaDataManager.centerMonsterSpawn(id, monsterSpawnID);
            refreshMonsterSpawns();
        } catch (BadDataException | NoSuchPathException ignored) {}
    }

    public void setMonsterSpawnType(int monsterSpawnID, int type) {
        ArenaDataManager.setMonsterSpawnType(id, monsterSpawnID, type);
        refreshMonsterSpawns();
    }

    public int getMonsterSpawnType(int monsterSpawnID) {
        try {
            return ArenaDataManager.getMonsterSpawnType(id, monsterSpawnID);
        }
        catch (NoSuchPathException e) {
            return 0;
        }
    }

    /**
     * Generates a new ID for a new monster spawn.
     * @return New monster spawn ID
     */
    public int newMonsterSpawnID() {
        return Calculator.nextSmallestUniqueWhole(ArenaDataManager.getMonsterSpawns(id).keySet());
    }

    /**
     * Refreshes the villager spawns of the arena.
     */
    public void refreshVillagerSpawns() {
        // Prevent refreshing villager spawns when arena is open
        if (!isClosed() && Main.plugin.isLoaded())
            return;

        // Close off any particles if they are on
        cancelVillagerParticles();

        // Attempt to fetch new villager spawns
        villagerSpawns.clear();
        ArenaDataManager.getVillagerSpawns(id).forEach((id, location) -> {
            try {
                villagerSpawns.add(new ArenaSpawn(Objects.requireNonNull(location), ArenaSpawnType.VILLAGER, id));
            } catch (InvalidLocationException | NullPointerException ignored) {}
        });

        // Turn on particles if appropriate
        if (isClosed())
            startVillagerParticles();
    }

    /**
     * Retrieves a specific villager spawn of the arena.
     * @param villagerSpawnID Villager spawn ID.
     * @return Villager spawn.
     */
    public ArenaSpawn getVillagerSpawn(int villagerSpawnID) {
        List<ArenaSpawn> query = villagerSpawns.stream().filter(spawn -> spawn.getId() == villagerSpawnID)
                .collect(Collectors.toList());

        if (query.size() != 1)
            return null;
        else return query.get(0);
    }

    public void setVillagerSpawn(int villagerSpawnID, Location location) {
        ArenaDataManager.setVillagerSpawn(id, villagerSpawnID, location);
        refreshVillagerSpawns();
    }

    public void centerVillagerSpawn(int villagerSpawnID) {
        try {
            ArenaDataManager.centerVillagerSpawn(id, villagerSpawnID);
            refreshVillagerSpawns();
        }
        catch (BadDataException | NoSuchPathException ignored) {}
    }

    /**
     * Generates a new ID for a new villager spawn.
     * @return New villager spawn ID
     */
    public int newVillagerSpawnID() {
        return Calculator.nextSmallestUniqueWhole(ArenaDataManager.getVillagerSpawns(id).keySet());
    }

    public List<String> getBannedKits() {
        return ArenaDataManager.getBannedKits(id);
    }

    public void setBannedKits(List<String> bannedKits) {
        ArenaDataManager.setBannedKits(id, bannedKits);
    }

    public List<String> getForcedChallenges() {
        return ArenaDataManager.getForcedChallenges(id);
    }

    public void setForcedChallenges(List<String> forcedChallenges) {
        ArenaDataManager.setForcedChallenges(id, forcedChallenges);
    }

    public String getSpawnTableName() {
        try {
            return ArenaDataManager.getSpawnTableName(id);
        }
        catch (NoSuchPathException e) {
            setSpawnTableFile("default");
            return "default";
        }
    }

    public SpawnTableDataManager getSpawnTable() {
        if (getSpawnTableName().equals("custom")) {
            return new SpawnTableDataManager("arena." + id);
        }
        else {
            return new SpawnTableDataManager(getSpawnTableName());
        }
    }

    public boolean setSpawnTableFile(String option) {
        String file = option + ".yml";
        if (option.equals("custom")) {
            file = "arena." + id + ".yml";
        }

        if (new File(Main.plugin.getDataFolder().getPath(), "spawnTables/" + file).exists() ||
                option.equals("default")) {
            ArenaDataManager.setSpawnTableName(id, option);
            return true;
        }

        return false;
    }

    public boolean hasSpawnParticles() {
        try {
            return ArenaDataManager.hasSpawnParticles(id);
        }
        catch (NoSuchPathException e) {
            setSpawnParticles(true);
            return true;
        }
    }

    public void setSpawnParticles(boolean spawnParticles) {
        ArenaDataManager.setSpawnParticles(id, spawnParticles);
    }

    public void startSpawnParticles() {
        Particle spawnParticle = Particle.valueOf(NMSVersion.getCurrent().getNmsManager().getSpawnParticleName());
        
        if (getPlayerSpawn() == null)
            return;

        if (isClosed())
            getPlayerSpawn().turnOnIndicator();

        if (playerParticlesID != 0)
            return;

        playerParticlesID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            double var = 0;
            double var2 = 0;
            Location first, second;

            @Override
            public void run() {
                try {
                    // Update particle locations
                    var += Math.PI / 12;
                    var2 -= Math.PI / 12;
                    first = getPlayerSpawn().getLocation().clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                    second = getPlayerSpawn().getLocation().clone().add(Math.cos(var2 + Math.PI), Math.sin(var2) + 1,
                            Math.sin(var2 + Math.PI));

                    // Spawn particles
                    Objects.requireNonNull(getPlayerSpawn().getLocation().getWorld())
                            .spawnParticle(spawnParticle, first, 0);
                    getPlayerSpawn().getLocation().getWorld().spawnParticle(spawnParticle, second, 0);
                } catch (Exception e) {
                    CommunicationManager.debugError(
                        CommunicationManager.DebugLevel.VERBOSE, String.format("Player spawn particle generation error for %s.", getName())
                    );
                }
            }
        }, 0 , 2);
    }

    public void cancelSpawnParticles() {
        if (getPlayerSpawn() == null)
            return;

        getPlayerSpawn().turnOffIndicator();

        if (playerParticlesID != 0)
            Bukkit.getScheduler().cancelTask(playerParticlesID);
        playerParticlesID = 0;
    }

    public boolean hasMonsterParticles() {
        try {
            return ArenaDataManager.hasMonsterParticles(id);
        }
        catch (NoSuchPathException e) {
            setMonsterParticles(true);
            return true;
        }
    }

    public void setMonsterParticles(boolean monsterParticles) {
        ArenaDataManager.setMonsterParticles(id, monsterParticles);
    }

    public void startMonsterParticles() {
        Particle monsterParticle = Particle.valueOf(NMSVersion.getCurrent().getNmsManager().getMonsterParticleName());
        
        if (monsterParticlesID == 0 && !getMonsterSpawns().isEmpty())
            monsterParticlesID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                double var = 0;
                Location first, second;
                boolean init = false;

                @Override
                public void run() {
                    var -= Math.PI / 12;
                    getMonsterSpawns().forEach(spawn -> {
                        if (isClosed() && !init)
                            spawn.turnOnIndicator();

                        Location location = spawn.getLocation();
                        try {
                            // Update particle locations
                            first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                            second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                    Math.sin(var + Math.PI));

                            // Spawn particles
                            Objects.requireNonNull(location.getWorld())
                                    .spawnParticle(monsterParticle, first, 0);
                            location.getWorld().spawnParticle(monsterParticle, second, 0);
                        } catch (Exception e) {
                            CommunicationManager.debugError(
                                CommunicationManager.DebugLevel.VERBOSE, String.format("Monster particle generation error for %s.", getName())
                            );
                        }
                    });
                    init = true;
                }
            }, 0 , 2);
    }

    public void cancelMonsterParticles() {
        getMonsterSpawns().forEach(ArenaSpawn::turnOffIndicator);
        if (monsterParticlesID != 0)
            Bukkit.getScheduler().cancelTask(monsterParticlesID);
        monsterParticlesID = 0;
    }

    public boolean hasVillagerParticles() {
        try {
            return ArenaDataManager.hasVillagerParticles(id);
        }
        catch (NoSuchPathException e) {
            setVillagerParticles(true);
            return true;
        }
    }

    public void setVillagerParticles(boolean villagerParticles) {
        ArenaDataManager.setVillagerParticles(id, villagerParticles);
    }

    public void startVillagerParticles() {
        Particle villagerParticle = Particle.valueOf(NMSVersion.getCurrent().getNmsManager().getVillagerParticleName());
        
        if (villagerParticlesID == 0 && !getVillagerSpawns().isEmpty())
            villagerParticlesID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                double var = 0;
                Location first, second;
                boolean init = false;

                @Override
                public void run() {
                    var += Math.PI / 12;
                    getVillagerSpawns().forEach(spawn -> {
                        if (isClosed() && !init)
                            spawn.turnOnIndicator();

                        Location location = spawn.getLocation();
                        try {
                            // Update particle locations
                            first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                            second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                    Math.sin(var + Math.PI));

                            // Spawn particles
                            Objects.requireNonNull(location.getWorld())
                                    .spawnParticle(villagerParticle, first, 0);
                            location.getWorld().spawnParticle(villagerParticle, second, 0);
                        } catch (Exception e) {
                            CommunicationManager.debugError(
                                CommunicationManager.DebugLevel.VERBOSE, String.format("Villager particle generation error for %s.", getName())
                            );
                        }
                    });
                    init = true;
                }
            }, 0 , 2);
    }

    public void cancelVillagerParticles() {
        getVillagerSpawns().forEach(ArenaSpawn::turnOffIndicator);
        if (villagerParticlesID != 0)
            Bukkit.getScheduler().cancelTask(villagerParticlesID);
        villagerParticlesID = 0;
    }

    public boolean hasBorderParticles() {
        try {
            return ArenaDataManager.hasBorderParticles(id);
        }
        catch (NoSuchPathException e) {
            setBorderParticles(false);
            return false;
        }
    }

    public void setBorderParticles(boolean bool) {
        ArenaDataManager.setBorderParticles(id, bool);
    }

    public void startBorderParticles() {
        Particle borderParticle = Particle.valueOf(NMSVersion.getCurrent().getNmsManager().getBorderParticleName());
        Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 2);

        if (cornerParticlesID == 0 && getCorner1() != null && getCorner2() != null)
            cornerParticlesID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                World world;
                Location first, second;

                @Override
                public void run() {
                    // Spawn particles
                    try {
                        world = getCorner1().getWorld();
                        assert world != null;

                        first = new Location(world, Math.max(getCorner1().getX(), getCorner2().getX()),
                                Math.max(getCorner1().getY(), getCorner2().getY()),
                                Math.max(getCorner1().getZ(), getCorner2().getZ()));
                        second = new Location(world, Math.min(getCorner1().getX(), getCorner2().getX()),
                                Math.min(getCorner1().getY(), getCorner2().getY()),
                                Math.min(getCorner1().getZ(), getCorner2().getZ()));

                        for (double x = second.getX(); x <= first.getX(); x += 5)
                            for (double y = second.getY(); y <= first.getY(); y += 5)
                                world.spawnParticle(borderParticle, x, y, first.getZ(), 0, dust);
                        for (double x = second.getX(); x <= first.getX(); x += 5)
                            for (double y = second.getY(); y <= first.getY(); y += 5)
                                world.spawnParticle(borderParticle, x, y, second.getZ(), 0, dust);
                        for (double x = second.getX(); x <= first.getX(); x += 5)
                            for (double z = second.getZ(); z <= first.getZ(); z += 5)
                                world.spawnParticle(borderParticle, x, first.getY(), z, 0, dust);
                        for (double x = second.getX(); x <= first.getX(); x += 5)
                            for (double z = second.getZ(); z <= first.getZ(); z += 5)
                                world.spawnParticle(borderParticle, x, second.getY(), z, 0, dust);
                        for (double z = second.getZ(); z <= first.getZ(); z += 5)
                            for (double y = second.getY(); y <= first.getY(); y += 5)
                                world.spawnParticle(borderParticle, first.getX(), y, z, 0, dust);
                        for (double z = second.getZ(); z <= first.getZ(); z += 5)
                            for (double y = second.getY(); y <= first.getY(); y += 5)
                                world.spawnParticle(borderParticle, second.getX(), y, z, 0, dust);

                    } catch (Exception e) {
                        CommunicationManager.debugError(
                            CommunicationManager.DebugLevel.NORMAL, String.format("Border particle generation error for %s.", getName()),
                            true,
                                e
                        );
                    }
                }
            }, 0 , 20);
    }

    public void cancelBorderParticles() {
        if (cornerParticlesID != 0)
            Bukkit.getScheduler().cancelTask(cornerParticlesID);
        cornerParticlesID = 0;
    }

    private void checkClosedParticles() {
        if (isClosed()) {
            startSpawnParticles();
            startMonsterParticles();
            startVillagerParticles();
            startBorderParticles();
        } else {
            cancelSpawnParticles();
            cancelMonsterParticles();
            cancelVillagerParticles();
            cancelBorderParticles();
        }
    }

    public boolean hasNormal() {
        try {
            return ArenaDataManager.hasNormal(id);
        }
        catch (NoSuchPathException e) {
            setNormal(true);
            return true;
        }
    }

    public void setNormal(boolean normal) {
        ArenaDataManager.setNormal(id, normal);
    }

    public boolean hasEnchants() {
        try {
            return ArenaDataManager.hasEnchants(id);
        }
        catch (NoSuchPathException e) {
            setEnchants(true);
            return true;
        }
    }

    public void setEnchants(boolean enchants) {
        ArenaDataManager.setEnchants(id, enchants);
    }

    public boolean hasCustom() {
        try {
            return ArenaDataManager.hasCustom(id);
        }
        catch (NoSuchPathException e) {
            setCustom(false);
            return false;
        }
    }

    public void setCustom(boolean custom) {
        ArenaDataManager.setCustom(id, custom);
    }

    public boolean hasCommunity() {
        try {
            return ArenaDataManager.hasCommunity(id);
        }
        catch (NoSuchPathException e) {
            setCommunity(true);
            return true;
        }
    }

    public void setCommunity(boolean community) {
        ArenaDataManager.setCommunity(id, community);
    }

    public boolean hasGemDrop() {
        try {
            return ArenaDataManager.hasGemDrop(id);
        }
        catch (NoSuchPathException e) {
            setGemDrop(true);
            return true;
        }
    }

    public void setGemDrop(boolean gemDrop) {
        ArenaDataManager.setGemDrop(id, gemDrop);
    }

    public boolean hasExpDrop() {
        try {
            return ArenaDataManager.hasExpDrop(id);
        }
        catch (NoSuchPathException e) {
            setExpDrop(true);
            return true;
        }
    }

    public void setExpDrop(boolean expDrop) {
        ArenaDataManager.setExpDrop(id, expDrop);
    }

    public Location getCorner1() {
        return ArenaDataManager.getCorner1(id);
    }

    public void setCorner1(Location location) {
        // Remove particles
        cancelBorderParticles();

        // Set location
        ArenaDataManager.setCorner1(id, location);

        // Turn on particles if appropriate
        if (isClosed())
            startBorderParticles();
    }

    public Location getCorner2() {
        return ArenaDataManager.getCorner2(id);
    }

    public void setCorner2(Location location) {
        // Remove particles
        cancelBorderParticles();

        // Set location
        ArenaDataManager.setCorner2(id, location);

        // Turn on particles if appropriate
        if (isClosed())
            startBorderParticles();
    }

    public BoundingBox getBounds() {
        return new BoundingBox(getCorner1().getX(), getCorner1().getY(), getCorner1().getZ(),
                getCorner2().getX(), getCorner2().getY(), getCorner2().getZ());
    }

    public boolean hasWinSound() {
        try {
            return ArenaDataManager.hasWinSound(id);
        }
        catch (NoSuchPathException e) {
            setWinSound(true);
            return true;
        }
    }

    public void setWinSound(boolean bool) {
        ArenaDataManager.setWinSound(id, bool);
    }

    public boolean hasLoseSound() {
        try {
            return ArenaDataManager.hasLoseSound(id);
        }
        catch (NoSuchPathException e) {
            setLoseSound(true);
            return true;
        }
    }

    public void setLoseSound(boolean bool) {
        ArenaDataManager.setLoseSound(id, bool);
    }

    public boolean hasWaveStartSound() {
        try {
            return ArenaDataManager.hasWaveStart(id);
        }
        catch (NoSuchPathException e) {
            setWaveStartSound(true);
            return true;
        }
    }

    public void setWaveStartSound(boolean bool) {
        ArenaDataManager.setWaveStart(id, bool);
    }

    public boolean hasWaveEndSound() {
        try {
            return ArenaDataManager.hasWaveEnd(id);
        }
        catch (NoSuchPathException e) {
            setWaveEndSound(true);
            return true;
        }
    }

    public void setWaveEndSound(boolean bool) {
        ArenaDataManager.setWaveEnd(id, bool);
    }

    public boolean hasGemSound() {
        try {
            return ArenaDataManager.hasGemSound(id);
        }
        catch (NoSuchPathException e) {
            setGemSound(true);
            return true;
        }
    }

    public void setGemSound(boolean bool) {
        ArenaDataManager.setGemSound(id, bool);
    }

    public boolean hasPlayerDeathSound() {
        try {
            return ArenaDataManager.hasDeathSound(id);
        }
        catch (NoSuchPathException e) {
            setPlayerDeathSound(true);
            return true;
        }
    }

    public void setPlayerDeathSound(boolean bool) {
        ArenaDataManager.setDeathSound(id, bool);
    }

    public boolean hasAbilitySound() {
        try {
            return ArenaDataManager.hasAbilitySound(id);
        }
        catch (NoSuchPathException e) {
            setAbilitySound(true);
            return true;
        }
    }

    public void setAbilitySound(boolean bool) {
        ArenaDataManager.setAbilitySound(id, bool);
    }

    public boolean hasDynamicCount() {
        try {
            return ArenaDataManager.hasDynamicCount(id);
        }
        catch (NoSuchPathException e) {
            setDynamicCount(false);
            return false;
        }
    }

    public void setDynamicCount(boolean bool) {
        ArenaDataManager.setDynamicCount(id, bool);
    }

    public boolean hasDynamicDifficulty() {
        try {
            return ArenaDataManager.hasDynamicDifficulty(id);
        }
        catch (NoSuchPathException e) {
            setDynamicDifficulty(false);
            return false;
        }
    }

    public void setDynamicDifficulty(boolean bool) {
        ArenaDataManager.setDynamicDifficulty(id, bool);
    }

    public boolean hasDynamicPrices() {
        try {
            return ArenaDataManager.hasDynamicPrices(id);
        }
        catch (NoSuchPathException e) {
            setDynamicPrices(false);
            return false;
        }
    }

    public void setDynamicPrices(boolean bool) {
        ArenaDataManager.setDynamicPrices(id, bool);
    }

    public boolean hasDynamicLimit() {
        try {
            return ArenaDataManager.hasDynamicLimit(id);
        }
        catch (NoSuchPathException e) {
            setDynamicLimit(false);
            return false;
        }
    }

    public void setDynamicLimit(boolean bool) {
        ArenaDataManager.setDynamicLimit(id, bool);
    }

    public boolean hasLateArrival() {
        try {
            return ArenaDataManager.hasLateArrival(id);
        }
        catch (NoSuchPathException e) {
            setLateArrival(false);
            return false;
        }
    }

    public void setLateArrival(boolean bool) {
        ArenaDataManager.setLateArrival(id, bool);
    }

    public boolean isClosed() {
        try {
            return ArenaDataManager.getArenaClosed(id);
        }

        // Default to closed
        catch (NoSuchPathException e) {
            return true;
        }
    }

    public void setClosed(boolean closed) {
        // Kick players
        getPlayers().forEach(vdPlayer -> Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(vdPlayer.getPlayer()))));

        // Clear the arena
        WorldManager.clear(getCorner1(), getCorner2());

        // Set closed and handle particles/holographics
        ArenaDataManager.setArenaClosed(id, closed);
        refreshPortal();
        checkClosedParticles();
    }

    public List<ArenaRecord> getArenaRecords() {
        try {
            return ArenaDataManager.getArenaRecords(id);
        } catch (BadDataException | NoSuchPathException e) {
            CommunicationManager.debugError(
                CommunicationManager.DebugLevel.VERBOSE, String.format("Attempted to retrieve arena records for %s but encountered an error.", getName())
            );
            return new ArrayList<>();
        }
    }

    public List<ArenaRecord> getSortedDescendingRecords() {
        return getArenaRecords().stream().filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(ArenaRecord::getWave).reversed())
                .collect(Collectors.toList());
    }

    public boolean checkNewRecord(ArenaRecord record) {
        List<ArenaRecord> records = getArenaRecords();

        // Automatic record
        if (records.size() < 4)
            records.add(record);

        // New record
        else if (records.stream().filter(Objects::nonNull)
                .anyMatch(arenaRecord -> arenaRecord.getWave() < record.getWave())) {
            records.sort(Comparator.comparingInt(ArenaRecord::getWave));
            records.set(0, record);
        }

        // No record
        else return false;

        // Save data
        ArenaDataManager.setArenaRecords(id, records);
        return true;
    }

    public void setStatus(ArenaStatus status) {
        this.status = status;
        refreshPortal();
    }

    public void newGameID() {
        gameID = (int) (100 * Math.random());
    }

    public double getCurrentDifficulty() {
        double difficulty = Math.pow(Math.E, Math.pow(Math.max(currentWave - 1, 0), .55) / (5 - getDifficultyMultiplier() / 2d));
        if (hasDynamicDifficulty())
            difficulty *= Math.sqrt(.1 * getActiveCount() + .6);
        return difficulty;
    }

    public void incrementCurrentWave() {
        currentWave++;
        refreshPortal();
    }

    public void resetCurrentWave() {
        currentWave = 0;
        refreshPortal();
    }

    public void resetVillagers() {
        villagers = 0;
    }

    public void resetEnemies() {
        enemies = 0;
    }

    public void incrementGolems() {
        golems++;
    }

    public void decrementGolems() {
        golems--;
    }

    public void resetGolems() {
        golems = 0;
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link VDPlayer.Status} ALIVE.
     */
    public List<VDPlayer> getAlives() {
        return players.stream().filter(Objects::nonNull).filter(p -> p.getStatus() == VDPlayer.Status.ALIVE)
                .collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link VDPlayer.Status} GHOST.
     */
    public List<VDPlayer> getGhosts() {
        return players.stream().filter(Objects::nonNull).filter(p -> p.getStatus() == VDPlayer.Status.GHOST)
                .collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link VDPlayer.Status} SPECTATOR.
     */
    public List<VDPlayer> getSpectators() {
        return players.stream().filter(Objects::nonNull).filter(p -> p.getStatus() == VDPlayer.Status.SPECTATOR)
                .collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link VDPlayer.Status} ALIVE or GHOST.
     */
    public List<VDPlayer> getActives() {
        return Stream.concat(getAlives().stream(), getGhosts().stream()).collect(Collectors.toList());
    }

    /**
     * A function to get the corresponding {@link VDPlayer} in the arena for a given {@link Player}.
     * @param player The {@link Player} in question.
     * @return The corresponding {@link VDPlayer}.
     * @throws PlayerNotFoundException Thrown when the arena doesn't have a corresponding {@link VDPlayer}.
     */
    public @NotNull VDPlayer getPlayer(Player player) throws PlayerNotFoundException {
        try {
            return players.stream().filter(Objects::nonNull).filter(p -> p.getID().equals(player.getUniqueId()))
                    .collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            throw new PlayerNotFoundException("Player not in this arena.");
        }
    }

    /**
     * Checks whether there is a corresponding {@link VDPlayer} for a given {@link Player}.
     * @param player The {@link Player} in question.
     * @return Whether a corresponding {@link VDPlayer} was found.
     */
    public boolean hasPlayer(Player player) {
        try {
            return players.stream().filter(Objects::nonNull).anyMatch(p -> p.getID().equals(player.getUniqueId()));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasPlayer(VDPlayer player) {
        return players.stream().filter(Objects::nonNull).anyMatch(p -> p.equals(player));
    }

    public int getActiveCount() {
        return getActives().size();
    }

    public int getAlive() {
        return getActiveCount() - getGhostCount();
    }

    public int getGhostCount() {
        return getGhosts().size();
    }

    public int getSpectatorCount() {
        return getSpectators().size();
    }

    public Inventory getCustomShopEditorMenu() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(
                new InventoryMeta(InventoryID.CUSTOM_SHOP_EDITOR_MENU, InventoryType.MENU, this),
                54,
                CommunicationManager.format("&6&lCustom Shop Editor: " + getName())
        );

        // Set exit option
        for (int i = 45; i < 54; i++)
            inv.setItem(i, InventoryButtons.exit());

        // Get items from stored inventory
        try {
            ArenaDataManager.getCustomShop(id).forEach(inv::setItem);
        } catch (BadDataException e) {
            CommunicationManager.debugError(
                CommunicationManager.DebugLevel.NORMAL, String.format("Attempted to retrieve the custom shop inventory of %s but encountered an error.", getName())
            );
        }

        return inv;
    }

    public Inventory getCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(
                new InventoryMeta(InventoryID.CUSTOM_SHOP_MENU, InventoryType.MENU, this),
                54,
                CommunicationManager.format("&6&l") + LanguageManager.names.customShop
        );

        // Set exit option
        inv.setItem(49, InventoryButtons.exit());

        // Get items from stored inventory
        try {
            ArenaDataManager.getCustomShop(id).forEach(inv::setItem);
        } catch (BadDataException e) {
            CommunicationManager.debugError(
                CommunicationManager.DebugLevel.NORMAL, String.format("Attempted to retrieve the custom shop inventory of %s but encountered an error.", getName())
            );
        }

        return inv;
    }

    /**
     * Retrieves a mockup of the custom shop for presenting arena information.
     * @return Mock custom shop {@link Inventory}
     */
    public Inventory getMockCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(
                new InventoryMeta(InventoryID.MOCK_CUSTOM_SHOP_MENU, InventoryType.MENU, this),
                54,
                CommunicationManager.format("&6&l" + LanguageManager.names.customShop + ": " + getName())
        );

        // Set exit option
        inv.setItem(49, InventoryButtons.exit());

        // Get items from stored inventory
        try {
            ArenaDataManager.getCustomShop(id).forEach(inv::setItem);
        } catch (BadDataException e) {
            CommunicationManager.debugError(
                CommunicationManager.DebugLevel.NORMAL, String.format("Attempted to retrieve the custom shop inventory of %s but encountered an error.", getName())
            );
        }

        return inv;
    }

    /**
     * Create a time limit bar to display.
     */
    public void startTimeLimitBar() {
        timeLimitBar = Bukkit.createBossBar(CommunicationManager.format("&e" +
                        String.format(LanguageManager.names.timeBar, getCurrentWave()) + " - " +
                        getWaveTimeLimit() + ":00"),
                BarColor.YELLOW, BarStyle.SOLID);
    }

    /**
     * Updates the time limit bar's progress.
     * @param progress The bar's new progress.
     */
    public void updateTimeLimitBar(double progress) {
        timeLimitBar.setProgress(progress);
        timeLimitBar.setTitle(getTimeLimitBarTitle(progress));
    }

    /**
     * Updates the time limit bar's color and progress.
     * @param color The bar's new color.
     * @param progress The bar's new progress.
     */
    public void updateTimeLimitBar(BarColor color, double progress) {
        timeLimitBar.setColor(color);
        timeLimitBar.setProgress(progress);
        timeLimitBar.setTitle(getTimeLimitBarTitle(progress));
    }

    /**
     * Removes the time limit bar from every player.
     */
    public void removeTimeLimitBar() {
        players.forEach(vdPlayer -> timeLimitBar.removePlayer(vdPlayer.getPlayer()));
        timeLimitBar = null;
    }

    private String getTimeLimitBarTitle(double progress) {
        int minutes = (int) (progress * getWaveTimeLimit());
        int seconds = (int) ((progress * getWaveTimeLimit() - minutes) * 60 + 0.5);
        return CommunicationManager.format("&e" +
                String.format(LanguageManager.names.timeBar, getCurrentWave()) + " - " +
                minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
    }

    /**
     * Displays the time limit bar to a player.
     * @param player {@link Player} to display the time limit bar to.
     */
    public void addPlayerToTimeLimitBar(Player player) {
        if (timeLimitBar != null)
            timeLimitBar.addPlayer(player);
    }

    /**
     * Removes the time limit bar from a player's display.
     * @param player {@link Player} to remove the time limit bar from.
     */
    public void removePlayerFromTimeLimitBar(Player player) {
        if (timeLimitBar != null && player != null)
            timeLimitBar.removePlayer(player);
    }

    /**
     * Sets remaining monsters glowing.
     */
    public void setMonsterGlow() {
        Objects.requireNonNull(getPlayerSpawn().getLocation().getWorld())
                .getNearbyEntities(getBounds()).stream().filter(Objects::nonNull)
                .filter(entity -> entity.hasMetadata("VD"))
                .filter(entity -> entity instanceof Monster || entity instanceof Slime ||
                        entity instanceof Hoglin || entity instanceof Phantom)
                .forEach(entity -> entity.setGlowing(true));
    }

    /**
     * Checks and closes an arena if the arena does not meet opening requirements. Opens arena if autoOpen is on.
     */
    public void checkClose() {
        if (!GameDataManager.hasLobby() || getPortalLocation() == null || getPlayerSpawn() == null ||
                getMonsterSpawns().isEmpty() || getVillagerSpawns().isEmpty() || !hasCustom() && !hasNormal() ||
                getCorner1() == null || getCorner2() == null ||
                !Objects.equals(getCorner1().getWorld(), getCorner2().getWorld())) {
            setClosed(true);
            CommunicationManager.debugInfo(
                CommunicationManager.DebugLevel.VERBOSE, String.format("%s did not meet opening requirements and was closed.", getName())
            );
        }

        else if (Main.plugin.getConfig().getBoolean("autoOpen")) {
            setClosed(false);
            CommunicationManager.debugInfo(
                CommunicationManager.DebugLevel.VERBOSE, String.format("%s met opening requirements and was opened.", getName())
            );
        }
    }

    /**
     * Check the number of players that are sharing a certain effect.
     *
     * @param effectType The effect type to look for.
     * @return Number of players sharing the effect type.
     */
    public int effectShareCount(Kit.EffectType effectType) {
        Kit effectKit;

        switch (effectType) {
            case BLACKSMITH:
                effectKit = Kit.blacksmith().setKitLevel(1);
                break;
            case WITCH:
                effectKit = Kit.witch().setKitLevel(1);
                break;
            case MERCHANT:
                effectKit = Kit.merchant().setKitLevel(1);
                break;
            case VAMPIRE:
                effectKit = Kit.vampire().setKitLevel(1);
                break;
            case GIANT1:
                effectKit = Kit.giant().setKitLevel(1);
                break;
            case GIANT2:
                effectKit = Kit.giant().setKitLevel(2);
                break;
            default:
                effectKit = Kit.none();
        }

        return (int) getActives().stream().filter(VDPlayer::isSharing).filter(player ->
                effectKit.equals(player.getKit()) || effectKit.equals(player.getKit2())).count();
    }

    /**
     * Checks mobs within its boundaries to make sure mob counts are accurate.
     */
    public void calibrate() {
        int monsters;
        int villagers;
        int golems;

        // Get accurate numbers
        monsters = (int) Objects.requireNonNull(getPlayerSpawn().getLocation().getWorld())
                .getNearbyEntities(getBounds()).stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.hasMetadata("VD"))
                .filter(entity -> entity instanceof Monster || entity instanceof Slime || entity instanceof Hoglin ||
                        entity instanceof Phantom).count();
        villagers = (int) getPlayerSpawn().getLocation().getWorld().getNearbyEntities(getBounds()).stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.hasMetadata("VD")).filter(entity -> entity instanceof Villager).count();
        golems = (int) getPlayerSpawn().getLocation().getWorld().getNearbyEntities(getBounds()).stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.hasMetadata("VD")).filter(entity -> entity instanceof IronGolem).count();
        boolean calibrated = false;

        // Update if out of cal
        if (monsters != enemies) {
            enemies = monsters;
            calibrated = true;
        }
        if (villagers != this.villagers) {
            this.villagers = villagers;
            calibrated = true;
        }
        if (golems != this.golems)
            this.golems = golems;

        // Skip if no visible calibration was performed
        if (!calibrated)
            return;

        // Update scoreboards
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(this)));

        // Trigger game end if all villagers are gone
        if (this.villagers <= 0 && status == ArenaStatus.ACTIVE && !isSpawningVillagers()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(this)));
            return;
        }

        // Trigger wave end if all monsters are gone
        if (enemies <= 0 && status == ArenaStatus.ACTIVE && !isSpawningMonsters())
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                    Bukkit.getPluginManager().callEvent(new WaveEndEvent(this)));
    }

    /**
     * Copies permanent arena characteristics from an existing arena and saves the change to the arena file.
     * @param arenaToCopy The arena to copy characteristics from.
     */
    public void copy(Arena arenaToCopy) {
        setMaxPlayers(arenaToCopy.getMaxPlayers());
        setMinPlayers(arenaToCopy.getMinPlayers());
        setMaxWaves(arenaToCopy.getMaxWaves());
        setWaveTimeLimit(arenaToCopy.getWaveTimeLimit());
        setDifficultyMultiplier(arenaToCopy.getDifficultyMultiplier());
        setDynamicCount(arenaToCopy.hasDynamicCount());
        setDynamicDifficulty(arenaToCopy.hasDynamicDifficulty());
        setLateArrival(arenaToCopy.hasLateArrival());
        setDynamicLimit(arenaToCopy.hasDynamicLimit());
        setDynamicPrices(arenaToCopy.hasDynamicPrices());
        setDifficultyLabel(arenaToCopy.getDifficultyLabel());
        setBannedKits(arenaToCopy.getBannedKits());
        setNormal(arenaToCopy.hasNormal());
        setEnchants(arenaToCopy.hasEnchants());
        setCustom(arenaToCopy.hasCustom());
        setCommunity(arenaToCopy.hasCommunity());
        setWinSound(arenaToCopy.hasWinSound());
        setLoseSound(arenaToCopy.hasLoseSound());
        setWaveStartSound(arenaToCopy.hasWaveStartSound());
        setWaveEndSound(arenaToCopy.hasWaveEndSound());
        setGemSound(arenaToCopy.hasGemSound());
        setPlayerDeathSound(arenaToCopy.hasPlayerDeathSound());
        setAbilitySound(arenaToCopy.hasAbilitySound());
        setWaitingSound(arenaToCopy.getWaitingSoundCode());
        setSpawnParticles(arenaToCopy.hasSpawnParticles());
        setMonsterParticles(arenaToCopy.hasMonsterParticles());
        setVillagerParticles(arenaToCopy.hasVillagerParticles());
        setBorderParticles(arenaToCopy.hasBorderParticles());
        if (ArenaDataManager.hasCustomShop(arenaToCopy.id)) {
            try {
                ArenaDataManager.copyCustomShop(arenaToCopy.id, id);
            } catch (NoSuchPathException e) {
                CommunicationManager.debugError(
                    CommunicationManager.DebugLevel.NORMAL, String.format("Unsuccessful attempt to copy the custom shop inventory of %s to %s.",
                        arenaToCopy.getName(), getName()));
            }
        }

        CommunicationManager.debugInfo(
            CommunicationManager.DebugLevel.VERBOSE, String.format("Copied the characteristics of %s to %s.", arenaToCopy.getName(), getName())
        );
    }

    /**
     * Removes all data of this arena from the arena file.
     */
    public void remove() {
        wipe();
        ArenaDataManager.removeArena(id);
        CommunicationManager.debugInfo(CommunicationManager.DebugLevel.NORMAL, String.format("Removing %s.", getName()));
    }

    /**
     * Removes all trace of the arena's physical existence.
     */
    public void wipe() {
        // Kick players
        getPlayers().forEach(vdPlayer -> Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(vdPlayer.getPlayer()))));

        // Clear the arena
        WorldManager.clear(getCorner1(), getCorner2());

        // Set closed
        ArenaDataManager.setArenaClosed(id, true);

        // Remove holographics
        if (getArenaBoard() != null)
            getArenaBoard().remove();
        if (getPortal() != null)
            getPortal().remove();
        cancelSpawnParticles();
        cancelMonsterParticles();
        cancelVillagerParticles();
        cancelBorderParticles();
    }
}
