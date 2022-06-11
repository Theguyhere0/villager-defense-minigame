package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandTab implements TabCompleter {
    private final String[] arguments = {"admin", "help", "leave", "stats", "kits", "join", "crystals", "start",
            "end", "delay", "fix", "debug", "die", "reload", "open", "close", "achievements"};
    private final String[] playerNameCommands = {"stats", "crystals"};
    private final String[] arenaNameCommands = {"start", "end", "delay"};

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                      @NotNull String label, String[] args) {
        // Complete as characters are added
        List<String> result = new ArrayList<>();

        // Argument after "vd"
        if (args.length == 1) {
            for (String a : arguments)
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
        }

        // For commands that need player names
        else if (Arrays.stream(playerNameCommands).anyMatch(arg -> args[0].equalsIgnoreCase(arg)) && args.length == 2) {
            Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(name -> {
                if (name.toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add(name);
            });
        }

        // For commands that need arena names
        else if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("end") ||
                args[0].equalsIgnoreCase("delay") || args[0].equalsIgnoreCase("open") ||
                args[0].equalsIgnoreCase("close")) {
            StringBuilder nameFrag = new StringBuilder(args[1]);
            for (int i = 0; i < args.length - 2; i++)
                nameFrag.append(" ").append(args[i + 2]);
            GameManager.getArenas().values().stream().filter(Objects::nonNull).map(Arena::getName).forEach(name -> {
                if (name.toLowerCase().startsWith(nameFrag.toString()))
                    result.add(name);
            });
        }

        // Debug command needing numbers 0 through 3
        else if (args[0].equalsIgnoreCase("debug"))
            for (int i = 0; i < 4; i++)
                result.add(String.valueOf(i));

        // Help command needing numbers 1 through 3
        else if (args[0].equalsIgnoreCase("help"))
            for (int i = 1; i < 4; i++)
                result.add(String.valueOf(i));

        return result;
    }
}
