package me.theguyhere.villagerdefense.plugin.game.achievements.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.entities.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class BonusListener implements Listener {
    // Damage reduction
    @EventHandler
    public void onPlayerHurt(EntityDamageEvent e) {
        // Check for player taking damage
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            gamer = GameManager.getArena(player).getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Check if player has damage reduction achievement and is boosted
        FileConfiguration playerData = Main.getPlayerData();
        String path = player.getUniqueId() + ".achievements";
        if (!playerData.contains(path))
            return;
        if (gamer.isBoosted() && playerData.getStringList(path).contains(Achievement.totalKills9().getID()))
            e.setDamage(e.getDamage() * .9);
    }

    // Damage increase
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        // Check for living entity
        if (!(e.getEntity() instanceof LivingEntity)) return;

        // Check damage was done to monster
        if (!(e.getEntity().hasMetadata("VD"))) return;

        // Check that a player caused the damage
        if (!(e.getDamager() instanceof Player || e.getDamager() instanceof Projectile)) return;

        Player player;
        VDPlayer gamer;

        // Check if projectile came from player, then set player
        if (e.getDamager() instanceof Projectile) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player)
                player = (Player) ((Projectile) e.getDamager()).getShooter();
            else return;
        } else player = (Player) e.getDamager();
        assert player != null;

        // Attempt to get arena and player
        try {
            gamer = GameManager.getArena(player).getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Check if player has damage increase achievement and is boosted
        FileConfiguration playerData = Main.getPlayerData();
        String path = player.getUniqueId() + ".achievements";
        if (!playerData.contains(path))
            return;
        if (gamer.isBoosted() && playerData.getStringList(path).contains(Achievement.topKills9().getID()))
            e.setDamage(e.getDamage() * 1.1);
    }
}
