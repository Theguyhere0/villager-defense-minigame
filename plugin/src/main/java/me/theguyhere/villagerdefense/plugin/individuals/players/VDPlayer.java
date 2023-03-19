package me.theguyhere.villagerdefense.plugin.individuals.players;

import com.google.common.util.concurrent.AtomicDouble;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaException;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.huds.BottomBarController;
import me.theguyhere.villagerdefense.plugin.huds.SidebarManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDHorse;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDPet;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.items.weapons.Ammo;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * A class holding data about players in a Villager Defense game.
 */
public class VDPlayer {
    /** UUID of corresponding {@link Player}.*/
    private final UUID player;
    /** Status of the this {@link VDPlayer}.*/
    private Status status;
    /** The arena that this {@link VDPlayer} belongs in.*/
    private final Arena arena;

    // Important arena stats
    private int maxHealth = 0;
    private int currentHealth = 0;
    private int absorption = 0;
    private int baseDamage = 0;
    private final Map<String, Integer> damageValues = new HashMap<>();
    private final AtomicDouble damageMultiplier = new AtomicDouble(1);
    private final AtomicInteger armor = new AtomicInteger();
    private final AtomicInteger toughness = new AtomicInteger();
    private final AtomicDouble weight = new AtomicDouble(1);
    private boolean ability = false;
    private final AtomicBoolean range = new AtomicBoolean(false);
    private final AtomicBoolean perBlock = new AtomicBoolean(false);
    private final AtomicInteger ammoCost = new AtomicInteger();
    private final AtomicInteger ammoCap = new AtomicInteger();
    private final AtomicReference<IndividualAttackType> attackType = new AtomicReference<>(IndividualAttackType.NORMAL);
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
            status = Status.SPECTATOR;
        else status = Status.ALIVE;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

            // Kill off pets
            pets.forEach(VDPet::kill);

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

    public void updateDamageMultiplier() {
        // Reset
        damageMultiplier.set(1);

        // Calculate boosts or reductions
        getPlayer().getActivePotionEffects().forEach(potionEffect -> {
            if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
                damageMultiplier.addAndGet(.1 * (potionEffect.getAmplifier() + 1));
            else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
                damageMultiplier.addAndGet(-.1 * (potionEffect.getAmplifier() + 1));
        });
        if (boost && PlayerManager.hasAchievement(player, Achievement.topKills9().getID()))
           damageMultiplier.addAndGet(.1);
        if (getPlayer().isInsideVehicle())
            damageMultiplier.addAndGet(VDHorse.getDamageBoost(getPets().stream().filter(pet -> pet instanceof VDHorse)
                    .collect(Collectors.toList()).get(0).getLevel()));
    }

