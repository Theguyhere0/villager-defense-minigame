package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.GameEndEvent;
import me.theguyhere.villagerdefense.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.events.WaveEndEvent;
import me.theguyhere.villagerdefense.events.WaveStartEvent;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.game.models.kits.Kit;
import me.theguyhere.villagerdefense.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
			plugin.getGame().arenas.get(arena).getPlayers().forEach(player ->
				player.getPlayer().sendMessage(Utils.notify(plugin.getLanguageData().getString("waiting"))));
			plugin.debugInfo("Arena " + arena + " is currently waiting for players to start.", 2);
		}
	};

	// 2 minute warning
	public final Runnable min2 = new Runnable() {
		@Override
		public void run() {
			try {
				plugin.getGame().arenas.get(arena).getPlayers().forEach(player ->
						player.getPlayer().sendMessage(Utils.notify(String.format(
								plugin.getLanguageData().getString("minutesLeft"), 2))));
			} catch (Exception e) {
				plugin.debugError("The key 'minutesLeft' is missing or corrupt in the active language file",
						1);
			}
			plugin.debugInfo("Arena " + arena + " is starting in 2 minutes.", 2);
		}
	};

	// 1 minute warning
	public final Runnable min1 = new Runnable() {
		@Override
		public void run() {
			try {
				plugin.getGame().arenas.get(arena).getPlayers().forEach(player ->
						player.getPlayer().sendMessage(Utils.notify(String.format(
								plugin.getLanguageData().getString("minutesLeft"), 1))));
			} catch (Exception e) {
				plugin.debugError("The key 'minutesLeft' is missing or corrupt in the active language file",
						1);
			}
			plugin.debugInfo("Arena " + arena + " is starting in 1 minute.", 2);
		}
	};

	// 30 second warning
	public final Runnable sec30 = new Runnable() {
		@Override
		public void run() {
			try {
				plugin.getGame().arenas.get(arena).getPlayers().forEach(player ->
						player.getPlayer().sendMessage(Utils.notify(String.format(
								plugin.getLanguageData().getString("secondsLeft"), 30))));
			} catch (Exception e) {
				plugin.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			plugin.debugInfo("Arena " + arena + " is starting in 30 seconds.", 2);
		}
	};

	// 10 second warning
	public final Runnable sec10 = new Runnable() {
		@Override
		public void run() {
			try {
				plugin.getGame().arenas.get(arena).getPlayers().forEach(player ->
						player.getPlayer().sendMessage(Utils.notify(String.format(
								plugin.getLanguageData().getString("secondsLeft"), 10))));
			} catch (Exception e) {
				plugin.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			plugin.debugInfo("Arena " + arena + " is starting in 10 seconds.", 2);
		}
	};

	// 10 second warning when full
	public final Runnable full10 = new Runnable() {
		@Override
		public void run() {
			try {
				plugin.getGame().arenas.get(arena).getPlayers().forEach(player -> {
					player.getPlayer().sendMessage(Utils.notify(plugin.getLanguageData().getString("maxCapacity")));
					player.getPlayer().sendMessage(Utils.notify(String.format(
							plugin.getLanguageData().getString("secondsLeft"), 10)));
				});
			} catch (Exception e) {
				plugin.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			plugin.debugInfo("Arena " + arena + " is full and is starting in 10 seconds.", 2);
		}

	};

	// 5 second warning
	public final Runnable sec5 = new Runnable() {
		@Override
		public void run() {
			try {
				plugin.getGame().arenas.get(arena).getPlayers().forEach(player ->
						player.getPlayer().sendMessage(Utils.notify(String.format(
								plugin.getLanguageData().getString("secondsLeft"), 5))));
			} catch (Exception e) {
				plugin.debugError("The key 'secondsLeft' is missing or corrupt in the active language file",
						1);
			}
			plugin.debugInfo("Arena " + arena + " is starting in 5 seconds.", 2);

		}
	};

	// Start a new wave
	public final Runnable wave = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = plugin.getGame().arenas.get(arena);
			FileConfiguration language = plugin.getLanguageData();

			// Increment wave
			arenaInstance.incrementCurrentWave();
			int currentWave = arenaInstance.getCurrentWave();

			// Refresh the portal hologram and scoreboards
			plugin.getPortal().refreshHolo(arenaInstance.getArena(), plugin.getGame());
			updateBoards.run();

			// Revive dead players
			arenaInstance.getGhosts().forEach(p -> {
				Utils.teleAdventure(p.getPlayer(), arenaInstance.getPlayerSpawn());
				p.setStatus(PlayerStatus.ALIVE);
				giveItems(p);

				// Set health for people with giant kits
				if (p.getKit().equals(Kit.giant().setKitLevel(1)))
					p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
				else if (p.getKit().equals(Kit.giant().setKitLevel(2)))
					p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
							.addModifier(new AttributeModifier("Giant1", 4,
									AttributeModifier.Operation.ADD_NUMBER));
			});

			arenaInstance.getActives().forEach(p -> {
				// Notify of upcoming wave
				try {
					p.getPlayer().sendTitle(Utils.format(String.format(plugin.getLanguageData().getString("wave"),
									currentWave)), Utils.format(plugin.getLanguageData().getString("starting")),
							Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));
				} catch (Exception e) {
					plugin.debugError("The key 'starting' is either missing or corrupt in the active language file",
							1);
				}
				// Give players gem rewards
				int multiplier;
				switch (arenaInstance.getDifficultyMultiplier()) {
					case 1:
						multiplier = 10;
						break;
					case 2:
						multiplier = 8;
						break;
					case 3:
						multiplier = 6;
						break;
					default:
						multiplier = 5;
				}
				int reward = (currentWave - 1) * multiplier;
				p.addGems(reward);
				if (currentWave > 1)
					try {
						p.getPlayer().sendMessage(Utils.notify(String.format(language.getString("gems"), reward)));
					} catch (Exception e) {
						plugin.debugError("The key 'gems' is either missing or corrupt in the active language file",
								1);
					}
				plugin.getGame().createBoard(p);
			});

			// Notify spectators of upcoming wave
			try {
				arenaInstance.getSpectators().forEach(p ->
						p.getPlayer().sendTitle(Utils.format(String.format(language.getString("wave"), currentWave)),
								Utils.format(language.getString("starting")), Utils.secondsToTicks(.5),
								Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));
			} catch (Exception e) {
				plugin.debugError("The key 'wave' is either missing or corrupt in the active language file",
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
									player.getPlayer().sendTitle(Utils.format(language.getString("shopUpgrade")),
											Utils.format(language.getString("shopInfo")),
											Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5),
											Utils.secondsToTicks(1))), Utils.secondsToTicks(4));
			}

			// Spawns mobs after 15 seconds
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveStartEvent(arenaInstance)),
					Utils.secondsToTicks(15));

			// Debug message to console
			plugin.debugInfo("Starting wave " + currentWave + " for Arena " + arena, 2);
		}
	};
	
	// Start actual game
	public final Runnable start = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = plugin.getGame().arenas.get(arena);

			// Teleport players to arena if waiting room exists
			if (arenaInstance.getWaitingRoom() != null) {
				arenaInstance.getActives().forEach(player ->
						Utils.teleAdventure(player.getPlayer(), arenaInstance.getPlayerSpawn()));
				arenaInstance.getSpectators().forEach(player ->
						Utils.teleSpectator(player.getPlayer(), arenaInstance.getPlayerSpawn()));
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

			arenaInstance.getActives().forEach(player -> {
				// Give all players starting items
				giveItems(player);

				// Give me items to test with
				if (plugin.getDebugLevel() >= 3 && player.getPlayer().getName().equals("Theguyhere")) {
					Utils.giveItem(player.getPlayer(), GameItems.wolf(), "uh oh");
					Utils.giveItem(player.getPlayer(), GameItems.golem(), "uh oh");
				}

				// Set health for people with giant kits
				if (player.getKit().equals(Kit.giant().setKitLevel(1)))
					player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
				else if (player.getKit().equals(Kit.giant().setKitLevel(2)))
					player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
							.addModifier(new AttributeModifier("Giant1", 4,
									AttributeModifier.Operation.ADD_NUMBER));
			});

			// Set arena to active and reset villager and enemy count
			arenaInstance.setStatus(ArenaStatus.ACTIVE);
			arenaInstance.resetVillagers();
			arenaInstance.resetEnemies();

			// Initiate community chest
			arenaInstance.setCommunityChest(Bukkit.createInventory(new InventoryMeta(arena), 54,
					Utils.format("&k") + Utils.format("&d&lCommunity Chest")));

			// Trigger WaveEndEvent
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveEndEvent(arenaInstance)));

			// Debug message to console
			plugin.debugInfo("Arena " + arena + " is starting.", 2);
		}
	};

	// Reset the arena
	public final Runnable reset = new Runnable() {
		@Override
		public void run() {
			Arena arenaInstance = plugin.getGame().arenas.get(arena);

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
			Utils.clear(arenaInstance.getCorner1(), arenaInstance.getCorner2());

			// Remove particles
			arenaInstance.cancelSpawnParticles();
			arenaInstance.cancelMonsterParticles();
			arenaInstance.cancelVillagerParticles();

			// Refresh portal
			plugin.getPortal().refreshHolo(arenaInstance.getArena(), plugin.getGame());

			// Debug message to console
			plugin.debugInfo("Arena " + arena + " is resetting.", 2);
		}
	};

	// Update active player scoreboards
	public final Runnable updateBoards = new Runnable() {
		@Override
		public void run() {
			plugin.getGame().arenas.get(arena).getActives().forEach(plugin.getGame()::createBoard);
		}
	};

	// Update time limit bar
	public final Runnable updateBar = new Runnable() {
		double progress = 1;
		double time;
		boolean messageSent;

		@Override
		public void run() {
			Arena arenaInstance = plugin.getGame().arenas.get(arena);

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
				plugin.debugInfo("Adding time limit bar to Arena " + arenaInstance, 2);
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
									player.getPlayer().sendTitle(Utils.format(
											plugin.getLanguageData().getString("minuteWarning")), null,
											Utils.secondsToTicks(.5), Utils.secondsToTicks(1.5),
											Utils.secondsToTicks(.5)));

							// Set monsters glowing when time is low
							arenaInstance.getPlayerSpawn().getWorld().getNearbyEntities(arenaInstance.getPlayerSpawn(),
									200, 200, 200).stream().filter(entity -> entity.hasMetadata("VD"))
									.filter(entity -> entity instanceof Monster || entity instanceof Slime ||
											entity instanceof Hoglin || entity instanceof Phantom)
									.forEach(entity -> entity.setGlowing(true));
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
					equipment.getHelmet() == null)
				equipment.setHelmet(item);
			else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
					equipment.getChestplate() == null)
				equipment.setChestplate(item);
			else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
					equipment.getLeggings() == null)
				equipment.setLeggings(item);
			else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == item.getType()) &&
					equipment.getBoots() == null)
				equipment.setBoots(item);
			else Utils.giveItem(player.getPlayer(), item, plugin.getLanguageData().getString("inventoryFull"));
		}
		Utils.giveItem(player.getPlayer(), GameItems.shop(), plugin.getLanguageData().getString("inventoryFull"));
	}
}
