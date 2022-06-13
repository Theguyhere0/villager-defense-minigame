package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;

public class AchievementReward {
    private final RewardType type;
    private final int value;
    private final String description;

    public AchievementReward(RewardType type, int value) {
        this.type = type;
        this.value = value;
        description = LanguageManager.rewards.crystals;
    }

    public AchievementReward(RewardType type, BoostRewardID id) {
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

    public RewardType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
