package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.customEvents.ReloadBoardsEvent;
import me.theguyhere.villagerdefense.customEvents.WaveEndEvent;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
				player.getPlayer().sendMessage(Utils.format("&6Waiting for more players to start the game.")));
		}
	};

	// 2 minute warning
	public final Runnable min2 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.format("&c2 &6minutes until the game starts!")));
		}

	};

	// 1 minute warning
	public final Runnable min1 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.format("&c1 &6minutes until the game starts!")));
		}
		
	};

	// 30 second warning
	public final Runnable sec30 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.format("&c30 &6seconds until the game starts!")));
		}
	};

	// 10 second warning
	public final Runnable sec10 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.format("&c10 &6seconds until the game starts!")));
		}
		
	};

	// 10 second warning when full
	public final Runnable full10 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player -> {
					player.getPlayer().sendMessage(Utils.format("&6Arena has reached max player capacity."));
					player.getPlayer().sendMessage(Utils.format("&c10 &6seconds until the game starts!"));
			});
		}

	};

	// 5 second warning
	public final Runnable sec5 = new Runnable() {

		@Override
		public void run() {
			game.arenas.get(arena).getPlayers().forEach(player ->
					player.getPlayer().sendMessage(Utils.format("&c5 &6seconds until the game starts!")));
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

			// Refresh the portal hologram
			portal.refreshHolo(arena, game);

			// Regenerate shops when time and notify players of it
			if (currentWave % 10 == 0 || currentWave == 1) {
				int shopNum = currentWave / 10;
				if (shopNum > 5) shopNum = 5;
				arenaInstance.setShop(Inventories.createShop(shopNum));
				if (currentWave != 1)
					arenaInstance.getActives().forEach(player ->
						player.getPlayer().sendMessage(Utils.format("&6Shops have reset!")));
			}

			// Revive dead players
			arenaInstance.getGhosts().forEach(p -> {
				Utils.prepTeleAdventure(p.getPlayer());
				p.getPlayer().teleport(arenaInstance.getPlayerSpawn());
				p.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
				p.getPlayer().getInventory().addItem(GameItems.shop());
			});

			arenaInstance.getActives().forEach(p -> {
				// Notify of upcoming wave
				int reward = currentWave * 10 - 10;
				p.getPlayer().sendTitle(Utils.format("&6Wave " + currentWave),
						Utils.format("&7Starting in 15 seconds"), 10 , 70, 20);

				// Give players gem rewards
				p.addGems(reward);
				if (currentWave > 1)
					p.getPlayer().sendMessage(Utils.format("&fYou have received &a" + reward + " &fgems!"));
			});

			arenaInstance.getSpectators().forEach(p ->
				p.getPlayer().sendTitle(Utils.format("&6Wave " + currentWave),
						Utils.format("&7Starting in 15 seconds"), 10 , 70, 20));

			// Spawns mobs after 15 seconds
			new BukkitRunnable() {

				@Override
				public void run() {
					spawnMonsters(arena, currentWave, arenaInstance.getMonsterSpawns());
					spawnVillagers(arena, currentWave, arenaInstance.getVillagerSpawns());
				}
				
			}.runTaskLater(plugin, 300);
		}
		
	};
	
	// Start actual game
	public final Runnable start = new Runnable() {

		@Override
		public void run() {
			Arena arenaInstance = game.arenas.get(arena);

			// Give all players a wooden sword and a shop while removing pre-game protection
			arenaInstance.getActives().forEach(player -> {
				player.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
				player.getPlayer().getInventory().addItem(GameItems.shop());
				player.getPlayer().getActivePotionEffects()
						.forEach(effect -> player.getPlayer().removePotionEffect(effect.getType()));
				player.getPlayer().setFireTicks(0);
				player.getPlayer().setInvulnerable(false);
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

			// Refresh portal
			portal.refreshHolo(arena, game);
		}
	};

	// Update active player scoreboards
	public final Runnable updateBoards = new Runnable() {
		@Override
		public void run() {
			game.arenas.get(arena).getActives().forEach(game::createBoard);
		}
	};
	
	// Spawns villagers randomly
	private void spawnVillagers(int arena, int round, List<Location> spawns) {
		Random r = new Random();
		for (int i = 0;
			 i < plugin.getConfig().getInt("waves.wave" + round + ".vlgr") - game.arenas.get(arena).getVillagers();
			 i++) {
			int num = r.nextInt(spawns.size());
			new BukkitRunnable() {

				@Override
				public void run() {
					Villager n = (Villager) spawns.get(num).getWorld()
							.spawnEntity(spawns.get(num), EntityType.VILLAGER);
					n.setCustomName(Utils.format("&aVD" + arena +": Villager"));
					game.arenas.get(arena).incrementVillagers();
				}

			}.runTask(plugin);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(game.arenas.get(arena))));
	}
	
	// Spawns monsters randomly
	private void spawnMonsters(int arena, int round, List<Location> spawns) {
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
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
					game.arenas.get(arena).incrementEnemies();
				}
				
			}.runTaskLater(plugin, 21);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(game.arenas.get(arena))), 22);
	}
}
