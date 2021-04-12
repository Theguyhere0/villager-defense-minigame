package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.GameEndEvent;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.customEvents.WaveStartEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class Tasks {
	private final Main plugin;
	private final Game game;
	private final int arena;
	private final Portal portal;
	// Maps runnables to ID of the currently running runnable
	private final Map<Runnable, Integer> tasks = new HashMap<>();

	public Tasks(Main plugin, Game game, int arena, Portal portal) {
		this.plugin = plugin;
		this.game = game;
		this.arena = arena;
		this.portal = portal;
	}

	public Map<Runnable, Integer> getTasks() {
		return tasks;
	}

	// Waiting for enough players message
	public final Runnable waiting = new Runnable() {
		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player -> 
				player.getPlayer().sendMessage(Utils.notify("&6Waiting for more players to start the game.")));
		}
	};

	// 2 minute warning
	public final Runnable min2 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b2 &6minutes until the game starts!")));
		}

	};

	// 1 minute warning
	public final Runnable min1 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b1 &6minutes until the game starts!")));
		}
		
	};

	// 30 second warning
	public final Runnable sec30 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b30 &6seconds until the game starts!")));
		}
	};

	// 10 second warning
	public final Runnable sec10 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b10 &6seconds until the game starts!")));
		}
		
	};

	// 10 second warning when full
	public final Runnable full10 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player -> {
					player.getPlayer().sendMessage(Utils.notify("&6Arena has reached max player capacity."));
					player.getPlayer().sendMessage(Utils.notify("&b10 &6seconds until the game starts!"));
			});
		}

	};

	// 5 second warning
	public final Runnable sec5 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b5 &6seconds until the game starts!")));
		}
		
	};

	// Start a new wave
	public final Runnable wave = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Increment wave
			arenaInstance.incrementCurrentWave();
			int currentWave = arenaInstance.getCurrentWave();

			// Refresh the portal hologram
			portal.refreshHolo(arena, game);

			// Regenerate shops when time and notify players of it
			if (currentWave % 10 == 0 || currentWave == 1) {
				int shopNum = currentWave / 10 + 1;
				arenaInstance.setShop(Inventories.createShop(shopNum));
				if (currentWave != 1)
					arenaInstance.getActives().forEach(player ->
						player.getPlayer().sendMessage(Utils.notify("&6Shops have reset!")));
			}

			// Revive dead players
			arenaInstance.getGhosts().forEach(p -> {
				Utils.teleAdventure(p.getPlayer(), arenaInstance.getPlayerSpawn());
				p.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
				p.getPlayer().getInventory().addItem(GameItems.shop());
			});

			arenaInstance.getActives().forEach(p -> {
				// Notify of upcoming wave
				int reward = currentWave * 10 - 10;
				p.getPlayer().sendTitle(Utils.format("&6Wave " + currentWave),
						Utils.format("&7Starting in 15 seconds"), Utils.secondsToTicks(.5) ,
						Utils.secondsToTicks(3.5), Utils.secondsToTicks(1));

				// Give players gem rewards
				p.addGems(reward);
				if (currentWave > 1)
					p.getPlayer().sendMessage(Utils.notify("You have received &a" + reward + " &fgems!"));
			});

			// Notify spectators of upcoming wave
			arenaInstance.getSpectators().forEach(p ->
				p.getPlayer().sendTitle(Utils.format("&6Wave " + currentWave),
						Utils.format("&7Starting in 15 seconds"), Utils.secondsToTicks(.5) ,
						Utils.secondsToTicks(3.5), Utils.secondsToTicks(1)));

			// Spawns mobs after 15 seconds
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveStartEvent(arenaInstance)),
					Utils.secondsToTicks(15));
		}
		
	};
	
	// Start actual game
	public final Runnable start = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Teleport players to arena if waiting room exists
			if (arenaInstance.getWaitingRoom() != null) {
				arenaInstance.getActives().forEach(player ->
						Utils.teleAdventure(player.getPlayer(), arenaInstance.getPlayerSpawn()));
				arenaInstance.getSpectators().forEach(player ->
						Utils.teleSpectator(player.getPlayer(), arenaInstance.getPlayerSpawn()));
			}

			// Give all players a wooden sword and a shop while removing pre-game protection
			arenaInstance.getActives().forEach(player -> {
				player.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
				player.getPlayer().getInventory().addItem(GameItems.shop());
				player.getPlayer().getActivePotionEffects()
						.forEach(effect -> player.getPlayer().removePotionEffect(effect.getType()));
				player.getPlayer().setFireTicks(0);
				player.getPlayer().setInvulnerable(false);
			});
			
			// Set arena to active and reset villager and enemy count
			arenaInstance.setActive(true);
			arenaInstance.resetVillagers();
			arenaInstance.resetEnemies();

			// Trigger WaveEndEvent
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveEndEvent(arenaInstance)));
		}
	};

	// Reset the arena
	public final Runnable reset = new Runnable() {
		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Update data
			arenaInstance.setActive(false);
			arenaInstance.flipEnding();
			arenaInstance.resetCurrentWave();
			arenaInstance.resetEnemies();
			arenaInstance.resetVillagers();
			arenaInstance.getTask().getTasks().clear();

			// Remove players from the arena
			arenaInstance.getPlayers().forEach(player ->
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player.getPlayer()))));

			// Clear the arena
			Utils.clear(arenaInstance.getPlayerSpawn());

			// Refresh portal
			portal.refreshHolo(arena, game);
		}
	};

	// Update active player scoreboards
	public final Runnable updateBoards = new Runnable() {
		@Override
		public void run() {
			game.arenas.get(arena).getActives().forEach(game::createBoard);
		}
	};

	// Update time limit bar
	public final Runnable updateBar = new Runnable() {
		double progress = 1;
		double time;

		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Add time limit bar if it doesn't exist
			if (arenaInstance.getTimeLimitBar() == null) {
				progress = 1;
				arenaInstance.startTimeLimitBar();
				arenaInstance.getPlayers().forEach(vdPlayer ->
						arenaInstance.addPlayerToTimeLimitBar(vdPlayer.getPlayer()));
				time = 1d / Utils.minutesToSeconds(arenaInstance.getWaveTimeLimit());
			}

			else {
				// Trigger wave end event
				if (progress <= 0) {
					progress = 0;
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new GameEndEvent(arenaInstance)));
				}

				// Decrement time limit bar
				else {
					if (progress <= time * Utils.minutesToSeconds(1))
						arenaInstance.updateTimeLimitBar(BarColor.RED, progress);
					else arenaInstance.updateTimeLimitBar(progress);
					progress -= time;
				}
			}

		}
	};
}
