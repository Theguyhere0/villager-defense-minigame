package me.theguyhere.villagerdefense.plugin.game.models.arenas;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.exceptions.*;
import me.theguyhere.villagerdefense.plugin.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.plugin.game.displays.Portal;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.*;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryID;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryType;
import me.theguyhere.villagerdefense.plugin.tools.*;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
    private final int id;
    /** A variable to more quickly access the file configuration of the arena file.*/
    private final FileConfiguration config;
    /** Common string for all data paths in the arena file.*/
    private final String path;

    /** Collection of active tasks currently running for this arena.*/
    private final Map<String, BukkitRunnable> activeTasks = new HashMap<>();
    /** Collection of mobs managed under this arena.*/
    private final List<VDMob> mobs = new ArrayList<>();
    /** Status of the arena.*/
    private ArenaStatus status;
    /** A collection of spawning tasks for the arena.*/
    private final List<BukkitTask> spawnTasks = new ArrayList<>();
    /** Whether the arena is in the process of spawning monsters.*/
    private boolean spawningMonsters;
    /** Whether the arena is in the process of spawning villagers.*/
    private boolean spawningVillagers;
    /** The ID of the game currently in progress.*/
    private int gameID = 0;
    /** Current wave of the active game.*/
    private int currentWave = 0;
    /** Villager count.*/
    private int villagers = 0;
    /** Enemy count.*/
    private int enemies = 0;
    /** Iron golem count.*/
    private int golems = 0;
    /** ID of task managing player spawn particles.*/
    private int playerParticlesID = 0;
    /** ID of task managing monster spawn particles.*/
    private int monsterParticlesID = 0;
    /** ID of task managing villager spawn particles.*/
    private int villagerParticlesID = 0;
    /** ID of task managing corner particles.*/
    private int cornerParticlesID = 0;
    /** A list of players in the arena.*/
    private final List<VDPlayer> players = new ArrayList<>();
    /** Weapon shop inventory.*/
    private Inventory weaponShop;
    /** Armor shop inventory.*/
    private Inventory armorShop;
    /** Consumables shop inventory.*/
    private Inventory consumeShop;
    /** Community chest inventory.*/
    private Inventory communityChest;
    /** Time limit bar object.*/
    private BossBar timeLimitBar;
    /** Portal object for the arena.*/
    private Portal portal;
    /** The player spawn for the arena.*/
    private ArenaSpawn playerSpawn;
    /** The monster spawns for the arena.*/
    private final List<ArenaSpawn> monsterSpawns = new ArrayList<>();
    /** The villager spawns for the arena.*/
    private final List<ArenaSpawn> villagerSpawns = new ArrayList<>();
    /** Arena scoreboard object for the arena.*/
    private ArenaBoard arenaBoard;

    // Task names
    private static final String NOTIFY_WAITING = "notifyWaiting";
    private static final String ONE_MINUTE_NOTICE = "1MinuteNotice";
    private static final String THIRTY_SECOND_NOTICE = "30SecondNotice";
    private static final String TEN_SECOND_NOTICE = "10SecondNotice";
    private static final String FORCE_FIVE_SECOND_NOTICE = "force5SecondNotice";
    private static final String FIVE_SECOND_NOTICE = "5SecondNotice";
    private static final String START_ARENA = "startArena";
    private static final String FORCE_START_ARENA = "forceStartArena";
    private static final String DIALOGUE_TWO = "dialogue2";
    private static final String DIALOGUE_THREE = "dialogue3";
    private static final String DIALOGUE_FOUR = "dialogue4";
    private static final String DIALOGUE_FIVE = "dialogue5";
    private static final String END_WAVE = "endWave";
    private static final String START_WAVE = "startWave";
    private static final String UPDATE_BAR = "updateBar";
    private static final String CALIBRATE = "calibrate";
    private static final String ONE_TICK = "oneTick";
    private static final String TEN_TICK = "tenTick";
    private static final String TWENTY_TICK = "twentyTick";
    private static final String FORTY_TICK = "fortyTick";
    private static final String TICK = "Tick";
    private static final String KICK = "kick";
    private static final String RESET = "restart";

    public Arena(int arenaID) {
        config = Main.getArenaData();
        id = arenaID;
        path = "arena." + arenaID;
        status = ArenaStatus.WAITING;
        refreshArenaBoard();
        refreshPlayerSpawn();
        refreshMonsterSpawns();
        refreshVillagerSpawns();
        refreshPortal();
        checkClosedParticles();
        checkClose();
    }

    public int getId() {
        return id;
    }

    /**
     * Retrieves the path of the arena from the arena file.
     * @return Arena path prefix.
     */
    public String getPath() {
        return path;
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
    public void setName(String name) throws IllegalArenaNameException {
        // Check if name is not empty
        if (name == null || name.length() == 0) throw new IllegalArenaNameException("Empty");

        // Check if name is the same as current
        else if (name.equals(getName())) throw new IllegalArenaNameException("Same");

        else {
            // Check for duplicate name
            try {
                GameManager.getArena(name);
                throw new IllegalArenaNameException("Duplicate");
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

        // Set default villager type to plains if it doesn't exist
        if (getVillagerType() == null || getVillagerType().isEmpty())
            setVillagerType("plains");

        // Set default wolf cap to 5 if it doesn't exist
        if (getWolfCap() == 0)
            setWolfCap(5);

        // Set default iron golem cap to 2 if it doesn't exist
        if (getGolemCap() == 0)
            setGolemCap(2);

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
            setPlayerDeathSound(true);
            setAbilitySound(true);
            setWaitingSound("none");
        }

        // Set community chest toggle
        if (!config.contains(path + ".community"))
            setCommunity(true);

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

    public String getVillagerType() {
        return config.getString(path + ".villagerType");
    }

    public void setVillagerType(String type) {
        config.set(path + ".villagerType", type);
        Main.saveArenaData();
        refreshPortal();
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
    public void setGolemCap(int golemCap) {
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
    @NotNull
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
        if (sound != null && GameManager.getValidSounds().contains(sound)) {
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

    public Portal getPortal() {
        return portal;
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
            CommunicationManager.debugError(String.format("Invalid location for %s's portal ", getName()), 1,
                    !Main.releaseMode, e);
            CommunicationManager.debugInfo("Portal location data may be corrupt. If data cannot be manually " +
                    "corrected in arenaData.yml, please delete the portal location data for " + getName() + ".",
                    1);
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

    public ArenaBoard getArenaBoard() {
        return arenaBoard;
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
                    1,
                    !Main.releaseMode,
                    e
            );
            CommunicationManager.debugInfo("Arena board location data may be corrupt. If data cannot be " +
                            "manually corrected in arenaData.yml, please delete the arena board location data for " +
                            getName() + ".", 1);
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
        if (!isClosed() && Main.isLoaded())
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

    public ArenaSpawn getPlayerSpawn() {
        return playerSpawn;
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
        if (!isClosed() && Main.isLoaded())
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

    public List<ArenaSpawn> getMonsterSpawns() {
        return monsterSpawns;
    }

    public List<Location> getMonsterGroundSpawnLocations() {
        // Gather spawns
        List<Location> grounds = new ArrayList<>();
        for (ArenaSpawn arenaSpawn : monsterSpawns) {
            if (arenaSpawn.getSpawnType() != ArenaSpawnType.MONSTER_AIR)
                grounds.add(arenaSpawn.getLocation());
        }

        // Default to all if empty
        if (grounds.isEmpty())
            return monsterSpawns.stream().map(ArenaSpawn::getLocation).collect(Collectors.toList());
        else return grounds;
    }

    public List<Location> getMonsterAirSpawnLocations() {
        // Gather spawns
        List<Location> airs = new ArrayList<>();
        for (ArenaSpawn arenaSpawn : monsterSpawns) {
            if (arenaSpawn.getSpawnType() != ArenaSpawnType.MONSTER_GROUND)
                airs.add(arenaSpawn.getLocation());
        }

        // Default to all if empty
        if (airs.isEmpty())
            return monsterSpawns.stream().map(ArenaSpawn::getLocation).collect(Collectors.toList());
        else return airs;
    }

    public List<Location> getVillagerSpawnLocations() {
        List<Location> spawns = new ArrayList<>();
        for (ArenaSpawn arenaSpawn : villagerSpawns)
            spawns.add(arenaSpawn.getLocation());
        return spawns;
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
        return Utils.nextSmallestUniqueWhole(DataManager.getConfigLocationMap(path + ".monster").keySet());
    }

    /**
     * Refreshes the villager spawns of the arena.
     */
    public void refreshVillagerSpawns() {
        // Prevent refreshing villager spawns when arena is open
        if (!isClosed() && Main.isLoaded())
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

    public List<ArenaSpawn> getVillagerSpawns() {
        return villagerSpawns;
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
        return Utils.nextSmallestUniqueWhole(DataManager.getConfigLocationMap(path + ".villager")
                .keySet());
    }

    public List<String> getBannedKitIDs() {
        return config.getStringList(path + ".bannedKits");
    }

    public void setBannedKitIDs(List<String> bannedKits) {
        config.set(path + ".bannedKits", bannedKits);
        Main.saveArenaData();
    }

    public List<String> getForcedChallengeIDs() {
        return config.getStringList(path + ".forcedChallenges");
    }

    public void setForcedChallengeIDs(List<String> forcedChallenges) {
        config.set(path + ".forcedChallenges", forcedChallenges);
        Main.saveArenaData();
    }

    public String getSpawnTableFile() {
        if (!config.contains(path + ".spawnTable"))
            setSpawnTableFile("default");
        String file = config.getString(path + ".spawnTable");
        if ("custom".equals(file))
            return "a" + id + ".yml";
        else return file + ".yml";
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
                            2);
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
                                    2
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
                                    2
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
                                1,
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

    public boolean hasCommunity() {
        return config.getBoolean(path + ".community");
    }

    public void setCommunity(boolean bool) {
        config.set(path + ".community", bool);
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

    public void stretchBounds() {
        Location temp = getCorner1();
        temp.setY(Objects.requireNonNull(getCorner1().getWorld()).getMaxHeight());
        setCorner1(temp);
        temp = getCorner2();
        temp.setY(Objects.requireNonNull(getCorner2().getWorld()).getMinHeight() - 1);
        setCorner2(temp);
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
        kickPlayers();

        // Set closed and handle particles/holographics
        config.set(path + ".closed", closed);
        Main.saveArenaData();
        resetGame();
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
                        2);
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

    public void startNotifyWaiting() throws ArenaClosedException, ArenaStatusException, ArenaTaskException {
        // Check if waiting notifications can start
        if (isClosed())
            throw new ArenaClosedException();
        if (status != ArenaStatus.WAITING)
            throw new ArenaStatusException(ArenaStatus.WAITING);
        if (getActiveCount() >= getMinPlayers())
            throw new ArenaTaskException("Arena is no longer waiting");
        if (activeTasks.containsKey(NOTIFY_WAITING))
            throw new ArenaTaskException("Arena already started waiting notifications");
        if (getActiveCount() > 0 && activeTasks.containsKey(FORCE_START_ARENA))
            throw new ArenaTaskException("Arena was force started");

        // Clear active tasks
        activeTasks.forEach((name, task) -> task.cancel());
        activeTasks.clear();

        // Start repeating notification of waiting
        activeTasks.put(NOTIFY_WAITING, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                getPlayers().forEach(player ->
                        PlayerManager.notifyAlert(player.getPlayer(), LanguageManager.messages.waitingForPlayers));
                CommunicationManager.debugInfo("%s is currently waiting for players to start.", 2,
                        getName());
            }
        });
        activeTasks.get(NOTIFY_WAITING).runTaskTimer(Main.plugin, 0,
                Utils.secondsToTicks(Utils.minutesToSeconds(1)));
    }

    public void startCountDown() throws ArenaStatusException, ArenaClosedException, ArenaTaskException {
        // Check additionally for if countdown already started
        if (activeTasks.containsKey(START_ARENA) || activeTasks.containsKey(FORCE_START_ARENA))
            throw new ArenaTaskException("Arena already started countdown");

        restartCountDown();
    }

    public void restartCountDown() throws ArenaStatusException, ArenaClosedException, ArenaTaskException {
        // Check if countdown can start
        if (isClosed())
            throw new ArenaClosedException();
        if (status != ArenaStatus.WAITING)
            throw new ArenaStatusException(ArenaStatus.WAITING);
        if (getActiveCount() == 0)
            throw new ArenaTaskException("Arena cannot start countdown without players");

        // Clear active tasks
        activeTasks.forEach((name, task) -> task.cancel());
        activeTasks.clear();

        // Two-minute notice
        getPlayers().forEach(player ->
                PlayerManager.notifyAlert(
                        player.getPlayer(),
                        LanguageManager.messages.minutesLeft,
                        new ColoredMessage(ChatColor.AQUA, "2")
                ));
        CommunicationManager.debugInfo("%s is starting in %s minutes.", 2, getName(), "2");

        // Schedule one-minute notice
        activeTasks.put(ONE_MINUTE_NOTICE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                getPlayers().forEach(player ->
                        PlayerManager.notifyAlert(
                                player.getPlayer(),
                                LanguageManager.messages.minutesLeft,
                                new ColoredMessage(ChatColor.AQUA, "1")
                        ));
                CommunicationManager.debugInfo("%s is starting in %s minute.", 2,
                        getName(), "1");

                // Cleanup
                activeTasks.remove(ONE_MINUTE_NOTICE);
            }
        });
        activeTasks.get(ONE_MINUTE_NOTICE).runTaskLater(Main.plugin, Utils.secondsToTicks(Utils.minutesToSeconds(1)));

        // Schedule 30-second notice
        activeTasks.put(THIRTY_SECOND_NOTICE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                getPlayers().forEach(player ->
                        PlayerManager.notifyAlert(
                                player.getPlayer(),
                                LanguageManager.messages.secondsLeft,
                                new ColoredMessage(ChatColor.AQUA, "30")
                        ));
                CommunicationManager.debugInfo("%s is starting in %s seconds.", 2,
                        getName(), "30");

                // Cleanup
                activeTasks.remove(THIRTY_SECOND_NOTICE);
            }
        });
        activeTasks.get(THIRTY_SECOND_NOTICE).runTaskLater(Main.plugin,
                Utils.secondsToTicks(Utils.minutesToSeconds(2) - 30));

        // Schedule 10-second notice
        activeTasks.put(TEN_SECOND_NOTICE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                getPlayers().forEach(player ->
                        PlayerManager.notifyAlert(
                                player.getPlayer(),
                                LanguageManager.messages.secondsLeft,
                                new ColoredMessage(ChatColor.AQUA, "10")
                        ));
                CommunicationManager.debugInfo("%s is starting in %s seconds.", 2,
                        getName(), "10");

                // Cleanup
                activeTasks.remove(TEN_SECOND_NOTICE);
            }
        });
        activeTasks.get(TEN_SECOND_NOTICE).runTaskLater(Main.plugin,
                Utils.secondsToTicks(Utils.minutesToSeconds(2) - 10));


        // Schedule 5-second notice
        activeTasks.put(FIVE_SECOND_NOTICE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                getPlayers().forEach(player ->
                        PlayerManager.notifyAlert(
                                player.getPlayer(),
                                LanguageManager.messages.secondsLeft,
                                new ColoredMessage(ChatColor.AQUA, "5")
                        ));
                CommunicationManager.debugInfo("%s is starting in %s seconds.", 2,
                        getName(), "5");

                // Cleanup
                activeTasks.remove(FIVE_SECOND_NOTICE);
            }
        });
        activeTasks.get(FIVE_SECOND_NOTICE).runTaskLater(Main.plugin,
                Utils.secondsToTicks(Utils.minutesToSeconds(2) - 5));

        // Schedule start of arena
        activeTasks.put(START_ARENA, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                try {
                    startGame();
                } catch (ArenaException e) {
                    CommunicationManager.debugErrorShouldNotHappen();
                }

                // Cleanup
                activeTasks.remove(START_ARENA);
            }
        });
        activeTasks.get(START_ARENA).runTaskLater(Main.plugin, Utils.secondsToTicks(Utils.minutesToSeconds(2)));
    }

    public void expediteCountDown() throws ArenaClosedException, ArenaStatusException, ArenaTaskException {
        // Check if expedited countdown can start
        if (isClosed())
            throw new ArenaClosedException();
        if (status != ArenaStatus.WAITING)
            throw new ArenaStatusException(ArenaStatus.WAITING);
        if (getActiveCount() == 0)
            throw new ArenaTaskException("Arena cannot start countdown without players");
        if (activeTasks.containsKey(FORCE_START_ARENA))
            throw new ArenaTaskException("Arena already expedited countdown");

        // Clear active tasks
        activeTasks.forEach((name, task) -> task.cancel());
        activeTasks.clear();

        // Forced 10-second notice
        getPlayers().forEach(player -> {
                PlayerManager.notifyAlert(player.getPlayer(), LanguageManager.messages.maxCapacity);
                PlayerManager.notifyAlert(
                        player.getPlayer(),
                        LanguageManager.messages.secondsLeft,
                        new ColoredMessage(ChatColor.AQUA, "10")
                );
        });
        CommunicationManager.debugInfo("%s is starting in %s seconds.", 2,
                getName(), "10");

        // Schedule forced 5-second notice
        activeTasks.put(FORCE_FIVE_SECOND_NOTICE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                getPlayers().forEach(player ->
                        PlayerManager.notifyAlert(
                                player.getPlayer(),
                                LanguageManager.messages.secondsLeft,
                                new ColoredMessage(ChatColor.AQUA, "5")
                        ));
                CommunicationManager.debugInfo("%s is starting in %s seconds.", 2,
                        getName(), "5");

                // Cleanup
                activeTasks.remove(FORCE_FIVE_SECOND_NOTICE);
            }
        });
        activeTasks.get(FORCE_FIVE_SECOND_NOTICE).runTaskLater(Main.plugin, Utils.secondsToTicks(5));

        // Schedule forced start of arena
        activeTasks.put(FORCE_START_ARENA, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                try {
                    startGame();
                } catch (ArenaException e) {
                    CommunicationManager.debugErrorShouldNotHappen();
                }

                // Cleanup
                activeTasks.remove(FORCE_START_ARENA);
            }
        });
        activeTasks.get(FORCE_START_ARENA).runTaskLater(Main.plugin, Utils.secondsToTicks(10));
    }

    private void startGame() throws ArenaClosedException, ArenaStatusException {
        // Check if arena can start
        if (isClosed())
            throw new ArenaClosedException();
        if (status != ArenaStatus.WAITING)
            throw new ArenaStatusException(ArenaStatus.WAITING);

        // Clear active tasks
        activeTasks.forEach((name, task) -> task.cancel());
        activeTasks.clear();

        // Reset villager and enemy count, set new game ID, clear arena
        resetVillagers();
        resetEnemies();
        newGameID();
        WorldManager.clear(getCorner1(), getCorner2());

        // Teleport players to arena if waiting room exists, otherwise clear inventory
        if (getWaitingRoom() != null) {
            for (VDPlayer vdPlayer : getActives())
                PlayerManager.teleAdventure(vdPlayer.getPlayer(), getPlayerSpawn().getLocation());
            for (VDPlayer player : getSpectators())
                PlayerManager.teleSpectator(player.getPlayer(), getPlayerSpawn().getLocation());
        } else {
            for (VDPlayer vdPlayer : getActives())
                vdPlayer.getPlayer().getInventory().clear();
        }

        // Set arena status to active
        setStatus(ArenaStatus.ACTIVE);

        // Stop waiting sound
        if (getWaitingSound() != null)
            getPlayers().forEach(player ->
                    player.getPlayer().stopSound(getWaitingSound()));

        // Start particles if enabled
        if (hasSpawnParticles())
            startSpawnParticles();
        if (hasMonsterParticles())
            startMonsterParticles();
        if (hasVillagerParticles())
            startVillagerParticles();
        if (hasBorderParticles())
            startBorderParticles();

        getActives().forEach(player -> {
            Kit second;

            // Give second kit to players with two kit bonus
            if (player.isBoosted() && PlayerManager.hasAchievement(player.getID(), Achievement.allKits().getID()))
                do {
                    second = Kit.randomKit();

                    // Single tier kits
                    if (!second.isMultiLevel())
                        second.setKitLevel(1);

                        // Multiple tier kits
                    else second.setKitLevel(PlayerManager.getMultiTierKitLevel(player.getID(), second.getID()));

                    player.setKit2(second);
                } while (second.equals(player.getKit()));

            // Give all players starting items and set up attributes
            player.giveItems();
            player.setupAttributes();

            // Give Traders their gems
            if (Kit.trader().setKitLevel(1).equals(player.getKit()) ||
                    Kit.trader().setKitLevel(1).equals(player.getKit2()))
                player.addGems(200);

            // Give gems from crystal conversion
            int amount;
            if (Main.hasCustomEconomy())
                amount = player.getGemBoost() * Math.max((int)
                        (5 * Main.plugin.getConfig().getDouble("vaultEconomyMult")), 1);
            else amount = player.getGemBoost() * 5;
            player.addGems(player.getGemBoost());
            PlayerManager.withdrawCrystalBalance(player.getID(), amount);
        });
        updateScoreboards();

        // Initiate community chest
        setCommunityChest(Bukkit.createInventory(
                new InventoryMeta(InventoryID.COMMUNITY_CHEST_INVENTORY, InventoryType.CONTROLLED, this),
                54,
                CommunicationManager.format("&d&l" + LanguageManager.names.communityChest)
        ));

        // Initiate shops
        setWeaponShop(Inventories.createWeaponShopMenu(1, this));
        setArmorShop(Inventories.createArmorShopMenu(1, this));
        setConsumeShop(Inventories.createConsumableShopMenu(1, this));

        // Start dialogue, then trigger WaveEndEvent
        for (VDPlayer player : getPlayers()) {
            PlayerManager.namedNotify(
                    player.getPlayer(),
                    new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
                    new ColoredMessage(LanguageManager.messages.villageCaptainDialogue1)
            );
        }
        activeTasks.put(DIALOGUE_TWO, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                for (VDPlayer player : getPlayers()) {
                    PlayerManager.namedNotify(
                            player.getPlayer(),
                            new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
                            new ColoredMessage(LanguageManager.messages.villageCaptainDialogue2),
                            new ColoredMessage(ChatColor.AQUA, getName()),
                            new ColoredMessage(ChatColor.AQUA, LanguageManager.names.crystals)
                    );
                }

                // Cleanup
                activeTasks.remove(DIALOGUE_TWO);
            }
        });
        activeTasks.get(DIALOGUE_TWO).runTaskLater(Main.plugin, Utils.secondsToTicks(5));
        activeTasks.put(DIALOGUE_THREE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                for (VDPlayer player : getPlayers()) {
                    PlayerManager.namedNotify(
                            player.getPlayer(),
                            new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
                            new ColoredMessage(LanguageManager.messages.villageCaptainDialogue3)
                    );
                }

                // Cleanup
                activeTasks.remove(DIALOGUE_THREE);
            }
        });
        activeTasks.get(DIALOGUE_THREE).runTaskLater(Main.plugin, Utils.secondsToTicks(11));
        activeTasks.put(DIALOGUE_FOUR, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                for (VDPlayer player : getPlayers()) {
                    PlayerManager.namedNotify(
                            player.getPlayer(),
                            new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
                            new ColoredMessage(LanguageManager.messages.villageCaptainDialogue4),
                            new ColoredMessage(ChatColor.AQUA, "/vd leave")
                    );
                }

                // Cleanup
                activeTasks.remove(DIALOGUE_FOUR);
            }
        });
        activeTasks.get(DIALOGUE_FOUR).runTaskLater(Main.plugin, Utils.secondsToTicks(18));
        activeTasks.put(DIALOGUE_FIVE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                for (VDPlayer player : getPlayers()) {
                    PlayerManager.namedNotify(
                            player.getPlayer(),
                            new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
                            new ColoredMessage(LanguageManager.messages.villageCaptainDialogue5),
                            new ColoredMessage(ChatColor.AQUA, getName())
                    );
                }

                // Cleanup
                activeTasks.remove(DIALOGUE_FIVE);
            }
        });
        activeTasks.get(DIALOGUE_FIVE).runTaskLater(Main.plugin, Utils.secondsToTicks(25));
        activeTasks.put(END_WAVE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                try {
                    endWave();
                } catch (ArenaException e) {
                    CommunicationManager.debugErrorShouldNotHappen();
                }

                // Cleanup
                activeTasks.remove(END_WAVE);
            }
        });
        activeTasks.get(END_WAVE).runTaskLater(Main.plugin, Utils.secondsToTicks(30));

        // Schedule updates
        activeTasks.put(ONE_TICK, new BukkitRunnable() {
            @Override
            public void run() {
                // Update player stats to show
                getActives().forEach(VDPlayer::showAndUpdatStats);

                // Update mob targets
                mobs.forEach(mob -> {
                    Mob mobster = mob.getEntity();
                    Location location = mobster.getLocation();
                    int range = mob.getTargetRange();
                    List<Entity> nearby = Objects.requireNonNull(location.getWorld())
                            .getNearbyEntities(getBounds(), entity -> range < 0 ||
                                    mobster.getLocation().distance(entity.getLocation()) <= range)
                            .stream()
                            .filter(entity -> entity instanceof LivingEntity)
                            .filter(entity -> !entity.isDead())
                            .filter(entity -> mobster.getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue())
                                    && entity instanceof Player || !(entity instanceof Player) &&
                                    !mobster.getMetadata(VDMob.TEAM).equals(entity.getMetadata(VDMob.TEAM)))
                            .filter(entity -> {
                                if (entity instanceof Player)
                                    return ((Player) entity).getGameMode() == GameMode.ADVENTURE;
                                else return true;
                            })
                            .filter(mobster::hasLineOfSight)
                            .sorted((e1, e2) -> (int) (mobster.getLocation().distance(e1.getLocation()) -
                                    mobster.getLocation().distance(e2.getLocation()))).collect(Collectors.toList());
                    List<Entity> priority = nearby.stream().filter(mob.getTargetPriority().getTest())
                            .sorted((e1, e2) -> (int) (mobster.getLocation().distance(e1.getLocation()) -
                                    mobster.getLocation().distance(e2.getLocation())))
                            .collect(Collectors.toList());
                    LivingEntity oldTarget = mobster.getTarget();
                    LivingEntity newTarget = priority.isEmpty() ?
                            (nearby.isEmpty() ? null : (LivingEntity) nearby.get(0)) : (LivingEntity) priority.get(0);
                    if (!(oldTarget == null && newTarget == null) && oldTarget == null || newTarget == null ||
                            !oldTarget.getUniqueId().equals(newTarget.getUniqueId())) {
                        mobster.setTarget(newTarget);
                    }
                });
            }
        });
        activeTasks.get(ONE_TICK).runTaskTimer(Main.plugin, 0, 1);
        activeTasks.put(TEN_TICK, new BukkitRunnable() {
            @Override
            public void run() {
                // Refill ammo
                getActives().forEach(VDPlayer::refill);
            }
        });
        activeTasks.get(TEN_TICK).runTaskTimer(Main.plugin, 0, 10);
        activeTasks.put(TWENTY_TICK, new BukkitRunnable() {
            @Override
            public void run() {
                // Refill ammo
                getActives().forEach(VDPlayer::heal);
            }
        });
        activeTasks.get(TWENTY_TICK).runTaskTimer(Main.plugin, 0, 20);
        activeTasks.put(FORTY_TICK, new BukkitRunnable() {
            @Override
            public void run() {
                // Make witch throw potion
                mobs.forEach(mob -> {
                    Mob mobster = mob.getEntity();
                    LivingEntity target = mobster.getTarget();

                    if (mob instanceof VDWitch && target != null &&
                            target.getLocation().distance(mobster.getLocation()) <= 10) {
                        mobster.launchProjectile(ThrownPotion.class,
                                target.getLocation().subtract(mobster.getLocation()).toVector().normalize());
                    }
                });
            }
        });
        activeTasks.get(FORTY_TICK).runTaskTimer(Main.plugin, 0, 40);

        // Debug message to console
        CommunicationManager.debugInfo("%s is starting.", 2, getName());
    }

    private void endWave() throws ArenaClosedException, ArenaStatusException {
        // Check if wave can end
        if (isClosed())
            throw new ArenaClosedException();
        if (status != ArenaStatus.ACTIVE)
            throw new ArenaStatusException(ArenaStatus.ACTIVE);

        // Clear active tasks EXCEPT update and show status
        Map<String, BukkitRunnable> cache = new HashMap<>();
        activeTasks.forEach((name, task) -> {
            if (!name.contains(TICK))
                task.cancel();
            else cache.put(name, task);
        });
        activeTasks.clear();
        activeTasks.putAll(cache);

        // Stop time limit bar
        removeTimeLimitBar();

        // Play wave end sound if not just starting
        if (hasWaveFinishSound() && getCurrentWave() != 0)
            for (VDPlayer vdPlayer : getPlayers()) {
                vdPlayer.getPlayer().playSound(getPlayerSpawn().getLocation().clone().add(0, -8, 0),
                        Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, .75f);
            }

        // Update player stats
        for (VDPlayer active : getActives())
            if (PlayerManager.getTopWave(active.getID()) < getCurrentWave())
                PlayerManager.setTopWave(active.getID(), getCurrentWave());

        ConfigurationSection limited = Main.getCustomEffects()
                .getConfigurationSection("limited.onWaveComplete");
        ConfigurationSection unlimited = Main.getCustomEffects()
                .getConfigurationSection("unlimited.onWaveComplete");

        // Check custom effects for limited wave arenas
        if (getMaxWaves() > 0 && limited != null)
            limited.getKeys(false).forEach(key -> {
                try {
                    String command = limited.getString(key);
                    if (getCurrentWave() == Integer.parseInt(key) && command != null)
                        getActives().forEach(player ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")));
                } catch (Exception ignored) {
                }
            });

        // Check custom effects for unlimited wave arenas
        if (getMaxWaves() < 0 && unlimited != null)
            unlimited.getKeys(false).forEach(key -> {
                try {
                    String command = unlimited.getString(key);
                    if (getCurrentWave() == Integer.parseInt(key) && command != null)
                        getActives().forEach(player ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")));
                } catch (Exception ignored) {
                }
            });

        // Debug message to console
        CommunicationManager.debugInfo("%s completed wave %s", 2, getName(),
                Integer.toString(getCurrentWave()));

        // Refresh the scoreboards
        updateScoreboards();

        // Increment wave
        incrementCurrentWave();

        // Win condition
        if (getCurrentWave() == getMaxWaves()) {
            endGame();
            if (hasWinSound()) {
                for (VDPlayer vdPlayer : getPlayers()) {
                    vdPlayer.getPlayer().playSound(getPlayerSpawn().getLocation().clone().add(0, -8, 0),
                            Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                }
            }
            return;
        }

        mobs.removeIf(mob -> mob.getEntity().getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue()));

        // Revive dead players
        for (VDPlayer p : getGhosts()) {
            PlayerManager.teleAdventure(p.getPlayer(), getPlayerSpawn().getLocation());
            p.setStatus(PlayerStatus.ALIVE);
            p.giveItems();
            p.setupAttributes();
        }

        getActives().forEach(p -> {
            // Notify of upcoming wave
            if (currentWave % 5 == 0)
                p.getPlayer().sendTitle(CommunicationManager.format("&6" +
                                String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
                        CommunicationManager.format("&7" +
                                String.format(LanguageManager.messages.starting, "&b25&7")),
                        Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));
            else if (currentWave != 1)
                p.getPlayer().sendTitle(CommunicationManager.format("&6" +
                                String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
                        CommunicationManager.format("&7" + String.format(LanguageManager.messages.starting,
                                "&b15&7")),
                        Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));
            else p.getPlayer().sendTitle(CommunicationManager.format("&6" +
                            String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
                    " ", Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

            // Give players gem rewards
            int multiplier;
            switch (getDifficultyMultiplier()) {
                case 1:
                    multiplier = 15;
                    break;
                case 2:
                    multiplier = 10;
                    break;
                case 3:
                    multiplier = 8;
                    break;
                default:
                    multiplier = 6;
            }
            int reward = (currentWave - 1) * multiplier;
            p.addGems(reward);
            if (currentWave > 1)
                PlayerManager.notifySuccess(
                        p.getPlayer(),
                        LanguageManager.messages.gemsReceived,
                        new ColoredMessage(ChatColor.AQUA, Integer.toString(reward))
                );
            GameManager.createBoard(p);
        });

        // Notify spectators of upcoming wave
        if (currentWave % 5 == 0)
            getSpectators().forEach(p ->
                    p.getPlayer().sendTitle(CommunicationManager.format("&6" +
                                    String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
                            CommunicationManager.format("&7" +
                                    String.format(LanguageManager.messages.starting, "&b25&7")),
                            Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));
        else if (currentWave != 1)
            getSpectators().forEach(p ->
                    p.getPlayer().sendTitle(CommunicationManager.format("&6" +
                                    String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
                            CommunicationManager.format("&7" +
                                    String.format(LanguageManager.messages.starting, "&b15&7")),
                            Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));
        else getSpectators().forEach(p ->
                p.getPlayer().sendTitle(CommunicationManager.format("&6" +
                                String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
                        " ", Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));

        // Regenerate shops when time and notify players of it, then start after 25 seconds
        if (currentWave % 5 == 0) {
            int level = currentWave / 5 + 1;
            setWeaponShop(Inventories.createWeaponShopMenu(level, this));
            setArmorShop(Inventories.createArmorShopMenu(level, this));
            setConsumeShop(Inventories.createConsumableShopMenu(level, this));
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> getActives().forEach(player ->
                    player.getPlayer().sendTitle(CommunicationManager.format(
                                    "&6" + LanguageManager.messages.shopUpgrade),
                            CommunicationManager.format("&7" +
                                    String.format(LanguageManager.messages.shopInfo, "5")),
                            Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5),
                            Utils.secondsToTicks(1))), Utils.secondsToTicks(4));
            activeTasks.put(START_WAVE, new BukkitRunnable() {
                @Override
                public void run() {
                    // Task
                    try {
                        startWave();
                    } catch (ArenaException ignored) {
                    }
                }
            });
            activeTasks.get(START_WAVE).runTaskLater(Main.plugin, Utils.secondsToTicks(25));
        }

        // Start wave after 15 seconds if not first wave
        else if (currentWave != 1) {
            activeTasks.put(START_WAVE, new BukkitRunnable() {
                @Override
                public void run() {
                    // Task
                    try {
                        startWave();
                    } catch (ArenaException ignored) {
                    }
                }
            });
            activeTasks.get(START_WAVE).runTaskLater(Main.plugin, Utils.secondsToTicks(15));
        }

        // Start first wave immediately
        else startWave();
    }

    private void startWave() throws ArenaClosedException, ArenaStatusException {
        // Check if wave can start
        if (isClosed())
            throw new ArenaClosedException();
        if (status != ArenaStatus.ACTIVE)
            throw new ArenaStatusException(ArenaStatus.ACTIVE);

        // Clear active tasks EXCEPT update and show status
        Map<String, BukkitRunnable> cache = new HashMap<>();
        activeTasks.forEach((name, task) -> {
            if (!name.contains(TICK))
                task.cancel();
            else cache.put(name, task);
        });
        activeTasks.clear();
        activeTasks.putAll(cache);

        // Play wave start sound
        if (hasWaveStartSound()) {
            for (VDPlayer vdPlayer : getPlayers()) {
                vdPlayer.getPlayer().playSound(getPlayerSpawn().getLocation().clone().add(0, -8, 0),
                        Sound.ENTITY_ENDER_DRAGON_GROWL, 10, .25f);
            }
        }

        // Start wave count down
        if (getWaveTimeLimit() != -1) {
            activeTasks.put(UPDATE_BAR, new BukkitRunnable() {
                double progress = 1;
                double time;
                boolean messageSent;

                @Override
                public void run() {
                    // Get proper multiplier
                    double multiplier = 1 + .2 * ((int) getCurrentDifficulty() - .5);
                    if (!hasDynamicLimit())
                        multiplier = 1;

                    // Add time limit bar if it doesn't exist
                    if (getTimeLimitBar() == null) {
                        progress = 1;
                        startTimeLimitBar();
                        getPlayers().forEach(vdPlayer ->
                                addPlayerToTimeLimitBar(vdPlayer.getPlayer()));
                        time = 1d / Utils.minutesToSeconds(getWaveTimeLimit() * multiplier);
                        messageSent = false;

                        // Debug message to console
                        CommunicationManager.debugInfo("Adding time limit bar to %s", 2, getName());
                    }

                    // Trigger wave end event
                    else if (progress <= 0) {
                        progress = 0;
                        try {
                            endGame();
                        } catch (ArenaException e) {
                            resetGame();
                        }
                    }

                    // Decrement time limit bar
                    else {
                        if (progress <= time * Utils.minutesToSeconds(1)) {
                            updateTimeLimitBar(BarColor.RED, progress);
                            if (!messageSent) {
                                // Send warning
                                getActives().forEach(player ->
                                        player.getPlayer().sendTitle(CommunicationManager.format(
                                                        "&c" + LanguageManager.messages.oneMinuteWarning),
                                                null, Utils.secondsToTicks(.5), Utils.secondsToTicks(1.5),
                                                Utils.secondsToTicks(.5)));

                                // Set monsters glowing when time is low
                                setMonsterGlow();

                                messageSent = true;
                            }
                        } else updateTimeLimitBar(progress);
                        progress -= time;
                    }
                }
            });
            activeTasks.get(UPDATE_BAR).runTaskTimer(Main.plugin, 0, Utils.secondsToTicks(1));
        }

        // Schedule and record calibration
        activeTasks.put(CALIBRATE, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                calibrate();
            }
        });
        activeTasks.get(CALIBRATE).runTaskTimer(Main.plugin, 0, Utils.secondsToTicks(0.5));

        // Get spawn data
        DataManager data = new DataManager("spawnTables/" + getSpawnTableFile());
        String wave = Integer.toString(currentWave);
        if (!data.getConfig().contains(wave)) {
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";
        }
        int monsterCount = data.getConfig().getInt(wave + ".count.m");
        List<String> villagers = getTypeRatio(data, wave + ".vtypes");
        List<String> monsterTypeRatio = getTypeRatio(data, wave + ".mtypes");

        // Account for existing villagers
        Objects.requireNonNull(getPlayerSpawn().getLocation().getWorld()).getNearbyEntities(getBounds()).stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.hasMetadata(VDMob.VD)).filter(entity -> entity instanceof Villager)
                .forEach(villager -> {
                    if (((Villager) villager).getProfession() == Villager.Profession.FLETCHER)
                        villagers.remove(VDFletcher.KEY);
                    else villagers.remove(VDNormalVillager.KEY);
                });

        // Calculate count multiplier
        double countMultiplier = Math.log((getActiveCount() + 7) / 10d) + 1;
        if (!hasDynamicCount())
            countMultiplier = 1;

        // Set mobs left to spawn
        if (monsterCount != 0)
            monsterCount = Math.max((int) (monsterCount * countMultiplier), 1);

        // Prepare, schedule, and start spawning
        Arena arena = this;
        Random r = new Random();
        int delay = 0;
        spawningVillagers = true;
        for (int i = 0; i < villagers.size(); i++) {
            delay += spawnDelayTicks(i);
            int finalI = i;
            spawnTasks.add(new BukkitRunnable() {
                @Override
                public void run() {
                    // Get spawn location
                    Location spawn = getVillagerSpawnLocations().get(r.nextInt(getVillagerSpawnLocations().size()));

                    // Spawn
                    try {
                        addMob(VDMob.of(villagers.get(finalI), arena, spawn, null));
                    } catch (InvalidVDMobKeyException e) {
                        CommunicationManager.debugError("Invalid mob key detected in spawn file!", 1);
                    }
                }
            }.runTaskLater(Main.plugin, delay));
        }
        spawnTasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                spawningVillagers = false;
            }
        }.runTaskLater(Main.plugin, delay));
        delay = 0;
        spawningMonsters = true;
        for (int i = 0; i < monsterCount; i++) {
            delay += spawnDelayTicks(i);
            spawnTasks.add(new BukkitRunnable() {
                @Override
                public void run() {
                    // Get spawn locations
                    Location ground = getMonsterGroundSpawnLocations()
                            .get(r.nextInt(getMonsterGroundSpawnLocations().size()));
                    Location air = getMonsterAirSpawnLocations()
                            .get(r.nextInt(getMonsterAirSpawnLocations().size()));

                    // Spawn
                    try {
                        addMob(VDMob.of(monsterTypeRatio.get(r.nextInt(monsterTypeRatio.size())),
                                arena, ground, air));
                    } catch (InvalidVDMobKeyException e) {
                        CommunicationManager.debugError("Invalid mob key detected in spawn file!", 1);
                    } catch (Exception ignored) {
                    }
                }
            }.runTaskLater(Main.plugin, delay));
        }
        spawnTasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                spawningMonsters = false;
            }
        }.runTaskLater(Main.plugin, delay));
        // TODO: Spawn bosses

        // Debug message to console
        CommunicationManager.debugInfo("%s started wave %s", 2, getName(),
                Integer.toString(getCurrentWave()));
    }

    private int spawnDelayTicks(int mobNum) {
        Random r = new Random();
        return r.nextInt((int) (60 * Math.pow(Math.E, - mobNum / 60d)));
    }

    private List<String> getTypeRatio(DataManager data, String path) {
        List<String> typeRatio = new ArrayList<>();

        Objects.requireNonNull(data.getConfig().getConfigurationSection(path)).getKeys(false)
                .forEach(type -> {
                    for (int i = 0; i < data.getConfig().getInt(path + "." + type); i++)
                        typeRatio.add(type);
                });
        return typeRatio;
    }

    public void updateScoreboards() {
        getActives().forEach(GameManager::createBoard);
    }

    public void endGame() throws ArenaClosedException, ArenaStatusException {
        // Check if game can end
        if (isClosed())
            throw new ArenaClosedException();
        if (status != ArenaStatus.ACTIVE)
            throw new ArenaStatusException(ArenaStatus.ACTIVE);

        // Clear active tasks and spawning tasks
        activeTasks.forEach((name, task) -> task.cancel());
        activeTasks.clear();
        spawnTasks.forEach(BukkitTask::cancel);
        spawnTasks.clear();

        // Set states to not spawning
        spawningVillagers = false;
        spawningMonsters = false;

        // Set the arena to ending
        setStatus(ArenaStatus.ENDING);

        // Notify players that the game has ended (Title)
        getPlayers().forEach(player ->
                player.getPlayer().sendTitle(CommunicationManager.format("&4&l" +
                                LanguageManager.messages.gameOver), " ", Utils.secondsToTicks(.5),
                        Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));

        // Notify players that the game has ended (Chat)
        getPlayers().forEach(player ->
                PlayerManager.notifyAlert(
                        player.getPlayer(),
                        LanguageManager.messages.end,
                        new ColoredMessage(ChatColor.AQUA, Integer.toString(getCurrentWave() - 1)),
                        new ColoredMessage(ChatColor.AQUA, "10")
                ));

        // Set all players to invincible
        getAlives().forEach(player -> player.getPlayer().setInvulnerable(true));

        // Remove mob AI and set them invincible
        mobs.forEach(mob -> {
            mob.getEntity().setAI(false);
            mob.getEntity().setInvulnerable(true);
        });

        // Play sound if turned on and arena is either not winning or has unlimited waves
        if (hasLoseSound() && (getCurrentWave() <= getMaxWaves() || getMaxWaves() < 0)) {
            for (VDPlayer vdPlayer : getPlayers()) {
                vdPlayer.getPlayer().playSound(getPlayerSpawn().getLocation().clone().add(0, -8, 0),
                        Sound.ENTITY_ENDER_DRAGON_DEATH, 10, .5f);
            }
        }

        // If there are players left
        if (getActiveCount() > 0) {
            // Check for record
            if (checkNewRecord(new ArenaRecord(getCurrentWave() - 1, getActives().stream()
                    .map(vdPlayer -> vdPlayer.getPlayer().getName()).collect(Collectors.toList())))) {
                getPlayers().forEach(player -> player.getPlayer().sendTitle(
                        new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.record).toString(), null,
                        Utils.secondsToTicks(.5), Utils.secondsToTicks(3.5), Utils.secondsToTicks(1)));
                refreshArenaBoard();
            }

            // Give persistent rewards
            getActives().forEach(vdPlayer -> {
                // Calculate reward from difficulty multiplier, wave, kills, and gem balance
                int reward = (5 * getDifficultyMultiplier()) *
                        (Math.max(getCurrentWave() - vdPlayer.getJoinedWave() - 1, 0));
                reward += vdPlayer.getKills();
                reward += (vdPlayer.getGems() + 25) / 50;

                // Calculate challenge bonuses
                int bonus = 0;
                for (Challenge challenge : vdPlayer.getChallenges())
                    bonus += challenge.getBonus();
                bonus = (int) (reward * bonus / 100d);

                // Apply vault economy multiplier, if active
                if (Main.hasCustomEconomy()) {
                    reward = (int) (reward * Main.plugin.getConfig().getDouble("vaultEconomyMult"));
                    bonus = (int) (bonus * Main.plugin.getConfig().getDouble("vaultEconomyMult"));
                }

                // Give rewards and notify
                PlayerManager.depositCrystalBalance(vdPlayer.getID(), reward + bonus);
                PlayerManager.notifySuccess(
                        vdPlayer.getPlayer(),
                        LanguageManager.messages.crystalsEarned,
                        new ColoredMessage(ChatColor.AQUA, String.format("%d (+%d)", reward, bonus)),
                        new ColoredMessage(ChatColor.AQUA, LanguageManager.names.crystals)
                );
            });
        }

        // Reset the arena
        removeTimeLimitBar();
        activeTasks.put(KICK, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                kickPlayers();
            }
        });
        activeTasks.get(KICK).runTaskLater(Main.plugin, Utils.secondsToTicks(10));
        activeTasks.put(RESET, new BukkitRunnable() {
            @Override
            public void run() {
                // Task
                resetGame();
            }
        });
        activeTasks.get(RESET).runTaskLater(Main.plugin, Utils.secondsToTicks(12));

        // Debug message to console
        CommunicationManager.debugInfo("%s is ending.", 2, getName());

        ConfigurationSection limited = Main.getCustomEffects()
                .getConfigurationSection("limited");
        ConfigurationSection unlimited = Main.getCustomEffects()
                .getConfigurationSection("unlimited.onGameEnd");

        // Check for limited waves
        if (limited != null && getMaxWaves() > 0) {
            // Schedule commands to run after win
            if (getCurrentWave() > getMaxWaves())
                limited.getStringList("onGameWin").stream().filter(Objects::nonNull).forEach(command -> getActives()
                        .forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                                Main.plugin,
                                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")),
                                Utils.secondsToTicks(12.5)
                        )));

            // Schedule commands to run after lose
            else limited.getStringList("onGameLose").stream().filter(Objects::nonNull).forEach(command ->
                    getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                            Main.plugin,
                            () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    command.replace("%player%", player.getPlayer().getName())
                                            .replaceFirst("/", "")),
                            Utils.secondsToTicks(12.5)
                    )));
        }

        // Check for unlimited waves
        if (unlimited != null && getMaxWaves() < 0) {
            unlimited.getKeys(false).forEach(key -> {
                    String command = unlimited.getString(key);

                    if (command != null) {
                        // Check upper boundaries
                        if (key.contains("<") && getCurrentWave() < Integer.parseInt(key.substring(1)))
                            getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                                    Main.plugin,
                                    () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            command.replace("%player%", player.getPlayer().getName())
                                                    .replaceFirst("/", "")),
                                    Utils.secondsToTicks(12.5)
                            ));

                        // Check lower boundaries
                        else if (key.contains("^") && getCurrentWave() > Integer.parseInt(key.substring(1)))
                            getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                                    Main.plugin,
                                    () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            command.replace("%player%", player.getPlayer().getName())
                                                    .replaceFirst("/", "")),
                                    Utils.secondsToTicks(12.5)
                            ));

                        // Check range
                        else if (key.contains("-") && getCurrentWave() <= Integer.parseInt(key.split("-")[1]) &&
                                getCurrentWave() >= Integer.parseInt(key.split("-")[0]))
                            getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                                    Main.plugin,
                                    () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                            command.replace("%player%", player.getPlayer().getName())
                                                    .replaceFirst("/", "")),
                                    Utils.secondsToTicks(12.5)
                            ));
                    }
            });
        }
    }

    private void kickPlayers() {
        getPlayers().forEach(player ->
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                        Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player.getPlayer()))));
    }

    private void resetGame() {
        // Clear active tasks
        activeTasks.forEach((name, task) -> task.cancel());
        activeTasks.clear();

        // Update data
        setStatus(ArenaStatus.WAITING);
        resetCurrentWave();
        resetEnemies();
        resetVillagers();
        resetGolems();

        // Clear the arena
        WorldManager.clear(getCorner1(), getCorner2());

        // Remove particles
        cancelSpawnParticles();
        cancelMonsterParticles();
        cancelVillagerParticles();
        cancelBorderParticles();

        // Refresh portal
        refreshPortal();

        // Debug message to console
        CommunicationManager.debugInfo(getName() + " is resetting.", 2);
    }

    public void addMob(VDMob mob) {
        mobs.add(mob);
    }

    public VDMob getMob(UUID id) throws VDMobNotFoundException {
        try {
            return mobs.stream().filter(Objects::nonNull).filter(mob -> mob.getID().equals(id))
                    .collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            throw new VDMobNotFoundException();
        }
    }

    public void removeMob(UUID id) {
        try {
            mobs.remove(getMob(id));
        } catch (VDMobNotFoundException ignored) {
        }
    }

    public ArenaStatus getStatus() {
        return status;
    }

    public void setStatus(ArenaStatus status) {
        this.status = status;
        refreshPortal();
    }

    public boolean isSpawningMonsters() {
        return spawningMonsters;
    }

    public boolean isSpawningVillagers() {
        return spawningVillagers;
    }

    public int getGameID() {
        return gameID;
    }

    public void newGameID() {
        gameID = (int) (100 * Math.random());
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public double getCurrentDifficulty() {
        double difficulty = Math.pow(Math.E, Math.pow(Math.max(currentWave - 1, 0), .35) /
                (4.5 - getDifficultyMultiplier() / 2d));
        if (hasDynamicDifficulty())
            difficulty *= Math.pow(.1 * getActiveCount() + .6, .2);
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

    public int getVillagers() {
        return villagers;
    }

    public void resetVillagers() {
        villagers = 0;
    }

    public int getEnemies() {
        return enemies;
    }

    public void resetEnemies() {
        enemies = 0;
    }

    public int getGolems() {
        return golems;
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
     * @return A list of all {@link VDPlayer} in this arena.
     */
    public List<VDPlayer> getPlayers() {
        return players;
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
     * A function to get the corresponding {@link VDPlayer} in the arena for a given {@link UUID}.
     * @param id The {@link UUID} in question.
     * @return The corresponding {@link VDPlayer}.
     * @throws PlayerNotFoundException Thrown when the arena doesn't have a corresponding {@link VDPlayer}.
     */
    public @NotNull VDPlayer getPlayer(UUID id) throws PlayerNotFoundException {
        try {
            return players.stream().filter(Objects::nonNull).filter(p -> p.getID().equals(id))
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

    public Inventory getWeaponShop() {
        return weaponShop;
    }

    public void setWeaponShop(Inventory weaponShop) {
        this.weaponShop = weaponShop;
    }

    public Inventory getArmorShop() {
        return armorShop;
    }

    public void setArmorShop(Inventory armorShop) {
        this.armorShop = armorShop;
    }

    public Inventory getConsumeShop() {
        return consumeShop;
    }

    public void setConsumeShop(Inventory consumeShop) {
        this.consumeShop = consumeShop;
    }

    public Inventory getCommunityChest() {
        return communityChest;
    }

    public void setCommunityChest(Inventory communityChest) {
        this.communityChest = communityChest;
    }

    public BossBar getTimeLimitBar() {
        return timeLimitBar;
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
    private void removeTimeLimitBar() {
        if (timeLimitBar != null) {
            players.forEach(vdPlayer -> timeLimitBar.removePlayer(vdPlayer.getPlayer()));
            timeLimitBar = null;
        }
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
                .filter(entity -> entity.hasMetadata(VDMob.VD))
                .filter(entity -> entity.getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue()))
                .forEach(entity -> entity.setGlowing(true));
    }

    /**
     * Checks and closes an arena if the arena does not meet opening requirements. Opens arena if autoOpen is on.
     */
    public void checkClose() {
        if (!config.contains("lobby") || getPortalLocation() == null || getPlayerSpawn() == null ||
                getMonsterSpawns().isEmpty() || getVillagerSpawns().isEmpty() || getCorner1() == null ||
                getCorner2() == null || !Objects.equals(getCorner1().getWorld(), getCorner2().getWorld()) ||
                Main.isOutdated()) {
            setClosed(true);
            CommunicationManager.debugInfo(
                    String.format("%s did not meet opening requirements and was closed.", getName()),
                    2
            );
        }

        else if (Main.plugin.getConfig().getBoolean("autoOpen")) {
            setClosed(false);
            CommunicationManager.debugInfo(
                    String.format("%s met opening requirements and was opened.", getName()),
                    2
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
                .filter(entity -> entity.hasMetadata(VDMob.VD))
                .filter(entity -> entity instanceof Monster || entity instanceof Slime || entity instanceof Hoglin ||
                        entity instanceof Phantom).count();
        villagers = (int) getPlayerSpawn().getLocation().getWorld().getNearbyEntities(getBounds()).stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.hasMetadata(VDMob.VD)).filter(entity -> entity instanceof Villager).count();
        golems = (int) getPlayerSpawn().getLocation().getWorld().getNearbyEntities(getBounds()).stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.hasMetadata(VDMob.VD)).filter(entity -> entity instanceof IronGolem).count();
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
        updateScoreboards();

        // Trigger game end if all villagers are gone
        if (this.villagers <= 0 && status == ArenaStatus.ACTIVE && !isSpawningVillagers()) {
            try {
                endGame();
                return;
            } catch (ArenaException ignored) {
            }
        }

        // Trigger wave end if all monsters are gone and no more are spawning
        if (enemies <= 0 && !isSpawningMonsters())
            try {
                endWave();
            } catch (ArenaException ignored) {
            }
    }

    /**
     * Copies permanent arena characteristics from an existing arena and saves the change to the arena file.
     * @param arenaToCopy The arena to copy characteristics from.
     */
    public void copy(Arena arenaToCopy) {
        setMaxPlayers(arenaToCopy.getMaxPlayers());
        setMinPlayers(arenaToCopy.getMinPlayers());
        setVillagerType(arenaToCopy.getVillagerType());
        setMaxWaves(arenaToCopy.getMaxWaves());
        setWaveTimeLimit(arenaToCopy.getWaveTimeLimit());
        setDifficultyMultiplier(arenaToCopy.getDifficultyMultiplier());
        setDynamicCount(arenaToCopy.hasDynamicCount());
        setDynamicDifficulty(arenaToCopy.hasDynamicDifficulty());
        setLateArrival(arenaToCopy.hasLateArrival());
        setDynamicLimit(arenaToCopy.hasDynamicLimit());
        setDynamicPrices(arenaToCopy.hasDynamicPrices());
        setDifficultyLabel(arenaToCopy.getDifficultyLabel());
        setBannedKitIDs(arenaToCopy.getBannedKitIDs());
        setCommunity(arenaToCopy.hasCommunity());
        setWinSound(arenaToCopy.hasWinSound());
        setLoseSound(arenaToCopy.hasLoseSound());
        setWaveStartSound(arenaToCopy.hasWaveStartSound());
        setWaveFinishSound(arenaToCopy.hasWaveFinishSound());
        setPlayerDeathSound(arenaToCopy.hasPlayerDeathSound());
        setAbilitySound(arenaToCopy.hasAbilitySound());
        setWaitingSound(arenaToCopy.getWaitingSoundCode());
        setSpawnParticles(arenaToCopy.hasSpawnParticles());
        setMonsterParticles(arenaToCopy.hasMonsterParticles());
        setVillagerParticles(arenaToCopy.hasVillagerParticles());
        setBorderParticles(arenaToCopy.hasBorderParticles());

        CommunicationManager.debugInfo(
                String.format("Copied the characteristics of %s to %s.", arenaToCopy.getName(), getName()),
                2
        );
    }

    /**
     * Removes all data of this arena from the arena file.
     */
    public void remove() {
        wipe();
        config.set(path, null);
        Main.saveArenaData();
        CommunicationManager.debugInfo(String.format("Removing %s.", getName()), 1);
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
