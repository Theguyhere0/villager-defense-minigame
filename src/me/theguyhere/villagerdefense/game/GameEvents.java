package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.GameEndEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class GameEvents implements Listener {
	private final Main plugin;
	private final Game game;

	public GameEvents (Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}
	
	// Keep score and drop gems
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.getName().contains("VD"))
			return;

		Arena arena = game.arenas.get(Integer.parseInt(ent.getName().substring(4, 5)));

		// Arena enemies not part of an active arena
		if (!arena.isActive())
			return;

		// Update villager count
		if (ent instanceof Villager) {
			arena.decrementVillagers();
			if (arena.getVillagers() == 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
			}
		}

		// Manage drops and update enemy count, update player kill count
		else {
			// Clear normal drops
			e.getDrops().clear();

			// Set drop to emerald
			ItemStack gem = new ItemStack(Material.EMERALD);
			ItemMeta meta = gem.getItemMeta();
			meta.setDisplayName(Integer.toString(game.arenas.indexOf(arena)));
			gem.setItemMeta(meta);
			e.getDrops().add(gem);

			// Decrement enemy count
			arena.decrementEnemies();

			// Check for wave end condition
			if (arena.getEnemies() == 0 && !arena.isEnding()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new WaveEndEvent(arena)));
			}
		}

		// Update scoreboards
		arena.getTask().updateBoards.run();
	}

	// Handle creeper explosions
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.getName().contains("VD"))
			return;

		Arena arena = game.arenas.get(Integer.parseInt(ent.getName().substring(4, 5)));

		// Arena enemies not part of an active arena
		if (!arena.isActive())
			return;

		// Decrement enemy count
		arena.decrementEnemies();

		// Check for wave end condition
		if (arena.getEnemies() == 0 && !arena.isEnding()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveEndEvent(arena)));
		}

		// Update scoreboards
		arena.getTask().updateBoards.run();
	}
	
	// Open shop
	@EventHandler
	public void onShop(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		// See if the player is in a game
		if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		// Check for the shop item
		if (!player.getEquipment().getItemInMainHand().equals(GameItems.shop()))
			return;

		Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Open shop inventory
		player.openInventory(arena.getShop());
	}
	
	// Purchase items
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().contains(Utils.format("&2&lItem Shop"))) {
			Player player = (Player) e.getWhoClicked();
			VDPlayer gamer = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0).getPlayer(player);

			// See if the player is in a game
			if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
				return;

			// Ignore clicks in player's own inventory
			if (e.getClickedInventory().getType() == InventoryType.PLAYER)
				return;
			e.setCancelled(true);

			ItemStack buy = e.getClickedInventory().getItem(e.getSlot()).clone();
			ItemMeta meta = buy.getItemMeta();
			int cost = Integer.parseInt(meta.getLore().get(0).substring(meta.getLore().get(0).length() - 4).trim());

			// Check if they can afford the item
			if (!gamer.canAfford(cost))
				return;

			meta.setLore(new ArrayList<>());
			buy.setItemMeta(meta);

			// Subtract from balance, update scoreboard, give item
			gamer.addGems(-cost);
			game.createBoard(gamer);
			player.getInventory().addItem(buy);
		}

	}
	
	// Stops players from hurting villagers and other players
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent e) {
		Entity ent = e.getEntity();

		// Cancel damage to each other if they are in a game
		if (ent instanceof Player && e.getDamager() instanceof Player) {
			Player player = (Player) ent;
			if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player)))
				e.setCancelled(true);
		}

		// Check for special mobs
		if (!ent.getName().contains("VD"))
			return;

		// Cancel damage to villager
		if (ent instanceof Villager && e.getDamager() instanceof Player)
				e.setCancelled(true);
	}

	// Handles players falling into the void
	@EventHandler
	public void onVoidDamage(EntityDamageEvent e) {
		// Check for player taking damage
		if (!(e.getEntity() instanceof Player)) return;

		Player player = (Player) e.getEntity();

		// Check for fall damage
		if (!e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;

		// Check if player is in a game
		if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player))) return;

		Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Check if game has started yet
		if (!arena.isActive()) {
			// Cancel void damage
			e.setCancelled(true);

			// Teleport player back to player spawn
			player.teleport(arena.getPlayerSpawn());
		} else {
			// Set them to spectator mode instead of dying
			e.setCancelled(true);
			player.setGameMode(GameMode.SPECTATOR);
			player.getInventory().clear();

			// Teleport player back to player spawn
			player.teleport(arena.getPlayerSpawn());

			// Notify everyone of player death
			arena.getPlayers().forEach(gamer ->
					gamer.getPlayer().sendMessage(Utils.format("&c" + player.getName() + " has died and will " +
							"respawn next round.")));

			// Update scoreboards
			arena.getTask().updateBoards.run();

			// Check for game end condition
			if (arena.getAlive() == 0)
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
		}
	}
	
	// Give gems
	@EventHandler
	public void onGemPickup(EntityPickupItemEvent e) {
		// Check for player picking up item
		if (!(e.getEntity() instanceof Player))
			return;

		Player player = (Player) e.getEntity();

		// See if the player is in a game
		if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);
		VDPlayer gamer = arena.getPlayer(player);

		// Check for gem item
		if (!e.getItem().getItemStack().getType().equals(Material.EMERALD))
			return;

		// Calculate and give player gems
		int stack = e.getItem().getItemStack().getAmount();
		Random r = new Random();
		int wave = arena.getCurrentWave();
		int num = r.nextInt(Math.toIntExact(Math.round(40 * Math.pow(wave, 1 / (2 + Math.pow(Math.E, -wave + 3))))));
		gamer.addGems(num * stack);

		// Cancel picking up of emeralds and notify player
		e.setCancelled(true);
		e.getItem().remove();
		player.sendMessage(Utils.format("&fYou found &a" + (num * stack) + "&f gem(s)!"));

		// Update scoreboard
		game.createBoard(gamer);
	}
	
	// Handle player death
	@EventHandler
	public void onPlayerDeath(EntityDamageEvent e) {
		// Check for player
		if (!(e.getEntity() instanceof Player)) return;

		Player player = (Player) e.getEntity();

		// See if the player is in a game
		if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player))) return;

		Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Check if arena is active
		if (!arena.isActive()) return;

		// Check if player is about to die
		if (e.getFinalDamage() < player.getHealth()) return;

		// Set them to spectator mode instead of dying
		e.setCancelled(true);
		player.setGameMode(GameMode.SPECTATOR);
		player.getInventory().clear();

		// Notify everyone of player death
		arena.getPlayers().forEach(gamer ->
				gamer.getPlayer().sendMessage(Utils.format("&c" + player.getName() + " has died and will " +
						"respawn next round.")));

		// Update scoreboards
		arena.getTask().updateBoards.run();

		// Check for game end condition
		if (arena.getAlive() == 0)
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
	}

	// Update player kill counter
	@EventHandler
	public void onMobKillByPlayer(EntityDamageByEntityEvent e) {
		// Check for fatal damage
		if (!e.getEntity().isDead())
			return;

		// Check damage was done to monster
		if (!(e.getEntity() instanceof Monster))
			return;

		// Check that a player caused the damage
		if (!(e.getDamager() instanceof Player))
			return;

		Player player = (Player) e.getDamager();

		// Check for player in an arena
		if (game.arenas.stream().filter(Objects::nonNull)
				.noneMatch(a -> a.getPlayers().stream().anyMatch(p -> p.getPlayer().equals(player))))
			return;

		VDPlayer gamer = game.arenas.stream().filter(Objects::nonNull)
				.filter(a -> a.getPlayers().stream().anyMatch(p -> p.getPlayer().equals(player)))
				.collect(Collectors.toList()).get(0).getPlayer(player);

		// Increment kill count
		gamer.incrementKills();
	}
	
	// Stops slimes and magma cubes from splitting on death
	@EventHandler
	public void onSplit(SlimeSplitEvent e) {
		Entity ent = e.getEntity();
		if (!ent.getName().contains("VD"))
			return;
		e.setCancelled(true);
	}
}
