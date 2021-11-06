package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Challenge;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.GameItems;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class ChallengeListener implements Listener {
    private final Main plugin;

    public ChallengeListener(Main plugin) {
        this.plugin = plugin;
    }

    // Prevent using certain item slots
    @EventHandler
    public void onIllegalEquip(PlayerStatisticIncrementEvent e) {
        if (!e.getStatistic().equals(Statistic.TIME_SINCE_REST))
            return;

        Player player = e.getPlayer();
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            gamer = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                    .collect(Collectors.toList()).get(0).getPlayer(player);
        } catch (Exception err) {
            return;
        }

        // Ignore creative and spectator mode players
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        FileConfiguration language = plugin.getLanguageData();

        // Get armor
        ItemStack off = player.getInventory().getItemInOffHand();
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        // Unequip off hand
        if (gamer.getChallenges().contains(Challenge.amputee()) && off.getType() != Material.AIR) {
            Utils.giveItem(player, off, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setItemInOffHand(null);
            player.sendMessage(Utils.notify(language.getString("amputee")));
        }

        // Unequip armor
        if (!gamer.getChallenges().contains(Challenge.naked()))
            return;
        if (!(helmet == null || helmet.getType() == Material.AIR)) {
            Utils.giveItem(player, helmet, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setHelmet(null);
            player.sendMessage(Utils.notify(language.getString("naked")));
        }
        if (!(chestplate == null || chestplate.getType() == Material.AIR)) {
            Utils.giveItem(player, chestplate, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setChestplate(null);
            player.sendMessage(Utils.notify(language.getString("naked")));
        }
        if (!(leggings == null || leggings.getType() == Material.AIR)) {
            Utils.giveItem(player, leggings, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setLeggings(null);
            player.sendMessage(Utils.notify(language.getString("naked")));
        }
        if (!(boots == null || boots.getType() == Material.AIR)) {
            Utils.giveItem(player, boots, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setBoots(null);
            player.sendMessage(Utils.notify(language.getString("naked")));
        }
    }

    // Handling interactions with items
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        VDPlayer gamer;

        // Attempt to get player
        try {
            gamer = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                    .collect(Collectors.toList()).get(0).getPlayer(player);
        } catch (Exception err) {
            return;
        }

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
        if (r.nextDouble() < dropChance)
            if (e.getHand() == EquipmentSlot.HAND)
                player.dropItem(true);
            else if (item != null) {
                player.getWorld().dropItem(player.getLocation(), item);
                Objects.requireNonNull(player.getEquipment()).setItemInOffHand(null);
            }
    }

    // Handle taking damage
    @EventHandler
    public void onPlayerHurt(EntityDamageByEntityEvent e) {
        // Player hurt
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Entity enemy = e.getDamager();
            VDPlayer gamer;

            // Attempt to get player
            try {
                gamer = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                        .collect(Collectors.toList()).get(0).getPlayer(player);
            } catch (Exception err) {
                return;
            }

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

            // Attempt to get VDplayer
            try {
                gamer = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                        .collect(Collectors.toList()).get(0).getPlayer(player);
            } catch (Exception err) {
                return;
            }

            // Check for pacifist challenge
            if (gamer.getChallenges().contains(Challenge.pacifist()))
                // Cancel if not an enemy of the player
                if (!gamer.getEnemies().contains(e.getEntity().getUniqueId()))
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
        VDPlayer gamer;

        // Attempt to get player
        try {
            gamer = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                    .collect(Collectors.toList()).get(0).getPlayer(player);
        } catch (Exception err) {
            return;
        }

        // Check for blind challenge
        if (!gamer.getChallenges().contains(Challenge.blind()))
            return;

        // Add back blindness
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 0)), 2);
    }

    // UHC effect
    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        // Check for player
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            gamer = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                    .collect(Collectors.toList()).get(0).getPlayer(player);
        } catch (Exception err) {
            return;
        }

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
