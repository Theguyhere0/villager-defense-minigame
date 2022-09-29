package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandFormatException;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandExecImp implements CommandExecutor {
	// Maps to keep track of commands that need to be re-triggered for safeguard
	static final Map<UUID, Long> reload = new HashMap<>();
	static final Map<UUID, Long> disable = new HashMap<>();

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
							 String[] args) {
		try {
			// Check for "vd" as first argument
			if (!label.equalsIgnoreCase("vd"))
				return false;

			// Gather who sent command
			Player player;
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				player = null;
			}

			// No additional arguments
			if (CommandGuard.checkArgsLengthMatch(args, 0)) {
				notifyCommandFailure(player);
				return true;
			}

			// Check which command to run
			CommandOpenAdminPanel.execute(args, sender);
			CommandModifyArenaData.execute(args, sender);
			CommandGiveHelp.execute(args, sender);
			CommandLeaveArena.execute(args, sender);
			CommandCheckStats.execute(args, sender);
			CommandCheckKits.execute(args, sender);
			CommandCheckAchievements.execute(args, sender);
			CommandJoinAsPhantom.execute(args, sender);
			CommandModifyCrystalBalance.execute(args, sender);
			CommandForceStartArena.execute(args, sender);
			CommandForceEndArena.execute(args, sender);
			CommandForceDelayArena.execute(args, sender);
			CommandFixFiles.execute(args, sender);
			CommandChangeDebugLevel.execute(args, sender);
			CommandKillPlayer.execute(args, sender);
			CommandReloadPlugin.execute(args, sender);
			CommandDisablePlugin.execute(args, sender);
			CommandTest.execute(args, sender);

			// No valid commend sent
			if (Arrays.stream(Argument.values()).noneMatch(arg -> CommandGuard.checkArg(args, 0, arg.getArg())))
				notifyCommandFailure(sender);
		}
		catch (NullPointerException e) {
			notifyFailure(sender, "The language file is missing some attributes, please update it!");
		}
		catch (CommandFormatException e) {
			notifyFailure(sender, "Usage: " + e.getMessage());
		}
		catch (CommandException e) {
			notifyFailure(sender, e.getMessage());
		}
		return false;
	}

	enum Argument {
		ADMIN("admin"),
		TEST("test"),
		LEAVE("leave"),
		STATS("stats"),
		KITS("kits"),
		ACHIEVEMENTS("achievements"),
		HELP("help"),
		JOIN("join"),
		CRYSTALS("crystals"),
		START("start"),
		END("end"),
		DELAY("delay"),
		FIX("fix"),
		DEBUG("debug"),
		DIE("die"),
		RELOAD("reload"),
		DISABLE("disable")
		;

		private final String arg;

		Argument(String arg) {
			this.arg = arg;
		}

		String getArg() {
			return arg;
		}
	}

	private void notifyCommandFailure(CommandSender sender) {
		if (sender instanceof Player)
			PlayerManager.notifyFailure((Player) sender, LanguageManager.errors.command,
					new ColoredMessage(ChatColor.AQUA, CommandGiveHelp.COMMAND_FORMAT));
		else CommunicationManager.debugError(LanguageManager.errors.command, 0,
				CommandGiveHelp.COMMAND_FORMAT.substring(1));
	}

	static void notifyFailure(CommandSender sender, String message) {
		if (sender instanceof Player)
			PlayerManager.notifyFailure((Player) sender, message);
		else CommunicationManager.debugError(message, 0);
	}

	static void notifySuccess(CommandSender sender, String message) {
		if (sender instanceof Player)
			PlayerManager.notifySuccess((Player) sender, message);
		else CommunicationManager.debugInfo(message, 0);
	}
}
