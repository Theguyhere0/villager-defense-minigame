package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.GameEndEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class GameEvents implements Listener {
	private final Main plugin;
	private final Game game;

	// Constants for armor types
	private final Material[] HELMETS = {Material.LEATHER_HELMET, Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET,
			Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET, Material.TURTLE_HELMET
	};
	private final Material[] CHESTPLATES = {Material.LEATHER_CHESTPLATE, Material.GOLDEN_CHESTPLATE,
			Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
			Material.NETHERITE_HELMET
	};
	private final Material[] LEGGINGS = {Material.LEATHER_LEGGINGS, Material.GOLDEN_LEGGINGS,
			Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS
	};
	private final Material[] BOOTS = {Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS,
			Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
	};

	public GameEvents (Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}
	
	// Keep score and drop gems
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		Arena arena = game.arenas.get(ent.getMetadata("VD").get(0).asInt());

		// Arena enemies not part of an active arena
		if (!arena.isActive()) {
			e.getDrops().clear();
			return;
		}

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
			e.getDrops().add(Utils.createItem(Material.EMERALD, null, Integer.toString(arena.getArena())));

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

	// Stop automatic game mode switching between worlds
	@EventHandler
	public void onGameModeSwitch(PlayerGameModeChangeEvent e) {
		if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(e.getPlayer())) &&
				e.getNewGameMode() == GameMode.SURVIVAL) e.setCancelled(true);
	}

	// Handle creeper explosions
	@EventHandler
	public void onExplode(ExplosionPrimeEvent e) {

		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		Arena arena = game.arenas.get(ent.getMetadata("VD").get(0).asInt());

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

	// Update health bar when damage is dealt by entity
	@EventHandler
	public void onHurt(EntityDamageByEntityEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		Entity damager = e.getDamager();

		// Ignore phantom damage to villager
		if (ent instanceof Villager && damager instanceof Player)
			return;

		// Ignore phantom damage to monsters
		if (ent instanceof Monster && damager instanceof Monster)
			return;

		// Check for projectile damage
		if (damager instanceof Projectile) {
			if (ent instanceof Villager && ((Projectile) damager).getShooter() instanceof Player)
				return;
			if (ent instanceof Monster && ((Projectile) damager).getShooter() instanceof Monster)
				return;
		}

		LivingEntity n = (LivingEntity) ent;

		// Update health bar
		ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
				n.getHealth() - e.getFinalDamage(), 5));
	}

	// Update health bar when damage is dealt not by another entity
	@EventHandler
	public void onHurt(EntityDamageEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		// Don't handle entity on entity damage
		if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
				e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
			return;

		LivingEntity n = (LivingEntity) ent;

		// Update health bar
		ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
				n.getHealth() - e.getFinalDamage(), 5));
	}

	// Update health bar when healed
	@EventHandler
	public void onHeal(EntityRegainHealthEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		LivingEntity n = (LivingEntity) ent;

		// Update health bar
		ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
				n.getHealth(), 5));
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

		// Open shop inventory and cancel interaction
		e.setCancelled(true);
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
			if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER)
				return;
			e.setCancelled(true);

			ItemStack buy = e.getClickedInventory().getItem(e.getSlot()).clone();
			ItemMeta meta = buy.getItemMeta();
			int cost = Integer.parseInt(meta.getLore().get(0).substring(10));

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				player.sendMessage(Utils.notify("&cYou can't afford this!"));
				return;
			}

			meta.setLore(new ArrayList<>());
			buy.setItemMeta(meta);

			// Subtract from balance, update scoreboard, give item
			gamer.addGems(-cost);
			game.createBoard(gamer);

			EntityEquipment equipment = player.getPlayer().getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Arrays.stream(HELMETS).anyMatch(mat -> mat == buy.getType()) && equipment.getHelmet() == null) {
				equipment.setHelmet(buy);
				player.sendMessage(Utils.notify("&aHelmet equipped!"));
			} else if (Arrays.stream(CHESTPLATES).anyMatch(mat -> mat == buy.getType()) &&
					equipment.getChestplate() == null) {
				equipment.setChestplate(buy);
				player.sendMessage(Utils.notify("&aChestplate equipped!"));
			} else if (Arrays.stream(LEGGINGS).anyMatch(mat -> mat == buy.getType()) &&
					equipment.getLeggings() == null) {
				equipment.setLeggings(buy);
				player.sendMessage(Utils.notify("&aLeggings equipped!"));
			} else if (Arrays.stream(BOOTS).anyMatch(mat -> mat == buy.getType()) && equipment.getBoots() == null) {
				equipment.setBoots(buy);
				player.sendMessage(Utils.notify("&aBoots equipped!"));
			} else {
				Utils.giveItem(player, buy);
				player.sendMessage(Utils.notify("&aItem purchased!"));
			}
		}
	}
	
	// Stops players from hurting villagers and other players
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent e) {
		Entity ent = e.getEntity();
		Entity damager = e.getDamager();

		// Cancel damage to each other if they are in a game
		if (ent instanceof Player && damager instanceof Player) {
			Player player = (Player) ent;
			if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player)))
				e.setCancelled(true);
		}

		// Check for special mobs
		if (!ent.hasMetadata("VD"))
			return;

		// Cancel damage to villager
		if (ent instanceof Villager && damager instanceof Player)
			e.setCancelled(true);

		// Cancel monster friendly fire damage
		else if (ent instanceof Monster && damager instanceof Monster)
			e.setCancelled(true);

		// Check for projectile damage
		else if (damager instanceof Projectile) {
			if (ent instanceof Player && ((Projectile) damager).getShooter() instanceof Player) {
				Player player = (Player) ent;
				if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player)))
					e.setCancelled(true);
			}
			if (ent instanceof Villager && ((Projectile) damager).getShooter() instanceof Player)
				e.setCancelled(true);
			else if (ent instanceof Monster && ((Projectile) damager).getShooter() instanceof Monster)
				e.setCancelled(true);
		}
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
					gamer.getPlayer().sendMessage(Utils.notify("&b" + player.getName() + "&c has died and will " +
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
		int earned = 0;
		for (int i = 0; i < stack; i++)
			earned += r.nextInt((int) (40 * Math.pow(wave, .15)));
		gamer.addGems(earned);

		// Cancel picking up of emeralds and notify player
		e.setCancelled(true);
		e.getItem().remove();
		player.sendMessage(Utils.notify("&fYou found &a" + (earned) + "&f gem(s)!"));

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
				gamer.getPlayer().sendMessage(Utils.notify("&b" + player.getName() + "&c has died and will " +
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
		// Check for living entity
		if (!(e.getEntity() instanceof LivingEntity)) return;

		// Check for fatal damage
		if (((LivingEntity) e.getEntity()).getHealth() > e.getFinalDamage()) return;

		// Check damage was done to monster
		if (!(e.getEntity().hasMetadata("VD"))) return;

		// Check that a player caused the damage
		if (!(e.getDamager() instanceof Player)) return;

		Player player = (Player) e.getDamager();

		// Check for player in an arena
		if (game.arenas.stream().filter(Objects::nonNull)
				.noneMatch(a -> a.getPlayers().stream().anyMatch(p -> p.getPlayer().equals(player)))) return;

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
		if (!ent.hasMetadata("VD"))
			return;
		e.setCancelled(true);
	}
}
