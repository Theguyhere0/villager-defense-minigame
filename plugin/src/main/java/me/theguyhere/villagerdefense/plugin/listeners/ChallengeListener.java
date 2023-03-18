package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.GameController;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import me.theguyhere.villagerdefense.plugin.managers.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChallengeListener implements Listener {
    // Prevent using certain item slots
    @EventHandler
    public void onIllegalEquip(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and VDPlayer
        try {
            arena = GameController.getArena(player);
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
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        // Unequip armor
        if (gamer.getChallenges().contains(Challenge.naked())) {
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

        // Drop inventory items
        if (gamer.getChallenges().contains(Challenge.amputee())) {
            ItemStack temp;
            boolean infraction = false;
            for (int i = 9; i < 36; i++) {
                temp = player.getInventory().getItem(i);
                if (temp == null)
                    continue;

                player.getWorld().dropItemNaturally(player.getLocation(), temp);
                player.getInventory().setItem(i, null);
                infraction = true;
            }

            // Notify of infraction
            if (infraction)
                PlayerManager.notifyFailure(player, LanguageManager.errors.amputee);
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
            arena = GameController.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
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
                arena = GameController.getArena(player);
                gamer = arena.getPlayer(player);
            } catch (ArenaNotFoundException | PlayerNotFoundException err) {
                return;
            }

            // Ignore arenas that aren't started
            if (arena.getStatus() != ArenaStatus.ACTIVE)
                return;

            // Make sure player is alive
            if (gamer.getStatus() != PlayerStatus.ALIVE)
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
            if (!(e.getEntity().hasMetadata(VDMob.VD))) return;

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
                gamer = GameController.getArena(player).getPlayer(player);
            } catch (ArenaNotFoundException | PlayerNotFoundException err) {
                return;
            }

            // Check for pacifist challenge
            if (gamer.getChallenges().contains(Challenge.pacifist()))
                // Cancel if not an enemy of the player
                if (!gamer.getEnemies().contains(e.getEntity().getUniqueId()))
                    e.setCancelled(true);
        }
    }

    // Handle inventory clicks
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Arena arena;
        VDPlayer gamer;

        // Try getting player and arena
        try {
            arena = GameController.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Check for amputees
        if (!gamer.getChallenges().contains(Challenge.amputee()))
            return;

        // Disallow shift clicking
        if (e.isShiftClick()) {
            e.setCancelled(true);
            PlayerManager.notifyFailure(player, LanguageManager.errors.amputee);
            return;
        }

        // Ignore empty clicks
        if (e.getCursor() == null || e.getCursor().getType() == Material.AIR)
            return;

        // Disallow clicking into forbidden slots
        if (e.getSlot() >= 9 && e.getSlot() <= 35) {
            e.setCancelled(true);
            PlayerManager.notifyFailure(player, LanguageManager.errors.amputee);
        }
    }

    // Handle inventory drags
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();
        Arena arena;
        VDPlayer gamer;

        // Try getting player and arena
        try {
            arena = GameController.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Check for amputees
        if (!gamer.getChallenges().contains(Challenge.amputee()))
            return;

        // Disallow dragging into forbidden slots
        AtomicBoolean forbidden = new AtomicBoolean(false);
        e.getInventorySlots().forEach(slot -> {
            if (slot >= 9 && slot <= 35)
                forbidden.set(true);
        });
        if (forbidden.get()) {
            e.setCancelled(true);
            PlayerManager.notifyFailure(player, LanguageManager.errors.amputee);
        }
    }

    // Handle picking up items
    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        // Check for player
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        Arena arena;
        VDPlayer gamer;

        // Try getting player and arena
        try {
            arena = GameController.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Check for amputees
        if (!gamer.getChallenges().contains(Challenge.amputee()))
            return;

        // Check for room to pick up item
        boolean full = true;
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR)
                full = false;
        }

        // Cancel for full inventory
        if (full)
            e.setCancelled(true);
    }
}
