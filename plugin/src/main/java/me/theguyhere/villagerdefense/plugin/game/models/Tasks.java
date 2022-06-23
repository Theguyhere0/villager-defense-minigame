package me.theguyhere.villagerdefense.plugin.game.models;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryID;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryType;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.events.WaveEndEvent;
import me.theguyhere.villagerdefense.plugin.events.WaveStartEvent;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import me.theguyhere.villagerdefense.plugin.tools.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class Tasks {
	private final Arena arena;
	/** Maps runnables to ID of the currently running runnable.*/
	private final Map<Runnable, Integer> tasks = new HashMap<>();

	public Tasks(Arena arena) {
		this.arena = arena;
	}

	public Map<Runnable, Integer> getTasks() {
		return tasks;
	}

	// Waiting for enough players message
	public final Runnable waiting = new Runnable() {
		@Override
		public void run() {
			arena.getPlayers().forEach(player ->
				PlayerManager.notifyAlert(player.getPlayer(), LanguageManager.messages.waitingForPlayers));
			CommunicationManager.debugInfo(arena.getName() + " is currently waiting for players to start.",
					2);
		}
	};

	// 2 minute warning
	public final Runnable min2 = new Runnable() {
		@Override
		public void run() {
			arena.getPlayers().forEach(player ->
					PlayerManager.notifyAlert(
							player.getPlayer(),
							LanguageManager.messages.minutesLeft,
							new ColoredMessage(ChatColor.AQUA, "2")
					));
			CommunicationManager.debugInfo(arena.getName() + " is starting in 2 minutes.", 2);
		}
	};

	// 1 minute warning
	public final Runnable min1 = new Runnable() {
		@Override
		public void run() {
			arena.getPlayers().forEach(player ->
					PlayerManager.notifyAlert(
							player.getPlayer(),
							LanguageManager.messages.minutesLeft,
							new ColoredMessage(ChatColor.AQUA, "1")
					));
			CommunicationManager.debugInfo(arena.getName() + " is starting in 1 minute.", 2);
		}
	};

	// 30 second warning
	public final Runnable sec30 = new Runnable() {
		@Override
		public void run() {
			arena.getPlayers().forEach(player ->
					PlayerManager.notifyAlert(
							player.getPlayer(),
							LanguageManager.messages.secondsLeft,
							new ColoredMessage(ChatColor.AQUA, "30")
					));
			CommunicationManager.debugInfo(arena.getName() + " is starting in 30 seconds.", 2);
		}
	};

	// 10 second warning
	public final Runnable sec10 = new Runnable() {
		@Override
		public void run() {
			arena.getPlayers().forEach(player ->
					PlayerManager.notifyAlert(
							player.getPlayer(),
							LanguageManager.messages.secondsLeft,
							new ColoredMessage(ChatColor.AQUA, "10")
					));
			CommunicationManager.debugInfo(arena.getName() + " is starting in 10 seconds.", 2);
		}
	};

	// 10 second warning when full
	public final Runnable full10 = new Runnable() {
		@Override
		public void run() {
			arena.getPlayers().forEach(player -> {
				PlayerManager.notifyAlert(player.getPlayer(), LanguageManager.messages.maxCapacity);
				PlayerManager.notifyAlert(
						player.getPlayer(),
						LanguageManager.messages.secondsLeft,
						new ColoredMessage(ChatColor.AQUA, "10")
				);
			});
			CommunicationManager.debugInfo(arena.getName() + " is full and is starting in 10 seconds.",
					2);
		}

	};

	// 5 second warning
	public final Runnable sec5 = new Runnable() {
		@Override
		public void run() {
			arena.getPlayers().forEach(player ->
					PlayerManager.notifyAlert(
							player.getPlayer(),
							LanguageManager.messages.secondsLeft,
							new ColoredMessage(ChatColor.AQUA, "5")
					));
			CommunicationManager.debugInfo(arena.getName() + " is starting in 5 seconds.", 2);

		}
	};

	// Start actual game
	public final Runnable start = new Runnable() {

		@Override
		public void run() {
			// Set arena to active, reset villager and enemy count, set new game ID, clear arena
			arena.setStatus(ArenaStatus.ACTIVE);
			arena.resetVillagers();
			arena.resetEnemies();
			arena.newGameID();
			WorldManager.clear(arena.getCorner1(), arena.getCorner2());

			// Teleport players to arena if waiting room exists, otherwise clear inventory
			if (arena.getWaitingRoom() != null) {
				for (VDPlayer vdPlayer : arena.getActives())
					PlayerManager.teleAdventure(vdPlayer.getPlayer(), arena.getPlayerSpawn().getLocation());
				for (VDPlayer player : arena.getSpectators())
					PlayerManager.teleSpectator(player.getPlayer(), arena.getPlayerSpawn().getLocation());
			} else {
				for (VDPlayer vdPlayer : arena.getActives())
					vdPlayer.getPlayer().getInventory().clear();
			}

			// Stop waiting sound
			if (arena.getWaitingSound() != null)
				arena.getPlayers().forEach(player ->
						player.getPlayer().stopSound(arena.getWaitingSound()));

			// Start particles if enabled
			if (arena.hasSpawnParticles())
				arena.startSpawnParticles();
			if (arena.hasMonsterParticles())
				arena.startMonsterParticles();
			if (arena.hasVillagerParticles())
				arena.startVillagerParticles();
			if (arena.hasBorderParticles())
				arena.startBorderParticles();

			arena.getActives().forEach(player -> {
				FileConfiguration playerData = Main.plugin.getPlayerData();
				String path = player.getPlayer().getUniqueId() + ".achievements";
				Kit second;

				// Give second kit to players with two kit bonus
				if (playerData.contains(path) && player.isBoosted() &&
						playerData.getStringList(path).contains(Achievement.allKits().getID()))
					do {
						second = Kit.randomKit();

						// Single tier kits
						if (!second.isMultiLevel())
							second.setKitLevel(1);

						// Multiple tier kits
						else second.setKitLevel(playerData.getInt(player.getPlayer().getUniqueId() + ".kits." +
								second.getName()));

						player.setKit2(second);
					} while (second.equals(player.getKit()));

				// Give all players starting items
				giveItems(player);

				// Give admins items or events to test with
				if (CommunicationManager.getDebugLevel() >= 3 && player.getPlayer().hasPermission("vd.admin")) {
				}

				Random r = new Random();

				// Set health for people with giant kits
				if ((Kit.giant().setKitLevel(1).equals(player.getKit()) ||
						Kit.giant().setKitLevel(1).equals(player.getKit2())) && !player.isSharing())
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
				else if ((Kit.giant().setKitLevel(2).equals(player.getKit()) ||
						Kit.giant().setKitLevel(2).equals(player.getKit2())) && !player.isSharing())
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant2", 4,
									AttributeModifier.Operation.ADD_NUMBER));
				else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT2))) {
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant2", 4,
									AttributeModifier.Operation.ADD_NUMBER));
					PlayerManager.notifySuccess(player.getPlayer(), LanguageManager.messages.effectShare);
				}
				else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT1))) {
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
					PlayerManager.notifySuccess(player.getPlayer(), LanguageManager.messages.effectShare);
				}

				// Set health for people with health boost and are boosted
				if (playerData.contains(path) && player.isBoosted() &&
						playerData.getStringList(path).contains(Achievement.topWave9().getID()))
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("HealthBoost", 2,
									AttributeModifier.Operation.ADD_NUMBER));

				// Set health for people with dwarf challenge
				if (player.getChallenges().contains(Challenge.dwarf()))
					Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Dwarf", -.5,
									AttributeModifier.Operation.MULTIPLY_SCALAR_1));

				// Make sure new health is set up correctly
				player.getPlayer().setHealth(
						Objects.requireNonNull(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
								.getValue());

				// Give blindness to people with that challenge
				if (player.getChallenges().contains(Challenge.blind()))
					player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999,
							0));

				// Give Traders their gems
				if (Kit.trader().setKitLevel(1).equals(player.getKit()) ||
						Kit.trader().setKitLevel(1).equals(player.getKit2()))
					player.addGems(200);

				// Give gems from crystal conversion
				path = player.getPlayer().getUniqueId() + ".crystalBalance";
				player.addGems(player.getGemBoost());
				playerData.set(path, playerData.getInt(path) - player.getGemBoost() * 5);
				Main.plugin.savePlayerData();
			});

			// Initiate community chest
			arena.setCommunityChest(Bukkit.createInventory(
					new InventoryMeta(InventoryID.COMMUNITY_CHEST_INVENTORY, InventoryType.CONTROLLED, arena),
					54,
					CommunicationManager.format("&d&l" + LanguageManager.names.communityChest)
			));

			// Initiate shops
			arena.setWeaponShop(Inventories.createWeaponShopMenu(1, arena));
			arena.setArmorShop(Inventories.createArmorShopMenu(1, arena));
			arena.setConsumeShop(Inventories.createConsumableShopMenu(1, arena));

			// Start dialogue, then trigger WaveEndEvent
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
				for (VDPlayer player : arena.getPlayers()) {
					PlayerManager.namedNotify(
							player.getPlayer(),
							new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
							new ColoredMessage(LanguageManager.messages.villageCaptainDialogue1)
					);
				}
			});
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
				for (VDPlayer player : arena.getPlayers()) {
					PlayerManager.namedNotify(
							player.getPlayer(),
							new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
							new ColoredMessage(LanguageManager.messages.villageCaptainDialogue2),
							new ColoredMessage(ChatColor.AQUA, arena.getName()),
							new ColoredMessage(ChatColor.AQUA, LanguageManager.names.crystals)
					);
				}
			}, Utils.secondsToTicks(5));
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
				for (VDPlayer player : arena.getPlayers()) {
					PlayerManager.namedNotify(
							player.getPlayer(),
							new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
							new ColoredMessage(LanguageManager.messages.villageCaptainDialogue3)
					);
				}
			}, Utils.secondsToTicks(11));
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
				for (VDPlayer player : arena.getPlayers()) {
					PlayerManager.namedNotify(
							player.getPlayer(),
							new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
							new ColoredMessage(LanguageManager.messages.villageCaptainDialogue4),
							new ColoredMessage(ChatColor.AQUA, "/vd leave")
					);
				}
			}, Utils.secondsToTicks(18));
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
				for (VDPlayer player : arena.getPlayers()) {
					PlayerManager.namedNotify(
							player.getPlayer(),
							new ColoredMessage(ChatColor.DARK_GREEN, LanguageManager.names.villageCaptain),
							new ColoredMessage(LanguageManager.messages.villageCaptainDialogue5),
							new ColoredMessage(ChatColor.AQUA, arena.getName())
					);
				}
			}, Utils.secondsToTicks(25));
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveEndEvent(arena)), Utils.secondsToTicks(30));

			// Debug message to console
			CommunicationManager.debugInfo(arena.getName() + " is starting.", 2);
		}
	};

	// Start a new wave
	public final Runnable wave = new Runnable() {

		@Override
		public void run() {
			// Increment wave
			arena.incrementCurrentWave();
			int currentWave = arena.getCurrentWave();

			// Refresh the scoreboards
			updateBoards.run();

			// Remove any unwanted mobs
			Objects.requireNonNull(arena.getCorner1().getWorld()).getNearbyEntities(arena.getBounds())
					.stream().filter(Objects::nonNull)
					.filter(ent -> ent instanceof Monster || ent instanceof Hoglin || ent instanceof Phantom ||
							ent instanceof Slime)
					.filter(ent -> (!ent.hasMetadata("game") ||
							ent.getMetadata("game").get(0).asInt() != arena.getGameID()))
					.forEach(System.out::println);
			Objects.requireNonNull(arena.getCorner1().getWorld()).getNearbyEntities(arena.getBounds())
					.stream().filter(Objects::nonNull)
					.filter(ent -> ent instanceof Monster || ent instanceof Hoglin || ent instanceof Phantom ||
							ent instanceof Slime)
					.filter(ent -> (!ent.hasMetadata("wave") ||
							ent.getMetadata("wave").get(0).asInt() != arena.getCurrentWave()))
					.forEach(Entity::remove);

			// Revive dead players
			for (VDPlayer p : arena.getGhosts()) {
				PlayerManager.teleAdventure(p.getPlayer(), arena.getPlayerSpawn().getLocation());
				p.setStatus(PlayerStatus.ALIVE);
				giveItems(p);

				Random r = new Random();

				// Set health for people with giant kits
				if ((Kit.giant().setKitLevel(1).equals(p.getKit()) ||
						Kit.giant().setKitLevel(1).equals(p.getKit2())) && !p.isSharing())
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
				else if ((Kit.giant().setKitLevel(2).equals(p.getKit()) ||
						Kit.giant().setKitLevel(2).equals(p.getKit2())) && !p.isSharing())
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant2", 4,
									AttributeModifier.Operation.ADD_NUMBER));
				else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT2))) {
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant2", 4,
									AttributeModifier.Operation.ADD_NUMBER));
					PlayerManager.notifySuccess(p.getPlayer(), LanguageManager.messages.effectShare);
				}
				else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT1))) {
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
					PlayerManager.notifySuccess(p.getPlayer(), LanguageManager.messages.effectShare);
				}

				// Set health for people with health boost and are boosted
				FileConfiguration playerData = Main.plugin.getPlayerData();
				String path = p.getPlayer().getUniqueId() + ".achievements";

				if (playerData.contains(path) && p.isBoosted() &&
						playerData.getStringList(path).contains(Achievement.topWave9().getID()))
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("HealthBoost", 2,
									AttributeModifier.Operation.ADD_NUMBER));

				// Set health for people with dwarf challenge
				if (p.getChallenges().contains(Challenge.dwarf()))
					Objects.requireNonNull(p.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
							.addModifier(new AttributeModifier("Dwarf", -.5,
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

			arena.getActives().forEach(p -> {
				// Notify of upcoming wave
				if (currentWave != 1)
					p.getPlayer().sendTitle(CommunicationManager.format("&6" +
							String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
							CommunicationManager.format("&7" + String.format(LanguageManager.messages.starting,
									"&b15&7")),
							Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));
				else p.getPlayer().sendTitle(CommunicationManager.format("&6" +
								String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
						" ", Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

				// Give players gem rewards
				int multiplier;
				switch (arena.getDifficultyMultiplier()) {
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
					PlayerManager.notifySuccess(
							p.getPlayer(),
							LanguageManager.messages.gemsReceived,
							new ColoredMessage(ChatColor.AQUA, Integer.toString(reward))
					);
				GameManager.createBoard(p);
			});

			// Notify spectators of upcoming wave
			if (currentWave != 1)
				arena.getSpectators().forEach(p ->
						p.getPlayer().sendTitle(CommunicationManager.format("&6" +
								String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
								CommunicationManager.format("&7" +
										String.format(LanguageManager.messages.starting, "&b15&7")),
								Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));
			else arena.getSpectators().forEach(p ->
					p.getPlayer().sendTitle(CommunicationManager.format("&6" +
									String.format(LanguageManager.messages.waveNum, Integer.toString(currentWave))),
							" ", Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));

			// Regenerate shops when time and notify players of it
			if (currentWave % 10 == 0 && currentWave != 0) {
				int level = currentWave / 10 + 1;
				arena.setWeaponShop(Inventories.createWeaponShopMenu(level, arena));
				arena.setArmorShop(Inventories.createArmorShopMenu(level, arena));
				arena.setConsumeShop(Inventories.createConsumableShopMenu(level, arena));
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.getActives().forEach(player ->
						player.getPlayer().sendTitle(CommunicationManager.format(
								"&6" + LanguageManager.messages.shopUpgrade),
								"&7" + CommunicationManager.format(
										String.format(LanguageManager.messages.shopInfo, "10")),
								Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5),
								Utils.secondsToTicks(1))), Utils.secondsToTicks(4));
			}

			// Spawn mobs after 15 seconds if not first wave
			if (currentWave != 1)
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
						Bukkit.getPluginManager().callEvent(new WaveStartEvent(arena)), Utils.secondsToTicks(15));
			else Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveStartEvent(arena)));

			// Debug message to console
			CommunicationManager.debugInfo("Starting wave " + currentWave + " for " + arena.getName(), 2);
		}
	};

	// Calibrate the arena
	public final Runnable calibrate = new Runnable() {
		@Override
		public void run() {
			arena.calibrate();
			CommunicationManager.debugInfo(arena.getName() + " performed a calibration check.", 2);
		}
	};

	// Kick players from the arena
	public final Runnable kickPlayers = new Runnable() {
		@Override
		public void run() {
			// Remove players from the arena
			arena.getPlayers().forEach(player ->
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
							Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player.getPlayer()))));
		}
	};

	// Reset the arena
	public final Runnable reset = new Runnable() {
		@Override
		public void run() {
			// Update data
			arena.setStatus(ArenaStatus.WAITING);
			arena.resetCurrentWave();
			arena.resetEnemies();
			arena.resetVillagers();
			arena.resetGolems();
			arena.getTask().getTasks().clear();

			// Clear the arena
			WorldManager.clear(arena.getCorner1(), arena.getCorner2());

			// Remove particles
			arena.cancelSpawnParticles();
			arena.cancelMonsterParticles();
			arena.cancelVillagerParticles();
			arena.cancelBorderParticles();

			// Refresh portal
			arena.refreshPortal();

			// Debug message to console
			CommunicationManager.debugInfo(arena.getName() + " is resetting.", 2);
		}
	};

	// Update active player scoreboards
	public final Runnable updateBoards = new Runnable() {
		@Override
		public void run() {
			arena.getActives().forEach(GameManager::createBoard);
		}
	};

	// Update time limit bar
	public final Runnable updateBar = new Runnable() {
		double progress = 1;
		double time;
		boolean messageSent;

		@Override
		public void run() {
			// Get proper multiplier
			double multiplier = 1 + .2 * ((int) arena.getCurrentDifficulty() - 1);
			if (!arena.hasDynamicLimit())
				multiplier = 1;

			// Add time limit bar if it doesn't exist
			if (arena.getTimeLimitBar() == null) {
				progress = 1;
				arena.startTimeLimitBar();
				arena.getPlayers().forEach(vdPlayer ->
						arena.addPlayerToTimeLimitBar(vdPlayer.getPlayer()));
				time = 1d / Utils.minutesToSeconds(arena.getWaveTimeLimit() * multiplier);
				messageSent = false;

				// Debug message to console
				CommunicationManager.debugInfo("Adding time limit bar to " + arena.getName(), 2);
			}

			// Trigger wave end event
			else if (progress <= 0) {
				progress = 0;
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
						Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
			}

			// Decrement time limit bar
			else {
				if (progress <= time * Utils.minutesToSeconds(1)) {
					arena.updateTimeLimitBar(BarColor.RED, progress);
					if (!messageSent) {
						// Send warning
						arena.getActives().forEach(player ->
								player.getPlayer().sendTitle(CommunicationManager.format(
										"&c" + LanguageManager.messages.oneMinuteWarning),
										null, Utils.secondsToTicks(.5), Utils.secondsToTicks(1.5),
										Utils.secondsToTicks(.5)));

						// Set monsters glowing when time is low
						arena.setMonsterGlow();

						messageSent = true;
					}
				} else arena.updateTimeLimitBar(progress);
				progress -= time;
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
			else PlayerManager.giveItem(player.getPlayer(), item, LanguageManager.errors.inventoryFull);
		}
		if (player.getKit2() != null)
			for (ItemStack item: player.getKit2().getItems()) {
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
				else PlayerManager.giveItem(player.getPlayer(), item, LanguageManager.errors.inventoryFull);
			}
		PlayerManager.giveItem(player.getPlayer(), GameItems.shop(), LanguageManager.errors.inventoryFull);
	}
}
