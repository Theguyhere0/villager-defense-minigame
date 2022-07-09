package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaException;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.VDMobNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.items.ItemMetaKey;
import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.game.models.items.eggs.VDEgg;
import me.theguyhere.villagerdefense.plugin.game.models.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.game.models.items.menuItems.*;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.VDWeapon;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.game.models.players.AttackClass;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameListener implements Listener {
	// Keep score and manage mob death
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		LivingEntity ent = e.getEntity();

		// Check for arena mobs
		if (!ent.hasMetadata(VDMob.VD))
			return;

		Arena arena;
		VDMob mob;
		try {
			arena = GameManager.getArena(ent.getMetadata(VDMob.VD).get(0).asInt());
			mob = arena.getMob(ent.getUniqueId());
		} catch (ArenaNotFoundException | VDMobNotFoundException err) {
			return;
		}

		// Check for right game
		if (mob.getGameID() != arena.getGameID())
			return;

		// Arena enemies not part of an active arena
		if (arena.getStatus() != ArenaStatus.ACTIVE) {
			e.getDrops().clear();
			return;
		}

		// Check for right wave
		if (mob.getWave() != arena.getCurrentWave())
			return;

		// Clear normal drops
		e.getDrops().clear();
		e.setDroppedExp(0);

        // Get spawn table
        DataManager data = new DataManager("spawnTables/" + arena.getSpawnTableFile());

		if (Main.getVillagersTeam().hasEntry(ent.getUniqueId().toString())) {
			// Handle pet death TODO
			if (ent instanceof Wolf) {
				try {
					arena.getPlayer((Player) ((Wolf) ent).getOwner()).decrementWolves();
				} catch (Exception err) {
					return;
				}
			}

			// Handle golem death TODO
			else if (ent instanceof IronGolem)
				arena.decrementGolems();
		}

		// Handle enemy death
		else if (Main.getMonstersTeam().hasEntry(ent.getUniqueId().toString())) {
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

		// Remove the mob
		mob.remove();
		arena.removeMob(mob.getID());

		// Update scoreboards
		arena.updateScoreboards();
	}

	// Stop automatic game mode switching between worlds
	@EventHandler
	public void onGameModeSwitch(PlayerGameModeChangeEvent e) {
		if (GameManager.checkPlayer(e.getPlayer()) && e.getNewGameMode() == GameMode.SURVIVAL)
			e.setCancelled(true);
	}

	// Manage projectiles being fired
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		Projectile projectile = e.getEntity();
		Entity source = (Entity) projectile.getShooter();
		if (!(source instanceof LivingEntity))
			return;
		LivingEntity shooter = (LivingEntity) source;
		Arena arena;
		VDMob finalShooter;

		// Player shot
		if (shooter instanceof Player) {
			Player player = (Player) shooter;
			VDPlayer gamer;

			// Attempt to get VDPlayer and VDMob
			try {
				arena = GameManager.getArena(player);
				gamer = arena.getPlayer(player);
			} catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Check for arrow
			if (!(projectile instanceof Arrow))
				return;

			// Encode damage information
			projectile.setMetadata(ItemMetaKey.DAMAGE.name(),
					new FixedMetadataValue(Main.plugin, gamer.dealRawDamage(AttackClass.RANGE, 0)));
			projectile.setMetadata(ItemMetaKey.PER_BLOCK.name(),
					new FixedMetadataValue(Main.plugin, true));
			projectile.setMetadata(ItemMetaKey.ORIGIN_LOCATION.name(),
					new FixedMetadataValue(Main.plugin, player.getLocation()));

			// Don't allow pickup
			((Arrow) projectile).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		}

		// Mob shot
		// TODO
	}

	// Update health when damage is dealt by entity
	@EventHandler
	public void onHurt(EntityDamageByEntityEvent e) {
		boolean projectile = e.getDamager() instanceof Projectile;
		Entity interimVictim = e.getEntity();
		Entity interimDamager = projectile ? (Entity) ((Projectile) e.getDamager()).getShooter() : e.getDamager();
		if (!(interimVictim instanceof LivingEntity))
			return;
		if (!(interimDamager instanceof LivingEntity))
			return;
		LivingEntity victim = (LivingEntity) interimVictim;
		LivingEntity damager = (LivingEntity) interimDamager;
		Arena arena;
		Player player = null;
		VDPlayer gamer = null;
		VDMob finalVictim;
		VDMob finalDamager = null;

		// Player getting hurt
		if (victim instanceof Player) {
			player = (Player) victim;

			// Attempt to get VDPlayer and VDMob
			try {
				arena = GameManager.getArena(player);
				gamer = arena.getPlayer(player);
				finalDamager = arena.getMob(damager.getUniqueId());
			} catch (ArenaNotFoundException | PlayerNotFoundException | VDMobNotFoundException err) {
				return;
			}

			// Avoid phantom damage effects
			if (Main.getVillagersTeam().hasEntry(damager.getUniqueId().toString()))
				return;

			// Cancel original damage
			e.setDamage(0);

			// Check damage cooldown
			if (!finalDamager.checkCooldown()) {
				e.setCancelled(true);
				return;
			}

			if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
				// Make sure fast attacks only apply when mobs are close
				if (damager.getLocation().distance(victim.getLocation()) > 1.75) {
					e.setCancelled(true);
					return;
				}

				// Make hurt sound if custom
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
			}

			// Implement faster attacks
			if (finalDamager.getAttackSpeed() == .4 && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM)
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
						.callEvent(new EntityDamageByEntityEvent(damager, victim,
						EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.45));
			else if (finalDamager.getAttackSpeed() == .2 && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
						.callEvent(new EntityDamageByEntityEvent(damager, victim,
						EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.25));
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
						.callEvent(new EntityDamageByEntityEvent(damager, victim,
						EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.5));
			}

			// Realize damage
			gamer.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType());
		}

		// VD entity getting hurt
		else if (victim.hasMetadata(VDMob.VD)) {
			// Check for player damager, then get player
			if (damager instanceof Player)
				player = (Player) damager;

			// Attempt to get VDPlayer and VDMobs
			if (player != null) {
				try {
					arena = GameManager.getArena(player);
					gamer = arena.getPlayer(player);
					finalVictim = arena.getMob(victim.getUniqueId());
				} catch (ArenaNotFoundException | PlayerNotFoundException | VDMobNotFoundException err) {
					return;
				}
			} else {
				try {
					arena = GameManager.getArena(victim.getMetadata(VDMob.VD).get(0).asInt());
					finalDamager = arena.getMob(damager.getUniqueId());
					finalVictim = arena.getMob(victim.getUniqueId());
				} catch (ArenaNotFoundException | VDMobNotFoundException err) {
					return;
				}
			}

			// Enemy getting hurt
			if (Main.getMonstersTeam().hasEntry(victim.getUniqueId().toString())) {
				// Avoid phantom damage effects
				if (Main.getMonstersTeam().hasEntry(damager.getUniqueId().toString()))
					return;

				// Check for pacifist challenge and not an enemy
				if (gamer != null && gamer.getChallenges().contains(Challenge.pacifist()) &&
						!gamer.getEnemies().contains(damager.getUniqueId()))
					return;

				// Cancel and capture original damage
				double damage = e.getDamage();
				e.setDamage(0);

				// Damage dealt by player
				if (gamer != null) {
					AttackClass attackClass;

					// Range damage
					if (projectile) {
						if (e.getDamager().getMetadata(ItemMetaKey.PER_BLOCK.name()).get(0).asBoolean())
							finalVictim.takeDamage(
									(int) (e.getDamager().getMetadata(ItemMetaKey.DAMAGE.name())
											.get(0).asInt()
											* victim.getLocation().distance((Location)
											Objects.requireNonNull(e.getDamager().getMetadata(
													ItemMetaKey.ORIGIN_LOCATION.name()).get(0).value())))
											+ gamer.getBaseDamage(),
									AttackType.NORMAL,
									player,
									arena
							);
						return;
					}

					// Crit damage
					if (damage > 1)
						attackClass = AttackClass.CRITICAL;

					// Sweep damage
					else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
						attackClass = AttackClass.SWEEP;

					// Main damage
					else attackClass = AttackClass.MAIN;

					// Play out damage
					finalVictim.takeDamage(gamer.dealRawDamage(attackClass, damage), gamer.getAttackType(), player,
							arena);
				}

				// Damage not dealt by player
				else {
					// Check damage cooldown
					if (finalDamager.checkCooldown()) {
						e.setCancelled(true);
						return;
					}

					// Make sure fast attacks only apply when mobs are close
					if (damager.getLocation().distance(victim.getLocation()) > 1.75) {
						e.setCancelled(true);
						return;
					}

					// Implement faster attacks
					if (finalDamager.getAttackSpeed() == .4 && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM)
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
								.callEvent(new EntityDamageByEntityEvent(damager, victim,
								EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.45));
					else if (finalDamager.getAttackSpeed() == .2 &&
							e.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
								.callEvent(new EntityDamageByEntityEvent(damager, victim,
								EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.25));
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
								.callEvent(new EntityDamageByEntityEvent(damager, victim,
								EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.5));
					}

					// Play out damage
					finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(), null,
							arena);
				}
			}

			// Friendly getting hurt
			if (Main.getVillagersTeam().hasEntry(victim.getUniqueId().toString())) {
				// Avoid phantom damage effects
				if (Main.getVillagersTeam().hasEntry(damager.getUniqueId().toString()))
					return;
				if (finalDamager == null)
					return;

				// Cancel original damage
				e.setDamage(0);

				// Check damage cooldown
				if (finalDamager.checkCooldown()) {
					e.setCancelled(true);
					return;
				}

				// Implement faster attacks
				if (finalDamager.getAttackSpeed() == .4 && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM)
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
							.callEvent(new EntityDamageByEntityEvent(damager, victim,
									EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.45));
				else if (finalDamager.getAttackSpeed() == .2 &&
						e.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
							.callEvent(new EntityDamageByEntityEvent(damager, victim,
									EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.25));
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Bukkit.getPluginManager()
							.callEvent(new EntityDamageByEntityEvent(damager, victim,
									EntityDamageEvent.DamageCause.CUSTOM, 0)), Utils.secondsToTicks(.5));
				}

				// Play out damage
				finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(), null,
						arena);
			}
		}
	}

	// Ignore damage not dealt by another entity
	@EventHandler
	public void onHurt(EntityDamageEvent e) {
		Entity ent = e.getEntity();

		// Check for arena mobs or players
		if (!ent.hasMetadata(VDMob.VD) && !(ent instanceof Player))
			return;

		// Check player is in a game
		if (ent instanceof Player && !GameManager.checkPlayer((Player) ent))
			return;

		// Don't handle entity on entity damage
		if (e instanceof EntityDamageByEntityEvent)
			return;

		// Cancel
		e.setCancelled(true);
	}

	// Prevent using certain item slots
	@EventHandler
	public void onIllegalEquip(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Arena arena;

		// Attempt to get arena
		try {
			arena = GameManager.getArena(player);
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Ignore arenas that aren't started
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			return;

		// Get off hand
		ItemStack off = player.getInventory().getItemInOffHand();

		// Unequip weapons in off-hand
		if (VDWeapon.matchesNoAmmo(off)) {
			PlayerManager.giveItem(player, off, LanguageManager.errors.inventoryFull);
			player.getInventory().setItemInOffHand(null);
			PlayerManager.notifyFailure(player, LanguageManager.errors.offWeapon);
		}
	}

	// Handle player level up
	@EventHandler
	public void onLevelUp(PlayerLevelChangeEvent e) {
		Player player = e.getPlayer();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
		} catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Ignore if arena isn't active
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			return;

		// Increase health and possibly damage
		gamer.setMaxHealth(gamer.getMaxHealth() + 5);
		if (player.getLevel() % 4 == 0) {
			gamer.setBaseDamage(gamer.getBaseDamage() + 1);
			PlayerManager.notifySuccess(player, LanguageManager.messages.levelUp,
					new ColoredMessage(ChatColor.RED, "+5" + Utils.HP + "  +1" + Utils.DAMAGE));
		} else PlayerManager.notifySuccess(player, LanguageManager.messages.levelUp,
				new ColoredMessage(ChatColor.RED, "+5" + Utils.HP));
	}

	// Prevent players from going hungry while waiting for an arena to start
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		Player player = (Player) e.getEntity();

		// Check for player in arena
		try {
			GameManager.getArena(player);
		} catch (ArenaNotFoundException err) {
			return;
		}

		e.setCancelled(true);
	}

	// Handle healing
	@EventHandler
	public void onHeal(EntityRegainHealthEvent e) {
		Entity ent = e.getEntity();
		Player player;
		Arena arena;

		// Check for player
		if (e.getEntity() instanceof Player) {
			player = (Player) ent;

			// Check player is in an arena
			try {
				arena = GameManager.getArena(player);
			} catch (ArenaNotFoundException err) {
				return;
			}

			// Ignore arenas that aren't started
			if (arena.getStatus() != ArenaStatus.ACTIVE)
				return;

			// Negate natural health regain and manage saturation
			if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
					e.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING ||
					e.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)
				e.setCancelled(true);
		}


