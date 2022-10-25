package me.theguyhere.villagerdefense.plugin.game.models.players;

import com.google.common.util.concurrent.AtomicDouble;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaException;
import me.theguyhere.villagerdefense.plugin.exceptions.VDMobNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.Ammo;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDFletcher;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class holding data about players in a Villager Defense game.
 */
public class VDPlayer {
    /** UUID of corresponding {@link Player}.*/
    private final UUID player;
    /** Status of the this {@link VDPlayer}.*/
    private PlayerStatus status;
    /** The arena that this {@link VDPlayer} belongs in.*/
    private final Arena arena;

    // Important arena stats
    private int maxHealth = 0;
    private int currentHealth = 0;
    private int absorption = 0;
    private int baseDamage = 0;
    private int armor = 0;
    private int toughness = 0;
    private long cooldown = 0;
    /** Gem balance.*/
    private int gems = 0;
    /** Kill count.*/
    private int kills = 0;
    /** Wolf count.*/
    private int wolves = 0;
    /** The wave at which the player joined the game as an active player.*/
    private int joinedWave = 0;
    /** The number of times this player violated arena boundaries.*/
    private int infractions = 0;
    /** The {@link Kit} the player will play with.*/
    private Kit kit = Kit.none();
    /** A possible second {@link Kit} the player can play with.*/
    private Kit kit2;
    /** The list of {@link Challenge}'s the player will take on.*/
    private List<Challenge> challenge = new ArrayList<>();
    /** The list of UUIDs of those that damaged the player.*/
    private final List<UUID> enemies = new ArrayList<>();
    /** Whether permanent boosts are on or not.*/
    private boolean boost = false; // TODO return to true once boosts are reworked
    /** Number of gems to be converted from crystals.*/
    private int gemBoost = 0;
    /** Whether effect kits are shared or not.*/
    private boolean share = false;

    public VDPlayer(Player player, Arena arena, boolean spectating) {
        this.player = player.getUniqueId();
        this.arena = arena;
        if (spectating)
            status = PlayerStatus.SPECTATOR;
        else status = PlayerStatus.ALIVE;
    }

    public UUID getID() {
        return player;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public Arena getArena() {
        return arena;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealthInit(int maxHealth) {
        this.maxHealth = Math.max(maxHealth, 0);
        currentHealth = Math.max(maxHealth, 0);
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(maxHealth, 0);
        getPlayer().setHealth(currentHealth *
                Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / maxHealth);
    }

    public boolean hasMaxHealth() {
        return currentHealth == maxHealth;
    }

    public void addAbsorption(int absorption) {
        this.absorption += absorption;
    }

    /**
     * Takes final health difference and applies the difference, checking for absorption, death, and performing
     * notifications.
     * @param dif Final health difference.
     */
    public void changeCurrentHealth(int dif) {
        // Make sure health was initialized properly
        if (maxHealth <= 0)
            return;

        // Handle absorption
        int trueDif = dif;
        if (dif < 0) {
            trueDif = Math.min(0, absorption + dif);
            absorption = Math.max(absorption + dif, 0);
        }

        // Update true health
        this.currentHealth = Math.min(Math.max(currentHealth + trueDif, 0), maxHealth);

        // Check for death
        if (this.currentHealth == 0) {
            // Check if player has resurrection achievement and is boosted
            Random random = new Random();
            if (isBoosted() && random.nextDouble() < .1 &&
                    PlayerManager.hasAchievement(getPlayer().getUniqueId(), Achievement.allChallenges().getID())) {
                PlayerManager.giveTotemEffect(getPlayer());
                return;
            }

            // Set player to fake death mode
            PlayerManager.fakeDeath(this);

            // Check for explosive challenge
            if (getChallenges().contains(Challenge.explosive())) {
                // Create an explosion
                getPlayer().getWorld().createExplosion(getPlayer().getLocation(), 1.25F, false, false);

                // Drop all items and clear inventory
                getPlayer().getInventory().forEach(itemStack -> {
                    if (itemStack != null && !Shop.matches(itemStack))
                        getPlayer().getWorld().dropItemNaturally(getPlayer().getLocation(), itemStack);
                });
                getPlayer().getInventory().clear();
            }

            // Notify player of their own death
            getPlayer().sendTitle(
                    new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.death1).toString(),
                    new ColoredMessage(ChatColor.RED, LanguageManager.messages.death2).toString(),
                    Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1));

            // Notify everyone else of player death
            arena.getPlayers().forEach(fighter -> {
                if (!fighter.getPlayer().getUniqueId().equals(getPlayer().getUniqueId()))
                    PlayerManager.notifyAlert(fighter.getPlayer(),
                            String.format(LanguageManager.messages.death, getPlayer().getName()));
                if (arena.hasPlayerDeathSound())
                    try {
                        fighter.getPlayer().playSound(arena.getPlayerSpawn().getLocation().add(0, 5, 0),
                                Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10,
                                .75f);
                    } catch (NullPointerException err) {
                        CommunicationManager.debugError(err.getMessage(), 0);
                    }
            });

            // Update scoreboards
            arena.updateScoreboards();

            // Check for game end condition
            if (arena.getAlive() == 0)
                try {
                    arena.endGame();
                } catch (ArenaException ignored) {
                }
        }
    }

