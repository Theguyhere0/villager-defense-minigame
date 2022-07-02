package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.displays.Popup;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class VDMob {
    protected UUID id;
    protected final Map<UUID, Integer> damageMap = new HashMap<>();
    protected int gameID;
    protected int wave;
    protected String name;
    protected int hpBarSize;
    
    protected final int level;
    protected int maxHealth;
    protected int currentHealth;
    protected int armor;
    protected double toughness;
    protected int damage;
    protected double damageSpread;
    protected final AttackType attackType;
    protected double attackSpeed;
    protected int loot;
    protected double lootSpread;
    protected long lastStrike = 0;
    
    public static final String VD = "VD";
    private static final String KNOCKBACK = "knockback";
    private static final String WEIGHT = "weight";
    private static final String SPEED = "speed";
    
    protected VDMob(int level, AttackType attackType) {
        this.level = level;
        this.attackType = attackType;
    }
    
    public abstract LivingEntity getEntity();

    public UUID getID() {
        return id;
    }

    public int getGameID() {
        return gameID;
    }

    public int getWave() {
        return wave;
    }

    public void takeDamage(int damage, @NotNull AttackType attackType, @Nullable Player attacker, Arena arena) {
        // Final damage calculation and display
        if (attackType == AttackType.NORMAL)
            damage -= Math.min(damage, armor);
        else damage *= Math.max(0, 1 - toughness);
        if (attacker != null)
            try {
                Popup.create(getEntity().getEyeLocation(),
                        new ColoredMessage(ChatColor.RED, "-" + damage + "\u2665").toString(), 1,
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
                        Popup.create(getEntity().getLocation(),
                                new ColoredMessage(ChatColor.GREEN, "+" + gems + "\u2666  ") +
                                new ColoredMessage(ChatColor.YELLOW, "+" + exp + "\u2605").toString(), 2.5,
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
    }
    private void addDamage(int damage, UUID id) {
        currentHealth -= damage;
        if (id == null)
            return;
        if (damageMap.containsKey(id))
            damageMap.replace(id, damageMap.get(id) + damage);
        else damageMap.put(id, damage);
    }
    
    public int dealDamage(int armor, double toughness) {
        int damage = dealRawDamage();
        if (attackType == AttackType.NORMAL)
            return damage - Math.min(damage, armor);
        else return (int) (damage * Math.max(0, 1 - toughness));
    }
    
    public int dealRawDamage() {
        Random r = new Random();
        return (int) (this.damage * (1 + (r.nextDouble() * 2 - 1) * damageSpread));
    }

    public AttackType getAttackType() {
        return attackType;
    }

    /**
     * Check whether the cooldown for the mob is up or not. If it is, update when it last attacked.
     * @return Whether the cooldown was up or not.
     */
    public boolean checkCooldown() {
        boolean cooldownUp = System.currentTimeMillis() >= lastStrike + attackSpeed * 1000;
        if (cooldownUp)
            lastStrike = System.currentTimeMillis();
        return cooldownUp;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    // Function for Gaussian level distribution, with restrictions
    protected static int getLevel(double difficulty, double rate, int start) {
        Random r = new Random();
        double mult = 1 + .1 * Math.max(Math.min(r.nextGaussian(), 3), -3); // Mean 100%, SD 10%, restrict 30%
        return Math.max((int) ((difficulty * mult - start) / rate), 1);
    }

    // Sets the proper health for the mob
    protected void setHealth(int base, int delta, int lvl, int start) {
        maxHealth = base + delta * Math.max(0, lvl - start + 1);
        currentHealth = maxHealth;
    }

    // Sets the proper armor for the mob
    protected void setArmor(int base, int delta, int lvl, int start) {
        armor = base + delta * Math.max(0, lvl - start + 1);
    }

    // Sets the proper toughness for the mob
    protected void setToughness(double base, double delta, int lvl, int start) {
        toughness = base + delta * Math.max(0, lvl - start + 1);
    }

    // Sets the proper damage for the mob
    protected void setDamage(int base, int delta, int lvl, int start, double spread) {
        damage = base + delta * Math.max(0, lvl - start + 1);
        damageSpread = spread;
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
    protected void setNoneKnockback(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        0 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setLowKnockback(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        1 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setModerateKnockback(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        2 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setHighKnockback(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        3.5 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setVeryHighKnockback(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK))
                .addModifier(new AttributeModifier(
                        KNOCKBACK,
                        5 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }

    // Set weight options
    protected void setVeryLightWeight(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        0 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setLightWeight(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .1 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setMediumWeight(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .25 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setHeavyWeight(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .4 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setVeryHeavyWeight(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .getValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE))
                .addModifier(new AttributeModifier(
                        WEIGHT,
                        .7 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }


    // Set speed options
    protected void setVerySlowLandSpeed(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .1 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setSlowLandSpeed(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .2 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setMediumLandSpeed(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .3 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setFastLandSpeed(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .4 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }
    protected void setVeryFastLandSpeed(LivingEntity livingEntity) {
        double initial = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .getBaseValue();
        Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                .addModifier(new AttributeModifier(
                        SPEED,
                        .5 - initial,
                        AttributeModifier.Operation.ADD_NUMBER
                ));
    }

    // Sets the proper loot for the mob
    protected void setLoot(int base, double rate, int lvl, int start, double spread) {
        loot = (int) (base * Math.pow(rate, Math.max(0, lvl - start + 1)));
        lootSpread = spread;
    }

    // Set name properly
    protected void updateNameTag() {
        int healthLength = Integer.toString(currentHealth).length();
        int trueSize = hpBarSize * 4 + healthLength;
        int bars = (int) ((double) currentHealth / maxHealth * trueSize);
        StringBuilder healthIndicator = new StringBuilder(new String(new char[bars])
                .replace("\0", "\u258c"))
                .append(new String(new char[trueSize - bars]).replace("\0", " "));
        healthIndicator.replace(hpBarSize * 2, hpBarSize * 2 + healthLength, "&b" + currentHealth + "&c");
        getEntity().setCustomName(CommunicationManager.format(
                new ColoredMessage(ChatColor.RED, LanguageManager.messages.mobName),
                new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
                new ColoredMessage(ChatColor.RED, name),
                new ColoredMessage(ChatColor.RESET, CommunicationManager.format(
                        String.format("&7[&c%s&7]", healthIndicator)))
        ));
    }

    // Prepares the entity as a minion
    protected void setMinion(Arena arena, LivingEntity livingEntity, String name) {
        Main.getMonstersTeam().addEntry(livingEntity.getUniqueId().toString());
        livingEntity.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        hpBarSize = 2;
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCanPickupItems(false);
        if (livingEntity.isInsideVehicle())
            Objects.requireNonNull(livingEntity.getVehicle()).remove();
        for (Entity passenger : livingEntity.getPassengers())
            passenger.remove();
        livingEntity.setHealth(1);
        updateNameTag();
        livingEntity.setCustomNameVisible(true);
    }
    
    public void remove(Arena arena) {
        if (Main.getVillagersTeam().hasEntry(id.toString()))
            Main.getVillagersTeam().removeEntry(id.toString());
        if (Main.getMonstersTeam().hasEntry(id.toString()))
            Main.getMonstersTeam().removeEntry(id.toString());
        arena.removeMob(id);
    }
}
