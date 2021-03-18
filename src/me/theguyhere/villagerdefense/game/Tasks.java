package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Tasks extends BukkitRunnable {
	private final Main plugin;
	private final Game game;
	private final String arena;
	private final GameItems gi;
	private final Inventories inv;
	private final Portal portal;
	
	public Tasks(Main plugin, Game game, String arena, GameItems gi, Inventories inv, Portal portal) {
		this.plugin = plugin;
		this.game = game;
		this.arena = arena;
		this.gi = gi;
		this.inv = inv;
		this.portal = portal;
	}
	
	List<Location> monsterSpawns = new ArrayList<>();
	List<Location> villagerSpawns = new ArrayList<>();

	// 1 minute warning
	Runnable min1 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c1 &6minute until the game starts!"));
			});
			game.spectating.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c1 &6minute until the game starts!"));
			});
		}
		
	};

	// 30 second warning
	Runnable sec30 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c30 &6seconds until the game starts!"));
			});
			game.spectating.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c30 &6seconds until the game starts!"));
			});
		}
	};

	// 10 second warning
	Runnable sec10 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c10 &6seconds until the game starts!"));
			});
			game.spectating.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c10 &6seconds until the game starts!"));
			});
		}
		
	};

	// 5 second warning
	Runnable sec5 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c5 &6seconds until the game starts!"));
			});
			game.spectating.forEach((player, num) -> {
				if (arena.equals(num))
					Bukkit.getServer().getPlayer(player)
							.sendMessage(Utils.format("&c5 &6seconds until the game starts!"));
			});
		}
		
	};

	// Start a wave
	Runnable wave = new BukkitRunnable() {

		@Override
		public void run() {
			// Sets this arena on break
			game.breaks.add(arena);
			
			// Increment wave
			plugin.getData().set("a" + arena + ".currentWave",
					plugin.getData().getInt("a" + arena + ".currentWave") + 1);
			plugin.saveData();

			// Refresh the portal hologram
			portal.refreshHolo(Integer.parseInt(arena));

			// Regenerate shops when time and notify players of it
			if (plugin.getData().getInt("a" + arena + ".currentWave") % 10 == 0) {
				int shopNum = plugin.getData().getInt("a" + arena + ".currentWave") / 10;
				if (shopNum > 5) shopNum = 5;
				game.shops.put(arena, inv.createShop(shopNum));
				game.playing.forEach((player, num) -> {
					if (arena.equals(num))
						Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&6Shops have reset!"));
				});
			}
			
			// Send notifications to players about wave starting
			game.playing.forEach((player, num) -> {
				Player gamer = Bukkit.getServer().getPlayer(player);
				if (arena.equals(num)) {
					// Revive dead players
					if (gamer.getGameMode().equals(GameMode.SPECTATOR)) {
						Utils.prepTeleAdventure(gamer);
						Location location = new Location(Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
								plugin.getData().getDouble("a" + arena + ".spawn.x"), plugin.getData().getDouble("a" + arena + ".spawn.y"),
								plugin.getData().getDouble("a" + arena + ".spawn.z"));
						gamer.teleport(location);
						gamer.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
						gamer.getInventory().addItem(gi.shop());
					}

					// Notify of upcoming wave
					int wave = plugin.getData().getInt("a" + arena + ".currentWave");
					gamer.sendTitle(Utils.format("&6Wave " + wave),
							Utils.format("&7Starting in 15 seconds"), 10 , 70, 20);

					// Give players gem rewards
					game.gems.put(player, game.gems.get(player) + wave * 10 - 10);
					if (wave > 1)
						gamer
								.sendMessage(Utils.format("&fYou have received &a" + (wave * 10 - 10) +
										" &fgems!"));
				}
			});

			// Send notifications to spectators about wave starting
			game.spectating.forEach((player, num) -> {
				// Notify of upcoming wave
				Bukkit.getServer().getPlayer(player).sendTitle(Utils.format("&6Wave " +
						plugin.getData().getInt("a" + arena + ".currentWave")),
						Utils.format("&7Starting in 15 seconds"), 10 , 70, 20);
			});
			
			// Spawns mobs after 15 seconds
			new BukkitRunnable() {

				@Override
				public void run() {
					spawnMonsters(arena, plugin.getData().getInt("a" + arena + ".currentWave"), monsterSpawns);
					spawnVillagers(arena, plugin.getData().getInt("a" + arena + ".currentWave"), villagerSpawns);
					game.breaks.remove(arena);
				}
				
			}.runTaskLater(plugin, 300);
		}
		
	};
	
	// Start actual game
	Runnable start = new BukkitRunnable() {

		@Override
		public void run() {
			// Give all players a wooden sword and a shop
			game.playing.forEach((player, num) -> {
				if (arena.equals(num)) {
					Bukkit.getServer().getPlayer(player).getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
					Bukkit.getServer().getPlayer(player).getInventory().addItem(gi.shop());
				}
			});
			
			// Set arena to active
			plugin.getData().set("a" + arena + ".active", true);
			plugin.saveData();

			// Refresh the portal hologram
			portal.refreshHolo(Integer.parseInt(arena));
			
			// Create the initial shop inventory
			game.shops.put(arena, inv.createShop(0));
			
			// Keep running waves until game end conditions are met
			new BukkitRunnable() {
				
				@Override
				public void run() {
					// Count players alive
					int[] players = {0};
					game.playing.forEach((gamer, num) -> {
						if (num.equals(arena)) {
							players[0]++;
							if (Bukkit.getServer().getPlayer(gamer).getGameMode().equals(GameMode.SPECTATOR))
								players[0]--;
						}
					});

					// Lose condition
					if (game.villagers.get(arena) == 0 &&
							!(plugin.getData().getInt("a" + arena + ".currentWave") == 0) &&
							!game.breaks.contains(arena) || players[0] == 0) {
						cancel();
						game.endGame(arena);
					}

					// TEMPORARY win condition
					else if (plugin.getData().getInt("a" + arena + ".currentWave") == 12) {
						cancel();
						game.endGame(arena);
					}

					// Round win condition
					else if (!game.breaks.contains(arena) && game.enemies.get(arena) == 0)
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, wave);
				}
				
			}.runTaskTimer(plugin, 0, 20);
		}
	};
	
	@Override
	public void run() {
		// Add the spawns locally
		plugin.getData().getConfigurationSection("a" + arena + ".monster").getKeys(false).forEach(num -> {
			monsterSpawns.add(new Location(
					Bukkit.getWorld(plugin.getData().getString("a" + arena + ".monster." + num + ".world")),
					plugin.getData().getDouble("a" + arena + ".monster." + num + ".x"),
					plugin.getData().getDouble("a" + arena + ".monster." + num + ".y"),
					plugin.getData().getDouble("a" + arena + ".monster." + num + ".z")));
		});
		plugin.getData().getConfigurationSection("a" + arena + ".villager").getKeys(false).forEach(num -> {
			villagerSpawns.add(new Location(
					Bukkit.getWorld(plugin.getData().getString("a" + arena + ".villager." + num + ".world")),
					plugin.getData().getDouble("a" + arena + ".villager." + num + ".x"),
					plugin.getData().getDouble("a" + arena + ".villager." + num + ".y"),
					plugin.getData().getDouble("a" + arena + ".villager." + num + ".z")));
		});

		// Send 2 minute warning
		game.playing.forEach((player, num) -> {
			if (arena.equals(num))
				Bukkit.getServer().getPlayer(player)
						.sendMessage(Utils.format("&c2 &6minutes until the game starts!"));
		});
		int min1ID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, min1, 1200);
		int sec30ID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, sec30, 1800);
		int sec10ID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, sec10, 2200);
		int sec5ID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, sec5, 2300);
		int startID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, start, 2400);
		
