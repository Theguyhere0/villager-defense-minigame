package me.theguyhere.villagerdefense.plugin.data;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.UpdateFailedException;
import me.theguyhere.villagerdefense.plugin.structures.ArenaRecord;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ArenaDataManager {
    private static YAMLManager yamlManager;

    public static void init() {
        yamlManager = new YAMLManager("arenaData.yml");
    }

    public static Set<Integer> getArenaIDs() {
        try {
            return yamlManager.getKeys("arena").stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        }
        catch (BadDataException | NoSuchPathException e) {
            return new HashSet<>();
        }
    }

    public static List<String> getArenaNames() {
        List<String> names = new ArrayList<>();
        try {
            for (String id : yamlManager.getKeys("arena")) {
                names.add(yamlManager.getString("arena." + id + ".name"));
            }
        }
        catch (BadDataException | NoSuchPathException ignored) {}
        return names;
    }

    public static Set<String> getArenaWorlds() {
        Set<String> worlds = new HashSet<>();
        try {
            for (String id : yamlManager.getKeys("arena")) {
                try {
                    worlds.add(yamlManager.getString("arena." + id + ".arenaBoard.world"));
                    worlds.add(yamlManager.getString("arena." + id + ".spawn.world"));
                    worlds.add(yamlManager.getString("arena." + id + ".portal.world"));
                }
                catch (NoSuchPathException ignored) {}
            }
        }
        catch (BadDataException | NoSuchPathException ignored) {}
        return worlds;
    }

    public static String getArenaName(int id) throws NoSuchPathException {
        return yamlManager.getString("arena." + id + ".name");
    }

    public static void setArenaName(int id, String name) {
        yamlManager.setString("arena." + id + ".name", name);
    }

    public static boolean getArenaClosed(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".closed");
    }

    public static void setArenaClosed(int id, boolean closed) {
        yamlManager.setBoolean("arena." + id + ".closed", closed);
    }

    public static Location getArenaPortal(int id) {
        try {
            return yamlManager.getConfigLocationNoPitch("arena." + id + ".portal");
        } catch (BadDataException | NoSuchPathException e) {
            return null;
        }
    }

    public static void setArenaPortal(int id, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".portal", location);
    }

    public static void centerArenaPortal(int id) throws NoSuchPathException, BadDataException {
        yamlManager.centerConfigLocation("arena." + id + ".portal");
    }

    public static void removeArenaPortal(int id) {
        yamlManager.delete("arena." + id + ".portal");
    }

    public static Location getArenaBoard(int id) {
        try {
            return yamlManager.getConfigLocationNoPitch("arena." + id + ".arenaBoard");
        } catch (BadDataException | NoSuchPathException e) {
            return null;
        }
    }

    public static void setArenaBoard(int id, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".arenaBoard", location);
    }

    public static void centerArenaBoard(int id) throws NoSuchPathException, BadDataException {
        yamlManager.centerConfigLocation("arena." + id + ".arenaBoard");
    }

    public static void removeArenaBoard(int id) {
        yamlManager.delete("arena." + id + ".arenaBoard");
    }

    public static Location getPlayerSpawn(int id) {
        try {
            return yamlManager.getConfigLocation("arena." + id + ".spawn");
        } catch (BadDataException | NoSuchPathException e) {
            return null;
        }
    }

    public static void setPlayerSpawn(int id, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".spawn", location);
    }

    public static void centerPlayerSpawn(int id) throws NoSuchPathException, BadDataException {
        yamlManager.centerConfigLocation("arena." + id + ".spawn");
    }

    public static Location getWaitingRoom(int id) {
        try {
            return yamlManager.getConfigLocation("arena." + id + ".waiting");
        } catch (BadDataException | NoSuchPathException e) {
            return null;
        }
    }

    public static void setWaitingRoom(int id, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".waiting", location);
    }

    public static void centerWaitingRoom(int id) throws NoSuchPathException, BadDataException {
        yamlManager.centerConfigLocation("arena." + id + ".waiting");
    }

    public static void removeArena(int id) {
        yamlManager.delete("arena." + id);
    }

    public static boolean hasNormal(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".normal");
    }

    public static void setNormal(int id, boolean normal) {
        yamlManager.setBoolean("arena." + id + ".normal", normal);
    }

    public static boolean hasEnchants(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".enchants");
    }

    public static void setEnchants(int id, boolean enchants) {
        yamlManager.setBoolean("arena." + id + ".enchants", enchants);
    }

    public static boolean hasCustom(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".custom");
    }

    public static void setCustom(int id, boolean custom) {
        yamlManager.setBoolean("arena." + id + ".custom", custom);
    }

    public static boolean hasCommunity(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".community");
    }

    public static void setCommunity(int id, boolean community) {
        yamlManager.setBoolean("arena." + id + ".community", community);
    }

    public static boolean hasGemDrop(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".gemDrop");
    }

    public static void setGemDrop(int id, boolean gemDrop) {
        yamlManager.setBoolean("arena." + id + ".gemDrop", gemDrop);
    }

    public static boolean hasExpDrop(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".expDrop");
    }

    public static void setExpDrop(int id, boolean expDrop) {
        yamlManager.setBoolean("arena." + id + ".expDrop", expDrop);
    }

    public static boolean hasSpawnParticles(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".particles.spawn");
    }

    public static void setSpawnParticles(int id, boolean spawnParticles) {
        yamlManager.setBoolean("arena." + id + ".particles.spawn", spawnParticles);
    }

    public static boolean hasMonsterParticles(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".particles.monster");
    }

    public static void setMonsterParticles(int id, boolean monsterParticles) {
        yamlManager.setBoolean("arena." + id + ".particles.monster", monsterParticles);
    }

    public static boolean hasVillagerParticles(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".particles.villager");
    }

    public static void setVillagerParticles(int id, boolean villagerParticles) {
        yamlManager.setBoolean("arena." + id + ".particles.villager", villagerParticles);
    }

    public static boolean hasBorderParticles(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".particles.border");
    }

    public static void setBorderParticles(int id, boolean borderParticles) {
        yamlManager.setBoolean("arena." + id + ".particles.border", borderParticles);
    }

    public static boolean hasWinSound(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".sounds.win");
    }

    public static void setWinSound(int id, boolean sound) {
        yamlManager.setBoolean("arena." + id + ".sounds.win", sound);
    }

    public static boolean hasLoseSound(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".sounds.lose");
    }

    public static void setLoseSound(int id, boolean sound) {
        yamlManager.setBoolean("arena." + id + ".sounds.lose", sound);
    }

    public static boolean hasWaveStart(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".sounds.start");
    }

    public static void setWaveStart(int id, boolean sound) {
        yamlManager.setBoolean("arena." + id + ".sounds.start", sound);
    }

    public static boolean hasWaveEnd(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".sounds.end");
    }

    public static void setWaveEnd(int id, boolean sound) {
        yamlManager.setBoolean("arena." + id + ".sounds.end", sound);
    }

    public static boolean hasGemSound(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".sounds.gem");
    }

    public static void setGemSound(int id, boolean sound) {
        yamlManager.setBoolean("arena." + id + ".sounds.gem", sound);
    }

    public static boolean hasDeathSound(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".sounds.death");
    }

    public static void setDeathSound(int id, boolean sound) {
        yamlManager.setBoolean("arena." + id + ".sounds.death", sound);
    }

    public static boolean hasAbilitySound(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".sounds.ability");
    }

    public static void setAbilitySound(int id, boolean sound) {
        yamlManager.setBoolean("arena." + id + ".sounds.ability", sound);
    }

    public static String getWaitingSound(int id) throws NoSuchPathException {
        return yamlManager.getString("arena." + id + ".sounds.waiting");
    }

    public static void setWaitingSound(int id, String sound) {
        yamlManager.setString("arena." + id + ".sounds.waiting", sound);
    }

    public static boolean hasDynamicCount(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".dynamicCount");
    }

    public static void setDynamicCount(int id, boolean toggle) {
        yamlManager.setBoolean("arena." + id + ".dynamicCount", toggle);
    }

    public static boolean hasDynamicDifficulty(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".dynamicDifficulty");
    }

    public static void setDynamicDifficulty(int id, boolean toggle) {
        yamlManager.setBoolean("arena." + id + ".dynamicDifficulty", toggle);
    }

    public static boolean hasDynamicPrices(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".dynamicPrices");
    }

    public static void setDynamicPrices(int id, boolean toggle) {
        yamlManager.setBoolean("arena." + id + ".dynamicPrices", toggle);
    }

    public static boolean hasDynamicLimit(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".dynamicLimit");
    }

    public static void setDynamicLimit(int id, boolean toggle) {
        yamlManager.setBoolean("arena." + id + ".dynamicLimit", toggle);
    }

    public static boolean hasLateArrival(int id) throws NoSuchPathException {
        return yamlManager.getBoolean("arena." + id + ".lateArrival");
    }

    public static void setLateArrival(int id, boolean toggle) {
        yamlManager.setBoolean("arena." + id + ".lateArrival", toggle);
    }

    public static String getDifficultyLabel(int id) {
        try {
            return yamlManager.getString("arena." + id + ".difficultyLabel");
        }
        catch (NoSuchPathException e) {
            return "";
        }
    }

    public static void setDifficultyLabel(int id, String label) {
        yamlManager.setString("arena." + id + ".difficultyLabel", label);
    }

    public static int getMaxPlayers(int id) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".max");
    }

    public static void setMaxPlayers(int id, int value) {
        yamlManager.setInteger("arena." + id + ".max", value);
    }

    public static int getMinPlayers(int id) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".min");
    }

    public static void setMinPlayers(int id, int value) {
        yamlManager.setInteger("arena." + id + ".min", value);
    }

    public static int getWolfCap(int id) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".wolf");
    }

    public static void setWolfCap(int id, int value) {
        yamlManager.setInteger("arena." + id + ".wolf", value);
    }

    public static int getGolemCap(int id) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".golem");
    }

    public static void setGolemCap(int id, int value) {
        yamlManager.setInteger("arena." + id + ".golem", value);
    }

    public static int getMaxWaves(int id) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".maxWaves");
    }

    public static void setMaxWaves(int id, int value) {
        yamlManager.setInteger("arena." + id + ".maxWaves", value);
    }

    public static int getWaveTimeLimit(int id) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".waveTimeLimit");
    }

    public static void setWaveTimeLimit(int id, int value) {
        yamlManager.setInteger("arena." + id + ".waveTimeLimit", value);
    }

    public static int getDifficultyMultiplier(int id) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".difficulty");
    }

    public static void setDifficultyMultiplier(int id, int value) {
        yamlManager.setInteger("arena." + id + ".difficulty", value);
    }

    public static String getSpawnTableName(int id) throws NoSuchPathException {
        return yamlManager.getString("arena." + id + ".spawnTable");
    }

    public static void setSpawnTableName(int id, String name) {
        yamlManager.setString("arena." + id + ".spawnTable", name);
    }

    public static Location getCorner1(int id) {
        try {
            return yamlManager.getConfigLocationNoPitch("arena." + id + ".corner1");
        } catch (BadDataException | NoSuchPathException e) {
            return null;
        }
    }

    public static void setCorner1(int id, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".corner1", location);
    }

    public static Location getCorner2(int id) {
        try {
            return yamlManager.getConfigLocationNoPitch("arena." + id + ".corner2");
        } catch (BadDataException | NoSuchPathException e) {
            return null;
        }
    }

    public static void setCorner2(int id, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".corner2", location);
    }

    public static List<String> getBannedKits(int id) {
        try {
            return yamlManager.getStringList("arena." + id + ".bannedKits");
        }
        catch (NoSuchPathException e) {
            return new ArrayList<>();
        }
    }

    public static void setBannedKits(int id, List<String> bannedKits) {
        yamlManager.setStringList("arena." + id + ".bannedKits", bannedKits);
    }

    public static List<String> getForcedChallenges(int id) {
        try {
            return yamlManager.getStringList("arena." + id + ".forcedChallenges");
        }
        catch (NoSuchPathException e) {
            return new ArrayList<>();
        }
    }

    public static void setForcedChallenges(int id, List<String> forcedChallenges) {
        yamlManager.setStringList("arena." + id + ".forcedChallenges", forcedChallenges);
    }

    public static Map<Integer, Location> getMonsterSpawns(int id) {
        try {
            return yamlManager.getConfigLocationMap("arena." + id + ".monster");
        }
        catch (NoSuchPathException e) {
            return new HashMap<>();
        }
    }

    public static void setMonsterSpawn(int id, int spawnID, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".monster." + spawnID, location);
    }

    public static void centerMonsterSpawn(int id, int spawnID) throws NoSuchPathException, BadDataException {
        yamlManager.centerConfigLocation("arena." + id + ".monster." + spawnID);
    }

    public static int getMonsterSpawnType(int id, int spawnID) throws NoSuchPathException {
        return yamlManager.getInteger("arena." + id + ".monster." + spawnID + ".type");
    }

    public static void setMonsterSpawnType(int id, int spawnID, int type) {
        yamlManager.setInteger("arena." + id + ".monster." + spawnID + ".type", type);
    }

    public static Map<Integer, Location> getVillagerSpawns(int id) {
        try {
            return yamlManager.getConfigLocationMap("arena." + id + ".villager");
        }
        catch (NoSuchPathException e) {
            return new HashMap<>();
        }
    }

    public static void setVillagerSpawn(int id, int spawnID, Location location) {
        yamlManager.setConfigLocation("arena." + id + ".villager." + spawnID, location);
    }

    public static void centerVillagerSpawn(int id, int spawnID) throws NoSuchPathException, BadDataException {
        yamlManager.centerConfigLocation("arena." + id + ".villager." + spawnID);
    }

    public static List<ArenaRecord> getArenaRecords(int id) throws NoSuchPathException, BadDataException {
        List<ArenaRecord> arenaRecords = new ArrayList<>();
        if (yamlManager.hasPath("arena." + id + ".records")) {
            yamlManager.getKeys("arena." + id + ".records").forEach(index ->
            {
                try {
                    arenaRecords.add(new ArenaRecord(
                        yamlManager.getInteger("arena." + id + ".records." + index + ".wave"),
                        yamlManager.getStringList("arena." + id + ".records." + index + ".players")
                    ));
                } catch (NoSuchPathException ignored) {}
            });
        }

        return arenaRecords;
    }

    public static void setArenaRecords(int id, List<ArenaRecord> records) {
        for (int i = 0; i < records.size(); i++) {
            yamlManager.setInteger("arena." + id + ".records." + i + ".wave", records.get(i).getWave());
            yamlManager.setStringList("arena." + id + ".records." + i + ".players", records.get(i).getPlayers());
        }
    }

    public static boolean hasCustomShop(int id) {
        return yamlManager.hasPath("arena." + id + ".customShop");
    }

    public static Map<Integer, ItemStack> getCustomShop(int id) throws BadDataException {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        try {
            for (String index : yamlManager.getKeys("arena." + id + ".customShop")) {
                try {
                    inventory.put(Integer.parseInt(index),
                        yamlManager.getItemStack("arena." + id + ".customShop." + index));
                }
                catch (NoSuchPathException e) {
                    throw new BadDataException();
                }
            }
        }
        catch (NoSuchPathException ignored) {}
        return inventory;
    }

    public static void copyCustomShop(int from, int to) throws NoSuchPathException {
        yamlManager.copyPath("arena." + from + ".customShop", "arena." + to + ".customShop");
    }

    public static void setCustomShopItem(int id, int index, ItemStack item) {
        yamlManager.setItemStack("arena." + id + ".customShop." + index, item);
    }

    public static void removeCustomShopItem(int id, int index) {
        yamlManager.delete("arena." + id + ".customShop." + index);
    }

    public static void updateToVersion4() throws UpdateFailedException {
        try {
            // Transfer portals
            if (yamlManager.hasPath("portal")) {
                for (String s : yamlManager.getKeys("portal")) {
                    yamlManager.setConfigLocation("a" + s + ".portal",
                        yamlManager.getConfigLocation("portal." + s));
                    yamlManager.delete("portal." + s);
                }
                yamlManager.delete("portal");
            }

            // Transfer arena boards
            for (String id : yamlManager.getKeys("arenaBoard")) {
                yamlManager.setConfigLocation("a" + id + ".arenaBoard",
                    yamlManager.getConfigLocation("arenaBoard." + id));
                yamlManager.delete("arenaBoard." + id);
            }
            yamlManager.delete("arenaBoard");
        }
        catch (BadDataException e) {
            throw new UpdateFailedException();
        }
        catch (NoSuchPathException ignored) {}
    }

    public static void updateToVersion5() throws UpdateFailedException {
        try {
            // Translate waiting sounds
            for (String key : yamlManager.getKeys("")) {
                String soundPath = key + ".sounds.waiting";
                if (key.charAt(0) == 'a' && key.length() < 4 && yamlManager.hasPath(soundPath)) {
                    int oldValue = yamlManager.getInteger(soundPath);
                    switch (oldValue) {
                        case 0:
                            yamlManager.setString(soundPath, "cat");
                            break;
                        case 1:
                            yamlManager.setString(soundPath, "blocks");
                            break;
                        case 2:
                            yamlManager.setString(soundPath, "far");
                            break;
                        case 3:
                            yamlManager.setString(soundPath, "strad");
                            break;
                        case 4:
                            yamlManager.setString(soundPath, "mellohi");
                            break;
                        case 5:
                            yamlManager.setString(soundPath, "ward");
                            break;
                        case 9:
                            yamlManager.setString(soundPath, "chirp");
                            break;
                        case 10:
                            yamlManager.setString(soundPath, "stal");
                            break;
                        case 11:
                            yamlManager.setString(soundPath, "mall");
                            break;
                        case 12:
                            yamlManager.setString(soundPath, "wait");
                            break;
                        case 13:
                            yamlManager.setString(soundPath, "pigstep");
                            break;
                        default:
                            yamlManager.setString(soundPath, "none");
                    }
                }
            }
        }
        catch (BadDataException e) {
            throw new UpdateFailedException();
        }
        catch (NoSuchPathException ignored) {}
    }

    public static void updateToVersion6() throws UpdateFailedException {
        try {
            // Take old data and put into new format
            for (String s : yamlManager.getKeys("").stream()
                .filter(key -> key.contains("a") && key.length() < 4)
                .collect(Collectors.toSet())) {
                int arenaId = Integer.parseInt(s.substring(1));
                yamlManager.swapPath(s, "arena." + arenaId);
            }
        }
        catch (BadDataException e) {
            throw new UpdateFailedException();
        }
        catch (NoSuchPathException ignored) {}
    }

    public static void updateToVersion7() throws UpdateFailedException {
        try {
            // Move lobby to gameData.yml
            if (yamlManager.hasPath("lobby")) {
                GameDataManager.setLobbyLocation(yamlManager.getConfigLocation("lobby"));
                yamlManager.delete("lobby");
            }

            // Move info boards to gameData.yml
            if (yamlManager.hasPath("infoBoard")) {
                for (String id : yamlManager.getKeys("infoBoard")) {
                    GameDataManager.setInfoBoardLocation(Integer.parseInt(id),
                        yamlManager.getConfigLocation("infoBoard." + id));
                }
                yamlManager.delete("infoBoard");
            }

            // Move leaderboards to gameData.yml
            for (String type : yamlManager.getKeys("leaderboard")) {
                GameDataManager.setLeaderboardLocation(type, yamlManager.getConfigLocation("leaderboard." + type));
            }
            yamlManager.delete("leaderboard");

            // Convert item stacks
            AtomicBoolean updateFailed = new AtomicBoolean(false);
            yamlManager.getKeys("arena").stream()
                .filter(id -> yamlManager.hasPath("arena." + id + ".customShop"))
                .forEach(id -> {
                    if (!updateFailed.get()) {
                        try {
                            yamlManager.getKeys("arena." + id + ".customShop").forEach(index -> {
                                if (!updateFailed.get()) {
                                    // Get raw item and data
                                    ItemStack item;
                                    try {
                                        item = yamlManager.getItemStack("arena." + id + ".customShop." + index);
                                    } catch (NoSuchPathException e) {
                                        updateFailed.set(true);
                                        return;
                                    }
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

                                    // Set proper item into file
                                    yamlManager.setItemStack("arena." + id + ".customShop." + index, item);
                                }
                            });
                        }
                        catch (BadDataException | NoSuchPathException e) {
                            updateFailed.set(true);
                        }
                    }
                });
            if (updateFailed.get()) {
                throw new UpdateFailedException();
            }
        }
        catch (BadDataException e) {
            throw new UpdateFailedException();
        }
        catch (NoSuchPathException ignored) {}
    }
}
