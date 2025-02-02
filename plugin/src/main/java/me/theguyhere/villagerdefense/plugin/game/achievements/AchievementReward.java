package me.theguyhere.villagerdefense.plugin.game.achievements;

import lombok.Getter;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;

@Getter
public class AchievementReward {
    private final Type type;
    private final int value;
    private final String description;

    public AchievementReward(Type type, int value) {
        this.type = type;
        this.value = value;
        description = LanguageManager.rewards.crystals;
    }

    public AchievementReward(Type type, BoostRewardID id) {
        this.type = type;
        value = id.getID();
        switch (id) {
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
                description = LanguageManager.rewards.crystalConvert;
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
            case TWO_KITS:
                description = LanguageManager.rewards.twoKits;
                break;
            default:
                description = "";
        }
    }

    /**
     * Reward types in Villager Defense. Possible types:<ul>
     *     <li>{@link #CRYSTAL}</li>
     *     <li>{@link #BOOST}</li>
     * </ul>
     */
    public enum Type {
        /** Rewards that give a certain number of crystals.*/
        CRYSTAL,
        /** Rewards that provide a permanent boost.*/
        BOOST,
    }
}
