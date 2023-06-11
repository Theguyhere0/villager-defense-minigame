package me.theguyhere.villagerdefense.plugin.background;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.achievements.AchievementChecker;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.arenas.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.displays.NPCLeftClickEvent;
import me.theguyhere.villagerdefense.plugin.displays.NPCRightClickEvent;
import me.theguyhere.villagerdefense.plugin.displays.Portal;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.guis.SignGUIEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.List;
import java.util.Objects;

/**
 * The listener that handles background events.
 */
public class BackgroundListener implements PacketListener, Listener {
	private final NMSManager nmsManager = NMSVersion
		.getCurrent()
		.getNmsManager();

	@Override
	public void onAttack(Player player, int entityID) {
		GameController
			.getArenas()
			.values()
			.stream()
			.filter(Objects::nonNull)
			.map(Arena::getPortal)
			.filter(Objects::nonNull)
			.map(Portal::getNpc)
			.filter(Objects::nonNull)
			.forEach(npc -> {
				int npcId = npc.getEntityID();
				if (npcId == entityID)
					Bukkit
						.getScheduler()
						.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
							Bukkit
								.getPluginManager()
								.callEvent(new NPCLeftClickEvent(player, npcId)));
			});
	}

	@Override
	public void onInteractMain(Player player, int entityID) {
		GameController
			.getArenas()
			.values()
			.stream()
			.filter(Objects::nonNull)
			.map(Arena::getPortal)
			.filter(Objects::nonNull)
			.map(Portal::getNpc)
			.filter(Objects::nonNull)
			.forEach(npc -> {
				int npcId = npc.getEntityID();
				if (npcId == entityID)
					Bukkit
						.getScheduler()
						.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
							Bukkit
								.getPluginManager()
								.callEvent(new NPCRightClickEvent(player, npcId)));
			});
	}

	@Override
	public void onSignUpdate(Player player, String[] signLines) {
		Arena arena;
		String header = signLines[0];

		try {
			arena = GameController.getArena(Integer.parseInt(header.substring(18, header.length() - 4)));
		}
		catch (ArenaNotFoundException | NumberFormatException | IndexOutOfBoundsException e) {
			return;
		}

		// Check for right sign GUI
		if (!(signLines[1].contains(CommunicationManager.format("&1===============")) &&
			signLines[3].contains(CommunicationManager.format("&1==============="))))
			return;

		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
				Bukkit
					.getPluginManager()
					.callEvent(new SignGUIEvent(arena, player, signLines)));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		GameController.displayEverything(player);
		nmsManager.injectPacketListener(player, new BackgroundListener());
		FileConfiguration playerData = Main.getPlayerData();

		// Get list of loggers from data file
		List<String> loggers = playerData.getStringList("loggers");

		// Check if player is a logger
		if (loggers.contains(player
			.getUniqueId()
			.toString())) {
			CommunicationManager.debugInfo("%s joined after disconnecting mid-game.",
				CommunicationManager.DebugLevel.VERBOSE, player.getName()
			);

			// Teleport them back to lobby
			PlayerManager.teleAdventure(player, GameController.getLobby());
			loggers.remove(player
				.getUniqueId()
				.toString());
			playerData.set("loggers", loggers);

			// Return player health, food, exp, and items
			if (Main.plugin
				.getConfig()
				.getBoolean("keepInv"))
				PlayerManager.returnSurvivalStats(player);

			Main.savePlayerData();
		}

		// If the plugin setup is outdated, send message to admins
		if (Main.isOutdated() && player.hasPermission("vd.admin"))
			PlayerManager.notifyFailure(player, LanguageManager.errors.outdated,
				new ColoredMessage(ChatColor.AQUA, "/vd fix")
			);

		// Check for achievements
		AchievementChecker.checkDefaultHighScoreAchievements(player);
		AchievementChecker.checkDefaultKitAchievements(player);
	}

	@EventHandler
	public void onPortal(PlayerChangedWorldEvent e) {
		GameController.displayAllPortals(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		// Uninject player and make them leave from arena
		nmsManager.uninjectPacketListener(player);
		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.plugin, () ->
				Bukkit
					.getPluginManager()
					.callEvent(new LeaveArenaEvent(player)));

		// Get list of loggers from data file and add player to it
		List<String> loggers = Main
			.getPlayerData()
			.getStringList("loggers");
		loggers.add(player
			.getUniqueId()
			.toString());

		// Add to list of loggers if in a game
		if (GameController.checkPlayer(player)) {
			CommunicationManager.debugInfo("%s disconnected mid-game.", CommunicationManager.DebugLevel.VERBOSE,
				player.getName()
			);
			Main
				.getPlayerData()
				.set("loggers", loggers);
			Main.savePlayerData();
		}
	}

	@EventHandler
	public void onWorldLoadEvent(WorldLoadEvent e) {
		String worldName = e
			.getWorld()
			.getName();

		// Handle world loading after initialization
		if (Main
			.getUnloadedWorlds()
			.contains(worldName)) {
			Main.loadWorld(worldName);
			Main.resetGameManager();
			GameController.reloadLobby();
			GameController.refreshAll();
		}
	}

	@EventHandler
	public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e) {
		GameController.displayEverything(e.getPlayer());
	}
}
