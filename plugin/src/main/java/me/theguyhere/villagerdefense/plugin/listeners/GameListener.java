package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.GUI.Inventories;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.events.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.plugin.game.models.*;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
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
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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

		Arena arena = GameManager.getArena(ent.getMetadata("VD").get(0).asInt());

		// Check for right game
		if (!ent.hasMetadata("game"))
			return;
		if (ent.getMetadata("game").get(0).asInt() != arena.getGameID())
			return;

		// Arena enemies not part of an active arena
		if (arena.getStatus() != ArenaStatus.ACTIVE) {
			e.getDrops().clear();
			return;
		}

		// Check for right wave
		if (!ent.hasMetadata("wave"))
			return;
		if (ent.getMetadata("wave").get(0).asInt() != arena.getCurrentWave())
			return;

		// Clear normal drops
		e.getDrops().clear();
		e.setDroppedExp(0);

		DataManager data;

		// Get spawn table
		if (arena.getSpawnTableFile().equals("custom"))
			data = new DataManager(plugin, "spawnTables/a" + arena.getArena() + ".yml");
		else data = new DataManager(plugin, "spawnTables/" + arena.getSpawnTableFile() + ".yml");

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
					e.getDrops().add(ItemManager.createItems(Material.EMERALD, 20, null,
							Integer.toString(arena.getArena())));
				if (arena.hasExpDrop())
					e.setDroppedExp((int) (arena.getCurrentDifficulty() * 40));
			} else {
				if (arena.hasGemDrop()) {
					e.getDrops().add(ItemManager.createItem(Material.EMERALD, null,
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
			if (arena.getEnemies() <= .2 * count && !arena.isSpawningMonsters() && arena.getEnemies() > 0)
				arena.setMonsterGlow();
		}

		// Update scoreboards
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)));
	}

	// Stop automatic game mode switching between worlds
	@EventHandler
	public void onGameModeSwitch(PlayerGameModeChangeEvent e) {
		if (GameManager.checkPlayer(e.getPlayer()) && e.getNewGameMode() == GameMode.SURVIVAL)
			e.setCancelled(true);
	}

	// Handle creeper explosions
	@EventHandler
	public void onExplode(ExplosionPrimeEvent e) {

		Entity ent = e.getEntity();

		// Check for arena enemies
		if (!ent.hasMetadata("VD"))
			return;

		Arena arena = GameManager.getArena(ent.getMetadata("VD").get(0).asInt());

		// Check for right game
		if (!ent.hasMetadata("game"))
			return;
		if (ent.getMetadata("game").get(0).asInt() != arena.getGameID())
			return;

		// Arena enemies not part of an active arena
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			return;

		// Check for right wave
		if (!ent.hasMetadata("wave"))
			return;
		if (ent.getMetadata("wave").get(0).asInt() != arena.getCurrentWave())
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
		if (item.getType() == Material.EMERALD && item.hasItemMeta() &&
				Objects.requireNonNull(item.getItemMeta()).hasLore())
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

		Player player;
		VDPlayer gamer;

		// Check for player damager, then get player
		if (damager instanceof Player)
			player = (Player) damager;
		else if (damager instanceof Projectile &&
				((Projectile) damager).getShooter() instanceof Player)
			player = (Player) ((Projectile) damager).getShooter();
		else player = null;

		// Attempt to get VDplayer
		if (player != null) {
			try {
				gamer = GameManager.getArena(player).getPlayer(player);
			} catch (Exception err) {
				return;
			}
		} else gamer = null;

		// Check for pacifist challenge
		if (gamer != null)
			if (gamer.getChallenges().contains(Challenge.pacifist()))
				// Cancel if not an enemy of the player
				if (!gamer.getEnemies().contains(ent.getUniqueId()))
					return;

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
		double maxHealth = Objects.requireNonNull(n.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

		// Update health bar
		if (ent instanceof IronGolem || ent instanceof Ravager)
			ent.setCustomName(Mobs.healthBar(maxHealth, n.getHealth() - e.getFinalDamage(), 10));
		else ent.setCustomName(Mobs.healthBar(maxHealth, n.getHealth() - e.getFinalDamage(), 5));
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
		double maxHealth = Objects.requireNonNull(n.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

		// Update health bar
		if (ent instanceof IronGolem || ent instanceof Ravager)
			ent.setCustomName(Mobs.healthBar(maxHealth, n.getHealth() - e.getFinalDamage(), 10));
		else ent.setCustomName(Mobs.healthBar(maxHealth, n.getHealth() - e.getFinalDamage(), 5));
	}

	// Prevent players from going hungry while waiting for an arena to start
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		Player player = (Player) e.getEntity();

		// See if player is in a game and if game is already in progress
		try {
			if (GameManager.getArena(player).getCurrentWave() != 0)
				return;
		} catch (Exception err) {
			return;
		}

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
		double maxHealth = Objects.requireNonNull(n.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
		double modifiedHealth = n.getHealth() + e.getAmount();

		// Update health bar
		if (ent instanceof IronGolem || ent instanceof Ravager)
			ent.setCustomName(Mobs.healthBar(maxHealth, Math.min(modifiedHealth, maxHealth), 10));
		else ent.setCustomName(Mobs.healthBar(maxHealth, Math.min(modifiedHealth, maxHealth), 5));
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

		// Attempt to get arena and player
		try {
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Get item in hand
		ItemStack item;
		if (e.getHand() == EquipmentSlot.OFF_HAND) {
			item = Objects.requireNonNull(player.getEquipment()).getItemInOffHand();

			// Check for other clickables in main hand
			if (Arrays.asList(GameItems.ABILITY_ITEMS).contains(item) ||
					Arrays.asList(GameItems.FOOD_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.ARMOR_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.CARE_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.CLICKABLE_WEAPON_MATERIALS).contains(item.getType()) ||
					Arrays.asList(GameItems.CLICKABLE_CONSUME_MATERIALS).contains(item.getType()))
				return;
		}
		else item = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();

		// Open shop inventory
		if (GameItems.shop().equals(item))
			player.openInventory(Inventories.createShop(arena.getCurrentWave() / 10 + 1, arena));

		// Open kit selection menu
		else if (GameItems.kitSelector().equals(item))
			player.openInventory(Inventories.createSelectKitsInventory(player, arena));

		// Open challenge selection menu
		else if (GameItems.challengeSelector().equals(item))
			player.openInventory(Inventories.createSelectChallengesInventory(gamer, arena));

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
			if (GameManager.checkPlayer((Player) ent))
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
			// Player on player
			if (ent instanceof Player && ((Projectile) damager).getShooter() instanceof Player) {
				if (GameManager.checkPlayer((Player) ent))
					e.setCancelled(true);
			}

			// Player on friendly
			if ((ent instanceof Villager || ent instanceof Wolf || ent instanceof IronGolem) &&
					((Projectile) damager).getShooter() instanceof Player)
				e.setCancelled(true);

			// Monster on monster
			else if ((ent instanceof Monster || ent instanceof Slime) &&
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
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Check if game has started yet
		if (arena.getStatus() == ArenaStatus.WAITING) {
			// Cancel void damage
			e.setCancelled(true);

			// Teleport player back to player spawn or waiting room
			if (arena.getWaitingRoom() == null)
				try {
					player.teleport(arena.getPlayerSpawn().getLocation());
				} catch (NullPointerException err) {
					CommunicationManager.debugError(err.getMessage(), 0);
				}
			else player.teleport(arena.getWaitingRoom());
		} else {
			// Set player to fake death mode
			PlayerManager.fakeDeath(gamer);

			// Notify player of their own death
			player.sendTitle(CommunicationManager.format("&4" + LanguageManager.messages.death1),
					CommunicationManager.format("&c" + LanguageManager.messages.death2),
					Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

			// Teleport player back to player spawn
			try {
				player.teleport(arena.getPlayerSpawn().getLocation());
			} catch (NullPointerException err) {
				CommunicationManager.debugError(err.getMessage(), 0);
			}
			player.closeInventory();

			// Notify everyone else of player death
			arena.getPlayers().forEach(fighter -> {
				if (!fighter.getPlayer().getUniqueId().equals(player.getUniqueId()))
					PlayerManager.notifyAlert(fighter.getPlayer(),
							String.format(LanguageManager.messages.death, player.getName()));
				if (arena.hasPlayerDeathSound())
					try {
						fighter.getPlayer().playSound(arena.getPlayerSpawn().getLocation(),
								Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10,
								.75f);
					} catch (NullPointerException err) {
						CommunicationManager.debugError(err.getMessage(), 0);
					}
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
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		ItemStack item = e.getItem().getItemStack();

		// Check for gem item
		if (!item.getType().equals(Material.EMERALD))
			return;

		// Ignore item shop
		if (Objects.requireNonNull(item.getItemMeta()).getDisplayName().contains(LanguageManager.names.itemShop))
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
				CommunicationManager.format(ChatColor.GREEN, LanguageManager.messages.foundGems,
						ChatColor.AQUA, Integer.toString(earned))));
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
		GameManager.createBoard(gamer);
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
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Check if arena is active
		if (arena.getStatus() != ArenaStatus.ACTIVE) return;

		// Check if player is about to die
		if (e.getFinalDamage() < player.getHealth()) return;

		// Check if player is holding a totem
		if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING ||
				player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) return;

		// Set player to fake death mode
		e.setCancelled(true);
		PlayerManager.fakeDeath(gamer);

		// Notify player of their own death
		player.sendTitle(CommunicationManager.format("&4" + LanguageManager.messages.death1),
				CommunicationManager.format("&c" + LanguageManager.messages.death2),
				Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

		// Notify everyone else of player death
		arena.getPlayers().forEach(fighter -> {
			if (!fighter.getPlayer().getUniqueId().equals(player.getUniqueId()))
				PlayerManager.notifyAlert(fighter.getPlayer(),
						String.format(LanguageManager.messages.death, player.getName()));
			if (arena.hasPlayerDeathSound())
				try {
					fighter.getPlayer().playSound(arena.getPlayerSpawn().getLocation(),
							Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10,
							.75f);
				} catch (NullPointerException err) {
					CommunicationManager.debugError(err.getMessage(), 0);
				}
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
		assert player != null;

		// Attempt to get arena and player
		try {
			arena = GameManager.getArena(player);
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
							PlayerManager.notifySuccess(vdPlayer.getPlayer(),
									LanguageManager.messages.earnedGems, ChatColor.AQUA,
									Integer.toString(earned));

							FileConfiguration playerData = plugin.getPlayerData();

							// Update player stats
							playerData.set(vdPlayer.getPlayer().getName() + ".totalGems",
									playerData.getInt(vdPlayer.getPlayer().getName() + ".totalGems") + earned);
							if (playerData.getInt(vdPlayer.getPlayer().getName() + ".topBalance") <
									vdPlayer.getGems())
								playerData.set(vdPlayer.getPlayer().getName() + ".topBalance", vdPlayer.getGems());
							plugin.savePlayerData();

							// Update scoreboard
							GameManager.createBoard(vdPlayer);
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
					PlayerManager.giveItem(player, GameItems.randCare(wave / 10 + 1),
							LanguageManager.errors.inventoryFull);

				// Notify player
				PlayerManager.notifySuccess(player, LanguageManager.messages.earnedGems,
						ChatColor.AQUA, Integer.toString(earned));

				FileConfiguration playerData = plugin.getPlayerData();

				// Update player stats
				playerData.set(player.getName() + ".totalGems",
						playerData.getInt(player.getName() + ".totalGems") + earned);
				if (playerData.getInt(player.getName() + ".topBalance") < gamer.getGems())
					playerData.set(player.getName() + ".topBalance", gamer.getGems());
				plugin.savePlayerData();

				// Update scoreboard
				GameManager.createBoard(gamer);
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
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			arena = GameManager.getArena(player);
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
				PlayerManager.notifyFailure(player, LanguageManager.errors.wolf, ChatColor.AQUA,
						Integer.toString(arena.getWolfCap()));
				return;
			}

			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);

			Location location = Objects.requireNonNull(e.getClickedBlock()).getLocation();
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
				PlayerManager.notifyFailure(player, LanguageManager.errors.golem, ChatColor.AQUA,
						Integer.toString(arena.getGolemCap()));
				return;
			}

			// Remove an item
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);

			Location location = Objects.requireNonNull(e.getClickedBlock()).getLocation();
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
			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);

			// Give items and notify
			if (gamer.getKit().equals(Kit.blacksmith().setKitLevel(1))) {
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randWeapon(1))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randArmor(1))), LanguageManager.errors.inventoryFull);
			} else {
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randWeapon(1)),
						LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randArmor(1)),
						LanguageManager.errors.inventoryFull);
			}
			PlayerManager.notifySuccess(player, LanguageManager.confirms.carePackage);
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
			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);

			// Give items and notify
			if (gamer.getKit().equals(Kit.blacksmith().setKitLevel(1))) {
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randWeapon(2))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randArmor(2))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randNotCare(2))), LanguageManager.errors.inventoryFull);
			} else {
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randWeapon(2)),
						LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randArmor(2)),
						LanguageManager.errors.inventoryFull);
				if (gamer.getKit().equals(Kit.witch().setKitLevel(1)))
					PlayerManager.giveItem(player, ItemManager.makeSplash(ItemManager.removeLastLore(
							GameItems.randNotCare(2))), LanguageManager.errors.inventoryFull);
				else PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randNotCare(2)),
						LanguageManager.errors.inventoryFull);
			}
			PlayerManager.notifySuccess(player, LanguageManager.confirms.carePackage);
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
			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);

			// Give items and notify
			if (gamer.getKit().equals(Kit.blacksmith().setKitLevel(1))) {
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randWeapon(4))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randArmor(3))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randArmor(3))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randNotCare(3))), LanguageManager.errors.inventoryFull);
			} else {
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randWeapon(4)),
						LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randArmor(3)),
						LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randArmor(3)),
						LanguageManager.errors.inventoryFull);
				if (gamer.getKit().equals(Kit.witch().setKitLevel(1)))
					PlayerManager.giveItem(player, ItemManager.makeSplash(ItemManager.removeLastLore(
							GameItems.randNotCare(3))), LanguageManager.errors.inventoryFull);
				else PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randNotCare(3)),
						LanguageManager.errors.inventoryFull);
			}
			PlayerManager.notifySuccess(player, LanguageManager.confirms.carePackage);
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
			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);

			// Give items and notify
			if (gamer.getKit().equals(Kit.blacksmith().setKitLevel(1))) {
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randWeapon(5))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randWeapon(4))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randArmor(5))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randArmor(4))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randNotCare(4))), LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.makeUnbreakable(ItemManager.removeLastLore(
						GameItems.randNotCare(4))), LanguageManager.errors.inventoryFull);
			} else {
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randWeapon(5)),
						LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randWeapon(4)),
						LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randArmor(5)),
						LanguageManager.errors.inventoryFull);
				PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randArmor(4)),
						LanguageManager.errors.inventoryFull);
				if (gamer.getKit().equals(Kit.witch().setKitLevel(1))) {
					PlayerManager.giveItem(player, ItemManager.makeSplash(ItemManager.removeLastLore(
							GameItems.randNotCare(4))), LanguageManager.errors.inventoryFull);
					PlayerManager.giveItem(player, ItemManager.makeSplash(ItemManager.removeLastLore(
							GameItems.randNotCare(4))), LanguageManager.errors.inventoryFull);
				} else {
					PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randNotCare(4)),
							LanguageManager.errors.inventoryFull);
					PlayerManager.giveItem(player, ItemManager.removeLastLore(GameItems.randNotCare(4)),
							LanguageManager.errors.inventoryFull);
				}
			}
			PlayerManager.notifySuccess(player, LanguageManager.confirms.carePackage);
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
		if (GameManager.checkPlayer((Player) ((Wolf) ent).getOwner()))
			return;

		e.setCancelled(true);
	}

	// Prevent players from teleporting when in a game
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		Arena arena;

		// Attempt to get arena
		try {
			arena = GameManager.getArena(player);
		} catch (Exception err) {
			return;
		}

		// Check if the arena has started
		if (arena.getStatus() == ArenaStatus.WAITING)
			return;

		// Cancel teleport and notify if teleport is outside arena bounds
		if (!(BoundingBox.of(arena.getCorner1(), arena.getCorner2())
				.contains(Objects.requireNonNull(e.getTo()).getX(), e.getTo().getY(), e.getTo().getZ())) ||
				!Objects.equals(e.getTo().getWorld(), arena.getCorner1().getWorld())) {
			e.setCancelled(true);
			PlayerManager.notifyFailure(player, LanguageManager.errors.teleport, ChatColor.AQUA, "/vd leave");
		}
	}

	// Prevent players from leaving the arena bounds
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Arena arena;
		VDPlayer gamer;

		// Exempt admins for testing purposes
		if (CommunicationManager.getDebugLevel() >= 3 && player.hasPermission("vd.admin"))
			return;

		// Attempt to get VDPlayer and arena
		try {
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
		} catch (Exception err) {
			return;
		}

		// Exempt if in waiting status and waiting room exists
		if (arena.getStatus() == ArenaStatus.WAITING && arena.getWaitingRoom() != null)
			return;

		// Ignore players that have already left
		if (gamer.getStatus() == PlayerStatus.LEFT)
			return;

		// Cancel move and notify if movement is outside arena bounds
		if (!(BoundingBox.of(arena.getCorner1(), arena.getCorner2())
				.contains(Objects.requireNonNull(e.getTo()).getX(), e.getTo().getY(), e.getTo().getZ())) ||
				!Objects.equals(e.getTo().getWorld(), arena.getCorner1().getWorld())) {

			// Teleport player back into arena after several infractions
			if (gamer.incrementInfractions() > 5) {
				gamer.resetInfractions();
				try {
					if (gamer.getStatus() == PlayerStatus.ALIVE)
						player.teleport(arena.getPlayerSpawn().getLocation());
					else PlayerManager.teleSpectator(player, arena.getPlayerSpawn().getLocation());
				} catch (NullPointerException err) {
					CommunicationManager.debugError(err.getMessage(), 0);
				}
			} else e.setCancelled(true);

			PlayerManager.notifyFailure(player, LanguageManager.errors.bounds);
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
		if (!GameManager.checkPlayer(player))
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
		if (!GameManager.checkPlayer(player))
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

	// Prevent consumption from happening in the off-hand when the main hand has something interact-able
	@EventHandler
	public void onFalseConsume(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack main = player.getInventory().getItemInMainHand();

		// Check for player in arena
		if (!GameManager.checkPlayer(player))
			return;

		// Filter off-hand interactions
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

		// Attempt to get arena
		try {
			arena = GameManager.getArena(player);
		} catch (Exception err) {
			return;
		}

		// Cancel event if arena is in waiting mode
		if (arena.getStatus() == ArenaStatus.WAITING)
			e.setCancelled(true);
	}

	// Apply book enchantment to items
	@EventHandler
	public void onEnchantingApply(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack clickedOn = e.getCurrentItem();
		ItemStack clickedWith = e.getCursor();
		EnchantingBook enchantingBook = EnchantingBook.check(clickedWith);

		// Check for player in arena
		if (!GameManager.checkPlayer(player))
			return;

		// Ignore if not clicking on own inventory
		if (e.getClickedInventory() == null || !player.equals(e.getClickedInventory().getHolder()))
			return;

		// Ignore clicks to nothing
		if (clickedOn == null || clickedOn.getType() == Material.AIR)
			return;

		// Ignore clicks on shop or other books
		if (EnchantingBook.check(clickedOn) != null || GameItems.shop().equals(clickedOn))
			return;

		// Check for enchanting book
		if (enchantingBook == null)
			return;

		// Cancel event
		e.setCancelled(true);

		// Attempt to add enchant and remove book
		Map<Enchantment, Integer> enchantList = Objects.requireNonNull(clickedOn.getItemMeta()).getEnchants();
		if (enchantList.containsKey(enchantingBook.getEnchantToAdd())) {
			if (enchantingBook.getEnchantToAdd() == Enchantment.ARROW_FIRE ||
					enchantingBook.getEnchantToAdd() == Enchantment.MULTISHOT ||
					enchantingBook.getEnchantToAdd() == Enchantment.ARROW_INFINITE ||
					enchantingBook.getEnchantToAdd() == Enchantment.MENDING) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.enchant);
				return;
			}
			clickedOn.addUnsafeEnchantment(enchantingBook.getEnchantToAdd(),
					enchantList.get(enchantingBook.getEnchantToAdd()) + 1);
		} else clickedOn.addUnsafeEnchantment(enchantingBook.getEnchantToAdd(), 1);
		player.setItemOnCursor(new ItemStack(Material.AIR));
		PlayerManager.notifySuccess(player, LanguageManager.confirms.enchant);
	}

	// Prevent swapping items while waiting for game to start
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent e) {
		Player player = e.getPlayer();
		Arena arena;

		// Attempt to get arena
		try {
			arena = GameManager.getArena(player);
		} catch (Exception err) {
			return;
		}

		// Cancel event if arena is in waiting mode
		if (arena.getStatus() == ArenaStatus.WAITING)
			e.setCancelled(true);
	}
}
