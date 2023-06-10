package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TabCompleterImp implements TabCompleter {

	@Override
	public List<String> onTabComplete(
		@NotNull CommandSender commandSender, @NotNull Command command,
		@NotNull String label, String[] args
	) {
		// Complete as characters are added
		List<String> result = new ArrayList<>();

		// Argument after "vd"
		if (CommandGuard.checkArgsLengthMatch(args, 1)) {
			for (CommandExecImp.Argument a : CommandExecImp.Argument.values())
				if (a.getArg().toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(a.getArg());
		}

		// For commands that need player names
		else if (CommandGuard.checkArgsLengthMatch(args, 2) &&
			(CommandGuard.checkArg(args, 0, CommandExecImp.Argument.STATS.getArg()) ||
				CommandGuard.checkArg(args, 0, CommandExecImp.Argument.CRYSTALS.getArg()))) {
			Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(name -> {
				if (name.toLowerCase().startsWith(args[1].toLowerCase()))
					result.add(name);
			});
		}

		// For commands that need arena names only
		else if (CommandGuard.checkArg(args, 0, CommandExecImp.Argument.START.getArg()) ||
			CommandGuard.checkArg(args, 0, CommandExecImp.Argument.END.getArg()) ||
			CommandGuard.checkArg(args, 0, CommandExecImp.Argument.DELAY.getArg())) {
			StringBuilder nameFrag = new StringBuilder(args[1].toLowerCase());
			for (int i = 0; i < args.length - 2; i++)
				nameFrag.append(" ").append(args[i + 2]);
			GameController.getArenas().values().stream().filter(Objects::nonNull).map(Arena::getName).forEach(name -> {
				if (name.toLowerCase().startsWith(nameFrag.toString()))
					result.add(name);
			});
		}

		// The command tree for admin commands
		else if (CommandGuard.checkArg(args, 0, CommandExecImp.Argument.ADMIN.getArg())) {
			StringBuilder argFrag;

			// First args
			if (CommandGuard.checkArgsLengthMatch(args, 2)) {
				argFrag = new StringBuilder(args[1].toLowerCase());
				Arrays.stream(CommandModifyArenaData.Argument.values()).forEach(arg -> {
					if (arg.getArg().toLowerCase().startsWith(argFrag.toString()))
						result.add(arg.getArg());
				});

				return result;
			}

			if (CommandGuard.checkArg(args, 1, CommandModifyArenaData.Argument.LOBBY.getArg())) {
				if (!CommandGuard.checkArgsLengthMatch(args, 3))
					return result;

				argFrag = new StringBuilder(args[2].toLowerCase());
				Arrays.stream(CommandModifyArenaData.LocationOptionArgument.values()).forEach(arg -> {
					if (arg.getArg().startsWith(argFrag.toString()))
						result.add(arg.getArg());
				});
			}
			else if (CommandGuard.checkArg(args, 1, CommandModifyArenaData.Argument.INFOBOARD.getArg())) {
				ConfigurationSection infoBoardSection = Main.getArenaData()
					.getConfigurationSection("infoBoard");

				if (CommandGuard.checkArgsLengthMatch(args, 3)) {
					argFrag = new StringBuilder(args[2].toLowerCase());

					if (infoBoardSection != null) {
						infoBoardSection.getKeys(false).forEach(key -> {
							if (key.startsWith(argFrag.toString()))
								result.add(key);
						});
					}
					if (CommandModifyArenaData.CREATE.startsWith(argFrag.toString()))
						result.add(CommandModifyArenaData.CREATE);
				}

				else if (CommandGuard.checkArgsLengthMatch(args, 4)) {
					argFrag = new StringBuilder(args[3].toLowerCase());
					Arrays.stream(CommandModifyArenaData.LocationOptionArgument.values()).forEach(arg -> {
						if (arg.getArg().startsWith(argFrag.toString()))
							result.add(arg.getArg());
					});
				}
			}
			else if (CommandGuard.checkArg(args, 1, CommandModifyArenaData.Argument.LEADERBOARD.getArg())) {
				if (CommandGuard.checkArgsLengthMatch(args, 3)) {
					argFrag = new StringBuilder(args[2]);
					Arrays.stream(CommandModifyArenaData.LeaderboardTypeArgument.values()).forEach(arg -> {
						if (arg.getArg().startsWith(argFrag.toString()))
							result.add(arg.getArg());
					});
				}

				else if (CommandGuard.checkArgsLengthMatch(args, 4)) {
					argFrag = new StringBuilder(args[3].toLowerCase());
					Arrays.stream(CommandModifyArenaData.LocationOptionArgument.values()).forEach(arg -> {
						if (arg.getArg().startsWith(argFrag.toString()))
							result.add(arg.getArg());
					});
				}
			}
			else if (CommandGuard.checkArg(args, 1, CommandModifyArenaData.Argument.ARENA.getArg())) {
				if (CommandGuard.checkArgsLengthMatch(args, 3)) {
					argFrag = new StringBuilder(args[2]);
					Arrays.stream(CommandModifyArenaData.ArenaOperationArgument.values()).forEach(arg -> {
						if (arg.getArg().startsWith(argFrag.toString()))
							result.add(arg.getArg());
					});
				}

				else {
					argFrag = new StringBuilder(args[3].toLowerCase());
					for (int i = 0; i < args.length - 4; i++)
						argFrag.append(" ").append(args[i + 4]);
					GameController.getArenas().values().stream().filter(Objects::nonNull).map(Arena::getName)
						.forEach(name -> {
							if (name.toLowerCase().startsWith(argFrag.toString()))
								result.add(name);
						});
				}
			}

			return result;
		}

		// Debug command needing numbers 0 through 3
		else if (args[0].equalsIgnoreCase(CommandExecImp.Argument.DEBUG.getArg()))
			for (int i = 0; i < 4; i++)
				result.add(String.valueOf(i));

			// Help command needing numbers 1 through 3
		else if (CommandGuard.checkArg(args, 0, CommandExecImp.Argument.HELP.getArg()))
			for (int i = 1; i < 4; i++)
				result.add(String.valueOf(i));

		return result;
	}
}
