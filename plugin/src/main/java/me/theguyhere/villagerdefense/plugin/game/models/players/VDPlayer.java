package me.theguyhere.villagerdefense.plugin.game.models.players;

import com.google.common.util.concurrent.AtomicDouble;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaException;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.game.models.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.Ammo;
import me.theguyhere.villagerdefense.plugin.game.models.kits.EffectType;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.pets.VDPet;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
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
    /** The time until weapon cooldown us up.*/
    private long weaponCooldown = 0;
    /** The time until ammo warning cooldown us up.*/
    private long ammoWarningCooldown = 0;
    /** The time until ability cooldown us up.*/
    private long abilityCooldown = 0;
    /** Gem balance.*/
    private int gems = 0;
    /** Kill count.*/
    private int kills = 0;
    /** Pets following the player.*/
    private final List<VDPet> pets = new ArrayList<>();
    /** Maximum pet slots available for use.*/
    private int petSlots = 0;
    /** The wave at which the player joined the game as an active player.*/
    private int joinedWave = 0;
    /** The number of times this player violated arena boundaries.*/
    private int infractions = 0;
    /** The {@link Kit} the player will play with.*/
    private Kit kit = Kit.none();
    /** The level of tiered ammo the player has.*/
    private int tieredAmmoLevel = 0;
    /** The level of tiered essence the player has.*/
    private int tieredEssenceLevel = 0;
    /** The list of {@link Challenge}'s the player will take on.*/
    private List<Challenge> challenge = new ArrayList<>();
    /** The list of UUIDs of those that damaged the player.*/
    private final List<UUID> enemies = new ArrayList<>();
    /** Helmet {@link ItemStack} held for ninja ability.*/
    private ItemStack helmet;
    /** Chestplate {@link ItemStack} held for ninja ability.*/
    private ItemStack chestplate;
    /** Leggings {@link ItemStack} held for ninja ability.*/
    private ItemStack leggings;
    /** Boots {@link ItemStack} held for ninja ability.*/
    private ItemStack boots;
    /** Whether permanent boosts are on or not.*/
    private boolean boost = true;
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasMaxHealth() {
        return currentHealth == maxHealth;
    }

    public void addAbsorption(int absorption) {
        this.absorption += absorption;
    }

    public void addAbsorptionUpTo(int absorption) {
        if (this.absorption < absorption)
            this.absorption = absorption;
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
        currentHealth = Math.min(Math.max(currentHealth + trueDif, 0), maxHealth);

        // Set warning effect
        NMSVersion.getCurrent().getNmsManager().createEffect(arena.getPlayerSpawn().getLocation(),
                currentHealth / (double) maxHealth).sendTo(getPlayer());

        // Check for death
        if (this.currentHealth == 0) {
            // Check if player has resurrection achievement and is boosted
            Random random = new Random();
            if (boost && random.nextDouble() < .1 &&
                    PlayerManager.hasAchievement(getPlayer().getUniqueId(), Achievement.allChallenges().getID())) {
                PlayerManager.giveTotemEffect(getPlayer());
                currentHealth = maxHealth / 2;
                return;
            }

            // Set player to fake death mode
            PlayerManager.fakeDeath(this);

            // Check for explosive challenge
            if (getChallenges().contains(Challenge.explosive())) {
                // Create an explosion
                getPlayer().getWorld().createExplosion(getPlayer().getLocation(), 1.75F, false, false);

                // Drop all items and clear inventory
                getPlayer().getInventory().forEach(itemStack -> {
                    if (itemStack != null && !Shop.matches(itemStack) && !VDAbility.matches(itemStack))
                        getPlayer().getWorld().dropItemNaturally(getPlayer().getLocation(), itemStack);
                });
                getPlayer().getInventory().clear();
                tieredEssenceLevel = 0;
                tieredAmmoLevel = 0;
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

    public void showAndUpdateStats() {
        AtomicBoolean penetrating = new AtomicBoolean(false);
        AtomicBoolean crushing = new AtomicBoolean(false);
        AtomicBoolean range = new AtomicBoolean(false);
        AtomicBoolean perBlock = new AtomicBoolean(false);
        boolean ability = false;
        AtomicInteger ammoCost = new AtomicInteger();
        AtomicInteger ammoCap = new AtomicInteger();
        AtomicInteger armor = new AtomicInteger();
        AtomicInteger toughness = new AtomicInteger();
        AtomicDouble weight = new AtomicDouble(1);
        String damage = Integer.toString(baseDamage);
        AtomicDouble increase = new AtomicDouble();

        // Make sure health was properly initialized
        if (maxHealth <= 0)
            return;

        // Calculate stats
        try {
            ItemStack weapon = Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand();
            List<Integer> damageValues = new ArrayList<>();

            if (VDAbility.matches(weapon))
                ability = true;
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
                else if (lore.contains(LanguageManager.names.penetrating))
                    penetrating.set(true);
                else if (lore.contains(LanguageManager.names.crushing))
                    crushing.set(true);
                else if (lore.contains(LanguageManager.messages.ammoCost
                        .replace("%s", ""))) {
                    ammoCost.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.ammoCost.length())
                            .replace(ChatColor.BLUE.toString(), "")));
                }
            });
            damageValues.sort(Comparator.comparingInt(Integer::intValue));

            // Calculate boosts or reductions
            getPlayer().getActivePotionEffects().forEach(potionEffect -> {
                if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
                    increase.addAndGet((1 + potionEffect.getAmplifier()) * .1);
                else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
                    increase.addAndGet(- (1 + potionEffect.getAmplifier()) * .1);
            });
            if (boost && PlayerManager.hasAchievement(player, Achievement.topKills9().getID()))
                increase.addAndGet(.1);

            // Apply base damage and multipliers
            damageValues.replaceAll(original -> (int) ((original + (perBlock.get() ? 0 : baseDamage)) *
                    (1 + increase.get())));

            if (damageValues.size() == 1)
                damage = Integer.toString((damageValues.get(0)));
            else damage = damageValues.get(0) + "-" + damageValues.get(damageValues.size() - 1);
        } catch (Exception ignored) {
        }
        try {
            ItemStack ammo = Objects.requireNonNull(getPlayer().getEquipment()).getItemInOffHand();

            if (VDAbility.matches(ammo))
                ability = true;
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

        // Resistance effect
        getPlayer().getActivePotionEffects().forEach(potionEffect -> {
            if (PotionEffectType.DAMAGE_RESISTANCE.equals(potionEffect.getType())) {
                armor.addAndGet(10 * (1 + potionEffect.getAmplifier()));
                toughness.addAndGet(10 * (1 + potionEffect.getAmplifier()));
            }
        });

        // Update status bar
        String SPACE = "    ";
        String middleText = new ColoredMessage(ChatColor.AQUA, Utils.ARMOR + " " + armor) + SPACE +
                new ColoredMessage(ChatColor.DARK_AQUA, Utils.TOUGH + " " + toughness + "%");
        if (remainingAmmoWarningCooldown() > 0)
            middleText = new ColoredMessage(ChatColor.DARK_RED, LanguageManager.errors.ammoOffHand).toString();
        else if (ability && remainingAbilityCooldown() > 0)
            middleText = CommunicationManager.format(new ColoredMessage(ChatColor.DARK_RED,
                    LanguageManager.messages.cooldown), new ColoredMessage(ChatColor.AQUA,
                    Double.toString(Math.round(Utils.millisToSeconds(remainingAbilityCooldown()) * 10) / 10d)));
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                new ColoredMessage(absorption > 0 ? ChatColor.GOLD : ChatColor.RED,
                        Utils.HP + " " + (currentHealth + absorption) + "/" + maxHealth) + SPACE + middleText
                         + SPACE + new ColoredMessage(ammoCap.get() < ammoCost.get() ? ChatColor.DARK_RED :
                        crushing.get() ? ChatColor.YELLOW : penetrating.get() ? ChatColor.RED : ChatColor.GREEN,
                        (range.get() ? Utils.ARROW : Utils.DAMAGE) + " " + damage +
                                (perBlock.get() ? " /" + Utils.BLOCK + " +" + (int) (baseDamage *
                                        (1 + increase.get())) : ""))));

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
        // Natural heal
        if (!challenge.contains(Challenge.uhc())) {
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

        // Regeneration
        getPlayer().getActivePotionEffects().forEach(potionEffect -> {
            if (PotionEffectType.REGENERATION.equals(potionEffect.getType()))
                changeCurrentHealth(5 * (1 + potionEffect.getAmplifier()));
        });
    }

    public void refill() {
        // Update ammo
        Ammo.updateRefill(Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand(),
                boost && PlayerManager.hasAchievement(player, Achievement.allKits().getID()));
        Ammo.updateRefill(Objects.requireNonNull(getPlayer().getEquipment()).getItemInOffHand(),
                boost && PlayerManager.hasAchievement(player, Achievement.allKits().getID()));
    }

    public void takeDamage(int damage, @NotNull AttackType attackType) {
        // Scale damage by attack type
        if (attackType == AttackType.NORMAL || attackType == AttackType.CRUSHING)
            damage -= Math.min(damage, armor);
        if (attackType == AttackType.NORMAL || attackType == AttackType.PENETRATING)
            damage *= Math.max(0, 1 - toughness / 100d);
        if (attackType == AttackType.NONE)
            damage = 0;

        // Apply boost
        if (boost && PlayerManager.hasAchievement(player, Achievement.totalKills9().getID()))
            damage *= .9;

        // Realize damage
        changeCurrentHealth(-damage);

        // Damage armor
        if (attackType == AttackType.NORMAL || attackType == AttackType.CRUSHING ||
                attackType == AttackType.PENETRATING)
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
        double damage;
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
                        damage = (baseDamage + attributes.get("main")) * mainMult;
                    else damage = (baseDamage + attributes.get("mainLow") +
                            r.nextInt(attributes.get("mainHigh") - attributes.get("mainLow"))) * mainMult;
                    break;
                case CRITICAL:
                    if (attributes.containsKey("crit"))
                        damage = baseDamage + attributes.get("crit");
                    else damage = baseDamage + attributes.get("critLow") +
                            r.nextInt(attributes.get("critHigh") - attributes.get("critLow"));
                    break;
                case SWEEP:
                    if (attributes.containsKey("sweep"))
                        damage = baseDamage + attributes.get("sweep");
                    else damage = baseDamage + attributes.get("sweepLow") +
                            r.nextInt(attributes.get("sweepHigh") - attributes.get("sweepLow"));
                    break;
                case RANGE:
                    if (attributes.containsKey("range"))
                        damage = attributes.get("range");
                    else damage = attributes.get("rangeLow") + r.nextInt(attributes.get("rangeHigh") -
                            attributes.get("rangeLow"));
                    break;
                default:
                    damage = 0;
            }
        } catch (Exception e) {
            damage = baseDamage;
        }

        // Calculate boosts or reductions
        AtomicInteger increase = new AtomicInteger();
        getPlayer().getActivePotionEffects().forEach(potionEffect -> {
            if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
                increase.addAndGet(1 + potionEffect.getAmplifier());
            else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
                increase.addAndGet(- 1 - potionEffect.getAmplifier());
        });
        if (boost && PlayerManager.hasAchievement(player, Achievement.topKills9().getID()))
            increase.incrementAndGet();

        return (int) (damage * (1 + .1 * increase.get()));
    }

    public long remainingWeaponCooldown() {
        return Math.max(weaponCooldown - System.currentTimeMillis(), 0);
    }

    public void triggerWeaponCooldown(int cooldown) {
        weaponCooldown = System.currentTimeMillis() + cooldown;
    }

    public long remainingAmmoWarningCooldown() {
        return Math.max(ammoWarningCooldown - System.currentTimeMillis(), 0);
    }

    public void triggerAmmoWarningCooldown() {
        ammoWarningCooldown = System.currentTimeMillis() + Utils.secondsToMillis(1);
    }

    public long remainingAbilityCooldown() {
        return Math.max(abilityCooldown - System.currentTimeMillis(), 0);
    }

    public void triggerAbilityCooldown(int cooldown) {
        abilityCooldown = System.currentTimeMillis() + cooldown;
    }

    public AttackType getAttackType() {
        try {
            ItemStack weapon = Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand();
            if (Objects.requireNonNull(Objects.requireNonNull(weapon.getItemMeta()).getLore()).stream()
                    .anyMatch(lore -> lore.contains(LanguageManager.names.penetrating)))
                return AttackType.PENETRATING;
            if (Objects.requireNonNull(Objects.requireNonNull(weapon.getItemMeta()).getLore()).stream()
                    .anyMatch(lore -> lore.contains(LanguageManager.names.crushing)))
                return AttackType.CRUSHING;
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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canAfford(int cost) {
        return cost <= gems;
    }

    public void incrementKills() {
        kills++;
    }

    public Kit getKit() {
        return kit;
    }

    public int getTieredAmmoLevel() {
        return tieredAmmoLevel;
    }

    public void incrementTieredAmmoLevel() {
        tieredAmmoLevel++;
    }

    public int getTieredEssenceLevel() {
        return tieredEssenceLevel;
    }

    public void incrementTieredEssenceLevel() {
        tieredEssenceLevel++;
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
        GameManager.createBoard(this);
    }

    public boolean isSharing() {
        return share;
    }

    public void toggleShare() {
        share = !share;
    }

    public void addPet(VDPet pet) {
        pets.add(pet);
        arena.addMob(pet);
    }

    public void removePet(int index) {
        pets.get(index).getEntity().remove();
        arena.removeMob(pets.get(index).getID());
        pets.remove(index);
    }

    public void respawnPets() {
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getEntity().isDead()) {
                VDPet newPet = pets.get(i).respawn(arena, getPlayer().getLocation());
                pets.set(i, newPet);
                arena.addMob(newPet);
            }
        }
    }

    public int getPetSlots() {
        return petSlots;
    }

    public int getRemainingPetSlots() {
        // Calculate remaining slots
        AtomicInteger remaining = new AtomicInteger(petSlots);
        pets.forEach(pet -> remaining.addAndGet(-pet.getSlots()));

        return remaining.get();
    }

    public List<VDPet> getPets() {
        return pets;
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

    /**
     * Removes armor from the player while they are invisible under the ninja ability.
     */
    public void hideArmor() {
        helmet = getPlayer().getInventory().getHelmet();
        getPlayer().getInventory().setHelmet(null);
        chestplate = getPlayer().getInventory().getChestplate();
        getPlayer().getInventory().setChestplate(null);
        leggings = getPlayer().getInventory().getLeggings();
        getPlayer().getInventory().setLeggings(null);
        boots = getPlayer().getInventory().getBoots();
        getPlayer().getInventory().setBoots(null);
    }

    /**
     * Returns armor to the player after the ninja ability wears out.
     */
    public void exposeArmor() {
        getPlayer().getInventory().setHelmet(helmet);
        getPlayer().getInventory().setChestplate(chestplate);
        getPlayer().getInventory().setLeggings(leggings);
        getPlayer().getInventory().setBoots(boots);
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
            else {
                if (boost && PlayerManager.hasAchievement(player, Achievement.allMaxedAbility().getID()))
                    PlayerManager.giveItem(getPlayer(), VDAbility.modifyCooldown(item, .9),
                            LanguageManager.errors.inventoryFull);
                else PlayerManager.giveItem(getPlayer(), item, LanguageManager.errors.inventoryFull);
            }
        }
        PlayerManager.giveItem(getPlayer(), Shop.create(), LanguageManager.errors.inventoryFull);
    }

    /**
     * Sets up attributes properly after dying or first spawning.
     */
    public void setupAttributes(boolean first) {
        Random r = new Random();
        int maxHealth = 500;

        // Set health for people with giant kits
        if (Kit.giant().setKitLevel(1).equals(getKit()) && !isSharing()) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant1", 2,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 550;
        }
        else if (Kit.giant().setKitLevel(2).equals(getKit()) && !isSharing()) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant2", 4,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 600;
        }
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT1))) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant2", 4,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 550;
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.GIANT2))) {
            Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
                    .addModifier(new AttributeModifier("Giant1", 2,
                            AttributeModifier.Operation.ADD_NUMBER));
            maxHealth = 600;
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }

        // Set health for people with health boost and are boosted
        if (boost && PlayerManager.hasAchievement(player, Achievement.topWave9().getID())) {
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

        // Only run the first time
        if (first) {
            // Set up pet slots
            if (Kit.trainer().setKitLevel(1).equals(getKit()) && !isSharing())
                petSlots = 4;
            else if (Kit.trainer().setKitLevel(2).equals(getKit()) && !isSharing())
                petSlots = 5;
            else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.TRAINER1))) {
                petSlots = 4;
                PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
            } else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(EffectType.TRAINER2))) {
                petSlots = 5;
                PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
            } else petSlots = 3;
        }
    }
}