    public void showAndUpdatStats() {
        AtomicBoolean penetrating = new AtomicBoolean(false);
        AtomicBoolean range = new AtomicBoolean(false);
        AtomicBoolean perBlock = new AtomicBoolean(false);
        AtomicInteger ammoCost = new AtomicInteger();
        AtomicInteger ammoCap = new AtomicInteger();
        AtomicInteger armor = new AtomicInteger();
        AtomicInteger toughness = new AtomicInteger();
        AtomicDouble weight = new AtomicDouble(1);
        String damage = Integer.toString(baseDamage);


        // Make sure health was properly initialized
        if (maxHealth <= 0)
            return;

        // Calculate stats
        try {
            ItemStack weapon = Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand();
            List<Integer> damageValues = new ArrayList<>();

            Objects.requireNonNull(Objects.requireNonNull(weapon.getItemMeta()).getLore()).forEach(lore -> {
                if (lore.contains(LanguageManager.messages.attackMainDamage
                        .replace("%s", ""))) {
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 +
                                        LanguageManager.messages.attackMainDamage.length())
                                .split("-");
                        damageValues.add(Integer.valueOf(split[0]));
                        damageValues.add(Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                    } else damageValues.add(Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackMainDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
                else if (lore.contains(LanguageManager.messages.attackCritDamage
                        .replace("%s", ""))) {
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 +
                                        LanguageManager.messages.attackCritDamage.length())
                                .split("-");
                        damageValues.add(Integer.valueOf(split[0]));
                        damageValues.add(Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                    } else damageValues.add(Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackCritDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
                else if (lore.contains(LanguageManager.messages.attackSweepDamage
                        .replace("%s", ""))) {
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 +
                                        LanguageManager.messages.attackSweepDamage.length())
                                .split("-");
                        damageValues.add(Integer.valueOf(split[0]));
                        damageValues.add(Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                    } else damageValues.add(Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackSweepDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
                else if (lore.contains(LanguageManager.messages.attackRangeDamage
                        .replace("%s", ""))) {
                    range.set(true);
                    String perBlockText = LanguageManager.messages.perBlock.replace("%s", "");
                    if (lore.contains(perBlockText))
                        perBlock.set(true);
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 + LanguageManager.messages.attackRangeDamage.length())
                                .replace(perBlockText, "")
                                .split("-");
                        damageValues.add(Integer.valueOf(split[0]));
                        damageValues.add(Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                    } else damageValues.add(Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackRangeDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
                else if (lore.contains(LanguageManager.names.penetrating.replace("%s", "")))
                    penetrating.set(true);
                else if (lore.contains(LanguageManager.messages.ammoCost
                        .replace("%s", ""))) {
                    ammoCost.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.ammoCost.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
            });
            damageValues.sort(Comparator.comparingInt(Integer::intValue));
            if (damageValues.size() == 1)
                damage = Integer.toString(damageValues.get(0) + (perBlock.get() ? 0 : baseDamage));
            else damage = (damageValues.get(0) + (perBlock.get() ? 0 : baseDamage)) + "-" +
                    (damageValues.get(damageValues.size() - 1) + (perBlock.get() ? 0 : baseDamage));
        } catch (Exception ignored) {
        }
        try {
            ItemStack ammo = Objects.requireNonNull(getPlayer().getEquipment()).getItemInOffHand();
            Objects.requireNonNull(Objects.requireNonNull(ammo.getItemMeta()).getLore()).forEach(lore -> {
                if (lore.contains(LanguageManager.messages.capacity
                        .replace("%s", ""))) {
                    ammoCap.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.capacity.length())
                            .replace(ChatColor.BLUE.toString(), "")
                            .replace(ChatColor.WHITE.toString(), "")
                            .split(" / ")[0]));
                }
            });
        } catch (Exception ignored) {
        }
        try {
            ItemStack helmet = Objects.requireNonNull(Objects.requireNonNull(getPlayer().getEquipment()).getHelmet());

            Objects.requireNonNull(Objects.requireNonNull(helmet.getItemMeta()).getLore()).forEach(lore -> {
                if (lore.contains(LanguageManager.messages.armor.replace("%s", "")))
                    armor.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.armor.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                else if (lore.contains(LanguageManager.messages.toughness
                        .replace("%s", "")))
                    toughness.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.toughness.length())
                            .replace(ChatColor.BLUE.toString(), "")
                            .replace("%", "")));
                else if (lore.contains(LanguageManager.messages.weight.replace("%s", "")))
                    weight.addAndGet(-Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.weight.length())
                            .replace(ChatColor.BLUE.toString(), "")) * .01);
            });
        } catch (Exception ignored) {
        }
        try {
            ItemStack chestplate = Objects.requireNonNull(Objects.requireNonNull(getPlayer().getEquipment())
                    .getChestplate());

            Objects.requireNonNull(Objects.requireNonNull(chestplate.getItemMeta()).getLore()).forEach(lore -> {
                if (lore.contains(LanguageManager.messages.armor.replace("%s", "")))
                    armor.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.armor.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                else if (lore.contains(LanguageManager.messages.toughness
                        .replace("%s", "")))
                    toughness.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.toughness.length())
                            .replace(ChatColor.BLUE.toString(), "")
                            .replace("%", "")));
                else if (lore.contains(LanguageManager.messages.weight.replace("%s", "")))
                    weight.addAndGet(-Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.weight.length())
                            .replace(ChatColor.BLUE.toString(), "")) * .01);
            });
        } catch (Exception ignored) {
        }
        try {
            ItemStack leggings = Objects.requireNonNull(Objects.requireNonNull(getPlayer().getEquipment())
                    .getLeggings());

            Objects.requireNonNull(Objects.requireNonNull(leggings.getItemMeta()).getLore()).forEach(lore -> {
                if (lore.contains(LanguageManager.messages.armor.replace("%s", "")))
                    armor.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.armor.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                else if (lore.contains(LanguageManager.messages.toughness
                        .replace("%s", "")))
                    toughness.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.toughness.length())
                            .replace(ChatColor.BLUE.toString(), "")
                            .replace("%", "")));
                else if (lore.contains(LanguageManager.messages.weight.replace("%s", "")))
                    weight.addAndGet(-Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.weight.length())
                            .replace(ChatColor.BLUE.toString(), "")) * .01);
            });
        } catch (Exception ignored) {
        }
        try {
            ItemStack boots = Objects.requireNonNull(Objects.requireNonNull(getPlayer().getEquipment()).getBoots());

            Objects.requireNonNull(Objects.requireNonNull(boots.getItemMeta()).getLore()).forEach(lore -> {
                if (lore.contains(LanguageManager.messages.armor.replace("%s", "")))
                    armor.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.armor.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                else if (lore.contains(LanguageManager.messages.toughness
                        .replace("%s", "")))
                    toughness.addAndGet(Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.toughness.length())
                            .replace(ChatColor.BLUE.toString(), "")
                            .replace("%", "")));
                else if (lore.contains(LanguageManager.messages.weight.replace("%s", "")))
                    weight.addAndGet(-Integer.parseInt(lore.substring(2 +
                                    LanguageManager.messages.weight.length())
                            .replace(ChatColor.BLUE.toString(), "")) * .01);
            });
        } catch (Exception ignored) {
        }

        // Update status bar
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                new ColoredMessage(absorption > 0 ? ChatColor.GOLD : ChatColor.RED,
                        Utils.HP + " " + (currentHealth + absorption) + "/" + maxHealth) + "    " +
                        new ColoredMessage(ChatColor.AQUA, Utils.ARMOR + " " + armor) + "    " +
                        new ColoredMessage(ChatColor.DARK_AQUA, Utils.TOUGH + " " + toughness + "%") + "    " +
                        new ColoredMessage(ammoCap.get() < ammoCost.get() ? ChatColor.RED :
                                penetrating.get() ? ChatColor.YELLOW : ChatColor.GREEN,
                                (range.get() ? Utils.ARROW : Utils.DAMAGE) + " " + damage +
                                        (perBlock.get() ? " /" + Utils.BLOCK + " +" + baseDamage : ""))));

        // Update normal health display
        getPlayer().setHealth(Math.max(currentHealth *
                Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / maxHealth,
                1));
        getPlayer().setAbsorptionAmount(absorption *
                Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / maxHealth);

        // Update armor and toughness
        this.armor = armor.get();
        this.toughness = toughness.get();

        // Set speed
        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(.1 * weight.get());
    }

    public void heal() {
        int hunger = getPlayer().getFoodLevel();
        if (hunger >= 20)
            changeCurrentHealth(6);
        else if (hunger >= 16)
            changeCurrentHealth(5);
        else if (hunger >= 10)
            changeCurrentHealth(3);
        else if (hunger >= 4)
            changeCurrentHealth(2);
        else if (hunger > 0)
            changeCurrentHealth(1);
    }

    public void refill() {
        AtomicBoolean fletcher = new AtomicBoolean(false);
        Objects.requireNonNull(getPlayer().getWorld())
                .getNearbyEntities(getArena().getBounds(), entity ->
                        getPlayer().getLocation().distance(entity.getLocation()) <= 16)
                .stream()
                .filter(entity -> entity instanceof Villager)
                .forEach(entity -> {
                    try {
                        VDMob villager = getArena().getMob(entity.getUniqueId());
                        if (villager instanceof VDFletcher)
                            fletcher.set(true);
                    } catch (VDMobNotFoundException ignored) {
                    }
                });

        // Update ammo
        Ammo.updateRefill(Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand(), fletcher.get());
        Ammo.updateRefill(Objects.requireNonNull(getPlayer().getEquipment()).getItemInOffHand(), fletcher.get());
    }

    public void takeDamage(int damage, @NotNull AttackType attackType) {
        // Scale damage by attack type
        if (attackType == AttackType.NORMAL)
            damage -= Math.min(damage, armor);
        else if (attackType == AttackType.PENETRATING)
            damage *= Math.max(0, 1 - toughness);
        else if (attackType == AttackType.NONE)
            damage = 0;

        // Realize damage
        changeCurrentHealth(-damage);

        // Damage armor
        if (attackType == AttackType.NORMAL || attackType == AttackType.PENETRATING)
            Arrays.stream(getPlayer().getInventory().getArmorContents()).filter(Objects::nonNull).forEach(armor ->
                    Bukkit.getPluginManager().callEvent(new PlayerItemDamageEvent(getPlayer(), armor, 0)));
    }

    public void combust(int ticks) {
        if (getPlayer().getFireTicks() < ticks)
            getPlayer().setFireTicks(ticks);
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    public int dealRawDamage(@NotNull AttackClass attackClass, double mainMult) {
        Random r = new Random();

        // Modify damage based on weapon
        try {
            ItemStack weapon = Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand();
            Map<String, Integer> attributes = new HashMap<>();

            // Gather weapon attributes
            Objects.requireNonNull(Objects.requireNonNull(weapon.getItemMeta()).getLore()).forEach(lore -> {
                if (lore.contains(LanguageManager.messages.attackMainDamage
                        .replace("%s", ""))) {
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 +
                                        LanguageManager.messages.attackMainDamage.length())
                                .split("-");
                        attributes.put("mainLow", Integer.valueOf(split[0]));
                        attributes.put("mainHigh",
                                Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                    } else attributes.put("main", Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackMainDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
                else if (lore.contains(LanguageManager.messages.attackCritDamage
                        .replace("%s", ""))) {
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 +
                                        LanguageManager.messages.attackCritDamage.length())
                                .split("-");
                        attributes.put("critLow", Integer.valueOf(split[0]));
                        attributes.put("critHigh",
                                Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                    } else attributes.put("crit", Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackCritDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
                else if (lore.contains(LanguageManager.messages.attackSweepDamage
                        .replace("%s", ""))) {
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 +
                                        LanguageManager.messages.attackSweepDamage.length())
                                .split("-");
                        attributes.put("sweepLow", Integer.valueOf(split[0]));
                        attributes.put("sweepHigh",
                                Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                    } else attributes.put("sweep", Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackSweepDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
                else if (lore.contains(LanguageManager.messages.attackRangeDamage
                        .replace("%s", ""))) {
                    String perBlockText = LanguageManager.messages.perBlock.replace("%s", "");
                    if (lore.contains("-")) {
                        String[] split = lore.substring(2 + LanguageManager.messages.attackRangeDamage.length())
                                .replace(perBlockText, "")
                                .split("-");
                        attributes.put("rangeLow", Integer.valueOf(split[0]));
                        attributes.put("rangeHigh", Integer.valueOf(split[1]
                                .replace(ChatColor.BLUE.toString(), "")));
                    } else attributes.put("range", Integer.valueOf(lore.substring(2 +
                                    LanguageManager.messages.attackRangeDamage.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
            });

            // Deal raw damage
            switch (attackClass) {
                case MAIN:
                    if (attributes.containsKey("main"))
                        return (int) ((baseDamage + attributes.get("main")) * mainMult);
                    else return (int) ((baseDamage + attributes.get("mainLow") +
                            r.nextInt(attributes.get("mainHigh") - attributes.get("mainLow"))) * mainMult);
                case CRITICAL:
                    if (attributes.containsKey("crit"))
                        return baseDamage + attributes.get("crit");
                    else return baseDamage + attributes.get("critLow") +
                            r.nextInt(attributes.get("critHigh") - attributes.get("critLow"));
                case SWEEP:
                    if (attributes.containsKey("sweep"))
                        return baseDamage + attributes.get("sweep");
                    else return baseDamage + attributes.get("sweepLow") +
                            r.nextInt(attributes.get("sweepHigh") - attributes.get("sweepLow"));
                case RANGE:
                    if (attributes.containsKey("range"))
                        return attributes.get("range");
                    else return attributes.get("rangeLow") +
                            r.nextInt(attributes.get("rangeHigh") - attributes.get("rangeLow"));
                default:
                    return 0;
            }
        } catch (Exception e) {
            return baseDamage;
        }
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public AttackType getAttackType() {
        try {
            ItemStack weapon = Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand();
            if (Objects.requireNonNull(Objects.requireNonNull(weapon.getItemMeta()).getLore()).stream()
                    .anyMatch(lore -> lore.contains(LanguageManager.names.penetrating
                            .replace("%s", ""))))
                return AttackType.PENETRATING;
        } catch (Exception ignored) {
        }
        return AttackType.NORMAL;
    }

    public int getGems() {
        return gems;
    }

    public int getKills() {
        return kills;
    }

    public void addGems(int change) {
        gems += change;
    }

    /**
     * Checks whether the player can afford a shop item.
     * @param cost Item cost.
     * @return Boolean indicating whether the item was affordable.
     */
    public boolean canAfford(int cost) {
        return cost <= gems;
    }

    public void incrementKills() {
        kills++;
    }

    public Kit getKit() {
        return kit;
    }

    public Kit getKit2() {
        return kit2;
    }

    public List<Challenge> getChallenges() {
        return challenge;
    }

    public void addChallenge(Challenge toBeAdded) {
        if (!challenge.contains(toBeAdded))
            challenge.add(toBeAdded);
    }

    public void removeChallenge(Challenge toBeRemoved) {
        challenge.remove(toBeRemoved);
    }

    public void resetChallenges() {
        challenge = new ArrayList<>();
    }

    public List<UUID> getEnemies() {
        return enemies;
    }

    public void addEnemy(UUID toBeAdded) {
        if (!enemies.contains(toBeAdded))
            enemies.add(toBeAdded);
    }

    public boolean isBoosted() {
        return boost;
    }

    public void toggleBoost() {
        boost = !boost;
    }

    public int getGemBoost() {
        return gemBoost;
    }

    public void setGemBoost(int gemBoost) {
        this.gemBoost = gemBoost;
    }

    public boolean isSharing() {
        return share;
    }

    public void toggleShare() {
        share = !share;
    }

    public int getWolves() {
        return wolves;
    }

    public void incrementWolves() {
        wolves++;
    }

    public void decrementWolves() {
        wolves--;
    }

    public int getJoinedWave() {
        return joinedWave;
    }

    public void setJoinedWave(int joinedWave) {
        this.joinedWave = joinedWave;
    }

    public int incrementInfractions() {
        return ++infractions;
    }

    public void resetInfractions() {
        infractions = 0;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public void setKit2(Kit kit2) {
        this.kit2 = kit2;
    }

    /**
     * Gives items on spawn or respawn based on kit selected
     */
    public void giveItems() {
        for (ItemStack item: getKit().getItems()) {
            EntityEquipment equipment = getPlayer().getEquipment();

            // Equip armor if possible, otherwise put in inventory, otherwise drop at feet
            if (item.getType().toString().contains("HELMET") && Objects.requireNonNull(equipment).getHelmet() == null)
                equipment.setHelmet(item);
            else if (item.getType().toString().contains("CHESTPLATE") &&
                    Objects.requireNonNull(equipment).getChestplate() == null)
                equipment.setChestplate(item);
            else if (item.getType().toString().contains("LEGGINGS") &&
                    Objects.requireNonNull(equipment).getLeggings() == null)
                equipment.setLeggings(item);
            else if (item.getType().toString().contains("BOOTS") &&
                    Objects.requireNonNull(equipment).getBoots() == null)
                equipment.setBoots(item);
            else PlayerManager.giveItem(getPlayer(), item, LanguageManager.errors.inventoryFull);
        }
        if (getKit2() != null)
            for (ItemStack item: getKit2().getItems()) {
                EntityEquipment equipment = getPlayer().getEquipment();

                // Equip armor if possible, otherwise put in inventory, otherwise drop at feet
                if (item.getType().toString().contains("HELMET") &&
                        Objects.requireNonNull(equipment).getHelmet() == null)
                    equipment.setHelmet(item);
                else if (item.getType().toString().contains("CHESTPLATE") &&
                        Objects.requireNonNull(equipment).getChestplate() == null)
                    equipment.setChestplate(item);
                else if (item.getType().toString().contains("LEGGINGS") &&
                        Objects.requireNonNull(equipment).getLeggings() == null)
                    equipment.setLeggings(item);
                else if (item.getType().toString().contains("BOOTS") &&
                        Objects.requireNonNull(equipment).getBoots() == null)
                    equipment.setBoots(item);
                else PlayerManager.giveItem(getPlayer(), item, LanguageManager.errors.inventoryFull);
            }
        PlayerManager.giveItem(getPlayer(), Shop.create(), LanguageManager.errors.inventoryFull);
    }

    /**
     * Sets up attributes properly after dying or first spawning.
     */
    public void setupAttributes() {
        Random r = new Random();
        int maxHealth = 500;

        // Set health for people with giant kits
        if ((Kit.giant().setKitLevel(1).equals(getKit()) ||
                Kit.giant().setKitLevel(1).equals(getKit2())) && !isSharing()) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant1", 2,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 550;
        }
        else if ((Kit.giant().setKitLevel(2).equals(getKit()) ||
                Kit.giant().setKitLevel(2).equals(getKit2())) && !isSharing()) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant2", 4,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 600;
        }
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT2))) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant2", 4,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 550;
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT1))) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant1", 2,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 600;
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }

        // Set health for people with health boost and are boosted
        if (isBoosted() && PlayerManager.hasAchievement(getID(), Achievement.topWave9().getID())) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("HealthBoost", 2,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth += 50;
        }

        // Set health for people with dwarf challenge
        if (getChallenges().contains(Challenge.dwarf())) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Dwarf", -.5,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            maxHealth /= 2;
        }

        // Make sure new health is set up correctly
        getPlayer().setHealth(
                Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .getValue());

        // Give blindness to people with that challenge
        if (getChallenges().contains(Challenge.blind()))
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 0));

        // Set up health and damage
        setMaxHealthInit(maxHealth);
        setBaseDamage(10);
    }
}
