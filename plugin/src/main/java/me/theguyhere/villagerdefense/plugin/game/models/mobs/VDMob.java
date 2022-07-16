package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidVDMobKeyException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.displays.Popup;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class VDMob {
    protected Mob mob;
    protected UUID id;
    protected final String lore;
    protected final Map<UUID, Integer> damageMap = new HashMap<>();
    protected int gameID;
    protected int wave;
    protected String name;
    protected int hpBarSize;
    protected boolean hostile;
    
    protected final int level;
    protected int maxHealth;
    protected int currentHealth;
    protected int armor;
    protected double toughness;
    protected int damage;
    protected double damageSpread;
    protected PotionEffectType effectType;
    protected int effectLevel;
    protected int effectDuration;
    protected int pierce;
    protected final AttackType attackType;
    protected double attackSpeed;
    protected TargetPriority targetPriority = TargetPriority.NONE;
    protected int targetRange;
    protected int loot;
    protected double lootSpread;
    protected long lastStrike = 0;
    
    public static final String VD = "VD";
    private static final String KNOCKBACK = "knockback";
    private static final String WEIGHT = "weight";
    private static final String SPEED = "speed";
    
    protected VDMob(String lore, int level, AttackType attackType) {
        this.lore = lore;
        this.level = level;
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

    public void takeDamage(int damage, @NotNull AttackType attackType, @Nullable Player attacker, Arena arena) {
        // Final damage calculation and display
        if (attackType == AttackType.NORMAL)
            damage -= Math.min(damage, armor);
        else if (attackType == AttackType.PENETRATING)
            damage *= Math.max(0, 1 - toughness);
        else if (attackType == AttackType.NONE)
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
            int finalGems = (int) (loot * 2 * (1 + (r.nextDouble() * 2 - 1) * lootSpread));
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
    }
    private void addDamage(int damage, UUID id) {
        currentHealth -= damage;
        if (id == null)
            return;
        if (damageMap.containsKey(id))
            damageMap.replace(id, damageMap.get(id) + damage);
        else damageMap.put(id, damage);
    }
    
    public int dealRawDamage() {
        Random r = new Random();
        return (int) (this.damage * (1 + (r.nextDouble() * 2 - 1) * damageSpread));
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
        return effectType == null ? null : new PotionEffect(effectType, effectDuration, effectLevel - 1);
    }

    public int getPierce() {
        return pierce;
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

    public TargetPriority getTargetPriority() {
        return targetPriority;
    }

    public int getTargetRange() {
        return targetRange;
    }

    // Function for Gaussian level distribution, with restrictions
    protected static int getLevel(double difficulty, double rate, int start) {
        Random r = new Random();
        double mult = 1 + .1 * Math.max(Math.min(r.nextGaussian(), 3), -3); // Mean 100%, SD 10%, restrict 30%
        return Math.max((int) ((difficulty * mult - start) / rate + .5), 1);
    }

    // Sets the proper health for the mob
    protected void setHealth(int base, int delta) {
        maxHealth = base + delta * (level - 1);
        currentHealth = maxHealth;
    }

    // Sets the proper armor for the mob
    protected void setArmor(int base, int delta) {
        armor = base + delta * (level - 1);
    }

    // Sets the proper toughness for the mob
    protected void setToughness(double base, double delta, int start) {
        toughness = base + delta * Math.max(0, level - start + 1);
    }

    // Sets the proper damage for the mob
    protected void setDamage(int base, int delta, double spread) {
        damage = base + delta * (level - 1);
        damageSpread = spread;
    }

    // Sets the proper effect type, if there is one
    protected void setEffectType(PotionEffectType effectType) {
        this.effectType = effectType;
    }

    // Sets the proper effect level, if there is one
    protected void setEffectLevel(boolean levelChange) {
        if (levelChange && level >= 10)
            effectLevel = 2;
        else effectLevel = 1;
    }

    // Sets the proper effect duration, if there is one
    protected void setEffectDuration(int base, int delta, boolean levelChange) {
        effectDuration = Utils.secondsToTicks(base + delta * (level - 1));
        if (levelChange && level >= 10)
            effectDuration /= 2;
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
        targetRange = 10;
    }
    protected void setModerateTargetRange() {
        targetRange = 18;
    }
    protected void setFarTargetRange() {
        targetRange = 30;
    }
    protected void setUnboundedTargetRange() {
        targetRange = -1;
    }

    // Sets the proper loot for the mob
    protected void setLoot(int base, double rate, double spread) {
        loot = (int) (base * Math.pow(rate, level - 1));
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

    public void remove() {
        if (Main.getVillagersTeam().hasEntry(id.toString()))
            Main.getVillagersTeam().removeEntry(id.toString());
        if (Main.getMonstersTeam().hasEntry(id.toString()))
            Main.getMonstersTeam().removeEntry(id.toString());
    }

    public static VDMob of(String key, Arena arena, Location ground, Location air) throws InvalidVDMobKeyException {
        switch (key) {
            case VDCleric.KEY:
                return new VDCleric(arena, ground);
            case VDWeaponsmith.KEY:
                return new VDWeaponsmith(arena, ground);
            case VDArmorer.KEY:
                return new VDArmorer(arena, ground);
            case VDFarmer.KEY:
                return new VDFarmer(arena, ground);
            case VDVaultKeeper.KEY:
                return new VDVaultKeeper(arena, ground);
            case VDFletcher.KEY:
                return new VDFletcher(arena, ground);
            case VDMayor.KEY:
                return new VDMayor(arena, ground);
            case VDZombie.KEY:
                return new VDZombie(arena, ground);
            case VDBabyZombie.KEY:
                return new VDBabyZombie(arena, ground);
            case VDHusk.KEY:
                return new VDHusk(arena, ground);
            case VDBabyHusk.KEY:
                return new VDBabyHusk(arena, ground);
            case VDWitherSkeleton.KEY:
                return new VDWitherSkeleton(arena, ground);
            case VDPiglinSoldier.KEY:
                return new VDPiglinSoldier(arena, ground);
            case VDPiglinSniper.KEY:
                return new VDPiglinSniper(arena, ground);
            case VDBrute.KEY:
                return new VDBrute(arena, ground);
            case VDVindicator.KEY:
                return new VDVindicator(arena, ground);
            case VDSkeleton.KEY:
                return new VDSkeleton(arena, ground);
            case VDStray.KEY:
                return new VDStray(arena, ground);
            case VDPillager.KEY:
                return new VDPillager(arena, ground);
            case VDDrowned.KEY:
                return new VDDrowned(arena, ground);
            case VDBabyDrowned.KEY:
                return new VDBabyDrowned(arena, ground);
            case VDPhantom.KEY:
                return new VDPhantom(arena, air);
            case VDBlaze.KEY:
                return new VDBlaze(arena, air);
            case VDGhast.KEY:
                return new VDGhast(arena, air);
            case VDCreeper.KEY:
                return new VDCreeper(arena, ground);
            case VDChargedCreeper.KEY:
                return new VDChargedCreeper(arena, ground);
            case VDSpider.KEY:
                return new VDSpider(arena, ground);
            case VDCaveSpider.KEY:
                return new VDCaveSpider(arena, ground);
            case VDSilverfish.KEY:
                return new VDSilverfish(arena, ground);
            case VDVex.KEY:
                return new VDVex(arena, air);
            default:
                throw new InvalidVDMobKeyException();
        }
    }
}
