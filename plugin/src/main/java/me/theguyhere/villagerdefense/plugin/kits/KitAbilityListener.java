package me.theguyhere.villagerdefense.plugin.kits;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.game.WorldManager;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.items.abilities.*;
import me.theguyhere.villagerdefense.plugin.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.items.weapons.VDWeapon;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class KitAbilityListener implements Listener {
	// Most ability functionalities
	@EventHandler
	public void onAbility(PlayerInteractEvent e) {
		// Check for right click
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Player player = e.getPlayer();
		UUID id = player.getUniqueId();
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

		// Check for other clickable items if on off-hand
		ItemStack main = player
			.getInventory()
			.getItemInMainHand();
		if (e.getHand() == EquipmentSlot.OFF_HAND && (Shop.matches(main) || VDAbility.matches(main) ||
			VDFood.matches(main) || VDArmor.matches(main) || VDWeapon.matchesClickableWeapon(main)))
			return;

		// Wait for arena to progress to first wave
		if (arena.getCurrentWave() < 1)
			return;

		// Gather item
		ItemStack item = e.getItem();
		if (item == null || !VDAbility.matches(item))
			return;

		// Check cooldown
		if (gamer.remainingAbilityCooldown() > 0)
			return;

		// Gather stats
		PersistentDataContainer dataContainer =
			Objects
				.requireNonNull(item.getItemMeta())
				.getPersistentDataContainer();
		Double cooldown = dataContainer.get(VDAbility.COOLDOWN_KEY, PersistentDataType.DOUBLE);
		Double range = dataContainer.get(VDAbility.RANGE_KEY, PersistentDataType.DOUBLE);
		Double duration = dataContainer.get(VDAbility.DURATION_KEY, PersistentDataType.DOUBLE);
		AtomicReference<String> effect = new AtomicReference<>();
		Objects
			.requireNonNull(Objects
				.requireNonNull(item.getItemMeta())
				.getLore())
			.forEach(lore -> {
				if (lore.contains(LanguageManager.messages.effect
					.replace("%s", ""))) {
					effect.set(lore);
				}
			});
		if (cooldown == null)
			return;
		if (range == null)
			range = 0d;
		if (duration == null)
			duration = 0d;
		double altDuration = duration * 0.6;

		// Check if player has cooldown decrease achievement and is boosted
		if (gamer.isBoosted() && PlayerManager.hasAchievement(id, Achievement
			.allMaxedAbility()
			.getID()))
			cooldown = cooldown * .9;

		// Mage
		if (MageAbility.matches(item)) {
			float yield = 1.75f;

			// Activate ability
			Fireball fireball = player
				.getWorld()
				.spawn(player.getEyeLocation(), Fireball.class);
			fireball.setYield(yield);
			fireball.setShooter(player);
			fireball.setMetadata(
				VDItem.MetaKey.DAMAGE.name(),
				new FixedMetadataValue(Main.plugin, gamer.dealRawDamage(VDPlayer.AttackClass.RANGE, 0))
			);
			fireball.setMetadata(VDItem.MetaKey.PER_BLOCK.name(), new FixedMetadataValue(Main.plugin, false));
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));
		}

		// Ninja
		else if (NinjaAbility.matches(item)) {
			// Activate ability
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
				Calculator.secondsToTicks(duration), 0
			));
			Double finalDuration2 = duration;
			WorldManager
				.getPets(player)
				.forEach(wolf ->
					wolf.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY,
						Calculator.secondsToTicks(finalDuration2), 0
					))));
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Nerf
			gamer.hideArmor();

			// Schedule un-nerf
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(Main.plugin, gamer::exposeArmor,
					Calculator.secondsToTicks(duration)
				);

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
		}

		// Templar
		else if (TemplarAbility.matches(item)) {
			// Calculate effect
			int absorption = 0;
			if (effect
				.get()
				.contains("100"))
				absorption = 100;
			else if (effect
				.get()
				.contains("200"))
				absorption = 200;
			else if (effect
				.get()
				.contains("300"))
				absorption = 300;

			// Activate ability
			int finalAbsorption = absorption;
			WorldManager
				.getNearbyPlayers(player, range)
				.forEach(player1 -> {
					try {
						arena
							.getPlayer(player1)
							.addAbsorptionUpTo(finalAbsorption);
					}
					catch (PlayerNotFoundException ignored) {
					}
				});
			gamer.addAbsorptionUpTo(absorption);
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				arena
					.getActives()
					.forEach(vdPlayer -> vdPlayer
						.getPlayer()
						.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
		}

		// Warrior
		else if (WarriorAbility.matches(item)) {
			// Calculate effect
			int amplifier;
			if (effect
				.get()
				.contains("10"))
				amplifier = 0;
			else if (effect
				.get()
				.contains("20"))
				amplifier = 1;
			else if (effect
				.get()
				.contains("30"))
				amplifier = 2;
			else return;

			// Activate ability
			WorldManager
				.getNearbyPlayers(player, range)
				.forEach(player1 -> player1.addPotionEffect(
					new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Calculator.secondsToTicks(altDuration),
						amplifier
					)));
			WorldManager
				.getNearbyAllies(player, range)
				.forEach(ally -> ally.addPotionEffect(
					new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Calculator.secondsToTicks(altDuration),
						amplifier
					)));
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
				Calculator.secondsToTicks(duration), amplifier
			));
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				arena
					.getActives()
					.forEach(vdPlayer -> vdPlayer
						.getPlayer()
						.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
		}

		// Knight
		else if (KnightAbility.matches(item)) {
			// Calculate effect
			int amplifier;
			if (effect
				.get()
				.contains("10"))
				amplifier = 0;
			else if (effect
				.get()
				.contains("20"))
				amplifier = 1;
			else if (effect
				.get()
				.contains("30"))
				amplifier = 2;
			else return;

			// Activate ability
			WorldManager
				.getNearbyPlayers(player, range)
				.forEach(player1 -> player1.addPotionEffect(
					new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Calculator.secondsToTicks(altDuration),
						amplifier
					)));
			WorldManager
				.getNearbyAllies(player, range)
				.forEach(ally -> ally.addPotionEffect(
					new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Calculator.secondsToTicks(altDuration),
						amplifier
					)));
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
				Calculator.secondsToTicks(duration), amplifier
			));
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				arena
					.getActives()
					.forEach(vdPlayer -> vdPlayer
						.getPlayer()
						.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
		}

		// Priest
		else if (PriestAbility.matches(item)) {
			// Calculate effect
			int amplifier;
			if (effect
				.get()
				.contains("+5"))
				amplifier = 0;
			else if (effect
				.get()
				.contains("+10"))
				amplifier = 1;
			else if (effect
				.get()
				.contains("+15"))
				amplifier = 2;
			else return;

			// Activate ability
			WorldManager
				.getNearbyPlayers(player, range)
				.forEach(player1 -> player1.addPotionEffect(
					new PotionEffect(PotionEffectType.REGENERATION, Calculator.secondsToTicks(altDuration),
						amplifier
					)));
			WorldManager
				.getNearbyAllies(player, range)
				.forEach(ally -> ally.addPotionEffect(
					new PotionEffect(PotionEffectType.REGENERATION, Calculator.secondsToTicks(altDuration),
						amplifier
					)));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
				Calculator.secondsToTicks(duration), amplifier
			));
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				arena
					.getActives()
					.forEach(vdPlayer -> vdPlayer
						.getPlayer()
						.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
		}

		// Siren
		else if (SirenAbility.matches(item)) {
			// Calculate effect
			int amp1 = -1;
			int amp2 = -1;
			if (effect
				.get()
				.contains("15"))
				amp1 = 0;
			else if (effect
				.get()
				.contains("30"))
				amp1 = 1;
			if (effect
				.get()
				.contains("10"))
				amp2 = 0;

			// Activate ability
			if (amp1 != -1) {
				int finalAmp = amp1;
				Double finalDuration = duration;
				WorldManager
					.getNearbyMonsters(player, range)
					.forEach(ent -> ent.addPotionEffect(
						new PotionEffect(PotionEffectType.SLOW, Calculator.secondsToTicks(finalDuration), finalAmp)));
			}
			if (amp2 != -1) {
				int finalAmp1 = amp2;
				Double finalDuration1 = duration;
				WorldManager
					.getNearbyMonsters(player, range)
					.forEach(ent -> ent.addPotionEffect(
						new PotionEffect(PotionEffectType.WEAKNESS, Calculator.secondsToTicks(finalDuration1),
							finalAmp1
						)));
			}
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				arena
					.getActives()
					.forEach(vdPlayer -> vdPlayer
						.getPlayer()
						.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1.25f));
		}

		// Monk
		else if (MonkAbility.matches(item)) {
			// Calculate effect
			int amplifier;
			if (effect
				.get()
				.contains("40"))
				amplifier = 1;
			else if (effect
				.get()
				.contains("80"))
				amplifier = 3;
			else if (effect
				.get()
				.contains("100"))
				amplifier = 4;
			else return;

			// Activate ability
			WorldManager
				.getNearbyPlayers(player, range)
				.forEach(player1 -> player1.addPotionEffect(
					new PotionEffect(PotionEffectType.FAST_DIGGING, Calculator.secondsToTicks(altDuration),
						amplifier)));
			WorldManager
				.getNearbyAllies(player, range)
				.forEach(ally -> ally.addPotionEffect(
					new PotionEffect(PotionEffectType.FAST_DIGGING, Calculator.secondsToTicks(altDuration),
						amplifier)));
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
				Calculator.secondsToTicks(duration), amplifier
			));
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				arena
					.getActives()
					.forEach(vdPlayer -> vdPlayer
						.getPlayer()
						.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
		}

		// Messenger
		else if (MessengerAbility.matches(item)) {
			// Calculate effect
			int amplifier;
			if (effect
				.get()
				.contains("20"))
				amplifier = 0;
			else if (effect
				.get()
				.contains("40"))
				amplifier = 1;
			else if (effect
				.get()
				.contains("60"))
				amplifier = 2;
			else return;

			// Activate ability
			WorldManager
				.getNearbyPlayers(player, range)
				.forEach(player1 -> player1.addPotionEffect(
					new PotionEffect(PotionEffectType.SPEED, Calculator.secondsToTicks(altDuration), amplifier)));
			WorldManager
				.getNearbyAllies(player, range)
				.forEach(ally -> ally.addPotionEffect(
					new PotionEffect(PotionEffectType.SPEED, Calculator.secondsToTicks(altDuration), amplifier)));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Calculator.secondsToTicks(duration),
				amplifier
			));
			gamer.triggerAbilityCooldown(Calculator.secondsToMillis(cooldown));

			// Fire ability sound if turned on
			if (arena.hasAbilitySound())
				arena
					.getActives()
					.forEach(vdPlayer -> vdPlayer
						.getPlayer()
						.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
		}
	}
}
