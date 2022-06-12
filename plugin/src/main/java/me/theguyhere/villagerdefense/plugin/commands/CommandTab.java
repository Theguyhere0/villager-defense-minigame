package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
    private final String[] adminFirstArgs = {"lobby", "infoBoard", "leaderboard", "arena"};
    private final String[] displayMenuArgs = {"set", "teleport", "center", "remove"};

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

        // The command tree for admin commands
        else if (args[0].equalsIgnoreCase("admin")) {
            StringBuilder argFrag;
            FileConfiguration arenaData = Main.plugin.getArenaData();

            // First args
            if (args.length == 2) {
                argFrag = new StringBuilder(args[1].toLowerCase());
                Arrays.stream(adminFirstArgs).forEach(arg -> {
                    if (arg.toLowerCase().startsWith(argFrag.toString()))
                        result.add(arg);
                });

                return result;
            }

            switch (args[1].toLowerCase()) {
                case "lobby":
                    if (args.length != 3)
                        return result;

                    argFrag = new StringBuilder(args[2].toLowerCase());
                    Arrays.stream(displayMenuArgs).forEach(arg -> {
                        if (arg.startsWith(argFrag.toString()))
                            result.add(arg);
                    });

                    return result;
                case "infoboard":
                    ConfigurationSection infoBoardSection = arenaData.getConfigurationSection("infoBoard");

                    if (args.length == 3 && infoBoardSection != null) {
                        argFrag = new StringBuilder(args[2].toLowerCase());
                        infoBoardSection.getKeys(false).forEach(key -> {
                            if (key.startsWith(argFrag.toString()))
                                result.add(key);
                        });
                    }

                    else if (args.length == 4) {
                        argFrag = new StringBuilder(args[3].toLowerCase());
                        Arrays.stream(displayMenuArgs).forEach(arg -> {
                            if (arg.startsWith(argFrag.toString()))
                                result.add(arg);
                        });
                    }

                    return result;

                case "leaderboard":
                case "arena":
                default:
            }
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