//		TODO
//		// Check for arena enemies
//		if (!ent.hasMetadata(VDMob.VD))
//			return;
//
//		// Ignore wolves and players
//		if (ent instanceof Wolf || ent instanceof Player)
//			return;
//
//		// Ignore bosses
//		if (ent instanceof Wither)
//			return;
//
//		ent.setCustomName(Mobs.formattedName((LivingEntity) ent));
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
		} catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Get item in hand
		ItemStack item;
		if (e.getHand() == EquipmentSlot.OFF_HAND) {
			item = Objects.requireNonNull(player.getEquipment()).getItemInOffHand();

			// Check for other clickables in main hand
			if (VDAbility.matches(item) || VDFood.matches(item) || VDArmor.matches(item) ||
					VDWeapon.matchesClickableWeapon(item) || VDEgg.matches(item))
				return;
		}
		else item = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();

		// Open shop inventory
		if (Shop.matches(item))
			player.openInventory(Inventories.createShopMenu(arena.getCurrentWave() / 10 + 1, arena));

		// Open kit selection menu
		else if (KitSelector.matches(item))
			player.openInventory(Inventories.createSelectKitsMenu(player, arena));

		// Open challenge selection menu
		else if (ChallengeSelector.matches(item))
			player.openInventory(Inventories.createSelectChallengesMenu(gamer, arena));

		// Toggle boost
		else if (BoostToggle.matches(item)) {
			gamer.toggleBoost();
			PlayerManager.giveChoiceItems(gamer);
		}

		// Toggle share
		else if (ShareToggle.matches(item)) {
			gamer.toggleShare();
			PlayerManager.giveChoiceItems(gamer);
		}

		// Open crystal convert menu
		else if (CrystalConverter.matches(item))
			player.openInventory(Inventories.createCrystalConvertMenu(gamer));

		// Make player leave
		else if (Leave.matches(item))
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
					Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));

		// Ignore
		else return;

		// Cancel interaction
		e.setCancelled(true);
	}

	// Stops players from hurting villagers and other players, and monsters from hurting each other
	// TODO: check if this is still needed
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
		if (!ent.hasMetadata(VDMob.VD) && !(ent instanceof Player))
			return;

		// Cancel damage to friendly mobs
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
		} catch (ArenaNotFoundException | PlayerNotFoundException err) {
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

			// Check for explosive challenge
			if (gamer.getChallenges().contains(Challenge.explosive()))
				player.getInventory().clear();

			// Notify player of their own death
			player.sendTitle(
					new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.death1).toString(),
					new ColoredMessage(ChatColor.RED, LanguageManager.messages.death2).toString(),
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
			arena.updateScoreboards();

			// Check for game end condition
			if (arena.getAlive() == 0)
				try {
					arena.endGame();
				} catch (ArenaException ignored) {
				}
		}
	}

	// Stops slimes and magma cubes from splitting on death
	@EventHandler
	public void onSplit(SlimeSplitEvent e) {
		Entity ent = e.getEntity();
		if (!ent.hasMetadata(VDMob.VD))
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
		if (ent.hasMetadata(VDMob.VD))
			e.setCancelled(true);
	}

	// Stop spawning babies
	@EventHandler
	public void onBabyAttempt(PlayerInteractEntityEvent e) {
		// Check for player in game
		try {
			GameManager.getArena(e.getPlayer());
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Check for wolf
		if (!(e.getRightClicked() instanceof Wolf))
			return;

		e.setCancelled(true);
	}

	// Manage usage of consumables
	@EventHandler
	public void onConsume(PlayerInteractEvent e) {
		// Check for right click
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player player = e.getPlayer();
		ItemStack item = e.getItem() == null ? new ItemStack(Material.AIR) : e.getItem();
		ItemStack main = player.getInventory().getItemInMainHand();
		List<String> lores;
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena, player, and lore
		try {
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
			lores = Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore());
		} catch (ArenaNotFoundException | PlayerNotFoundException | NullPointerException err) {
			return;
		}

		// Check for active arena, at least wave 1
		if (arena.getStatus() != ArenaStatus.ACTIVE || arena.getCurrentWave() < 1) {
			e.setCancelled(true);
			return;
		}

		// Avoid false consume
		if (e.getHand() == EquipmentSlot.OFF_HAND &&
				(Shop.matches(main) || VDAbility.matches(main) || VDFood.matches(main) || VDArmor.matches(main) ||
						VDWeapon.matchesClickableWeapon(main) || VDEgg.matches(main)))
			return;

		// Give health
		AtomicBoolean food = new AtomicBoolean(false);

		lores.forEach(lore -> {
			if (lore.contains(ChatColor.RED.toString()) && lore.contains(Utils.HP) && !gamer.hasMaxHealth()) {
				gamer.changeCurrentHealth(Integer.parseInt(lore.substring(3).replace(Utils.HP, "").trim()));
				food.set(true);
			}
			if (lore.contains(ChatColor.GOLD.toString()) && lore.contains(Utils.HP)) {
				gamer.addAbsorption(Integer.parseInt(lore.substring(3).replace(Utils.HP, "").trim()));
				food.set(true);
			}
		});

		// Consume
		if (food.get()) {
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);
		}

		// Wolf spawn
