package me.theguyhere.villagerdefense.genListeners;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.stream.Collectors;

public class Commands implements CommandExecutor {
	Main plugin;
	Inventories inv;
	Game game;
	
	public Commands(Main plugin, Inventories inv, Game game) {
		this.plugin = plugin;
		this.inv = inv;
		this.game = game;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// Open arena inventory
		if (label.equalsIgnoreCase("vd")) {
			FileConfiguration language = plugin.getLanguageData();

			// Check for player executing command
			if (!(sender instanceof Player)) {
				sender.sendMessage("Bad console!");
				return true;
			}
			
			Player player = (Player) sender;

			// No arguments
			if (args.length == 0) {
				player.sendMessage(Utils.notify("&c" + language.getString("commandError")));
				return true;
			}

			// No arguments
			if (args[0].equalsIgnoreCase("admin")) {
				// Check for permission to use the command
				if (!player.hasPermission("vd.use")) {
					player.sendMessage(Utils.notify("&c" + language.getString("permissionError")));
					return true;
				}

				player.openInventory(inv.createArenasInventory());
				return true;
			}
			
			// Redirects to wiki for help
			// NOT FINALIZED
			if (args[0].equalsIgnoreCase("help")) {
				player.sendMessage(Utils.notify("&6" + language.getString("info")));
				TextComponent message = new TextComponent("Visit the wiki!");
				message.setBold(true);
				message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
						"https://github.com/Theguyhere0/villager-defense-minigame/wiki"));
				player.spigot().sendMessage(message);
				return true;
			}
			
			// Player leaves a game
			if (args[0].equalsIgnoreCase("leave")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));
				return true;
			}

			// Player checks stats
			if (args[0].equalsIgnoreCase("stats")) {
				if (args.length == 1)
					player.openInventory(inv.createPlayerStatsInventory(player.getName()));
				else if (plugin.getPlayerData().contains(args[1]))
					player.openInventory(inv.createPlayerStatsInventory(args[1]));
				else player.sendMessage(Utils.notify("&c" + args[1] + " " + language.getString("noStats")));
				return true;
			}

			// Player checks kits
			if (args[0].equalsIgnoreCase("kits")) {
				player.openInventory(inv.createPlayerKitsInventory(player.getName(), player.getName()));
				return true;
			}

			// Player selects kits
			if (args[0].equalsIgnoreCase("select")) {
				// Check if player is in a game
				if (game.arenas.stream().filter(Objects::nonNull).noneMatch(arena -> arena.hasPlayer(player))) {
					player.sendMessage(Utils.notify("&c" + language.getString("inGameError")));
					return true;
				}

				Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(arena1 -> arena1.hasPlayer(player))
						.collect(Collectors.toList()).get(0);
				VDPlayer gamer = arena.getPlayer(player);

				// Check arena is in session
				if (arena.isActive() && arena.getActives().contains(gamer)) {
					player.sendMessage(Utils.notify("&c" + language.getString("kitChangeError")));
					return true;
				}

				// Check for unqualified spectators
				if (arena.getSpectators().contains(gamer)  &&
						!plugin.getPlayerData().getBoolean(player.getName() + ".kits.Phantom")) {
					player.sendMessage(Utils.notify("&c" + language.getString("spectatorError")));
					return true;
				}

				// Open inventory
				player.openInventory(inv.createSelectKitsInventory(player, arena));
				return true;
			}

			if (args[0].equalsIgnoreCase("crystals")) {
				// Check for permission to use the command
				if (!player.hasPermission("vd.crystals")) {
					player.sendMessage(Utils.notify("&c" + language.getString("permissionError")));
					return true;
				}

				// Check for valid command format
				if (args.length != 3) {
					player.sendMessage(Utils.notify("&cCommand format: /vd crystals [player] [change amount]"));
					return true;
				}

				// Check for valid player
				if (!plugin.getPlayerData().contains(args[1])) {
					player.sendMessage(Utils.notify("&cInvalid player!"));
					return true;
				}

				// Check for valid amount
				try {
					int amount = Integer.parseInt(args[2]);
					plugin.getPlayerData().set(args[1] + ".crystalBalance",
							Math.max(plugin.getPlayerData().getInt(args[1] + ".crystalBalance") + amount, 0));
					plugin.savePlayerData();
					player.sendMessage(Utils.notify("&a" + args[1] +"'s crystal balance was set to " +
							plugin.getPlayerData().getInt(args[1] + ".crystalBalance")));
					return true;
				} catch (Exception e) {
					player.sendMessage(Utils.notify("&cAmount must be an integer!"));
					return true;
				}
			}

			// No valid command sent
			player.sendMessage(Utils.notify("&c" + language.getString("commandError")));
			return true;
		}
		return false;
	}
}
