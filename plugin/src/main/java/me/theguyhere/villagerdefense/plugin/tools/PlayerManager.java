package me.theguyhere.villagerdefense.plugin.tools;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Class to manage player manipulations.
 */
public class PlayerManager {
    // Gives item to player if possible, otherwise drops at feet
    public static void giveItem(Player player, ItemStack item, String message) {
        // Inventory is full
        if (player.getInventory().firstEmpty() == -1 && (player.getInventory().first(item.getType()) == -1 ||
                (player.getInventory().all(new ItemStack(item.getType(), item.getMaxStackSize())).size() ==
                        player.getInventory().all(item.getType()).size()) &&
                        player.getInventory().all(item.getType()).size() != 0)) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            notifyFailure(player, message);
        }

        // Add item to inventory
        else player.getInventory().addItem(item);
    }

    // Prepares and teleports a player into adventure mode
    public static void teleAdventure(Player player, @NotNull Location location) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;

        if (!maxHealth.getModifiers().isEmpty())
            maxHealth.getModifiers().forEach(maxHealth::removeModifier);
        player.setHealth(maxHealth.getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setInvulnerable(false);
        player.getInventory().clear();
        player.teleport(location);
        player.setGameMode(GameMode.ADVENTURE);
        player.setGlowing(false);
    }

    // Prepares and teleports a player into spectator mode
    public static void teleSpectator(Player player, @NotNull Location location) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth == null)
            return;

        if (!maxHealth.getModifiers().isEmpty())
            maxHealth.getModifiers().forEach(maxHealth::removeModifier);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.closeInventory();
        player.teleport(location);
        player.setGlowing(false);
    }

    public static void notify(Player player, @NotNull ChatColor base, String msg) {
        player.sendMessage(CommunicationManager.notify(base + msg));
    }

    public static void notify(Player player, @NotNull ChatColor base, String msg, @NotNull ChatColor replace,
                              String value) {
        player.sendMessage(CommunicationManager.notify(base + String.format(msg, replace + value + base)));
    }

    public static void notify(Player player, @NotNull ChatColor base, String msg, @NotNull ChatColor replace,
                              String value1, String value2) {
        player.sendMessage(CommunicationManager.notify(base + String.format(msg, replace + value1 + base,
                replace + value2 + base)));
    }

    public static void notifySuccess(Player player, String msg) {
        notify(player, ChatColor.GREEN, msg);
    }

    public static void notifySuccess(Player player, String msg, @NotNull ChatColor replace, String value) {
        notify(player, ChatColor.GREEN, msg, replace, value);
    }

    public static void notifySuccess(Player player, String msg, @NotNull ChatColor replace, String value1,
                                     String value2) {
        notify(player, ChatColor.GREEN, msg, replace, value1, value2);
    }

    public static void notifyFailure(Player player, String msg) {
        notify(player, ChatColor.RED, msg);
    }

    public static void notifyFailure(Player player, String msg, @NotNull ChatColor replace, String value) {
        notify(player, ChatColor.RED, msg, replace, value);
    }

    public static void notifyAlert(Player player, String msg) {
        notify(player, ChatColor.GOLD, msg);
    }

    public static void notifyAlert(Player player, String msg, @NotNull ChatColor replace, String value) {
        notify(player, ChatColor.GOLD, msg, replace, value);
    }

    public static void notifyAlert(Player player, String msg, @NotNull ChatColor replace, String value1,
                                   String value2) {
        notify(player, ChatColor.GOLD, msg, replace, value1, value2);
    }

    public static void fakeDeath(VDPlayer vdPlayer) {
        Player player = vdPlayer.getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.closeInventory();
        vdPlayer.setStatus(PlayerStatus.GHOST);
        player.setFallDistance(0);
        player.setGlowing(false);
        player.setVelocity(new Vector());
    }

    public static void sendPacketToOnline(PacketGroup packetGroup) {
        for (Player player : Bukkit.getOnlinePlayers())
            packetGroup.sendTo(player);
    }
}
