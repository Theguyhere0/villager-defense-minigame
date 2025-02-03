package me.theguyhere.villagerdefense.plugin.data;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class VDExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "vd";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Theguyhere";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        FileConfiguration playerData = Main.getPlayerData();
        Arena arena = null;
        OfflinePlayer player = null;

        // Attempt to get either the arena or the player from the command
        try {
            arena = GameManager.getArena(params.substring(params.indexOf('_') + 1));
        } catch (ArenaNotFoundException ignored) {
        }
        try {
            player = Bukkit.getOfflinePlayer(params.substring(params.indexOf('_') + 1));
        } catch (Exception ignored) {
        }

        // Arena information
        if (params.contains("maxPlayers_") && arena != null)
            return String.valueOf(arena.getMaxPlayers());
        else if (params.contains("minPlayers_") && arena != null)
            return String.valueOf(arena.getMinPlayers());
        else if (params.contains("waveTimeLimit_") && arena != null)
            return String.valueOf(arena.getWaveTimeLimit());
        else if (params.contains("maxWaves_") && arena != null)
            return String.valueOf(arena.getMaxWaves());
        else if (params.contains("currentDifficulty_") && arena != null)
            return String.valueOf(arena.getCurrentDifficulty());
        else if (params.contains("currentWave_") && arena != null)
            return String.valueOf(arena.getCurrentWave());
        else if (params.contains("id_") && arena != null)
            return String.valueOf(arena.getId());
        else if (params.contains("difficultyLabel_") && arena != null)
            return String.valueOf(arena.getDifficultyLabel());
        else if (params.contains("activeCount_") && arena != null)
            return String.valueOf(arena.getActiveCount());
        else if (params.contains("aliveCount_") && arena != null)
            return String.valueOf(arena.getAlive());
        else if (params.contains("difficultyMultiplier_") && arena != null)
            return String.valueOf(arena.getDifficultyMultiplier());
        else if (params.contains("enemies_") && arena != null)
            return String.valueOf(arena.getEnemies());
        else if (params.contains("golemCap_") && arena != null)
            return String.valueOf(arena.getGolemCap());
        else if (params.contains("golems_") && arena != null)
            return String.valueOf(arena.getGolems());
        else if (params.contains("villagers_") && arena != null)
            return String.valueOf(arena.getVillagers());
        else if (params.contains("spectatorCount_") && arena != null)
            return String.valueOf(arena.getSpectatorCount());
        else if (params.contains("ghostCount_") && arena != null)
            return String.valueOf(arena.getGhostCount());
        else if (params.contains("wolfCap_") && arena != null)
            return String.valueOf(arena.getWolfCap());

            // Player information
        else if (params.contains("crystalBalance_") && player != null)
            return Integer.toString(playerData.getInt(player.getUniqueId() + ".crystalBalance"));
        else if (params.contains("topBalance_") && player != null)
            return Integer.toString(playerData.getInt(player.getUniqueId() + ".topBalance"));
        else if (params.contains("topKills_") && player != null)
            return Integer.toString(playerData.getInt(player.getUniqueId() + ".topKills"));
        else if (params.contains("topWave_") && player != null)
            return Integer.toString(playerData.getInt(player.getUniqueId() + ".topWave"));
        else if (params.contains("totalGems_") && player != null)
            return Integer.toString(playerData.getInt(player.getUniqueId() + ".totalGems"));
        else if (params.contains("totalKills_") && player != null)
            return Integer.toString(playerData.getInt(player.getUniqueId() + ".totalKills"));

        // Unknown placeholder
        return null;
    }
}
