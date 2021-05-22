package me.theguyhere.villagerdefense.game.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.Kits;
import me.theguyhere.villagerdefense.game.models.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class AbilityEvents implements Listener {
    private final Main plugin;
    private final Game game;
    private final Map<VDPlayer, Long> cooldowns = new HashMap<>();

    public AbilityEvents(Main plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    // Most ability functionalities
    @EventHandler
    public void onAbility(PlayerInteractEvent e) {
        FileConfiguration language = plugin.getLanguageData();
        
        // Check for right click
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();

        // Avoid accidental usage when holding food, shop, or potions
        if (player.getInventory().getItemInMainHand().getType() == Material.EMERALD ||
                player.getInventory().getItemInMainHand().getType() == Material.BEETROOT ||
                player.getInventory().getItemInMainHand().getType() == Material.CARROT ||
                player.getInventory().getItemInMainHand().getType() == Material.BREAD ||
                player.getInventory().getItemInMainHand().getType() == Material.MUTTON ||
                player.getInventory().getItemInMainHand().getType() == Material.COOKED_BEEF ||
                player.getInventory().getItemInMainHand().getType() == Material.GOLDEN_CARROT ||
                player.getInventory().getItemInMainHand().getType() == Material.GOLDEN_APPLE ||
                player.getInventory().getItemInMainHand().getType() == Material.ENCHANTED_GOLDEN_APPLE ||
                player.getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE
        )
            return;

        // See if the player is in a game
        if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
            return;

        Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                .collect(Collectors.toList()).get(0);
        VDPlayer gamer = arena.getPlayer(player);
        ItemStack item = e.getItem();

        // Ensure cooldown is initialized
        if (!cooldowns.containsKey(gamer))
            cooldowns.put(gamer, 0L);

        // Mage
        if (gamer.getKit().contains("Mage") && Kits.mage().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int coolDown = Utils.secondsToMillis(13 - Math.pow(Math.E, (level - 1) / 12d));
            float yield = 1 + level * .05f;

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
                fireball.setYield(yield);
                fireball.setShooter(player);
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Ninja
        if (gamer.getKit().contains("Ninja") && Kits.ninja().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            int duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 8.5));

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0));
                Utils.getPets(player).forEach(wolf ->
                        wolf.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0))));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Templar
        if (gamer.getKit().contains("Templar") && Kits.templar().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int duration, amplifier;
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            double range = 2 + level * .1d;
            if (level > 20) {
                duration = Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
                amplifier = 2;
            }
            else if (level > 10) {
                duration = Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
                amplifier = 1;
            }
            else {
                duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
                Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Warrior
        if (gamer.getKit().contains("Warrior") && Kits.warrior().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int duration, amplifier;
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            double range = 2 + level * .1d;
            if (level > 20) {
                duration = Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
                amplifier = 2;
            }
            else if (level > 10) {
                duration = Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
                amplifier = 1;
            }
            else {
                duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
                Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Knight
        if (gamer.getKit().contains("Knight") && Kits.knight().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int duration, amplifier;
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            double range = 2 + level * .1d;
            if (level > 20) {
                duration = Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
                amplifier = 2;
            }
            else if (level > 10) {
                duration = Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
                amplifier = 1;
            }
            else {
                duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
                Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Priest
        if (gamer.getKit().contains("Priest") && Kits.priest().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int duration, amplifier;
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            double range = 2 + level * .1d;
            if (level > 20) {
                duration = Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
                amplifier = 2;
            }
            else if (level > 10) {
                duration = Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
                amplifier = 1;
            }
            else {
                duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
                Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Siren
        if (gamer.getKit().contains("Siren") && Kits.siren().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int duration, amp1, amp2;
            int coolDown = Utils.secondsToMillis(31 - Math.pow(Math.E, (level - 1) / 12d));
            double range = 3 + level * .1d;
            if (level > 20) {
                duration = Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
                amp1 = 1;
                amp2 = 0;
            }
            else if (level > 10) {
                duration = Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
                amp1 = 1;
                amp2 = -1;
            }
            else {
                duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
                amp1 = 0;
                amp2 = -1;
            }
            int altDuration = (int) (.4 * duration);

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Utils.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOW, duration, amp1)));
                if (amp2 != -1)
                    Utils.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                            new PotionEffect(PotionEffectType.WEAKNESS, altDuration, amp2)));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Monk
        if (gamer.getKit().contains("Monk") && Kits.monk().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int duration, amplifier;
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            double range = 2 + level * .1d;
            if (level > 20) {
                duration = Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
                amplifier = 2;
            }
            else if (level > 10) {
                duration = Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
                amplifier = 1;
            }
            else {
                duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
                Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
        }

        // Messenger
        if (gamer.getKit().contains("Messenger") && Kits.messenger().equals(item)) {
            // Get effective player level
            int level = player.getLevel();
            if (gamer.getKit().contains("1") && level > 10)
                level = 10;
            if (gamer.getKit().contains("2") && level > 20)
                level = 20;
            if (gamer.getKit().contains("3") && level > 30)
                level = 30;

            // Check for zero level
            if (level == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format("&c" + language.getString("levelError"))));
                return;
            }

            // Calculate stats
            long dif = cooldowns.get(gamer) - System.currentTimeMillis();
            int duration, amplifier;
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            double range = 2 + level * .1d;
            if (level > 20) {
                duration = Utils.secondsToTicks(20.5 + Math.pow(Math.E, (level - 21) / 4d));
                amplifier = 2;
            }
            else if (level > 10) {
                duration = Utils.secondsToTicks(12 + Math.pow(Math.E, (level - 11) / 4d));
                amplifier = 1;
            }
            else {
                duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 4d));
                amplifier = 0;
            }
            int altDuration = (int) (.6 * duration);

            // Activate ability if cooldown has passed
            if (dif <= 0) {
                Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
                Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
                cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            } else player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
				Utils.format(String.format("&c" + language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
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
        if (!(ent instanceof Monster || ent instanceof Slime || ent instanceof Hoglin) || !(damager instanceof Player))
            return;

        Player player = (Player) damager;

        // Check if player is in a game
        if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player))) return;

        Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                .collect(Collectors.toList()).get(0);
        VDPlayer gamer = arena.getPlayer(player);

        // Check for vampire kit
        if (!gamer.getKit().equals("Vampire"))
            return;

        Random r = new Random();
        double damage = e.getFinalDamage();

        // Heal if probability is right
        if (r.nextInt(100) < damage)
            player.setHealth(Math.min(player.getHealth() + 1,
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
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

        // Check for player dealing damage
        if (!(damager instanceof Player))
            return;

        // Check for mob taking damage
        if (!(ent instanceof Mob))
            return;

        Player player = (Player) damager;
        Mob mob = (Mob) ent;

        // Check for invisibility
        if (player.getActivePotionEffects().stream()
                .noneMatch(potion -> potion.getType().equals(PotionEffectType.INVISIBILITY)))
            return;

        // Set target to null if not already
        if (mob.getTarget() != null)
            mob.setTarget(null);
    }
}