//		When game reaches max player capacity
		new BukkitRunnable() {
			
			@Override
			public void run() {
				int[] players = {0};
				game.playing.forEach((player, num) -> {
					if (arena.equals(num))
						players[0]++;
				});

				if (Integer.toString(players[0]).equals(plugin.getData().getString("a" + arena + ".max"))) {
					
					Bukkit.getScheduler().cancelTask(min1ID);
					Bukkit.getScheduler().cancelTask(sec30ID);
					Bukkit.getScheduler().cancelTask(sec10ID);
					Bukkit.getScheduler().cancelTask(sec5ID);
					Bukkit.getScheduler().cancelTask(startID);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, sec10, 0);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, sec5, 100);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, start, 200);
					cancel();
				}
				else if (plugin.getData().getInt("a" + arena + ".currentWave") > 0) {
					cancel();
				}
			}
			
		}.runTaskTimer(plugin, 0, 5);
	}

	// Spawns villagers randomly
	private void spawnVillagers(String arena, int round, List<Location> spawns) {
		Random r = new Random();
		for (int i = 0;
			 i < plugin.getConfig().getInt("waves.wave" + round + ".vlgr") - game.villagers.get(arena);
			 i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Villager n = (Villager) Bukkit.getWorld(spawns.get(num).getWorld().getName())
							.spawnEntity(spawns.get(num), EntityType.VILLAGER);
					n.setCustomName(Utils.format("&aVD" + arena +": Villager"));
					n.setCustomNameVisible(false);
					game.villagers.put(arena, game.villagers.get(arena) + 1);
				}

			}.runTaskLater(plugin, 0);
		}
	}
	
