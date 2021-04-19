package me.theguyhere.villagerdefense.game.listeners;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.GameEndEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.GameItems;
import me.theguyhere.villagerdefense.game.models.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

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

		// Check for arena mobs
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
			if (arena.getVillagers() == 0 && !arena.isSpawning()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
				if (arena.isLoseSound())
					arena.getPlayers().forEach(vdPlayer -> vdPlayer.getPlayer().playSound(arena.getPlayerSpawn(),
							Sound.ENTITY_ENDER_DRAGON_DEATH, 10, 0));
			}
		}

		// Manage drops and update enemy count, update player kill count
		else {
			// Clear normal drops
			e.getDrops().clear();
			e.setDroppedExp(0);

			if (ent instanceof Monster || ent instanceof Slime || ent instanceof Hoglin) {
				// Set drop to emerald
				e.getDrops().add(Utils.createItem(Material.EMERALD, null, Integer.toString(arena.getArena())));
				e.setDroppedExp((int) arena.getCurrentDifficulty());

				// Decrement enemy count
				arena.decrementEnemies();
			}

			// Check for wave end condition
			if (arena.getEnemies() == 0 && !arena.isEnding() && !arena.isSpawning()) {
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

	// Save gems from explosions
	@EventHandler
	public void onGemExplode(EntityDamageEvent e) {
		Entity ent = e.getEntity();

		// Check for item
		if (!(ent instanceof Item))
			return;

		ItemStack item = ((Item) ent).getItemStack();

		// Check for right item
		if (item.getType() == Material.EMERALD && item.hasItemMeta() && item.getItemMeta().hasLore())
			e.setCancelled(true);
	}

	// Update health bar when damage is dealt
	@EventHandler
	public void onHurt(EntityDamageEvent e) {
		// Ignore cancelled events
		if (e.isCancelled())
			return;

		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		// Ignore wolves and players
		if (ent instanceof Wolf || ent instanceof Player)
			return;

		LivingEntity n = (LivingEntity) ent;

		// Update health bar
		if (ent instanceof IronGolem)
			ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
					n.getHealth() - e.getFinalDamage(), 10));
		else ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
				n.getHealth() - e.getFinalDamage(), 5));
	}

	// Prevent players from going hungry while waiting for an arena to start
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		Player player = (Player) e.getEntity();

		// See if the player is in a game
		if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		// See if game is already in progress
		if (game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0).getCurrentWave() != 0)
			return;

		e.setCancelled(true);
	}

	// Update health bar when healed
	@EventHandler
	public void onHeal(EntityRegainHealthEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		// Ignore wolves and players
		if (ent instanceof Wolf || ent instanceof Player)
			return;

		LivingEntity n = (LivingEntity) ent;

		// Update health bar
		if (ent instanceof IronGolem)
			ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
					n.getHealth(), 10));
		else ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
				n.getHealth(), 5));
	}

	// Open shop
	@EventHandler
	public void onShop(PlayerInteractEvent e) {
		// Check for right click
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player player = e.getPlayer();

		// See if the player is in a game
		if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Check for the shop item
		if (!GameItems.shop().equals(player.getEquipment().getItemInMainHand()))
			return;

		// Ignore if already in shop
		if (player.getOpenInventory().getTitle().contains(Utils.format("&k")))
			return;

		// Open shop inventory and cancel interaction
		e.setCancelled(true);
		player.openInventory(Inventories.createShop(arena.getCurrentWave() / 10 + 1));
	}

	// Stops players from hurting villagers and other players, and monsters from hurting each other
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
		if (!ent.hasMetadata("VD") && !(ent instanceof Player))
			return;

		// Cancel damage to villager
		if ((ent instanceof Villager || ent instanceof Wolf || ent instanceof IronGolem) && damager instanceof Player)
			e.setCancelled(true);

		// Cancel monster friendly fire damage
		else if ((ent instanceof Monster || ent instanceof Slime || ent instanceof Hoglin) &&
				(damager instanceof Monster || damager instanceof Slime || ent instanceof Hoglin))
			e.setCancelled(true);

		// Check for projectile damage
		else if (damager instanceof Projectile) {
			if (ent instanceof Player && ((Projectile) damager).getShooter() instanceof Player) {
				Player player = (Player) ent;
				if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player)))
					e.setCancelled(true);
			}
			if ((ent instanceof Villager || ent instanceof Wolf || ent instanceof IronGolem) &&
					((Projectile) damager).getShooter() instanceof Player)
				e.setCancelled(true);
			else if ((ent instanceof Monster || ent instanceof Slime || ent instanceof Hoglin) &&
					((Projectile) damager).getShooter() instanceof Monster)
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
			if (arena.getAlive() == 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
				if (arena.isLoseSound())
					arena.getPlayers().forEach(vdPlayer -> vdPlayer.getPlayer().playSound(arena.getPlayerSpawn(),
							Sound.ENTITY_ENDER_DRAGON_DEATH, 10, 0));
			}
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
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, .5f, 0);

		FileConfiguration playerData = plugin.getPlayerData();

		// Update player stats
		playerData.set(player.getName() + ".totalGems",
				playerData.getInt(player.getName() + ".totalGems") + earned);
		if (playerData.getInt(player.getName() + ".topBalance") < gamer.getGems())
			playerData.set(player.getName() + ".topBalance", gamer.getGems());
		plugin.savePlayerData();

		// Update scoreboard
		game.createBoard(gamer);
	}
	
	// Handle player death
	@EventHandler
	public void onPlayerDeath(EntityDamageEvent e) {
		// Ignore if cancelled
		if (e.isCancelled())
			return;

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
		arena.getPlayers().forEach(gamer -> {
				gamer.getPlayer().sendMessage(Utils.notify("&b" + player.getName() + "&c has died and will " +
						"respawn next round."));
				if (arena.isPlayerDeathSound())
					gamer.getPlayer().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 4, 0);
		});

		// Update scoreboards
		arena.getTask().updateBoards.run();

		// Check for game end condition
		if (arena.getAlive() == 0) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
			if (arena.isLoseSound())
				arena.getPlayers().forEach(vdPlayer -> vdPlayer.getPlayer().playSound(arena.getPlayerSpawn(),
						Sound.ENTITY_ENDER_DRAGON_DEATH, 10, 0));
		}
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
		if (!(e.getDamager() instanceof Player || e.getDamager() instanceof Projectile)) return;

		Player player;

		// Check if projectile came from player, then set player
		if (e.getDamager() instanceof Projectile) {
			if (((Projectile) e.getDamager()).getShooter() instanceof Player)
				player = (Player) ((Projectile) e.getDamager()).getShooter();
			else return;
		} else player = (Player) e.getDamager();

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

	// Stop interactions with villagers in game
	@EventHandler
	public void onTrade(PlayerInteractEntityEvent e) {
		Entity ent = e.getRightClicked();

		// Check for villager
		if (!(ent instanceof Villager))
			return;

		// Check for arena mobs
		if (ent.hasMetadata("VD"))
			e.setCancelled(true);
	}

	// Manage spawning pets and care packages
	@EventHandler
	public void onConsume(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		// See if the player is in a game
		if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player))) return;

		Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);
		VDPlayer gamer = arena.getPlayer(player);

		// Wolf spawn
		if (item.getType() == Material.WOLF_SPAWN_EGG) {
			// Ignore if it wasn't a right click on a block
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;

			// Cancel normal spawn
			e.setCancelled(true);

			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItemInMainHand(null);

			Location location = e.getClickedBlock().getLocation();
			location.setY(location.getY() + 1);

			// Spawn and tame the wolf
			Wolf wolf = (Wolf) player.getWorld().spawnEntity(location, EntityType.WOLF);
			wolf.setAdult();
			wolf.setOwner(player);
			wolf.setBreed(false);
			wolf.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
			wolf.setCustomName(player.getName() + "'s Wolf");
			wolf.setCustomNameVisible(true);

			return;
		}

		// Ignore other vanilla items
		if (item.getItemMeta() == null)
			return;

		// Iron golem spawn
		if (item.getItemMeta().getDisplayName().contains("Iron Golem Spawn Egg")) {
			// Ignore if it wasn't a right click on a block
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;

			// Cancel normal spawn
			e.setCancelled(true);

			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItemInMainHand(null);

			Location location = e.getClickedBlock().getLocation();
			location.setY(location.getY() + 1);

			// Spawn iron golem
			IronGolem ironGolem = (IronGolem) player.getWorld()
					.spawnEntity(location, EntityType.IRON_GOLEM);
			ironGolem.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
			ironGolem.setCustomName(Utils.healthBar(1, 1, 10));
			ironGolem.setCustomNameVisible(true);
		}

		// Small care package
		if (item.getItemMeta().getDisplayName().contains("Small Care Package")) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItemInMainHand(null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(1))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(1))));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(1)));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(1)));
			}
			player.sendMessage(Utils.notify("&aCare package delivered!"));
		}

		// Medium care package
		if (item.getItemMeta().getDisplayName().contains("Medium Care Package")) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItemInMainHand(null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(2))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(2))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randConsumable(2))));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(2)));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(2)));
				if (gamer.getKit().equals("Witch"))
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randConsumable(2))));
				else Utils.giveItem(player, Utils.removeLastLore(GameItems.randConsumable(2)));
			}
			player.sendMessage(Utils.notify("&aCare package delivered!"));
		}

		// Large care package
		if (item.getItemMeta().getDisplayName().contains("Large Care Package")) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItemInMainHand(null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(4))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(3))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(3))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randConsumable(3))));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(4)));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(3)));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(3)));
				if (gamer.getKit().equals("Witch"))
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randConsumable(3))));
				else Utils.giveItem(player, Utils.removeLastLore(GameItems.randConsumable(3)));
			}
			player.sendMessage(Utils.notify("&aCare package delivered!"));
		}

		// Extra large care package
		if (item.getItemMeta().getDisplayName().contains("Extra large Care Package")) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItemInMainHand(null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(5))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(4))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(5))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(4))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randConsumable(4))));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randConsumable(4))));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(5)));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(4)));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(5)));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(4)));
				if (gamer.getKit().equals("Witch")) {
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randConsumable(4))));
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randConsumable(4))));
				} else {
					Utils.giveItem(player, Utils.removeLastLore(GameItems.randConsumable(4)));
					Utils.giveItem(player, Utils.removeLastLore(GameItems.randConsumable(4)));
				}
			}
			player.sendMessage(Utils.notify("&aCare package delivered!"));
		}
	}

	// Prevent wolves from targeting villagers
	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e) {
		// Check for wolf
		if (!(e.getEntity() instanceof Wolf))
			return;

		// Check for villager target
		if (!(e.getTarget() instanceof Villager))
			return;

		// Cancel if special wolf
		if (e.getEntity().hasMetadata("VD"))
			e.setCancelled(true);
	}

	// Prevent wolves from teleporting
	@EventHandler
	public void onTeleport(EntityTeleportEvent e) {
		Entity ent = e.getEntity();

		// Check for wolf
		if (!(ent instanceof Wolf))
			return;

		// Check for special mob
		if (!ent.hasMetadata("VD"))
			return;

		// Check if player is playing in an arena
		if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer((Player) ((Wolf) ent).getOwner())))
			return;

		e.setCancelled(true);
	}
}
