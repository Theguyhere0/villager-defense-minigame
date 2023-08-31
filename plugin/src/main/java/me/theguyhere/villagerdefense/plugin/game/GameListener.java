package me.theguyhere.villagerdefense.plugin.game;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.*;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.guis.Inventories;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMobNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.minions.VDChargedCreeper;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.minions.VDCreeper;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.minions.VDWitch;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDPet;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.items.abilities.MageAbility;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.items.food.ShopFood;
import me.theguyhere.villagerdefense.plugin.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.items.menuItems.*;
import me.theguyhere.villagerdefense.plugin.items.weapons.*;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The listener that handles in-game events.
 */
public class GameListener implements Listener {
	// Keep score and manage mob death
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		LivingEntity ent = e.getEntity();

		// Check for arena mobs
		if (!VDMob.isVDMob(ent))
			return;

		Arena arena;
		VDMob mob;
		try {
			arena = GameController.getArena(VDMob.getArenaID(ent));
			mob = arena.getMob(ent.getUniqueId());
		}
		catch (ArenaNotFoundException | VDMobNotFoundException err) {
			return;
		}

		// Arena enemies not part of an active arena
		if (arena.getStatus() != ArenaStatus.ACTIVE) {
			e
				.getDrops()
				.clear();
			return;
		}

		// Clear normal drops
		e
			.getDrops()
			.clear();
		e.setDroppedExp(0);

		// Remove the mob
		arena.removeMob(mob.getID());

		if (VDMob.isTeam(ent, IndividualTeam.VILLAGER)) {
			// Update arena stats and scoreboards
			arena.decrementVillagers();
			arena.updateScoreboards();

			// Set villagers glowing when only 20% remain
			if (arena.getVillagers() <= .2 * arena.getMaxVillagers() && !arena.isSpawningVillagers() &&
				arena.getVillagers() > 0 && !arena.hasLowVillagerTriggered()) {
				arena.setVillagerGlow();
				arena.getPlayers().forEach(vdPlayer ->
					vdPlayer
						.getPlayer()
						.sendTitle(new ColoredMessage(ChatColor.RED, LanguageManager.messages.lowVillagerWarning).toString(), " ",
							Calculator.secondsToTicks(.5),
							Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
						));
				arena.setLowVillagerTriggered(true);
			}
		}

