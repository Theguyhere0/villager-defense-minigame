package me.theguyhere.villagerdefense.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import me.theguyhere.villagerdefense.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.theguyhere.villagerdefense.Inventories;
import me.theguyhere.villagerdefense.Main;

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
	
	List<Location> spawns = new ArrayList<Location>();
	
//	1 minute warning
	Runnable min1 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num)) {
					Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&c1 &6minute until the game starts!"));
				}
			});
		}
		
	};

//	30 second warning
	Runnable sec30 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num)) {
					Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&c30 &6seconds until the game starts!"));
				}
			});
		}
	};

//	10 second warning
	Runnable sec10 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num)) {
					Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&c10 &6seconds until the game starts!"));
				}
			});
		}
		
	};

//	5 second warning
	Runnable sec5 = new BukkitRunnable() {

		@Override
		public void run() {
			game.playing.forEach((player, num) -> {
				if (arena.equals(num)) {
					Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&c5 &6seconds until the game starts!"));
				}
			});
		}
		
	};

//	Start a wave
	Runnable wave = new BukkitRunnable() {

		@Override
		public void run() {
//			Sets this arena on break
			game.breaks.add(arena);
			
//			Increment wave
			plugin.getData().set("a" + arena + ".currentWave", plugin.getData().getInt("a" + arena + ".currentWave") + 1);
			plugin.saveData();

			// Refresh the portal hologram
			portal.refreshHolo(Integer.parseInt(arena));

//			Regenerate shops and notify players of it
			if (plugin.getData().getInt("a" + arena + ".currentWave") % 10 == 0) {
				int shopNum = plugin.getData().getInt("a" + arena + ".currentWave") / 10;
				if (shopNum > 5)
					shopNum = 5;
				game.shops.put(arena, inv.createShop(shopNum));
				game.playing.forEach((player, num) -> {
					if (arena.equals(num)) {
						Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&6Shops have reset!"));
					}
				});
			}
			
//			Send notifications to players about wave starting
			game.playing.forEach((player, num) -> {
				if (arena.equals(num)) {
					if (Bukkit.getServer().getPlayer(player).getGameMode().equals(GameMode.SPECTATOR)) {
						Location location = new Location(Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
								plugin.getData().getDouble("a" + arena + ".spawn.x"), plugin.getData().getDouble("a" + arena + ".spawn.y"),
								plugin.getData().getDouble("a" + arena + ".spawn.z"));
						Bukkit.getServer().getPlayer(player).teleport(location);
						Bukkit.getServer().getPlayer(player).setGameMode(GameMode.ADVENTURE);
						Bukkit.getServer().getPlayer(player).getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
						Bukkit.getServer().getPlayer(player).getInventory().addItem(gi.shop());
						Bukkit.getServer().getPlayer(player).getActivePotionEffects().clear();
						Bukkit.getServer().getPlayer(player).setHealth(Bukkit.getServer().getPlayer(player).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
						Bukkit.getServer().getPlayer(player).setFoodLevel(20);
						Bukkit.getServer().getPlayer(player).setSaturation(20);
						Bukkit.getServer().getPlayer(player).setLevel(0);
					}
					int wave = plugin.getData().getInt("a" + arena + ".currentWave");
					Bukkit.getServer().getPlayer(player).sendTitle(Utils.format("&6Wave " + wave), Utils.format("&7Starting in 15 seconds"), 10 , 70, 20);
					game.gems.put(player, game.gems.get(player) + wave * 10 - 10);
					if (wave > 1)
						Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&fYou have received &a" + (wave * 10 - 10) + " &fgems!"));
				}
			});
			
//			Spawns mobs after 15 seconds
			new BukkitRunnable() {

				@Override
				public void run() {
					spawn(arena, plugin.getData().getInt("a" + arena + ".currentWave"), spawns);
					game.breaks.remove(arena);

				}
				
			}.runTaskLater(plugin, 300);
		}
		
	};
	
//	Start actual game
	Runnable start = new BukkitRunnable() {

		@Override
		public void run() {
//			Give all wooden sword
			game.playing.forEach((player, num) -> {
				if (arena.equals(num)) {
					Bukkit.getServer().getPlayer(player).getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
					Bukkit.getServer().getPlayer(player).getInventory().addItem(gi.shop());
				}
			});
			
//			Start the waves tasks and set arena to active
			plugin.getData().set("a" + arena + ".active", true);
			plugin.saveData();

			// Refresh the portal hologram
			portal.refreshHolo(Integer.parseInt(arena));
			
//			Create the initial shop inventory
			game.shops.put(arena, inv.createShop(0));
			
//			Keep running waves until game end conditions are met
			new BukkitRunnable() {
				
				@Override
				public void run() {
					Integer[] players = {0};
					Integer[] ghosts = {0};
					game.playing.forEach((gamer, num) -> {
						if (num.equals(arena)) {
							players[0]++;
							if (Bukkit.getServer().getPlayer(gamer).getGameMode().equals(GameMode.SPECTATOR))
								ghosts[0]++;
						}
					});
//					Game end situations
					if (game.villagers.get(arena) == 0 && !(plugin.getData().getInt("a" + arena + ".currentWave") == 0) &&
							!game.breaks.contains(arena) || players[0] - ghosts[0] == 0) {
						cancel();
						game.endGame(arena);
					}
//					TEMPORARY win condition
					else if (plugin.getData().getInt("a" + arena + ".currentWave") == 12) {
						cancel();
						game.endGame(arena);
					}
					else if (!game.breaks.contains(arena) && game.enemies.get(arena) == 0) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, wave);
					}
				}
				
			}.runTaskTimer(plugin, 0, 20);
		}
		
	};
	
	@Override
	public void run() {
		plugin.getData().getConfigurationSection("a" + arena + ".mob").getKeys(false).forEach(num -> {
			spawns.add(new Location(Bukkit.getWorld(plugin.getData().getString("a" + arena + ".mob." + num + ".world")),
					plugin.getData().getDouble("a" + arena + ".mob." + num + ".x"), plugin.getData().getDouble("a" + arena + ".mob." + num + ".y"),
					plugin.getData().getDouble("a" + arena + ".mob." + num + ".z")));
		});
//		Clear the arena
		Location location = new Location(Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
				plugin.getData().getDouble("a" + arena + ".spawn.x"), plugin.getData().getDouble("a" + arena + ".spawn.y"),
				plugin.getData().getDouble("a" + arena + ".spawn.z"));
		Collection<Entity> ents = Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")).getNearbyEntities(location, 100, 100, 50);
		ents.forEach(ent -> {
			if (ent instanceof LivingEntity && !(ent instanceof Player)) {
				if (ent.getName().contains("VD")) {
					((LivingEntity) ent).setHealth(0);
				}
			}
			else if (ent instanceof Item && !(ent instanceof ArmorStand)) {
				ent.remove();
			}
		});
//		2 minute warning
		game.playing.forEach((player, num) -> {
			if (arena.equals(num)) {
				Bukkit.getServer().getPlayer(player).sendMessage(Utils.format("&c2 &6minutes until the game starts!"));
			}
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
					if (arena.equals(num)) {
						players[0]++;
					}
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
	
//	Spawns mobs by mob spawn randomly
	private void spawn(String arena, Integer round, List<Location> spawns) {
		Random r = new Random();
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".vlgr"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Villager n = (Villager) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.VILLAGER);
					n.setCustomName(Utils.format("&aVD" + arena +": Villager"));
					game.villagers.put(arena, game.villagers.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 0);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".zomb"); i++) {
			Integer num = r.nextInt(spawns.size());
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
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".husk"); i++) {
			Integer num = r.nextInt(spawns.size());
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
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wskl"); i++) {
			Integer num = r.nextInt(spawns.size());
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
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".brut"); i++) {
			Integer num = r.nextInt(spawns.size());
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
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".vind"); i++) {
			Integer num = r.nextInt(spawns.size());
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
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".spid"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Spider n = (Spider) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.SPIDER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Spider"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 6);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".cspd"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					CaveSpider n = (CaveSpider) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.CAVE_SPIDER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Cave Spider"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 7);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wtch"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Witch n = (Witch) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.WITCH);
					n.setCustomName(Utils.format("&cVD" + arena + ": Witch"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 8);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".skel"); i++) {
			Integer num = r.nextInt(spawns.size());
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
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".stry"); i++) {
			Integer num = r.nextInt(spawns.size());
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
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".blze"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Blaze n = (Blaze) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.BLAZE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Blaze"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 11);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".ghst"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Ghast n = (Ghast) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.GHAST);
					n.setCustomName(Utils.format("&cVD" + arena + ": Ghast"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 12);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".pill"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Pillager n = (Pillager) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.PILLAGER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Pillager"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 13);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".slim"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Slime n = (Slime) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.SLIME);
					n.setCustomName(Utils.format("&cVD" + arena + ": Slime"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 14);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".mslm"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					MagmaCube n = (MagmaCube) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.MAGMA_CUBE);
					n.setCustomName(Utils.format("&cVD" + arena + ": Magma Cube"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 15);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".crpr"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Creeper n = (Creeper) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.CREEPER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Creeper"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 16);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".phtm"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Phantom n = (Phantom) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.PHANTOM);
					n.setCustomName(Utils.format("&cVD" + arena + ": Phantom"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 17);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".evok"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Evoker n = (Evoker) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.EVOKER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Evoker"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 18);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".hgln"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Hoglin n = (Hoglin) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.HOGLIN);
					n.setCustomName(Utils.format("&cVD" + arena + ": Hoglin"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 19);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".rvgr"); i++) {
			Integer num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Ravager n = (Ravager) Bukkit.getWorld(spawns.get(num).getWorld().getName()).spawnEntity(spawns.get(num), EntityType.RAVAGER);
					n.setCustomName(Utils.format("&cVD" + arena + ": Ravager"));
					game.enemies.put(arena, game.enemies.get(arena) + 1);
				}
				
			}.runTaskLater(plugin, 20);
		}
		for (Integer i = 0; i < plugin.getConfig().getInt("waves.wave" + round + ".wthr"); i++) {
			Integer num = r.nextInt(spawns.size());
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
