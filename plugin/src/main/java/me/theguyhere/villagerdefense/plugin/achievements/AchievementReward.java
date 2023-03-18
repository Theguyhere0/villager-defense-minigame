package me.theguyhere.villagerdefense.plugin.achievements;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;

class AchievementReward {
    private final Type type;
    private final int value;
    private final String description;

    AchievementReward(Type type, int value) {
        this.type = type;
        this.value = value;
        description = CommunicationManager.format(LanguageManager.rewards.crystals,
                Integer.toString(value == 0 ? 0 : (Main.hasCustomEconomy() ? (int) (value *
                        Main.plugin.getConfig().getDouble("vaultEconomyMult")) : value)),
                LanguageManager.names.crystals);
    }

    AchievementReward(Type type, Boost boost) {
        this.type = type;
        value = 0;
        switch (boost) {
            case DAMAGE_REDUCTION:
                description = LanguageManager.rewards.damageReduction;
                break;
            case DAMAGE_INCREASE:
                description = LanguageManager.rewards.damageIncrease;
                break;
            case HEALTH_INCREASE:
                description = LanguageManager.rewards.healthIncrease;
                break;
            case GEM_INCREASE:
                description = LanguageManager.rewards.gemIncrease;
                break;
            case CRYSTAL_CONVERT:
                description = String.format(LanguageManager.rewards.crystalConvert, LanguageManager.names.crystals);
                break;
            case RESURRECTION:
                description = LanguageManager.rewards.resurrection;
                break;
            case COOLDOWN_REDUCTION:
                description = LanguageManager.rewards.cooldownReduction;
                break;
            case SHARE_EFFECT:
                description = LanguageManager.rewards.shareEffect;
                break;
            case AMMO_DOUBLE:
                description = LanguageManager.rewards.ammoDouble;
                break;
            default:
                description = "";
        }
    }

    Type getType() {
        return type;
    }

    int getValue() {
        return value;
    }

    String getDescription() {
        return description;
    }

    /**
     * Reward types in Villager Defense. Possible types:<ul>
     *     <li>{@link #CRYSTAL}</li>
     *     <li>{@link #BOOST}</li>
     * </ul>
     */
    enum Type {
        /** Rewards that give a certain number of crystals.*/
        CRYSTAL,
        /** Rewards that provide a permanent boost.*/
        BOOST,
    }

    /**
     * The different types of boosts available as a reward.
     */
    enum Boost {
        DAMAGE_REDUCTION,
        DAMAGE_INCREASE,
        HEALTH_INCREASE,
        GEM_INCREASE,
        CRYSTAL_CONVERT,
        RESURRECTION,
        COOLDOWN_REDUCTION,
        SHARE_EFFECT,
        AMMO_DOUBLE,
    }
}
