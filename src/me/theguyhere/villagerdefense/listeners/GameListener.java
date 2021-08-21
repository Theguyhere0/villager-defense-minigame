package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.GameEndEvent;
import me.theguyhere.villagerdefense.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.events.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.game.models.GameItems;
import me.theguyhere.villagerdefense.game.models.Mobs;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.game.models.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.DataManager;
import me.theguyhere.villagerdefense.tools.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class GameListener implements Listener {
	private final Main plugin;

	public GameListener(Main plugin) {
		this.plugin = plugin;
	}
	
	// Keep score and drop gems, exp, and rare loot
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		LivingEntity ent = e.getEntity();

		// Check for arena mobs
		if (!ent.hasMetadata("VD"))
			return;

		Arena arena = plugin.getGame().arenas.get(ent.getMetadata("VD").get(0).asInt());

		// Arena enemies not part of an active arena
		if (arena.getStatus() != ArenaStatus.ACTIVE) {
			e.getDrops().clear();
			return;
		}

		// Clear normal drops
		e.getDrops().clear();
		e.setDroppedExp(0);

		// Update villager count
		if (ent instanceof Villager)
			arena.decrementVillagers();

		// Update wolf count
		else if (ent instanceof Wolf) {
			try {
				arena.getPlayer((Player) ((Wolf) ent).getOwner()).decrementWolves();
			} catch (Exception err) {
				return;
			}
		}

		// Update iron golem count
		else if (ent instanceof IronGolem) {
			arena.decrementGolems();
		}

		// Manage drops and update enemy count, update player kill count
		else {
			Random r = new Random();

			// Set drop to emerald, exp, and rare loot
			if (ent instanceof Wither) {
				if (arena.hasGemDrop())
					e.getDrops().add(Utils.createItems(Material.EMERALD, 20, null,
							Integer.toString(arena.getArena())));
				if (arena.hasExpDrop())
					e.setDroppedExp((int) (arena.getCurrentDifficulty() * 40));
			} else {
				if (arena.hasGemDrop()) {
					e.getDrops().add(Utils.createItem(Material.EMERALD, null,
							Integer.toString(arena.getArena())));

					// Get rare loot probability
					double probability;
					switch (arena.getDifficultyMultiplier()) {
						case 1:
							probability = .015;
							break;
						case 2:
							probability = .01;
							break;
						case 3:
							probability = .008;
							break;
						default:
							probability = .006;
					}

					if (r.nextDouble() < probability)
						e.getDrops().add(GameItems.randCare(arena.getCurrentWave() / 10 + 1));
				}
				if (arena.hasExpDrop())
					e.setDroppedExp((int) (arena.getCurrentDifficulty() * 2));

				// Decrement enemy count
				arena.decrementEnemies();
			}

			DataManager data;

			// Get spawn table
			if (arena.getSpawnTableFile().equals("custom"))
				data = new DataManager(plugin, "spawnTables/a" + arena.getArena() + ".yml");
			else data = new DataManager(plugin, "spawnTables/" + arena.getSpawnTableFile() + ".yml");

			// Get wave
			String wave = Integer.toString(arena.getCurrentWave());
			if (!data.getConfig().contains(wave))
				if (data.getConfig().contains("freePlay"))
					wave = "freePlay";
				else wave = "1";

			// Calculate count multiplier
			double countMultiplier = Math.log((arena.getActiveCount() + 7) / 10d) + 1;
			if (!arena.hasDynamicCount())
				countMultiplier = 1;

			// Calculate monster count
			int count = (int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier);

			// Set monsters glowing when only 20% remain
			if (arena.getEnemies() <= .2 * count && !arena.isSpawningMonsters() && arena.getEnemies() > 0) {
				arena.getPlayerSpawn().getWorld().getNearbyEntities(arena.getPlayerSpawn(),
						200, 200, 200).stream().filter(entity -> entity.hasMetadata("VD"))
						.filter(entity -> entity instanceof Monster || entity instanceof Slime ||
								entity instanceof Hoglin || entity instanceof Phantom)
						.forEach(entity -> entity.setGlowing(true));
			}
		}

		// Update scoreboards
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)));
	}

	// Stop automatic game mode switching between worlds
	@EventHandler
	public void onGameModeSwitch(PlayerGameModeChangeEvent e) {
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(e.getPlayer())) &&
				e.getNewGameMode() == GameMode.SURVIVAL) e.setCancelled(true);
	}

	// Handle creeper explosions
	@EventHandler
	public void onExplode(ExplosionPrimeEvent e) {

		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		Arena arena = plugin.getGame().arenas.get(ent.getMetadata("VD").get(0).asInt());

		// Arena enemies not part of an active arena
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			return;

		// Decrement enemy count
		arena.decrementEnemies();

		// Update scoreboards
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)));
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

	// Update health bar when damage is dealt by entity
	@EventHandler
	public void onHurt(EntityDamageByEntityEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		Entity damager = e.getDamager();

		// Ignore wolves
		if (ent instanceof Wolf)
			return;

		// Ignore phantom damage to villager
		if ((ent instanceof Villager || ent instanceof IronGolem) && damager instanceof Player)
			return;

		// Ignore phantom damage to monsters
		if ((ent instanceof Monster || ent instanceof Slime || ent instanceof Hoglin || ent instanceof Phantom) &&
				(damager instanceof Monster || damager instanceof Hoglin))
			return;

		// Check for phantom projectile damage
		if (damager instanceof Projectile) {
			if ((ent instanceof Villager || ent instanceof IronGolem) &&
					((Projectile) damager).getShooter() instanceof Player)
				return;
			if ((ent instanceof Monster || ent instanceof Slime || ent instanceof Hoglin || ent instanceof Phantom) &&
					((Projectile) damager).getShooter() instanceof Monster)
				return;
		}

		// Ignore bosses
		if (ent instanceof Wither)
			return;

		LivingEntity n = (LivingEntity) ent;

		// Update health bar
		if (ent instanceof IronGolem || ent instanceof Ravager)
			ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
					n.getHealth() - e.getFinalDamage(), 10));
		else ent.setCustomName(Utils.healthBar(n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
				n.getHealth() - e.getFinalDamage(), 5));
	}

	// Update health bar when damage is dealt not by another entity
	@EventHandler
	public void onHurt(EntityDamageEvent e) {
		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		// Ignore wolves
		if (ent instanceof Wolf)
			return;

		// Don't handle entity on entity damage
		if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
				e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK||
				e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION||
				e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
			return;

		// Ignore bosses
		if (ent instanceof Wither)
			return;

		LivingEntity n = (LivingEntity) ent;

		// Update health bar
		if (ent instanceof IronGolem || ent instanceof Ravager)
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
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		// See if game is already in progress
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
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

		// Ignore bosses
		if (ent instanceof Wither)
			return;

		LivingEntity n = (LivingEntity) ent;
		double maxHealth = n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double modifiedHealth = n.getHealth() + e.getAmount();

		// Update health bar
		if (ent instanceof IronGolem || ent instanceof Ravager)
			ent.setCustomName(Utils.healthBar(maxHealth, Math.min(modifiedHealth, maxHealth), 10));
		else ent.setCustomName(Utils.healthBar(maxHealth, Math.min(modifiedHealth, maxHealth), 5));
	}

	// Open shop, kit selecting menu, or leave
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		// Check for right click
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player player = e.getPlayer();
		Arena arena;
		VDPlayer gamer;

		// See if the player is in a game
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Get item in hand
		ItemStack item;
		if (e.getHand() == EquipmentSlot.OFF_HAND) {
			item = player.getEquipment().getItemInOffHand();

			// Check for other clickables in main hand
			if (Arrays.asList(GameItems.ABILITY_ITEMS).contains(item) ||
					Arrays.asList(GameItems.FOOD_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.ARMOR_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.CARE_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.CLICKABLE_WEAPON_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.CLICKABLE_CONSUME_MATERIALS).contains(item.getType()))
				return;
		}
		else item = player.getEquipment().getItemInMainHand();

		Inventories inv = new Inventories(plugin);

		// Open shop inventory
		if (GameItems.shop().equals(item))
			player.openInventory(Inventories.createShop(arena.getCurrentWave() / 10 + 1, arena));

		// Open kit selection menu
		else if (GameItems.kitSelector().equals(item))
			player.openInventory(inv.createSelectKitsInventory(player, arena));

		// Open challenge selection menu
		else if (GameItems.challengeSelector().equals(item))
			player.openInventory(inv.createSelectChallengesInventory(gamer, arena));

		// Make player leave
		else if (GameItems.leave().equals(item))
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));

		// Ignore
		else return;

		// Cancel interaction
		e.setCancelled(true);
	}

	// Stops players from hurting villagers and other players, and monsters from hurting each other
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent e) {
		Entity ent = e.getEntity();
		Entity damager = e.getDamager();

		// Cancel damage to each other if they are in a game
		if (ent instanceof Player && damager instanceof Player) {
			Player player = (Player) ent;
			if (plugin.getGame().arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player)))
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
				if (plugin.getGame().arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player)))
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
		Arena arena;
		VDPlayer gamer;

		// Check for void damage
		if (!e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}
		FileConfiguration language = plugin.getLanguageData();

		// Check if game has started yet
		if (arena.getStatus() == ArenaStatus.WAITING) {
			// Cancel void damage
			e.setCancelled(true);

			// Teleport player back to player spawn or waiting room
			if (arena.getWaitingRoom() == null)
				player.teleport(arena.getPlayerSpawn());
			else player.teleport(arena.getWaitingRoom());
		} else {
			// Set player to fake death mode
			player.setGameMode(GameMode.SPECTATOR);
			player.getInventory().clear();
			player.closeInventory();
			gamer.setStatus(PlayerStatus.GHOST);
			player.setFallDistance(0);

			// Notify player of their own death
			player.sendTitle(Utils.format(language.getString("death1")), language.getString("death2"),
					Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

			// Teleport player back to player spawn
			player.teleport(arena.getPlayerSpawn());
			player.closeInventory();

			// Notify everyone else of player death
			arena.getPlayers().forEach(fighter -> {
				if (!fighter.getPlayer().getUniqueId().equals(player.getUniqueId()))
					if (language.getString("death") == null) {
						plugin.debugError("The language file is missing the attribute 'death'!", 0);
					} else {
						fighter.getPlayer().sendMessage(Utils.notify(String.format(
								language.getString("death"), player.getName())));
					}
				if (arena.hasPlayerDeathSound())
					fighter.getPlayer().playSound(arena.getPlayerSpawn(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10,
							.75f);
			});

			// Update scoreboards
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)));

			// Check for game end condition
			if (arena.getAlive() == 0 && arena.getStatus() == ArenaStatus.ACTIVE)
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
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		ItemStack item = e.getItem().getItemStack();

		// Check for gem item
		if (!item.getType().equals(Material.EMERALD))
			return;

		// Ignore item shop
		if (item.getItemMeta().getDisplayName().contains("Item Shop"))
			return;

		// Calculate and give player gems
		int stack = item.getAmount();
		Random r = new Random();
		int wave = arena.getCurrentWave();
		int earned = 0;
		for (int i = 0; i < stack; i++) {
			int temp = r.nextInt((int) (50 * Math.pow(wave, .15)));
			earned += temp == 0 ? 1 : temp;
		}
		gamer.addGems(earned);

		// Cancel picking up of emeralds and notify player
		e.setCancelled(true);
		e.getItem().remove();
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format(plugin.getLanguageData().getString("foundGems"), earned))));
		if (arena.hasGemSound())
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, .5f, 0);

		FileConfiguration playerData = plugin.getPlayerData();

		// Update player stats
		playerData.set(player.getName() + ".totalGems",
				playerData.getInt(player.getName() + ".totalGems") + earned);
		if (playerData.getInt(player.getName() + ".topBalance") < gamer.getGems())
			playerData.set(player.getName() + ".topBalance", gamer.getGems());
		plugin.savePlayerData();

		// Update scoreboard
		plugin.getGame().createBoard(gamer);
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
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		FileConfiguration language = plugin.getLanguageData();

		// Check if arena is active
		if (arena.getStatus() != ArenaStatus.ACTIVE) return;

		// Check if player is about to die
		if (e.getFinalDamage() < player.getHealth()) return;

		// Check if player is holding a totem
		if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING ||
				player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) return;

		// Set player to fake death mode
		e.setCancelled(true);
		player.setGameMode(GameMode.SPECTATOR);
		player.getInventory().clear();
		player.closeInventory();
		gamer.setStatus(PlayerStatus.GHOST);

		// Notify player of their own death
		player.sendTitle(Utils.format(language.getString("death1")), language.getString("death2"),
				Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

		// Notify everyone else of player death
		arena.getPlayers().forEach(fighter -> {
			if (!fighter.getPlayer().getUniqueId().equals(player.getUniqueId()))
				if (language.getString("death") == null) {
					plugin.debugError("The language file is missing the attribute 'death'!", 0);
				} else {
					fighter.getPlayer().sendMessage(Utils.notify(String.format(
							language.getString("death"), player.getName())));
				}
			if (arena.hasPlayerDeathSound())
				fighter.getPlayer().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10, .75f);
		});

		// Update scoreboards
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)));

		// Check for game end condition
		if (arena.getAlive() == 0 && arena.getStatus() == ArenaStatus.ACTIVE)
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

		// Prevent wither roses from being created
		if (e.getDamager() instanceof WitherSkull || e.getDamager() instanceof Wither) {
			e.setCancelled(true);
			e.getEntity().remove();
		}

		// Check that a player caused the damage
		if (!(e.getDamager() instanceof Player || e.getDamager() instanceof Projectile)) return;

		Player player;
		Arena arena;
		VDPlayer gamer;

		// Check if projectile came from player, then set player
		if (e.getDamager() instanceof Projectile) {
			if (((Projectile) e.getDamager()).getShooter() instanceof Player)
				player = (Player) ((Projectile) e.getDamager()).getShooter();
			else return;
		} else player = (Player) e.getDamager();

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull)
					.filter(a -> a.getPlayers().stream().anyMatch(p -> p.getPlayer().equals(player)))
					.collect(Collectors.toList()).get(0);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Increment kill count
		gamer.incrementKills();

		// Add gems, loot, and experience if needed
		if (!arena.hasGemDrop()) {
			// Calculate and give player gems
			Random r = new Random();
			int wave = arena.getCurrentWave();

			if (e.getEntity() instanceof Wither) {
				int earned = r.nextInt((int) (50 * Math.pow(wave, .15) * 20) / arena.getAlive());
				arena.getActives().stream().filter(vdPlayer -> !arena.getGhosts().contains(vdPlayer))
						.forEach(vdPlayer -> {
							vdPlayer.addGems(earned);

							// Notify player
							vdPlayer.getPlayer().sendMessage(
									Utils.notify(String.format(plugin.getLanguageData().getString("earnedGems"),
									earned)));

							FileConfiguration playerData = plugin.getPlayerData();

							// Update player stats
							playerData.set(vdPlayer.getPlayer().getName() + ".totalGems",
									playerData.getInt(vdPlayer.getPlayer().getName() + ".totalGems") + earned);
							if (playerData.getInt(vdPlayer.getPlayer().getName() + ".topBalance") <
									vdPlayer.getGems())
								playerData.set(vdPlayer.getPlayer().getName() + ".topBalance", vdPlayer.getGems());
							plugin.savePlayerData();

							// Update scoreboard
							plugin.getGame().createBoard(vdPlayer);
						});
			} else {
				int earned = r.nextInt((int) (50 * Math.pow(wave, .15)));
				gamer.addGems(earned == 0 ? 1 : earned);

				// Get rare loot probability
				double probability;
				switch (arena.getDifficultyMultiplier()) {
					case 1:
						probability = .015;
						break;
					case 2:
						probability = .01;
						break;
					case 3:
						probability = .008;
						break;
					default:
						probability = .006;
				}

				if (r.nextDouble() < probability)
					Utils.giveItem(player, GameItems.randCare(wave / 10 + 1),
							plugin.getLanguageData().getString("inventoryFull"));

				// Notify player
				player.sendMessage(Utils.notify(String.format(plugin.getLanguageData().getString("earnedGems"),
						earned)));

				FileConfiguration playerData = plugin.getPlayerData();

				// Update player stats
				playerData.set(player.getName() + ".totalGems",
						playerData.getInt(player.getName() + ".totalGems") + earned);
				if (playerData.getInt(player.getName() + ".topBalance") < gamer.getGems())
					playerData.set(player.getName() + ".topBalance", gamer.getGems());
				plugin.savePlayerData();

				// Update scoreboard
				plugin.getGame().createBoard(gamer);
			}
		}
		if (!arena.hasExpDrop()) {
			if (e.getEntity() instanceof Wither)
				arena.getActives().stream().filter(vdPlayer -> !arena.getGhosts().contains(vdPlayer))
						.forEach(vdPlayer -> vdPlayer.getPlayer()
								.giveExp((int) (arena.getCurrentDifficulty() * 40) / arena.getAlive()));
			else player.giveExp((int) (arena.getCurrentDifficulty() * 2));
		}
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
		ItemStack item = e.getItem() == null ? new ItemStack(Material.AIR) : e.getItem();
		ItemStack main = player.getInventory().getItemInMainHand();
		FileConfiguration language = plugin.getLanguageData();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Avoid false consume
		if (main.equals(GameItems.shop()) || Arrays.asList(GameItems.ABILITY_ITEMS).contains(main) ||
				Arrays.stream(GameItems.FOOD_MATERIALS).anyMatch(m -> m == main.getType()) ||
				Arrays.stream(GameItems.ARMOR_MATERIALS).anyMatch(m -> m == main.getType()) ||
				Arrays.stream(GameItems.CLICKABLE_WEAPON_MATERIALS).anyMatch(m -> m == main.getType()) ||
				(Arrays.stream(GameItems.CLICKABLE_CONSUME_MATERIALS).anyMatch(m -> m == main.getType()) &&
						main.getType() != GameItems.wolf().getType() && main.getType() != GameItems.golem().getType() ))
			return;

		// Wolf spawn
		if (item.getType() == Material.WOLF_SPAWN_EGG && 
				!(main.getType() == Material.WOLF_SPAWN_EGG && e.getHand() == EquipmentSlot.OFF_HAND) &&
				main.getType() != Material.POLAR_BEAR_SPAWN_EGG && main.getType() != Material.COAL_BLOCK &&
				main.getType() != Material.IRON_BLOCK && main.getType() != Material.DIAMOND_BLOCK &&
				main.getType() != Material.BEACON) {
			// Ignore if it wasn't a right click on a block
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;

			// Cancel normal spawn
			e.setCancelled(true);

			// Check for wolf cap
			if (gamer.getWolves() >= arena.getWolfCap()) {
				player.sendMessage(Utils.notify(String.format(language.getString("wolfError"),
						arena.getWolfCap())));
				return;
			}

			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(e.getHand(), null);

			Location location = e.getClickedBlock().getLocation();
			location.setY(location.getY() + 1);

			// Spawn and tame the wolf
			Mobs.setWolf(plugin, arena, gamer, (Wolf) player.getWorld().spawnEntity(location, EntityType.WOLF));
			return;
		}

		// Ignore other vanilla items
		if (item.getItemMeta() == null)
			return;

		// Iron golem spawn
		if (item.getItemMeta().getDisplayName().contains("Iron Golem Spawn Egg") &&
				!(main.getType() == Material.POLAR_BEAR_SPAWN_EGG && e.getHand() == EquipmentSlot.OFF_HAND) &&
				main.getType() != Material.WOLF_SPAWN_EGG &&
				main.getType() != Material.COAL_BLOCK && main.getType() != Material.IRON_BLOCK &&
				main.getType() != Material.DIAMOND_BLOCK && main.getType() != Material.BEACON) {
			// Ignore if it wasn't a right click on a block
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;

			// Cancel normal spawn
			e.setCancelled(true);

			// Check for golem cap
			if (arena.getGolems() >= arena.getGolemCap()) {
				player.sendMessage(Utils.notify(String.format(language.getString("golemError"),
						arena.getGolemCap())));
				return;
			}

			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(e.getHand(), null);

			Location location = e.getClickedBlock().getLocation();
			location.setY(location.getY() + 1);

			// Spawn iron golem
			Mobs.setGolem(plugin, arena, (IronGolem) player.getWorld().spawnEntity(location, EntityType.IRON_GOLEM));
			return;
		}

		// Small care package
		if (item.getItemMeta().getDisplayName().contains("Small Care Package") &&
				main.getType() != Material.POLAR_BEAR_SPAWN_EGG && 
				!(main.getType() == Material.COAL_BLOCK && e.getHand() == EquipmentSlot.OFF_HAND) &&
				main.getType() != Material.WOLF_SPAWN_EGG && main.getType() != Material.IRON_BLOCK &&
				main.getType() != Material.DIAMOND_BLOCK && main.getType() != Material.BEACON) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(e.getHand(), null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(1))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(1))),
						language.getString("inventoryFull"));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(1)),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(1)),
						language.getString("inventoryFull"));
			}
			player.sendMessage(Utils.notify(language.getString("carePackage")));
		}

		// Medium care package
		if (item.getItemMeta().getDisplayName().contains("Medium Care Package") &&
				main.getType() != Material.POLAR_BEAR_SPAWN_EGG && main.getType() != Material.COAL_BLOCK &&
				!(main.getType() == Material.IRON_BLOCK && e.getHand() == EquipmentSlot.OFF_HAND) &&
				main.getType() != Material.WOLF_SPAWN_EGG &&
				main.getType() != Material.DIAMOND_BLOCK && main.getType() != Material.BEACON) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(e.getHand(), null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(2))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(2))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randNotCare(2))),
						language.getString("inventoryFull"));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(2)),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(2)),
						language.getString("inventoryFull"));
				if (gamer.getKit().equals("Witch"))
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randNotCare(2))),
							language.getString("inventoryFull"));
				else Utils.giveItem(player, Utils.removeLastLore(GameItems.randNotCare(2)),
						language.getString("inventoryFull"));
			}
			player.sendMessage(Utils.notify(language.getString("carePackage")));
		}

		// Large care package
		if (item.getItemMeta().getDisplayName().contains("Large Care Package") &&
				main.getType() != Material.WOLF_SPAWN_EGG && main.getType() != Material.POLAR_BEAR_SPAWN_EGG &&
				main.getType() != Material.COAL_BLOCK && main.getType() != Material.IRON_BLOCK &&
				!(main.getType() == Material.DIAMOND_BLOCK && e.getHand() == EquipmentSlot.OFF_HAND) &&
				main.getType() != Material.BEACON) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(e.getHand(), null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(4))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(3))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(3))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randNotCare(3))),
						language.getString("inventoryFull"));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(4)),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(3)),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(3)),
						language.getString("inventoryFull"));
				if (gamer.getKit().equals("Witch"))
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randNotCare(3))),
							language.getString("inventoryFull"));
				else Utils.giveItem(player, Utils.removeLastLore(GameItems.randNotCare(3)),
						language.getString("inventoryFull"));
			}
			player.sendMessage(Utils.notify(language.getString("carePackage")));
		}

		// Extra large care package
		if (item.getItemMeta().getDisplayName().contains("Extra Large Care Package") &&
				main.getType() != Material.WOLF_SPAWN_EGG && main.getType() != Material.POLAR_BEAR_SPAWN_EGG &&
				main.getType() != Material.COAL_BLOCK && main.getType() != Material.IRON_BLOCK &&
				main.getType() != Material.DIAMOND_BLOCK && 
				!(main.getType() == Material.BEACON && e.getHand() == EquipmentSlot.OFF_HAND)) {
			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(e.getHand(), null);

			// Give items and notify
			if (gamer.getKit().equals("Blacksmith")) {
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(5))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randWeapon(4))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(5))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randArmor(4))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randNotCare(4))),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.makeUnbreakable(Utils.removeLastLore(GameItems.randNotCare(4))),
						language.getString("inventoryFull"));
			} else {
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(5)),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randWeapon(4)),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(5)),
						language.getString("inventoryFull"));
				Utils.giveItem(player, Utils.removeLastLore(GameItems.randArmor(4)),
						language.getString("inventoryFull"));
				if (gamer.getKit().equals("Witch")) {
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randNotCare(4))),
							language.getString("inventoryFull"));
					Utils.giveItem(player, Utils.makeSplash(Utils.removeLastLore(GameItems.randNotCare(4))),
							language.getString("inventoryFull"));
				} else {
					Utils.giveItem(player, Utils.removeLastLore(GameItems.randNotCare(4)),
							language.getString("inventoryFull"));
					Utils.giveItem(player, Utils.removeLastLore(GameItems.randNotCare(4)),
							language.getString("inventoryFull"));
				}
			}
			player.sendMessage(Utils.notify(language.getString("carePackage")));
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
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer((Player) ((Wolf) ent).getOwner())))
			return;

		e.setCancelled(true);
	}

	// Prevent players from teleporting when in a game
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Player player = e.getPlayer();

		// Check if player is playing in an arena
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		Arena arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Cancel teleport and notify if teleport is outside arena bounds
		if (!(BoundingBox.of(arena.getCorner1(), arena.getCorner2())
				.contains(e.getTo().getX(), e.getTo().getY(), e.getTo().getZ())) ||
				!Objects.equals(e.getTo().getWorld(), arena.getCorner1().getWorld())) {
			e.setCancelled(true);
			player.sendMessage(Utils.notify(plugin.getLanguageData().getString("teleportError")));
		}
	}

	// Prevent players from leaving the arena bounds
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();

		// Check if player is playing in an arena
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		// Exempt myself for testing purposes
		if (plugin.getDebugLevel() >= 3 && player.hasPermission("vd.admin"))
			return;

		Arena arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);
		VDPlayer gamer;

		// Attempt to get VDPlayer
		try {
			gamer = arena.getPlayer(player);
		} catch (PlayerNotFoundException err) {
			return;
		}

		// Ignore players that have already left
		if (gamer.getStatus() == PlayerStatus.LEFT)
			return;

		// Cancel move and notify if movement is outside arena bounds
		if (!(BoundingBox.of(arena.getCorner1(), arena.getCorner2())
				.contains(e.getTo().getX(), e.getTo().getY(), e.getTo().getZ())) ||
				!Objects.equals(e.getTo().getWorld(), arena.getCorner1().getWorld())) {

			// Teleport player back into arena after several infractions
			if (gamer.incrementInfractions() > 5) {
				gamer.resetInfractions();
				if (gamer.getStatus() == PlayerStatus.ALIVE)
					player.teleport(arena.getPlayerSpawn());
				else Utils.teleSpectator(player, arena.getPlayerSpawn());
			} else e.setCancelled(true);

			player.sendMessage(Utils.notify(plugin.getLanguageData().getString("boundsError")));
		}
	}

	// Prevents arena mobs from turning into different entities
	@EventHandler
	public void onTransform(EntityTransformEvent e) {
		Entity ent = e.getEntity();

		// Check for special mob
		if (!ent.hasMetadata("VD"))
			return;

		e.setCancelled(true);
	}

	// Prevent zombies from breaking doors
	@EventHandler
	public void onBreakDoor(EntityBreakDoorEvent e) {
		Entity ent = e.getEntity();

		// Check for special mob
		if (!ent.hasMetadata("VD"))
			return;

		e.setCancelled(true);
	}

	// Prevent players from dropping standard game items
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItemDrop().getItemStack();

		// Check if player is in an arena
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(arena -> arena.hasPlayer(player)))
			return;

		// Check for standard game items item
		if (item.equals(GameItems.shop()) || item.equals(GameItems.kitSelector()) || item.equals(GameItems.leave()))
			e.setCancelled(true);
	}

	// Delete bottles and buckets after using consumables
	@EventHandler
	public void onFinishConsumption(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();

		// Check if player is playing in an arena
		if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
			return;

		if (e.getItem().getType() == Material.POTION || e.getItem().getType() == Material.MILK_BUCKET) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				player.getInventory().remove(Material.GLASS_BOTTLE);
				player.getInventory().remove(Material.BUCKET);
				if (player.getInventory().getItemInOffHand().getType() == Material.GLASS_BOTTLE)
					player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				if (player.getInventory().getItemInOffHand().getType() == Material.BUCKET)
					player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			}, 3);
		}
	}

	// Prevent consumption from happening in the off hand when the main hand has something interact-able
	@EventHandler
	public void onFalseConsume(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack main = player.getInventory().getItemInMainHand();
		Arena arena;

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Filter off hand interactions
		if (e.getHand() != EquipmentSlot.OFF_HAND)
			return;

		// Avoid false consume
		if (main.equals(GameItems.shop()) || Arrays.asList(GameItems.ABILITY_ITEMS).contains(main) ||
				Arrays.stream(GameItems.FOOD_MATERIALS).anyMatch(m -> m == main.getType()) ||
				Arrays.stream(GameItems.ARMOR_MATERIALS).anyMatch(m -> m == main.getType()) ||
				Arrays.stream(GameItems.CARE_MATERIALS).anyMatch(m -> m == main.getType()) ||
				Arrays.stream(GameItems.CLICKABLE_WEAPON_MATERIALS).anyMatch(m -> m == main.getType()) ||
				Arrays.stream(GameItems.CLICKABLE_CONSUME_MATERIALS).anyMatch(m -> m == main.getType()))
			e.setCancelled(true);
	}

	// Prevent moving items around while waiting for game to start
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Arena arena;

		// Attempt to get arena and player
		try {
			arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Cancel event if arena is in waiting mode
		if (arena.getStatus() == ArenaStatus.WAITING)
			e.setCancelled(true);
	}
}
