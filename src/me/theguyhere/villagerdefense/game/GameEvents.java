package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.GameEndEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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
import java.util.Random;
import java.util.stream.Collectors;

public class GameEvents implements Listener {
	private final Main plugin;
	private final Game game;

	public GameEvents (Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}
	
//	Keep score and drop gems
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.getName().contains("VD"))
			return;

		int arena = Integer.parseInt(ent.getName().substring(4, 5));

		// Arena enemies not part of active arena
		if (game.actives.stream().noneMatch(r -> r.getArena() == arena))
			return;

		Arena arenaInstance = game.actives.stream().filter(r -> r.getArena() == arena)
				.collect(Collectors.toList()).get(0);

		// Update villager count
		if (ent instanceof Villager) {
			arenaInstance.decrementVillagers();
			if (arenaInstance.getVillagers() == 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
			}
		}

		// Manage drops and update enemy count
		else {
			e.getDrops().clear();
			ItemStack gem = new ItemStack(Material.EMERALD);
			ItemMeta meta = gem.getItemMeta();
			meta.setDisplayName(Integer.toString(arena));
			gem.setItemMeta(meta);
			e.getDrops().add(gem);
			arenaInstance.decrementEnemies();
			if (arenaInstance.getEnemies() == 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new WaveEndEvent(arena)));
			}
		}

		// Update scoreboards
		arenaInstance.getTask().updateBoards.run();
	}
	
	// Open shop
	@EventHandler
	public void onShop(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		// See if the player is in a game
		if (game.playing.stream().noneMatch(p -> p.getPlayer().equals(player)))
			return;

		// Check for the shop item
		if (!player.getEquipment().getItemInMainHand().equals(GameItems.shop()))
			return;

		VDPlayer gamer = game.playing.stream().filter(p -> p.getPlayer().equals(player))
				.collect(Collectors.toList()).get(0);
		Arena arena = game.actives.stream().filter(r -> r.getArena() == gamer.getArena())
				.collect(Collectors.toList()).get(0);

		// Open shop inventory
		player.openInventory(arena.getShop());
	}
	
	// Purchase items
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().contains(Utils.format("&2&lItem Shop"))) {
			Player player = (Player) e.getWhoClicked();
			VDPlayer gamer = game.playing.stream().filter(p -> p.getPlayer().equals(player))
					.collect(Collectors.toList()).get(0);

			// See if the player is in a game
			if (game.playing.stream().noneMatch(p -> p.getPlayer().equals(player)))
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

		// Check for special mobs
		if (!ent.getName().contains("VD"))
			return;

		// Cancel damage to villager
		if (ent instanceof Villager && e.getDamager() instanceof Player)
				e.setCancelled(true);

		// Cancel damage to each other if they are in a game
		if (ent instanceof Player && e.getDamager() instanceof Player) {
			Player player = (Player) ent;
			if (game.playing.stream().anyMatch(p -> p.getPlayer().equals(player)))
				e.setCancelled(true);
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
		if (game.playing.stream().noneMatch(p -> p.getPlayer().equals(player)))
			return;

		VDPlayer gamer = game.playing.stream().filter(p -> p.getPlayer().equals(player))
				.collect(Collectors.toList()).get(0);

		// Check for gem item
		if (!e.getItem().getItemStack().getType().equals(Material.EMERALD))
			return;

		// Calculate and give player gems
		int stack = e.getItem().getItemStack().getAmount();
		Random r = new Random();
		int wave = plugin.getData().getInt("a" + gamer.getArena() + ".currentWave");
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
		if (!(e.getEntity() instanceof Player))
			return;

		Player player = (Player) e.getEntity();

		// See if the player is in a game
		if (game.playing.stream().noneMatch(p -> p.getPlayer().equals(player)))
			return;

		VDPlayer gamer = game.playing.stream().filter(p -> p.getPlayer().equals(player))
				.collect(Collectors.toList()).get(0);

		// Check if player is about to die
		if (e.getFinalDamage() < player.getHealth())
			return;

		// Set them to spectator mode instead of dying
		e.setCancelled(true);
		player.setGameMode(GameMode.SPECTATOR);
		player.getInventory().clear();

		Arena arena = game.actives.stream().filter(r -> r.getArena() == gamer.getArena())
				.collect(Collectors.toList()).get(0);

		// Update scoreboards
		arena.getTask().updateBoards.run();
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
