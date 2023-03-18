package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.GameController;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import me.theguyhere.villagerdefense.plugin.managers.DataManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import me.theguyhere.villagerdefense.plugin.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Executes command to attempt fixing and updating files.
 */
@SuppressWarnings("deprecation")
class CommandFixFiles {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.FIX.getArg()))
            return;

        boolean fixed = false;
        FileConfiguration playerData = Main.getPlayerData();
        FileConfiguration arenaData = Main.getArenaData();
        FileConfiguration customEffects = Main.getCustomEffects();

        // Check if config.yml is outdated
        if (Main.plugin.getConfig().getInt("version") < Main.configVersion)
            notifyManualUpdate(sender, "config.yml");

        // Check if arenaData.yml is outdated
        int arenaDataVersion = Main.plugin.getConfig().getInt("arenaData");
        boolean arenaAbort = false;
        if (arenaDataVersion < 4) {
            try {
                // Transfer portals
                Objects.requireNonNull(arenaData.getConfigurationSection("portal"))
                        .getKeys(false).forEach(arenaID -> {
                            DataManager.setConfigurationLocation("a" + arenaID + ".portal",
                                    DataManager.getConfigLocation("portal." + arenaID));
                            arenaData.set("portal." + arenaID, null);
                        });
                arenaData.set("portal", null);

                // Transfer arena boards
                Objects.requireNonNull(arenaData.getConfigurationSection("arenaBoard"))
                        .getKeys(false).forEach(arenaID -> {
                            DataManager.setConfigurationLocation("a" + arenaID + ".arenaBoard",
                                    DataManager.getConfigLocation("arenaBoard." + arenaID));
                            arenaData.set("arenaBoard." + arenaID, null);
                        });
                arenaData.set("arenaBoard", null);

                Main.saveArenaData();

                // Reload portals
                GameController.refreshPortals();

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("arenaData", 4);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "arenaData.yml", 4);
            } catch (Exception e) {
                arenaAbort = true;
                notifyManualUpdate(sender, "arenaData.yml");
            }
        }
        if (arenaDataVersion < 5 && !arenaAbort) {
            try {
                // Translate waiting sounds
                Objects.requireNonNull(arenaData.getConfigurationSection("")).getKeys(false)
                        .forEach(key -> {
                            String soundPath = key + ".sounds.waiting";
                            if (key.charAt(0) == 'a' && key.length() < 4 && arenaData.contains(soundPath)) {
                                int oldValue = arenaData.getInt(soundPath);
                                switch (oldValue) {
                                    case 0:
                                        arenaData.set(soundPath, "cat");
                                        break;
                                    case 1:
                                        arenaData.set(soundPath, "blocks");
                                        break;
                                    case 2:
                                        arenaData.set(soundPath, "far");
                                        break;
                                    case 3:
                                        arenaData.set(soundPath, "strad");
                                        break;
                                    case 4:
                                        arenaData.set(soundPath, "mellohi");
                                        break;
                                    case 5:
                                        arenaData.set(soundPath, "ward");
                                        break;
                                    case 9:
                                        arenaData.set(soundPath, "chirp");
                                        break;
                                    case 10:
                                        arenaData.set(soundPath, "stal");
                                        break;
                                    case 11:
                                        arenaData.set(soundPath, "mall");
                                        break;
                                    case 12:
                                        arenaData.set(soundPath, "wait");
                                        break;
                                    case 13:
                                        arenaData.set(soundPath, "pigstep");
                                        break;
                                    default:
                                        arenaData.set(soundPath, "none");
                                }
                            }
                        });
                Main.saveArenaData();

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("arenaData", 5);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "arenaData.yml", 5);
            } catch (Exception e) {
                arenaAbort = true;
                notifyManualUpdate(sender, "arenaData.yml");
            }
        }
        if (arenaDataVersion < 6 && !arenaAbort) {
            try {
                // Take old data and put into new format
                Objects.requireNonNull(arenaData.getConfigurationSection("")).getKeys(false)
                        .stream().filter(key -> key.contains("a") && key.length() < 4)
                        .forEach(key -> {
                            int arenaId = Integer.parseInt(key.substring(1));
                            String newPath = "arena." + arenaId;

                            // Single key-value pairs
                            moveData(arenaData, newPath + ".name", key + ".name");
                            moveData(arenaData, newPath + ".max", key + ".max");
                            moveData(arenaData, newPath + ".min", key + ".min");
                            moveData(arenaData, newPath + ".spawnTable", key + ".spawnTable");
                            moveData(arenaData, newPath + ".maxWaves", key + ".maxWaves");
                            moveData(arenaData, newPath + ".waveTimeLimit", key + ".waveTimeLimit");
                            moveData(arenaData, newPath + ".difficulty", key + ".difficulty");
                            moveData(arenaData, newPath + ".closed", key + ".closed");
                            moveData(arenaData, newPath + ".normal", key + ".normal");
                            moveData(arenaData, newPath + ".dynamicCount", key + ".dynamicCount");
                            moveData(arenaData, newPath + ".dynamicDifficulty",
                                    key + ".dynamicDifficulty");
                            moveData(arenaData, newPath + ".dynamicPrices",
                                    key + ".dynamicPrices");
                            moveData(arenaData, newPath + ".difficultyLabel",
                                    key + ".difficultyLabel");
                            moveData(arenaData, newPath + ".dynamicLimit", key + ".dynamicLimit");
                            moveData(arenaData, newPath + ".wolf", key + ".wolf");
                            moveData(arenaData, newPath + ".golem", key + ".golem");
                            moveData(arenaData, newPath + ".expDrop", key + ".expDrop");
                            moveData(arenaData, newPath + ".gemDrop", key + ".gemDrop");
                            moveData(arenaData, newPath + ".community", key + ".community");
                            moveData(arenaData, newPath + ".lateArrival", key + ".lateArrival");
                            moveData(arenaData, newPath + ".enchants", key + ".enchants");
                            moveData(arenaData, newPath + ".bannedKits", key + ".bannedKits");

                            // Config sections
                            moveSection(arenaData, newPath + ".sounds", key + ".sounds");
                            moveSection(arenaData, newPath + ".particles", key + ".particles");
                            moveSection(arenaData, newPath + ".spawn", key + ".spawn");
                            moveSection(arenaData, newPath + ".waiting", key + ".waiting");
                            moveSection(arenaData, newPath + ".corner1", key + ".corner1");
                            moveSection(arenaData, newPath + ".corner2", key + ".corner2");
                            moveSection(arenaData, newPath + ".arenaBoard", key + ".arenaBoard");
                            moveSection(arenaData, newPath + ".portal", key + ".portal");

                            // Nested sections
                            moveNested(arenaData, newPath + ".monster", key + ".monster");
                            moveNested(arenaData, newPath + ".monster", key + ".monsters");
                            moveNested(arenaData, newPath + ".villager", key + ".villager");
                            moveNested(arenaData, newPath + ".records", key + ".records");
                            moveInventory(arenaData, newPath + ".customShop", key + ".customShop");

                            // Remove old structure
                            arenaData.set(key, null);
                        });

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("arenaData", 6);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "arenaData.yml", 6);
            } catch (Exception e) {
                arenaAbort = true;
                notifyManualUpdate(sender, "arenaData.yml");
            }
        }
        if (arenaDataVersion < 7 && !arenaAbort) {
            try {
                // Take old data and translate to new format
                Objects.requireNonNull(arenaData.getConfigurationSection("arena")).getKeys(false)
                        .forEach(key -> {
                            String newPath = "arena." + key;

                            // Translate over kits from names to IDs
                            if (arenaData.contains(newPath + ".bannedKits"))
                                arenaData.set(newPath + ".bannedKits",
                                        arenaData.getStringList(newPath + ".bannedKits")
                                                .stream().filter(kit -> !kit.isEmpty())
                                                .map(kit -> Objects.requireNonNull(
                                                        Kit.getKitByName(kit)).getID())
                                                .collect(Collectors.toList()));

                            // Translate over challenges from names to IDs
                            if (arenaData.contains(newPath + ".forcedChallenges"))
                                arenaData.set(newPath + ".forcedChallenges",
                                        arenaData.getStringList(newPath + ".forcedChallenges")
                                                .stream().filter(challenge -> !challenge.isEmpty())
                                                .map(challenge -> Objects.requireNonNull(
                                                        Challenge.getChallengeByName(challenge)).getID())
                                                .collect(Collectors.toList()));

                            Main.saveArenaData();
                        });

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("arenaData", 7);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "arenaData.yml", 7);
            } catch (Exception e) {
                arenaAbort = true;
                notifyManualUpdate(sender, "arenaData.yml");
            }
        }
        if (arenaDataVersion < 8 && !arenaAbort) {
            try {
                Objects.requireNonNull(arenaData.getConfigurationSection("arena")).getKeys(false)
                        .forEach(key -> {
                            String newPath = "arena." + key;

                            // Remove legacy data
                            if (arenaData.contains(newPath + ".normal"))
                                arenaData.set(newPath + ".normal", null);
                            if (arenaData.contains(newPath + ".expDrop"))
                                arenaData.set(newPath + ".expDrop", null);
                            if (arenaData.contains(newPath + ".gemDrop"))
                                arenaData.set(newPath + ".gemDrop", null);
                            if (arenaData.contains(newPath + ".enchants"))
                                arenaData.set(newPath + ".enchants", null);
                            if (arenaData.contains(newPath + ".customShop"))
                                arenaData.set(newPath + ".customShop", null);
                            if (arenaData.contains(newPath + ".custom"))
                                arenaData.set(newPath + ".custom", null);

                            // Set default villager type
                            if (!arenaData.contains(newPath + ".villagerType"))
                                arenaData.set(newPath + ".villagerType", "plains");

                            Main.saveArenaData();
                        });

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("arenaData", 8);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "arenaData.yml", 8);
            } catch (Exception e) {
                notifyManualUpdate(sender, "arenaData.yml");
            }
        }

        // Check if playerData.yml is outdated
        int playerDataVersion = Main.plugin.getConfig().getInt("playerData");
        boolean playerAbort = false;
        if (playerDataVersion < 2) {
            try {
                // Transfer player names to UUID
                Objects.requireNonNull(playerData.getConfigurationSection("")).getKeys(false)
                        .forEach(key -> {
                            if (!key.equals("loggers")) {
                                playerData.set(
                                        Bukkit.getOfflinePlayer(key).getUniqueId().toString(),
                                        playerData.get(key)
                                );
                                playerData.set(key, null);
                            }
                        });
                Main.savePlayerData();

                // Reload everything
                GameController.refreshAll();

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("playerData", 2);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "playerData.yml", 2);
            } catch (Exception e) {
                playerAbort = true;
                notifyManualUpdate(sender, "playerData.yml");
            }
        }
        if (playerDataVersion < 3 && !playerAbort) {
            try {
                Objects.requireNonNull(playerData.getConfigurationSection("")).getKeys(false)
                        .stream().filter(key -> !key.equals("loggers")).forEach(key -> {
                            String newPath = key + ".achievements";
                            List<String> achievements = playerData.getStringList(newPath);

                            // Check for typo and correct
                            if (achievements.contains("pacifisKills")) {
                                achievements.remove("pacifisKills");
                                achievements.add("pacifistKills");
                                playerData.set(newPath, achievements);
                            }

                            // Map old kit keys to new kit keys
                            if (playerData.contains(key + ".kits")) {
                                Objects.requireNonNull(playerData.getConfigurationSection(
                                                key + ".kits"))
                                        .getKeys(false).stream()
                                        .filter(kit -> Kit.getKitByName(kit) != null)
                                        .forEach(kit -> moveData(
                                                playerData,
                                                key + ".kits." + Objects.requireNonNull(
                                                        Kit.getKitByName(kit)).getID(),
                                                key + ".kits." + kit)
                                        );
                            }

                            Main.savePlayerData();
                        });

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("playerData", 3);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "playerData.yml", 3);
            } catch (Exception e) {
                notifyManualUpdate(sender, "playerData.yml");
            }
        }

        // Update default spawn table
        if (Main.plugin.getConfig().getInt("spawnTableStructure") < Main.spawnTableVersion ||
                Main.plugin.getConfig().getInt("spawnTableDefault") < Main.defaultSpawnVersion) {
            // Flip flag
            fixed = true;

            // Fix
            Main.plugin.saveResource("spawnTables/default.yml", true);
            Main.plugin.getConfig().set("spawnTableDefault", Main.defaultSpawnVersion);
            Main.plugin.saveConfig();

            // Notify
            if (sender instanceof Player) {
                PlayerManager.notifySuccess(
                        (Player) sender,
                        LanguageManager.confirms.autoUpdate,
                        new ColoredMessage(ChatColor.AQUA, "default.yml"),
                        new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.defaultSpawnVersion)));
                PlayerManager.notifyAlert(
                        (Player) sender,
                        LanguageManager.messages.manualUpdateWarn,
                        new ColoredMessage(ChatColor.AQUA, "All other spawn files")
                );
            }
            CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, 0,
                    "default.yml", Integer.toString(Main.defaultSpawnVersion));
            CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, 0,
                    "All other spawn files");
        }

        // Check if spawn table structure can be considered updated
        boolean noCustomSpawnTables = false;
        try (Stream<Path> stream = Files.list(Paths.get(Main.plugin.getDataFolder().getPath() + "/spawnTables"))) {
            noCustomSpawnTables = stream.count() < 2;
        } catch (IOException ignored) {
        }
        if (noCustomSpawnTables && Main.plugin.getConfig().getInt("spawnTableStructure") < Main.spawnTableVersion) {
            // Flip flag
            fixed = true;

            // Fix
            Main.plugin.getConfig().set("spawnTableStructure", Main.spawnTableVersion);
            Main.plugin.saveConfig();

            // Notify
            if (sender instanceof Player) {
                PlayerManager.notifySuccess(
                        (Player) sender,
                        LanguageManager.confirms.autoUpdate,
                        new ColoredMessage(ChatColor.AQUA, "Spawn tables"),
                        new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.spawnTableVersion)));
            }
            CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, 0,
                    "Spawn tables", Integer.toString(Main.spawnTableVersion));
        }

        // Update default language file
        if (Main.plugin.getConfig().getInt("languageFile") < Main.languageFileVersion) {
            // Flip flag
            fixed = true;

            // Fix
            Main.plugin.saveResource("languages/en_US.yml", true);
            Main.plugin.getConfig().set("languageFile", Main.languageFileVersion);
            Main.plugin.saveConfig();

            // Notify
            if (sender instanceof Player) {
                PlayerManager.notifySuccess(
                        (Player) sender,
                        LanguageManager.confirms.autoUpdate,
                        new ColoredMessage(ChatColor.AQUA, "en_US.yml"),
                        new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.languageFileVersion))
                );
                PlayerManager.notifyAlert(
                        (Player) sender,
                        LanguageManager.messages.manualUpdateWarn,
                        new ColoredMessage(ChatColor.AQUA, "All other language files")
                );
                PlayerManager.notifyAlert((Player) sender, LanguageManager.messages.reloadPlugin);
            }
            CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, 0,
                    "en_US.yml", Integer.toString(Main.languageFileVersion));
            CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, 0,
                    "All other language files");
            CommunicationManager.debugError(LanguageManager.messages.reloadPlugin, 0);
        }

        // Check if customEffects.yml is outdated
        int customEffectsVersion = Main.plugin.getConfig().getInt("customEffects");
        boolean customAbort = false;
        if (customEffectsVersion < 2) {
            try {
                // Modify threshold keys
                String path = "unlimited.onGameEnd";
                ConfigurationSection section = customEffects.getConfigurationSection(path);
                if (section != null)
                    section.getKeys(false).stream().filter(key -> !key.contains("-") && !key.contains("<"))
                            .forEach(key -> {
                                moveData(customEffects, path + ".^" + key, path + "." + key);
                                Main.saveCustomEffects();
                            });

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("customEffects", 2);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "customEffects.yml", 2);
            } catch (Exception e) {
                customAbort = true;
                notifyManualUpdate(sender, "customEffects.yml");
            }
        }
        if (customEffectsVersion < 3 && !customAbort) {
            try {
                // Move to correct sections
                moveSection(customEffects, "unlimited.onGameEndLobby", "unlimited.onGameEnd");
                moveData(customEffects, "limited.onGameWinLobby", "limited.onGameWin");
                moveData(customEffects, "limited.onGameLoseLobby", "limited.onGameLose");
                Main.saveCustomEffects();

                // Flip flag and update config.yml
                fixed = true;
                Main.plugin.getConfig().set("customEffects", 3);
                Main.plugin.saveConfig();

                // Notify
                notifyAutoUpdate(sender, "customEffects.yml", 3);
            } catch (Exception e) {
                notifyManualUpdate(sender, "customEffects.yml");
            }
        }

        // Message to player depending on whether the command fixed anything, then reload if fixed
        if (!fixed) {
            if (sender instanceof Player)
                PlayerManager.notifyAlert((Player) sender, LanguageManager.messages.noAutoUpdate);
            else CommunicationManager.debugInfo(LanguageManager.messages.noAutoUpdate, 0);
        } else {
            // Notify of reload
            if (sender instanceof Player)
                PlayerManager.notifyAlert((Player) sender, "Reloading plugin data");
            else CommunicationManager.debugInfo("Reloading plugin data", 0);

            Main.plugin.reload();
        }
    }

    private static void moveData(FileConfiguration config, String to, String from) {
        if (config.get(from) != null) {
            config.set(to, config.get(from));
            config.set(from, null);
        }
    }

    private static void moveSection(FileConfiguration config, String to, String from) {
        if (config.contains(from)) {
            Objects.requireNonNull(config.getConfigurationSection(from)).getKeys(false).forEach(key ->
                    moveData(config, to + "." + key, from + "." + key));
            config.set(from, null);
        }
    }

    private static void moveNested(FileConfiguration config, String to, String from) {
        if (config.contains(from)) {
            Objects.requireNonNull(config.getConfigurationSection(from)).getKeys(false).forEach(key ->
                    moveSection(config, to + "." + key, from + "." + key));
            config.set(from, null);
        }
    }

    private static void moveInventory(FileConfiguration config, String to, String from) {
        if (config.contains(from)) {
            Objects.requireNonNull(config.getConfigurationSection(from)).getKeys(false).forEach(key ->
                    config.set(to + "." + key, config.getItemStack(from + "." + key)));
            config.set(from, null);
        }
    }

    private static void notifyManualUpdate(CommandSender sender, String file) {
        if (sender instanceof Player)
            PlayerManager.notifyAlert(
                    (Player) sender,
                    LanguageManager.messages.manualUpdateWarn,
                    new ColoredMessage(ChatColor.AQUA, file)
            );
        else CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, 0, file);
    }

    private static void notifyAutoUpdate(CommandSender sender, String file, int version) {
        if (sender instanceof Player)
            PlayerManager.notifySuccess(
                    (Player) sender,
                    LanguageManager.confirms.autoUpdate,
                    new ColoredMessage(ChatColor.AQUA, file),
                    new ColoredMessage(ChatColor.AQUA, Integer.toString(version))
            );
        CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, 0, file,
                Integer.toString(version));
    }
}
