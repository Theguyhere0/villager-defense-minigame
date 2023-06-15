package me.theguyhere.villagerdefense.plugin.arenas;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.DataManager;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.displays.ArenaBoard;
import me.theguyhere.villagerdefense.plugin.displays.Portal;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.game.WorldManager;
import me.theguyhere.villagerdefense.plugin.guis.InventoryID;
import me.theguyhere.villagerdefense.plugin.guis.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.guis.InventoryType;
import me.theguyhere.villagerdefense.plugin.huds.CountdownController;
import me.theguyhere.villagerdefense.plugin.huds.SidebarManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMobNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.minions.VDWitch;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDCat;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDDog;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDHorse;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDPet;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class representing a Villager Defense arena.
 */
public class Arena {
	/**
	 * Arena id.
	 */
	private final int id;
	/**
	 * A variable to more quickly access the file configuration of the arena file.
	 */
	private final FileConfiguration config;
	/**
	 * Common string for all data paths in the arena file.
	 */
	private final String path;

	/**
	 * Collection of active tasks currently running for this arena.
	 */
	private final Map<String, BukkitRunnable> activeTasks = new HashMap<>();
	/**
	 * Collection of mobs managed under this arena.
	 */
	private final List<VDMob> mobs = new ArrayList<>();
	/**
	 * Status of the arena.
	 */
	private ArenaStatus status;
	/**
	 * A collection of spawning tasks for the arena.
	 */
	private final List<BukkitTask> spawnTasks = new ArrayList<>();
	/**
	 * Whether the arena is in the process of spawning monsters.
	 */
	private boolean spawningMonsters;
	/**
	 * Whether the arena is in the process of spawning villagers.
	 */
	private boolean spawningVillagers;
	/**
	 * Current wave of the active game.
	 */
	private int currentWave = 0;
	/**
	 * Villager count.
	 */
	private int villagers = 0;
	/**
	 * Enemy count.
	 */
	private int enemies = 0;
	/**
	 * Maximum enemies in a wave.
	 */
	private int maxEnemies = 0;
	/**
	 * Iron golem count.
	 */
	private int playerParticlesID = 0;
	/**
	 * ID of task managing monster spawn particles.
	 */
	private int monsterParticlesID = 0;
	/**
	 * ID of task managing villager spawn particles.
	 */
	private int villagerParticlesID = 0;
	/**
	 * ID of task managing corner particles.
	 */
	private int cornerParticlesID = 0;
	/**
	 * A list of players in the arena.
	 */
	private final List<VDPlayer> players = new ArrayList<>();
	/**
	 * Community chest inventory.
	 */
	private Inventory communityChest;
	/**
	 * Portal object for the arena.
	 */
	private Portal portal;
	/**
	 * The player spawn for the arena.
	 */
	private ArenaSpawn playerSpawn;
	/**
	 * The monster spawns for the arena.
	 */
	private final List<ArenaSpawn> monsterSpawns = new ArrayList<>();
	/**
	 * The villager spawns for the arena.
	 */
	private final List<ArenaSpawn> villagerSpawns = new ArrayList<>();
	/**
	 * The golems associated with each villager spawn for the arena.
	 */
	private final List<VDGolem> golems = new ArrayList<>();
	/**
	 * Arena scoreboard object for the arena.
	 */
	private ArenaBoard arenaBoard;

	// Task names
	private static final String NOTIFY_WAITING = "notifyWaiting";
	private static final String NOTIFY_INFO = "notifyInfo";
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
	private static final String CALIBRATE = "calibrate";
	private static final String TICK = "Tick";
	private static final String ONE_TICK = "one" + TICK;
	private static final String TWENTY_TICK = "twenty" + TICK;
	private static final String FORTY_TICK = "forty" + TICK;
	private static final String TWO_HUNDRED_TICK = "twoHundred" + TICK;
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
	 *
	 * @return Arena path prefix.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Retrieves the name of the arena from the arena file.
	 *
	 * @return Arena name.
	 */
	public String getName() {
		return config.getString(path + ".name");
	}

	/**
	 * Writes the new name of the arena into the arena file.
	 *
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
				GameController.getArena(name);
				throw new IllegalArenaNameException("Duplicate");
			}
			catch (ArenaNotFoundException ignored) {
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
	 *
	 * @return Arena difficulty label.
	 */
	public String getDifficultyLabel() {
		if (config.contains(path + ".difficultyLabel"))
			return config.getString(path + ".difficultyLabel");
		else return "";
	}

	/**
	 * Writes the new difficulty label of the arena into the arena file.
	 *
	 * @param label New difficulty label.
	 */
	public void setDifficultyLabel(String label) {
		config.set(path + ".difficultyLabel", label);
		Main.saveArenaData();
		refreshPortal();
	}

	/**
	 * Retrieves the maximum player count of the arena from the arena file.
	 *
	 * @return Maximum player count.
	 */
	public int getMaxPlayers() {
		return config.getInt(path + ".max");
	}

	/**
	 * Writes the new maximum player count of the arena into the arena file.
	 *
	 * @param maxPlayers New maximum player count.
	 */
	public void setMaxPlayers(int maxPlayers) {
		config.set(path + ".max", maxPlayers);
		Main.saveArenaData();
	}

	/**
	 * Retrieves the minimum player count of the arena from the arena file.
	 *
	 * @return Minimum player count.
	 */
	public int getMinPlayers() {
		return config.getInt(path + ".min");
	}

	/**
	 * Writes the new minimum player count of the arena into the arena file.
	 *
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
	 * Retrieves the maximum waves of the arena from the arena file.
	 *
	 * @return Maximum waves.
	 */
	public int getMaxWaves() {
		return config.getInt(path + ".maxWaves");
	}

	/**
	 * Writes the new maximum waves of the arena into the arena file.
	 *
	 * @param maxWaves New maximum waves.
	 */
	public void setMaxWaves(int maxWaves) {
		config.set(path + ".maxWaves", maxWaves);
		Main.saveArenaData();
	}

	/**
	 * Retrieves the nominal time limit per wave of the arena from the arena file.
	 *
	 * @return Nominal time limit per wave.
	 */
	public int getWaveTimeLimit() {
		return config.getInt(path + ".waveTimeLimit");
	}

	public double getAdjustedWaveTimeLimit() {
		// Get proper multiplier
		double multiplier = 1 + .05 * ((int) getCurrentDifficulty() - .5);
		if (!hasDynamicLimit())
			multiplier = 1;

		return getWaveTimeLimit() * multiplier;
	}

	/**
	 * Writes the new nominal time limit per wave of the arena into the arena file.
	 *
	 * @param timeLimit New nominal time limit per wave.
	 */
	public void setWaveTimeLimit(int timeLimit) {
		config.set(path + ".waveTimeLimit", timeLimit);
		Main.saveArenaData();
	}

	/**
	 * Retrieves the difficulty multiplier of the arena from the arena file.
	 *
	 * @return Difficulty multiplier.
	 */
	public int getDifficultyMultiplier() {
		return config.getInt(path + ".difficulty");
	}

	/**
	 * Writes the new difficulty multiplier of the arena into the arena file.
	 *
	 * @param multiplier New difficulty multiplier.
	 */
	public void setDifficultyMultiplier(int multiplier) {
		config.set(path + ".difficulty", multiplier);
		Main.saveArenaData();
	}

