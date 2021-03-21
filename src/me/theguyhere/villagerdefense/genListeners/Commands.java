package me.theguyhere.villagerdefense.genListeners;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.ArenaResetEvent;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.theguyhere.villagerdefense.game.Game;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
		
//		Open arena inventory
		if (label.equalsIgnoreCase("vd")) {
			// Check for player executing command
			if (!(sender instanceof Player)) {
				sender.sendMessage("Bad console!");
				return true;
			}
			
			Player player = (Player) sender;
			
//			Check for permission to use the command
			if (!player.hasPermission("vd.use")) {
				player.sendMessage(ChatColor.RED + "You do not have permission!");
				return true;
			}
			
//			No arguments
			if (args.length == 0) {
				player.openInventory(inv.createArenasInventory());
				return true;
			}
			
//			Redirects to wiki for help
//			NOT FINALIZED
			if (args[0].equalsIgnoreCase("help")) {
				player.sendMessage(Utils.format("&6For more information, click below to visit the wiki!"));
				TextComponent message = new TextComponent("Visit the wiki!");
				message.setBold(true);
				message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
						"https://github.com/Theguyhere0/compressed-cobblestone/wiki"));
				player.spigot().sendMessage(message);
				return true;
			}
			
//			Player leaves a game
			if (args[0].equalsIgnoreCase("leave")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));
				return true;
			}
			
//			No valid command sent
			player.sendMessage(Utils.format("&cInvalid command. Use /vd help for more info."));
			return true;
		}
		return false;
	}
}
