package me.theguyhere.villagerdefense.plugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
        Arena arena = null;
        UUID id = null;

        // Attempt to get either the arena or the player from the command
        try {
            arena = GameManager.getArena(params.substring(params.indexOf('_') + 1));
        } catch (ArenaNotFoundException ignored) {
        }
        try {
            id = Bukkit.getOfflinePlayer(params.substring(params.indexOf('_') + 1)).getUniqueId();
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
        else if (params.contains("crystalBalance_") && id != null)
            return Integer.toString(PlayerManager.getCrystalBalance(id));
        else if (params.contains("topBalance_") && id != null)
            return Integer.toString(PlayerManager.getTopBalance(id));
        else if (params.contains("topKills_") && id != null)
            return Integer.toString(PlayerManager.getTopKills(id));
        else if (params.contains("topWave_") && id != null)
            return Integer.toString(PlayerManager.getTopWave(id));
        else if (params.contains("totalGems_") && id != null)
            return Integer.toString(PlayerManager.getTotalGems(id));
        else if (params.contains("totalKills_") && id != null)
            return Integer.toString(PlayerManager.getTotalKills(id));

        // Unknown placeholder
        return null;
    }
}