//		if (item.getType() == Material.WOLF_SPAWN_EGG &&
//				!(main.getType() == Material.WOLF_SPAWN_EGG && e.getHand() == EquipmentSlot.OFF_HAND) &&
//				main.getType() != Material.POLAR_BEAR_SPAWN_EGG) {
//			// Ignore if it wasn't a right click on a block
//			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
//				return;
//
//			// Cancel normal spawn
//			e.setCancelled(true);
//
//			// Check for wolf cap
//			if (gamer.getWolves() >= arena.getWolfCap()) {
//				PlayerManager.notifyFailure(player, LanguageManager.errors.wolf,
//						new ColoredMessage(ChatColor.AQUA, Integer.toString(arena.getWolfCap())));
//				return;
//			}
//
//			// Remove an item
//			if (item.getAmount() > 1)
//				item.setAmount(item.getAmount() - 1);
//			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);
//
//			Location location = Objects.requireNonNull(e.getClickedBlock()).getLocation();
//			location.setY(location.getY() + 1);
//
//			// Spawn and tame the wolf
//			Wolf wolf = (Wolf) player.getWorld().spawnEntity(location, EntityType.WOLF);
//			Mobs.setWolf(Main.plugin, arena, gamer, wolf);
//			Main.getVillagersTeam().addEntry(wolf.getUniqueId().toString());
//			return;
//		}

		// Iron golem spawn
