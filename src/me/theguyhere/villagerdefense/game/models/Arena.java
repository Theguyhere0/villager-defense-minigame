package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.GUI.InventoryItems;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Arena {
    private final Main plugin;
    private final int arena; // Arena number
    private final FileConfiguration config; // Shortcut for file configuration of arena file
    private final String path; // Shortcut for the arena path in the arena file
    private final Tasks task; // The tasks object for the arena

    private boolean caps; // Indicates whether the naming inventory has caps lock on
    private boolean active; // Indicates whether the arena has a game ongoing
    private boolean spawning; // Indicates whether the arena is in the process of spawning mobs
    private boolean ending; // Indicates whether the arena is about to end
    private int currentWave; // Current game wave
    private int villagers; // Villager count
    private int enemies; // Enemy count
    private int golems; // Iron golem count
    private int spawnID = 0; // Spawn particles ID
    private int monsterID = 0; // Monster particles ID
    private int villagerID = 0; // Villager particles ID
    private final List<VDPlayer> players = new ArrayList<>(); // Tracks players playing and their other related stats
    private Inventory weaponShop; // Weapon shop inventory
    private Inventory armorShop; // Armor shop inventory
    private Inventory consumeShop; // Consumables shop inventory
    private BossBar timeLimitBar; // Time limit bar

    public Arena(Main plugin, int arena, Tasks task) {
        this.plugin = plugin;
        config = plugin.getArenaData();
        this.arena = arena;
        path = "a" + arena;
        this.task = task;
        currentWave = 0;
        villagers = 0;
        enemies = 0;
    }

    public int getArena() {
        return arena;
    }

    public String getName() {
        return config.getString(path + ".name");
    }

    public void setName(String name) {
        config.set(path + ".name", name);
        plugin.saveArenaData();
    }

    public String getDifficultyLabel() {
        if (config.contains(path + ".difficultyLabel"))
            return config.getString(path + ".difficultyLabel");
        else return null;
    }

    public void setDifficultyLabel(String label) {
        config.set(path + ".difficultyLabel", label);
        plugin.saveArenaData();
    }

    public int getMaxPlayers() {
        return config.getInt(path + ".max");
    }

    public void setMaxPlayers(int maxPlayers) {
        config.set(path + ".max", maxPlayers);
        plugin.saveArenaData();
    }

    public int getMinPlayers() {
        return config.getInt(path + ".min");
    }

    public void setMinPlayers(int minPlayers) {
        config.set(path + ".min", minPlayers);
        plugin.saveArenaData();
    }

    public int getWolfCap() {
        return config.getInt(path + ".wolf");
    }

    public void setWolfCap(int wolfCap) {
        config.set(path + ".wolf", wolfCap);
        plugin.saveArenaData();
    }

    public int getGolemCap() {
        return config.getInt(path + ".golem");
    }

    public void setgolemCap(int golemCap) {
        config.set(path + ".golem", golemCap);
        plugin.saveArenaData();
    }

    public int getMaxWaves() {
        return config.getInt(path + ".maxWaves");
    }

    public void setMaxWaves(int maxWaves) {
        config.set(path + ".maxWaves", maxWaves);
        plugin.saveArenaData();
    }

    public int getWaveTimeLimit() {
        return config.getInt(path + ".waveTimeLimit");
    }

    public void setWaveTimeLimit(int timeLimit) {
        config.set(path + ".waveTimeLimit", timeLimit);
        plugin.saveArenaData();
    }

    public int getDifficultyMultiplier() {
        return config.getInt(path + ".difficulty");
    }

    public void setDifficultyMultiplier(int multiplier) {
        config.set(path + ".difficulty", multiplier);
        plugin.saveArenaData();
    }

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

    public int getWaitingSoundNum() {
        return config.getInt(path + ".sounds.waiting");
    }

    public void setWaitingSound(int sound) {
        config.set(path + ".sounds.waiting", sound);
        plugin.saveArenaData();
    }

    public Location getPortal() {
        return Utils.getConfigLocationNoPitch(plugin, "portal." + arena);
    }

    public void setPortal(Location location) {
        Utils.setConfigurationLocation(plugin, "portal." + arena, location);
        plugin.saveArenaData();
    }

    public void centerPortal() {
        Utils.centerConfigLocation(plugin, "portal." + arena);
    }

    public Location getArenaBoard() {
        return Utils.getConfigLocationNoRotation(plugin, "arenaBoard." + arena);
    }

    public void setArenaBoard(Location location) {
        Utils.setConfigurationLocation(plugin, "arenaBoard." + arena, location);
        plugin.saveArenaData();
    }

    public void centerArenaBoard() {
        Utils.centerConfigLocation(plugin, "arenaBoard." + arena);
    }

    public Location getPlayerSpawn() {
        return Utils.getConfigLocation(plugin, path + ".spawn");
    }

    public void setPlayerSpawn(Location location) {
        Utils.setConfigurationLocation(plugin, path + ".spawn", location);
        plugin.saveArenaData();
    }

    public void centerPlayerSpawn() {
        Utils.centerConfigLocation(plugin, path + ".spawn");
    }

    public Location getWaitingRoom() {
        return Utils.getConfigLocation(plugin, path + ".waiting");
    }

    public void setWaitingRoom(Location location) {
        Utils.setConfigurationLocation(plugin, path + ".waiting", location);
        plugin.saveArenaData();
    }

    public void centerWaitingRoom() {
        Utils.centerConfigLocation(plugin, path + ".waiting");
    }

    public List<Location> getMonsterSpawns() {
        return Utils.getConfigLocationList(plugin, path + ".monster").stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

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
        if (spawnID == 0)
            spawnID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                double var2 = 0;
                Location first, second;

                @Override
                public void run() {
                    // Update particle locations
                    var += Math.PI / 12;
                    var2 -= Math.PI / 12;
                    first = getPlayerSpawn().clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                    second = getPlayerSpawn().clone().add(Math.cos(var2 + Math.PI), Math.sin(var2) + 1,
                            Math.sin(var2 + Math.PI));

                    // Spawn particles
                    getPlayerSpawn().getWorld().spawnParticle(Particle.FLAME, first, 0);
                    getPlayerSpawn().getWorld().spawnParticle(Particle.FLAME, second, 0);
                }
            }, 0 , 2);
    }

    public void cancelSpawnParticles() {
        if (spawnID != 0)
            Bukkit.getScheduler().cancelTask(spawnID);
        spawnID = 0;
    }

    public boolean hasMonsterParticles() {
        return config.getBoolean(path + ".particles.monster");
    }

    public void setMonsterParticles(boolean bool) {
        config.set(path + ".particles.monster", bool);
        plugin.saveArenaData();
    }

    public void startMonsterParticles() {
        if (monsterID == 0)
            monsterID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                Location first, second;

                @Override
                public void run() {
                    var -= Math.PI / 12;
                    getMonsterSpawns().forEach(location -> {
                        // Update particle locations
                        first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                        second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                Math.sin(var + Math.PI));

                        // Spawn particles
                        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, first, 0);
                        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, second, 0);
                    });
                }
            }, 0 , 2);
    }

    public void cancelMonsterParticles() {
        if (monsterID != 0)
            Bukkit.getScheduler().cancelTask(monsterID);
        monsterID = 0;
    }

    public boolean hasVillagerParticles() {
        return config.getBoolean(path + ".particles.villager");
    }

    public void setVillagerParticles(boolean bool) {
        config.set(path + ".particles.villager", bool);
        plugin.saveArenaData();
    }

    public void startVillagerParticles() {
        if (villagerID == 0)
            villagerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                double var = 0;
                Location first, second;

                @Override
                public void run() {
                    var += Math.PI / 12;
                    getVillagerSpawns().forEach(location -> {
                        // Update particle locations
                        first = location.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                        second = location.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1,
                                Math.sin(var + Math.PI));

                        // Spawn particles
                        location.getWorld().spawnParticle(Particle.COMPOSTER, first, 0);
                        location.getWorld().spawnParticle(Particle.COMPOSTER, second, 0);
                    });
                }
            }, 0 , 2);
    }

    public void cancelVillagerParticles() {
        if (villagerID != 0)
            Bukkit.getScheduler().cancelTask(villagerID);
        villagerID = 0;
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
            config.getConfigurationSection(path + ".records").getKeys(false)
                    .forEach(index -> arenaRecords.add(new ArenaRecord(
                            config.getInt(path + ".records." + index + ".wave"),
                            config.getStringList(path + ".records." + index + ".players")
                    )));

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSpawning() {
        return spawning;
    }

    public void setSpawning(boolean spawning) {
        this.spawning = spawning;
    }

    public boolean isEnding() {
        return ending;
    }

    public void flipEnding() {
        ending = !ending;
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
        villagers--;
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
        enemies--;
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

    public List<VDPlayer> getPlayers() {
        return players;
    }

    public List<VDPlayer> getActives() {
        return players.stream().filter(p -> !p.isSpectating()).collect(Collectors.toList());
    }

    public List<VDPlayer> getGhosts() {
        return getActives().stream().filter(p -> p.getPlayer().getGameMode() == GameMode.SPECTATOR)
                .collect(Collectors.toList());
    }

    public List<VDPlayer> getSpectators() {
        return players.stream().filter(VDPlayer::isSpectating).collect(Collectors.toList());
    }

    public VDPlayer getPlayer(Player player) {
        return players.stream().filter(p -> p.getPlayer().equals(player)).collect(Collectors.toList()).get(0);
    }

    public boolean hasPlayer(Player player) {
        return players.stream().anyMatch(p -> p.getPlayer().equals(player));
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

    public Inventory getCustomShopEditor() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop Editor: " + getName()));

        // Set exit option
        inv.setItem(53, InventoryItems.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        config.getConfigurationSection(path + ".customShop").getKeys(false)
            .forEach(index -> inv.setItem(Integer.parseInt(index),
                    config.getItemStack(path + ".customShop." + index)));

        return inv;
    }

    public Inventory getCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop"));

        // Set exit option
        inv.setItem(53, InventoryItems.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        config.getConfigurationSection(path + ".customShop").getKeys(false)
                .forEach(index -> {
                    // Get raw item and data
                    ItemStack item = config.getItemStack(path + ".customShop." + index).clone();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                    int price = Integer.parseInt(meta.getDisplayName().substring(meta.getDisplayName().length() - 5));

                    // Transform to proper shop item
                    meta.setDisplayName(Utils.format("&f" + name));
                    if (meta.hasLore()) {
                        lore = meta.getLore();
                        lore.add(Utils.format("&2Gems: &a" + price));
                    } else lore.add(Utils.format("&2Gems: &a" + price));
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    // Set item into inventory
                    inv.setItem(Integer.parseInt(index), item);
                });

        return inv;
    }

    public Inventory getMockCustomShop() {
        // Create inventory
        Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, Utils.format("&k") +
                Utils.format("&6&lCustom Shop: " + getName()));

        // Set exit option
        inv.setItem(53, InventoryItems.exit());

        // Check for a stored inventory
        if (!config.contains(path + ".customShop"))
            return inv;

        // Get items from stored inventory
        config.getConfigurationSection(path + ".customShop").getKeys(false)
                .forEach(index -> {
                    // Get raw item and data
                    ItemStack item = config.getItemStack(path + ".customShop." + index).clone();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    String name = meta.getDisplayName().substring(0, meta.getDisplayName().length() - 5);
                    int price = Integer.parseInt(meta.getDisplayName().substring(meta.getDisplayName().length() - 5));

                    // Transform to proper shop item
                    meta.setDisplayName(Utils.format("&f" + name));
                    if (meta.hasLore()) {
                        lore = meta.getLore();
                        lore.add(Utils.format("&2Gems: &a" + price));
                    } else lore.add(Utils.format("&2Gems: &a" + price));
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    // Set item into inventory
                    inv.setItem(Integer.parseInt(index), item);
                });

        return inv;
    }

    public BossBar getTimeLimitBar() {
        return timeLimitBar;
    }

    public void startTimeLimitBar() {
        timeLimitBar = Bukkit.createBossBar(Utils.format("&eWave " + getCurrentWave() + " Time Limit"),
                BarColor.YELLOW, BarStyle.SOLID);
    }

    public void updateTimeLimitBar(double progress) {
        timeLimitBar.setProgress(progress);
    }

    public void updateTimeLimitBar(BarColor color, double progress) {
        timeLimitBar.setColor(color);
        timeLimitBar.setProgress(progress);
    }

    public void removeTimeLimitBar() {
        players.forEach(vdPlayer -> timeLimitBar.removePlayer(vdPlayer.getPlayer()));
        timeLimitBar = null;
    }

    public void addPlayerToTimeLimitBar(Player player) {
        timeLimitBar.addPlayer(player);
    }

    public void removePlayerFromTimeLimitBar(Player player) {
        timeLimitBar.removePlayer(player);
    }

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
            config.getConfigurationSection("a" + arenaToCopy.getArena() + ".customShop").getKeys(false)
                    .forEach(index -> config.set(path + ".customShop." + index,
                            config.getItemStack("a" + arenaToCopy.getArena() + ".customShop." + index)));
    }

    public void remove() {
        config.set(path, null);
        setPortal(null);
        setArenaBoard(null);
    }
}
