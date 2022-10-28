package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;

public class AchievementReward {
    private final RewardType type;
    private final int value;
    private final String description;

    public AchievementReward(RewardType type, int value) {
        this.type = type;
        this.value = value;
        description = String.format(LanguageManager.rewards.crystals,
                Integer.toString(value == 0 ? 0 : (Main.hasCustomEconomy() ? (int) (value *
                        Main.plugin.getConfig().getDouble("vaultEconomyMult")) : value)),
                LanguageManager.names.crystals);
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