//		if (item.getItemMeta().getDisplayName().contains("Iron Golem Spawn Egg") &&
//				!(main.getType() == Material.POLAR_BEAR_SPAWN_EGG && e.getHand() == EquipmentSlot.OFF_HAND) &&
//				main.getType() != Material.WOLF_SPAWN_EGG) {
//			// Ignore if it wasn't a right click on a block
//			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
//				return;
//
//			// Cancel normal spawn
//			e.setCancelled(true);
//
//			// Check for golem cap
//			if (arena.getGolems() >= arena.getGolemCap()) {
//				PlayerManager.notifyFailure(player, LanguageManager.errors.golem,
//						new ColoredMessage(ChatColor.AQUA, Integer.toString(arena.getGolemCap())));
//				return;
//			}
//
//			// Remove an item
//			if (item.getAmount() > 1)
//				item.setAmount(item.getAmount() - 1);
//			else player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);
//
//			Location location = Objects.requireNonNull(e.getClickedBlock()).getLocation();
//			location.setY(location.getY() + 1);
//
//			// Spawn iron golem
//			IronGolem ironGolem = (IronGolem) player.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
//			Mobs.setGolem(Main.plugin, arena, ironGolem);
//			Main.getVillagersTeam().addEntry(ironGolem.getUniqueId().toString());
//		}
	}

	// Prevent wolves from targeting villagers
	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e) {
//		// Check for wolf
//		if (!(e.getEntity() instanceof Wolf))
//			return;
//
//		// Check for villager target
//		if (!(e.getTarget() instanceof Villager))
//			return;
//
//		// Cancel if special wolf
//		if (e.getEntity().hasMetadata(VDMob.VD))
//			e.setCancelled(true);
	}

	// Prevent wolves from teleporting
	@EventHandler
	public void onTeleport(EntityTeleportEvent e) {
		Entity ent = e.getEntity();

		// Check for wolf
		if (!(ent instanceof Wolf))
			return;

		// Check for special mob
		if (!ent.hasMetadata(VDMob.VD))
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
		} catch (ArenaNotFoundException err) {
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
			PlayerManager.notifyFailure(player, LanguageManager.errors.teleport,
					new ColoredMessage(ChatColor.AQUA, "/vd leave"));
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
		} catch (ArenaNotFoundException | PlayerNotFoundException err) {
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
		if (!ent.hasMetadata(VDMob.VD))
			return;

		e.setCancelled(true);
	}

	// Prevent zombies from breaking doors
	@EventHandler
	public void onBreakDoor(EntityBreakDoorEvent e) {
		Entity ent = e.getEntity();

		// Check for special mob
		if (!ent.hasMetadata(VDMob.VD))
			return;

		e.setCancelled(true);
	}

	// Prevent players from dropping standard game items
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();

		// Check if player is in an arena
		if (!GameManager.checkPlayer(player))
			return;

		// Check for menu items
		if (VDMenuItem.matches(e.getItemDrop().getItemStack()))
			e.setCancelled(true);
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
		if (Shop.matches(main) || VDAbility.matches(main) || VDFood.matches(main) || VDArmor.matches(main) ||
				VDWeapon.matchesClickableWeapon(main) || VDEgg.matches(main))
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
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel event if arena is in waiting mode
		if (arena.getStatus() == ArenaStatus.WAITING)
			e.setCancelled(true);
	}

	// Prevent swapping items while waiting for game to start
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent e) {
		Player player = e.getPlayer();
		Arena arena;

		// Attempt to get arena
		try {
			arena = GameManager.getArena(player);
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel event if arena is in waiting mode
		if (arena.getStatus() == ArenaStatus.WAITING)
			e.setCancelled(true);
	}
}