		// Handle enemy death
		else if (VDMob.isTeam(ent, IndividualTeam.MONSTER)) {
			// Check for right wave
			if (mob.getWave() != arena.getCurrentWave())
				return;

			// Update arena stats and scoreboards
			arena.decrementEnemies();
			arena.updateScoreboards();

			// Set monsters glowing when only 20% remain
			if (arena.getEnemies() <= .2 * arena.getMaxEnemies() && !arena.isSpawningMonsters() &&
				arena.getEnemies() > 0 && !arena.hasLowEnemyTriggered()) {
				arena.setMonsterGlow();
				arena.getPlayers().forEach(vdPlayer ->
					vdPlayer
						.getPlayer()
						.sendTitle(new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.lowEnemyWarning).toString(), " ",
							Calculator.secondsToTicks(.5),
							Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
						));
				arena.setLowEnemyTriggered(true);
			}
		}
	}

	// Stop automatic game mode switching between worlds
	@EventHandler
	public void onGameModeSwitch(PlayerGameModeChangeEvent e) {
		if (GameController.checkPlayer(e.getPlayer()) && e.getNewGameMode() == GameMode.SURVIVAL)
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
			gamer = GameController
				.getArena(player)
				.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Check for ammo weapon
		ItemStack range = Objects
			.requireNonNull(player.getEquipment())
			.getItemInMainHand();
		if (!VDWeapon.matchesAmmoWeapon(range))
			return;

		// Check for ammo
		ItemStack ammo = Objects
			.requireNonNull(player.getEquipment())
			.getItemInOffHand();
		if (!Ammo.matches(ammo)) {
			e.setCancelled(true);
			gamer.triggerAmmoWarningCooldown();
			return;
		}

		// Get data
		int cost = gamer.getAmmoCost();
		int capacity = gamer.getAmmoCap();
		Double cooldown = Objects
			.requireNonNull(range.getItemMeta())
			.getPersistentDataContainer()
			.get(VDWeapon.ATTACK_SPEED_KEY, PersistentDataType.DOUBLE);

		// Ignore if not enough capacity or has bad cooldown data
		if (capacity < cost || cooldown == null)
			return;

		// Check for cooldown
		if (gamer.remainingWeaponCooldown() > 0)
			return;

		// Fire and update capacity, durability, and cooldown
		if (Bow.matches(range)) {
			player.launchProjectile(Arrow.class);
			player.setCooldown(Material.BOW, Calculator.secondsToTicks(1 / cooldown));
		}
		else if (Crossbow.matches(range)) {
			player.launchProjectile(Arrow.class, player.getEyeLocation().getDirection().multiply(1.5));
			player.setCooldown(Material.CROSSBOW, Calculator.secondsToTicks(1 / cooldown));
		}
		gamer.triggerWeaponCooldown(Calculator.secondsToMillis(1 / cooldown));
		if (Ammo.updateCapacity(ammo, -cost)) {
			player
				.getInventory()
				.setItemInOffHand(new ItemStack(Material.AIR));
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
		}
		gamer.updateOffHand(player
			.getInventory()
			.getItemInOffHand());
		Bukkit
			.getPluginManager()
			.callEvent(new PlayerItemDamageEvent(player, range, 1));
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
				arena = GameController.getArena(player);
				gamer = arena.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Check for arrow
			if (!(projectile instanceof Arrow))
				return;

			// Encode damage information
			ItemStack range = Objects
				.requireNonNull(player.getEquipment())
				.getItemInMainHand();
			projectile.setMetadata(
				VDItem.MetaKey.DAMAGE.name(),
				new FixedMetadataValue(Main.plugin, gamer.dealRawDamage(VDPlayer.AttackClass.RANGE, 0))
			);
			if (gamer.isPerBlock()) {
				projectile.setMetadata(
					VDItem.MetaKey.PER_BLOCK.name(),
					new FixedMetadataValue(Main.plugin, true)
				);
				projectile.setMetadata(
					VDItem.MetaKey.ORIGIN_LOCATION.name(),
					new FixedMetadataValue(Main.plugin, player.getLocation())
				);
			}
			else projectile.setMetadata(
				VDItem.MetaKey.PER_BLOCK.name(),
				new FixedMetadataValue(Main.plugin, false)
			);
			if (Crossbow.matches(range)) {
				Integer pierce = Objects
					.requireNonNull(range.getItemMeta())
					.getPersistentDataContainer()
					.get(VDWeapon.PIERCE_KEY, PersistentDataType.INTEGER);
				if (pierce != null)
					((Arrow) projectile).setPierceLevel(pierce);
			}

			// Don't allow pickup
			((Arrow) projectile).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		}

		// Mob shot
		else {
			// Attempt to get VDMob
			try {
				arena = GameController.getArena(VDMob.getArenaID(shooter));
				finalShooter = arena.getMob(shooter.getUniqueId());
			}
			catch (ArenaNotFoundException | VDMobNotFoundException | IndexOutOfBoundsException err) {
				return;
			}

			// Check for witch
			if ((finalShooter instanceof VDWitch)) {
				if (!((ThrownPotion) e.getEntity())
					.getEffects()
					.isEmpty())
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
				arena = GameController.getArena(player);
				gamer = arena.getPlayer(player);
				finalDamager = arena.getMob(damager.getUniqueId());
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}
			catch (VDMobNotFoundException err) {
				e.setCancelled(true);
				return;
			}

			// Avoid phantom damage effects and friendly fire
			if (damager instanceof Player || VDMob.isTeam(damager, IndividualTeam.VILLAGER)) {
				e.setCancelled(true);
				return;
			}

			// Cancel original damage
			e.setDamage(0);

			// Make hurt sound if custom
			if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);

			// Realize damage and deal effect
			gamer.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType());

			// Give creeper explosion more knockback
			Vector fromCreeperVector =
				gamer
					.getPlayer()
					.getLocation()
					.toVector()
					.subtract(finalDamager
						.getEntity()
						.getLocation()
						.toVector())
					.normalize();
			if (finalDamager instanceof VDCreeper)
				gamer.getPlayer().setVelocity(gamer.getPlayer().getVelocity().add(fromCreeperVector.multiply(0.5)));
			else if (finalDamager instanceof VDChargedCreeper)
				gamer.getPlayer().setVelocity(gamer.getPlayer().getVelocity().add(fromCreeperVector.multiply(1.25)));

			// Start of dealing effects
			if (finalDamager.getEffectType() == null)
				return;

			// Deal fire
			if (finalDamager
				.getEffectType()
				.getName()
				.equals(PotionEffectType.FIRE_RESISTANCE.getName()))
				gamer.combust(Calculator.secondsToTicks(finalDamager.getEffectDuration()));

			// Deal other effects
			if ((Kit
				.witch()
				.getID()
				.equals(gamer
					.getKit()
					.getID())) &&
				!gamer.isSharing())
				return;
			Random r = new Random();
			if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(Kit.EffectType.WITCH))) {
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
				return;
			}
			else gamer
				.getPlayer()
				.addPotionEffect(finalDamager.dealEffect());
		}

		// VD entity getting hurt
		else if (VDMob.isVDMob(victim)) {
			// Check for player damager, then get player
			if (damager instanceof Player)
				player = (Player) damager;

			// Attempt to get VDPlayer and VDMobs
			if (player != null) {
				try {
					arena = GameController.getArena(player);
					gamer = arena.getPlayer(player);
					finalVictim = arena.getMob(victim.getUniqueId());
				}
				catch (ArenaNotFoundException | PlayerNotFoundException | VDMobNotFoundException err) {
					return;
				}
			}
			else {
				try {
					arena = GameController.getArena(VDMob.getArenaID(victim));
					finalVictim = arena.getMob(victim.getUniqueId());
				}
				catch (ArenaNotFoundException | VDMobNotFoundException err) {
					return;
				}
				try {
					finalDamager = arena.getMob(damager.getUniqueId());
				}
				catch (VDMobNotFoundException err) {
					e.setCancelled(true);
					return;
				}
			}

			// Enemy getting hurt
			if (VDMob.isTeam(victim, IndividualTeam.MONSTER)) {
				// Avoid phantom damage effects and friendly fire
				if (!(damager instanceof Player) &&
					VDMob.isTeam(damager, IndividualTeam.MONSTER)) {
					e.setCancelled(true);
					return;
				}

				// Cancel and capture original damage
				double damage = e.getDamage();
				e.setDamage(0);

				// Check for pacifist challenge and not an enemy
				if (gamer != null && gamer
					.getChallenges()
					.contains(Challenge.pacifist()) &&
					!gamer
						.getEnemies()
						.contains(victim.getUniqueId()))
					return;

				// Damage dealt by player
				if (gamer != null) {
					VDPlayer.AttackClass playerAttackClass;

					// Calculate damage difference
					AtomicInteger dif = new AtomicInteger();
					player
						.getActivePotionEffects()
						.forEach(potionEffect -> {
							if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
								dif.addAndGet((1 + potionEffect.getAmplifier()) * 3);
							else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
								dif.addAndGet(-(1 + potionEffect.getAmplifier()) * 4);
						});

					// Range damage
					if (projectile) {
						if (e
							.getDamager()
							.getMetadata(VDItem.MetaKey.PER_BLOCK.name())
							.get(0)
							.asBoolean()) {
							double distance = victim
								.getLocation()
								.distance((Location)
									Objects.requireNonNull(e
										.getDamager()
										.getMetadata(
											VDItem.MetaKey.ORIGIN_LOCATION.name())
										.get(0)
										.value()));
							finalVictim.takeDamage(
								(int) (e
									.getDamager()
									.getMetadata(VDItem.MetaKey.DAMAGE.name())
									.get(0)
									.asInt()
									* (Math.log(distance) * 2 + distance / 3.5))
									+ gamer.getBaseDamage(),
								IndividualAttackType.NORMAL,
								player,
								arena
							);
						}
						else finalVictim.takeDamage(
							e
								.getDamager()
								.getMetadata(VDItem.MetaKey.DAMAGE.name())
								.get(0)
								.asInt() +
								gamer.getBaseDamage(),
							IndividualAttackType.NORMAL,
							player,
							arena
						);
						return;
					}

					// Crit damage
					if (damage > 20 + dif.get())
						playerAttackClass = VDPlayer.AttackClass.CRITICAL;

						// Sweep damage
					else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
						playerAttackClass = VDPlayer.AttackClass.SWEEP;

						// Main damage
					else playerAttackClass = VDPlayer.AttackClass.MAIN;

					// Play out damage
					int hurt = finalVictim.takeDamage(
						gamer.dealRawDamage(playerAttackClass, damage / (double) (dif.get() + 20)),
						gamer.getAttackType(), player, arena
					);

					Random r = new Random();

					// Check for vampire kit
					if (Kit
						.vampire()
						.getID()
						.equals(gamer
							.getKit()
							.getID()) && !gamer.isSharing()) {
						// Heal if probability is right
						if (r.nextDouble() < .2)
							gamer.changeCurrentHealth((int) (hurt * .25));
					}

					// Check for shared vampire effect
					else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(Kit.EffectType.VAMPIRE))) {
						// Heal if probability is right
						if (r.nextDouble() < .2) {
							gamer.changeCurrentHealth((int) (hurt * .25));
							PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
						}
					}
				}

				// Damage not dealt by player
				else {
					// Check for pet
					if (finalDamager instanceof VDPet)
						finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(),
							((VDPet) finalDamager)
								.getOwner()
								.getPlayer(), arena
						);

						// Play out damage and effect
					else finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(),
						null, arena
					);
					if (finalDamager.dealEffect() != null)
						finalVictim
							.getEntity()
							.addPotionEffect(finalDamager.dealEffect());
				}
			}

			// Friendly getting hurt
			if (VDMob.isTeam(victim, IndividualTeam.VILLAGER)) {
				// Avoid phantom damage effects and friendly fire
				if (damager instanceof Player ||
					VDMob.isTeam(damager, IndividualTeam.VILLAGER)) {
					e.setCancelled(true);
					return;
				}
				if (finalDamager == null)
					return;

				// Cancel original damage
				e.setDamage(0);

				// Check for no damage
				if (finalDamager.getAttackType() == IndividualAttackType.NONE) {
					e.setCancelled(true);
					return;
				}

				// Play out damage and effect
				finalVictim.takeDamage(finalDamager.dealRawDamage(), finalDamager.getAttackType(), null,
					arena
				);
				if (finalDamager.dealEffect() != null)
					finalVictim
						.getEntity()
						.addPotionEffect(finalDamager.dealEffect());
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
				arena = GameController.getArena(player);
				gamer = arena.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Custom damage handling
			switch (damageCause) {
				// Environmental damage, not meant for customization
				case DROWNING:
				case SUFFOCATION:
					gamer.takeDamage((int) (damage * 25), IndividualAttackType.DIRECT);
					break;
				case LAVA:
				case HOT_FLOOR:
				case LIGHTNING:
					gamer.takeDamage((int) (damage * 40), IndividualAttackType.PENETRATING);
					break;
				case FALLING_BLOCK:
				case FALL:
				case BLOCK_EXPLOSION:
					gamer.takeDamage((int) (damage * 25), IndividualAttackType.CRUSHING);
					break;
				// Custom handling
				case FIRE:
				case FIRE_TICK:
					gamer.takeDamage((int) (damage * 50), IndividualAttackType.NORMAL);
					break;
				case POISON:
					gamer.takeDamage((int) (damage * 25), IndividualAttackType.PENETRATING);
					break;
				case WITHER:
					gamer.takeDamage((int) (damage * 20), IndividualAttackType.DIRECT);
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
				arena = GameController.getArena(VDMob.getArenaID(ent));
				mob = arena.getMob(ent.getUniqueId());
			}
			catch (ArenaNotFoundException | VDMobNotFoundException | IndexOutOfBoundsException err) {
				return;
			}

			// Custom damage handling
			switch (damageCause) {
				// Environmental damage, not meant for customization
				case DROWNING:
				case SUFFOCATION:
					mob.takeDamage((int) (damage * 25), IndividualAttackType.DIRECT, null, arena);
					break;
				case LAVA:
				case HOT_FLOOR:
				case LIGHTNING:
					mob.takeDamage((int) (damage * 40), IndividualAttackType.PENETRATING, null, arena);
					break;
				case FALLING_BLOCK:
				case FALL:
				case BLOCK_EXPLOSION:
					mob.takeDamage((int) (damage * 25), IndividualAttackType.CRUSHING, null, arena);
					break;
				// Custom handling
				case FIRE:
				case FIRE_TICK:
					mob.takeDamage((int) (damage * 50), IndividualAttackType.NORMAL, null, arena);
					break;
				case POISON:
					mob.takeDamage((int) (damage * 25), IndividualAttackType.PENETRATING, null, arena);
					break;
				case WITHER:
					mob.takeDamage((int) (damage * 20), IndividualAttackType.DIRECT, null, arena);
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
		if (target == null || !VDMob.isVDMob(entity) || !(target instanceof Player) && !VDMob.isVDMob(target))
			return;

		Arena arena;
		VDMob mob;
		VDMob targeted;

		// Attempt to get VDPlayer and VDMob
		try {
			arena = GameController.getArena(VDMob.getArenaID(e.getEntity()));
			mob = arena.getMob(e
				.getEntity()
				.getUniqueId());
			if (target instanceof Player && !arena.hasPlayer((Player) target))
				return;
			else targeted = arena.getMob(target.getUniqueId());
		}
		catch (ArenaNotFoundException | VDMobNotFoundException err) {
			return;
		}

		// Monsters
		if (VDMob.isTeam(mob.getEntity(), IndividualTeam.MONSTER))
			if (targeted != null && VDMob.isTeam(targeted.getEntity(), IndividualTeam.MONSTER))
				e.setCancelled(true);

				// Villager team
			else if (VDMob.isTeam(mob.getEntity(), IndividualTeam.VILLAGER) && (targeted == null ||
				VDMob.isTeam(mob.getEntity(), IndividualTeam.VILLAGER)))
				e.setCancelled(true);
	}

	// Stop custom mobs from burning in the sun
	@EventHandler
	public void onCombust(EntityCombustEvent e) {
		if (e instanceof EntityCombustByBlockEvent || e instanceof EntityCombustByEntityEvent)
			return;

		if (VDMob.isVDMob(e.getEntity()))
			e.setCancelled(true);
	}

	// Create custom potion effects from witch
	@EventHandler
	public void onSplash(PotionSplashEvent e) {
		ThrownPotion potion = e.getEntity();
		Entity ent = (Entity) potion.getShooter();
		Arena arena;

		if (ent instanceof Player) {
			// Try to get arena
			try {
				GameController.getArena((Player) ent);
			}
			catch (ArenaNotFoundException err) {
				return;
			}

			// Apply to relevant entities
			for (LivingEntity affectedEntity : e.getAffectedEntities()) {
				// Not monster
				if (!(affectedEntity instanceof Player) && VDMob.isTeam(affectedEntity, IndividualTeam.MONSTER))
					continue;

				// Apply affects
				affectedEntity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
					Calculator.secondsToTicks(30), 0));
			}

			// Cancel vanilla event
			e.setCancelled(true);
		}
		else {
			VDWitch witch;

			// Try to get arena and VDMob
			try {
				arena = GameController.getArena(VDMob.getArenaID(Objects.requireNonNull(ent)));
				witch = (VDWitch) arena.getMob(ent.getUniqueId());
			}
			catch (ArenaNotFoundException | VDMobNotFoundException | IndexOutOfBoundsException |
				   NullPointerException | ClassCastException err) {
				return;
			}

			// Apply to relevant entities
			Random r = new Random();
			for (LivingEntity affectedEntity : e.getAffectedEntities()) {
				// Not monster
				if (!(affectedEntity instanceof Player) && VDMob.isTeam(affectedEntity, IndividualTeam.MONSTER))
					continue;

				// Ignore players with witch kit
				try {
					VDPlayer player = arena.getPlayer(affectedEntity.getUniqueId());
					if (Kit
						.witch()
						.getID()
						.equals(player
							.getKit()
							.getID()) && !player.isSharing())
						continue;
					if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(Kit.EffectType.WITCH))) {
						PlayerManager.notifySuccess(player.getPlayer(), LanguageManager.messages.effectShare);
						return;
					}
				}
				catch (PlayerNotFoundException ignored) {
				}

				// Apply affects
				affectedEntity.addPotionEffect(witch.dealEffect());
			}

			// Cancel vanilla event
			e.setCancelled(true);
		}
	}

	// Prevent players from going hungry while waiting in an arena
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		Player player = (Player) e.getEntity();
		Arena arena;

		// Check for player in arena
		try {
			arena = GameController.getArena(player);
		}
		catch (ArenaNotFoundException err) {
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
		VDPlayer gamer = null;

		// Check for VDPlayer
		if (ent instanceof Player) {
			Player player = (Player) ent;

			// Attempt to get VDPlayer
			try {
				gamer = GameController
					.getArena(player)
					.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}
		}

		// Check for VDMob
		else {
			try {
				GameController
					.getArena(VDMob.getArenaID(ent))
					.getMob(ent.getUniqueId());
			}
			catch (IndexOutOfBoundsException | VDMobNotFoundException | ArenaNotFoundException err) {
				return;
			}
		}

		// Allow plugin, command, and expiration causes, and update player stats if appropriate
		if (e.getCause() == EntityPotionEffectEvent.Cause.PLUGIN ||
			e.getCause() == EntityPotionEffectEvent.Cause.COMMAND ||
			e.getCause() == EntityPotionEffectEvent.Cause.EXPIRATION ||
			e.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK ||
			e.getCause() == EntityPotionEffectEvent.Cause.POTION_SPLASH) {
			if (gamer != null) {
				VDPlayer finalGamer = gamer;
				Bukkit
					.getScheduler()
					.scheduleSyncDelayedTask(Main.plugin, () -> {
						finalGamer.updateDamageMultiplier();
						finalGamer.updateArmor();
					}, 1);
			}
			return;
		}

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
				arena = GameController.getArena(player);
			}
			catch (ArenaNotFoundException err) {
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
			if (!VDMob.isVDMob(ent))
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
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Get item in hand
		ItemStack item;
		if (e.getHand() == EquipmentSlot.OFF_HAND) {
			item = Objects
				.requireNonNull(player.getEquipment())
				.getItemInOffHand();
			ItemStack main = Objects
				.requireNonNull(player.getEquipment())
				.getItemInMainHand();

			// Check for other clickables in main hand
			if (VDAbility.matches(main) || VDFood.matches(main) || VDArmor.matches(main) ||
				VDWeapon.matchesClickableWeapon(main))
				return;
		}
		else item = Objects
			.requireNonNull(player.getEquipment())
			.getItemInMainHand();

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
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(Main.plugin, () ->
					Bukkit
						.getPluginManager()
						.callEvent(new LeaveArenaEvent(player)));

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
		if (!e
			.getCause()
			.equals(EntityDamageEvent.DamageCause.VOID)) return;

		// Attempt to get arena and player
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Check if game has started yet
		if (arena.getStatus() == ArenaStatus.WAITING) {
			// Cancel void damage
			e.setCancelled(true);

			// Teleport player back to player spawn or waiting room
			if (arena.getWaitingRoom() == null)
				try {
					player.teleport(arena
						.getPlayerSpawn()
						.getLocation());
				}
				catch (NullPointerException err) {
					CommunicationManager.debugError(err.getMessage(), CommunicationManager.DebugLevel.QUIET);
				}
			else player.teleport(arena.getWaitingRoom());
		}
		else {
			// Set player to fake death mode
			PlayerManager.fakeDeath(gamer);

			// Check for explosive challenge
			if (gamer
				.getChallenges()
				.contains(Challenge.explosive()))
				player
					.getInventory()
					.clear();

			// Notify player of their own death
			player.sendTitle(
				new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.death1).toString(),
				new ColoredMessage(ChatColor.RED, LanguageManager.messages.death2).toString(),
				Calculator.secondsToTicks(.5), Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
			);

			// Teleport player back to player spawn
			try {
				player.teleport(arena
					.getPlayerSpawn()
					.getLocation());
			}
			catch (NullPointerException err) {
				CommunicationManager.debugError(err.getMessage(), CommunicationManager.DebugLevel.QUIET);
			}
			player.closeInventory();

			// Notify everyone else of player death
			arena
				.getPlayers()
				.forEach(fighter -> {
					if (!fighter
						.getPlayer()
						.getUniqueId()
						.equals(player.getUniqueId()))
						PlayerManager.notifyAlert(
							fighter.getPlayer(),
							String.format(LanguageManager.messages.death, player.getName())
						);
					if (arena.hasPlayerDeathSound())
						try {
							fighter
								.getPlayer()
								.playSound(arena
										.getPlayerSpawn()
										.getLocation()
										.clone()
										.add(0, -8, 0),
									Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10,
									.75f
								);
						}
						catch (NullPointerException err) {
							CommunicationManager.debugError(err.getMessage(), CommunicationManager.DebugLevel.QUIET);
						}
				});

			// Update scoreboards
			arena.updateScoreboards();

			// Set players glowing when only 20% remain and notify
			if (arena.getAlive() <= .2 * arena.getActiveCount() && arena.getAlive() > 0 &&
				!arena.hasLowPlayerTriggered()) {
				arena.setPlayerGlow();
				arena.getPlayers().forEach(vdPlayer ->
					vdPlayer
						.getPlayer()
						.sendTitle(new ColoredMessage(ChatColor.RED, LanguageManager.messages.lowPlayerWarning).toString(), " ",
							Calculator.secondsToTicks(.5),
							Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
						));
				arena.setLowPlayerTriggered(true);
			}

			// Check for game end condition
			if (arena.getAlive() == 0)
				try {
					arena.endGame();
				}
				catch (ArenaException err) {
					CommunicationManager.debugError("%s: %s", CommunicationManager.DebugLevel.QUIET, arena.getName(),
						err.getMessage());
				}
		}
	}

	// Stops slimes and magma cubes from splitting on death
	@EventHandler
	public void onSplit(SlimeSplitEvent e) {
		Entity ent = e.getEntity();
		if (!VDMob.isVDMob(ent))
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
		if (VDMob.isVDMob(ent))
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
		ItemStack main = player
			.getInventory()
			.getItemInMainHand();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException | NullPointerException err) {
			return;
		}

		// Check armor after equip
		if (VDArmor.matches(item)) {
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(Main.plugin, gamer::updateArmor, 1);
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
				VDWeapon.matchesClickableWeapon(main)))
			return;

		// Give health and hunger for totem
		if (ShopFood.matches(item) && item.getType() == Material.TOTEM_OF_UNDYING) {
			PersistentDataContainer dataContainer = Objects
				.requireNonNull(item.getItemMeta())
				.getPersistentDataContainer();

			Integer integer = dataContainer.get(VDFood.HEALTH_KEY, PersistentDataType.INTEGER);
			if (integer != null)
				gamer.changeCurrentHealth(integer);
			integer = dataContainer.get(VDFood.ABSORPTION_KEY, PersistentDataType.INTEGER);
			if (integer != null)
				gamer.addAbsorption(integer);
			integer = dataContainer.get(VDFood.HUNGER_KEY, PersistentDataType.INTEGER);
			if (integer != null)
				player.setFoodLevel(Math.min(20, player.getFoodLevel() + integer));

			// Consume
			player
				.getInventory()
				.setItem(Objects.requireNonNull(e.getHand()), null);
		}

		// Give proper effect for potions
		if (Potion.matches(item) && item.getType() != Material.SPLASH_POTION) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			if (meta == null) {
				CommunicationManager.debugErrorShouldNotHappen();
				return;
			}
			PotionEffectType type = meta
					.getBasePotionData()
					.getType()
					.getEffectType();
			if (type == null) {
				CommunicationManager.debugErrorShouldNotHappen();
				return;
			}
			PersistentDataContainer dataContainer = Objects
				.requireNonNull(item.getItemMeta())
				.getPersistentDataContainer();
			Double duration = dataContainer.get(VDItem.DURATION_KEY, PersistentDataType.DOUBLE);
			if (duration != null)
				player.addPotionEffect(new PotionEffect(type, Calculator.secondsToTicks(duration.intValue()), 0));

			// Consume
			player
				.getInventory()
				.setItem(Objects.requireNonNull(e.getHand()), null);
		}
	}

	// Manage consumption of food
	@EventHandler
	public void onEat(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException | NullPointerException err) {
			return;
		}

		// Stop vanilla consumption
		e.setCancelled(true);

		// Check for active arena, at least wave 1, otherwise ignore
		if (arena.getStatus() != ArenaStatus.ACTIVE || arena.getCurrentWave() < 1)
			return;

		// Give health and hunger
		PersistentDataContainer dataContainer = Objects
			.requireNonNull(item.getItemMeta())
			.getPersistentDataContainer();

		Integer integer = dataContainer.get(VDFood.HEALTH_KEY, PersistentDataType.INTEGER);
		if (integer != null)
			gamer.changeCurrentHealth(integer);
		integer = dataContainer.get(VDFood.ABSORPTION_KEY, PersistentDataType.INTEGER);
		if (integer != null)
			gamer.addAbsorption(integer);
		integer = dataContainer.get(VDFood.HUNGER_KEY, PersistentDataType.INTEGER);
		if (integer != null)
			player.setFoodLevel(Math.min(20, player.getFoodLevel() + integer));

		// No saturation increase
		player.setSaturation(0);

		// Manually consume
		item.setAmount(item.getAmount() - 1);
		if (e.getHand() == EquipmentSlot.HAND)
			player
				.getInventory()
				.setItemInMainHand(item);
		else if (e.getHand() == EquipmentSlot.OFF_HAND)
			player
				.getInventory()
				.setItemInOffHand(item);
	}

	// Prevent pets from teleporting
	@EventHandler
	public void onTeleport(EntityTeleportEvent e) {
		Entity ent = e.getEntity();

		// Check for wolf
		if (!(ent instanceof Wolf) && !(ent instanceof Cat))
			return;

		// Check for special mob
		if (!VDMob.isVDMob(ent))
			return;

		// Check if player is playing in an arena
		if (GameController.checkPlayer((Player) ((Tameable) ent).getOwner()))
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
			arena = GameController.getArena(player);
		}
		catch (ArenaNotFoundException err) {
			return;
		}

		// Check if the arena has started
		if (arena.getStatus() == ArenaStatus.WAITING)
			return;

		// Cancel teleport and notify if teleport is outside arena bounds
		if (!(BoundingBox
			.of(arena.getCorner1(), arena.getCorner2())
			.contains(Objects
				.requireNonNull(e.getTo())
				.getX(), e
				.getTo()
				.getY(), e
				.getTo()
				.getZ())) ||
			!Objects.equals(e
				.getTo()
				.getWorld(), arena
				.getCorner1()
				.getWorld())) {
			e.setCancelled(true);
			PlayerManager.notifyFailure(player, LanguageManager.errors.teleport,
				new ColoredMessage(ChatColor.AQUA, "/vd leave")
			);
		}
	}

	// Prevent players from leaving the arena bounds
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Arena arena;
		VDPlayer gamer;

		// Exempt admins for testing purposes
		if (CommunicationManager
			.getDebugLevel()
			.atLeast(CommunicationManager.DebugLevel.DEVELOPER) && player.hasPermission("vd.admin"))
			return;

		// Attempt to get VDPlayer and arena
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Exempt if in waiting status and waiting room exists
		if (arena.getStatus() == ArenaStatus.WAITING && arena.getWaitingRoom() != null)
			return;

		// Cancel move and notify if movement is outside arena bounds
		if (!(BoundingBox
			.of(arena.getCorner1(), arena.getCorner2())
			.contains(Objects
				.requireNonNull(e.getTo())
				.getX(), e
				.getTo()
				.getY(), e
				.getTo()
				.getZ())) ||
			!Objects.equals(e
				.getTo()
				.getWorld(), arena
				.getCorner1()
				.getWorld())) {

			// Teleport player back into arena after several infractions
			if (gamer.incrementInfractions() > 5) {
				gamer.resetInfractions();
				try {
					if (gamer.getStatus() == VDPlayer.Status.ALIVE)
						player.teleport(arena
							.getPlayerSpawn()
							.getLocation());
					else PlayerManager.teleSpectator(player, arena
						.getPlayerSpawn()
						.getLocation());
				}
				catch (NullPointerException err) {
					CommunicationManager.debugError(err.getMessage(), CommunicationManager.DebugLevel.QUIET);
				}
			}
			else e.setCancelled(true);

			PlayerManager.notifyFailure(player, LanguageManager.errors.bounds);
		}
	}

	// Prevents arena mobs from turning into different entities
	@EventHandler
	public void onTransform(EntityTransformEvent e) {
		Entity ent = e.getEntity();

		// Check for special mob
		if (!VDMob.isVDMob(ent))
			return;

		e.setCancelled(true);
	}

	// Prevent players from dropping menu items or abilities, otherwise update player stats
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		VDPlayer gamer;

		// Attempt to get VDPlayer
		try {
			gamer = GameController
				.getArena(player)
				.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Check for menu items and abilities
		ItemStack item = e
			.getItemDrop()
			.getItemStack();
		if (VDMenuItem.matches(item) || VDAbility.matches(item)) {
			e.setCancelled(true);
			return;
		}

		// Update everything
		gamer.updateMainHand(player
			.getInventory()
			.getItemInMainHand());
		gamer.updateOffHand(player
			.getInventory()
			.getItemInOffHand());
		gamer.updateArmor();
	}

	// Update player stats when picking up items
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		// Check for player
		if (!(e.getEntity() instanceof Player))
			return;

		Player player = (Player) e.getEntity();
		VDPlayer gamer;

		// Attempt to get VDPlayer
		try {
			gamer = GameController
				.getArena(player)
				.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Update main hand after one tick
		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.plugin,
				() -> gamer.updateMainHand(player
					.getInventory()
					.getItemInMainHand()), 1
			);
	}

	// Prevent consumption from happening in the off-hand when the main hand has something interact-able
	@EventHandler
	public void onFalseConsume(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack main = player
			.getInventory()
			.getItemInMainHand();

		// Check for player in arena
		if (!GameController.checkPlayer(player))
			return;

		// Filter off-hand interactions
		if (e.getHand() != EquipmentSlot.OFF_HAND)
			return;

		// Avoid false consume
		if (Shop.matches(main) || VDAbility.matches(main) || VDFood.matches(main) || VDArmor.matches(main) ||
			VDWeapon.matchesClickableWeapon(main))
			e.setCancelled(true);
	}

	// Restrict item movement and update player stats
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get VDPlayer and arena
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Cancel event if a menu item is being involved
		if (VDMenuItem.matches(e.getCurrentItem()) || VDMenuItem.matches(e.getCursor()) ||
			e.getClick() == ClickType.NUMBER_KEY && VDMenuItem.matches(player
				.getInventory()
				.getItem(e.getHotbarButton()))) {
			e.setCancelled(true);
			return;
		}

		// Ignore if not clicking in own inventory
		if (e.getClickedInventory() == null || Objects
			.requireNonNull(e.getClickedInventory())
			.getType() != InventoryType.PLAYER && Objects
			.requireNonNull(e.getClickedInventory())
			.getType() != InventoryType.CRAFTING)
			return;

		// Check for illegal offhand swap, then update offhand if slots changes
		if (e.getClick() == ClickType.SWAP_OFFHAND) {
			// Unequip weapons and mage abilities in offhand
			if (VDWeapon.matchesNoAmmo(e.getCurrentItem()) || MageAbility.matches(e.getCurrentItem())) {
				e.setCancelled(true);
				PlayerManager.notifyFailure(player, LanguageManager.errors.offWeapon);
			}

			else gamer.updateOffHand(e.getCurrentItem());
		}

		// Update main hand if that slot changes
		if (e.getSlot() == player
			.getInventory()
			.getHeldItemSlot())
			gamer.updateMainHand(e.getCursor());

		// Check offhand for illegal stuff, then update offhand if that slot changes
		else if (e.getSlot() == 40) {
			ItemStack buff;
			if (e.getClick() == ClickType.NUMBER_KEY)
				buff = player.getInventory().getItem(e.getHotbarButton());
			else buff = e.getCursor();

			// Unequip weapons and mage abilities in offhand
			if (VDWeapon.matchesNoAmmo(buff) || MageAbility.matches(buff)) {
				e.setCancelled(true);
				PlayerManager.notifyFailure(player, LanguageManager.errors.offWeapon);
			}

			else gamer.updateOffHand(buff);
		}

		// Update armor if those slots change
		else if (e.getSlot() >= 36 && e.getSlot() <= 39)
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(Main.plugin, gamer::updateArmor, 1);

		// Update weapon on shift click
		else if (e.isShiftClick() && VDWeapon.matches(e.getCurrentItem()) &&
			player
				.getInventory()
				.firstEmpty() == player
				.getInventory()
				.getHeldItemSlot())
			gamer.updateMainHand(e.getCurrentItem());

		// Update armor on shift click
		else if (e.isShiftClick() && VDArmor.matches(e.getCurrentItem()))
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(Main.plugin, gamer::updateArmor, 1);
	}

	// Prevent dragging items around while waiting for game to start, otherwise update player stats
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		Player player = (Player) e.getWhoClicked();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get VDPlayer and arena
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Cancel event if arena is in waiting mode
		if (arena.getStatus() == ArenaStatus.WAITING) {
			e.setCancelled(true);
			return;
		}

		// Ignore if not clicking in own inventory
		if (e
			.getInventory()
			.getType() != InventoryType.PLAYER && e
			.getInventory()
			.getType() != InventoryType.CRAFTING)
			return;

		// Update main hand if that slot changes
		if (e.getInventorySlots().contains(player
			.getInventory()
			.getHeldItemSlot()))
			gamer.updateMainHand(e.getCursor());

		// Check offhand for illegal stuff, then update offhand if that slot changes
		else if (e.getInventorySlots().contains(40)) {
			// Unequip weapons and mage abilities in offhand
			if (VDWeapon.matchesNoAmmo(e.getOldCursor()) || MageAbility.matches(e.getOldCursor())) {
				e.setCancelled(true);
				PlayerManager.notifyFailure(player, LanguageManager.errors.offWeapon);
			}

			else gamer.updateOffHand(e.getOldCursor());
		}

		// Update armor if those slots change
		else if (e.getInventorySlots().containsAll(Arrays.asList(36, 37, 38, 39)))
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(Main.plugin, gamer::updateArmor, 1);
	}

	// Prevent swapping items while waiting for game to start or for menu items, otherwise update player stats
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent e) {
		Player player = e.getPlayer();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get VDPlayer and arena
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Cancel event if arena is in waiting mode
		if (arena.getStatus() == ArenaStatus.WAITING) {
			e.setCancelled(true);
			return;
		}

		// Cancel for menu items
		if (VDMenuItem.matches(e.getMainHandItem()) || VDMenuItem.matches(e.getOffHandItem())) {
			e.setCancelled(true);
			return;
		}

		// Unequip weapons and mage abilities in offhand
		if (VDWeapon.matchesNoAmmo(e.getOffHandItem()) || MageAbility.matches(e.getOffHandItem())) {
			e.setCancelled(true);
			PlayerManager.notifyFailure(player, LanguageManager.errors.offWeapon);
		}

		// Trigger check for main and off hands
		gamer.updateMainHand(e.getMainHandItem());
		gamer.updateOffHand(e.getOffHandItem());
	}

	// Update player stats properly when main hand item changes from hotbar selection change
	@EventHandler
	public void onHotBarChange(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		Arena arena;
		VDPlayer gamer;

		// Attempt to get VDPlayer and arena
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Trigger check for main hand
		gamer.updateMainHand(player
			.getInventory()
			.getItem(e.getNewSlot()));
	}

	// Handle custom durability
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		VDPlayer gamer;

		// Attempt to get VDPlayer
		try {
			gamer = GameController
				.getArena(player)
				.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Cancel event, then destroy if ready
		e.setCancelled(true);
		if (!VDItem.updateDurability(item)) {
			if (item.equals(player
				.getInventory()
				.getItemInMainHand())) {
				player
					.getInventory()
					.setItemInMainHand(ItemStackBuilder.buildNothing());
				gamer.updateMainHand(ItemStackBuilder.buildNothing());
			}
			else if (item.equals(player
				.getInventory()
				.getBoots())) {
				player
					.getInventory()
					.setBoots(ItemStackBuilder.buildNothing());
			}
			else if (item.equals(player
				.getInventory()
				.getChestplate()))
				player
					.getInventory()
					.setChestplate(ItemStackBuilder.buildNothing());
			else if (item.equals(player
				.getInventory()
				.getHelmet()))
				player
					.getInventory()
					.setHelmet(ItemStackBuilder.buildNothing());
			else if (item.equals(player
				.getInventory()
				.getLeggings()))
				player
					.getInventory()
					.setLeggings(ItemStackBuilder.buildNothing());
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
		}
	}

	// Implement horse damage
	@EventHandler
	public void onHorseJump(HorseJumpEvent e) {
		LivingEntity ent = e.getEntity();
		Location location = ent.getLocation();

		// Check for arena mobs
		if (!VDMob.isVDMob(ent))
			return;

		Arena arena;
		try {
			arena = GameController.getArena(VDMob.getArenaID(ent));
		}
		catch (ArenaNotFoundException err) {
			return;
		}

		// Arena enemies not part of an active arena
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			return;

		// Summon explosion if at full power
		if (e.getPower() >= 1)
			Objects
				.requireNonNull(location.getWorld())
				.createExplosion(location, 1.75f, false, false, ent);
	}

	// Prevent riding other player's horses and update stats
	@EventHandler
	public void onMount(EntityMountEvent e) {
		// Check for player mounting
		if (!(e.getEntity() instanceof Player))
			return;

		// Check for horse mounted
		if (!(e.getMount() instanceof AbstractHorse))
			return;

		Player player = (Player) e.getEntity();
		VDPlayer gamer;
		AbstractHorse horse = (AbstractHorse) e.getMount();

		// Attempt to get arena and player
		try {
			gamer = GameController.getArena(player).getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Check for rightful owner, then update stats if true
		if (player.equals(horse.getOwner())) {
			gamer.updateDamageMultiplier();
			return;
		}

		// Cancel and notify
		e.setCancelled(true);
		PlayerManager.notifyFailure(player, LanguageManager.errors.notOwner);
	}

	// Make sure stats are updated properly on horse dismount
	@EventHandler
	public void onDismount(EntityDismountEvent e) {
		// Check for player dismounting
		if (!(e.getEntity() instanceof Player))
			return;

		// Check for horse dismounted
		if (!(e.getDismounted() instanceof AbstractHorse))
			return;

		Player player = (Player) e.getEntity();
		VDPlayer gamer;

		// Attempt to get arena and player
		try {
			gamer = GameController.getArena(player).getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Update stats
		gamer.updateDamageMultiplier();
	}

	// Prevent vanilla spawning in arenas
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		// Ignore for custom mobs
		if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
			return;

		// Check all arena bounds and cancel if inside
		GameController.getArenas().forEach((id, arena) -> {
			if (arena.getBounds().contains(e.getLocation().toVector())) {
				e.setCancelled(true);
				CommunicationManager.debugInfo("Prevent a mob from spawning", CommunicationManager.DebugLevel.VERBOSE);
			}
		});
	}
}
