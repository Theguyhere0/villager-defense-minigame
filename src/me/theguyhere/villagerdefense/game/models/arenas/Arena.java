package me.theguyhere.villagerdefense.game.models.arenas;

import me.theguyhere.villagerdefense.GUI.InventoryItems;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.GameEndEvent;
import me.theguyhere.villagerdefense.events.WaveEndEvent;
import me.theguyhere.villagerdefense.game.models.InventoryMeta;
import me.theguyhere.villagerdefense.game.models.Tasks;
import me.theguyhere.villagerdefense.game.models.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class managing data about a Villager Defense arena.
 */
public class Arena {
    /** Instance of the plugin.*/
    private final Main plugin;
    /** Arena number.*/
    private final int arena;
    /** A variable more quickly access the file configuration of the arena file.*/
    private final FileConfiguration config;
    /** Common string for all data paths in the arena file.*/
    private final String path;
    private final Tasks task; // The tasks object for the arena

    /** Caps lock flag for the arena's naming inventory.*/
    private boolean caps;
    /** Status of the arena.*/
    private ArenaStatus status;
    /** Whether the arena is in the process of spawning monsters.*/
    private boolean spawningMonsters;
    /** Whether the arena is in the process of spawning villagers.*/
    private boolean spawningVillagers;
    /** Current wave of the active game.*/
    private int currentWave;
    /** Villager count.*/
    private int villagers;
    /** Enemy count.*/
    private int enemies;
    /** Iron golem count.*/
    private int golems;
    /** ID of task managing player spawn particles.*/
    private int playerParticlesID = 0;
    /** ID of task managing monster spawn particles.*/
    private int monsterParticlesID = 0;
    /** ID of task managing villager spawn particles.*/
    private int villagerParticlesID = 0;
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

    public Arena(Main plugin, int arena, Tasks task) {
        this.plugin = plugin;
        config = plugin.getArenaData();
        this.arena = arena;
        path = "a" + arena;
        this.task = task;
        currentWave = 0;
        villagers = 0;
        enemies = 0;
        status = ArenaStatus.WAITING;
    }