    public void updateMainHand(ItemStack main) {
        // Reset values
        damageValues.clear();
        ability = false;
        range.set(false);
        perBlock.set(false);
        ammoCost.set(0);

        if (!VDItem.matches(main))
            return;

        if (VDAbility.matches(main) || VDAbility.matches(getPlayer().getInventory().getItemInOffHand()))
            ability = true;

        Objects.requireNonNull(Objects.requireNonNull(main.getItemMeta()).getLore()).forEach(lore -> {
            if (lore.contains(LanguageManager.messages.attackMainDamage
                    .replace("%s", ""))) {
                if (lore.contains("-")) {
                    String[] split = lore.substring(2 +
                                    LanguageManager.messages.attackMainDamage.length())
                            .split("-");
                    damageValues.put("mainLow", Integer.valueOf(split[0]));
                    damageValues.put("mainHigh",
                            Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                } else damageValues.put("main", Integer.valueOf(lore.substring(2 +
                                LanguageManager.messages.attackMainDamage.length())
                        .replace(ChatColor.BLUE.toString(), "")));
            }
            else if (lore.contains(LanguageManager.messages.attackCritDamage
                    .replace("%s", ""))) {
                if (lore.contains("-")) {
                    String[] split = lore.substring(2 +
                                    LanguageManager.messages.attackCritDamage.length())
                            .split("-");
                    damageValues.put("critLow", Integer.valueOf(split[0]));
                    damageValues.put("critHigh",
                            Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                } else damageValues.put("crit", Integer.valueOf(lore.substring(2 +
                                LanguageManager.messages.attackCritDamage.length())
                        .replace(ChatColor.BLUE.toString(), "")));
            }
            else if (lore.contains(LanguageManager.messages.attackSweepDamage
                    .replace("%s", ""))) {
                if (lore.contains("-")) {
                    String[] split = lore.substring(2 +
                                    LanguageManager.messages.attackSweepDamage.length())
                            .split("-");
                    damageValues.put("sweepLow", Integer.valueOf(split[0]));
                    damageValues.put("sweepHigh",
                            Integer.valueOf(split[1].replace(ChatColor.BLUE.toString(), "")));
                } else damageValues.put("sweep", Integer.valueOf(lore.substring(2 +
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
                    damageValues.put("rangeLow", Integer.valueOf(split[0]));
                    damageValues.put("rangeHigh", Integer.valueOf(split[1]
                            .replace(ChatColor.BLUE.toString(), "")));
                } else damageValues.put("range", Integer.valueOf(lore.substring(2 +
                                LanguageManager.messages.attackRangeDamage.length())
                        .replace(ChatColor.BLUE.toString(), "")));
            }
            else if (lore.contains(LanguageManager.names.penetrating))
                attackType.set(IndividualAttackType.PENETRATING);
            else if (lore.contains(LanguageManager.names.crushing))
                attackType.set(IndividualAttackType.CRUSHING);
            else if (lore.contains(LanguageManager.messages.ammoCost
                    .replace("%s", ""))) {
                ammoCost.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.ammoCost.length())
                        .replace(ChatColor.BLUE.toString(), "")));
            }
        });
    }

    public void updateMainHand() {
        updateMainHand(getPlayer().getInventory().getItemInMainHand());
    }

    public void updateOffHand(ItemStack off) {
        // Reset values
        ability = false;
        ammoCap.set(0);

        if (!VDItem.matches(off))
            return;

        if (VDAbility.matches(off) || VDAbility.matches(getPlayer().getInventory().getItemInMainHand()))
            ability = true;
        Objects.requireNonNull(Objects.requireNonNull(off.getItemMeta()).getLore()).forEach(lore -> {
            if (lore.contains(LanguageManager.messages.capacity
                    .replace("%s", ""))) {
                ammoCap.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.capacity.length())
                        .replace(ChatColor.BLUE.toString(), "")
                        .replace(ChatColor.WHITE.toString(), "")
                        .split(" / ")[0]));
            }
        });
    }

    public void updateArmor() {
        armor.set(0);
        toughness.set(0);
        weight.set(1);

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

        // Set speed
        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(.1 * weight.get());
    }

    public String getStatusBar() {
        // Make sure health was properly initialized
        if (maxHealth <= 0)
            return "";

        // Calculate damage
        String damage;
        List<Integer> values = new ArrayList<>(damageValues.values());
        values.sort(Comparator.comparingInt(Integer::intValue));
        values.replaceAll(original -> (int) ((original + (perBlock.get() ? 0 : baseDamage)) *
                damageMultiplier.get()));
        if (values.size() == 0)
            damage = Integer.toString(((int) (baseDamage * damageMultiplier.get())));
        else if (values.size() == 1)
            damage = Integer.toString((values.get(0)));
        else damage = values.get(0) + "-" + values.get(values.size() - 1);


        // Construct status bar
        String SPACE = "    ";
        String middleText = new ColoredMessage(ChatColor.AQUA, Utils.ARMOR + " " + armor) + SPACE +
                new ColoredMessage(ChatColor.DARK_AQUA, Utils.TOUGH + " " + toughness + "%");
        if (remainingAmmoWarningCooldown() > 0)
            middleText = new ColoredMessage(ChatColor.DARK_RED, LanguageManager.errors.ammoOffHand).toString();
        else if (ability && remainingAbilityCooldown() > 0)
            middleText = CommunicationManager.format(new ColoredMessage(ChatColor.DARK_RED,
                    LanguageManager.messages.cooldown), new ColoredMessage(ChatColor.AQUA,
                    Double.toString(Math.round(Utils.millisToSeconds(remainingAbilityCooldown()) * 10) / 10d)));
        ChatColor endTextColor;
        if (ammoCap.get() < ammoCost.get())
            endTextColor = ChatColor.DARK_RED;
        else if (attackType.get() == IndividualAttackType.CRUSHING)
            endTextColor = ChatColor.YELLOW;
        else if (attackType.get() == IndividualAttackType.PENETRATING)
            endTextColor = ChatColor.RED;
        else endTextColor = ChatColor.GREEN;

        return new ColoredMessage(absorption > 0 ? ChatColor.GOLD :
                ChatColor.RED, Utils.HP + " " + (currentHealth + absorption) + "/" + maxHealth) +
                SPACE +
                middleText +
                SPACE +
                new ColoredMessage(endTextColor, (range.get() ? Utils.ARROW : Utils.DAMAGE) + " " + damage +
                        (perBlock.get() ? " /" + Utils.BLOCK + " +" + (int) (baseDamage * damageMultiplier.get()) :
                                ""));
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

        // Update normal health display
        getPlayer().setHealth(Math.max(currentHealth *
                        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / maxHealth,
                1));
        getPlayer().setAbsorptionAmount(absorption *
                Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / maxHealth);
    }

    public void refill() {
        // Update ammo
        Ammo.updateRefill(Objects.requireNonNull(getPlayer().getEquipment()).getItemInMainHand(),
                boost && PlayerManager.hasAchievement(player, Achievement.allKits().getID()));
        Ammo.updateRefill(Objects.requireNonNull(getPlayer().getEquipment()).getItemInOffHand(),
                boost && PlayerManager.hasAchievement(player, Achievement.allKits().getID()));
    }

    public void takeDamage(int damage, @NotNull IndividualAttackType attackType) {
        // Scale damage by attack type
        if (attackType == IndividualAttackType.NORMAL || attackType == IndividualAttackType.CRUSHING)
            damage -= Math.min(damage, armor.get());
        if (attackType == IndividualAttackType.NORMAL || attackType == IndividualAttackType.PENETRATING)
            damage *= Math.max(0, 1 - toughness.get() / 100d);
        if (attackType == IndividualAttackType.NONE)
            damage = 0;

        // Apply boost
        if (boost && PlayerManager.hasAchievement(player, Achievement.totalKills9().getID()))
            damage *= .9;

        // Realize damage
        changeCurrentHealth(-damage);

        // Damage armor
        if (attackType == IndividualAttackType.NORMAL || attackType == IndividualAttackType.CRUSHING ||
                attackType == IndividualAttackType.PENETRATING)
            Arrays.stream(getPlayer().getInventory().getArmorContents()).filter(Objects::nonNull).forEach(armor ->
                    Bukkit.getPluginManager().callEvent(new PlayerItemDamageEvent(getPlayer(), armor, 0)));

        // Update normal health display
        getPlayer().setHealth(Math.max(currentHealth *
                        Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / maxHealth,
                1));
        getPlayer().setAbsorptionAmount(absorption *
                Objects.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() / maxHealth);
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

    public int dealRawDamage(@NotNull AttackClass playerAttackClass, double mainMult) {
        Random r = new Random();
        double damage;

        switch (playerAttackClass) {
            case MAIN:
                if (damageValues.size() == 0)
                    damage = baseDamage;
                else if (damageValues.containsKey(AttackClass.MAIN.straightID))
                    damage = (baseDamage + damageValues.get(AttackClass.MAIN.straightID)) * mainMult;
                else damage = (baseDamage + damageValues.get(AttackClass.MAIN.lowID) +
                        r.nextInt(damageValues.get(AttackClass.MAIN.highID) -
                                damageValues.get(AttackClass.MAIN.lowID))) * mainMult;
                break;
            case CRITICAL:
                if (damageValues.containsKey(AttackClass.CRITICAL.straightID))
                    damage = baseDamage + damageValues.get(AttackClass.CRITICAL.straightID);
                else damage = baseDamage + damageValues.get(AttackClass.CRITICAL.lowID) +
                        r.nextInt(damageValues.get(AttackClass.CRITICAL.highID) -
                                damageValues.get(AttackClass.CRITICAL.lowID));
                break;
            case SWEEP:
                if (damageValues.containsKey(AttackClass.SWEEP.straightID))
                    damage = baseDamage + damageValues.get(AttackClass.SWEEP.straightID);
                else damage = baseDamage + damageValues.get(AttackClass.SWEEP.lowID) +
                        r.nextInt(damageValues.get(AttackClass.SWEEP.highID) -
                                damageValues.get(AttackClass.SWEEP.lowID));
                break;
            case RANGE:
                if (damageValues.containsKey(AttackClass.RANGE.straightID))
                    damage = damageValues.get(AttackClass.RANGE.straightID);
                else damage = damageValues.get(AttackClass.RANGE.lowID) +
                        r.nextInt(damageValues.get(AttackClass.RANGE.highID) -
                                damageValues.get(AttackClass.RANGE.lowID));
                break;
            default:
                damage = 0;
        }

        return (int) (damage * damageMultiplier.get());
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

    public IndividualAttackType getAttackType() {
        return attackType.get();
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
        SidebarManager.updateActivePlayerSidebar(this);
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
        updateArmor();
    }

    /**
     * Returns armor to the player after the ninja ability wears out.
     */
    public void exposeArmor() {
        getPlayer().getInventory().setHelmet(helmet);
        getPlayer().getInventory().setChestplate(chestplate);
        getPlayer().getInventory().setLeggings(leggings);
        getPlayer().getInventory().setBoots(boots);
        updateArmor();
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

        updateArmor();
        updateOffHand(null);
        updateMainHand();
    }

    /**
     * Sets up attributes properly after dying or first spawning.
     */
    public void setupAttributes(boolean first) {
        Random r = new Random();
        int maxHealth = 500;

        // Set health for people with giant kits
        if (Kit.giant().setKitLevel(1).equals(getKit()) && !isSharing()) {
            maxHealth = 550;
        }
        else if (Kit.giant().setKitLevel(2).equals(getKit()) && !isSharing()) {
            maxHealth = 600;
        }
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(Kit.EffectType.GIANT1))) {
            maxHealth = 550;
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }
        else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(Kit.EffectType.GIANT2))) {
            maxHealth = 600;
            PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
        }

        // Set health for people with health boost and are boosted
        if (boost && PlayerManager.hasAchievement(player, Achievement.topWave9().getID())) {
            maxHealth += 50;
        }

        // Set health for people with dwarf challenge
        if (getChallenges().contains(Challenge.dwarf())) {
            maxHealth /= 2;
        }

        // Give blindness to people with that challenge
        if (getChallenges().contains(Challenge.blind()))
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 0));

        // Set up health and damage
        setMaxHealthInit(maxHealth);

        // Set up status bar
        updateDamageMultiplier();
        BottomBarController.startStatusBar(this);

        // Only run the first time
        if (first) {
            // Set up pet slots
            if (Kit.trainer().setKitLevel(1).equals(getKit()) && !isSharing())
                petSlots = 4;
            else if (Kit.trainer().setKitLevel(2).equals(getKit()) && !isSharing())
                petSlots = 5;
            else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(Kit.EffectType.TRAINER1))) {
                petSlots = 4;
                PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
            } else if (r.nextDouble() > Math.pow(.75, arena.effectShareCount(Kit.EffectType.TRAINER2))) {
                petSlots = 5;
                PlayerManager.notifySuccess(getPlayer(), LanguageManager.messages.effectShare);
            } else petSlots = 3;
        }
    }

    /**
     * Status of players in Villager Defense. Possible status:<ul>
     *     <li>{@link #ALIVE}</li>
     *     <li>{@link #GHOST}</li>
     *     <li>{@link #SPECTATOR}</li>
     * </ul>
     */
    public enum Status {
        /** Player is alive and active in the game.*/
        ALIVE,
        /** Player is dead but active in the game.*/
        GHOST,
        /** Player is spectating in the game.*/
        SPECTATOR
    }

    public enum AttackClass {
        MAIN("mainLow", "mainHigh", "main"),
        CRITICAL("critLow", "critHigh", "crit"),
        SWEEP("sweepLow", "sweepHigh", "sweep"),
        RANGE("rangeLow", "rangeHigh", "range");

        private final String lowID;
        private final String highID;
        private final String straightID;

        private AttackClass(String lowID, String highID, String straightID) {
            this.lowID = lowID;
            this.highID = highID;
            this.straightID = straightID;
        }
    }
}