//	Spawns monsters randomly
	private void spawnMonsters(String arena, int round, List<Location> spawns) {
		Random r = new Random();
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".zomb"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Zombie n = (Zombie) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.ZOMBIE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Zombie"));
					n.setCanPickupItems(false);
					n.getEquipment().setItemInMainHand(null);
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 1);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".husk"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Husk n = (Husk) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.HUSK);
					n.setCustomName(Utils.format("&cVD" + arena + ": Husk"));
					n.setCanPickupItems(false);
					n.getEquipment().setItemInMainHand(null);
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 2);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wskl"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					WitherSkeleton n = (WitherSkeleton) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.WITHER_SKELETON);
					n.setCustomName(Utils.format("&cVD" + arena + ": Wither Skeleton"));
					n.setCanPickupItems(false);
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 3);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".brut"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					PiglinBrute n = (PiglinBrute) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.PIGLIN_BRUTE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Piglin Brute"));
					n.setCanPickupItems(false);
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 4);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".vind"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Vindicator n = (Vindicator) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.VINDICATOR);
					n.setCustomName(Utils.format("&cVD" + arena + ": Vindicator"));
					n.setCanPickupItems(false);
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 5);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".spid"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Spider n = (Spider) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.SPIDER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Spider"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 6);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".cspd"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					CaveSpider n = (CaveSpider) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.CAVE_SPIDER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Cave Spider"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 7);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wtch"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Witch n = (Witch) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.WITCH);
					n.setCustomName(Utils.format("&cVD" + arena + ": Witch"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 8);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".skel"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Skeleton n = (Skeleton) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.SKELETON);
					n.setCustomName(Utils.format("&cVD" + arena + ": Skeleton"));
					n.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
					n.getEquipment().setItemInMainHandDropChance(0);
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 9);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".stry"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Stray n = (Stray) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.STRAY);
					n.setCustomName(Utils.format("&cVD" + arena + ": Stray"));
					n.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
					n.getEquipment().setItemInMainHandDropChance(0);
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 10);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".blze"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Blaze n = (Blaze) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.BLAZE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Blaze"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 11);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".ghst"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Ghast n = (Ghast) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.GHAST);
					n.setCustomName(Utils.format("&cVD" + arena + ": Ghast"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 12);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".pill"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Pillager n = (Pillager) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.PILLAGER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Pillager"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 13);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".slim"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Slime n = (Slime) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.SLIME);
					n.setCustomName(Utils.format("&cVD" + arena + ": Slime"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 14);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".mslm"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					MagmaCube n = (MagmaCube) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.MAGMA_CUBE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Magma Cube"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 15);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".crpr"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Creeper n = (Creeper) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.CREEPER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Creeper"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 16);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".phtm"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Phantom n = (Phantom) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.PHANTOM);
					n.setCustomName(Utils.format("&cVD" + arena + ": Phantom"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 17);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".evok"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Evoker n = (Evoker) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.EVOKER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Evoker"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 18);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".hgln"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Hoglin n = (Hoglin) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.HOGLIN);
					n.setCustomName(Utils.format("&cVD" + arena + ": Hoglin"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 19);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".rvgr"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Ravager n = (Ravager) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.RAVAGER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Ravager"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 20);
		}
		for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wthr"); i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Wither n = (Wither) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.WITHER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Wither"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 21);
		}
	}
}