    public int getArena() {
        return arena;
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
    public void setName(String name) {
        config.set(path + ".name", name);
        plugin.saveArenaData();
    }

    /**
     * Retrieves the difficulty label of the arena from the arena file.
     * @return Arena difficulty label.
     */
    public String getDifficultyLabel() {
        if (config.contains(path + ".difficultyLabel"))
            return config.getString(path + ".difficultyLabel");
        else return null;
    }

    /**
     * Writes the new difficulty label of the arena into the arena file.
     * @param label New difficulty label.
     */
    public void setDifficultyLabel(String label) {
        config.set(path + ".difficultyLabel", label);
        plugin.saveArenaData();
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
        plugin.saveArenaData();
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
        plugin.saveArenaData();
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
        plugin.saveArenaData();
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
        plugin.saveArenaData();
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
        plugin.saveArenaData();
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
        plugin.saveArenaData();
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
        plugin.saveArenaData();
    }

    /**
     * Retrieves the waiting music of the arena from the arena file.
     * @return Waiting {@link Sound}.
     */
    public Sound getWaitingSound() {
        switch (config.getInt(path + ".sounds.waiting")) {
            case 0:
                return Sound.MUSIC_DISC_CAT;
            case 1:
                return Sound.MUSIC_DISC_BLOCKS;
            case 2:
                return Sound.MUSIC_DISC_FAR;
            case 3:
                return Sound.MUSIC_DISC_STRAD;
            case 4:
                return Sound.MUSIC_DISC_MELLOHI;
            case 5:
                return Sound.MUSIC_DISC_WARD;
            case 9:
                return Sound.MUSIC_DISC_CHIRP;
            case 10:
                return Sound.MUSIC_DISC_STAL;
            case 11:
                return Sound.MUSIC_DISC_MALL;
            case 12:
                return Sound.MUSIC_DISC_WAIT;
            case 13:
                return Sound.MUSIC_DISC_PIGSTEP;
            default:
                return null;
        }
    }

    /**
     * Create the button for a given waiting music of the arena from the arena file.
     * @return A button for GUIs.
     */
    public ItemStack getWaitingSoundButton(int number) {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        int sound = config.getInt(path + ".sounds.waiting");
        boolean selected;

        switch (number) {
            case 0:
                 selected = sound == 0;
                return Utils.createItem(Material.MUSIC_DISC_CAT,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Cat"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 1:
                selected = sound == 1;
                return Utils.createItem(Material.MUSIC_DISC_BLOCKS,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Blocks"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 2:
                selected = sound == 2;
                return Utils.createItem(Material.MUSIC_DISC_FAR,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Far"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 3:
                selected = sound == 3;
                return Utils.createItem(Material.MUSIC_DISC_STRAD,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Strad"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 4:
                selected = sound == 4;
                return Utils.createItem(Material.MUSIC_DISC_MELLOHI,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Mellohi"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 5:
                selected = sound == 5;
                return Utils.createItem(Material.MUSIC_DISC_WARD,
                        Utils.format(((selected ? "&a&l" : "&4&l") + "Ward")),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 9:
                selected = sound == 9;
                return Utils.createItem(Material.MUSIC_DISC_CHIRP,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Chirp"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 10:
                selected = sound == 10;
                return Utils.createItem(Material.MUSIC_DISC_STAL,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Stal"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 11:
                selected = sound == 11;
                return Utils.createItem(Material.MUSIC_DISC_MALL,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Mall"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 12:
                selected = sound == 12;
                return Utils.createItem(Material.MUSIC_DISC_WAIT,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Wait"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            case 13:
                selected = sound == 13;
                return Utils.createItem(Material.MUSIC_DISC_PIGSTEP,
                        Utils.format((selected ? "&a&l" : "&4&l") + "Pigstep"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
            default:
                selected = sound < 0 || sound > 5 && sound < 9 || sound > 13;
                return Utils.createItem(Material.LIGHT_GRAY_CONCRETE,
                        Utils.format((selected ? "&a&l" : "&4&l") + "None"),
                        Utils.BUTTON_FLAGS, selected ? enchants : null);
        }
    }

    /**
     * Retrieves the waiting music title of the arena into the arena file.
     * @return Waiting music title.
     */
    public String getWaitingSoundName() {
        switch (config.getInt(path + ".sounds.waiting")) {
            case 0:
                return "Cat";
            case 1:
                return "Blocks";
            case 2:
                return "Far";
            case 3:
                return "Strad";
            case 4:
                return "Mellohi";
            case 5:
                return "Ward";
            case 9:
                return "Chirp";
            case 10:
                return "Stal";
            case 11:
                return "Mall";
            case 12:
                return "Wait";
            case 13:
                return "Pigstep";
            default:
                return "None";
        }
    }

    /**
     * Retrieves the waiting music numerical representation of the arena into the arena file.
     * @return Waiting music numerical representation.
     */
    public int getWaitingSoundNum() {
        return config.getInt(path + ".sounds.waiting");
    }

    /**
     * Writes the new waiting music of the arena into the arena file.
     * @param sound Numerical representation of the new waiting music.
     */
    public void setWaitingSound(int sound) {
        config.set(path + ".sounds.waiting", sound);
        plugin.saveArenaData();
    }

    /**
     * Retrieves the arena portal location from the arena file.
     * @return Arena portal location.
     */
    public Location getPortal() {
        return Utils.getConfigLocationNoPitch(plugin, "portal." + arena);
    }

    /**
     * Writes the new arena portal location into the arena file.
     * @param location New arena portal location.
     */
    public void setPortal(Location location) {
        Utils.setConfigurationLocation(plugin, "portal." + arena, location);
        plugin.saveArenaData();
    }

    /**
     * Centers the arena portal location along the x and z axis.
     */
    public void centerPortal() {
        Utils.centerConfigLocation(plugin, "portal." + arena);
    }

    /**
     * Retrieves the arena leaderboard location from the arena file.
     * @return Arena leaderboard location.
     */
    public Location getArenaBoard() {
        return Utils.getConfigLocationNoRotation(plugin, "arenaBoard." + arena);
    }

    /**
     * Writes the new arena leaderboard location into the arena file.
     * @param location New arena leaderboard location.
     */
    public void setArenaBoard(Location location) {
        Utils.setConfigurationLocation(plugin, "arenaBoard." + arena, location);
        plugin.saveArenaData();
    }

    /**
     * Centers the arena leaderboard location along the x and z axis.
     */
    public void centerArenaBoard() {
        Utils.centerConfigLocation(plugin, "arenaBoard." + arena);
    }

    /**
     * Retrieves the player spawn location of the arena from the arena file.
     * @return Player spawn location.
     */
    public Location getPlayerSpawn() {
        return Utils.getConfigLocation(plugin, path + ".spawn");
    }

    /**
     * Writes the new player spawn location of the arena into the arena file.
     * @param location New player spawn location.
     */
    public void setPlayerSpawn(Location location) {
        Utils.setConfigurationLocation(plugin, path + ".spawn", location);
        plugin.saveArenaData();
    }

    /**
     * Centers the player spawn location of the arena along the x and z axis.
     */
    public void centerPlayerSpawn() {
        Utils.centerConfigLocation(plugin, path + ".spawn");
    }

    /**
     * Retrieves the waiting room location of the arena from the arena file.
     * @return Player spawn location.
     */
    public Location getWaitingRoom() {
        return Utils.getConfigLocation(plugin, path + ".waiting");
    }

    /**
     * Writes the new waiting room location of the arena into the arena file.
     * @param location New player spawn location.
     */
    public void setWaitingRoom(Location location) {
        Utils.setConfigurationLocation(plugin, path + ".waiting", location);
        plugin.saveArenaData();
    }

    /**
     * Centers the waiting room location of the arena along the x and z axis.
     */
    public void centerWaitingRoom() {
        Utils.centerConfigLocation(plugin, path + ".waiting");
    }

    /**
     * Retrieves a list of monster spawn locations of the arena from the arena file.
     * @return List of monster spawns.
     */
    public List<Location> getMonsterSpawns() {
        return Utils.getConfigLocationList(plugin, path + ".monster").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Retrieves a specific monster spawn location of the arena from the arena file.
     * @param num Monster spawn number.
     * @return Monster spawn location.
     */
    public Location getMonsterSpawn(int num) {
        return Utils.getConfigLocationNoRotation(plugin, path + ".monster." + num);
    }

    public void setMonsterSpawn(int num, Location location) {
        Utils.setConfigurationLocation(plugin, path + ".monster." + num, location);
        plugin.saveArenaData();
    }

    public void centerMonsterSpawn(int num) {
        Utils.centerConfigLocation(plugin, path + ".monster." + num);
    }

    public void setMonsterSpawnType(int num, int type) {
        config.set(path + ".monsters." + num + ".type", type);
        plugin.saveArenaData();
    }

    public int getMonsterSpawnType(int num) {
        return config.getInt(path + ".monsters." + num + ".type");
    }

    public List<Location> getVillagerSpawns() {
        return Utils.getConfigLocationList(plugin, path + ".villager").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Location getVillagerSpawn(int num) {
        return Utils.getConfigLocationNoRotation(plugin, path + ".villager." + num);
    }

    public void setVillagerSpawn(int num, Location location) {
        Utils.setConfigurationLocation(plugin, path + ".villager." + num, location);
        plugin.saveArenaData();
    }

    public void centerVillagerSpawn(int num) {
        Utils.centerConfigLocation(plugin, path + ".villager." + num);
    }

    public List<String> getBannedKits() {
        return config.getStringList(path + ".bannedKits");
    }

    public void setBannedKits(List<String> bannedKits) {
        config.set(path + ".bannedKits", bannedKits);
        plugin.saveArenaData();
    }

    public String getSpawnTableFile() {
        if (!config.contains(path + ".spawnTable"))
            setSpawnTableFile("default");
        return config.getString(path + ".spawnTable");
    }

    public boolean setSpawnTableFile(String option) {
        String file = option + ".yml";
        if (option.equals("custom"))
            file = "a" + arena + ".yml";

        if (new File(plugin.getDataFolder().getPath(), "spawnTables/" + file).exists() ||
                option.equals("default")) {
            config.set(path + ".spawnTable", option);
            plugin.saveArenaData();
            return true;
        }

        return false;
    }

    public boolean hasSpawnParticles() {
        return config.getBoolean(path + ".particles.spawn");
    }

    public void setSpawnParticles(boolean bool) {
        config.set(path + ".particles.spawn", bool);
        plugin.saveArenaData();
    }

    public void startSpawnParticles() {
        if (playerParticlesID == 0)
            playerParticlesID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                double var2 = 0;
                Location first, second;

                @Override
                public void run() {
                    try {
                        // Update particle locations
                        var += Math.PI / 12;
                        var2 -= Math.PI / 12;
                        first = getPlayerSpawn().clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                        second = getPlayerSpawn().clone().add(Math.cos(var2 + Math.PI), Math.sin(var2) + 1,
                                Math.sin(var2 + Math.PI));

                        // Spawn particles
                        getPlayerSpawn().getWorld().spawnParticle(Particle.FLAME, first, 0);
                        getPlayerSpawn().getWorld().spawnParticle(Particle.FLAME, second, 0);
                    } catch (Exception e) {
                        plugin.debugError(String.format("Player spawn particle generation error for arena %d.", arena),
                                2);
                    }
                }
            }, 0 , 2);
    }

    public void cancelSpawnParticles() {
        if (playerParticlesID != 0)
            Bukkit.getScheduler().cancelTask(playerParticlesID);
        playerParticlesID = 0;
    }

    public boolean hasMonsterParticles() {
        return config.getBoolean(path + ".particles.monster");
    }

    public void setMonsterParticles(boolean bool) {
        config.set(path + ".particles.monster", bool);
        plugin.saveArenaData();
    }

    public void startMonsterParticles() {
        if (monsterParticlesID == 0)
            monsterParticlesID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                Location first, second;

                @Override
                public void run() {
                    var -= Math.PI / 12;
                    getMonsterSpawns().forEach(location -> {
                        try {
                            // Update particle locations
                            first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                            second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                    Math.sin(var + Math.PI));

                            // Spawn particles
                            location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, first, 0);
                            location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, second, 0);
                        } catch (Exception e) {
                            plugin.debugError(String.format("Monster particle generation error for arena %d.", arena),
                                    2);
                        }
                    });
                }
            }, 0 , 2);
    }

    public void cancelMonsterParticles() {
        if (monsterParticlesID != 0)
            Bukkit.getScheduler().cancelTask(monsterParticlesID);
        monsterParticlesID = 0;
    }

    public boolean hasVillagerParticles() {
        return config.getBoolean(path + ".particles.villager");
    }

    public void setVillagerParticles(boolean bool) {
        config.set(path + ".particles.villager", bool);
        plugin.saveArenaData();
    }

    public void startVillagerParticles() {
        if (villagerParticlesID == 0)
            villagerParticlesID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                Location first, second;

                @Override
                public void run() {
                    var += Math.PI / 12;
                    getVillagerSpawns().forEach(location -> {
                        try {
                            // Update particle locations
                            first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                            second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                    Math.sin(var + Math.PI));

                            // Spawn particles
                            location.getWorld().spawnParticle(Particle.COMPOSTER, first, 0);
                            location.getWorld().spawnParticle(Particle.COMPOSTER, second, 0);
                        } catch (Exception e) {
                            plugin.debugError(String.format("Villager particle generation error for arena %d.", arena),
                                    2);
                        }
                    });
                }
            }, 0 , 2);
    }

    public void cancelVillagerParticles() {
        if (villagerParticlesID != 0)
            Bukkit.getScheduler().cancelTask(villagerParticlesID);
        villagerParticlesID = 0;
    }

    public boolean hasNormal() {
        return config.getBoolean(path + ".normal");
    }

    public void setNormal(boolean normal) {
        config.set(path + ".normal", normal);
        plugin.saveArenaData();
    }

    public boolean hasCustom() {
        return config.getBoolean(path + ".custom");
    }

    public void setCustom(boolean bool) {
        config.set(path + ".custom", bool);
        plugin.saveArenaData();
    }

    public boolean hasCommunity() {
        return config.getBoolean(path + ".community");
    }

    public void setCommunity(boolean bool) {
        config.set(path + ".community", bool);
        plugin.saveArenaData();
    }

    public boolean hasGemDrop() {
        return config.getBoolean(path + ".gemDrop");
    }

    public void setGemDrop(boolean bool) {
        config.set(path + ".gemDrop", bool);
        plugin.saveArenaData();
    }

    public boolean hasExpDrop() {
        return config.getBoolean(path + ".expDrop");
    }

    public void setExpDrop(boolean bool) {
        config.set(path + ".expDrop", bool);
        plugin.saveArenaData();
    }

    public Location getCorner1() {
        return Utils.getConfigLocationNoRotation(plugin, path + ".corner1");
    }

    public void setCorner1(Location location) {
        Utils.setConfigurationLocation(plugin, path + ".corner1", location);
    }

    public Location getCorner2() {
        return Utils.getConfigLocationNoRotation(plugin, path + ".corner2");
    }

    public void setCorner2(Location location) {
        Utils.setConfigurationLocation(plugin, path + ".corner2", location);
    }

    public boolean hasWinSound() {
        return config.getBoolean(path + ".sounds.win");
    }

    public void setWinSound(boolean bool) {
        config.set(path + ".sounds.win", bool);
        plugin.saveArenaData();
    }

    public boolean hasLoseSound() {
        return config.getBoolean(path + ".sounds.lose");
    }

    public void setLoseSound(boolean bool) {
        config.set(path + ".sounds.lose", bool);
        plugin.saveArenaData();
    }

    public boolean hasWaveStartSound() {
        return config.getBoolean(path + ".sounds.start");
    }

    public void setWaveStartSound(boolean bool) {
        config.set(path + ".sounds.start", bool);
        plugin.saveArenaData();
    }

    public boolean hasWaveFinishSound() {
        return config.getBoolean(path + ".sounds.end");
    }

    public void setWaveFinishSound(boolean bool) {
        config.set(path + ".sounds.end", bool);
        plugin.saveArenaData();
    }

    public boolean hasGemSound() {
        return config.getBoolean(path + ".sounds.gem");
    }

    public void setGemSound(boolean bool) {
        config.set(path + ".sounds.gem", bool);
        plugin.saveArenaData();
    }

    public boolean hasPlayerDeathSound() {
        return config.getBoolean(path + ".sounds.death");
    }

    public void setPlayerDeathSound(boolean bool) {
        config.set(path + ".sounds.death", bool);
        plugin.saveArenaData();
    }

    public boolean hasAbilitySound() {
        return config.getBoolean(path + ".sounds.ability");
    }

    public void setAbilitySound(boolean bool) {
        config.set(path + ".sounds.ability", bool);
        plugin.saveArenaData();
    }

    public boolean hasDynamicCount() {
        return config.getBoolean(path + ".dynamicCount");
    }

    public void setDynamicCount(boolean bool) {
        config.set(path + ".dynamicCount", bool);
        plugin.saveArenaData();
    }

    public boolean hasDynamicDifficulty() {
        return config.getBoolean(path + ".dynamicDifficulty");
    }

    public void setDynamicDifficulty(boolean bool) {
        config.set(path + ".dynamicDifficulty", bool);
        plugin.saveArenaData();
    }

    public boolean hasDynamicPrices() {
        return config.getBoolean(path + ".dynamicPrices");
    }

    public void setDynamicPrices(boolean bool) {
        config.set(path + ".dynamicPrices", bool);
        plugin.saveArenaData();
    }

    public boolean hasDynamicLimit() {
        return config.getBoolean(path + ".dynamicLimit");
    }

    public void setDynamicLimit(boolean bool) {
        config.set(path + ".dynamicLimit", bool);
        plugin.saveArenaData();
    }

    public boolean hasLateArrival() {
        return config.getBoolean(path + ".lateArrival");
    }

    public void setLateArrival(boolean bool) {
        config.set(path + ".lateArrival", bool);
        plugin.saveArenaData();
    }

    public boolean isClosed() {
        return config.getBoolean(path + ".closed");
    }

    public void setClosed(boolean closed) {
        config.set(path + ".closed", closed);
        plugin.saveArenaData();
    }

    public List<ArenaRecord> getArenaRecords() {
        List<ArenaRecord> arenaRecords = new ArrayList<>();
        if (config.contains(path + ".records"))
            try {
                config.getConfigurationSection(path + ".records").getKeys(false)
                        .forEach(index -> arenaRecords.add(new ArenaRecord(
                                config.getInt(path + ".records." + index + ".wave"),
                                config.getStringList(path + ".records." + index + ".players")
                        )));
            } catch (Exception e) {
                plugin.debugError(
                        String.format("Attempted to retrieve arena records for arena %d but found none.", arena),
                        2);
            }

        return arenaRecords;
    }

    public List<ArenaRecord> getSortedDescendingRecords() {
        return getArenaRecords().stream().sorted(Comparator.comparingInt(ArenaRecord::getWave).reversed())
                .collect(Collectors.toList());
    }

    public boolean checkNewRecord(ArenaRecord record) {
        List<ArenaRecord> records = getArenaRecords();

        // Automatic record
        if (records.size() < 4)
            records.add(record);

        // New record
        else if (records.stream().anyMatch(arenaRecord -> arenaRecord.getWave() < record.getWave())) {
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
        plugin.saveArenaData();
        return true;
    }

    public Tasks getTask() {
        return task;
    }

    public boolean isCaps() {
        return caps;
    }

    public void flipCaps() {
        caps = !caps;
    }

    public ArenaStatus getStatus() {
        return status;
    }

    public void setStatus(ArenaStatus status) {
        this.status = status;
    }

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
        double difficulty = Math.pow(Math.E, Math.pow(currentWave - 1, .55) / (5 - getDifficultyMultiplier() / 2d));
        if (hasDynamicDifficulty())
            difficulty *= Math.sqrt(.1 * getActiveCount() + .6);
        return difficulty;
    }

    public void incrementCurrentWave() {
        currentWave++;
    }

    public void resetCurrentWave() {
        currentWave = 0;
    }

    public int getVillagers() {
        return villagers;
    }

    public void incrementVillagers() {
        villagers++;
    }

    public void decrementVillagers() {
        if (--villagers <= 0 && status == ArenaStatus.ACTIVE && !spawningVillagers)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(this)));
    }

    public void resetVillagers() {
        villagers = 0;
    }

    public int getEnemies() {
        return enemies;
    }

    public void incrementEnemies() {
        enemies++;
    }

    public void decrementEnemies() {
        if (--enemies <= 0 && status == ArenaStatus.ACTIVE && !spawningMonsters)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new WaveEndEvent(this)));
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
        return players.stream().filter(p -> p.getStatus() == PlayerStatus.ALIVE).collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link PlayerStatus} GHOST.
     */
    public List<VDPlayer> getGhosts() {
        return players.stream().filter(p -> p.getStatus() == PlayerStatus.GHOST).collect(Collectors.toList());
    }

    /**
     * @return A list of {@link VDPlayer} of the {@link PlayerStatus} SPECTATOR.
     */
    public List<VDPlayer> getSpectators() {
        return players.stream().filter(p -> p.getStatus() == PlayerStatus.SPECTATOR).collect(Collectors.toList());
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
    public VDPlayer getPlayer(Player player) throws PlayerNotFoundException {
        try {
            return players.stream().filter(p -> p.getID().equals(player.getUniqueId())).collect(Collectors.toList())
                    .get(0);
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
            return players.stream().anyMatch(p -> p.getID().equals(player.getUniqueId()));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasPlayer(VDPlayer player) {
        return players.stream().anyMatch(p -> p.equals(player));
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

    public Inventory getCustomShopEditor() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop Editor: " + getName()));

        // Set exit option
        for (int i = 45; i < 54; i++)
            inv.setItem(i, InventoryItems.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        try {
            config.getConfigurationSection(path + ".customShop").getKeys(false)
                    .forEach(index -> {
                        try {
                            // Get raw item and data
                            ItemStack item = config.getItemStack(path + ".customShop." + index).clone();
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                            int price = Integer.parseInt(meta.getDisplayName().substring(meta.getDisplayName().length() - 5));

                            // Transform to proper shop item
                            meta.setDisplayName(Utils.format("&f" + name));
                            if (meta.hasLore())
                                lore = meta.getLore();
                            lore.add(Utils.format("&2Gems: &a" + price));
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            // Set item into inventory
                            inv.setItem(Integer.parseInt(index), item);
                        } catch (Exception e) {
                            plugin.debugError(
                                    String.format(
                                            "An error occurred retrieving an item from arena %d's custom shop.", arena),
                                    2);
                        }
                    });
        } catch (Exception e) {
            plugin.debugError(
                    String.format("Attempted to retrieve the custom shop inventory of arena %d but found none.", arena),
                    1);
        }

        return inv;
    }

    public Inventory getCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop"));

        // Set exit option
        inv.setItem(49, InventoryItems.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        try {
            config.getConfigurationSection(path + ".customShop").getKeys(false)
                    .forEach(index -> {
                        try {
                            // Get raw item and data
                            ItemStack item = config.getItemStack(path + ".customShop." + index).clone();
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                            int price = Integer.parseInt(meta.getDisplayName().substring(meta.getDisplayName().length() - 5));

                            // Transform to proper shop item
                            meta.setDisplayName(Utils.format("&f" + name));
                            if (meta.hasLore())
                                lore = meta.getLore();
                            lore.add(Utils.format("&2Gems: &a" + price));
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            // Set item into inventory
                            inv.setItem(Integer.parseInt(index), item);
                        } catch (Exception e) {
                            plugin.debugError(
                                    String.format(
                                            "An error occurred retrieving an item from arena %d's custom shop.", arena),
                                    2);
                        }
                    });
        } catch (Exception e) {
            plugin.debugError(
                    String.format("Attempted to retrieve the custom shop inventory of arena %d but found none.", arena),
                    1);
        }

        return inv;
    }

    /**
     * Retrieves a mockup of the custom shop for presenting arena information.
     * @return Mock custom shop {@link Inventory}
     */
    public Inventory getMockCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop: " + getName()));

        // Set exit option
        inv.setItem(49, InventoryItems.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        try {
            config.getConfigurationSection(path + ".customShop").getKeys(false)
                    .forEach(index -> {
                        try {
                            // Get raw item and data
                            ItemStack item = config.getItemStack(path + ".customShop." + index).clone();
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = new ArrayList<>();
                            String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                            int price = Integer.parseInt(meta.getDisplayName().substring(meta.getDisplayName().length() - 5));

                            // Transform to proper shop item
                            meta.setDisplayName(Utils.format("&f" + name));
                            if (meta.hasLore())
                                lore = meta.getLore();
                            lore.add(Utils.format("&2Gems: &a" + price));
                            meta.setLore(lore);
                            item.setItemMeta(meta);

                            // Set item into inventory
                            inv.setItem(Integer.parseInt(index), item);
                        } catch (Exception e) {
                            plugin.debugError(
                                    String.format(
                                            "An error occurred retrieving an item from arena %d's custom shop.", arena),
                                    2);
                        }
                    });
        } catch (Exception e) {
            plugin.debugError(
                    String.format("Attempted to retrieve the custom shop inventory of arena %d but found none.", arena),
                    1);
        }

        return inv;
    }

    public BossBar getTimeLimitBar() {
        return timeLimitBar;
    }

    /**
     * Create a time limit bar to display.
     */
    public void startTimeLimitBar() {
        try {
            timeLimitBar = Bukkit.createBossBar(Utils.format(
                    String.format(plugin.getLanguageData().getString("timeBar"), getCurrentWave())),
                    BarColor.YELLOW, BarStyle.SOLID);
        } catch (Exception e) {
            plugin.debugError("The active language file is missing text for the key 'timeBar'.", 1);
        }
    }

    /**
     * Updates the time limit bar's progress.
     * @param progress The bar's new progress.
     */
    public void updateTimeLimitBar(double progress) {
        timeLimitBar.setProgress(progress);
    }

    /**
     * Updates the time limit bar's color and progress.
     * @param color The bar's new color.
     * @param progress The bar's new progress.
     */
    public void updateTimeLimitBar(BarColor color, double progress) {
        timeLimitBar.setColor(color);
        timeLimitBar.setProgress(progress);
    }

    /**
     * Removes the time limit bar from every player.
     */
    public void removeTimeLimitBar() {
        players.forEach(vdPlayer -> timeLimitBar.removePlayer(vdPlayer.getPlayer()));
        timeLimitBar = null;
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
     * Checks and closes an arena if the arena does not meet opening requirements.
     */
    public void checkClose() {
        if (!plugin.getArenaData().contains("lobby") || getPortal() == null || getPlayerSpawn() == null ||
                getMonsterSpawns().stream().noneMatch(Objects::nonNull) ||
                getVillagerSpawns().stream().noneMatch(Objects::nonNull) || !hasCustom() && !hasNormal() ||
                getCorner1() == null || getCorner2() == null ||
                !Objects.equals(getCorner1().getWorld(), getCorner2().getWorld())) {
            setClosed(true);
            plugin.debugInfo(String.format("Arena %d did not meet opening requirements and was closed.", arena),
                    2);
        }
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
        setDynamicLimit(arenaToCopy.hasDynamicLimit());
        setDynamicPrices(arenaToCopy.hasDynamicPrices());
        setDifficultyLabel(arenaToCopy.getDifficultyLabel());
        setBannedKits(arenaToCopy.getBannedKits());
        setNormal(arenaToCopy.hasNormal());
        setCustom(arenaToCopy.hasCustom());
        setCommunity(arenaToCopy.hasCommunity());
        setWinSound(arenaToCopy.hasWinSound());
        setLoseSound(arenaToCopy.hasLoseSound());
        setWaveStartSound(arenaToCopy.hasWaveStartSound());
        setWaveFinishSound(arenaToCopy.hasWaveFinishSound());
        setGemSound(arenaToCopy.hasGemSound());
        setPlayerDeathSound(arenaToCopy.hasPlayerDeathSound());
        setAbilitySound(arenaToCopy.hasAbilitySound());
        setWaitingSound(arenaToCopy.getWaitingSoundNum());
        setSpawnParticles(arenaToCopy.hasSpawnParticles());
        setMonsterParticles(arenaToCopy.hasMonsterParticles());
        setVillagerParticles(arenaToCopy.hasVillagerParticles());
        if (config.contains("a" + arenaToCopy.getArena() + ".customShop"))
            try {
                config.getConfigurationSection("a" + arenaToCopy.getArena() + ".customShop").getKeys(false)
                        .forEach(index -> config.set(path + ".customShop." + index,
                                config.getItemStack("a" + arenaToCopy.getArena() + ".customShop." + index)));
            } catch (Exception e) {
                plugin.debugError(
                        String.format("Attempted to retrieve the custom shop inventory of arena %d but found none.",
                                arena), 1);
            }

        plugin.debugInfo(
                String.format("Copied the characteristics of arena %d to arena %d.", arenaToCopy.getArena(), arena),
                2);
    }

    /**
     * Removes all data of this arena from the arena file.
     */
    public void remove() {
        config.set(path, null);
        setPortal(null);
        setArenaBoard(null);
        plugin.debugInfo(String.format("Removing arena %d.", arena), 1);
    }
}
