package me.theguyhere.villagerdefense.plugin.listeners;

import com.google.common.util.concurrent.AtomicDouble;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.items.ItemMetaKey;
import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.*;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.AttackClass;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import me.theguyhere.villagerdefense.plugin.tools.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AbilityListener implements Listener {
    // Most ability functionalities
    @EventHandler
    public void onAbility(PlayerInteractEvent e) {
        // Check for right click
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();
        UUID id = player.getUniqueId();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            return;
        }

        // Wait for arena to progress to first wave
        if (arena.getCurrentWave() < 1)
            return;

        // Gather item
        ItemStack item = e.getItem();
        if (item == null || !VDAbility.matches(item))
            return;

        // Check cooldown
        if (gamer.remainingAbilityCooldown() > 0)
            return;

        // Gather stats
        AtomicDouble cooldown = new AtomicDouble();
        AtomicDouble range = new AtomicDouble();
        AtomicDouble duration = new AtomicDouble();
        AtomicReference<String> effect = new AtomicReference<>();
        Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore()).forEach(lore -> {
            if (lore.contains(LanguageManager.messages.cooldown
                    .replace("%s", ""))) {
                cooldown.set(Double.parseDouble(lore.substring(2 + LanguageManager.messages.cooldown.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(LanguageManager.messages.seconds.substring(3), "")));
            }
            else if (lore.contains(LanguageManager.messages.range
                    .replace("%s", ""))) {
                range.set(Double.parseDouble(lore.substring(2 + LanguageManager.messages.range.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(LanguageManager.messages.blocks.substring(3), "")));
            }
            else if (lore.contains(LanguageManager.messages.duration
                    .replace("%s", ""))) {
                duration.set(Double.parseDouble(lore.substring(2 + LanguageManager.messages.duration.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(LanguageManager.messages.seconds.substring(3), "")));
            }
            else if (lore.contains(LanguageManager.messages.effect
                    .replace("%s", ""))) {
                effect.set(lore);
            }
        });
        double altDuration = duration.get() * .6;

        // Check if player has cooldown decrease achievement and is boosted
        if (gamer.isBoosted() && PlayerManager.hasAchievement(id, Achievement.allMaxedAbility().getID()))
            cooldown.getAndSet(cooldown.get() * .9);

        // Mage
        if (Kit.mage().getID().equals(gamer.getKit().getID()) && MageAbility.matches(item)) {
            float yield = 1.5f;

            // Activate ability
            Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
            fireball.setYield(yield);
            fireball.setShooter(player);
            fireball.setMetadata(ItemMetaKey.DAMAGE.name(),
                    new FixedMetadataValue(Main.plugin, gamer.dealRawDamage(AttackClass.RANGE, 0)));
            fireball.setMetadata(ItemMetaKey.PER_BLOCK.name(), new FixedMetadataValue(Main.plugin, false));
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));
        }

        // Ninja
        else if (Kit.ninja().getID().equals(gamer.getKit().getID()) && NinjaAbility.matches(item)) {
            // Activate ability
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
                    Utils.secondsToTicks(duration.get()), 0));
            WorldManager.getPets(player).forEach(wolf ->
                    wolf.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY,
                            Utils.secondsToTicks(duration.get()), 0))));
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Nerf
            gamer.hideArmor();

            // Schedule un-nerf
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, gamer::exposeArmor,
                    Utils.secondsToTicks(duration.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        }

        // Templar
        else if (Kit.templar().getID().equals(gamer.getKit().getID()) && TemplarAbility.matches(item)) {
            // Calculate effect
            int absorption = 0;
            if (effect.get().contains("100"))
                absorption = 100;
            else if (effect.get().contains("200"))
                absorption = 200;
            else if (effect.get().contains("300"))
                absorption = 300;

            // Activate ability
            int finalAbsorption = absorption;
            WorldManager.getNearbyPlayers(player, range.get()).forEach(player1 -> {
                try {
                    arena.getPlayer(player1).addAbsorptionUpTo(finalAbsorption);
                } catch (PlayerNotFoundException ignored) {
                }
            });
            gamer.addAbsorptionUpTo(absorption);
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Warrior
        else if (Kit.warrior().getID().equals(gamer.getKit().getID()) && WarriorAbility.matches(item)) {
            // Calculate effect
            int amplifier;
            if (effect.get().contains("10"))
                amplifier = 0;
            else if (effect.get().contains("20"))
                amplifier = 1;
            else if (effect.get().contains("30"))
                amplifier = 2;
            else return;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range.get()).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(altDuration), amplifier)));
            WorldManager.getNearbyAllies(player, range.get()).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Utils.secondsToTicks(altDuration), amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                    Utils.secondsToTicks(duration.get()), amplifier));
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Knight
        else if (Kit.knight().getID().equals(gamer.getKit().getID()) && KnightAbility.matches(item)) {
            // Calculate effect
            int amplifier;
            if (effect.get().contains("10"))
                amplifier = 0;
            else if (effect.get().contains("20"))
                amplifier = 1;
            else if (effect.get().contains("30"))
                amplifier = 2;
            else return;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range.get()).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(altDuration),
                            amplifier)));
            WorldManager.getNearbyAllies(player, range.get()).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Utils.secondsToTicks(altDuration),
                            amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                    Utils.secondsToTicks(duration.get()), amplifier));
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Priest
        else if (Kit.priest().getID().equals(gamer.getKit().getID()) && PriestAbility.matches(item)) {
            // Calculate effect
            int amplifier;
            if (effect.get().contains("+5"))
                amplifier = 0;
            else if (effect.get().contains("+10"))
                amplifier = 1;
            else if (effect.get().contains("+15"))
                amplifier = 2;
            else return;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range.get()).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(altDuration),
                            amplifier)));
            WorldManager.getNearbyAllies(player, range.get()).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.REGENERATION, Utils.secondsToTicks(altDuration),
                            amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                    Utils.secondsToTicks(duration.get()), amplifier));
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Siren
        else if (Kit.siren().getID().equals(gamer.getKit().getID()) && SirenAbility.matches(item)) {
            // Calculate effect
            int amp1 = -1;
            int amp2 = -1;
            if (effect.get().contains("15"))
                amp1 = 0;
            else if (effect.get().contains("30"))
                amp1 = 1;
            if (effect.get().contains("10"))
                amp2 = 0;

            // Activate ability
            if (amp1 != -1) {
                int finalAmp = amp1;
                WorldManager.getNearbyMonsters(player, range.get()).forEach(ent -> ent.addPotionEffect(
                    new PotionEffect(PotionEffectType.SLOW, Utils.secondsToTicks(duration.get()), finalAmp)));
            }
            if (amp2 != -1) {
                int finalAmp1 = amp2;
                WorldManager.getNearbyMonsters(player, range.get()).forEach(ent -> ent.addPotionEffect(
                        new PotionEffect(PotionEffectType.WEAKNESS, Utils.secondsToTicks(duration.get()), finalAmp1)));
            }
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1.25f));
        }

        // Monk
        else if (Kit.monk().getID().equals(gamer.getKit().getID()) && MonkAbility.matches(item)) {
            // Calculate effect
            int amplifier;
            if (effect.get().contains("20"))
                amplifier = 0;
            else if (effect.get().contains("40"))
                amplifier = 1;
            else if (effect.get().contains("60"))
                amplifier = 2;
            else return;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range.get()).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(altDuration), amplifier)));
            WorldManager.getNearbyAllies(player, range.get()).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.FAST_DIGGING, Utils.secondsToTicks(altDuration), amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
                    Utils.secondsToTicks(duration.get()), amplifier));
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Messenger
        else if (Kit.messenger().getID().equals(gamer.getKit().getID()) && MessengerAbility.matches(item)) {
            // Calculate effect
            int amplifier;
            if (effect.get().contains("20"))
                amplifier = 0;
            else if (effect.get().contains("40"))
                amplifier = 1;
            else if (effect.get().contains("60"))
                amplifier = 2;
            else return;

            // Activate ability
            WorldManager.getNearbyPlayers(player, range.get()).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(altDuration), amplifier)));
            WorldManager.getNearbyAllies(player, range.get()).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(altDuration), amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Utils.secondsToTicks(duration.get()),
                    amplifier));
            gamer.triggerAbilityCooldown(Utils.secondsToMillis(cooldown.get()));

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }
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
}
