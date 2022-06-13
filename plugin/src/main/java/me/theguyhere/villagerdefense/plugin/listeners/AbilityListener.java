package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.EndNinjaNerfEvent;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import me.theguyhere.villagerdefense.plugin.tools.WorldManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class AbilityListener implements Listener {
    private final Map<VDPlayer, Long> cooldowns = new HashMap<>();

    // Most ability functionalities
    @EventHandler
    public void onAbility(PlayerInteractEvent e) {
        // Check for right click
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (Exception err) {
            return;
        }

        ItemStack item = e.getItem();
        ItemStack main = player.getInventory().getItemInMainHand();

        // Avoid accidental usage when holding food, shop, ranged weapons, potions, or care packages
        if (GameItems.shop().equals(main) ||
                Arrays.stream(GameItems.FOOD_MATERIALS).anyMatch(m -> m == main.getType()) ||
                Arrays.stream(GameItems.ARMOR_MATERIALS).anyMatch(m -> m == main.getType()) ||
                Arrays.stream(GameItems.CARE_MATERIALS).anyMatch(m -> m == main.getType()) ||
                Arrays.stream(GameItems.CLICKABLE_WEAPON_MATERIALS).anyMatch(m -> m == main.getType()) ||
                Arrays.stream(GameItems.CLICKABLE_CONSUME_MATERIALS).anyMatch(m -> m == main.getType()))
            return;

        // Ensure cooldown is initialized
        if (!cooldowns.containsKey(gamer))
            cooldowns.put(gamer, 0L);

        // Get effective player level
        int level1 = player.getLevel();
        if (gamer.getKit().getLevel() == 1 && level1 > 10)
            level1 = 10;
        else if (gamer.getKit().getLevel() == 2 && level1 > 20)
            level1 = 20;
        else if (gamer.getKit().getLevel() == 3 && level1 > 30)
            level1 = 30;

        int level2 = player.getLevel();
        if (gamer.getKit2() != null) {
            if (gamer.getKit2().getLevel() == 1 && level2 > 10)
                level2 = 10;
            else if (gamer.getKit2().getLevel() == 2 && level2 > 20)
                level2 = 20;
            else if (gamer.getKit2().getLevel() == 3 && level2 > 30)
                level2 = 30;
        }

        long dif = cooldowns.get(gamer) - System.currentTimeMillis();

        // Mage
        if (Kit.mage().getName().equals(gamer.getKit().getName()) && GameItems.mage().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int coolDown = Utils.secondsToMillis(13 - Math.pow(Math.E, (level1 - 1) / 12d));
            float yield = 1 + level1 * .05f;

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
            fireball.setYield(yield);
            fireball.setShooter(player);
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
        }

        // Ninja
        if (Kit.ninja().getName().equals(gamer.getKit().getName()) && GameItems.ninja().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int coolDown = normalCooldown(level1);
            int duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level1 - 1) / 8.5));

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0));
            WorldManager.getPets(player).forEach(wolf ->
                    wolf.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0))));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            gamer.hideArmor();

            // Schedule un-nerf
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                    Bukkit.getPluginManager().callEvent(new EndNinjaNerfEvent(gamer)), duration);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        }

        // Templar
        if (Kit.templar().getName().equals(gamer.getKit().getName()) && GameItems.templar().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int duration, amplifier;
            int coolDown = normalCooldown(level1);
            double range = 2 + level1 * .1d;
            if (level1 > 20) {
                duration = normalDuration20Plus(level1);
                amplifier = 2;
            }
            else if (level1 > 10) {
                duration = normalDuration10Plus(level1);
                amplifier = 1;
            }
            else {
                duration = normalDuration(level1);
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
            WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Warrior
        if (Kit.warrior().getName().equals(gamer.getKit().getName()) && GameItems.warrior().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int duration, amplifier;
            int coolDown = normalCooldown(level1);
            double range = 2 + level1 * .1d;
            if (level1 > 20) {
                duration = normalDuration20Plus(level1);
                amplifier = 2;
            }
            else if (level1 > 10) {
                duration = normalDuration10Plus(level1);
                amplifier = 1;
            }
            else {
                duration = normalDuration(level1);
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
            WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Knight
        if (Kit.knight().getName().equals(gamer.getKit().getName()) && GameItems.knight().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int duration, amplifier;
            int coolDown = normalCooldown(level1);
            double range = 2 + level1 * .1d;
            if (level1 > 20) {
                duration = normalDuration20Plus(level1);
                amplifier = 2;
            }
            else if (level1 > 10) {
                duration = normalDuration10Plus(level1);
                amplifier = 1;
            }
            else {
                duration = normalDuration(level1);
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
            WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Priest
        if (Kit.priest().getName().equals(gamer.getKit().getName()) && GameItems.priest().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int duration, amplifier;
            int coolDown = normalCooldown(level1);
            double range = 2 + level1 * .1d;
            if (level1 > 20) {
                duration = normalDuration20Plus(level1);
                amplifier = 2;
            }
            else if (level1 > 10) {
                duration = normalDuration10Plus(level1);
                amplifier = 1;
            }
            else {
                duration = normalDuration(level1);
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
            WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Siren
        if (Kit.siren().getName().equals(gamer.getKit().getName()) && GameItems.siren().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int duration, amp1, amp2;
            int coolDown = Utils.secondsToMillis(26 - Math.pow(Math.E, (level1 - 1) / 12d));
            double range = 3 + level1 * .1d;
            if (level1 > 20) {
                duration = normalDuration20Plus(level1);
                amp1 = 1;
                amp2 = 0;
            }
            else if (level1 > 10) {
                duration = normalDuration10Plus(level1);
                amp1 = 1;
                amp2 = -1;
            }
            else {
                duration = normalDuration(level1);
                amp1 = 0;
                amp2 = -1;
            }
            int altDuration = (int) (.4 * duration);

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            WorldManager.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                    new PotionEffect(PotionEffectType.SLOW, duration, amp1)));
            if (amp2 != -1)
                WorldManager.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                        new PotionEffect(PotionEffectType.WEAKNESS, altDuration, amp2)));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1.25f));
        }

        // Monk
        if (Kit.monk().getName().equals(gamer.getKit().getName()) && GameItems.monk().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int duration, amplifier;
            int coolDown = normalCooldown(level1);
            double range = 2 + level1 * .1d;
            if (level1 > 20) {
                duration = normalDuration20Plus(level1);
                amplifier = 2;
            }
            else if (level1 > 10) {
                duration = normalDuration10Plus(level1);
                amplifier = 1;
            }
            else {
                duration = normalDuration(level1);
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
            WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Messenger
        if (Kit.messenger().getName().equals(gamer.getKit().getName()) && GameItems.messenger().equals(item)) {
            // Perform checks
            if (checkLevel(level1, player))
                return;
            if (checkCooldown(dif, player))
                return;

            // Calculate stats
            int duration, amplifier;
            int coolDown = normalCooldown(level1);
            double range = 2 + level1 * .1d;
            if (level1 > 20) {
                duration = normalDuration20Plus(level1);
                amplifier = 2;
            }
            else if (level1 > 10) {
                duration = normalDuration10Plus(level1);
                amplifier = 1;
            }
            else {
                duration = normalDuration(level1);
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Check if player has cooldown decrease achievement and is boosted
            FileConfiguration playerData = Main.plugin.getPlayerData();
            String path = player.getUniqueId() + ".achievements";
            if (playerData.contains(path) && gamer.isBoosted() &&
                    playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                coolDown *= .9;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
            WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        if (gamer.getKit2() != null) {
            // Mage
            if (Kit.mage().nameCompare(gamer.getKit2()) && GameItems.mage().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int coolDown = Utils.secondsToMillis(13 - Math.pow(Math.E, (level2 - 1) / 12d));
                float yield = 1 + level2 * .05f;

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
                fireball.setYield(yield);
                fireball.setShooter(player);
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            }

            // Ninja
            if (Kit.ninja().nameCompare(gamer.getKit2()) && GameItems.ninja().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int coolDown = normalCooldown(level2);
                int duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level2 - 1) / 8.5));

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0));
                WorldManager.getPets(player).forEach(wolf ->
                        wolf.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0))));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
                gamer.hideArmor();

                // Schedule un-nerf
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                        Bukkit.getPluginManager().callEvent(new EndNinjaNerfEvent(gamer)), duration);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
            }

            // Templar
            if (Kit.templar().nameCompare(gamer.getKit2()) && GameItems.templar().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int duration, amplifier;
                int coolDown = normalCooldown(level2);
                double range = 2 + level2 * .1d;
                if (level2 > 20) {
                    duration = normalDuration20Plus(level2);
                    amplifier = 2;
                } else if (level2 > 10) {
                    duration = normalDuration10Plus(level2);
                    amplifier = 1;
                } else {
                    duration = normalDuration(level2);
                    amplifier = 0;
                }
                int altDuration = (int) (.6 * duration);

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
                WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                            .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
            }

            // Warrior
            if (Kit.warrior().nameCompare(gamer.getKit2()) && GameItems.warrior().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int duration, amplifier;
                int coolDown = normalCooldown(level2);
                double range = 2 + level2 * .1d;
                if (level2 > 20) {
                    duration = normalDuration20Plus(level2);
                    amplifier = 2;
                } else if (level2 > 10) {
                    duration = normalDuration10Plus(level2);
                    amplifier = 1;
                } else {
                    duration = normalDuration(level2);
                    amplifier = 0;
                }
                int altDuration = (int) (.6 * duration);

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
                WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                            .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
            }

            // Knight
            if (Kit.knight().nameCompare(gamer.getKit2()) && GameItems.knight().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int duration, amplifier;
                int coolDown = normalCooldown(level2);
                double range = 2 + level2 * .1d;
                if (level2 > 20) {
                    duration = normalDuration20Plus(level2);
                    amplifier = 2;
                } else if (level2 > 10) {
                    duration = normalDuration10Plus(level2);
                    amplifier = 1;
                } else {
                    duration = normalDuration(level2);
                    amplifier = 0;
                }
                int altDuration = (int) (.6 * duration);

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
                WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                            .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
            }

            // Priest
            if (Kit.priest().nameCompare(gamer.getKit2()) && GameItems.priest().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int duration, amplifier;
                int coolDown = normalCooldown(level2);
                double range = 2 + level2 * .1d;
                if (level2 > 20) {
                    duration = normalDuration20Plus(level2);
                    amplifier = 2;
                } else if (level2 > 10) {
                    duration = normalDuration10Plus(level2);
                    amplifier = 1;
                } else {
                    duration = normalDuration(level2);
                    amplifier = 0;
                }
                int altDuration = (int) (.6 * duration);

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
                WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                            .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
            }

            // Siren
            if (Kit.siren().nameCompare(gamer.getKit2()) && GameItems.siren().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int duration, amp1, amp2;
                int coolDown = Utils.secondsToMillis(26 - Math.pow(Math.E, (level2 - 1) / 12d));
                double range = 3 + level2 * .1d;
                if (level2 > 20) {
                    duration = normalDuration20Plus(level2);
                    amp1 = 1;
                    amp2 = 0;
                } else if (level2 > 10) {
                    duration = normalDuration10Plus(level2);
                    amp1 = 1;
                    amp2 = -1;
                } else {
                    duration = normalDuration(level2);
                    amp1 = 0;
                    amp2 = -1;
                }
                int altDuration = (int) (.4 * duration);

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                WorldManager.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOW, duration, amp1)));
                if (amp2 != -1)
                    WorldManager.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                            new PotionEffect(PotionEffectType.WEAKNESS, altDuration, amp2)));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                            .playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1.25f));
            }

            // Monk
            if (Kit.monk().nameCompare(gamer.getKit2()) && GameItems.monk().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int duration, amplifier;
                int coolDown = normalCooldown(level2);
                double range = 2 + level2 * .1d;
                if (level2 > 20) {
                    duration = normalDuration20Plus(level2);
                    amplifier = 2;
                } else if (level2 > 10) {
                    duration = normalDuration10Plus(level1);
                    amplifier = 1;
                } else {
                    duration = normalDuration(level2);
                    amplifier = 0;
                }
                int altDuration = (int) (.6 * duration);

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
                WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                            .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
            }

            // Messenger
            if (Kit.messenger().nameCompare(gamer.getKit2()) && GameItems.messenger().equals(item)) {
                // Perform checks
                if (checkLevel(level2, player))
                    return;
                if (checkCooldown(dif, player))
                    return;

                // Calculate stats
                int duration, amplifier;
                int coolDown = normalCooldown(level2);
                double range = 2 + level2 * .1d;
                if (level2 > 20) {
                    duration = normalDuration20Plus(level2);
                    amplifier = 2;
                } else if (level2 > 10) {
                    duration = normalDuration10Plus(level2);
                    amplifier = 1;
                } else {
                    duration = normalDuration(level2);
                    amplifier = 0;
                }
                int altDuration = (int) (.6 * duration);

                // Check if player has cooldown decrease achievement and is boosted
                FileConfiguration playerData = Main.plugin.getPlayerData();
                String path = player.getUniqueId() + ".achievements";
                if (playerData.contains(path) && gamer.isBoosted() &&
                        playerData.getStringList(path).contains(Achievement.allMaxedAbility().getID()))
                    coolDown *= .9;

                // Activate ability
                WorldManager.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
                WorldManager.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

                // Fire ability sound if turned on
                if (arena.hasAbilitySound())
                    arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                            .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
            }
        }
    }

    // Vampire healing
    @EventHandler
    public void onVampire(EntityDamageByEntityEvent e) {
        // Ignore cancelled events
        if (e.isCancelled())
            return;

        Entity ent = e.getEntity();
        Entity damager = e.getDamager();

        // Check if damage was done by player to valid monsters
        if (!(ent instanceof Monster || ent instanceof Slime || ent instanceof Hoglin) ||
                !(damager instanceof Player))
            return;

        Player player = (Player) damager;
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (Exception err) {
            return;
        }

        Random r = new Random();
        double damage = e.getFinalDamage();

        // Check for vampire kit
        if ((Kit.vampire().getName().equals(gamer.getKit().getName()) ||
                Kit.vampire().nameCompare(gamer.getKit2())) && !gamer.isSharing()) {
            // Heal if probability is right
            if (r.nextInt(50) < damage)
                player.setHealth(Math.min(player.getHealth() + 1,
                        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));
        }

        // Check for shared vampire effect
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.VAMPIRE))) {
            // Heal if probability is right
            if (r.nextInt(50) < damage) {
                player.setHealth(Math.min(player.getHealth() + 1,
                        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));
                PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
            }
        }
    }

    // Ninja stealth
    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        Entity ent = e.getEntity();
        Entity target = e.getTarget();

        // Check for arena mobs
        if (!ent.hasMetadata("VD"))
            return;

        // Cancel for invisible players
        if (target instanceof Player) {
            Player player = (Player) target;

            if (player.getActivePotionEffects().stream()
                    .anyMatch(potion -> potion.getType().equals(PotionEffectType.INVISIBILITY)))
                e.setCancelled(true);
        }

        // Cancel for invisible wolves
        if (target instanceof Wolf) {
            Wolf wolf = (Wolf) target;

            if (wolf.getActivePotionEffects().stream()
                    .anyMatch(potion -> potion.getType().equals(PotionEffectType.INVISIBILITY)))
                e.setCancelled(true);
        }
    }

    // Ninja stun
    @EventHandler
    public void onStun(EntityDamageByEntityEvent e) {
        Entity ent = e.getEntity();
        Entity damager = e.getDamager();

        // Check for arena enemies
        if (!ent.hasMetadata("VD"))
            return;

        // Check for player or wolf dealing damage
        if (!(damager instanceof Player || damager instanceof Wolf))
            return;

        // Check for mob taking damage
        if (!(ent instanceof Mob))
            return;

        Mob mob = (Mob) ent;
        LivingEntity stealthy = (LivingEntity) damager;

        // Check for invisibility
        if (stealthy.getActivePotionEffects().stream()
                .noneMatch(potion -> potion.getType().equals(PotionEffectType.INVISIBILITY)))
            return;

        // Set target to null if not already
        if (mob.getTarget() != null)
            mob.setTarget(null);
    }

    // Ninja nerf
    @EventHandler
    public void onInvisibleEquip(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        // Check if player is in a game
        if (!GameManager.checkPlayer(player))
            return;

        // Ignore creative and spectator mode players
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        // Ignore if not invisible
        if (player.getActivePotionEffects().stream()
                .noneMatch(potion -> potion.getType().equals(PotionEffectType.INVISIBILITY)))
            return;

        // Get armor
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        // Unequip armor
        if (!(helmet == null || helmet.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, helmet, LanguageManager.errors.inventoryFull);
            player.getInventory().setHelmet(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.ninja);
        }
        if (!(chestplate == null || chestplate.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, chestplate, LanguageManager.errors.inventoryFull);
            player.getInventory().setChestplate(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.ninja);
        }
        if (!(leggings == null || leggings.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, leggings, LanguageManager.errors.inventoryFull);
            player.getInventory().setLeggings(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.ninja);
        }
        if (!(boots == null || boots.getType() == Material.AIR)) {
            PlayerManager.giveItem(player, boots, LanguageManager.errors.inventoryFull);
            player.getInventory().setBoots(null);
            PlayerManager.notifyFailure(player, LanguageManager.errors.ninja);
        }
    }

    private boolean checkLevel(int level, Player player) {
        if (level == 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    CommunicationManager.format("&c" + LanguageManager.errors.level)));
            return true;
        }
        return false;
    }

    private boolean checkCooldown(long dif, Player player) {
        if (dif > 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    CommunicationManager.format(ChatColor.RED, LanguageManager.errors.cooldown, ChatColor.AQUA,
                            String.valueOf(Math.round(Utils.millisToSeconds(dif) * 10) / 10d))));
            return true;
        }
        return false;
    }

    private static int normalCooldown(int level) {
        return Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
    }

    private static int normalDuration(int level) {
        return Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
    }

    private static int normalDuration10Plus(int level) {
        return Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
    }

    private static int normalDuration20Plus(int level) {
        return Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
    }
}
