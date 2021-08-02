package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandTab implements TabCompleter {
    private final Game game;

    private final String[] arguments = {"admin", "help", "leave", "stats", "kits", "select", "crystals", "start",
            "end", "delay"};

    public CommandTab(Game game) {
        this.game = game;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        // Complete as characters are added
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments)
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
        } else if (args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("crystals")
                && args.length == 2) {
            Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(name -> {
                if (name.toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add(name);
            });
        } else if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("end")) {
            StringBuilder nameFrag = new StringBuilder(args[1]);
            for (int i = 0; i < args.length - 2; i++)
                nameFrag.append(" ").append(args[i + 2]);
            game.arenas.stream().filter(Objects::nonNull).map(Arena::getName).forEach(name -> {
                if (name.toLowerCase().startsWith(nameFrag.toString()))
                    result.add(name);
            });
        }

        return result;
    }
}