	/**
	 * Retrieves the waiting music of the arena from the arena file.
	 *
	 * @return Waiting {@link Sound}.
	 */
	public Sound getWaitingSound() {
		switch (Objects.requireNonNull(config.getString(path + ".sounds.waiting"))) {
			case "blocks":
				return Sound.MUSIC_DISC_BLOCKS;
			case "cat":
				return Sound.MUSIC_DISC_CAT;
			case "chirp":
				return Sound.MUSIC_DISC_CHIRP;
			case "far":
				return Sound.MUSIC_DISC_FAR;
			case "mall":
				return Sound.MUSIC_DISC_MALL;
			case "mellohi":
				return Sound.MUSIC_DISC_MELLOHI;
			case "otherside":
				if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1))
					return Sound.valueOf("MUSIC_DISC_OTHERSIDE");
				else return null;
			case "pigstep":
				return Sound.MUSIC_DISC_PIGSTEP;
			case "stal":
				return Sound.MUSIC_DISC_STAL;
			case "strad":
				return Sound.MUSIC_DISC_STRAD;
			case "wait":
				return Sound.MUSIC_DISC_WAIT;
			case "ward":
				return Sound.MUSIC_DISC_WARD;
			default:
				return null;
		}
	}

	/**
	 * Create the button for a given waiting music of the arena from the arena file.
	 *
	 * @return A button for GUIs.
	 */
	@NotNull
	public ItemStack getWaitingSoundButton(String name) {
		String sound = config.getString(path + ".sounds.waiting");
		boolean selected;

		switch (name) {
			case "blocks":
				selected = "blocks".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_BLOCKS,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Blocks")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "cat":
				selected = "cat".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_CAT,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Cat")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "chirp":
				selected = "chirp".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_CHIRP,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Chirp")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "far":
				selected = "far".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_FAR,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Far")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "mall":
				selected = "mall".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_MALL,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Mall")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "mellohi":
				selected = "mellohi".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_MELLOHI,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Mellohi")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "otherside":
				if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1)) {
					selected = "otherside".equals(sound);
					return new ItemStackBuilder(
						Material.valueOf("MUSIC_DISC_OTHERSIDE"),
						CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Otherside")
					)
						.setButtonFlags()
						.setGlowingIfTrue(selected)
						.build();
				}
				else {
					selected = !GameController
						.getValidSounds()
						.contains(sound);
					return new ItemStackBuilder(
						Material.LIGHT_GRAY_CONCRETE,
						CommunicationManager.format((selected ? "&a&l" : "&4&l") + "None")
					)
						.setButtonFlags()
						.setGlowingIfTrue(selected)
						.build();
				}
			case "pigstep":
				selected = "pigstep".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_PIGSTEP,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Pigstep")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "stal":
				selected = "stal".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_STAL,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Stal")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "strad":
				selected = "strad".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_STRAD,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Strad")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "wait":
				selected = "wait".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_WAIT,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Wait")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			case "ward":
				selected = "ward".equals(sound);
				return new ItemStackBuilder(
					Material.MUSIC_DISC_WARD,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "Ward")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
			default:
				selected = !GameController
					.getValidSounds()
					.contains(sound);
				return new ItemStackBuilder(
					Material.LIGHT_GRAY_CONCRETE,
					CommunicationManager.format((selected ? "&a&l" : "&4&l") + "None")
				)
					.setButtonFlags()
					.setGlowingIfTrue(selected)
					.build();
		}
	}

	/**
	 * Retrieves the waiting music title of the arena from the arena file.
	 *
	 * @return Waiting music title.
	 */
	public String getWaitingSoundName() {
		String sound = config.getString(path + ".sounds.waiting");
		if (sound != null && GameController
			.getValidSounds()
			.contains(sound)) {
			return sound
				.substring(0, 1)
				.toUpperCase() + sound.substring(1);
		}
		else return "None";
	}

	/**
	 * Retrieves the waiting music code of the arena into the arena file.
	 *
	 * @return Waiting music code.
	 */
	public String getWaitingSoundCode() {
		return config.getString(path + ".sounds.waiting");
	}

	/**
	 * Writes the new waiting music of the arena into the arena file.
	 *
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
	 *
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
		}
		catch (Exception e) {
			CommunicationManager.debugError(String.format("Invalid location for %s's portal ", getName()),
				CommunicationManager.DebugLevel.NORMAL, !Main.releaseMode, e
			);
			CommunicationManager.debugInfo(
				"Portal location data may be corrupt. If data cannot be manually corrected " +
					"in arenaData.yml, please delete the portal location data for " + getName() + ".",
				CommunicationManager.DebugLevel.NORMAL
			);
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
	 *
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
				Objects.requireNonNull(DataManager.getConfigLocationNoPitch(path + ".arenaBoard")), this);
			arenaBoard.displayForOnline();
		}
		catch (Exception e) {
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
		if (!isClosed() && Main.isLoaded())
			return;

		// Remove particles
		cancelSpawnParticles();

		// Attempt to fetch new player spawn
		try {
			playerSpawn = new ArenaSpawn(
				Objects.requireNonNull(DataManager.getConfigLocation(path + ".spawn")),
				ArenaSpawnType.PLAYER,
				0
			);
		}
		catch (InvalidLocationException | NullPointerException e) {
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
	 *
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
	 *
	 * @return Player spawn location.
	 */
	public Location getWaitingRoom() {
		return DataManager.getConfigLocation(path + ".waiting");
	}

	/**
	 * Writes the new waiting room location of the arena into the arena file.
	 *
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
		DataManager
			.getConfigLocationMap(path + ".monster")
			.forEach((id, location) ->
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
				}
				catch (InvalidLocationException | NullPointerException ignored) {
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
			return monsterSpawns
				.stream()
				.map(ArenaSpawn::getLocation)
				.collect(Collectors.toList());
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
			return monsterSpawns
				.stream()
				.map(ArenaSpawn::getLocation)
				.collect(Collectors.toList());
		else return airs;
	}

	public List<Location> getVillagerSpawnLocations() {
		List<Location> spawns = new ArrayList<>();
		villagerSpawns.forEach(spawn -> spawns.add(spawn.getLocation()));
		return spawns;
	}

	/**
	 * Retrieves a specific monster spawn of the arena.
	 *
	 * @param monsterSpawnID - Monster spawn ID.
	 * @return Monster spawn.
	 */
	public ArenaSpawn getMonsterSpawn(int monsterSpawnID) {
		List<ArenaSpawn> query = monsterSpawns
			.stream()
			.filter(spawn -> spawn.getId() == monsterSpawnID)
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
		return Calculator.nextSmallestUniqueWhole(DataManager
			.getConfigLocationMap(path + ".monster")
			.keySet());
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
		DataManager
			.getConfigLocationMap(path + ".villager")
			.forEach((id, location) ->
			{
				try {
					villagerSpawns.add(new ArenaSpawn(Objects.requireNonNull(location), ArenaSpawnType.VILLAGER, id));
				}
				catch (InvalidLocationException | NullPointerException ignored) {
				}
			});

		// Turn on particles if appropriate
		if (isClosed())
			startVillagerParticles();
	}

	public List<ArenaSpawn> getVillagerSpawns() {
		return villagerSpawns;
	}

	public List<VDGolem> getGolems() {
		return golems;
	}

	/**
	 * Retrieves a specific villager spawn of the arena.
	 *
	 * @param villagerSpawnID - Villager spawn ID.
	 * @return Villager spawn.
	 */
	public ArenaSpawn getVillagerSpawn(int villagerSpawnID) {
		List<ArenaSpawn> query = villagerSpawns
			.stream()
			.filter(spawn -> spawn.getId() == villagerSpawnID)
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
		return Calculator.nextSmallestUniqueWhole(DataManager
			.getConfigLocationMap(path + ".villager")
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

		if (new File(Main.plugin
			.getDataFolder()
			.getPath(), "spawnTables/" + file).exists() ||
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
		Particle spawnParticle = Particle.valueOf(NMSVersion
			.getCurrent()
			.getNmsManager()
			.getSpawnParticleName());

		if (getPlayerSpawn() == null)
			return;

		if (isClosed())
			getPlayerSpawn().turnOnIndicator();

		if (playerParticlesID != 0)
			return;

		playerParticlesID = Bukkit
			.getScheduler()
			.scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
				double var = 0;
				double var2 = 0;
				Location first, second;

				@Override
				public void run() {
					try {
						// Update particle locations
						var += Math.PI / 12;
						var2 -= Math.PI / 12;
						first = getPlayerSpawn()
							.getLocation()
							.clone()
							.add(Math.cos(var), Math.sin(var) + 1,
								Math.sin(var)
							);
						second = getPlayerSpawn()
							.getLocation()
							.clone()
							.add(Math.cos(var2 + Math.PI), Math.sin(var2) + 1,
								Math.sin(var2 + Math.PI)
							);

						// Spawn particles
						Objects
							.requireNonNull(getPlayerSpawn()
								.getLocation()
								.getWorld())
							.spawnParticle(spawnParticle, first, 0);
						getPlayerSpawn()
							.getLocation()
							.getWorld()
							.spawnParticle(spawnParticle, second, 0);
					}
					catch (Exception e) {
						CommunicationManager.debugError(
							String.format("Player spawn particle generation error for %s.", getName()),
							CommunicationManager.DebugLevel.VERBOSE
						);
					}
				}
			}, 0, 2);
	}

	public void cancelSpawnParticles() {
		if (getPlayerSpawn() == null)
			return;

		getPlayerSpawn().turnOffIndicator();

		if (playerParticlesID != 0)
			Bukkit
				.getScheduler()
				.cancelTask(playerParticlesID);
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
		Particle monsterParticle = Particle.valueOf(NMSVersion
			.getCurrent()
			.getNmsManager()
			.getMonsterParticleName());

		if (monsterParticlesID == 0 && !getMonsterSpawns().isEmpty())
			monsterParticlesID = Bukkit
				.getScheduler()
				.scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
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
								first = location
									.clone()
									.add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
								second = location
									.clone()
									.add(Math.cos(var + Math.PI), Math.sin(var) + 1,
										Math.sin(var + Math.PI)
									);

								// Spawn particles
								Objects
									.requireNonNull(location.getWorld())
									.spawnParticle(monsterParticle, first, 0);
								location
									.getWorld()
									.spawnParticle(monsterParticle, second, 0);
							}
							catch (Exception e) {
								CommunicationManager.debugError(
									String.format("Monster particle generation error for %s.", getName()),
									CommunicationManager.DebugLevel.VERBOSE
								);
							}
						});
						init = true;
					}
				}, 0, 2);
	}

	public void cancelMonsterParticles() {
		getMonsterSpawns().forEach(ArenaSpawn::turnOffIndicator);
		if (monsterParticlesID != 0)
			Bukkit
				.getScheduler()
				.cancelTask(monsterParticlesID);
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
		Particle villagerParticle =
			Particle.valueOf(NMSVersion
				.getCurrent()
				.getNmsManager()
				.getVillagerParticleName());

		if (villagerParticlesID == 0 && !getVillagerSpawns().isEmpty())
			villagerParticlesID = Bukkit
				.getScheduler()
				.scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
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
								first = location
									.clone()
									.add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
								second = location
									.clone()
									.add(Math.cos(var + Math.PI), Math.sin(var) + 1,
										Math.sin(var + Math.PI)
									);

								// Spawn particles
								Objects
									.requireNonNull(location.getWorld())
									.spawnParticle(villagerParticle, first, 0);
								location
									.getWorld()
									.spawnParticle(villagerParticle, second, 0);
							}
							catch (Exception e) {
								CommunicationManager.debugError(
									String.format("Villager particle generation error for %s.", getName()),
									CommunicationManager.DebugLevel.VERBOSE
								);
							}
						});
						init = true;
					}
				}, 0, 2);
	}

	public void cancelVillagerParticles() {
		getVillagerSpawns().forEach(ArenaSpawn::turnOffIndicator);
		if (villagerParticlesID != 0)
			Bukkit
				.getScheduler()
				.cancelTask(villagerParticlesID);
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
		Particle borderParticle = Particle.valueOf(NMSVersion
			.getCurrent()
			.getNmsManager()
			.getBorderParticleName());
		Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 2);

		if (cornerParticlesID == 0 && getCorner1() != null && getCorner2() != null)
			cornerParticlesID = Bukkit
				.getScheduler()
				.scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
					World world;
					Location first, second;

					@Override
					public void run() {
						// Spawn particles
						try {
							world = getCorner1().getWorld();

							first = new Location(world, Math.max(getCorner1().getX(), getCorner2().getX()),
								Math.max(getCorner1().getY(), getCorner2().getY()),
								Math.max(getCorner1().getZ(), getCorner2().getZ())
							);
							second = new Location(world, Math.min(getCorner1().getX(), getCorner2().getX()),
								Math.min(getCorner1().getY(), getCorner2().getY()),
								Math.min(getCorner1().getZ(), getCorner2().getZ())
							);

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

						}
						catch (Exception e) {
							CommunicationManager.debugError(
								String.format("Border particle generation error for %s.", getName()),
								CommunicationManager.DebugLevel.NORMAL,
								true,
								e
							);
						}
					}
				}, 0, 20);
	}

	public void cancelBorderParticles() {
		if (cornerParticlesID != 0)
			Bukkit
				.getScheduler()
				.cancelTask(cornerParticlesID);
		cornerParticlesID = 0;
	}

	private void checkClosedParticles() {
		if (isClosed()) {
			startSpawnParticles();
			startMonsterParticles();
			startVillagerParticles();
			startBorderParticles();
		}
		else {
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
		if (getCorner1() == null || getCorner2() == null)
			return;

		Location temp = getCorner1();
		temp.setY(Objects
			.requireNonNull(getCorner1().getWorld())
			.getMaxHeight() + 10);
		setCorner1(temp);
		temp = getCorner2();
		temp.setY(Objects
			.requireNonNull(getCorner2().getWorld())
			.getMinHeight() - 25);
		setCorner2(temp);
	}

	public @NotNull BoundingBox getBounds() {
		if (getCorner1() == null || getCorner2() == null)
			return new BoundingBox();

		return new BoundingBox(getCorner1().getX(), getCorner1().getY(), getCorner1().getZ(),
			getCorner2().getX(), getCorner2().getY(), getCorner2().getZ()
		);
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
				Objects
					.requireNonNull(config.getConfigurationSection(path + ".records"))
					.getKeys(false)
					.forEach(index -> arenaRecords.add(new ArenaRecord(
						config.getInt(path + ".records." + index + ".wave"),
						config.getStringList(path + ".records." + index + ".players")
					)));
			}
			catch (Exception e) {
				CommunicationManager.debugError(
					String.format("Attempted to retrieve arena records for %s but found none.", getName()),
					CommunicationManager.DebugLevel.VERBOSE
				);
			}

		return arenaRecords;
	}

	public List<ArenaRecord> getSortedDescendingRecords() {
		return getArenaRecords()
			.stream()
			.filter(Objects::nonNull)
			.sorted(Comparator
				.comparingInt(ArenaRecord::getWave)
				.reversed())
			.collect(Collectors.toList());
	}

	public boolean checkNewRecord(ArenaRecord record) {
		List<ArenaRecord> records = getArenaRecords();

		// Automatic record
		if (records.size() < 4)
			records.add(record);

			// New record
		else if (records
			.stream()
			.filter(Objects::nonNull)
			.anyMatch(arenaRecord -> arenaRecord.getWave() < record.getWave())) {
			records.sort(Comparator.comparingInt(ArenaRecord::getWave));
			records.set(0, record);
		}

		// No record
		else return false;

		// Save data
		for (int i = 0; i < records.size(); i++) {
			config.set(path + ".records." + i + ".wave", records
				.get(i)
				.getWave());
			config.set(path + ".records." + i + ".players", records
				.get(i)
				.getPlayers());
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

		// Clear active tasks and countdowns
		activeTasks.forEach((name, task) -> task.cancel());
		activeTasks.clear();
		CountdownController.stopCountdown(this);

		// Start repeating notification of waiting
		activeTasks.put(NOTIFY_WAITING, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				players.forEach(player ->
					PlayerManager.notifyAlert(player.getPlayer(), LanguageManager.messages.waitingForPlayers));
				CommunicationManager.debugInfo(
					"%s is currently waiting for players to start.",
					CommunicationManager.DebugLevel.VERBOSE,
					getName()
				);
			}
		});
		activeTasks
			.get(NOTIFY_WAITING)
			.runTaskTimer(Main.plugin, 0,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(1))
			);
	}

	public void addNotifyInfo() throws ArenaClosedException, ArenaStatusException, ArenaTaskException {
		// Check if waiting notifications can start
		if (isClosed())
			throw new ArenaClosedException();
		if (status != ArenaStatus.WAITING)
			throw new ArenaStatusException(ArenaStatus.WAITING);
		if (activeTasks.containsKey(NOTIFY_INFO))
			throw new ArenaTaskException("Arena already started info notifications");

		// Start repeating notification of info
		activeTasks.put(NOTIFY_INFO, new BukkitRunnable() {
			@Override
			public void run() {
				String LIST = "  - ";
				// Task
				players.forEach(player -> {
					PlayerManager.notify(player.getPlayer(), new ColoredMessage(
						ChatColor.DARK_AQUA,
						LanguageManager.messages.quickInfo
					), new ColoredMessage(ChatColor.DARK_GREEN, getName()));
					player
						.getPlayer()
						.sendMessage(new ColoredMessage(ChatColor.GOLD, LIST + (getMaxWaves() < 0 ?
							LanguageManager.messages.noLastWave : String.format(
							LanguageManager.messages.finalWave,
							getMaxWaves()
						))).toString());
					player
						.getPlayer()
						.sendMessage(new ColoredMessage(hasDynamicLimit() ? ChatColor.GREEN :
							ChatColor.RED, String.format(
							LIST + LanguageManager.messages.dynamicTimeLimit,
							hasDynamicLimit() ? LanguageManager.messages.will :
								LanguageManager.messages.willNot
						)).toString());
					player
						.getPlayer()
						.sendMessage(new ColoredMessage(hasLateArrival() ? ChatColor.GREEN :
							ChatColor.RED, String.format(
							LIST + LanguageManager.messages.lateArrival,
							hasLateArrival() ? LanguageManager.messages.will : LanguageManager.messages.willNot
						))
							.toString());
					player
						.getPlayer()
						.sendMessage(new ColoredMessage(
							hasCommunity() ? ChatColor.GREEN : ChatColor.RED,
							String.format(LIST + LanguageManager.messages.communityChest, hasCommunity() ?
								LanguageManager.messages.will : LanguageManager.messages.willNot)
						).toString());
				});
			}
		});
		activeTasks
			.get(NOTIFY_INFO)
			.runTaskTimer(Main.plugin, Calculator.secondsToTicks(30),
				Calculator.secondsToTicks(Calculator.minutesToSeconds(1))
			);
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
		if (getActiveCount() < getMinPlayers())
			throw new ArenaTaskException("Arena must meet the minimum player count to start");

		// Clear active tasks EXCEPT notify info
		Map<String, BukkitRunnable> cache = new HashMap<>();
		activeTasks.forEach((name, task) -> {
			if (!name.equals(NOTIFY_INFO))
				task.cancel();
			else cache.put(name, task);
		});
		activeTasks.clear();
		activeTasks.putAll(cache);

		// Two-minute notice
		players.forEach(player ->
			PlayerManager.notifyAlert(
				player.getPlayer(),
				LanguageManager.messages.minutesLeft,
				new ColoredMessage(ChatColor.AQUA, "2")
			));
		CommunicationManager.debugInfo("%s is starting in %s minutes.", CommunicationManager.DebugLevel.VERBOSE,
			getName(), "2"
		);

		// Schedule one-minute notice
		activeTasks.put(ONE_MINUTE_NOTICE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				players.forEach(player ->
					PlayerManager.notifyAlert(
						player.getPlayer(),
						LanguageManager.messages.minutesLeft,
						new ColoredMessage(ChatColor.AQUA, "1")
					));
				CommunicationManager.debugInfo("%s is starting in %s minute.", CommunicationManager.DebugLevel.VERBOSE,
					getName(), "1"
				);

				// Cleanup
				activeTasks.remove(ONE_MINUTE_NOTICE);
			}
		});
		activeTasks
			.get(ONE_MINUTE_NOTICE)
			.runTaskLater(
				Main.plugin,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(1))
			);

		// Schedule 30-second notice
		activeTasks.put(THIRTY_SECOND_NOTICE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				players.forEach(player ->
					PlayerManager.notifyAlert(
						player.getPlayer(),
						LanguageManager.messages.secondsLeft,
						new ColoredMessage(ChatColor.AQUA, "30")
					));
				CommunicationManager.debugInfo("%s is starting in %s seconds.",
					CommunicationManager.DebugLevel.VERBOSE,
					getName(), "30"
				);

				// Cleanup
				activeTasks.remove(THIRTY_SECOND_NOTICE);
			}
		});
		activeTasks
			.get(THIRTY_SECOND_NOTICE)
			.runTaskLater(
				Main.plugin,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 30)
			);

		// Schedule 10-second notice
		activeTasks.put(TEN_SECOND_NOTICE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				players.forEach(player ->
					PlayerManager.notifyAlert(
						player.getPlayer(),
						LanguageManager.messages.secondsLeft,
						new ColoredMessage(ChatColor.AQUA, "10")
					));
				CommunicationManager.debugInfo("%s is starting in %s seconds.",
					CommunicationManager.DebugLevel.VERBOSE,
					getName(), "10"
				);

				// Cleanup
				activeTasks.remove(TEN_SECOND_NOTICE);
			}
		});
		activeTasks
			.get(TEN_SECOND_NOTICE)
			.runTaskLater(
				Main.plugin,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 10)
			);


		// Schedule 5-second notice
		activeTasks.put(FIVE_SECOND_NOTICE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				players.forEach(player ->
					PlayerManager.notifyAlert(
						player.getPlayer(),
						LanguageManager.messages.secondsLeft,
						new ColoredMessage(ChatColor.AQUA, "5")
					));
				CommunicationManager.debugInfo("%s is starting in %s seconds.",
					CommunicationManager.DebugLevel.VERBOSE,
					getName(), "5"
				);

				// Cleanup
				activeTasks.remove(FIVE_SECOND_NOTICE);
			}
		});
		activeTasks
			.get(FIVE_SECOND_NOTICE)
			.runTaskLater(
				Main.plugin,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 5)
			);

		// Schedule start of arena
		activeTasks.put(START_ARENA, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				try {
					startGame();
				}
				catch (ArenaException e) {
					CommunicationManager.debugErrorShouldNotHappen();
				}

				// Cleanup
				activeTasks.remove(START_ARENA);
			}
		});
		activeTasks
			.get(START_ARENA)
			.runTaskLater(
				Main.plugin,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2))
			);

		// Start countdown bar
		CountdownController.stopCountdown(this);
		CountdownController.startWaitingCountdown(this);
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
		players.forEach(player -> {
			PlayerManager.notifyAlert(player.getPlayer(), LanguageManager.messages.maxCapacity);
			PlayerManager.notifyAlert(
				player.getPlayer(),
				LanguageManager.messages.secondsLeft,
				new ColoredMessage(ChatColor.AQUA, "10")
			);
		});
		CommunicationManager.debugInfo("%s is starting in %s seconds.", CommunicationManager.DebugLevel.VERBOSE,
			getName(), "10"
		);

		// Schedule forced 5-second notice
		activeTasks.put(FORCE_FIVE_SECOND_NOTICE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				players.forEach(player ->
					PlayerManager.notifyAlert(
						player.getPlayer(),
						LanguageManager.messages.secondsLeft,
						new ColoredMessage(ChatColor.AQUA, "5")
					));
				CommunicationManager.debugInfo("%s is starting in %s seconds.",
					CommunicationManager.DebugLevel.VERBOSE,
					getName(), "5"
				);

				// Cleanup
				activeTasks.remove(FORCE_FIVE_SECOND_NOTICE);
			}
		});
		activeTasks
			.get(FORCE_FIVE_SECOND_NOTICE)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(5));

		// Schedule forced start of arena
		activeTasks.put(FORCE_START_ARENA, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				try {
					startGame();
				}
				catch (ArenaException e) {
					CommunicationManager.debugErrorShouldNotHappen();
				}

				// Cleanup
				activeTasks.remove(FORCE_START_ARENA);
			}
		});
		activeTasks
			.get(FORCE_START_ARENA)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(10));

		// Start countdown bar
		CountdownController.stopCountdown(this);
		CountdownController.startExpeditedWaitingCountdown(this);
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

		// Clear countdown bar
		CountdownController.stopCountdown(this);

		// Reset villager and enemy count, clear arena
		resetVillagers();
		resetEnemies();
		WorldManager.clear(getCorner1(), getCorner2());

		// Teleport players to arena, and spectators if there was a waiting room
		if (getWaitingRoom() != null)
			for (VDPlayer player : getSpectators())
				PlayerManager.teleSpectator(player.getPlayer(), getPlayerSpawn().getLocation());
		for (VDPlayer vdPlayer : getActives())
			PlayerManager.teleAdventure(vdPlayer.getPlayer(), getPlayerSpawn().getLocation());

		// Set arena status to active
		setStatus(ArenaStatus.ACTIVE);

		// Stop waiting sound
		if (getWaitingSound() != null)
			players.forEach(player ->
				player
					.getPlayer()
					.stopSound(getWaitingSound()));

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
			// Give all players starting items and set up attributes
			player.giveItems();
			player.setupAttributes(true);

			// Give Traders their gems
			if (Kit
				.trader()
				.setKitLevel(1)
				.equals(player.getKit()))
				player.addGems(200);

			// Give Summoners their dogs
			if (Kit
				.summoner()
				.setKitLevel(1)
				.equals(player.getKit()))
				player.addPet(new VDDog(this, player
					.getPlayer()
					.getLocation(), player, 1));
			if (Kit
				.summoner()
				.setKitLevel(2)
				.equals(player.getKit())) {
				player.addPet(new VDDog(this, player
					.getPlayer()
					.getLocation(), player, 1));
				player.addPet(new VDDog(this, player
					.getPlayer()
					.getLocation(), player, 1));
			}
			if (Kit
				.summoner()
				.setKitLevel(3)
				.equals(player.getKit()))
				player.addPet(new VDHorse(this, player
					.getPlayer()
					.getLocation(), player, 1));

			// Give gems from crystal conversion
			int amount;
			if (Main.hasCustomEconomy())
				amount = player.getGemBoost() * Math.max((int)
					(5 * Main.plugin
						.getConfig()
						.getDouble("vaultEconomyMult")), 1);
			else amount = player.getGemBoost() * 5;
			player.addGems(player.getGemBoost());
			PlayerManager.withdrawCrystalBalance(player.getID(), amount);
		});
		updateScoreboards();

		// Initiate community chest
		setCommunityChest(Bukkit.createInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.COMMUNITY_CHEST_INVENTORY, InventoryType.CONTROLLED)
				.setArena(this)
				.setClickEnabled()
				.setDragEnabled()
				.build(),
			54,
			CommunicationManager.format("&d&l" + LanguageManager.names.communityChest)
		));

		// Start dialogue, then trigger WaveEndEvent
		for (VDPlayer player : players) {
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
				for (VDPlayer player : players) {
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
		activeTasks
			.get(DIALOGUE_TWO)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(5));
		activeTasks.put(DIALOGUE_THREE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				for (VDPlayer player : players) {
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
		activeTasks
			.get(DIALOGUE_THREE)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(11));
		activeTasks.put(DIALOGUE_FOUR, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				for (VDPlayer player : players) {
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
		activeTasks
			.get(DIALOGUE_FOUR)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(18));
		activeTasks.put(DIALOGUE_FIVE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				for (VDPlayer player : players) {
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
		activeTasks
			.get(DIALOGUE_FIVE)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(25));
		activeTasks.put(END_WAVE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				try {
					endWave();
				}
				catch (ArenaException e) {
					CommunicationManager.debugErrorShouldNotHappen();
				}

				// Cleanup
				activeTasks.remove(END_WAVE);
			}
		});
		activeTasks
			.get(END_WAVE)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(30));

		// Schedule updates
		activeTasks.put(ONE_TICK, new BukkitRunnable() {
			@Override
			public void run() {
				// Update mob targets
				mobs.forEach(mob -> {
					Mob mobster = mob.getEntity();
					Location location = mobster.getLocation();
					int range = mob.getTargetRange();
					List<Entity> nearby = Objects
						.requireNonNull(location.getWorld())
						.getNearbyEntities(getBounds(), entity -> range < 0 ||
							mobster
								.getLocation()
								.distance(entity.getLocation()) <= range)
						.stream()
						.filter(entity -> entity instanceof LivingEntity)
						.filter(entity -> !entity.isDead())
						.filter(entity -> ((LivingEntity) entity)
							.getActivePotionEffects()
							.stream()
							.noneMatch(potion -> potion
								.getType()
								.equals(PotionEffectType.INVISIBILITY)))
						.filter(entity -> VDMob.isTeam(mobster, IndividualTeam.MONSTER)
							&& entity instanceof Player || !(entity instanceof Player) &&
							!VDMob.areSameTeam(mobster, entity))
						.filter(entity -> {
							if (entity instanceof Player)
								return ((Player) entity).getGameMode() == GameMode.ADVENTURE;
							else return true;
						})
						.filter(mobster::hasLineOfSight)
						.sorted((e1, e2) -> (int) (mobster
							.getLocation()
							.distance(e1.getLocation()) -
							mobster
								.getLocation()
								.distance(e2.getLocation())))
						.collect(Collectors.toList());
					List<Entity> priority = nearby
						.stream()
						.filter(mob
							.getTargetPriority()
							.getTest())
						.sorted((e1, e2) -> (int) (mobster
							.getLocation()
							.distance(e1.getLocation()) -
							mobster
								.getLocation()
								.distance(e2.getLocation())))
						.collect(Collectors.toList());
					LivingEntity oldTarget = mobster.getTarget();
					LivingEntity newTarget = priority.isEmpty() ?
						(nearby.isEmpty() ? null : (LivingEntity) nearby.get(0)) : (LivingEntity) priority.get(0);
					if (!(oldTarget == null && newTarget == null) && oldTarget == null || newTarget == null ||
						!oldTarget
							.getUniqueId()
							.equals(newTarget.getUniqueId())) {
						mobster.setTarget(newTarget);
					}
				});

				// Refill ammo
				getActives().forEach(VDPlayer::refill);
			}
		});
		activeTasks
			.get(ONE_TICK)
			.runTaskTimer(Main.plugin, 0, 1);
		activeTasks.put(TWENTY_TICK, new BukkitRunnable() {
			@Override
			public void run() {
				// Heal
				getActives().forEach(VDPlayer::heal);
				getActives().forEach(player -> player
					.getPets()
					.forEach(VDPet::heal));
				golems.forEach(VDGolem::heal);
			}
		});
		activeTasks
			.get(TWENTY_TICK)
			.runTaskTimer(Main.plugin, 0, 20);
		activeTasks.put(FORTY_TICK, new BukkitRunnable() {
			@Override
			public void run() {
				// Make witch throw potion
				mobs.forEach(mob -> {
					Mob mobster = mob.getEntity();
					LivingEntity target = mobster.getTarget();

					if (mob instanceof VDWitch && target != null &&
						target
							.getLocation()
							.distance(mobster.getLocation()) <= 10) {
						mobster.launchProjectile(
							ThrownPotion.class,
							target
								.getLocation()
								.subtract(mobster.getLocation())
								.toVector()
								.normalize()
						);
					}
				});
			}
		});
		activeTasks
			.get(FORTY_TICK)
			.runTaskTimer(Main.plugin, 0, 40);
		activeTasks.put(TWO_HUNDRED_TICK, new BukkitRunnable() {
			@Override
			public void run() {
				// Cat heal
				getActives().forEach(player -> {
					AtomicInteger heal = new AtomicInteger();
					player
						.getPets()
						.forEach(pet -> {
							if (pet instanceof VDCat)
								heal.addAndGet(VDCat.getHeal(pet.getLevel()));
						});
					player.changeCurrentHealth(heal.get());
					player
						.getPets()
						.forEach(pet -> pet.heal(heal.get()));
				});
			}
		});
		activeTasks
			.get(TWO_HUNDRED_TICK)
			.runTaskTimer(Main.plugin, 0, 200);

		// Debug message to console
		CommunicationManager.debugInfo("%s is starting.", CommunicationManager.DebugLevel.VERBOSE, getName());
	}

	private void endWave() throws ArenaClosedException, ArenaStatusException {
		// Check if wave can end
		if (isClosed())
			throw new ArenaClosedException();
		if (status != ArenaStatus.ACTIVE)
			throw new ArenaStatusException(ArenaStatus.ACTIVE);

		// Clear active tasks EXCEPT custom game ticking
		Map<String, BukkitRunnable> cache = new HashMap<>();
		activeTasks.forEach((name, task) -> {
			if (!name.contains(TICK))
				task.cancel();
			else cache.put(name, task);
		});
		activeTasks.clear();
		activeTasks.putAll(cache);

		// Stop wave time limit countdown
		CountdownController.stopCountdown(this);

		// Play wave end sound if not just starting
		if (hasWaveFinishSound() && getCurrentWave() != 0)
			for (VDPlayer vdPlayer : players) {
				vdPlayer
					.getPlayer()
					.playSound(getPlayerSpawn()
							.getLocation()
							.clone()
							.add(0, -8, 0),
						Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, .75f
					);
			}

		// Update player stats
		for (VDPlayer active : getActives())
			if (PlayerManager.getTopWave(active.getID()) < getCurrentWave())
				PlayerManager.setTopWave(active.getID(), getCurrentWave());

		ConfigurationSection limited = Main
			.getCustomEffects()
			.getConfigurationSection("limited.onWaveComplete");
		ConfigurationSection unlimited = Main
			.getCustomEffects()
			.getConfigurationSection("unlimited.onWaveComplete");

		// Check custom effects for limited wave arenas
		if (getMaxWaves() > 0 && limited != null)
			limited
				.getKeys(false)
				.forEach(key -> {
					try {
						String command = limited.getString(key);
						if (getCurrentWave() == Integer.parseInt(key) && command != null)
							getActives().forEach(player ->
								Bukkit.dispatchCommand(
									Bukkit.getConsoleSender(),
									command
										.replace("%player%", player
											.getPlayer()
											.getName())
										.replaceFirst("/", "")
								));
					}
					catch (Exception ignored) {
					}
				});

		// Check custom effects for unlimited wave arenas
		if (getMaxWaves() < 0 && unlimited != null)
			unlimited
				.getKeys(false)
				.forEach(key -> {
					try {
						String command = unlimited.getString(key);
						if (getCurrentWave() == Integer.parseInt(key) && command != null)
							getActives().forEach(player ->
								Bukkit.dispatchCommand(
									Bukkit.getConsoleSender(),
									command
										.replace("%player%", player
											.getPlayer()
											.getName())
										.replaceFirst("/", "")
								));
					}
					catch (Exception ignored) {
					}
				});

		// Debug message to console
		CommunicationManager.debugInfo("%s completed wave %s", CommunicationManager.DebugLevel.VERBOSE, getName(),
			Integer.toString(getCurrentWave())
		);

		// Refresh the scoreboards
		updateScoreboards();

		// Increment wave and reset max enemies
		incrementCurrentWave();
		maxEnemies = 0;

		// Win condition
		if (getCurrentWave() == getMaxWaves()) {
			endGame();
			if (hasWinSound()) {
				for (VDPlayer vdPlayer : players) {
					vdPlayer
						.getPlayer()
						.playSound(getPlayerSpawn()
								.getLocation()
								.clone()
								.add(0, -8, 0),
							Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1
						);
				}
			}
			return;
		}

		mobs.removeIf(mob -> VDMob.isTeam(mob.getEntity(), IndividualTeam.MONSTER));

		// Revive dead players
		for (VDPlayer p : getGhosts()) {
			PlayerManager.teleAdventure(p.getPlayer(), getPlayerSpawn().getLocation());
			p.setStatus(VDPlayer.Status.ALIVE);
			p.giveItems();
			p.setupAttributes(false);
		}

		// Revive dead golems
		for (int i = 0; i < golems.size(); i++) {
			if (golems
				.get(i)
				.getEntity()
				.isDead()) {
				addMob(golems
					.get(i)
					.respawn(this, getVillagerSpawns()
						.get(i)
						.getLocation()));
			}
		}

		getActives().forEach(p -> {
			// Notify of upcoming wave
			if (currentWave != 1)
				p
					.getPlayer()
					.sendTitle(CommunicationManager.format(
							"&6" + LanguageManager.messages.waveNum,
							Integer.toString(currentWave)
						),
						CommunicationManager.format(
							"&7" + LanguageManager.messages.starting,
							"&b15&7"
						),
						Calculator.secondsToTicks(.5), Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
					);
			else p
				.getPlayer()
				.sendTitle(CommunicationManager.format(
						"&6" + LanguageManager.messages.waveNum,
						Integer.toString(currentWave)
					),
					" ", Calculator.secondsToTicks(.5), Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
				);

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
			SidebarManager.updateActivePlayerSidebar(p);

			// Revive pets
			p.respawnPets();
		});

		// Notify spectators of upcoming wave
		if (currentWave != 1)
			getSpectators().forEach(p ->
				p
					.getPlayer()
					.sendTitle(CommunicationManager.format(
							"&6" + LanguageManager.messages.waveNum,
							Integer.toString(currentWave)
						),
						CommunicationManager.format(
							"&7" + LanguageManager.messages.starting,
							"&b15&7"
						),
						Calculator.secondsToTicks(.5), Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
					));
		else getSpectators().forEach(p ->
			p
				.getPlayer()
				.sendTitle(CommunicationManager.format(
						"&6" + LanguageManager.messages.waveNum,
						Integer.toString(currentWave)
					),
					" ", Calculator.secondsToTicks(.5), Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
				));

		// Start wave after 15 seconds if not first wave
		if (currentWave != 1) {
			activeTasks.put(START_WAVE, new BukkitRunnable() {
				@Override
				public void run() {
					// Task
					try {
						startWave();
					}
					catch (ArenaException ignored) {
					}
				}
			});
			activeTasks
				.get(START_WAVE)
				.runTaskLater(Main.plugin, Calculator.secondsToTicks(15));
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
			for (VDPlayer vdPlayer : players) {
				vdPlayer
					.getPlayer()
					.playSound(getPlayerSpawn()
							.getLocation()
							.clone()
							.add(0, -8, 0),
						Sound.ENTITY_ENDER_DRAGON_GROWL, 10, .25f
					);
			}
		}

		// Start wave count down
		if (getWaveTimeLimit() != -1) {
			CountdownController.startWaveTimeLimitCountdown(this);
		}

		// Schedule and record calibration
		activeTasks.put(CALIBRATE, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				calibrate();
			}
		});
		activeTasks
			.get(CALIBRATE)
			.runTaskTimer(Main.plugin, 0, Calculator.secondsToTicks(0.25));

		// Schedule spawning sequences
		spawnTasks.addAll(ArenaSpawnGenerator.generateVillagerSpawnSequence(this));
		spawnTasks.addAll(ArenaSpawnGenerator.generateMinionSpawnSequence(this));

		// Debug message to console
		CommunicationManager.debugInfo("%s started wave %s", CommunicationManager.DebugLevel.VERBOSE, getName(),
			Integer.toString(getCurrentWave())
		);
	}

	public void updateScoreboards() {
		getActives().forEach(SidebarManager::updateActivePlayerSidebar);
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
		players.forEach(player ->
			player
				.getPlayer()
				.sendTitle(CommunicationManager.format("&4&l" +
						LanguageManager.messages.gameOver), " ", Calculator.secondsToTicks(.5),
					Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
				));

		// Notify players that the game has ended (Chat)
		players.forEach(player ->
			PlayerManager.notifyAlert(
				player.getPlayer(),
				LanguageManager.messages.end,
				new ColoredMessage(ChatColor.AQUA, Integer.toString(Math.max(0, getCurrentWave() - 1))),
				new ColoredMessage(ChatColor.AQUA, "10")
			));

		// Set all players to invincible
		getAlives().forEach(player -> player
			.getPlayer()
			.setInvulnerable(true));

		// Remove mob AI and set them invincible
		mobs.forEach(mob -> {
			mob
				.getEntity()
				.setAware(false);
			mob
				.getEntity()
				.setInvulnerable(true);
		});

		// Play sound if turned on and arena is either not winning or has unlimited waves
		if (hasLoseSound() && (getCurrentWave() <= getMaxWaves() || getMaxWaves() < 0)) {
			for (VDPlayer vdPlayer : players) {
				vdPlayer
					.getPlayer()
					.playSound(getPlayerSpawn()
							.getLocation()
							.clone()
							.add(0, -8, 0),
						Sound.ENTITY_ENDER_DRAGON_DEATH, 10, .5f
					);
			}
		}

		// If there are players left
		if (getActiveCount() > 0) {
			// Check for record
			if (checkNewRecord(new ArenaRecord(getCurrentWave() - 1, getActives()
				.stream()
				.map(vdPlayer -> vdPlayer
					.getPlayer()
					.getName())
				.collect(Collectors.toList())))) {
				players.forEach(player -> player
					.getPlayer()
					.sendTitle(
						new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.record).toString(), null,
						Calculator.secondsToTicks(.5), Calculator.secondsToTicks(3.5), Calculator.secondsToTicks(1)
					));
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
					reward = (int) (reward * Main.plugin
						.getConfig()
						.getDouble("vaultEconomyMult"));
					bonus = (int) (bonus * Main.plugin
						.getConfig()
						.getDouble("vaultEconomyMult"));
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
		CountdownController.stopCountdown(this);
		activeTasks.put(KICK, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				kickPlayers();
			}
		});
		activeTasks
			.get(KICK)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(10));
		activeTasks.put(RESET, new BukkitRunnable() {
			@Override
			public void run() {
				// Task
				resetGame();
			}
		});
		activeTasks
			.get(RESET)
			.runTaskLater(Main.plugin, Calculator.secondsToTicks(12));

		// Debug message to console
		CommunicationManager.debugInfo("%s is ending.", CommunicationManager.DebugLevel.VERBOSE, getName());

		ConfigurationSection limited = Main
			.getCustomEffects()
			.getConfigurationSection("limited");
		ConfigurationSection unlimitedBefore = Main
			.getCustomEffects()
			.getConfigurationSection("unlimited.onGameEnd");
		ConfigurationSection unlimitedAfter = Main
			.getCustomEffects()
			.getConfigurationSection("unlimited.onGameEndLobby");

		// Check for limited waves
		if (limited != null && getMaxWaves() > 0) {
			// Schedule commands to run after win
			if (getCurrentWave() > getMaxWaves()) {
				limited
					.getStringList("onGameWin")
					.stream()
					.filter(Objects::nonNull)
					.forEach(command -> getActives()
						.forEach(player -> Bukkit.dispatchCommand(
							Bukkit.getConsoleSender(),
							command
								.replace("%player%", player
									.getPlayer()
									.getName())
								.replaceFirst("/", "")
						)));
				limited
					.getStringList("onGameWinLobby")
					.stream()
					.filter(Objects::nonNull)
					.forEach(command -> getActives()
						.forEach(player -> Bukkit
							.getScheduler()
							.scheduleSyncDelayedTask(
								Main.plugin,
								() -> Bukkit.dispatchCommand(
									Bukkit.getConsoleSender(),
									command
										.replace("%player%", player
											.getPlayer()
											.getName())
										.replaceFirst("/", "")
								),
								Calculator.secondsToTicks(12.5)
							)));
			}

			// Schedule commands to run after lose
			else {
				limited
					.getStringList("onGameLose")
					.stream()
					.filter(Objects::nonNull)
					.forEach(command ->
						getActives().forEach(player -> Bukkit.dispatchCommand(
							Bukkit.getConsoleSender(),
							command
								.replace("%player%", player
									.getPlayer()
									.getName())
								.replaceFirst("/", "")
						)));
				limited
					.getStringList("onGameLoseLobby")
					.stream()
					.filter(Objects::nonNull)
					.forEach(command ->
						getActives().forEach(player -> Bukkit
							.getScheduler()
							.scheduleSyncDelayedTask(
								Main.plugin,
								() -> Bukkit.dispatchCommand(
									Bukkit.getConsoleSender(),
									command
										.replace("%player%", player
											.getPlayer()
											.getName())
										.replaceFirst("/", "")
								),
								Calculator.secondsToTicks(12.5)
							)));
			}
		}

		// Check for unlimited waves
		if (unlimitedBefore != null && getMaxWaves() < 0) {
			unlimitedBefore
				.getKeys(false)
				.forEach(key -> {
					String command = unlimitedBefore.getString(key);

					if (command != null) {
						// Check upper boundaries
						if (key.contains("<") && getCurrentWave() < Integer.parseInt(key.substring(1)))
							getActives().forEach(player -> Bukkit.dispatchCommand(
								Bukkit.getConsoleSender(),
								command
									.replace("%player%", player
										.getPlayer()
										.getName())
									.replaceFirst("/", "")
							));

							// Check lower boundaries
						else if (key.contains("^") && getCurrentWave() > Integer.parseInt(key.substring(1)))
							getActives().forEach(player -> Bukkit.dispatchCommand(
								Bukkit.getConsoleSender(),
								command
									.replace("%player%", player
										.getPlayer()
										.getName())
									.replaceFirst("/", "")
							));

							// Check range
						else if (key.contains("-") && getCurrentWave() <= Integer.parseInt(key.split("-")[1]) &&
							getCurrentWave() >= Integer.parseInt(key.split("-")[0]))
							getActives().forEach(player -> Bukkit.dispatchCommand(
								Bukkit.getConsoleSender(),
								command
									.replace("%player%", player
										.getPlayer()
										.getName())
									.replaceFirst("/", "")
							));
					}
				});
		}
		if (unlimitedAfter != null && getMaxWaves() < 0) {
			unlimitedAfter
				.getKeys(false)
				.forEach(key -> {
					String command = unlimitedAfter.getString(key);

					if (command != null) {
						// Check upper boundaries
						if (key.contains("<") && getCurrentWave() < Integer.parseInt(key.substring(1)))
							getActives().forEach(player -> Bukkit
								.getScheduler()
								.scheduleSyncDelayedTask(
									Main.plugin,
									() -> Bukkit.dispatchCommand(
										Bukkit.getConsoleSender(),
										command
											.replace("%player%", player
												.getPlayer()
												.getName())
											.replaceFirst("/", "")
									),
									Calculator.secondsToTicks(12.5)
								));

							// Check lower boundaries
						else if (key.contains("^") && getCurrentWave() > Integer.parseInt(key.substring(1)))
							getActives().forEach(player -> Bukkit
								.getScheduler()
								.scheduleSyncDelayedTask(
									Main.plugin,
									() -> Bukkit.dispatchCommand(
										Bukkit.getConsoleSender(),
										command
											.replace("%player%", player
												.getPlayer()
												.getName())
											.replaceFirst("/", "")
									),
									Calculator.secondsToTicks(12.5)
								));

							// Check range
						else if (key.contains("-") && getCurrentWave() <= Integer.parseInt(key.split("-")[1]) &&
							getCurrentWave() >= Integer.parseInt(key.split("-")[0]))
							getActives().forEach(player -> Bukkit
								.getScheduler()
								.scheduleSyncDelayedTask(
									Main.plugin,
									() -> Bukkit.dispatchCommand(
										Bukkit.getConsoleSender(),
										command
											.replace("%player%", player
												.getPlayer()
												.getName())
											.replaceFirst("/", "")
									),
									Calculator.secondsToTicks(12.5)
								));
					}
				});
		}
	}

	public void kickPlayers() {
		players.forEach(player ->
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(Main.plugin, () ->
					Bukkit
						.getPluginManager()
						.callEvent(new LeaveArenaEvent(player.getPlayer()))));
	}

	public void resetGame() {
		// Clear active tasks
		activeTasks.forEach((name, task) -> task.cancel());
		activeTasks.clear();

		// Update data
		setStatus(ArenaStatus.WAITING);
		resetCurrentWave();
		resetEnemies();
		resetVillagers();

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
		CommunicationManager.debugInfo(getName() + " is resetting.", CommunicationManager.DebugLevel.VERBOSE);
	}

	public void addMob(VDMob mob) {
		mobs.add(mob);
	}

	public void addGolem(VDGolem golem) {
		golems.add(golem);
		addMob(golem);
	}

	public VDMob getMob(UUID id) throws VDMobNotFoundException {
		try {
			return mobs
				.stream()
				.filter(Objects::nonNull)
				.filter(mob -> mob
					.getID()
					.equals(id))
				.collect(Collectors.toList())
				.get(0);
		}
		catch (Exception e) {
			throw new VDMobNotFoundException();
		}
	}

	public void removeMob(UUID id) {
		try {
			mobs.remove(getMob(id));
		}
		catch (VDMobNotFoundException ignored) {
		}
	}

	public ArenaStatus getStatus() {
		return status;
	}

	public void setStatus(ArenaStatus status) {
		this.status = status;
		refreshPortal();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isSpawningMonsters() {
		return spawningMonsters;
	}

	public void setSpawningMonsters(boolean spawningMonsters) {
		this.spawningMonsters = spawningMonsters;
	}

	public boolean isSpawningVillagers() {
		return spawningVillagers;
	}

	public void setSpawningVillagers(boolean spawningVillagers) {
		this.spawningVillagers = spawningVillagers;
	}

	public int getCurrentWave() {
		return currentWave;
	}

	public double getCurrentDifficulty() {
		return Math.pow(Math.E, Math.pow(Math.max(currentWave - 1, 0), .4) /
			(4.5 - getDifficultyMultiplier() / 2d));
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

	public int getMaxEnemies() {
		return maxEnemies;
	}

	public void setMaxEnemies(int maxEnemies) {
		this.maxEnemies = maxEnemies;
	}

	// Modify the price of an item
	@NotNull
	public ItemStack modifyPrice(ItemStack itemStack) {
		// Set price modifier
		double modifier = Math.pow(getActiveCount() - 1, 2) / 250 + 1;

		// Get current price
		ItemStack item = itemStack.clone();
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		List<String> lore = Objects.requireNonNull(meta.getLore());
		Integer price = meta
			.getPersistentDataContainer()
			.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);

		// Check for price
		if (price == null)
			return itemStack;

		// Modify
		price = Calculator.roundToNearest(price * modifier, 5);
		meta
			.getPersistentDataContainer()
			.set(VDItem.PRICE_KEY, PersistentDataType.INTEGER, price);
		lore.set(lore.size() - 1, CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
			price));
		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * @return A list of all {@link VDPlayer}s in this arena that aren't of the {@link VDPlayer.Status} LEFT.
	 */
	public List<VDPlayer> getPlayers() {
		return players;
	}

	/**
	 * @return A list of all {@link Player}s in this arena that aren't of the {@link VDPlayer.Status} LEFT.
	 */
	public List<Player> getVanillaPlayers() {
		return players
			.stream()
			.map(VDPlayer::getPlayer)
			.collect(Collectors.toList());
	}

	/**
	 * @return A list of {@link VDPlayer}s of the {@link VDPlayer.Status} ALIVE.
	 */
	public List<VDPlayer> getAlives() {
		return players
			.stream()
			.filter(Objects::nonNull)
			.filter(p -> p.getStatus() == VDPlayer.Status.ALIVE)
			.collect(Collectors.toList());
	}

	/**
	 * @return A list of {@link VDPlayer}s of the {@link VDPlayer.Status} GHOST.
	 */
	public List<VDPlayer> getGhosts() {
		return players
			.stream()
			.filter(Objects::nonNull)
			.filter(p -> p.getStatus() == VDPlayer.Status.GHOST)
			.collect(Collectors.toList());
	}

	/**
	 * @return A list of {@link VDPlayer}s of the {@link VDPlayer.Status} SPECTATOR.
	 */
	public List<VDPlayer> getSpectators() {
		return players
			.stream()
			.filter(Objects::nonNull)
			.filter(p -> p.getStatus() == VDPlayer.Status.SPECTATOR)
			.collect(Collectors.toList());
	}

	/**
	 * @return A list of {@link VDPlayer}s of the {@link VDPlayer.Status} ALIVE or GHOST.
	 */
	public List<VDPlayer> getActives() {
		return Stream
			.concat(getAlives().stream(), getGhosts().stream())
			.collect(Collectors.toList());
	}

	/**
	 * A function to get the corresponding {@link VDPlayer} in the arena for a given {@link Player}.
	 *
	 * @param player The {@link Player} in question.
	 * @return The corresponding {@link VDPlayer}.
	 * @throws PlayerNotFoundException Thrown when the arena doesn't have a corresponding {@link VDPlayer}.
	 */
	public @NotNull VDPlayer getPlayer(Player player) throws PlayerNotFoundException {
		try {
			return players
				.stream()
				.filter(Objects::nonNull)
				.filter(p -> p
					.getID()
					.equals(player.getUniqueId()))
				.collect(Collectors.toList())
				.get(0);
		}
		catch (Exception e) {
			throw new PlayerNotFoundException("Player not in this arena.");
		}
	}

	/**
	 * A function to get the corresponding {@link VDPlayer} in the arena for a given {@link UUID}.
	 *
	 * @param id The {@link UUID} in question.
	 * @return The corresponding {@link VDPlayer}.
	 * @throws PlayerNotFoundException Thrown when the arena doesn't have a corresponding {@link VDPlayer}.
	 */
	public @NotNull VDPlayer getPlayer(UUID id) throws PlayerNotFoundException {
		try {
			return players
				.stream()
				.filter(Objects::nonNull)
				.filter(p -> p
					.getID()
					.equals(id))
				.collect(Collectors.toList())
				.get(0);
		}
		catch (Exception e) {
			throw new PlayerNotFoundException("Player not in this arena.");
		}
	}

	/**
	 * Checks whether there is a corresponding {@link VDPlayer} for a given {@link Player}.
	 *
	 * @param player The {@link Player} in question.
	 * @return Whether a corresponding {@link VDPlayer} was found.
	 */
	public boolean hasPlayer(Player player) {
		try {
			return players
				.stream()
				.filter(Objects::nonNull)
				.anyMatch(p -> p
					.getID()
					.equals(player.getUniqueId()));
		}
		catch (Exception e) {
			return false;
		}
	}

	public void removePlayer(VDPlayer player) {
		players.remove(player);
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

	public Inventory getCommunityChest() {
		return communityChest;
	}

	public void setCommunityChest(Inventory communityChest) {
		this.communityChest = communityChest;
	}

	/**
	 * Sets remaining monsters glowing.
	 */
	public void setMonsterGlow() {
		Objects
			.requireNonNull(getPlayerSpawn()
				.getLocation()
				.getWorld())
			.getNearbyEntities(getBounds())
			.stream()
			.filter(Objects::nonNull)
			.filter(VDMob::isVDMob)
			.filter(entity -> VDMob.isTeam(entity, IndividualTeam.MONSTER))
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
				CommunicationManager.DebugLevel.VERBOSE
			);
		}

		else if (Main.plugin
			.getConfig()
			.getBoolean("autoOpen")) {
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
	public int effectShareCount(Kit.EffectType effectType) {
		Kit effectKit;

		switch (effectType) {
			case BLACKSMITH:
				effectKit = Kit
					.blacksmith()
					.setKitLevel(1);
				break;
			case WITCH:
				effectKit = Kit
					.witch()
					.setKitLevel(1);
				break;
			case MERCHANT:
				effectKit = Kit
					.merchant()
					.setKitLevel(1);
				break;
			case VAMPIRE:
				effectKit = Kit
					.vampire()
					.setKitLevel(1);
				break;
			case GIANT1:
				effectKit = Kit
					.giant()
					.setKitLevel(1);
				break;
			case GIANT2:
				effectKit = Kit
					.giant()
					.setKitLevel(2);
				break;
			case TRAINER1:
				effectKit = Kit
					.trainer()
					.setKitLevel(1);
				break;
			case TRAINER2:
				effectKit = Kit
					.trainer()
					.setKitLevel(2);
				break;
			default:
				effectKit = Kit.none();
		}

		return (int) getActives()
			.stream()
			.filter(VDPlayer::isSharing)
			.filter(player ->
				effectKit.equals(player.getKit()))
			.count();
	}

	/**
	 * Checks mobs within its boundaries to make sure mob counts are accurate.
	 */
	public void calibrate() {
		int monsters;
		int villagers;

		// Get accurate numbers
		monsters = (int) Objects
			.requireNonNull(getPlayerSpawn()
				.getLocation()
				.getWorld())
			.getNearbyEntities(getBounds())
			.stream()
			.filter(Objects::nonNull)
			.filter(VDMob::isVDMob)
			.filter(entity -> VDMob.isTeam(entity, IndividualTeam.MONSTER))
			.count();
		villagers = (int) getPlayerSpawn()
			.getLocation()
			.getWorld()
			.getNearbyEntities(getBounds())
			.stream()
			.filter(Objects::nonNull)
			.filter(VDMob::isVDMob)
			.filter(entity -> entity instanceof Villager)
			.count();
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
			}
			catch (ArenaException ignored) {
			}
		}

		// Trigger wave end if all monsters are gone and no more are spawning
		if (enemies <= 0 && !isSpawningMonsters())
			try {
				endWave();
			}
			catch (ArenaException ignored) {
			}
	}

	/**
	 * Copies permanent arena characteristics from an existing arena and saves the change to the arena file.
	 *
	 * @param arenaToCopy The arena to copy characteristics from.
	 */
	public void copy(Arena arenaToCopy) {
		setMaxPlayers(arenaToCopy.getMaxPlayers());
		setMinPlayers(arenaToCopy.getMinPlayers());
		setVillagerType(arenaToCopy.getVillagerType());
		setMaxWaves(arenaToCopy.getMaxWaves());
		setWaveTimeLimit(arenaToCopy.getWaveTimeLimit());
		setDifficultyMultiplier(arenaToCopy.getDifficultyMultiplier());
		setLateArrival(arenaToCopy.hasLateArrival());
		setDynamicLimit(arenaToCopy.hasDynamicLimit());
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
		CommunicationManager.debugInfo(
			String.format("Removing %s.", getName()),
			CommunicationManager.DebugLevel.NORMAL
		);
	}

	/**
	 * Removes all trace of the arena's physical existence.
	 */
	public void wipe() {
		// Kick players
		players.forEach(vdPlayer -> Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.plugin, () ->
				Bukkit
					.getPluginManager()
					.callEvent(new LeaveArenaEvent(vdPlayer.getPlayer()))));

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
