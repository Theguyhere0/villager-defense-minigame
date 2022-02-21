package me.theguyhere.villagerdefense.plugin.game.models;

import me.theguyhere.villagerdefense.plugin.GUI.Inventories;
import me.theguyhere.villagerdefense.plugin.GUI.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.events.WaveEndEvent;
import me.theguyhere.villagerdefense.plugin.events.WaveStartEvent;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.tools.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
public class Tasks {
	private final Main plugin;
	private final int arena;
	/** Maps runnables to ID of the currently running runnable.*/
	private final Map<Runnable, Integer> tasks = new HashMap<>();

	public Tasks(Main plugin, int arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	public Map<Runnable, Integer> getTasks() {
		return tasks;
	}

	// Waiting for enough players message
	public final Runnable waiting = new Runnable() {
		@Override
		public void run() {
			ArenaManager.arenas[arena].getPlayers().forEach(player ->
				PlayerManager.notify(player.getPlayer(), plugin.getLanguageData().getString("waiting")));
			CommunicationManager.debugInfo("Arena " + arena + " is currently waiting for players to start.", 2);
		}
	};

	// 2 minute warning
	public final Runnable min2 = new Runnable() {
		@Override
		public void run() {
			try {
				ArenaManager.arenas[arena].getPlayers().forEach(player ->
						PlayerManager.notify(player.getPlayer(), String.format(
								Objects.requireNonNull(plugin.getLanguageData().getString("minutesLeft")), 2)));
			} catch (Exception e) {
				CommunicationManager.debugError("The key 'minutesLeft' is missing or corrupt in the active language file",
						1);
			}
			CommunicationManager.debugInfo("Arena " + arena + " is starting in 2 minutes.", 2);
		}
	};

	// 1 minute warning
	public final Runnable min1 = new Runnable() {
		@Override
		public void run() {
			try {
				ArenaManager.arenas[arena].getPlayers().forEach(player ->
						PlayerManager.notify(player.getPlayer(), String.format(
								Objects.requireNonNull(plugin.getLanguageData().getString("minutesLeft")), 1)));
			} catch (Exception e) {
				CommunicationManager.debugError("The key 'minutesLeft' is missing or corrupt in the active language file",
						1);
			}
			CommunicationManager.debugInfo("Arena " + arena + " is starting in 1 minute.", 2);
		}
	};

	// 30 second warning
	public final Runnable sec30 = new Runnable() {
		@Override
		public void run() {
			try {
				ArenaManager.arenas[arena].getPlayers().forEach(player ->
						PlayerManager.notify(player.getPlayer(), String.format(
								Objects.requireNonNull(plugin.getLanguageData().getString("secondsLeft")), 30)));
			} catch (Exception e) {
				CommunicationManager.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			CommunicationManager.debugInfo("Arena " + arena + " is starting in 30 seconds.", 2);
		}
	};

	// 10 second warning
	public final Runnable sec10 = new Runnable() {
		@Override
		public void run() {
			try {
				ArenaManager.arenas[arena].getPlayers().forEach(player ->
						PlayerManager.notify(player.getPlayer(), String.format(
								Objects.requireNonNull(plugin.getLanguageData().getString("secondsLeft")), 10)));
			} catch (Exception e) {
				CommunicationManager.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			CommunicationManager.debugInfo("Arena " + arena + " is starting in 10 seconds.", 2);
		}
	};

	// 10 second warning when full
	public final Runnable full10 = new Runnable() {
		@Override
		public void run() {
			try {
				ArenaManager.arenas[arena].getPlayers().forEach(player -> {
					PlayerManager.notify(player.getPlayer(), plugin.getLanguageData().getString("maxCapacity"));
					PlayerManager.notify(player.getPlayer(), String.format(
							Objects.requireNonNull(plugin.getLanguageData().getString("secondsLeft")), 10));
				});
			} catch (Exception e) {
				CommunicationManager.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			CommunicationManager.debugInfo("Arena " + arena + " is full and is starting in 10 seconds.", 2);
		}

	};

	// 5 second warning
	public final Runnable sec5 = new Runnable() {
		@Override
		public void run() {
			try {
				ArenaManager.arenas[arena].getPlayers().forEach(player ->
						PlayerManager.notify(player.getPlayer(), String.format(
								Objects.requireNonNull(plugin.getLanguageData().getString("secondsLeft")), 5)));
			} catch (Exception e) {
				CommunicationManager.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			CommunicationManager.debugInfo("Arena " + arena + " is starting in 5 seconds.", 2);

		}
	};

	// Start actual game
	public final Runnable start = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = ArenaManager.arenas[arena];

			// Set arena to active, reset villager and enemy count, set new game ID, clear arena
			arenaInstance.setStatus(ArenaStatus.ACTIVE);
			arenaInstance.resetVillagers();
			arenaInstance.resetEnemies();
			arenaInstance.newGameID();
			WorldManager.clear(arenaInstance.getCorner1(), arenaInstance.getCorner2());

			// Teleport players to arena if waiting room exists
			if (arenaInstance.getWaitingRoom() != null) {
				for (VDPlayer vdPlayer : arenaInstance.getActives()) {
					PlayerManager.teleAdventure(vdPlayer.getPlayer(), arenaInstance.getPlayerSpawn().getLocation());
				}
				for (VDPlayer player : arenaInstance.getSpectators()) {
					PlayerManager.teleSpectator(player.getPlayer(), arenaInstance.getPlayerSpawn().getLocation());
				}
			}

			// Stop waiting sound
			if (arenaInstance.getWaitingSound() != null)
				arenaInstance.getPlayers().forEach(player ->
						player.getPlayer().stopSound(arenaInstance.getWaitingSound()));

			// Start particles if enabled
			if (arenaInstance.hasSpawnParticles())
				arenaInstance.startSpawnParticles();
			if (arenaInstance.hasMonsterParticles())
				arenaInstance.startMonsterParticles();
			if (arenaInstance.hasVillagerParticles())
				arenaInstance.startVillagerParticles();
			if (arenaInstance.hasBorderParticles())
				arenaInstance.startBorderParticles();

			arenaInstance.getActives().forEach(player -> {
				// Give all players starting items
				giveItems(player);

				// Give admins items or events to test with
				if (CommunicationManager.getDebugLevel() >= 3 && player.getPlayer().hasPermission("vd.admin")) {
				}

				// Give Traders their gems
				if (player.getKit().equals(Kit.trader().setKitLevel(1)))
					player.addGems(200);

				// Set health for people with giant kits
				if (player.getKit().equals(Kit.giant().setKitLevel(1)))
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
				else if (player.getKit().equals(Kit.giant().setKitLevel(2)))
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 4,
									AttributeModifier.Operation.ADD_NUMBER));

				// Set health for people with dwarf challenge
				if (player.getChallenges().contains(Challenge.dwarf()))
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", -.5,
									AttributeModifier.Operation.MULTIPLY_SCALAR_1));

				// Make sure new health is set up correctly
				player.getPlayer().setHealth(
						Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
								.getValue());

				// Give blindness to people with that challenge
				if (player.getChallenges().contains(Challenge.blind()))
					player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999,
							0));
			});

			// Initiate community chest
			arenaInstance.setCommunityChest(Bukkit.createInventory(new InventoryMeta(arena), 54,
					CommunicationManager.format("&k") + CommunicationManager.format("&d&lCommunity Chest")));

			// Trigger WaveEndEvent
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveEndEvent(arenaInstance)));

			// Debug message to console
			CommunicationManager.debugInfo("Arena " + arena + " is starting.", 2);
		}
	};

	// Start a new wave
	public final Runnable wave = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = ArenaManager.arenas[arena];
			FileConfiguration language = plugin.getLanguageData();

			// Increment wave
			arenaInstance.incrementCurrentWave();
			int currentWave = arenaInstance.getCurrentWave();

			// Refresh the scoreboards
			updateBoards.run();

			// Remove any unwanted mobs
			Objects.requireNonNull(arenaInstance.getCorner1().getWorld()).getNearbyEntities(arenaInstance.getBounds())
					.stream().filter(Objects::nonNull)
					.filter(ent -> ent instanceof Monster || ent instanceof Hoglin || ent instanceof Phantom ||
							ent instanceof Slime)
					.filter(ent -> (!ent.hasMetadata("game") ||
							ent.getMetadata("game").get(0).asInt() != arenaInstance.getGameID()))
					.forEach(System.out::println);
			Objects.requireNonNull(arenaInstance.getCorner1().getWorld()).getNearbyEntities(arenaInstance.getBounds())
					.stream().filter(Objects::nonNull)
					.filter(ent -> ent instanceof Monster || ent instanceof Hoglin || ent instanceof Phantom ||
							ent instanceof Slime)
					.filter(ent -> (!ent.hasMetadata("wave") ||
							ent.getMetadata("wave").get(0).asInt() != arenaInstance.getCurrentWave()))
					.forEach(Entity::remove);

			// Revive dead players
			for (VDPlayer p : arenaInstance.getGhosts()) {
				PlayerManager.teleAdventure(p.getPlayer(), arenaInstance.getPlayerSpawn().getLocation());
				p.setStatus(PlayerStatus.ALIVE);
				giveItems(p);

				// Set health for people with giant kits
				if (p.getKit().equals(Kit.giant().setKitLevel(1)))
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
				else if (p.getKit().equals(Kit.giant().setKitLevel(2)))
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 4,
									AttributeModifier.Operation.ADD_NUMBER));

				// Set health for people with dwarf challenge
				if (p.getChallenges().contains(Challenge.dwarf()))
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", -.5,
									AttributeModifier.Operation.MULTIPLY_SCALAR_1));

				// Make sure new health is set up correctly
				p.getPlayer().setHealth(
						Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
								.getValue());

				// Give blindness to people with that challenge
				if (p.getChallenges().contains(Challenge.blind()))
					p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999,
							0));
			}

			arenaInstance.getActives().forEach(p -> {
				// Notify of upcoming wave
				try {
					p.getPlayer().sendTitle(CommunicationManager.format(String.format(
							Objects.requireNonNull(plugin.getLanguageData().getString("wave")), currentWave)),
							CommunicationManager.format(plugin.getLanguageData().getString("starting")),
							Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));
				} catch (Exception e) {
					CommunicationManager.debugError("The key 'starting' is either missing or corrupt in the active language file",
							1);
				}

				// Give players gem rewards
				int multiplier = switch (arenaInstance.getDifficultyMultiplier()) {
					case 1 -> 10;
					case 2 -> 8;
					case 3 -> 6;
					default -> 5;
				};
				int reward = (currentWave - 1) * multiplier;
				p.addGems(reward);
				if (currentWave > 1)
					try {
						PlayerManager.notify(p.getPlayer(), String.format(
								Objects.requireNonNull(language.getString("gems")), reward));
					} catch (Exception e) {
						CommunicationManager.debugError("The key 'gems' is either missing or corrupt in the active language file",
								1);
					}
				ArenaManager.createBoard(p);
			});

			// Notify spectators of upcoming wave
			try {
				arenaInstance.getSpectators().forEach(p ->
						p.getPlayer().sendTitle(CommunicationManager.format(String.format(
								Objects.requireNonNull(language.getString("wave")), currentWave)),
								CommunicationManager.format(language.getString("starting")), Utils.secondsToTicks(.5),
								Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));
			} catch (Exception e) {
				CommunicationManager.debugError("The key 'wave' is either missing or corrupt in the active language file",
						1);
			}

			// Regenerate shops when time and notify players of it
			if (currentWave % 10 == 0 || currentWave == 1) {
				int level = currentWave / 10 + 1;
				arenaInstance.setWeaponShop(Inventories.createWeaponShop(level, arenaInstance));
				arenaInstance.setArmorShop(Inventories.createArmorShop(level, arenaInstance));
				arenaInstance.setConsumeShop(Inventories.createConsumablesShop(level, arenaInstance));
				if (currentWave != 1)
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							arenaInstance.getActives().forEach(player ->
									player.getPlayer().sendTitle(CommunicationManager.format(language.getString("shopUpgrade")),
											CommunicationManager.format(language.getString("shopInfo")),
											Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5),
											Utils.secondsToTicks(1))), Utils.secondsToTicks(4));
			}

			// Spawns mobs after 15 seconds
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveStartEvent(arenaInstance)),
					Utils.secondsToTicks(15));

			// Debug message to console
			CommunicationManager.debugInfo("Starting wave " + currentWave + " for Arena " + arena, 2);
		}
	};

	// Calibrate the arena
	public final Runnable calibrate = new Runnable() {
		@Override
		public void run() {
			ArenaManager.arenas[arena].calibrate();
			CommunicationManager.debugInfo("Arena " + arena + " performed a calibration check.", 2);
		}
	};

	// Reset the arena
	public final Runnable reset = new Runnable() {
		@Override
		public void run() {
			Arena arenaInstance = ArenaManager.arenas[arena];

			// Update data
			arenaInstance.setStatus(ArenaStatus.WAITING);
			arenaInstance.resetCurrentWave();
			arenaInstance.resetEnemies();
			arenaInstance.resetVillagers();
			arenaInstance.resetGolems();
			arenaInstance.getTask().getTasks().clear();

			// Remove players from the arena
			arenaInstance.getPlayers().forEach(player ->
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player.getPlayer()))));

			// Clear the arena
			WorldManager.clear(arenaInstance.getCorner1(), arenaInstance.getCorner2());

			// Remove particles
			arenaInstance.cancelSpawnParticles();
			arenaInstance.cancelMonsterParticles();
			arenaInstance.cancelVillagerParticles();
			arenaInstance.cancelBorderParticles();

			// Refresh portal
			arenaInstance.refreshPortal();

			// Debug message to console
			CommunicationManager.debugInfo("Arena " + arena + " is resetting.", 2);
		}
	};

	// Update active player scoreboards
	public final Runnable updateBoards = new Runnable() {
		@Override
		public void run() {
			ArenaManager.arenas[arena].getActives().forEach(ArenaManager::createBoard);
		}
	};

	// Update time limit bar
	public final Runnable updateBar = new Runnable() {
		double progress = 1;
		double time;
		boolean messageSent;
		Arena arenaInstance;


		@Override
		public void run() {
			arenaInstance = ArenaManager.arenas[arena];

			double multiplier = 1 + .2 * ((int) arenaInstance.getCurrentDifficulty() - 1);
			if (!arenaInstance.hasDynamicLimit())
				multiplier = 1;

			// Add time limit bar if it doesn't exist
			if (arenaInstance.getTimeLimitBar() == null) {
				progress = 1;
				arenaInstance.startTimeLimitBar();
				arenaInstance.getPlayers().forEach(vdPlayer ->
						arenaInstance.addPlayerToTimeLimitBar(vdPlayer.getPlayer()));
				time = 1d / Utils.minutesToSeconds(arenaInstance.getWaveTimeLimit() * multiplier);
				messageSent = false;

				// Debug message to console
				CommunicationManager.debugInfo("Adding time limit bar to Arena " + arena, 2);
			}

			else {
				// Trigger wave end event
				if (progress <= 0) {
					progress = 0;
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new GameEndEvent(arenaInstance)));
				}

				// Decrement time limit bar
				else {
					if (progress <= time * Utils.minutesToSeconds(1)) {
						arenaInstance.updateTimeLimitBar(BarColor.RED, progress);
						if (!messageSent) {
							// Send warning
							arenaInstance.getActives().forEach(player ->
									player.getPlayer().sendTitle(CommunicationManager.format(
											plugin.getLanguageData().getString("minuteWarning")), null,
											Utils.secondsToTicks(.5), Utils.secondsToTicks(1.5),
											Utils.secondsToTicks(.5)));

							// Set monsters glowing when time is low
							arenaInstance.setMonsterGlow();

							messageSent = true;
						}
					} else arenaInstance.updateTimeLimitBar(progress);
					progress -= time;
				}
			}
		}
	};

	// Gives items on spawn or respawn based on kit selected
	public void giveItems(VDPlayer player) {
		for (ItemStack item: player.getKit().getItems()) {
			EntityEquipment equipment = player.getPlayer().getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Arrays.stream(GameItems.HELMET_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
					Objects.requireNonNull(equipment).getHelmet() == null)
				equipment.setHelmet(item);
			else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
					Objects.requireNonNull(equipment).getChestplate() == null)
				equipment.setChestplate(item);
			else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
					Objects.requireNonNull(equipment).getLeggings() == null)
				equipment.setLeggings(item);
			else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
					Objects.requireNonNull(equipment).getBoots() == null)
				equipment.setBoots(item);
			else PlayerManager.giveItem(player.getPlayer(), item, plugin.getLanguageData().getString("inventoryFull"));
		}
		PlayerManager.giveItem(player.getPlayer(), GameItems.shop(), plugin.getLanguageData().getString("inventoryFull"));
	}
}
