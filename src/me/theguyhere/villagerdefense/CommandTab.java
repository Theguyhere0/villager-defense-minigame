package me.theguyhere.villagerdefense;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandTab implements TabCompleter {

    private final List<String> arguments = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        // Arguments after "vd"
        if (arguments.isEmpty()) {
            arguments.add("help");
            arguments.add("leave");
        }

        // Complete as characters are added
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments)
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            return result;
        }

        return null;
    }
}
