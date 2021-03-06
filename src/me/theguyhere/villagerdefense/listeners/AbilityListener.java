package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.EndNinjaNerfEvent;
import me.theguyhere.villagerdefense.game.models.*;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.kits.Kit;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
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
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class AbilityListener implements Listener {
    private final Main plugin;
    private final Map<VDPlayer, Long> cooldowns = new HashMap<>();

    public AbilityListener(Main plugin) {
        this.plugin = plugin;
    }

    // Most ability functionalities
    @EventHandler
    public void onAbility(PlayerInteractEvent e) {
        FileConfiguration language = plugin.getLanguageData();

        // Check for right click
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                    .collect(Collectors.toList()).get(0);
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

        // See if the player is in a game
        if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
            return;

        // Ensure cooldown is initialized
        if (!cooldowns.containsKey(gamer))
            cooldowns.put(gamer, 0L);

        // Get effective player level
        int level = player.getLevel();
        if (gamer.getKit().getLevel() == 1 && level > 10)
            level = 10;
        if (gamer.getKit().getLevel() == 2 && level > 20)
            level = 20;
        if (gamer.getKit().getLevel() == 3 && level > 30)
            level = 30;

        long dif = cooldowns.get(gamer) - System.currentTimeMillis();

        // Mage
        if (gamer.getKit().getName().equals(Kit.mage().getName()) && GameItems.mage().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
            int coolDown = Utils.secondsToMillis(13 - Math.pow(Math.E, (level - 1) / 12d));
            float yield = 1 + level * .05f;

            // Activate ability
            Fireball fireball = player.getWorld().spawn(player.getEyeLocation(), Fireball.class);
            fireball.setYield(yield);
            fireball.setShooter(player);
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
        }

        // Ninja
        if (gamer.getKit().getName().equals(Kit.ninja().getName()) && GameItems.ninja().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
            int coolDown = Utils.secondsToMillis(46 - Math.pow(Math.E, (level - 1) / 12d));
            int duration = Utils.secondsToTicks(4 + Math.pow(Math.E, (level - 1) / 8.5));

            // Activate ability
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0));
            Utils.getPets(player).forEach(wolf ->
                    wolf.addPotionEffect((new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0))));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);
            gamer.hideArmor();

            // Schedule un-nerf
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new EndNinjaNerfEvent(gamer)), duration);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        }

        // Templar
        if (gamer.getKit().getName().equals(Kit.templar().getName()) && GameItems.templar().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
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

            // Activate ability
            Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
            Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.ABSORPTION, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Warrior
        if (gamer.getKit().getName().equals(Kit.warrior().getName()) && GameItems.warrior().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
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

            // Activate ability
            Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
            Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.INCREASE_DAMAGE, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Knight
        if (gamer.getKit().getName().equals(Kit.knight().getName()) && GameItems.knight().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
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

            // Activate ability
            Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
            Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Priest
        if (gamer.getKit().getName().equals(Kit.priest().getName()) && GameItems.priest().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
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

            // Activate ability
            Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
            Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.REGENERATION, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Siren
        if (gamer.getKit().getName().equals(Kit.siren().getName()) && GameItems.siren().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
            int duration, amp1, amp2;
            int coolDown = Utils.secondsToMillis(26 - Math.pow(Math.E, (level - 1) / 12d));
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

            // Activate ability
            Utils.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                    new PotionEffect(PotionEffectType.SLOW, duration, amp1)));
            if (amp2 != -1)
                Utils.getNearbyMonsters(player, range).forEach(ent -> ent.addPotionEffect(
                        new PotionEffect(PotionEffectType.WEAKNESS, altDuration, amp2)));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1.25f));
        }

        // Monk
        if (gamer.getKit().getName().equals(Kit.monk().getName()) && GameItems.monk().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
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

            // Activate ability
            Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
            Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.FAST_DIGGING, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
        }

        // Messenger
        if (gamer.getKit().getName().equals(Kit.messenger().getName()) && GameItems.messenger().equals(item)) {
            // Perform checks
            if (checkLevel(level, player, language))
                return;
            if (checkCooldown(dif, player, language))
                return;

            // Calculate stats
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

            // Activate ability
            Utils.getNearbyPlayers(player, range).forEach(player1 -> player1.addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
            Utils.getNearbyAllies(player, range).forEach(ally -> ally.addPotionEffect(
                    new PotionEffect(PotionEffectType.SPEED, altDuration, amplifier)));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
            cooldowns.put(gamer, System.currentTimeMillis() + coolDown);

            // Fire ability sound if turned on
            if (arena.hasAbilitySound())
                arena.getActives().forEach(vdPlayer -> vdPlayer.getPlayer()
                        .playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1));
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
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = plugin.getGame().arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                    .collect(Collectors.toList()).get(0);
            gamer = arena.getPlayer(player);
        } catch (Exception err) {
            return;
        }

        // Check for vampire kit
        if (!gamer.getKit().getName().equals(Kit.vampire().getName()))
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

        // Check for player or wolf dealing damage
        if (!(damager instanceof Player || damager instanceof Wolf))
            return;

        // Check for mob taking damage
        if (!(ent instanceof Mob))
            return;

        LivingEntity stealthy = (LivingEntity) damager;
        Mob mob = (Mob) ent;

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
    public void onInvisibleEquip(PlayerStatisticIncrementEvent e) {
        if (!e.getStatistic().equals(Statistic.TIME_SINCE_REST))
            return;

        Player player = e.getPlayer();

        // Check if player is in a game
        if (plugin.getGame().arenas.stream().filter(Objects::nonNull).noneMatch(arena -> arena.hasPlayer(player)))
            return;

        // Ignore creative and spectator mode players
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;

        // Ignore if not invisible
        if (player.getActivePotionEffects().stream()
                .noneMatch(potion -> potion.getType().equals(PotionEffectType.INVISIBILITY)))
            return;

        FileConfiguration language = plugin.getLanguageData();

        // Get armor
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        // Unequip armor
        if (!(helmet == null || helmet.getType() == Material.AIR)) {
            Utils.giveItem(player, helmet, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setHelmet(null);
            player.sendMessage(Utils.notify(language.getString("ninjaError")));
        }
        if (!(chestplate == null || chestplate.getType() == Material.AIR)) {
            Utils.giveItem(player, chestplate, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setChestplate(null);
            player.sendMessage(Utils.notify(language.getString("ninjaError")));
        }
        if (!(leggings == null || leggings.getType() == Material.AIR)) {
            Utils.giveItem(player, leggings, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setLeggings(null);
            player.sendMessage(Utils.notify(language.getString("ninjaError")));
        }
        if (!(boots == null || boots.getType() == Material.AIR)) {
            Utils.giveItem(player, boots, Utils.notify(language.getString("inventoryFull")));
            player.getInventory().setBoots(null);
            player.sendMessage(Utils.notify(language.getString("ninjaError")));
        }
    }

    private boolean checkLevel(int level, Player player, FileConfiguration language) {
        if (level == 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    Utils.format(language.getString("levelError"))));
            return true;
        }
        return false;
    }

    private boolean checkCooldown(long dif, Player player, FileConfiguration language) {
        if (dif > 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    Utils.format(String.format(language.getString("cooldownError"), Utils.millisToSeconds(dif)))));
            return true;
        }
        return false;
    }
}
