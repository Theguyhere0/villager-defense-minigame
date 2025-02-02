package me.theguyhere.villagerdefense.plugin.game.models.arenas;

import lombok.Getter;
import lombok.Setter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryID;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryType;
import me.theguyhere.villagerdefense.plugin.inventories.Buttons;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.events.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.plugin.events.WaveEndEvent;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidNameException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.plugin.game.displays.Portal;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.Tasks;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.*;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    /** A variable to more quickly access the file configuration of the arena file.*/
    private final FileConfiguration config;
    /** Common string for all data paths in the arena file.*/
    @Getter
    private final String path;
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
        config = Main.getArenaData();
        id = arenaID;
        path = "arena." + arenaID;
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
        return config.getString(path + ".name");
    }

    /**
     * Writes the new name of the arena into the arena file.
     * @param name New arena name.
     */
    public void setName(String name) throws InvalidNameException {
        // Check if name is not empty
        if (name == null || name.length() == 0) throw new InvalidNameException("Empty");

        // Check if name is the same as current
        else if (name.equals(getName())) throw new InvalidNameException("Same");

        else {
            // Check for duplicate name
            try {
                GameManager.getArena(name);
                throw new InvalidNameException("Duplicate");
            } catch (ArenaNotFoundException ignored) {
            }

            // Save name
            config.set(path + ".name", name);
            Main.saveArenaData();
        }

        // Set default max players to 12 if it doesn't exist
        if (getMaxPlayers() == 0)
            setMaxPlayers(12);

        // Set default min players to 1 if it doesn't exist
        if (getMinPlayers() == 0)
            setMinPlayers(1);

        // Set default wolf cap to 5 if it doesn't exist
        if (getWolfCap() == 0)
            setWolfCap(5);

        // Set default iron golem cap to 2 if it doesn't exist
        if (getGolemCap() == 0)
            setgolemCap(2);

        // Set default max waves to -1 if it doesn't exist
        if (getMaxWaves() == 0)
            setMaxWaves(-1);

        // Set default wave time limit to -1 if it doesn't exist
        if (getWaveTimeLimit() == 0)
            setWaveTimeLimit(-1);

        // Set default difficulty multiplier to 1 if it doesn't exist
        if (getDifficultyMultiplier() == 0)
            setDifficultyMultiplier(1);

        // Set default to closed if arena closed doesn't exist
        if (!config.contains(path + ".closed"))
            setClosed(true);

        // Set default sound options
        if (!config.contains(path + ".sounds")) {
            setWinSound(true);
            setLoseSound(true);
            setWaveStartSound(true);
            setWaveFinishSound(true);
            setGemSound(true);
            setPlayerDeathSound(true);
            setAbilitySound(true);
            setWaitingSound("none");
        }

        // Set default shop toggle
        if (!config.contains(path + ".normal"))
            setNormal(true);

        // Set enchant shop toggle
        if (!config.contains(path + ".enchants"))
            setEnchants(true);

        // Set community chest toggle
        if (!config.contains(path + ".community"))
            setCommunity(true);

        // Set default gem drop toggle
        if (!config.contains(path + ".gemDrop"))
            setGemDrop(true);

        // Set default experience drop toggle
        if (!config.contains(path + ".expDrop"))
            setExpDrop(true);

        // Set default particle toggles
        if (!config.contains(path + ".particles.spawn"))
            setSpawnParticles(true);
        if (!config.contains(path + ".particles.monster"))
            setMonsterParticles(true);
        if (!config.contains(path + ".particles.villager"))
            setVillagerParticles(true);

        // Refresh portal
        if (getPortalLocation() != null)
            refreshPortal();
    }

    /**
     * Retrieves the difficulty label of the arena from the arena file.
     * @return Arena difficulty label.
     */
    public String getDifficultyLabel() {
        if (config.contains(path + ".difficultyLabel"))
            return config.getString(path + ".difficultyLabel");
        else return "";
    }

    /**
     * Writes the new difficulty label of the arena into the arena file.
     * @param label New difficulty label.
     */
    public void setDifficultyLabel(String label) {
        config.set(path + ".difficultyLabel", label);
        Main.saveArenaData();
        refreshPortal();
    }

    /**
     * Retrieves the maximum player count of the arena from the arena file.
     * @return Maximum player count.
     */
    public int getMaxPlayers() {
        return config.getInt(path + ".max");
    }

    /**
     * Writes the new maximum player count of the arena into the arena file.
     * @param maxPlayers New maximum player count.
     */
    public void setMaxPlayers(int maxPlayers) {
        config.set(path + ".max", maxPlayers);
        Main.saveArenaData();
    }

    /**
     * Retrieves the minimum player count of the arena from the arena file.
     * @return Minimum player count.
     */
    public int getMinPlayers() {
        return config.getInt(path + ".min");
    }

    /**
     * Writes the new minimum player count of the arena into the arena file.
     * @param minPlayers New minimum player count.
     */
    public void setMinPlayers(int minPlayers) {
        config.set(path + ".min", minPlayers);
        Main.saveArenaData();
    }

    /**
     * Retrieves the wolf cap per player of the arena from the arena file.
     * @return Wolf cap per player.
     */
    public int getWolfCap() {
        return config.getInt(path + ".wolf");
    }

    /**
     * Writes the new wolf cap per player of the arena into the arena file.
     * @param wolfCap New wolf cap per player.
     */
    public void setWolfCap(int wolfCap) {
        config.set(path + ".wolf", wolfCap);
        Main.saveArenaData();
    }

    /**
     * Retrieves the iron golem cap of the arena from the arena file.
     * @return Iron golem cap.
     */
    public int getGolemCap() {
        return config.getInt(path + ".golem");
    }

    /**
     * Writes the new iron golem cap of the arena into the arena file.
     * @param golemCap New iron golem cap.
     */
    public void setgolemCap(int golemCap) {
        config.set(path + ".golem", golemCap);
        Main.saveArenaData();
    }

    /**
     * Retrieves the maximum waves of the arena from the arena file.
     * @return Maximum waves.
     */
    public int getMaxWaves() {
        return config.getInt(path + ".maxWaves");
    }

    /**
     * Writes the new maximum waves of the arena into the arena file.
     * @param maxWaves New maximum waves.
     */
    public void setMaxWaves(int maxWaves) {
        config.set(path + ".maxWaves", maxWaves);
        Main.saveArenaData();
    }

    /**
     * Retrieves the nominal time limit per wave of the arena from the arena file.
     * @return Nominal time limit per wave.
     */
    public int getWaveTimeLimit() {
        return config.getInt(path + ".waveTimeLimit");
    }

    /**
     * Writes the new nominal time limit per wave of the arena into the arena file.
     * @param timeLimit New nominal time limit per wave.
     */
    public void setWaveTimeLimit(int timeLimit) {
        config.set(path + ".waveTimeLimit", timeLimit);
        Main.saveArenaData();
    }

    /**
     * Retrieves the difficulty multiplier of the arena from the arena file.
     * @return Difficulty multiplier.
     */
    public int getDifficultyMultiplier() {
        return config.getInt(path + ".difficulty");
    }

    /**
     * Writes the new difficulty multiplier of the arena into the arena file.
     * @param multiplier New difficulty multiplier.
     */
    public void setDifficultyMultiplier(int multiplier) {
        config.set(path + ".difficulty", multiplier);
        Main.saveArenaData();
    }

    /**
     * Retrieves the waiting music of the arena from the arena file.
     * @return Waiting {@link Sound}.
     */
    public Sound getWaitingSound() {
        switch (Objects.requireNonNull(config.getString(path + ".sounds.waiting"))) {
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
        String sound = config.getString(path + ".sounds.waiting");
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
        String sound = config.getString(path + ".sounds.waiting");
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
        return config.getString(path + ".sounds.waiting");
    }

    /**
     * Writes the new waiting music of the arena into the arena file.
     * @param sound Numerical representation of the new waiting music.
     */
    public void setWaitingSound(String sound) {
        config.set(path + ".sounds.waiting", sound);
        Main.saveArenaData();
    }

    public Location getPortalLocation() {
        return DataManager.getConfigLocationNoPitch(path + ".portal");
    }

    /**
     * Creates a new portal at the given location and deletes the old portal.
     * @param location New location
     */
    public void setPortal(Location location) {
        // Save config location
        DataManager.setConfigurationLocation(path + ".portal", location);

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
            portal = new Portal(Objects.requireNonNull(DataManager.getConfigLocationNoPitch(
                    path + ".portal")), this);
            portal.displayForOnline();
        } catch (Exception e) {
            CommunicationManager.debugError(String.format("Invalid location for %s's portal ", getName()), CommunicationManager.DebugLevel.NORMAL,
                    !Main.releaseMode, e);
            CommunicationManager.debugInfo("Portal location data may be corrupt. If data cannot be manually " +
                    "corrected in arenaData.yml, please delete the portal location data for " + getName() + ".",
                    CommunicationManager.DebugLevel.NORMAL);
        }
    }

    /**
     * Centers the portal location along the x and z axis.
     */
    public void centerPortal() {
        // Center the location
        DataManager.centerConfigLocation(path + ".portal");

        // Recreate the portal
        refreshPortal();
    }

    /**
     * Removes the portal from the game and from the arena file.
     */
    public void removePortal() {
        if (portal != null) {
            portal.remove();
            portal = null;
        }
        DataManager.setConfigurationLocation(path + ".portal", null);
        checkClose();
    }

    public Location getArenaBoardLocation() {
        return DataManager.getConfigLocationNoPitch(path + ".arenaBoard");
    }
    
    /**
     * Creates a new arena leaderboard at the given location and deletes the old arena leaderboard.
     * @param location New location
     */
    public void setArenaBoard(Location location) {
        // Save config location
        DataManager.setConfigurationLocation(path + ".arenaBoard", location);

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
            arenaBoard = new ArenaBoard(
                    Objects.requireNonNull(DataManager.getConfigLocationNoPitch(path + ".arenaBoard")),
                    this);
            arenaBoard.displayForOnline();
        } catch (Exception e) {
            CommunicationManager.debugError(
                    String.format("Invalid location for %s's arena board ", getName()),
                    CommunicationManager.DebugLevel.NORMAL,
                    !Main.releaseMode,
                    e
            );
            CommunicationManager.debugInfo("Arena board location data may be corrupt. If data cannot be " +
                            "manually corrected in arenaData.yml, please delete the arena board location data for " +
                            getName() + ".", CommunicationManager.DebugLevel.NORMAL);
        }
    }

    /**
     * Centers the arena leaderboard location along the x and z axis.
     */
    public void centerArenaBoard() {
        // Center the location
        DataManager.centerConfigLocation(path + ".arenaBoard");

        // Recreate the board
        refreshArenaBoard();
    }

    /**
     * Removes the arena board from the game and from the arena file.
     */
    public void removeArenaBoard() {
        if (arenaBoard != null) {
            arenaBoard.remove();
            arenaBoard = null;
        }
        DataManager.setConfigurationLocation(path + ".arenaBoard", null);
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
                    Objects.requireNonNull(DataManager.getConfigLocation(path + ".spawn")),
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
        DataManager.setConfigurationLocation(path + ".spawn", location);
        refreshPlayerSpawn();
    }

    /**
     * Centers the player spawn location of the arena along the x and z axis.
     */
    public void centerPlayerSpawn() {
        DataManager.centerConfigLocation(path + ".spawn");
        refreshPlayerSpawn();
    }

    /**
     * Retrieves the waiting room location of the arena from the arena file.
     * @return Player spawn location.
     */
    public Location getWaitingRoom() {
        return DataManager.getConfigLocation(path + ".waiting");
    }

    /**
     * Writes the new waiting room location of the arena into the arena file.
     * @param location New player spawn location.
     */
    public void setWaitingRoom(Location location) {
        DataManager.setConfigurationLocation(path + ".waiting", location);
        Main.saveArenaData();
    }

    /**
     * Centers the waiting room location of the arena along the x and z axis.
     */
    public void centerWaitingRoom() {
        DataManager.centerConfigLocation(path + ".waiting");
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
        DataManager.getConfigLocationMap(path + ".monster").forEach((id, location) ->
        {
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
            } catch (InvalidLocationException | NullPointerException ignored) {
            }
        });

        // Turn on particles if appropriate
        if (isClosed())
            startMonsterParticles();
    }

    /**
     * Retrieves a specific monster spawn of the arena.
     * @param monsterSpawnID - Monster spawn ID.
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
        DataManager.setConfigurationLocation(path + ".monster." + monsterSpawnID, location);
        refreshMonsterSpawns();
    }

    public void centerMonsterSpawn(int monsterSpawnID) {
        DataManager.centerConfigLocation(path + ".monster." + monsterSpawnID);
        refreshMonsterSpawns();
    }

    public void setMonsterSpawnType(int monsterSpawnID, int type) {
        config.set(path + ".monster." + monsterSpawnID + ".type", type);
        Main.saveArenaData();
        refreshMonsterSpawns();
    }

    public int getMonsterSpawnType(int monsterSpawnID) {
        return config.getInt(path + ".monster." + monsterSpawnID + ".type");
    }

    /**
     * Generates a new ID for a new monster spawn.
     *
     * @return New monster spawn ID
     */
    public int newMonsterSpawnID() {
        return Calculator.nextSmallestUniqueWhole(DataManager.getConfigLocationMap(path + ".monster").keySet());
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
        DataManager.getConfigLocationMap(path + ".villager").forEach((id, location) ->
        {
            try {
                villagerSpawns.add(new ArenaSpawn(Objects.requireNonNull(location), ArenaSpawnType.VILLAGER, id));
            } catch (InvalidLocationException | NullPointerException ignored) {
            }
        });

        // Turn on particles if appropriate
        if (isClosed())
            startVillagerParticles();
    }

    /**
     * Retrieves a specific villager spawn of the arena.
     * @param villagerSpawnID - Villager spawn ID.
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
        DataManager.setConfigurationLocation(path + ".villager." + villagerSpawnID, location);
        refreshVillagerSpawns();
    }

    public void centerVillagerSpawn(int villagerSpawnID) {
        DataManager.centerConfigLocation(path + ".villager." + villagerSpawnID);
        refreshVillagerSpawns();
    }

    /**
     * Generates a new ID for a new villager spawn.
     *
     * @return New villager spawn ID
     */
    public int newVillagerSpawnID() {
        return Calculator.nextSmallestUniqueWhole(DataManager.getConfigLocationMap(path + ".villager")
                .keySet());
    }

    public List<String> getBannedKits() {
        return config.getStringList(path + ".bannedKits");
    }

    public void setBannedKits(List<String> bannedKits) {
        config.set(path + ".bannedKits", bannedKits);
        Main.saveArenaData();
    }

    public List<String> getForcedChallenges() {
        return config.getStringList(path + ".forcedChallenges");
    }

    public void setForcedChallenges(List<String> forcedChallenges) {
        config.set(path + ".forcedChallenges", forcedChallenges);
        Main.saveArenaData();
    }

    public String getSpawnTableFile() {
        if (!config.contains(path + ".spawnTable"))
            setSpawnTableFile("default");
        return config.getString(path + ".spawnTable");
    }

    public boolean setSpawnTableFile(String option) {
        String file = option + ".yml";
        if (option.equals("custom"))
            file = path + ".yml";

        if (new File(Main.plugin.getDataFolder().getPath(), "spawnTables/" + file).exists() ||
                option.equals("default")) {
            config.set(path + ".spawnTable", option);
            Main.saveArenaData();
            return true;
        }

        return false;
    }

    public boolean hasSpawnParticles() {
        return config.getBoolean(path + ".particles.spawn");
    }

    public void setSpawnParticles(boolean bool) {
        config.set(path + ".particles.spawn", bool);
        Main.saveArenaData();
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
                            String.format("Player spawn particle generation error for %s.", getName()),
                            CommunicationManager.DebugLevel.VERBOSE);
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
        return config.getBoolean(path + ".particles.monster");
    }

    public void setMonsterParticles(boolean bool) {
        config.set(path + ".particles.monster", bool);
        Main.saveArenaData();
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
                                    String.format("Monster particle generation error for %s.", getName()),
                                    CommunicationManager.DebugLevel.VERBOSE
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
        return config.getBoolean(path + ".particles.villager");
    }

    public void setVillagerParticles(boolean bool) {
        config.set(path + ".particles.villager", bool);
        Main.saveArenaData();
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
                                    String.format("Villager particle generation error for %s.", getName()),
                                    CommunicationManager.DebugLevel.VERBOSE
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
        return config.getBoolean(path + ".particles.border");
    }

    public void setBorderParticles(boolean bool) {
        config.set(path + ".particles.border", bool);
        Main.saveArenaData();
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
                                String.format("Border particle generation error for %s.", getName()),
                                CommunicationManager.DebugLevel.NORMAL,
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
        return config.getBoolean(path + ".normal");
    }

    public void setNormal(boolean normal) {
        config.set(path + ".normal", normal);
        Main.saveArenaData();
    }

    public boolean hasEnchants() {
        return config.getBoolean(path + ".enchants");
    }

    public void setEnchants(boolean enchants) {
        config.set(path + ".enchants", enchants);
        Main.saveArenaData();
    }

    public boolean hasCustom() {
        return config.getBoolean(path + ".custom");
    }

    public void setCustom(boolean bool) {
        config.set(path + ".custom", bool);
        Main.saveArenaData();
    }

    public boolean hasCommunity() {
        return config.getBoolean(path + ".community");
    }

    public void setCommunity(boolean bool) {
        config.set(path + ".community", bool);
        Main.saveArenaData();
    }

    public boolean hasGemDrop() {
        return config.getBoolean(path + ".gemDrop");
    }

    public void setGemDrop(boolean bool) {
        config.set(path + ".gemDrop", bool);
        Main.saveArenaData();
    }

    public boolean hasExpDrop() {
        return config.getBoolean(path + ".expDrop");
    }

    public void setExpDrop(boolean bool) {
        config.set(path + ".expDrop", bool);
        Main.saveArenaData();
    }

    public Location getCorner1() {
        return DataManager.getConfigLocationNoRotation(path + ".corner1");
    }

    public void setCorner1(Location location) {
        // Remove particles
        cancelBorderParticles();

        // Set location
        DataManager.setConfigurationLocation(path + ".corner1", location);

        // Turn on particles if appropriate
        if (isClosed())
            startBorderParticles();
    }

    public Location getCorner2() {
        return DataManager.getConfigLocationNoRotation(path + ".corner2");
    }

    public void setCorner2(Location location) {
        // Remove particles
        cancelBorderParticles();

        // Set location
        DataManager.setConfigurationLocation(path + ".corner2", location);

        // Turn on particles if appropriate
        if (isClosed())
            startBorderParticles();
    }

    public BoundingBox getBounds() {
        return new BoundingBox(getCorner1().getX(), getCorner1().getY(), getCorner1().getZ(),
                getCorner2().getX(), getCorner2().getY(), getCorner2().getZ());
    }

    public boolean hasWinSound() {
        return config.getBoolean(path + ".sounds.win");
    }

    public void setWinSound(boolean bool) {
        config.set(path + ".sounds.win", bool);
        Main.saveArenaData();
    }

    public boolean hasLoseSound() {
        return config.getBoolean(path + ".sounds.lose");
    }

    public void setLoseSound(boolean bool) {
        config.set(path + ".sounds.lose", bool);
        Main.saveArenaData();
    }

    public boolean hasWaveStartSound() {
        return config.getBoolean(path + ".sounds.start");
    }

    public void setWaveStartSound(boolean bool) {
        config.set(path + ".sounds.start", bool);
        Main.saveArenaData();
    }

    public boolean hasWaveFinishSound() {
        return config.getBoolean(path + ".sounds.end");
    }

    public void setWaveFinishSound(boolean bool) {
        config.set(path + ".sounds.end", bool);
        Main.saveArenaData();
    }

    public boolean hasGemSound() {
        return config.getBoolean(path + ".sounds.gem");
    }

    public void setGemSound(boolean bool) {
        config.set(path + ".sounds.gem", bool);
        Main.saveArenaData();
    }

    public boolean hasPlayerDeathSound() {
        return config.getBoolean(path + ".sounds.death");
    }

    public void setPlayerDeathSound(boolean bool) {
        config.set(path + ".sounds.death", bool);
        Main.saveArenaData();
    }

    public boolean hasAbilitySound() {
        return config.getBoolean(path + ".sounds.ability");
    }

    public void setAbilitySound(boolean bool) {
        config.set(path + ".sounds.ability", bool);
        Main.saveArenaData();
    }

    public boolean hasDynamicCount() {
        return config.getBoolean(path + ".dynamicCount");
    }

    public void setDynamicCount(boolean bool) {
        config.set(path + ".dynamicCount", bool);
        Main.saveArenaData();
    }

    public boolean hasDynamicDifficulty() {
        return config.getBoolean(path + ".dynamicDifficulty");
    }

    public void setDynamicDifficulty(boolean bool) {
        config.set(path + ".dynamicDifficulty", bool);
        Main.saveArenaData();
    }

    public boolean hasDynamicPrices() {
        return config.getBoolean(path + ".dynamicPrices");
    }

    public void setDynamicPrices(boolean bool) {
        config.set(path + ".dynamicPrices", bool);
        Main.saveArenaData();
    }

    public boolean hasDynamicLimit() {
        return config.getBoolean(path + ".dynamicLimit");
    }

    public void setDynamicLimit(boolean bool) {
        config.set(path + ".dynamicLimit", bool);
        Main.saveArenaData();
    }

    public boolean hasLateArrival() {
        return config.getBoolean(path + ".lateArrival");
    }

    public void setLateArrival(boolean bool) {
        config.set(path + ".lateArrival", bool);
        Main.saveArenaData();
    }

    public boolean isClosed() {
        return config.getBoolean(path + ".closed");
    }

    public void setClosed(boolean closed) {
        // Kick players
        getPlayers().forEach(vdPlayer -> Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(vdPlayer.getPlayer()))));

        // Clear the arena
        WorldManager.clear(getCorner1(), getCorner2());

        // Set closed and handle particles/holographics
        config.set(path + ".closed", closed);
        Main.saveArenaData();
        refreshPortal();
        checkClosedParticles();
    }

    public List<ArenaRecord> getArenaRecords() {
        List<ArenaRecord> arenaRecords = new ArrayList<>();
        if (config.contains(path + ".records"))
            try {
                Objects.requireNonNull(config.getConfigurationSection(path + ".records")).getKeys(false)
                        .forEach(index -> arenaRecords.add(new ArenaRecord(
                                config.getInt(path + ".records." + index + ".wave"),
                                config.getStringList(path + ".records." + index + ".players")
                        )));
            } catch (Exception e) {
                CommunicationManager.debugError(
                        String.format("Attempted to retrieve arena records for %s but found none.", getName()),
                        CommunicationManager.DebugLevel.VERBOSE);
            }

        return arenaRecords;
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
        for (int i = 0; i < records.size(); i++) {
            config.set(path + ".records." + i + ".wave", records.get(i).getWave());
            config.set(path + ".records." + i + ".players", records.get(i).getPlayers());
        }
        Main.saveArenaData();
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
     * @return A list of {@link VDPlayer} of the {@link PlayerStatus} ALIVE.
     */
    public List<VDPlayer> getAlives() {
        return players.stream().filter(Objects::nonNull).filter(p -> p.getStatus() == PlayerStatus.ALIVE)
                .collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link PlayerStatus} GHOST.
     */
    public List<VDPlayer> getGhosts() {
        return players.stream().filter(Objects::nonNull).filter(p -> p.getStatus() == PlayerStatus.GHOST)
                .collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link PlayerStatus} SPECTATOR.
     */
    public List<VDPlayer> getSpectators() {
        return players.stream().filter(Objects::nonNull).filter(p -> p.getStatus() == PlayerStatus.SPECTATOR)
                .collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link PlayerStatus} ALIVE or GHOST.
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
            inv.setItem(i, Buttons.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        try {
            Objects.requireNonNull(config.getConfigurationSection(path + ".customShop")).getKeys(false)
                    .forEach(index -> {
                        try {
                            // Get raw item and data
                            ItemStack item = Objects.requireNonNull(
                                    config.getItemStack(path + ".customShop." + index)).clone();
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            assert meta != null;
                            String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                            int price = NumberUtils.toInt(
                                    meta.getDisplayName().substring(meta.getDisplayName().length() - 5), -1);

                            // Transform to proper shop item
                            meta.setDisplayName(CommunicationManager.format("&f" + name));
                            if (meta.hasLore())
                                lore = meta.getLore();
                            assert lore != null;
                            if (price >= 0)
                                lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
                                        ": &a" + price));
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            // Set item into inventory
                            inv.setItem(Integer.parseInt(index), item);
                        } catch (Exception e) {
                            CommunicationManager.debugError(
                                    String.format(
                                            "An error occurred retrieving an item from %s's custom shop.", getName()),
                                    CommunicationManager.DebugLevel.VERBOSE
                            );
                        }
                    });
        } catch (Exception e) {
            CommunicationManager.debugError(
                    String.format("Attempted to retrieve the custom shop inventory of %s but found none.", getName()),
                    CommunicationManager.DebugLevel.NORMAL
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
        inv.setItem(49, Buttons.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        try {
            Objects.requireNonNull(config.getConfigurationSection(path + ".customShop")).getKeys(false)
                    .forEach(index -> {
                        try {
                            // Get raw item and data
                            ItemStack item = Objects.requireNonNull(
                                    config.getItemStack(path + ".customShop." + index)).clone();
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            assert meta != null;
                            String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                            int price = NumberUtils.toInt(
                                    meta.getDisplayName().substring(meta.getDisplayName().length() - 5), -1);

                            // Transform to proper shop item
                            meta.setDisplayName(CommunicationManager.format("&f" + name));
                            if (meta.hasLore())
                                lore = meta.getLore();
                            assert lore != null;
                            if (price >= 0)
                                lore.add(CommunicationManager.format("&2" +
                                        LanguageManager.messages.gems + ": &a" + price));
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            // Set item into inventory
                            inv.setItem(Integer.parseInt(index), item);
                        } catch (Exception e) {
                            CommunicationManager.debugError(
                                    String.format(
                                            "An error occurred retrieving an item from %s's custom shop.", getName()),
                                    CommunicationManager.DebugLevel.VERBOSE
                            );
                        }
                    });
        } catch (Exception e) {
            CommunicationManager.debugError(
                    String.format("Attempted to retrieve the custom shop inventory of %s but found none.", getName()),
                    CommunicationManager.DebugLevel.NORMAL
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
        inv.setItem(49, Buttons.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        try {
            Objects.requireNonNull(config.getConfigurationSection(path + ".customShop")).getKeys(false)
                    .forEach(index -> {
                        try {
                            // Get raw item and data
                            ItemStack item = Objects.requireNonNull(
                                    config.getItemStack(path + ".customShop." + index)).clone();
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            assert meta != null;
                            String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                            int price = NumberUtils.toInt(
                                    meta.getDisplayName().substring(meta.getDisplayName().length() - 5), -1);

                            // Transform to proper shop item
                            meta.setDisplayName(CommunicationManager.format("&f" + name));
                            if (meta.hasLore())
                                lore = meta.getLore();
                            assert lore != null;
                            if (price >= 0)
                                lore.add(CommunicationManager.format("&2" +
                                        LanguageManager.messages.gems + ": &a" + price));
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            // Set item into inventory
                            inv.setItem(Integer.parseInt(index), item);
                        } catch (Exception e) {
                            CommunicationManager.debugError(
                                    String.format(
                                            "An error occurred retrieving an item from %s's custom shop.", getName()),
                                    CommunicationManager.DebugLevel.VERBOSE
                            );
                        }
                    });
        } catch (Exception e) {
            CommunicationManager.debugError(
                    String.format("Attempted to retrieve the custom shop inventory of %s but found none.", getName()),
                    CommunicationManager.DebugLevel.NORMAL
            );
        }

        return inv;
    }

    /**
     * Create a time limit bar to display.
     */
    public void startTimeLimitBar() {
        timeLimitBar = Bukkit.createBossBar(CommunicationManager.format("&e" +
                        String.format(LanguageManager.names.timeBar, Integer.toString(getCurrentWave())) + " - " +
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
                String.format(LanguageManager.names.timeBar, Integer.toString(getCurrentWave())) + " - " +
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
        if (!Main.getArenaData().contains("lobby") || getPortalLocation() == null || getPlayerSpawn() == null ||
                getMonsterSpawns().isEmpty() || getVillagerSpawns().isEmpty() || !hasCustom() && !hasNormal() ||
                getCorner1() == null || getCorner2() == null ||
                !Objects.equals(getCorner1().getWorld(), getCorner2().getWorld())) {
            setClosed(true);
            CommunicationManager.debugInfo(
                    String.format("%s did not meet opening requirements and was closed.", getName()),
                    CommunicationManager.DebugLevel.VERBOSE
            );
        }

        else if (Main.plugin.getConfig().getBoolean("autoOpen")) {
            setClosed(false);
            CommunicationManager.debugInfo(
                    String.format("%s met opening requirements and was opened.", getName()),
                    CommunicationManager.DebugLevel.VERBOSE
            );
        }
    }

    /**
     * Check the number of players that are sharing a certain effect.
     *
     * @param effectType The effect type to look for.
     * @return Number of players sharing the effect type.
     */
    public int effectShareCount(EffectType effectType) {
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
        setWaveFinishSound(arenaToCopy.hasWaveFinishSound());
        setGemSound(arenaToCopy.hasGemSound());
        setPlayerDeathSound(arenaToCopy.hasPlayerDeathSound());
        setAbilitySound(arenaToCopy.hasAbilitySound());
        setWaitingSound(arenaToCopy.getWaitingSoundCode());
        setSpawnParticles(arenaToCopy.hasSpawnParticles());
        setMonsterParticles(arenaToCopy.hasMonsterParticles());
        setVillagerParticles(arenaToCopy.hasVillagerParticles());
        setBorderParticles(arenaToCopy.hasBorderParticles());
        if (config.contains(arenaToCopy.getPath() + ".customShop"))
            try {
                Objects.requireNonNull(config.getConfigurationSection(arenaToCopy.getPath() + ".customShop"))
                        .getKeys(false)
                        .forEach(index -> config.set(path + ".customShop." + index,
                                config.getItemStack(arenaToCopy.getPath() + ".customShop." + index)));
                Main.saveArenaData();
            } catch (Exception e) {
                CommunicationManager.debugError(
                        String.format("Unsuccessful attempt to copy the custom shop inventory of %s to %s.",
                                arenaToCopy.getName(), getName()), CommunicationManager.DebugLevel.NORMAL);
            }

        CommunicationManager.debugInfo(
                String.format("Copied the characteristics of %s to %s.", arenaToCopy.getName(), getName()),
                CommunicationManager.DebugLevel.VERBOSE
        );
    }

    /**
     * Removes all data of this arena from the arena file.
     */
    public void remove() {
        wipe();
        config.set(path, null);
        Main.saveArenaData();
        CommunicationManager.debugInfo(String.format("Removing %s.", getName()), CommunicationManager.DebugLevel.NORMAL);
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
        config.set(path + ".closed", true);
        Main.saveArenaData();

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
