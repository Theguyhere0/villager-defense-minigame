package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.GameEndEvent;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.customEvents.WaveStartEvent;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class Tasks {
	private final Main plugin;
	private final Game game;
	private final int arena;
	private final Portal portal;
	// Maps runnables to ID of the currently running runnable
	private final Map<Runnable, Integer> tasks = new HashMap<>();

	public Tasks(Main plugin, Game game, int arena, Portal portal) {
		this.plugin = plugin;
		this.game = game;
		this.arena = arena;
		this.portal = portal;
	}

	public Map<Runnable, Integer> getTasks() {
		return tasks;
	}

	// Waiting for enough players message
	public final Runnable waiting = new Runnable() {
		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
				player.getPlayer().sendMessage(Utils.notify("&6Waiting for more players to start the game.")));
		}
	};

	// 2 minute warning
	public final Runnable min2 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b2 &6minutes until the game starts!")));
		}

	};

	// 1 minute warning
	public final Runnable min1 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b1 &6minute until the game starts!")));
		}
		
	};

	// 30 second warning
	public final Runnable sec30 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b30 &6seconds until the game starts!")));
		}
	};

	// 10 second warning
	public final Runnable sec10 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b10 &6seconds until the game starts!")));
		}
		
	};

	// 10 second warning when full
	public final Runnable full10 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player -> {
					player.getPlayer().sendMessage(Utils.notify("&6Arena has reached max player capacity."));
					player.getPlayer().sendMessage(Utils.notify("&b10 &6seconds until the game starts!"));
			});
		}

	};

	// 5 second warning
	public final Runnable sec5 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.notify("&b5 &6seconds until the game starts!")));
		}
		
	};

	// Start a new wave
	public final Runnable wave = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Increment wave
			arenaInstance.incrementCurrentWave();
			int currentWave = arenaInstance.getCurrentWave();

			// Refresh the portal hologram and scoreboards
			portal.refreshHolo(arenaInstance.getArena(), game);
			updateBoards.run();

			// Revive dead players
			arenaInstance.getGhosts().forEach(p -> {
				Utils.teleAdventure(p.getPlayer(), arenaInstance.getPlayerSpawn());
				giveItems(p);
			});

			arenaInstance.getActives().forEach(p -> {
				// Notify of upcoming wave
				int reward = (currentWave - 1) * 5;
				p.getPlayer().sendTitle(Utils.format("&6Wave " + currentWave),
						Utils.format("&7Starting in 15 seconds"), Utils.secondsToTicks(.5),
						Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

				// Give players gem rewards
				p.addGems(reward);
				if (currentWave > 1)
					p.getPlayer().sendMessage(Utils.notify("You have received &a" + reward + " &fgems!"));
			});

			// Notify spectators of upcoming wave
			arenaInstance.getSpectators().forEach(p ->
				p.getPlayer().sendTitle(Utils.format("&6Wave " + currentWave),
						Utils.format("&7Starting in 15 seconds"), Utils.secondsToTicks(.5) ,
						Utils.secondsToTicks(3.5), Utils.secondsToTicks(1)));

			// Regenerate shops when time and notify players of it
			if (currentWave % 10 == 0 || currentWave == 1) {
				int level = currentWave / 10 + 1;
				arenaInstance.setWeaponShop(Inventories.createWeaponShop(level, arenaInstance));
				arenaInstance.setArmorShop(Inventories.createArmorShop(level, arenaInstance));
				arenaInstance.setConsumeShop(Inventories.createConsumablesShop(level, arenaInstance));
				if (currentWave != 1)
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							arenaInstance.getActives().forEach(player ->
									player.getPlayer().sendTitle(Utils.format("&6Shops upgraded!"),
											Utils.format("&7Shops upgrade every 10 rounds"),
											Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5),
											Utils.secondsToTicks(1))), Utils.secondsToTicks(4));
			}

			// Spawns mobs after 15 seconds
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveStartEvent(arenaInstance)),
					Utils.secondsToTicks(15));
		}
		
	};
	
	// Start actual game
	public final Runnable start = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Teleport players to arena if waiting room exists
			if (arenaInstance.getWaitingRoom() != null) {
				arenaInstance.getActives().forEach(player ->
						Utils.teleAdventure(player.getPlayer(), arenaInstance.getPlayerSpawn()));
				arenaInstance.getSpectators().forEach(player ->
						Utils.teleSpectator(player.getPlayer(), arenaInstance.getPlayerSpawn()));
				if (arenaInstance.getWaitingSound() != null)
					arenaInstance.getPlayers().forEach(player ->
							player.getPlayer().stopSound(arenaInstance.getWaitingSound()));
			}

			// Start particles if enabled
			if (arenaInstance.isSpawnParticles())
				arenaInstance.startSpawnParticles();
			if (arenaInstance.isMonsterParticles())
				arenaInstance.startMonsterParticles();
			if (arenaInstance.isVillagerParticles())
				arenaInstance.startVillagerParticles();

			// Give all players a wooden sword and a shop while removing pre-game protection
			arenaInstance.getActives().forEach(player -> {
				player.getPlayer().setFireTicks(0);
				player.getPlayer().setInvulnerable(false);
				giveItems(player);

				String kit = player.getKit();
				// Set health for people with giant kits
				if (kit.equals("Giant1"))
					player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
							.addModifier(new AttributeModifier("Giant1", 2,
									AttributeModifier.Operation.ADD_NUMBER));
				else if (kit.equals("Giant2"))
					player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
							.addModifier(new AttributeModifier("Giant1", 4,
									AttributeModifier.Operation.ADD_NUMBER));
			});

			// Set arena to active and reset villager and enemy count
			arenaInstance.setActive(true);
			arenaInstance.resetVillagers();
			arenaInstance.resetEnemies();

			// Trigger WaveEndEvent
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveEndEvent(arenaInstance)));
		}
	};

	// Reset the arena
	public final Runnable reset = new Runnable() {
		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Update data
			arenaInstance.setActive(false);
			arenaInstance.flipEnding();
			arenaInstance.resetCurrentWave();
			arenaInstance.resetEnemies();
			arenaInstance.resetVillagers();
			arenaInstance.getTask().getTasks().clear();

			// Remove players from the arena
			arenaInstance.getPlayers().forEach(player ->
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player.getPlayer()))));

			// Clear the arena
			Utils.clear(arenaInstance.getPlayerSpawn());

			// Remove particles
			arenaInstance.cancelSpawnParticles();
			arenaInstance.cancelMonsterParticles();
			arenaInstance.cancelVillagerParticles();

			// Refresh portal
			portal.refreshHolo(arenaInstance.getArena(), game);
		}
	};

	// Update active player scoreboards
	public final Runnable updateBoards = new Runnable() {
		@Override
		public void run() {
			game.arenas.get(arena).getActives().forEach(game::createBoard);
		}
	};

	// Update time limit bar
	public final Runnable updateBar = new Runnable() {
		double progress = 1;
		double time;
		boolean messageSent;

		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			double multiplier = 1 + .2 * ((int) arenaInstance.getCurrentDifficulty() - 1);
			if (!arenaInstance.isDynamicLimit())
				multiplier = 1;

			// Add time limit bar if it doesn't exist
			if (arenaInstance.getTimeLimitBar() == null) {
				progress = 1;
				arenaInstance.startTimeLimitBar();
				arenaInstance.getPlayers().forEach(vdPlayer ->
						arenaInstance.addPlayerToTimeLimitBar(vdPlayer.getPlayer()));
				time = 1d / Utils.minutesToSeconds(arenaInstance.getWaveTimeLimit() * multiplier);
				messageSent = false;
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
							arenaInstance.getActives().forEach(player ->
									player.getPlayer().sendTitle(Utils.format("&c1 minute left!"),
											null,
											Utils.secondsToTicks(.5), Utils.secondsToTicks(1.5),
											Utils.secondsToTicks(.5)));
							messageSent = true;
						}
					} else arenaInstance.updateTimeLimitBar(progress);
					progress -= time;
				}
			}

		}
	};

	// Gives items on spawn or respawn based on kit selected
	public static void giveItems(VDPlayer player) {
		switch (player.getKit()) {
			case "Orc":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.orc());
				break;
			case "Farmer":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.farmer());
				break;
			case "Soldier":
				Utils.giveItem(player.getPlayer(), Kits.soldier());
				break;
			case "Tailor":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				EntityEquipment equipment = player.getPlayer().getEquipment();
				equipment.setHelmet(Kits.tailorHelmet());
				equipment.setChestplate(Kits.tailorChestplate());
				equipment.setLeggings(Kits.tailorLeggings());
				equipment.setBoots(Kits.tailorBoots());
				break;
			case "Alchemist":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.alchemistSpeed());
				Utils.giveItem(player.getPlayer(), Kits.alchemistHealth());
				Utils.giveItem(player.getPlayer(), Kits.alchemistHealth());
				break;
			case "Trader":
				player.addGems(200);
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				break;
			case "Summoner1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.summoner1());
				break;
			case "Summoner2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.summoner2());
				break;
			case "Summoner3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.summoner3());
				break;
			case "Reaper1":
				Utils.giveItem(player.getPlayer(), Kits.reaper1());
				break;
			case "Reaper2":
				Utils.giveItem(player.getPlayer(), Kits.reaper2());
				break;
			case "Reaper3":
				Utils.giveItem(player.getPlayer(), Kits.reaper3());
				break;
			case "Mage1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.mage1());
				break;
			case "Mage2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.mage2());
				break;
			case "Mage3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.mage3());
				break;
			case "Ninja1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.ninja1());
				break;
			case "Ninja2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.ninja2());
				break;
			case "Ninja3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.ninja3());
				break;
			case "Templar1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.templar1());
				break;
			case "Templar2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.templar2());
				break;
			case "Templar3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.templar3());
				break;
			case "Warrior1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.warrior1());
				break;
			case "Warrior2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.warrior2());
				break;
			case "Warrior3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.warrior3());
				break;
			case "Knight1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.knight1());
				break;
			case "Knight2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.knight2());
				break;
			case "Knight3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.knight3());
				break;
			case "Priest1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.priest1());
				break;
			case "Priest2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.priest2());
				break;
			case "Priest3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.priest3());
				break;
			case "Siren1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.siren1());
				break;
			case "Siren2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.siren2());
				break;
			case "Siren3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.siren3());
				break;
			case "Monk1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.monk1());
				break;
			case "Monk2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.monk2());
				break;
			case "Monk3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.monk3());
				break;
			case "Messenger1":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.messenger1());
				break;
			case "Messenger2":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.messenger2());
				break;
			case "Messenger3":
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
				Utils.giveItem(player.getPlayer(), Kits.messenger3());
				break;
			case "Blacksmith":
				Utils.giveItem(player.getPlayer(), Utils.makeUnbreakable(new ItemStack(Material.WOODEN_SWORD)));
				break;
			default:
				Utils.giveItem(player.getPlayer(), new ItemStack(Material.WOODEN_SWORD));
		}
		Utils.giveItem(player.getPlayer(), GameItems.shop());
	}
}
