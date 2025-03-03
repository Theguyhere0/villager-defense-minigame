package me.theguyhere.villagerdefense.plugin.data;

import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.UpdateFailedException;
import me.theguyhere.villagerdefense.plugin.game.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerDataManager {
    private static YAMLManager yamlManager;

    public static void init() {
        yamlManager = new YAMLManager("playerData.yml");
    }

    public static boolean hasPlayer(UUID uuid) {
        return yamlManager.hasPath(uuid.toString());
    }

    public static boolean playerOwnsKit(UUID uuid, Kit kit) {
        try {
            return yamlManager.getSoftBoolean(uuid + ".kits." + kit.getName());
        } catch (NoSuchPathException e) {
            return false;
        }
    }

    /**
     * Retrieves the kit level owned by a player. Returns 0 if player doesn't own the kit and 1 for single tired kits.
     * @param uuid Player UUID
     * @param kit Kit
     * @return Kit level
     */
    public static int getPlayerKitLevel(UUID uuid, Kit kit) {
        try {
            return yamlManager.getSoftInteger(uuid + ".kits." + kit.getName());
        } catch (NoSuchPathException e) {
            return 0;
        }
    }

    public static void setPlayerKitLevel(UUID uuid, Kit kit, int level) {
        yamlManager.setInteger(uuid + ".kits." + kit.getName(), level);
    }
    
    public static int getPlayerCrystals(UUID uuid) {
        try {
            return yamlManager.getInteger(uuid + ".crystalBalance");
        }
        catch (NoSuchPathException e) {
            return 0;
        }
    }
    
    public static void setPlayerCrystals(UUID uuid, int balance) {
        yamlManager.setInteger(uuid + ".crystalBalance", balance);
    }

    public static int getPlayerStat(UUID uuid, @NotNull String type) {
        try {
            return yamlManager.getInteger(uuid + "." + type);
        }
        catch (NoSuchPathException e) {
            return 0;
        }
    }

    public static void setPlayerStat(UUID uuid, @NotNull String type, int value) {
        yamlManager.setInteger(uuid + "." + type, value);
    }

    /**
     * Retrieves a list of achievements for a player. Returns empty list if player path doesn't exist.
     * @param uuid Player UUID
     * @return List of achievements
     */
    public static List<String> getPlayerAchievements(UUID uuid) {
        try {
            return yamlManager.getStringList(uuid + ".achievements");
        }
        catch (NoSuchPathException e) {
            return new ArrayList<>();
        }
    }

    public static void setPlayerAchievements(UUID uuid, List<String> achievements) {
        yamlManager.setStringList(uuid + ".achievements", achievements);
    }

    /**
     * Retrieves the UUIDs of all players tracked by the plugin.
     * @return Set of UUIDs
     */
    public static Set<UUID> getTrackedPlayers() {
        try {
            return yamlManager.getKeys("").stream()
                .filter(s -> {
                    try {
                        UUID.fromString(s);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .map(UUID::fromString)
                .collect(Collectors.toSet());
        }
        catch (BadDataException | NoSuchPathException e) {
            return new HashSet<>();
        }
    }

    public static List<UUID> getLoggers() {
        try {
            return yamlManager.getStringList("loggers").stream().map(UUID::fromString).collect(Collectors.toList());
        }
        catch (NoSuchPathException e) {
            return new ArrayList<>();
        }
    }

    public static void setLoggers(List<UUID> loggers) {
        yamlManager.setStringList("loggers", loggers.stream().map(UUID::toString).collect(Collectors.toList()));
    }

    public static double getAndDeletePlayerHealth(UUID uuid) throws NoSuchPathException {
        double health = yamlManager.getDouble(uuid + ".health");
        yamlManager.delete(uuid + ".health");
        return health;
    }

    public static void setPlayerHealth(UUID uuid, double health) {
        yamlManager.setDouble(uuid + ".health", health);
    }

    public static double getAndDeletePlayerAbsorption(UUID uuid) throws NoSuchPathException {
        double absorption = yamlManager.getDouble(uuid + ".absorption");
        yamlManager.delete(uuid + ".absorption");
        return absorption;
    }

    public static void setPlayerAbsorption(UUID uuid, double absorption) {
        yamlManager.setDouble(uuid + ".absorption", absorption);
    }

    public static int getAndDeletePlayerFood(UUID uuid) throws NoSuchPathException {
        int food = yamlManager.getInteger(uuid + ".food");
        yamlManager.delete(uuid + ".food");
        return food;
    }

    public static void setPlayerFood(UUID uuid, int food) {
        yamlManager.setInteger(uuid + ".food", food);
    }

    public static double getAndDeletePlayerSaturation(UUID uuid) throws NoSuchPathException {
        double saturation = yamlManager.getDouble(uuid + ".saturation");
        yamlManager.delete(uuid + ".saturation");
        return saturation;
    }

    public static void setPlayerSaturation(UUID uuid, double saturation) {
        yamlManager.setDouble(uuid + ".saturation", saturation);
    }

    public static int getAndDeletePlayerLevel(UUID uuid) throws NoSuchPathException {
        int level = yamlManager.getInteger(uuid + ".level");
        yamlManager.delete(uuid + ".level");
        return level;
    }

    public static void setPlayerLevel(UUID uuid, int level) {
        yamlManager.setInteger(uuid + ".level", level);
    }

    public static double getAndDeletePlayerExp(UUID uuid) throws NoSuchPathException {
        double exp = yamlManager.getDouble(uuid + ".exp");
        yamlManager.delete(uuid + ".exp");
        return exp;
    }

    public static void setPlayerExp(UUID uuid, double exp) {
        yamlManager.setDouble(uuid + ".exp", exp);
    }

    public static Map<Integer, ItemStack> getAndDeletePlayerInventory(UUID uuid) throws NoSuchPathException, BadDataException {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        for (String num : yamlManager.getKeys(uuid + ".inventory")) {
            try {
                inventory.put(Integer.parseInt(num), yamlManager.getItemStack(uuid + ".inventory." + num));
            }
            catch (NoSuchPathException e) {
                throw new BadDataException();
            }
        }
        yamlManager.delete(uuid + ".inventory");
        return inventory;
    }

    public static void setPlayerInventory(UUID uuid, Inventory inventory) {
        for (int i = 0; i < inventory.getContents().length; i++) {
            yamlManager.setItemStack(uuid + ".inventory." + i, inventory.getContents()[i]);
        }
    }

    public static void deletePlayerData(UUID uuid) {
        yamlManager.delete(uuid.toString());
    }

    /**
     * Updates the YAML file structure to be compatible with player data version 2.
     */
    @SuppressWarnings("deprecation")
    public static void updateToVersion2() throws UpdateFailedException {
        try {
            // Change keys from player name to UUID
            for (String key : yamlManager.getKeys("")) {
                if (!key.equals("loggers")) {
                    try {
                        yamlManager.swapPath(Bukkit
                            .getOfflinePlayer(key)
                            .getUniqueId()
                            .toString(), key);
                    } catch (NoSuchPathException e) {
                        throw new UpdateFailedException();
                    }
                }
            }

            // Change loggers from player name to UUID
            List<String> oldLoggers = yamlManager.getStringList("loggers");
            List<String> newLoggers = new ArrayList<>();
            oldLoggers.forEach(logger -> newLoggers.add(Bukkit.getOfflinePlayer(logger).getUniqueId().toString()));
            yamlManager.setStringList("loggers", newLoggers);
        }
        catch (NullPointerException | BadDataException e) {
            throw new UpdateFailedException();
        }
        catch (NoSuchPathException ignored) {}
    }
}
