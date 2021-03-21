package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.GameEndEvent;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.customEvents.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Tasks {
	private final Main plugin;
	private final Game game;
	private final int arena;
	private final Portal portal;
	private final Map<Runnable, Integer> tasks = new HashMap<>(); // Maps runnables to ID of the currently running runnable
	private final List<Location> monsterSpawns = new ArrayList<>();
	private final List<Location> villagerSpawns = new ArrayList<>();

	public Tasks(Main plugin, Game game, int arena, Portal portal) {
		this.plugin = plugin;
		this.game = game;
		this.arena = arena;
		this.portal = portal;
		plugin.getData().getConfigurationSection("a" + arena + ".monster").getKeys(false).forEach(num -> 
				monsterSpawns.add(new Location(
				Bukkit.getWorld(plugin.getData().getString("a" + arena + ".monster." + num + ".world")),
				plugin.getData().getDouble("a" + arena + ".monster." + num + ".x"),
				plugin.getData().getDouble("a" + arena + ".monster." + num + ".y"),
				plugin.getData().getDouble("a" + arena + ".monster." + num + ".z"))));
		plugin.getData().getConfigurationSection("a" + arena + ".villager").getKeys(false).forEach(num -> 
				villagerSpawns.add(new Location(
				Bukkit.getWorld(plugin.getData().getString("a" + arena + ".villager." + num + ".world")),
				plugin.getData().getDouble("a" + arena + ".villager." + num + ".x"),
				plugin.getData().getDouble("a" + arena + ".villager." + num + ".y"),
				plugin.getData().getDouble("a" + arena + ".villager." + num + ".z"))));
	}

	public Map<Runnable, Integer> getTasks() {
		return tasks;
	}

	// Waiting for enough players message
	public final Runnable waiting = new Runnable() {
		@Override
		public void run() {
			game.playing.forEach(player -> {
				if (player.getArena() == arena)
					player.getPlayer().sendMessage(Utils.format("&6Waiting for more players to start the game."));
			});
		}
	};

	// 2 minute warning
	public final Runnable min2 = new Runnable() {

		@Override
		public void run() {
			game.playing.forEach(player -> {
				if (player.getArena() == arena)
					player.getPlayer().sendMessage(Utils.format("&c2 &6minutes until the game starts!"));
			});
		}

	};

	// 1 minute warning
	public final Runnable min1 = new Runnable() {

		@Override
		public void run() {
			game.playing.forEach(player -> {
				if (player.getArena() == arena)
					player.getPlayer().sendMessage(Utils.format("&c1 &6minutes until the game starts!"));
			});
		}
		
	};

	// 30 second warning
	public final Runnable sec30 = new Runnable() {

		@Override
		public void run() {
			game.playing.forEach(player -> {
				if (player.getArena() == arena)
					player.getPlayer().sendMessage(Utils.format("&c30 &6seconds until the game starts!"));
			});
		}
	};

	// 10 second warning
	public final Runnable sec10 = new Runnable() {

		@Override
		public void run() {
			game.playing.forEach(player -> {
				if (player.getArena() == arena)
					player.getPlayer().sendMessage(Utils.format("&c10 &6seconds until the game starts!"));
			});
		}
		
	};

	// 10 second warning when full
	public final Runnable full10 = new Runnable() {

		@Override
		public void run() {
			game.playing.forEach(player -> {
				if (player.getArena() == arena) {
					player.getPlayer().sendMessage(Utils.format("&6Arena has reached max player capacity."));
					player.getPlayer().sendMessage(Utils.format("&c10 &6seconds until the game starts!"));
				}
			});
		}

	};

	// 5 second warning
	public final Runnable sec5 = new Runnable() {

		@Override
		public void run() {
			game.playing.forEach(player -> {
				if (player.getArena() == arena)
					player.getPlayer().sendMessage(Utils.format("&c5 &6seconds until the game starts!"));
			});
		}
		
	};

	// Start a new wave
	public final Runnable wave = new Runnable() {

		@Override
		public void run() {
			// Increment wave
			plugin.getData().set("a" + arena + ".currentWave",
					plugin.getData().getInt("a" + arena + ".currentWave") + 1);
			plugin.saveData();
			int currentWave = plugin.getData().getInt("a" + arena + ".currentWave");

			// Refresh the portal hologram
			portal.refreshHolo(arena);

			// Regenerate shops when time and notify players of it
			if (currentWave % 10 == 0 || currentWave == 1) {
				int shopNum = plugin.getData().getInt("a" + arena + ".currentWave") / 10;
				if (shopNum > 5) shopNum = 5;
				Arena arenaInstance = game.actives.stream()
						.filter(r -> r.getArena() == arena).collect(Collectors.toList()).get(0);
				arenaInstance.setShop(Inventories.createShop(shopNum));
				game.playing.forEach(player -> {
					if (player.getArena() == arena && !player.isSpectating())
						player.getPlayer().sendMessage(Utils.format("&6Shops have reset!"));
				});
			}
			
			game.playing.forEach(player -> {
				Player gamer = player.getPlayer();
				int wave = plugin.getData().getInt("a" + arena + ".currentWave");

				// For active players
				if (player.getArena() == arena && !player.isSpectating()) {
					// Revive dead players
					if (gamer.getGameMode().equals(GameMode.SPECTATOR)) {
						Utils.prepTeleAdventure(gamer);
						Location location = new Location(Bukkit.getWorld(
								plugin.getData().getString("a" + arena + ".spawn.world")),
								plugin.getData().getDouble("a" + arena + ".spawn.x"),
								plugin.getData().getDouble("a" + arena + ".spawn.y"),
								plugin.getData().getDouble("a" + arena + ".spawn.z"));
						gamer.teleport(location);
						gamer.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
						gamer.getInventory().addItem(GameItems.shop());
					}

					// Notify of upcoming wave
					int reward = wave * 10 - 10;
					gamer.sendTitle(Utils.format("&6Wave " + wave),
							Utils.format("&7Starting in 15 seconds"), 10 , 70, 20);

					// Give players gem rewards
					player.addGems(reward);
					if (wave > 1)
						gamer.sendMessage(Utils.format("&fYou have received &a" + reward + " &fgems!"));
				}

				// Send notifications to spectators about wave starting
				else if (player.getArena() == arena && player.isSpectating())
					gamer.sendTitle(Utils.format("&6Wave " + wave),
							Utils.format("&7Starting in 15 seconds"), 10 , 70, 20);
			});

			// Spawns mobs after 15 seconds
			new BukkitRunnable() {

				@Override
				public void run() {
					spawnMonsters(arena, plugin.getData().getInt("a" + arena + ".currentWave"), monsterSpawns);
					spawnVillagers(arena, plugin.getData().getInt("a" + arena + ".currentWave"), villagerSpawns);
				}
				
			}.runTaskLater(plugin, 300);
		}
		
	};
	
	// Start actual game
	public final Runnable start = new Runnable() {

		@Override
		public void run() {
			// Give all players a wooden sword and a shop
			game.playing.forEach(player -> {
				if (player.getArena() == arena && !player.isSpectating()) {
					player.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
					player.getPlayer().getInventory().addItem(GameItems.shop());
				}
			});
			
			// Set arena to active
			plugin.getData().set("a" + arena + ".active", true);
			plugin.saveData();
			
			// Trigger WaveEndEvent
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
					Bukkit.getPluginManager().callEvent(new WaveEndEvent(arena)), 0);
		}
	};

	// Reset the arena
	public final Runnable reset = new Runnable() {
		@Override
		public void run() {
			// Update data
			plugin.getData().set("a" + arena + ".active", false);
			plugin.getData().set("a" + arena + ".currentWave", 0);
			plugin.saveData();
			Arena arenaInstance = game.actives.stream()
					.filter(r -> r.getArena() == arena).collect(Collectors.toList()).get(0);
			game.actives.remove(arenaInstance);

			// Remove players from the arena
			game.playing.forEach(player -> {
				if (player.getArena() == arena)
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player.getPlayer())), 0);
			});

			// Get all nearby entities in the arena
			Location location = new Location(
					Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
					plugin.getData().getDouble("a" + arena + ".spawn.x"),
					plugin.getData().getDouble("a" + arena + ".spawn.y"),
					plugin.getData().getDouble("a" + arena + ".spawn.z"));
			if (location.getWorld() == null) {
				System.out.println("Error: Location's world is null for endGame method");
				return;
			}
			Collection<Entity> ents = location.getWorld().getNearbyEntities(location, 200, 200, 100);

			if (!ents.isEmpty()) {
				// Clear the arena for living entities
				ents.forEach(ent -> {
					if (ent instanceof LivingEntity && !(ent instanceof Player)) {
						if (ent.getName().contains("VD")) {
							((LivingEntity) ent).setHealth(0);
						}
					}
				});

				// Clear the arena for items
				ents.forEach(ent -> {
					if (ent instanceof Item)
						ent.remove();
				});
			}

			// Refresh portal
			portal.refreshHolo(arena);
		}
	};

	// Update active player scoreboards
	public final Runnable updateBoards = new Runnable() {
		@Override
		public void run() {
			// Remove players from the arena
			game.playing.forEach(player -> {
				if (player.getArena() == arena && !player.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
					game.createBoard(player);
			});
		}
	};
	
	// Spawns villagers randomly
	private void spawnVillagers(int arena, int round, List<Location> spawns) {
		Random r = new Random();
		Arena arenaInstance = game.actives.stream()
				.filter(a -> a.getArena() == arena).collect(Collectors.toList()).get(0);
		for (int i = 0;
			 i < plugin.getConfig().getInt("waves.wave" + round + ".vlgr") - arenaInstance.getVillagers();
			 i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Villager n = (Villager) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.VILLAGER);
					n.setCustomName(Utils.format("&aVD" + arena +": Villager"));
					arenaInstance.incrementVillagers();
				}

			}.runTaskLater(plugin, 0);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)));
	}
	
	// Spawns monsters randomly
	private void spawnMonsters(int arena, int round, List<Location> spawns) {
		Random r = new Random();
		Arena arenaInstance = game.actives.stream()
				.filter(a -> a.getArena() == arena).collect(Collectors.toList()).get(0);

		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".zomb"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Zombie n = (Zombie) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.ZOMBIE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Zombie"));
					n.setCanPickupItems(false);
					n.getEquipment().setItemInMainHand(null);
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 1);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".husk"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Husk n = (Husk) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.HUSK);
					n.setCustomName(Utils.format("&cVD" + arena + ": Husk"));
					n.setCanPickupItems(false);
					n.getEquipment().setItemInMainHand(null);
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 2);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wskl"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					WitherSkeleton n = (WitherSkeleton) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.WITHER_SKELETON);
					n.setCustomName(Utils.format("&cVD" + arena + ": Wither Skeleton"));
					n.setCanPickupItems(false);
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 3);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".brut"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					PiglinBrute n = (PiglinBrute) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.PIGLIN_BRUTE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Piglin Brute"));
					n.setCanPickupItems(false);
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 4);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".vind"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Vindicator n = (Vindicator) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.VINDICATOR);
					n.setCustomName(Utils.format("&cVD" + arena + ": Vindicator"));
					n.setCanPickupItems(false);
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 5);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".spid"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Spider n = (Spider) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.SPIDER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Spider"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 6);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".cspd"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					CaveSpider n = (CaveSpider) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.CAVE_SPIDER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Cave Spider"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 7);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wtch"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Witch n = (Witch) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.WITCH);
					n.setCustomName(Utils.format("&cVD" + arena + ": Witch"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 8);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".skel"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Skeleton n = (Skeleton) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.SKELETON);
					n.setCustomName(Utils.format("&cVD" + arena + ": Skeleton"));
					n.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
					n.getEquipment().setItemInMainHandDropChance(0);
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 9);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".stry"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Stray n = (Stray) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.STRAY);
					n.setCustomName(Utils.format("&cVD" + arena + ": Stray"));
					n.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
					n.getEquipment().setItemInMainHandDropChance(0);
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 10);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".blze"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Blaze n = (Blaze) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.BLAZE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Blaze"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 11);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".ghst"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Ghast n = (Ghast) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.GHAST);
					n.setCustomName(Utils.format("&cVD" + arena + ": Ghast"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 12);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".pill"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Pillager n = (Pillager) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.PILLAGER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Pillager"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 13);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".slim"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Slime n = (Slime) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.SLIME);
					n.setCustomName(Utils.format("&cVD" + arena + ": Slime"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 14);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".mslm"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					MagmaCube n = (MagmaCube) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.MAGMA_CUBE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Magma Cube"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 15);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".crpr"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Creeper n = (Creeper) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.CREEPER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Creeper"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 16);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".phtm"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Phantom n = (Phantom) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.PHANTOM);
					n.setCustomName(Utils.format("&cVD" + arena + ": Phantom"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 17);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".evok"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Evoker n = (Evoker) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.EVOKER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Evoker"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 18);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".hgln"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Hoglin n = (Hoglin) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.HOGLIN);
					n.setCustomName(Utils.format("&cVD" + arena + ": Hoglin"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 19);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".rvgr"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Ravager n = (Ravager) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.RAVAGER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Ravager"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 20);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wthr"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Wither n = (Wither) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.WITHER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Wither"));
					arenaInstance.incrementEnemies();
				}
				
			}.runTaskLater(plugin, 21);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)), 22);
	}
}
