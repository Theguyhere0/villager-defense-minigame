package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Game {
	private final Main plugin;
	private final GameItems gi;
	private final Inventories inv;
	private final Portal portal;
	
	public Game(Main plugin, GameItems gi, Inventories inv, Portal portal) {
		this.plugin = plugin;
		this.gi = gi;
		this.inv = inv;
		this.portal = portal;
	}

	public Map<String, String> playing = new HashMap<>(); // Tracks players playing and the arena they're in
	public Map<String, String> spectating = new HashMap<>(); // Tracks players spectating and the arena they're in
	public Map<String, Integer> actives = new HashMap<>(); // Tracks active arenas and their associated task runnable
	public Map<String, Integer> gems = new HashMap<>(); // Maps players to their in-game gem count
	public Map<String, Integer> villagers = new HashMap<>(); // Maps arenas to their villager count
	public Map<String, Integer> enemies = new HashMap<>(); // Maps arenas to their enemy count
	public Map<String, Integer> kills = new HashMap<>(); // Maps players to their in-game kill count
	public Collection<String> breaks = new ArrayList<>(); // Tracks arenas that are in between rounds
	public Map<String, Inventory> shops = new HashMap<>(); // Maps arenas to their respective shop inventories
	private int taskID; // Initialized task ID for game board update

	// Handles players attempting to join a game
	public void join(Player player, String arena, Location location) {
		// Get number of players in the arena
		int players = plugin.getData().getInt("a" + arena + ".players.playing");

		// Starts up the arena upon first player joining
		if (players == 0) {
			// Start thread for arena
			int ID = new Tasks(plugin, this, arena, gi, inv, portal).runTask(plugin).getTaskId();

			// Initialize arena data
			actives.put(arena, ID);
			villagers.put(arena, 0);
			enemies.put(arena, 0);

			// Get all nearby entities in the arena
			if (location.getWorld() == null) {
				System.out.println("Error: Location's world is null for join method");
				player.sendMessage(Utils.format("&cSomething went wrong"));
				return;
			}
			Collection<Entity> ents = location.getWorld().getNearbyEntities(location, 100, 100, 50);

			// Clear the arena for living entities
			ents.forEach(ent -> {
				if (ent instanceof LivingEntity && !(ent instanceof Player)) {
					if (ent.getName().contains("VD")) {
						((LivingEntity) ent).setHealth(0);
					}
				}
			});

			// Clear the arena for items
			ents.forEach(ent -> {
				if (ent instanceof Item)
					ent.remove();
			});
		}

		// Prepares player to enter arena if it doesn't exceed max capacity or if the arena hasn't already started
		if (players < plugin.getData().getInt("a" + arena + ".max") &&
				!plugin.getData().getBoolean("a" + arena + ".active")) {
			// Teleport to arena
			Utils.prepTeleAdventure(player);
			player.teleport(location);

			// Update player tracking and in-game stats
			playing.put(player.getName(), arena);
			plugin.getData().set("a" + arena + ".players.playing",
					plugin.getData().getInt("a" + arena + ".players.playing") + 1);
			plugin.saveData();
			gems.put(player.getName(), 0);
			kills.put(player.getName(), 0);
			portal.refreshHolo(Integer.parseInt(arena));

			// Give them a game board
			createBoard(player, arena);
			start(player, arena);

			// Notify everyone in the arena
			playing.forEach((gamer, num) -> {
				if (num.equals(arena))
					Bukkit.getServer().getPlayer(gamer).sendMessage(
							Utils.format("&a" + player.getName() + " joined the arena."));
			});
		}

		// Join players as spectators if arena is full or game already started
		else {
			// Teleport to arena
			Utils.prepTeleSpectator(player);
			player.teleport(location);

			// Update player tracking and in-game stats
			spectating.put(player.getName(), arena);
			plugin.getData().set("a" + arena + ".players.spectating",
					plugin.getData().getInt("a" + arena + ".players.spectating") + 1);
			plugin.saveData();
			portal.refreshHolo(Integer.parseInt(arena));
		}
	}
	
	// Handles players leaving a game
	public void leave(Player player) {
		// Check if player is playing in the arena
		if (playing.containsKey(player.getName())) {
			String arena = playing.get(player.getName());

			// Remove the player's game board
			GameBoard board = new GameBoard(player.getUniqueId());
			if (board.hasID())
				board.stop();

			// Update player tracking and in-game data
			playing.remove(player.getName());
			plugin.getData().set("a" + arena + ".players.playing",
					plugin.getData().getInt("a" + arena + ".players.playing") - 1);
			plugin.saveData();
			gems.remove(player.getName());
			kills.remove(player.getName());

			// Notify people in arena player left
			playing.forEach((gamer, num) -> {
				if (num.equals(arena))
					Bukkit.getServer().getPlayer(gamer).sendMessage(Utils.format("&c" + player.getName() +
							" left the arena."));
			});

			// Sets them up for teleport to lobby
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			if (plugin.getData().contains("lobby")) {
				Utils.prepTeleAdventure(player);
				Location location = new Location(Bukkit.getWorld(plugin.getData().getString("lobby.world")),
						plugin.getData().getDouble("lobby.x"), plugin.getData().getDouble("lobby.y"),
						plugin.getData().getDouble("lobby.z"));
				player.teleport(location);
			}

			// Kill them to leave the game
			else {
				player.getInventory().clear();
				player.setHealth(0);
			}

			// Checks if the game has ended because no players are left
			if (!playing.containsValue(arena))
				endGame(arena);

			// Refresh the game portal
			portal.refreshHolo(Integer.parseInt(arena));
		}

		// Notify player they can't leave anything
		else player.sendMessage(Utils.format("&cYou are not in a game!"));
	}
	
	// Ends the game
	public void endGame(String arena) {
		// Notify players that the game has ended
		if (playing.containsValue(arena)) {
			playing.forEach((gamer, num) -> {
				if (num.equals(arena)) {
					Bukkit.getServer().getPlayer(gamer).sendMessage(
							Utils.format("&6You made it to round &b" +
									plugin.getData().getInt("a" + arena + ".currentWave") +
									"&6! Ending in 10 seconds."));
				}
			});
		}

		// Reset the arena
		new BukkitRunnable() {

			@Override
			public void run() {
				// Update data
				plugin.getData().set("a" + arena + ".active", false);
				plugin.getData().set("a" + arena + ".currentWave", 0);
				plugin.saveData();
				actives.remove(arena);
				shops.remove(arena);

				// Remove players from the arena
				if (playing.containsValue(arena))
					playing.forEach((gamer, num) -> {
						if (num.equals(arena))
							leave(Bukkit.getServer().getPlayer(gamer));
					});

				// Get all nearby entities in the arena
				Location location = new Location(
						Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
						plugin.getData().getDouble("a" + arena + ".spawn.x"),
						plugin.getData().getDouble("a" + arena + ".spawn.y"),
						plugin.getData().getDouble("a" + arena + ".spawn.z"));
				if (location.getWorld() == null) {
					System.out.println("Error: Location's world is null for endGame method");
					return;
				}
				Collection<Entity> ents = location.getWorld().getNearbyEntities(location, 100, 100, 50);

				// Clear the arena for living entities
				ents.forEach(ent -> {
					if (ent instanceof LivingEntity && !(ent instanceof Player)) {
						if (ent.getName().contains("VD")) {
							((LivingEntity) ent).setHealth(0);
						}
					}
				});

				// Clear the arena for items
				ents.forEach(ent -> {
					if (ent instanceof Item)
						ent.remove();
				});

				// Refresh portal
				portal.refreshHolo(Integer.parseInt(arena));
			}
			
		}.runTaskLater(plugin, 200);
	}
	
	// Creates a task to update the game board
	public void start(Player player, String arena) {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			final GameBoard board = new GameBoard(player.getUniqueId());
			
			@Override
			public void run() {
				if (!board.hasID())
					board.setID(taskID);
				createBoard(player, arena);
			}
			
		}, 0, 10);
	}

	// Creates a game board for the player
	public void createBoard(Player player, String arena) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		int[] players = {0};
		int[] ghosts = {0};
		playing.forEach((gamer, num) -> {
			if (num.equals(arena)) {
				players[0]++;
				if (Bukkit.getServer().getPlayer(gamer).getGameMode().equals(GameMode.SPECTATOR)) {
					ghosts[0]++;
					players[0]--;
				}
			}
		});
		Objective obj = board.registerNewObjective("VillagerDefense", "dummy",
				Utils.format("&2&l" + plugin.getData().getString("a" + arena + ".name")));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = obj.getScore(Utils.format("&6Wave: " + plugin.getData().getInt("a" + arena +
				".currentWave")));
		score.setScore(7);
		Score score2 = obj.getScore(Utils.format("&2Gems: " + gems.get(player.getName())));
		score2.setScore(6);
		Score score3 = obj.getScore(Utils.format("&dPlayers: " + players[0]));
		score3.setScore(5);
		Score score4 = obj.getScore(Utils.format("&8Ghosts: " + ghosts[0]));
		score4.setScore(4);
		Score score5 = obj.getScore(Utils.format("&7Spectators: " + plugin.getData().getInt("a" + arena +
				".players.spectating")));
		score5.setScore(3);
		Score score6 = obj.getScore(Utils.format("&aVillagers: " + villagers.get(arena)));
		score6.setScore(2);
		Score score7 = obj.getScore(Utils.format("&cEnemies: " + enemies.get(arena)));
		score7.setScore(1);
		Score score8 = obj.getScore(Utils.format("&4Kills: " + kills.get(player.getName())));
		score8.setScore(0);

		player.setScoreboard(board);
	}
}
