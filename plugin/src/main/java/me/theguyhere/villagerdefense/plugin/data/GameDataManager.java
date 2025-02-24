package me.theguyhere.villagerdefense.plugin.data;

import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GameDataManager {
    private static YAMLManager yamlManager;

    public static void init() {
        yamlManager = new YAMLManager("gameData.yml");
    }

    public static boolean hasLobby() {
        return yamlManager.hasPath("lobby");
    }

    public static @NotNull Location getLobbyLocation() throws BadDataException, NoSuchPathException {
        return yamlManager.getConfigLocationNoPitch("lobby");
    }

    public static @Nullable String getLobbyWorldName() {
        try {
            return yamlManager.getString("lobby.world");
        }
        catch (NoSuchPathException e) {
            return null;
        }
    }

    public static void setLobbyLocation(Location location) {
        yamlManager.setConfigLocation("lobby", location);
    }

    public static void centerLobbyLocation() throws BadDataException, NoSuchPathException {
        yamlManager.centerConfigLocation("lobby");
    }

    public static void removeLobbyLocation() {
        yamlManager.delete("lobby");
    }

    public static boolean hasInfoBoard(int id) {
        return yamlManager.hasPath("infoBoard." + id);
    }

    public static @NotNull Location getInfoBoardLocation(int id) throws BadDataException, NoSuchPathException {
        return yamlManager.getConfigLocationNoRotation("infoBoard." + id);
    }

    public static Set<String> getInfoBoardWorlds() {
        Set<String> worlds = new HashSet<>();
        try {
            for (String id : yamlManager.getKeys("infoBoard")) {
                worlds.add(yamlManager.getString("infoBoard." + id + ".world"));
            }
        }
        catch (BadDataException | NoSuchPathException ignored) {}
        return worlds;
    }

    public static Set<Integer> getInfoBoardIDs() {
        try {
            return yamlManager.getKeys("infoBoard").stream().map(Integer::parseInt).collect(Collectors.toSet());
        }
        catch (BadDataException | NoSuchPathException ignored) {
            return new HashSet<>();
        }
    }

    public static void setInfoBoardLocation(int id, Location location) {
        yamlManager.setConfigLocation("infoBoard." + id, location);
    }

    public static void centerInfoBoardLocation(int id) throws BadDataException, NoSuchPathException {
        yamlManager.centerConfigLocation("infoBoard." + id);
    }

    public static void removeInfoBoardLocation(int id) {
        yamlManager.delete("infoBoard." + id);
    }

    public static boolean hasLeaderboard(String type) {
        return yamlManager.hasPath("leaderboard." + type);
    }

    public static @NotNull Location getLeaderboardLocation(String type) throws BadDataException, NoSuchPathException {
        return yamlManager.getConfigLocationNoRotation("leaderboard." + type);
    }

    public static Set<String> getLeaderboardWorlds() {
        Set<String> worlds = new HashSet<>();
        try {
            for (String type : yamlManager.getKeys("leaderboard")) {
                worlds.add(yamlManager.getString("leaderboard." + type + ".world"));
            }
        }
        catch (BadDataException | NoSuchPathException ignored) {}
        return worlds;
    }

    public static Set<String> getLeaderboardTypes() {
        try {
            return yamlManager.getKeys("leaderboard");
        }
        catch (BadDataException | NoSuchPathException ignored) {
            return new HashSet<>();
        }
    }

    public static void setLeaderboardLocation(String type, Location location) {
        yamlManager.setConfigLocation("leaderboard." + type, location);
    }

    public static void centerLeaderboardLocation(String type) throws BadDataException, NoSuchPathException {
        yamlManager.centerConfigLocation("leaderboard." + type);
    }

    public static void removeLeaderboardLocation(String type) {
        yamlManager.delete("leaderboard." + type);
    }
}
