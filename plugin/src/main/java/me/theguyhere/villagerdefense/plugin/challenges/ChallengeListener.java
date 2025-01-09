package me.theguyhere.villagerdefense.plugin.challenges;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.entities.VDEntity;
import me.theguyhere.villagerdefense.plugin.entities.players.LegacyVDPlayer;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.entities.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.entities.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.menuItems.Shop;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class ChallengeListener implements Listener {
	// Handling interactions with items
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Arena arena;
		LegacyVDPlayer gamer;

		// Attempt to get arena and LegacyVDPlayer
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			return;
		}

		// Ignore arenas that aren't started
		if (arena.getStatus() != ArenaStatus.ACTIVE)
			return;

		ItemStack item = e.getItem();

		// Ignore shop item or essence
		if (Shop.matches(item) || VDAbility.matches(item))
			return;

		// Check for clumsy challenge
		if (!gamer
			.getChallenges()
			.contains(Challenge.clumsy()))
			return;

		double dropChance = .02;
		Random r = new Random();

		// See if item should be dropped
		if (r.nextDouble() < dropChance) {
			if (item == null)
				return;

			player
				.getWorld()
				.dropItem(player.getLocation(), item);

			if (e.getHand() == EquipmentSlot.HAND) {
				Objects
					.requireNonNull(player.getEquipment())
					.setItemInMainHand(null);
			}
			else {
				Objects
					.requireNonNull(player.getEquipment())
					.setItemInOffHand(null);
			}
		}
	}

	// Handle taking damage
	@EventHandler
	public void onPlayerHurt(EntityDamageByEntityEvent e) {
		// Player hurt
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			Entity enemy = e.getDamager();
			Arena arena;
			LegacyVDPlayer gamer;

			// Attempt to get arena and LegacyVDPlayer
			try {
				arena = GameController.getArena(player);
				gamer = arena.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Ignore arenas that aren't started
			if (arena.getStatus() != ArenaStatus.ACTIVE)
				return;

			// Make sure player is alive
			if (gamer.getStatus() != LegacyVDPlayer.Status.ALIVE)
				return;

			// Check for featherweight challenge
			if (gamer
				.getChallenges()
				.contains(Challenge.featherweight()) && !e.isCancelled())
				player.setVelocity(enemy
					.getLocation()
					.getDirection()
					.setY(0)
					.normalize()
					.multiply(5));

			// Get proper enemy and check for pacifist challenge
			if (gamer
				.getChallenges()
				.contains(Challenge.pacifist())) {
				if (enemy instanceof Projectile)
					gamer.addEnemy(((Entity) Objects.requireNonNull(((Projectile) enemy).getShooter())).getUniqueId());
				else gamer.addEnemy(enemy.getUniqueId());
			}
		}

		// Mob hurt
		else {
			// Check damage was done to monster
			if (!VDMob.isTeam(e.getEntity(), VDEntity.Team.MONSTER))
				return;

			Player player;
			LegacyVDPlayer gamer;

			// Check for player damager, then get player
			if (e.getDamager() instanceof Player)
				player = (Player) e.getDamager();
			else if (e.getDamager() instanceof Projectile &&
				((Projectile) e.getDamager()).getShooter() instanceof Player)
				player = (Player) ((Projectile) e.getDamager()).getShooter();
			else return;

			// Attempt to get LegacyVDPlayer
			try {
				gamer = GameController
					.getArena(player)
					.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Check for pacifist challenge
			if (gamer
				.getChallenges()
				.contains(Challenge.pacifist()))
				// Cancel if not an enemy of the player
				if (!gamer
					.getEnemies()
					.contains(e
						.getEntity()
						.getUniqueId()))
					e.setCancelled(true);
		}
	}
}
