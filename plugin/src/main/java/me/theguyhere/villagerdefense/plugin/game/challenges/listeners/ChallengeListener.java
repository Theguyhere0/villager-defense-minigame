package me.theguyhere.villagerdefense.plugin.game.challenges.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.entities.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.items.GameItems;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class ChallengeListener implements Listener {
    // Prevent using certain item slots
    @EventHandler
    public void onIllegalEquip(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and VDPlayer
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Ignore arenas that aren't started
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return;

        // Ignore creative and spectator mode players
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        // Get armor
        ItemStack off = player.getInventory().getItemInOffHand();
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        // Unequip off-hand
        if (gamer.getChallenges().contains(Challenge.amputee()) && off.getType() != Material.AIR) {
            PlayerManager.giveItem(player, off, LanguageManager.errors.inventoryFull);
            player.getInventory().setItemInOffHand(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.amputee);
        }

        // Unequip armor
        if (!gamer.getChallenges().contains(Challenge.naked()))
            return;
        if (!(helmet == null || helmet.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, helmet, LanguageManager.errors.inventoryFull);
            player.getInventory().setHelmet(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.naked);
        }
        if (!(chestplate == null || chestplate.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, chestplate, LanguageManager.errors.inventoryFull);
            player.getInventory().setChestplate(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.naked);
        }
        if (!(leggings == null || leggings.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, leggings, LanguageManager.errors.inventoryFull);
            player.getInventory().setLeggings(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.naked);
        }
        if (!(boots == null || boots.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, boots, LanguageManager.errors.inventoryFull);
            player.getInventory().setBoots(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.naked);
        }
    }

    // Handling interactions with items
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and VDPlayer
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Ignore arenas that aren't started
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return;

        ItemStack item = e.getItem();

        // Ignore shop item
        if (GameItems.shop().equals(item))
            return;

        // Check for clumsy challenge
        if (!gamer.getChallenges().contains(Challenge.clumsy()))
            return;

        double dropChance = .02;
        Random r = new Random();

        // See if item should be dropped
        if (r.nextDouble() < dropChance) {
            if (item == null)
                return;

            player.getWorld().dropItem(player.getLocation(), item);

            if (e.getHand() == EquipmentSlot.HAND) {
                Objects.requireNonNull(player.getEquipment()).setItemInMainHand(null);
            }
            else {
                Objects.requireNonNull(player.getEquipment()).setItemInOffHand(null);
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
            VDPlayer gamer;

            // Attempt to get arena and VDPlayer
            try {
                arena = GameManager.getArena(player);
                gamer = arena.getPlayer(player);
            } catch (ArenaNotFoundException | PlayerNotFoundException err) {
                return;
            }

            // Make sure enemy is not the projectile
            if (enemy instanceof Projectile) {
                enemy = (Entity) ((Projectile) enemy).getShooter();
            }

            // Ignore arenas that aren't started
            if (arena.getStatus() != ArenaStatus.ACTIVE)
                return;

            // Make sure player is alive
            if (gamer.getStatus() != VDPlayer.Status.ALIVE)
                return;

            // Check for featherweight challenge
            if (gamer.getChallenges().contains(Challenge.featherweight()))
                player.setVelocity(enemy.getLocation().getDirection().setY(0).normalize().multiply(5));

            // Check for pacifist challenge
            if (gamer.getChallenges().contains(Challenge.pacifist()))
                gamer.addEnemy(enemy.getUniqueId());
        }

        // Mob hurt
        else {
            // Check damage was done to monster
            if (!(e.getEntity().hasMetadata("VD"))) return;

            Player player;
            VDPlayer gamer;

            // Check for player damager, then get player
            if (e.getDamager() instanceof Player)
                player = (Player) e.getDamager();
            else if (e.getDamager() instanceof Projectile &&
                    ((Projectile) e.getDamager()).getShooter() instanceof Player)
                player = (Player) ((Projectile) e.getDamager()).getShooter();
            else return;

            // Attempt to get VDPlayer
            try {
                gamer = GameManager.getArena(player).getPlayer(player);
            } catch (ArenaNotFoundException | PlayerNotFoundException err) {
                return;
            }

            // Check for pacifist challenge
            if (gamer.getChallenges().contains(Challenge.pacifist()))
                // Cancel if not an enemy of the player and is not a baby slime
                if (!gamer
                    .getEnemies()
                    .contains(e
                        .getEntity()
                        .getUniqueId()) &&
                    !(e.getEntity() instanceof Slime && !(e.getEntity() instanceof MagmaCube) &&
                        ((Slime) e.getEntity()).getSize() < 2))
                    e.setCancelled(true);
        }
    }

    // Ensure blindness even after milk
    @EventHandler
    public void onMilk(PlayerItemConsumeEvent e) {
        // Check for milk bucket
        if (e.getItem().getType() != Material.MILK_BUCKET)
            return;

        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and VDPlayer
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Ignore arenas that aren't started
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return;

        // Check for blind challenge
        if (!gamer.getChallenges().contains(Challenge.blind()))
            return;

        // Add back blindness
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 0)), 2);
    }

    // UHC effect
    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        // Check for player
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and VDPlayer
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Ignore arenas that aren't started
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return;

        // Check for uhc challenge
        if (!gamer.getChallenges().contains(Challenge.uhc()))
            return;

        // Negate natural health regain and manage saturation
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
            e.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING) {
            e.setCancelled(true);
            player.setSaturation(player.getSaturation() + 1.5f);
        }
    }
}
