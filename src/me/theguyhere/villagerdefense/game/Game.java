package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.*;

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

	public Map<String, String> playing = new HashMap<>();
	public Map<String, Integer> actives = new HashMap<>();
	public Map<String, Integer> gems = new HashMap<>();
	public Map<String, Integer> villagers = new HashMap<>();
	public Map<String, Integer> enemies = new HashMap<>();
	public Map<String, Integer> kills = new HashMap<>();
	public Collection<String> breaks = new ArrayList<>();
	public Map<String, Inventory> shops = new HashMap<>();
	private int taskID;
	
//	Handles players attempting to join a game
	public void join(Player player, String arena, Location location) {
		int players = plugin.getData().getInt("a" + arena + ".players.playing");
//		Starts the game
		if (players == 0) {
			int ID = new Tasks(plugin, this, arena, gi, inv, portal).runTask(plugin).getTaskId();
			actives.put(arena, ID);
			villagers.put(arena, 0);
			enemies.put(arena, 0);
			Collection<Entity> ents = location.getWorld().getNearbyEntities(location, 100, 100, 50);
//			Clear the arena
			ents.forEach(ent -> {
				if (ent instanceof LivingEntity && !(ent instanceof Player)) {
					if (ent.getName().contains("VD")) {
						((LivingEntity) ent).setHealth(0);
					}
				}
				else if (ent instanceof Item) {
					ent.remove();
				}
			});
		}

//		Prepares player to enter the arena if it doesn't exceed max capacity
		if (players < plugin.getData().getInt("a" + arena + ".max") && !plugin.getData().getBoolean("a" + arena + ".active")) {
			player.getActivePotionEffects().clear();
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.setFoodLevel(20);
			player.setSaturation(20);
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(location);
			playing.put(player.getName(), arena);
			plugin.getData().set("a" + arena + ".players.playing",
					plugin.getData().getInt("a" + arena + ".players.playing") + 1);
			plugin.saveData();
			gems.put(player.getName(), 0);
			kills.put(player.getName(), 0);
			createBoard(player, arena);
			start(player, arena);
			playing.forEach((gamer, num) -> {
				if (num.equals(arena))
					Bukkit.getServer().getPlayer(gamer).sendMessage(Utils.format("&a" + player.getName() + " joined the arena."));
			});
			portal.refreshHolo(Integer.parseInt(arena));
		}
	}
	
//	Handles players leaving a game
	public void leave(Player player) {
//		Player is in a game
		if (playing.containsKey(player.getName())) {
			String arena = playing.get(player.getName());
//			Remove the player's gameboard
			GameBoard board = new GameBoard(player.getUniqueId());
			if (board.hasID())
				board.stop();
//			Remove player from lists
			playing.remove(player.getName());
			plugin.getData().set("a" + arena + ".players.playing",
					plugin.getData().getInt("a" + arena + ".players.playing") - 1);
			plugin.saveData();
			gems.remove(player.getName());
			kills.remove(player.getName());
//			Notify people in arena player left
			playing.forEach((gamer, num) -> {
				if (num.equals(arena)) {
					Bukkit.getServer().getPlayer(gamer).sendMessage(Utils.format("&c" + player.getName() + " left the arena."));
				}
			});
//			Sets them up for teleport
			player.getInventory().clear();
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			if (plugin.getData().contains("lobby")) {
				player.getActivePotionEffects().clear();
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				player.setFoodLevel(20);
				player.setSaturation(20);
				player.setLevel(0);
				Location location = new Location(Bukkit.getWorld(plugin.getData().getString("lobby.world")),
						plugin.getData().getDouble("lobby.x"), plugin.getData().getDouble("lobby.y"),
						plugin.getData().getDouble("lobby.z"));
				player.teleport(location);
			}
			else {
				player.setHealth(0);
			}
//			Checks if the game has ended
			if (!playing.containsValue(arena)) {
				endGame(arena);
			}
			player.setGameMode(GameMode.ADVENTURE);
			portal.refreshHolo(Integer.parseInt(arena));
		}
		else {
			player.sendMessage(Utils.format("&cYou are not in a game!"));
		}
	}
	
//	Ends the game
	public void endGame(String arena) {
		if (playing.containsValue(arena)) {
			playing.forEach((gamer, num) -> {
				if (num.equals(arena)) {
					Bukkit.getServer().getPlayer(gamer).sendMessage(Utils.format("&6You made it to round &b" + plugin.getData().getInt("a" + arena + ".currentWave")
							+ "&6! Ending in 10 seconds."));
				}
			});
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				plugin.getData().set("a" + arena + ".active", false);
				plugin.getData().set("a" + arena + ".currentWave", 0);
				plugin.saveData();
				List<String> gamers = new ArrayList<String>();
//				Remove from lists
				if (playing.containsValue(arena)) {
					playing.forEach((gamer, num) -> {
						if (num.equals(arena)) {
							gamers.add(gamer);
						}
					});
				}
				for (String gamer : gamers) {
					leave(Bukkit.getServer().getPlayer(gamer));
				}
//				Clear the arena
				Location location = new Location(Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
						plugin.getData().getDouble("a" + arena + ".spawn.x"), plugin.getData().getDouble("a" + arena + ".spawn.y"),
						plugin.getData().getDouble("a" + arena + ".spawn.z"));
				Collection<Entity> ents = Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")).getNearbyEntities(location, 100, 100, 50);
				ents.forEach(ent -> {
					if (ent instanceof LivingEntity && !(ent instanceof Player)) {
						if (ent.getName().contains("VD")) {
							((LivingEntity) ent).setHealth(0);
						}
					}
					else if (ent instanceof Item) {
						ent.remove();
					}
				});
//				Remove from active
				actives.remove(arena);
				shops.remove(arena);

				//Refresh portal
				portal.refreshHolo(Integer.parseInt(arena));
			}
			
		}.runTaskLater(plugin, 200);
	}
	
//	Creates a task to update the scoreboard
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

//	Creates a scoreboard for the player
	public void createBoard(Player player, String arena) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Integer[] players = {0};
		Integer[] ghosts = {0};
		playing.forEach((gamer, num) -> {
			if (num.equals(arena)) {
				players[0]++;
				if (Bukkit.getServer().getPlayer(gamer).getGameMode().equals(GameMode.SPECTATOR))
					ghosts[0]++;
			}
		});
		Objective obj = board.registerNewObjective("VillagerDefense", "dummy", Utils.format("&2&l" + plugin.getData().getString("a" + arena + ".name")));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = obj.getScore(Utils.format("&6Wave: " + plugin.getData().getInt("a" + arena + ".currentWave")));
		score.setScore(6);
		Score score2 = obj.getScore(Utils.format("&2Gems: " + gems.get(player.getName())));
		score2.setScore(5);
		Score score3 = obj.getScore(Utils.format("&dPlayers: " + players[0]));
		score3.setScore(4);
		Score score4 = obj.getScore(Utils.format("&8Ghosts: " + ghosts[0]));
		score4.setScore(3);
		Score score5 = obj.getScore(Utils.format("&aVillagers: " + villagers.get(arena)));
		score5.setScore(2);
		Score score6 = obj.getScore(Utils.format("&cEnemies: " + enemies.get(arena)));
		score6.setScore(1);
		Score score7 = obj.getScore(Utils.format("&4Kills: " + kills.get(player.getName())));
		score7.setScore(0);

		player.setScoreboard(board);
	}
}
