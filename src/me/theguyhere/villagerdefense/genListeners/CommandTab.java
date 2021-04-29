package me.theguyhere.villagerdefense.genListeners;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.List;

public class CommandTab implements TabCompleter {

    private final String[] arguments = {"admin", "help", "leave", "stats", "kits", "select", "crystals"};

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
        }

        return result;
    }
}
