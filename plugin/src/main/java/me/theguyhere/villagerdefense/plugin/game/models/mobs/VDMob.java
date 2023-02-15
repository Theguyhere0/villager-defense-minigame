package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaException;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.displays.Popup;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.game.models.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class VDMob {
    protected Mob mob;
    protected UUID id;
    protected final String lore;
    protected final Map<UUID, Integer> damageMap = new HashMap<>();
    protected int gameID;
    protected int wave;
    protected String name;
    protected int hpBarSize;

    protected int level;
    protected int maxHealth = 0;
    protected int currentHealth = 0;
    protected int armor = 0;
    protected double toughness = 0;
    protected int damage = 0;
    protected double damageSpread = 0;
    protected PotionEffectType effectType;
    protected int effectLevel = 0;
    protected int effectDuration = 0;
    protected int pierce = 0;
    protected final AttackType attackType;
    protected double attackSpeed = 0;
    protected TargetPriority targetPriority = TargetPriority.NONE;
    protected int targetRange = 0;
    protected int loot = 0;
    protected double lootSpread = 0;
    protected long lastStrike = 0;
    
    public static final String VD = "VD";
    public static final String TEAM = "VDTeam";
    private static final String KNOCKBACK = "knockback";
    private static final String WEIGHT = "weight";
    private static final String SPEED = "speed";
    
    protected VDMob(String lore, AttackType attackType) {
        this.lore = lore;
        this.attackType = attackType;
    }

    public Mob getEntity() {
        return mob;
    }

    public UUID getID() {
        return id;
    }

    public int getGameID() {
        return gameID;
    }

    public int getWave() {
        return wave;
    }

    public int takeDamage(int damage, @NotNull AttackType attackType, @Nullable Player attacker, Arena arena) {
        // Final damage calculation and display
        if (attackType == AttackType.NORMAL || attackType == AttackType.CRUSHING)
            damage -= Math.min(damage, armor);
        if (attackType == AttackType.NORMAL || attackType == AttackType.PENETRATING)
            damage *= Math.max(0, 1 - toughness);
        if (attackType == AttackType.NONE)
            damage = 0;
        if (attacker != null)
            try {
                Popup.create(getEntity().getEyeLocation(),
                        new ColoredMessage(ChatColor.RED, "-" + damage + Utils.HP).toString(), 1,
                        attacker.getPlayer());
            } catch (InvalidLocationException ignored) {
            }

        UUID attackerID = attacker == null ? null : attacker.getUniqueId();

        // Fatal damage
        if (damage >= currentHealth) {
            addDamage(currentHealth, attackerID);
            Random r = new Random();
            int finalGems = (int) (loot * (1 + (r.nextDouble() * 2 - 1) * lootSpread));
            int finalExp = (int) (loot * (1 + (r.nextDouble() * 2 - 1) * lootSpread)) / 10;

            // Reward for all damagers
            damageMap.forEach((id, contribution) -> {
                int gems = (int) (finalGems * (double) contribution / maxHealth);
                int exp = (int) (finalExp * (double) contribution / maxHealth);
                try {
                    VDPlayer gamer = arena.getPlayer(id);
                    
                    // Check if player has gem increase achievement and is boosted
                    if (gamer.isBoosted() && PlayerManager.hasAchievement(id, Achievement.topBalance9().getID()))
                        gems *= 1.1;
                    gamer.addGems(gems);

                    // Create popup
                    try {
                        Popup.create(getEntity().getLocation().add(0, 1, 0),
                                new ColoredMessage(ChatColor.GREEN, "+" + gems + Utils.GEM  + "  ") +
                                new ColoredMessage(ChatColor.YELLOW, "+" + exp + Utils.EXP).toString(), 2.5,
                                gamer.getPlayer());
                    } catch (InvalidLocationException ignored) {
                    }

                    // Update player stats
                    PlayerManager.setTotalGems(id, PlayerManager.getTotalGems(id) + gems);
                    if (PlayerManager.getTopBalance(id) < gamer.getGems())
                        PlayerManager.setTopBalance(id, gamer.getGems());

                    // Update scoreboard
                    GameManager.createBoard(gamer);

                    // Give exp
                    gamer.getPlayer().giveExp(exp);
                } catch (PlayerNotFoundException ignored) {
                }
            });
            
            // Reward kill to dealer of final blow
            try {
                arena.getPlayer(attacker).incrementKills();
            } catch (PlayerNotFoundException ignored) {
            }

            // Kill
            getEntity().setHealth(0);
        }
        
        // Non-fatal damage
        else {
            addDamage(damage, attackerID);
        }
        
        // Update entity name
        updateNameTag();

        return damage;
    }

    protected void addDamage(int damage, UUID id) {
        currentHealth -= damage;
        if (id == null)
            return;
        if (damageMap.containsKey(id))
            damageMap.replace(id, damageMap.get(id) + damage);
        else damageMap.put(id, damage);
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

        // Update true health
        currentHealth = Math.min(Math.max(currentHealth + dif, 0), maxHealth);
    }

    public int dealRawDamage() {
        Random r = new Random();
        AtomicInteger increase = new AtomicInteger();
        mob.getActivePotionEffects().forEach(potionEffect -> {
            if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
                increase.addAndGet(1 + potionEffect.getAmplifier());
            else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
                increase.addAndGet(- 1 - potionEffect.getAmplifier());
        });
        return (int) (this.damage * (1 + (r.nextDouble() * 2 - 1) * damageSpread) * (1 + .1 * increase.get()));
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    public PotionEffect dealEffect() {
        return effectType == null ? null : new PotionEffect(effectType, Utils.secondsToTicks(effectDuration),
                effectLevel - 1);
    }

    public int getPierce() {
        return pierce;
    }

    /**
     * Check whether the cooldown for the mob is up or not. If it is, update when it last attacked.
     * @return Whether the cooldown was up or not.
     */
    public boolean attackAttempt() {
        boolean cooldownUp = System.currentTimeMillis() >= lastStrike + Utils.secondsToMillis(attackSpeed);
        if (cooldownUp)
            lastStrike = System.currentTimeMillis();
        return cooldownUp || System.currentTimeMillis() < lastStrike + Utils.secondsToMillis(0.1);
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public TargetPriority getTargetPriority() {
        return targetPriority;
    }

    public int getTargetRange() {
        return targetRange;
    }

    /**
     * Sets the proper health for the mob.
     */
    protected void setHealth(int health) {
        maxHealth = health;
        currentHealth = maxHealth;
    }

    /**
     * Sets the proper damage for the mob.
     * @param base Base damage.
     * @param spread Damage spread in terms of proportion.
     */
    protected void setDamage(int base, double spread) {
        damage = base;
        damageSpread = spread;
    }

    // Sets the proper effect type, if there is one
    protected void setEffectType(PotionEffectType effectType) {
        this.effectType = effectType;
    }

    // Set attack speed options
    protected void setVerySlowAttackSpeed() {
        attackSpeed = 2;
    }
    protected void setSlowAttackSpeed() {
        attackSpeed = 1;
    }
    protected void setModerateAttackSpeed() {
        attackSpeed = .7;
    }
    protected void setFastAttackSpeed() {
        attackSpeed = .4;
    }
    protected void setVeryFastAttackSpeed() {
        attackSpeed = .2;
    }


    // Set knockback options
    protected void setNoneKnockback() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        0 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setLowKnockback() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        .25 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setModerateKnockback() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        .75 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setHighKnockback() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        1.25 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setVeryHighKnockback() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        2.5 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }

    // Set weight options
    protected void setVeryLightWeight() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        0 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setLightWeight() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .1 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setMediumWeight() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .25 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setHeavyWeight() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .4 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setVeryHeavyWeight() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .7 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }


    // Set speed options
    protected void setVerySlowSpeed() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .12 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setSlowSpeed() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .2 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setMediumSpeed() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .275 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setFastSpeed() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .325 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setVeryFastSpeed() {
        double initial = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .4 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }

    // Set target range
    protected void setCloseTargetRange() {
        targetRange = 12;
    }
    protected void setModerateTargetRange() {
        targetRange = 24;
    }
    protected void setFarTargetRange() {
        targetRange = 40;
    }
    protected void setUnboundedTargetRange() {
        targetRange = -1;
    }

    /**
     * Sets the proper loot for the mob.
     * @param value The value of the mob.
     * @param spread Spread in gem drop.
     */
    protected void setLoot(int value, double spread) {
        loot = (int) Math.pow(value, .9);
        lootSpread = spread;
    }

    // Set name properly
    protected void updateNameTag(ChatColor color) {
        int healthLength = Integer.toString(currentHealth).length();
        int trueSize = hpBarSize * 4 + healthLength;
        int bars = (int) ((double) currentHealth / maxHealth * trueSize);
        StringBuilder healthIndicator = new StringBuilder(new String(new char[bars])
                .replace("\0", Utils.HP_BAR))
                .append(new String(new char[trueSize - bars]).replace("\0", " "));
        healthIndicator.replace(hpBarSize * 2, hpBarSize * 2 + healthLength, "&b" + currentHealth + color);
        getEntity().setCustomName(CommunicationManager.format(
                new ColoredMessage(color, LanguageManager.messages.mobName),
                new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
                new ColoredMessage(color, name),
                new ColoredMessage(ChatColor.RESET, CommunicationManager.format(
                        String.format("&7[" + color + "%s&7]", healthIndicator)))
        ));
    }

    protected abstract void updateNameTag();
}
