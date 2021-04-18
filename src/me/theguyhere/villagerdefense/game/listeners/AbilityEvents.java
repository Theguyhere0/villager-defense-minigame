package me.theguyhere.villagerdefense.game.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.Kits;
import me.theguyhere.villagerdefense.game.models.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    private final Game game;
    private final Map<VDPlayer, Long> cooldowns = new HashMap<>();

    public AbilityEvents(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onAbility(PlayerInteractEvent e) {
        // Check for right click
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();

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
        if (gamer.getKit().equals("Mage1") && (Kits.mage1().equals(item))) {
            int expRequired = 1;
            int cooldown = 1;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
                    fireball.setYield(1.25f);
                    fireball.setShooter(player);
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Mage2") && (Kits.mage2().equals(item))) {
            int expRequired = 2;
            int cooldown = 2;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
                    fireball.setYield(1.75f);
                    fireball.setShooter(player);
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Mage3") && (Kits.mage3().equals(item))) {
            int expRequired = 4;
            int cooldown = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
                    fireball.setYield(2.25f);
                    fireball.setShooter(player);
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Ninja
        if (gamer.getKit().equals("Ninja1") && (Kits.ninja1().equals(item))) {
            int expRequired = 1;
            int cooldown = 30;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Utils.secondsToTicks(10),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Ninja2") && (Kits.ninja2().equals(item))) {
            int expRequired = 1;
            int cooldown = 50;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Utils.secondsToTicks(20),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Ninja3") && (Kits.ninja3().equals(item))) {
            int expRequired = 1;
            int cooldown = 60;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Utils.secondsToTicks(30),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Templar
        if (gamer.getKit().equals("Templar1") && (Kits.templar1().equals(item))) {
            int expRequired = 2;
            int cooldown = 60;
            double range = 2.5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.ABSORPTION, Utils.secondsToTicks(15), 0)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Utils.secondsToTicks(20),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Templar2") && (Kits.templar2().equals(item))) {
            int expRequired = 4;
            int cooldown = 80;
            double range = 4;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.ABSORPTION, Utils.secondsToTicks(15), 1)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Utils.secondsToTicks(25),
                            1));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Templar3") && (Kits.templar3().equals(item))) {
            int expRequired = 6;
            int cooldown = 100;
            double range = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.ABSORPTION, Utils.secondsToTicks(20), 2)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Utils.secondsToTicks(30),
                            2));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Warrior
        if (gamer.getKit().equals("Warrior1") && (Kits.warrior1().equals(item))) {
            int expRequired = 2;
            int cooldown = 60;
            double range = 2.5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(10), 0)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(15),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Warrior2") && (Kits.warrior2().equals(item))) {
            int expRequired = 4;
            int cooldown = 80;
            double range = 4;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(10), 1)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(15),
                            1));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Warrior3") && (Kits.warrior3().equals(item))) {
            int expRequired = 6;
            int cooldown = 100;
            double range = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(10), 2)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(15),
                            2));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Knight
        if (gamer.getKit().equals("Knight1") && (Kits.knight1().equals(item))) {
            int expRequired = 2;
            int cooldown = 60;
            double range = 2.5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(10), 0)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(15),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Knight2") && (Kits.knight2().equals(item))) {
            int expRequired = 4;
            int cooldown = 90;
            double range = 4;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(10), 1)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(15),
                            1));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Knight3") && (Kits.knight3().equals(item))) {
            int expRequired = 6;
            int cooldown = 120;
            double range = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(10), 2)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(15),
                            2));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Priest
        if (gamer.getKit().equals("Priest1") && (Kits.priest1().equals(item))) {
            int expRequired = 2;
            int cooldown = 60;
            double range = 2.5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(10), 0)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(15),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Priest2") && (Kits.priest2().equals(item))) {
            int expRequired = 4;
            int cooldown = 90;
            double range = 4;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(10), 1)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(15),
                            1));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Priest3") && (Kits.priest3().equals(item))) {
            int expRequired = 6;
            int cooldown = 120;
            double range = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(10), 2)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(15),
                            2));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Siren
        if (gamer.getKit().equals("Siren1") && (Kits.siren1().equals(item))) {
            int expRequired = 2;
            int cooldown = 40;
            double range = 3;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                            new PotionEffect(PotionEffectType.WEAKNESS, Utils.secondsToTicks(10), 0)));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Siren2") && (Kits.siren2().equals(item))) {
            int expRequired = 4;
            int cooldown = 60;
            double range = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                            new PotionEffect(PotionEffectType.WEAKNESS, Utils.secondsToTicks(10), 1)));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Siren3") && (Kits.siren3().equals(item))) {
            int expRequired = 6;
            int cooldown = 80;
            double range = 6;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyMonsters(player, range).forEach(ent -> {
                        ent.addPotionEffect(
                                new PotionEffect(PotionEffectType.WEAKNESS, Utils.secondsToTicks(10), 1));
                        ent.addPotionEffect(
                                new PotionEffect(PotionEffectType.SLOW, Utils.secondsToTicks(10), 0));
                    });
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Monk
        if (gamer.getKit().equals("Monk1") && (Kits.monk1().equals(item))) {
            int expRequired = 2;
            int cooldown = 60;
            double range = 2.5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(15), 0)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(20),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Monk2") && (Kits.monk2().equals(item))) {
            int expRequired = 4;
            int cooldown = 80;
            double range = 4;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(15), 1)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(25),
                            1));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Monk3") && (Kits.monk3().equals(item))) {
            int expRequired = 6;
            int cooldown = 100;
            double range = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(20), 2)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(30),
                            2));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

        // Messenger
        if (gamer.getKit().equals("Messenger1") && (Kits.messenger1().equals(item))) {
            int expRequired = 2;
            int cooldown = 60;
            double range = 2.5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(10), 0)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(15),
                            0));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Messenger2") && (Kits.messenger2().equals(item))) {
            int expRequired = 4;
            int cooldown = 80;
            double range = 4;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(10), 1)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(15),
                            1));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }
        if (gamer.getKit().equals("Messenger3") && (Kits.messenger3().equals(item))) {
            int expRequired = 6;
            int cooldown = 100;
            double range = 5;
            if (player.getLevel() >= expRequired) {
                long dif = cooldowns.get(gamer) - System.currentTimeMillis();
                if (dif <= 0) {
                    player.setLevel(player.getLevel() - expRequired);
                    Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                            new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(10), 2)));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(15),
                            2));
                    cooldowns.put(gamer, System.currentTimeMillis() + Utils.secondsToMillis(cooldown));
                } else player.sendMessage(Utils.notify(
                        String.format("&cYou have %.1f seconds left on your cooldown!", Utils.millisToSeconds(dif))));
            } else player.sendMessage(Utils.notify("&cYou don't have enough experience levels!"));
        }

    }

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
        if (r.nextInt(100) < damage * 1.5)
            player.setHealth(Math.min(player.getHealth() + 1,
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
    }
}
