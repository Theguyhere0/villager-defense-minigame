package me.theguyhere.villagerdefense.plugin.listeners;

import com.google.common.util.concurrent.AtomicDouble;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaException;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.VDMobNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.items.ItemMetaKey;
import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.MageAbility;
import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.game.models.items.food.ShopFood;
import me.theguyhere.villagerdefense.plugin.game.models.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.game.models.items.menuItems.*;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.Ammo;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.Bow;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.Crossbow;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.VDWeapon;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.Team;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.minions.VDCreeper;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.minions.VDWitch;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.pets.VDPet;
import me.theguyhere.villagerdefense.plugin.game.models.players.AttackClass;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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

		// Clear normal drops
		e.getDrops().clear();
		e.setDroppedExp(0);

		// Remove the mob
		arena.removeMob(mob.getID());

		if (ent.getMetadata(VDMob.TEAM).get(0).equals(Team.VILLAGER.getValue())) {
			// Update scoreboards
			arena.updateScoreboards();
		}

		// Handle enemy death
		else if (ent.getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue())) {
			// Check for right wave
			if (mob.getWave() != arena.getCurrentWave())
				return;

			// Update scoreboards
			arena.updateScoreboards();

			// Set monsters glowing when only 20% remain
			if (arena.getEnemies() <= .2 * arena.getMaxEnemies() && !arena.isSpawningMonsters() &&
					arena.getEnemies() > 0)
				arena.setMonsterGlow();
		}
	}

	// Stop automatic game mode switching between worlds
	@EventHandler
	public void onGameModeSwitch(PlayerGameModeChangeEvent e) {
		if (GameManager.checkPlayer(e.getPlayer()) && e.getNewGameMode() == GameMode.SURVIVAL)
			e.setCancelled(true);
	}

	// Handle usage of ranged weapons
	@EventHandler
	public void onRange(PlayerInteractEvent e) {
		// Check for right click
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		// Check for main hand
		if (e.getHand() != EquipmentSlot.HAND)
			return;

		Player player = e.getPlayer();
		VDPlayer gamer;

		// Attempt to get VDPlayer
		try {
			gamer = GameManager.getArena(player).getPlayer(player);
		} catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Check for ammo weapon
		ItemStack range = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
		if (!VDWeapon.matchesAmmoWeapon(range))
			return;

		// Check for ammo
		ItemStack ammo = Objects.requireNonNull(player.getEquipment()).getItemInOffHand();
		if (!Ammo.matches(ammo)) {
			e.setCancelled(true);
			gamer.triggerAmmoWarningCooldown();
			return;
		}

		// Get data
		AtomicInteger cost = new AtomicInteger();
		AtomicInteger capacity = new AtomicInteger();
		AtomicDouble cooldown = new AtomicDouble();
		Objects.requireNonNull(Objects.requireNonNull(range.getItemMeta()).getLore()).forEach(lore -> {
			if (lore.contains(LanguageManager.messages.ammoCost
					.replace("%s", ""))) {
				cost.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.ammoCost.length())
						.replace(ChatColor.BLUE.toString(), "")));
			}
			if (lore.contains(LanguageManager.messages.attackSpeed
					.replace("%s", ""))) {
				cooldown.set(1 / Double.parseDouble(lore.substring(2 + LanguageManager.messages.attackSpeed.length())
						.replace(ChatColor.BLUE.toString(), "")));
			}
		});
		List<String > lores = Objects.requireNonNull(Objects.requireNonNull(ammo.getItemMeta()).getLore());
		lores.forEach(lore -> {
			if (lore.contains(LanguageManager.messages.capacity
					.replace("%s", ""))) {
				capacity.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.capacity.length())
						.replace(ChatColor.BLUE.toString(), "")
						.replace(ChatColor.WHITE.toString(), "")
						.split(" / ")[0]));
			}
		});
		if (capacity.get() < cost.get())
			return;

		// Check for cooldown
		if (gamer.remainingWeaponCooldown() > 0)
			return;

		// Fire
		player.launchProjectile(Arrow.class);

		// Update capacity, durability, and cooldown
		if (Bow.matches(range))
			NMSVersion.getCurrent().getNmsManager().setBowCooldown(player, Utils.secondsToTicks(cooldown.get()));
		else NMSVersion.getCurrent().getNmsManager().setCrossbowCooldown(player, Utils.secondsToTicks(cooldown.get()));
		gamer.triggerWeaponCooldown(Utils.secondsToMillis(cooldown.get()));
		if (Ammo.updateCapacity(ammo, -cost.get())) {
			player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
		}
		Bukkit.getPluginManager().callEvent(new PlayerItemDamageEvent(player, range, 1));
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

			// Attempt to get VDPlayer
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
			ItemStack range = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();
			projectile.setMetadata(ItemMetaKey.DAMAGE.name(),
					new FixedMetadataValue(Main.plugin, gamer.dealRawDamage(AttackClass.RANGE, 0)));
			if (Objects.requireNonNull(Objects.requireNonNull(range.getItemMeta()).getLore()).stream().anyMatch(lore ->
					lore.contains(LanguageManager.messages.perBlock.replace("%s", "")))) {
				projectile.setMetadata(ItemMetaKey.PER_BLOCK.name(),
						new FixedMetadataValue(Main.plugin, true));
				projectile.setMetadata(ItemMetaKey.ORIGIN_LOCATION.name(),
						new FixedMetadataValue(Main.plugin, player.getLocation()));
			} else projectile.setMetadata(ItemMetaKey.PER_BLOCK.name(),
					new FixedMetadataValue(Main.plugin, false));
			if (Crossbow.matches(range))
				((Arrow) projectile).setPierceLevel(Crossbow.getPierce(range));

			// Don't allow pickup
			((Arrow) projectile).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		}

		// Mob shot
		else {
			// Attempt to get VDMob
			try {
				arena = GameManager.getArena(shooter.getMetadata(VDMob.VD).get(0).asInt());
				finalShooter = arena.getMob(shooter.getUniqueId());
			} catch (ArenaNotFoundException | VDMobNotFoundException | IndexOutOfBoundsException err) {
				return;
			}

			// Check for witch
			if ((finalShooter instanceof VDWitch)) {
				if (((ThrownPotion) e.getEntity()).getEffects().size() > 0)
					e.setCancelled(true);

				// Check for cooldown
				if (!finalShooter.attackAttempt())
					e.setCancelled(true);

				return;
			}

			// Handle pierce
			if (finalShooter.getPierce() > 0)
				((Arrow) projectile).setPierceLevel(finalShooter.getPierce());
		}
	}

	// Update health when damage is dealt by entity and prevent friendly fire
	@EventHandler
	public void onHurt(EntityDamageByEntityEvent e) {
		boolean projectile = e.getDamager() instanceof Projectile;
		Entity interimDamager = projectile ? (Entity) ((Projectile) e.getDamager()).getShooter() : e.getDamager();
		if (!(e.getEntity() instanceof LivingEntity))
			return;
		if (!(interimDamager instanceof LivingEntity))
			return;
		LivingEntity victim = (LivingEntity) e.getEntity();
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
			} catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			} catch (VDMobNotFoundException err) {
				e.setCancelled(true);
				return;
			}

			// Avoid phantom damage effects and friendly fire
			if (damager instanceof Player || damager.getMetadata(VDMob.TEAM).get(0).equals(Team.VILLAGER.getValue())) {
				e.setCancelled(true);
				return;
			}

			// Cancel original damage
			e.setDamage(0);

			// Check damage cooldown
			if (!finalDamager.attackAttempt()) {
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

			// Realize damage and deal effect
			gamer.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType());
			if (finalDamager.getEffectType() == null || (Kit.witch().getID().equals(gamer.getKit().getID())) &&
					!gamer.isSharing())
				return;
			Random r = new Random();
			if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.WITCH))) {
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
				return;
			}
			if (finalDamager.getEffectType().getName().equals(PotionEffectType.FIRE_RESISTANCE.getName()))
				gamer.combust(finalDamager.getEffectDuration());
			else gamer.getPlayer().addPotionEffect(finalDamager.dealEffect());
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
					finalVictim = arena.getMob(victim.getUniqueId());
				} catch (ArenaNotFoundException | VDMobNotFoundException err) {
					return;
				}
				try {
					finalDamager = arena.getMob(damager.getUniqueId());
				} catch (VDMobNotFoundException err) {
					e.setCancelled(true);
					return;
				}
			}

			// Enemy getting hurt
			if (victim.getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue())) {
				// Avoid phantom damage effects and friendly fire
				if (!(damager instanceof Player) &&
						damager.getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue())) {
					e.setCancelled(true);
					return;
				}

				// Cancel and capture original damage
				double damage = e.getDamage();
				e.setDamage(0);

				// Check for pacifist challenge and not an enemy
				if (gamer != null && gamer.getChallenges().contains(Challenge.pacifist()) &&
						!gamer.getEnemies().contains(victim.getUniqueId()))
					return;

				// Damage dealt by player
				if (gamer != null) {
					AttackClass attackClass;

					// Calculate damage difference
					AtomicInteger dif = new AtomicInteger();
					player.getActivePotionEffects().forEach(potionEffect -> {
						if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
							dif.addAndGet((1 + potionEffect.getAmplifier()) * 3);
						else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
							dif.addAndGet(- (1 + potionEffect.getAmplifier()) * 4);
					});

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
						else finalVictim.takeDamage(
								e.getDamager().getMetadata(ItemMetaKey.DAMAGE.name()).get(0).asInt() +
										gamer.getBaseDamage(),
								AttackType.NORMAL,
								player,
								arena
						);
						return;
					}

					// Crit damage
					if (damage > 20 + dif.get())
						attackClass = AttackClass.CRITICAL;

					// Sweep damage
					else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
						attackClass = AttackClass.SWEEP;

					// Main damage
					else attackClass = AttackClass.MAIN;

					// Play out damage
					int hurt = finalVictim.takeDamage(
							gamer.dealRawDamage(attackClass, damage / (double) (dif.get() + 20)),
							gamer.getAttackType(), player, arena);

					Random r = new Random();

					// Check for vampire kit
					if (Kit.vampire().getID().equals(gamer.getKit().getID()) && !gamer.isSharing()) {
						// Heal if probability is right
						if (r.nextDouble() < .2)
							gamer.changeCurrentHealth((int) (hurt * .25));
					}

					// Check for shared vampire effect
					else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.VAMPIRE))) {
						// Heal if probability is right
						if (r.nextDouble() < .2) {
							gamer.changeCurrentHealth((int) (hurt * .25));
							PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
						}
					}
				}

				// Damage not dealt by player
				else {
					// Check damage cooldown
					if (!finalDamager.attackAttempt()) {
						e.setCancelled(true);
						return;
					}

					if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
						// Make sure fast attacks only apply when mobs are close
						if (damager.getLocation().distance(victim.getLocation()) > 1.75) {
							e.setCancelled(true);
							return;
						}
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

					// Check for pet
					if (finalDamager instanceof VDPet)
						finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(),
								((VDPet) finalDamager).getOwner().getPlayer(), arena);

					// Play out damage and effect
					else finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(),
							null, arena);
					if (finalDamager.dealEffect() != null)
						finalVictim.getEntity().addPotionEffect(finalDamager.dealEffect());
				}
			}

			// Friendly getting hurt
			if (victim.getMetadata(VDMob.TEAM).get(0).equals(Team.VILLAGER.getValue())) {
				// Avoid phantom damage effects and friendly fire
				if (damager instanceof Player ||
						damager.getMetadata(VDMob.TEAM).get(0).equals(Team.VILLAGER.getValue())) {
					e.setCancelled(true);
					return;
				}
				if (finalDamager == null)
					return;

				// Cancel original damage
				e.setDamage(0);

				// Check damage cooldown
				if (!finalDamager.attackAttempt()) {
					e.setCancelled(true);
					return;
				}

				if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
					// Make sure fast attacks only apply when mobs are close
					if (damager.getLocation().distance(victim.getLocation()) > 1.75) {
						e.setCancelled(true);
						return;
					}
				}

				// Check for no damage
				if (finalDamager.getAttackType() == AttackType.NONE) {
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

				// Play out damage and effect
				finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(), null,
						arena);
				if (finalDamager.dealEffect() != null)
					finalVictim.getEntity().addPotionEffect(finalDamager.dealEffect());
			}
		}
	}

	// Handle other types of damage from effects and environment
	@EventHandler
	public void onHurt(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		Arena arena;
		VDMob mob;
		VDPlayer gamer;

		// Don't handle entity on entity damage
		if (e instanceof EntityDamageByEntityEvent)
			return;

		// Capture original damage and cause
		double damage = e.getDamage();
		EntityDamageEvent.DamageCause damageCause = e.getCause();

		// For players
		if (ent instanceof Player) {
			Player player = (Player) ent;

			// Attempt to get arena and VDPlayer
			try {
				arena = GameManager.getArena(player);
				gamer = arena.getPlayer(player);
			} catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Custom damage handling
			switch (damageCause) {
				// Environmental damage, not meant for customization
				case DROWNING:
				case SUFFOCATION:
					gamer.takeDamage((int) (damage * 25), AttackType.DIRECT);
					break;
				case LAVA:
				case HOT_FLOOR:
				case LIGHTNING:
					gamer.takeDamage((int) (damage * 40), AttackType.PENETRATING);
					break;
				case FALLING_BLOCK:
				case FALL:
				case BLOCK_EXPLOSION:
					gamer.takeDamage((int) (damage * 25), AttackType.CRUSHING);
					break;
				// Custom handling
				case FIRE:
				case FIRE_TICK:
					gamer.takeDamage((int) (damage * 50), AttackType.NORMAL);
					break;
				case POISON:
					gamer.takeDamage((int) (damage * 25), AttackType.PENETRATING);
					break;
				case WITHER:
					gamer.takeDamage((int) (damage * 20), AttackType.DIRECT);
					break;
				// Silence
				default:
					e.setCancelled(true);
			}
		}

		// For mobs
		else {
			// Try to get arena and VDMob
			try {
				arena = GameManager.getArena(ent.getMetadata(VDMob.VD).get(0).asInt());
				mob = arena.getMob(ent.getUniqueId());
			} catch (ArenaNotFoundException | VDMobNotFoundException | IndexOutOfBoundsException err) {
				return;
			}

			// Custom damage handling
			switch (damageCause) {
				// Environmental damage, not meant for customization
				case DROWNING:
				case SUFFOCATION:
					mob.takeDamage((int) (damage * 25), AttackType.DIRECT, null, arena);
					break;
				case LAVA:
				case HOT_FLOOR:
				case LIGHTNING:
					mob.takeDamage((int) (damage * 40), AttackType.PENETRATING, null, arena);
					break;
				case FALLING_BLOCK:
				case FALL:
				case BLOCK_EXPLOSION:
					mob.takeDamage((int) (damage * 25), AttackType.CRUSHING, null, arena);
					break;
				// Custom handling
				case FIRE:
				case FIRE_TICK:
					mob.takeDamage((int) (damage * 50), AttackType.NORMAL, null, arena);
					break;
				case POISON:
					mob.takeDamage((int) (damage * 25), AttackType.PENETRATING, null, arena);
					break;
				case WITHER:
					mob.takeDamage((int) (damage * 20), AttackType.DIRECT, null, arena);
					break;
				// Silence
				default:
					e.setCancelled(true);
			}
		}

		// Cancel original damage
		e.setDamage(0);
	}

	// Ensure proper override of targeting
	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e) {
		Entity entity = e.getEntity();
		LivingEntity target = e.getTarget();

		// Check for custom mobs and player
		if (target == null || !entity.hasMetadata(VDMob.VD) ||
				!(target instanceof Player) && !target.hasMetadata(VDMob.VD))
			return;

		Arena arena;
		VDMob mob;
		VDMob targeted;

		// Attempt to get VDPlayer and VDMob
		try {
			arena = GameManager.getArena(Objects.requireNonNull(e.getEntity()).getMetadata(VDMob.VD).get(0).asInt());
			mob = arena.getMob(e.getEntity().getUniqueId());
			if (target instanceof Player && !arena.hasPlayer((Player) target))
				return;
			else targeted = arena.getMob(target.getUniqueId());
		} catch (ArenaNotFoundException | VDMobNotFoundException err) {
			return;
		}

		// Monsters
		if (mob.getEntity().getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue()))
			if (targeted != null && targeted.getEntity().getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue()))
				e.setCancelled(true);

		// Villager team
		else if (mob.getEntity().getMetadata(VDMob.TEAM).get(0).equals(Team.VILLAGER.getValue()) && (targeted == null ||
					targeted.getEntity().getMetadata(VDMob.TEAM).get(0).equals(Team.VILLAGER.getValue())))
				e.setCancelled(true);
	}

	// Stop custom mobs from burning in the sun
	@EventHandler
	public void onCombust(EntityCombustEvent e) {
		if (e instanceof EntityCombustByBlockEvent || e instanceof EntityCombustByEntityEvent)
			return;

		if (e.getEntity().hasMetadata(VDMob.VD))
			e.setCancelled(true);
	}

	// Custom creeper explosion handler
	@EventHandler
	public void onExplode(ExplosionPrimeEvent e) {
		Entity ent = e.getEntity();
		Arena arena;
		VDMob mob;

		// Try to get arena and VDMob
		try {
			arena = GameManager.getArena(ent.getMetadata(VDMob.VD).get(0).asInt());
			mob = arena.getMob(ent.getUniqueId());
		} catch (ArenaNotFoundException | VDMobNotFoundException | IndexOutOfBoundsException err) {
			return;
		}

		// Check for creeper
		if (!(mob instanceof VDCreeper))
			return;

		// Cancel vanilla explosion
		e.setCancelled(true);

		// Create new explosion
		Objects.requireNonNull(ent.getLocation().getWorld()).createExplosion(ent.getLocation(), 2.5f, false,
				false, ent);

		// Create replacement creeper
		VDMob creeper = new VDCreeper((VDCreeper) mob, arena);
		arena.addMob(creeper);

		// Remove the old creeper
		arena.removeMob(ent.getUniqueId());
		ent.remove();
	}

	// Create custom potion effects from witch
	@EventHandler
	public void onSplash(PotionSplashEvent e) {
		ThrownPotion potion = e.getEntity();
		Entity ent = (Entity) potion.getShooter();
		Arena arena;
		VDWitch witch;

		// Try to get arena and VDMob
		try {
			arena = GameManager.getArena(Objects.requireNonNull(ent).getMetadata(VDMob.VD).get(0).asInt());
			witch = (VDWitch) arena.getMob(ent.getUniqueId());
		} catch (ArenaNotFoundException | VDMobNotFoundException | IndexOutOfBoundsException |
				NullPointerException | ClassCastException err) {
			return;
		}

		// Apply to relevant entities
		Random r = new Random();
		for (LivingEntity affectedEntity : e.getAffectedEntities()) {
			// Not monster
			if (!(affectedEntity instanceof Player) &&
					affectedEntity.getMetadata(VDMob.TEAM).get(0).equals(Team.MONSTER.getValue()))
				continue;

			// Ignore players with witch kit
			try {
				VDPlayer player = arena.getPlayer(affectedEntity.getUniqueId());
				if (Kit.witch().getID().equals(player.getKit().getID()) && !player.isSharing())
					continue;
				if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.WITCH))) {
					PlayerManager.notifySuccess(player.getPlayer(), LanguageManager.messages.effectShare);
					return;
				}
			} catch (PlayerNotFoundException ignored) {
			}

			// Apply affects
			affectedEntity.addPotionEffect(witch.dealEffect());
		}
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

		// Get offhand
		ItemStack off = player.getInventory().getItemInOffHand();

		// Unequip weapons and mage abilities in offhand
		if (VDWeapon.matchesNoAmmo(off) || MageAbility.matches(off)) {
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
		gamer.setMaxHealth(gamer.getMaxHealth() + 10);
		if (player.getLevel() % 5 == 0) {
			gamer.setBaseDamage(gamer.getBaseDamage() + 2);
			PlayerManager.notifySuccess(player, LanguageManager.messages.levelUp,
					new ColoredMessage(ChatColor.RED, "+10" + Utils.HP + "  +2" + Utils.DAMAGE));
		} else PlayerManager.notifySuccess(player, LanguageManager.messages.levelUp,
				new ColoredMessage(ChatColor.RED, "+10" + Utils.HP));
	}

	// Prevent players from going hungry while waiting in an arena
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		Player player = (Player) e.getEntity();
		Arena arena;

		// Check for player in arena
		try {
			arena = GameManager.getArena(player);
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel if arena is not active
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			e.setCancelled(true);
	}

	// Cancel natural potion effects for VDMobs or VDPlayers
	@EventHandler
	public void onNaturalEffect(EntityPotionEffectEvent e) {
		Entity ent = e.getEntity();

		// Check for VDPlayer
		if (ent instanceof Player) {
			if (!GameManager.checkPlayer((Player) ent))
				return;
		}

		// Check for VDMob
		else {
			try {
				GameManager.getArena(ent.getMetadata(VDMob.VD).get(0).asInt()).getMob(ent.getUniqueId());
			} catch (IndexOutOfBoundsException | VDMobNotFoundException | ArenaNotFoundException err) {
				return;
			}
		}

		// Allow plugin, command, and expiration causes
		if (e.getCause() == EntityPotionEffectEvent.Cause.PLUGIN ||
				e.getCause() == EntityPotionEffectEvent.Cause.COMMAND ||
				e.getCause() == EntityPotionEffectEvent.Cause.EXPIRATION ||
				e.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK ||
				e.getCause() == EntityPotionEffectEvent.Cause.POTION_SPLASH)
			return;

		// Cancel
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

		// Prevent natural regen for other mobs
		else {
			// Check for special mob
			if (!ent.hasMetadata(VDMob.VD))
				return;

			// Stop regen
			if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)
				e.setCancelled(true);
		}
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
			item = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();

			// Check for other clickables in main hand
			if (VDAbility.matches(item) || VDFood.matches(item) || VDArmor.matches(item) ||
					VDWeapon.matchesClickableWeapon(item))
				return;
		}
		else item = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();

		// Open shop inventory
		if (Shop.matches(item))
			player.openInventory(Inventories.createShopMenu(arena, gamer));

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
						fighter.getPlayer().playSound(arena.getPlayerSpawn().getLocation().clone()
										.add(0, -8, 0),
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

	// Stop spawning babies using spawn eggs
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

		// Ignore armor equip
		if (VDArmor.matches(item))
			return;

		// Check for active arena, at least wave 1
		if (arena.getStatus() != ArenaStatus.ACTIVE || arena.getCurrentWave() < 1) {
			e.setCancelled(true);
			return;
		}

		// Avoid false consume
		if (e.getHand() == EquipmentSlot.OFF_HAND &&
				(Shop.matches(main) || VDAbility.matches(main) || VDFood.matches(main) || VDArmor.matches(main) ||
						VDWeapon.matchesClickableWeapon(main)))
			return;

		// Give health and hunger for totem
		if (ShopFood.matches(item) && item.getType() == Material.TOTEM_OF_UNDYING) {
			lores.forEach(lore -> {
				if (lore.contains(Utils.HP)) {
					int hp = Integer.parseInt(lore.substring(3).replace(Utils.HP, "").trim());
					if (lore.contains(ChatColor.RED.toString()) && !gamer.hasMaxHealth())
						gamer.changeCurrentHealth(hp);
					else if (lore.contains(ChatColor.GOLD.toString()))
						gamer.addAbsorption(hp);
				} else if (lore.contains(Utils.HUNGER)) {
					player.setFoodLevel(Math.max(20, player.getFoodLevel() +
							Integer.parseInt(lore.substring(3).replace(Utils.HUNGER, "").trim())));
				}
			});

			// Consume
			player.getInventory().setItem(Objects.requireNonNull(e.getHand()), null);
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

	// Manage consumption of food
	@EventHandler
	public void onEat(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
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

		// Give health and hunger
		lores.forEach(lore -> {
			if (lore.contains(Utils.HP)) {
				int hp = Integer.parseInt(lore.substring(3).replace(Utils.HP, "").trim());
				if (lore.contains(ChatColor.RED.toString()) && !gamer.hasMaxHealth())
					gamer.changeCurrentHealth(hp);
				else if (lore.contains(ChatColor.GOLD.toString()))
					gamer.addAbsorption(hp);
			}
			else if (lore.contains(Utils.HUNGER)) {
				int trueHunger = player.getFoodLevel() +
						Integer.parseInt(lore.substring(3).replace(Utils.HUNGER, "").trim());
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
						player.setFoodLevel(Math.min(20, trueHunger)));
			}
		});

		// No saturation increase
		player.setSaturation(0);
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

	// Prevent players from dropping menu items or abilities
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();

		// Check if player is in an arena
		if (!GameManager.checkPlayer(player))
			return;

		// Check for menu items
		ItemStack item = e.getItemDrop().getItemStack();
		if (VDMenuItem.matches(item) || VDAbility.matches(item))
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
				VDWeapon.matchesClickableWeapon(main))
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

	// Handle custom durability
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		// Check for valid arena
		try {
			GameManager.getArena(e.getPlayer());
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel event, then destroy if ready
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		e.setCancelled(true);
		if (!VDItem.updateDurability(item)) {
			if (item.equals(player.getInventory().getItemInMainHand()))
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			else if (item.equals(player.getInventory().getBoots()))
				player.getInventory().setBoots(new ItemStack(Material.AIR));
			else if (item.equals(player.getInventory().getChestplate()))
				player.getInventory().setChestplate(new ItemStack(Material.AIR));
			else if (item.equals(player.getInventory().getHelmet()))
				player.getInventory().setHelmet(new ItemStack(Material.AIR));
			else if (item.equals(player.getInventory().getLeggings()))
				player.getInventory().setLeggings(new ItemStack(Material.AIR));
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
		}
	}

	// Implement horse damage
	@EventHandler
	public void onHorseJump(HorseJumpEvent e) {
		LivingEntity ent = e.getEntity();
		Location location = ent.getLocation();

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
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			return;

		// Summon explosion if at full power
		if (e.getPower() >= 1)
			Objects.requireNonNull(location.getWorld()).createExplosion(location, 1.75f, false, false, ent);
	}

	// Prevent riding other player's horses
	@EventHandler
	public void onMount(EntityMountEvent e) {
		// Check for player mounting
		if (!(e.getEntity() instanceof Player))
			return;

		// Check for horse mounted
		if (!(e.getMount() instanceof AbstractHorse))
			return;

		Player player = (Player) e.getEntity();
		AbstractHorse horse = (AbstractHorse) e.getMount();

		// Attempt to get arena and player
		try {
			GameManager.getArena(player);
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Check for rightful owner
		if (player.equals(horse.getOwner()))
			return;

		// Cancel and notify
		e.setCancelled(true);
		PlayerManager.notifyFailure(player, LanguageManager.errors.notOwner);
	}
}
