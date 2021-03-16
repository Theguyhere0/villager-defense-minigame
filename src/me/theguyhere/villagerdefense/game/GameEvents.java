package me.theguyhere.villagerdefense.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.theguyhere.villagerdefense.Main;

public class GameEvents implements Listener {
	private final Main plugin;
	private final Game game;
	private final GameItems gi;

	public GameEvents (Main plugin, Game game, GameItems gi) {
		this.plugin = plugin;
		this.game = game;
		this.gi = gi;
	}
	
//	Keep score and drop gems
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		Entity ent = e.getEntity();
		if (!ent.getName().contains("VD"))
			return;
		String arena = ent.getName().substring(4, 5);
		if (ent instanceof Villager) {
			game.villagers.put(arena, game.villagers.get(arena) - 1);
		}
		else {
			e.getDrops().clear();
			ItemStack gem = new ItemStack(Material.EMERALD);
			ItemMeta meta = gem.getItemMeta();
			meta.setDisplayName(arena);
			gem.setItemMeta(meta);
			e.getDrops().add(gem);
			game.enemies.put(arena, game.enemies.get(arena) - 1);
			if (game.enemies.get(arena) == 0) {
				
			}
		}
	}
	
//	Open shop
	@EventHandler
	public void onShop(PlayerInteractEvent e) {
		Player player = (Player) e.getPlayer();
		if (!game.playing.containsKey(player.getName()))
			return;
		else if (!player.getEquipment().getItemInMainHand().equals(gi.shop()))
			return;
		player.openInventory(game.shops.get(game.playing.get(player.getName())));
	}
	
//	Purchase items
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().contains(Utils.format("&2&lItem Shop"))) {
			Player player = (Player) e.getWhoClicked();
			if (!game.playing.containsKey(player.getName()))
				return;
			if (e.getClickedInventory().getType() == InventoryType.PLAYER)
				return;
			e.setCancelled(true);
			
			ItemStack buy = e.getClickedInventory().getItem(e.getSlot()).clone();
			ItemMeta meta = buy.getItemMeta();
			int cost = Integer.parseInt(meta.getLore().get(0).substring(meta.getLore().get(0).length() - 4).replaceAll(" ", ""));

			if (game.gems.get(player.getName()) < cost)
				return;
			List<String> lore = new ArrayList<String>();
			meta.setLore(lore);
			buy.setItemMeta(meta);
			
			game.gems.put(player.getName(), game.gems.get(player.getName()) - cost);
			player.getInventory().addItem(buy);
		}

	}
	
//	Stops players from hurting villagers and other players
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent e) {
		Entity ent = e.getEntity();
		if (!ent.getName().contains("VD"))
			return;
		if (ent instanceof Villager)
			if (e.getDamager() instanceof Player) {
				e.setCancelled(true);
			}
		if (ent instanceof Player)
			if (e.getDamager() instanceof Player) {
				Player player = (Player) ent;
				if (game.playing.containsKey(player.getName()))
					e.setCancelled(true);
			}
	}
	
//	Give gems
	@EventHandler
	public void onGemPickup(EntityPickupItemEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player player = (Player) e.getEntity();
		if (!e.getItem().getItemStack().getType().equals(Material.EMERALD))
			return;
		int stack = e.getItem().getItemStack().getAmount();
		if (game.gems.containsKey(player.getName())) {
			String arena = e.getItem().getItemStack().getItemMeta().getDisplayName();
			int wave = plugin.getData().getInt("a" + arena + ".currentWave");
			Random r = new Random();
			Integer num = r.nextInt(Math.toIntExact(Math.round(40 * Math.pow(wave, 1 / (2 + Math.pow(Math.E, -wave + 3))))));
			e.setCancelled(true);
			e.getItem().remove();
			game.gems.put(player.getName(), game.gems.get(player.getName()) + num * stack);
			player.sendMessage(Utils.format("&fYou found &a" + (num * stack) + "&f gem(s)!"));
		}
	}
	
//	Handle player death
	@EventHandler
	public void onPlayerDeath(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player player = (Player) e.getEntity();
		if (!game.playing.containsKey(player.getName()))
			return;
		if (e.getFinalDamage() < player.getHealth())
			return;
		e.setCancelled(true);
		player.setGameMode(GameMode.SPECTATOR);
		player.getInventory().clear();
	}
	
//	Stops slimes and magma cubes from splitting on death
	@EventHandler
	public void onSplit(SlimeSplitEvent e) {
		Entity ent = e.getEntity();
		if (!ent.getName().contains("VD"))
			return;
		e.setCancelled(true);
	}
}
