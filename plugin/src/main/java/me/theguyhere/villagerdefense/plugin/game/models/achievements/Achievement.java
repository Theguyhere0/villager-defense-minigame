package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidAchievementReqTypeException;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Achievement {
    /** The name of the achievement.*/
    private final String name;
    /** The main description for the achievement.*/
    private final String description;
    /** The ID of the achievement.*/
    private final String ID;
    /** The material used for GUI buttons relating to this achievement.*/
    private final Material buttonMaterial;
    /** The type of achievement this will be.*/
    private final AchievementType type;
    /** The requirements to get this achievement.*/
    private final List<AchievementRequirement> requirements = new ArrayList<>();
    /** The reward for getting this achievement.*/
    private final AchievementReward reward;
    /** Whether the requirements in this achievement have to all be true or only one.*/
    private final boolean and;

    public Achievement(
            String name,
            String description,
            String ID,
            Material buttonMaterial,
            AchievementType type,
            AchievementReward reward,
            boolean and
    ) {
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.buttonMaterial = buttonMaterial;
        this.type = type;
        this.reward = reward;
        this.and = and;
    }

    public Achievement(
            String name,
            String description,
            String ID,
            Material buttonMaterial,
            AchievementType type,
            AchievementReward reward
            ) {
        this(name, description, ID, buttonMaterial, type, reward, true);
    }

    public String getName() {
        return name;
    }

    private String getName(boolean obtained) {
        if (obtained)
            return CommunicationManager.format("&6&l" + name);
        else return CommunicationManager.format("&7&l" + name);
    }

    private String[] getDescription(boolean obtained) {
        List<String> descriptions = CommunicationManager.formatDescriptionList(ChatColor.GRAY, description,
                Utils.LORE_CHAR_LIMIT);
        descriptions.add("");
        descriptions.addAll(CommunicationManager.formatDescriptionList(obtained ? ChatColor.AQUA : ChatColor.GREEN,
                reward.getType() == RewardType.BOOST ? reward.getDescription() :
                        String.format(reward.getDescription(), Integer.toString(reward.getValue())),
                Utils.LORE_CHAR_LIMIT));

        return descriptions.toArray(new String[]{});
    }

    public String getID() {
        return ID;
    }

    private Material getButtonMaterial(boolean obtained) {
        if (obtained)
            return buttonMaterial;
        else return Material.GUNPOWDER;
    }

    public AchievementType getType() {
        return type;
    }

    @NotNull
    public ItemStack getButton(boolean obtained) {
        return ItemManager.createItem(
                getButtonMaterial(obtained),
                getName(obtained),
                ItemManager.BUTTON_FLAGS,
                null,
                getDescription(obtained)
        );
    }

    public List<AchievementRequirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(AchievementRequirement requirement) throws InvalidAchievementReqTypeException {
        if (requirement.getMetric().getType() != type)
            throw new InvalidAchievementReqTypeException();
        else requirements.add(requirement);
    }

    public AchievementReward getReward() {
        return reward;
    }

    public boolean isAnd() {
        return and;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return and == that.and && Objects.equals(name, that.name) && Objects.equals(description, that.description) &&
                Objects.equals(ID, that.ID) && buttonMaterial == that.buttonMaterial && type == that.type &&
                Objects.equals(requirements, that.requirements) && Objects.equals(reward, that.reward);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, ID, buttonMaterial, type, requirements, reward, and);
    }

    public static @NotNull Achievement allAbility() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allAbility.name,
                LanguageManager.achievements.allAbility.description,
                "allAbility",
                Material.CHORUS_FRUIT,
                AchievementType.KIT,
                new AchievementReward(RewardType.CRYSTAL, 3000)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.mage().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.knight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.messenger().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.monk().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.ninja().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.priest().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.templar().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.siren().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.warrior().getID()
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement allChallenges() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allChallenges.name,
                LanguageManager.achievements.allChallenges.description,
                "allChallenges",
                Material.HEART_OF_THE_SEA,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.BOOST, BoostRewardID.RESURRECTION)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.amputee().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.blind().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.clumsy().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.dwarf().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.explosive().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.featherweight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.pacifist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.naked().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.uhc().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 3));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement allEffect() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allEffect.name,
                LanguageManager.achievements.allEffect.description,
                "allEffect",
                Material.BREWING_STAND,
                AchievementType.KIT,
                new AchievementReward(RewardType.BOOST, BoostRewardID.SHARE_EFFECT)

        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.blacksmith().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.giant().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.merchant().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.witch().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.vampire().getID()
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement allGift() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allGift.name,
                LanguageManager.achievements.allGift.description,
                "allGift",
                Material.CHEST_MINECART,
                AchievementType.KIT,
                new AchievementReward(RewardType.CRYSTAL, 1000)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.soldier().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.alchemist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.trader().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.tailor().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.reaper().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.summoner().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.phantom().getID()
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement allKits() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allKits.name,
                LanguageManager.achievements.allKits.description,
                "allKits",
                Material.ENDER_CHEST,
                AchievementType.KIT,
                new AchievementReward(RewardType.BOOST, BoostRewardID.AMMO_DOUBLE)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.soldier().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.alchemist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.trader().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.tailor().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.reaper().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.summoner().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.phantom().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.mage().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.knight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.messenger().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.monk().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.ninja().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.priest().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.templar().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.siren().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.warrior().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.blacksmith().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    2,
                    Kit.giant().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.merchant().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.witch().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    Kit.vampire().getID()
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement allMaxedAbility() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allMaxedAbility.name,
                LanguageManager.achievements.allMaxedAbility.description,
                "allMaxedAbility",
                Material.CHORUS_FLOWER,
                AchievementType.KIT,
                new AchievementReward(RewardType.BOOST, BoostRewardID.COOLDOWN_REDUCTION)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.mage().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.knight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.messenger().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.monk().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.ninja().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.priest().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.templar().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.siren().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.warrior().getID()
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement alone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.alone.name,
                LanguageManager.achievements.alone.description,
                "alone",
                Material.ENDER_PEARL,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 250)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement amputeeAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.amputee.name
                ),
                "amputeeAlone",
                Material.BAMBOO,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.amputee().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement amputeeBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.amputee.name
                ),
                "amputeeBalance",
                Material.BAMBOO,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.amputee().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement amputeeKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.amputee.name
                ),
                "amputeeKills",
                Material.BAMBOO,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.amputee().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement amputeeWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.amputee.name
                ),
                "amputeeWave",
                Material.BAMBOO,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.amputee().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement blindAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.blind.name
                ),
                "blindAlone",
                Material.INK_SAC,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.blind().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement blindBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.blind.name
                ),
                "blindBalance",
                Material.INK_SAC,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.blind().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement blindKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.blind.name
                ),
                "blindKills",
                Material.INK_SAC,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.blind().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement blindWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.blind.name
                ),
                "blindWave",
                Material.INK_SAC,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.blind().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement clumsyAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.clumsy.name
                ),
                "clumsyAlone",
                Material.ICE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.clumsy().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement clumsyBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.clumsy.name
                ),
                "clumsyBalance",
                Material.ICE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.clumsy().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement clumsyKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.clumsy.name
                ),
                "clumsyKills",
                Material.ICE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.clumsy().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement clumsyWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.clumsy.name
                ),
                "clumsyWave",
                Material.ICE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.clumsy().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement dwarfAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.dwarf.name
                ),
                "dwarfAlone",
                Material.DEAD_BUSH,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.dwarf().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement dwarfBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.dwarf.name
                ),
                "dwarfBalance",
                Material.DEAD_BUSH,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.dwarf().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement dwarfKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.dwarf.name
                ),
                "dwarfKills",
                Material.DEAD_BUSH,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.dwarf().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement dwarfWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.dwarf.name
                ),
                "dwarfWave",
                Material.DEAD_BUSH,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.dwarf().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement explosiveAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.explosive.name
                ),
                "explosiveAlone",
                Material.TNT,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.explosive().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement explosiveBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.explosive.name
                ),
                "explosiveBalance",
                Material.TNT,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.explosive().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement explosiveKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.explosive.name
                ),
                "explosiveKills",
                Material.TNT,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.explosive().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement explosiveWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.explosive.name
                ),
                "explosiveWave",
                Material.TNT,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.explosive().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement featherweightAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.featherweight.name
                ),
                "featherweightAlone",
                Material.FEATHER,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.featherweight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement featherweightBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.featherweight.name
                ),
                "featherweightBalance",
                Material.FEATHER,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.featherweight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement featherweightKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.featherweight.name
                ),
                "featherweightKills",
                Material.FEATHER,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.featherweight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement featherweightWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.featherweight.name
                ),
                "featherweightWave",
                Material.FEATHER,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.featherweight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement maxedAbility() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.maxedAbility.name,
                LanguageManager.achievements.maxedAbility.description,
                "maxedAbility",
                Material.POPPED_CHORUS_FRUIT,
                AchievementType.KIT,
                new AchievementReward(RewardType.CRYSTAL, 2000),
                false
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.mage().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.knight().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.messenger().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.monk().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.ninja().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.priest().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.templar().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.siren().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    Kit.warrior().getID()
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement nakedAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.naked.name
                ),
                "nakedAlone",
                Material.ARMOR_STAND,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.naked().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement nakedBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.naked.name
                ),
                "nakedBalance",
                Material.ARMOR_STAND,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.naked().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement nakedKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.naked.name
                ),
                "nakedKills",
                Material.ARMOR_STAND,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.naked().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement nakedWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.naked.name
                ),
                "nakedWave",
                Material.ARMOR_STAND,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.naked().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement pacifistAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.pacifist.name
                ),
                "pacifistAlone",
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.pacifist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement pacifistBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.pacifist.name
                ),
                "pacifistBalance",
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.pacifist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement pacifistKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.pacifist.name
                ),
                "pacifistKills",
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.pacifist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement pacifistWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.pacifist.name
                ),
                "pacifistWave",
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.pacifist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement pacifistUhc() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifistUhc.name,
                String.format(
                        LanguageManager.achievements.pacifistUhc.description,
                        LanguageManager.challenges.pacifist.name + " & " + LanguageManager.challenges.uhc.name
                ),
                "pacifistUhc",
                Material.DAMAGED_ANVIL,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 200)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.pacifist().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.uhc().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 10));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement topBalance1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.one,
                String.format(LanguageManager.achievements.topBalance.description, "100"),
                "topBalance1",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 5)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 100));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.two,
                String.format(LanguageManager.achievements.topBalance.description, "250"),
                "topBalance2",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 10)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 250));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.three,
                String.format(LanguageManager.achievements.topBalance.description, "500"),
                "topBalance3",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 20)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.four,
                String.format(LanguageManager.achievements.topBalance.description, "1000"),
                "topBalance4",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.five,
                String.format(LanguageManager.achievements.topBalance.description, "2500"),
                "topBalance5",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 80)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 2500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.six,
                String.format(LanguageManager.achievements.topBalance.description, "5000"),
                "topBalance6",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 160)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 5000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.seven,
                String.format(LanguageManager.achievements.topBalance.description, "10000"),
                "topBalance7",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 320)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 10000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.eight,
                String.format(LanguageManager.achievements.topBalance.description, "25000"),
                "topBalance8",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 640)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 25000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topBalance9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.nine,
                String.format(LanguageManager.achievements.topBalance.description, "50000"),
                "topBalance9",
                Material.EMERALD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.BOOST, BoostRewardID.GEM_INCREASE)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 50000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement topKills1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.one,
                String.format(LanguageManager.achievements.topKills.description, "10"),
                "topKills1",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 5)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 10));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.two,
                String.format(LanguageManager.achievements.topKills.description, "20"),
                "topKills2",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 10)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.three,
                String.format(LanguageManager.achievements.topKills.description, "30"),
                "topKills3",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 20)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 30));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.four,
                String.format(LanguageManager.achievements.topKills.description, "50"),
                "topKills4",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 50));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.five,
                String.format(LanguageManager.achievements.topKills.description, "100"),
                "topKills5",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 80)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 100));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.six,
                String.format(LanguageManager.achievements.topKills.description, "200"),
                "topKills6",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 160)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 200));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.seven,
                String.format(LanguageManager.achievements.topKills.description, "300"),
                "topKills7",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 320)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 300));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.eight,
                String.format(LanguageManager.achievements.topKills.description, "500"),
                "topKills8",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 640)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topKills9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.nine,
                String.format(LanguageManager.achievements.topKills.description, "1000"),
                "topKills9",
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.BOOST, BoostRewardID.DAMAGE_INCREASE)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement topWave1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.one,
                String.format(LanguageManager.achievements.topWave.description, "5"),
                "topWave1",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 5)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 5));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.two,
                String.format(LanguageManager.achievements.topWave.description, "10"),
                "topWave2",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 10)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 10));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.three,
                String.format(LanguageManager.achievements.topWave.description, "15"),
                "topWave3",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 20)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 15));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.four,
                String.format(LanguageManager.achievements.topWave.description, "20"),
                "topWave4",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.five,
                String.format(LanguageManager.achievements.topWave.description, "25"),
                "topWave5",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 80)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 25));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.six,
                String.format(LanguageManager.achievements.topWave.description, "30"),
                "topWave6",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 160)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 30));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.seven,
                String.format(LanguageManager.achievements.topWave.description, "35"),
                "topWave7",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 320)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 35));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.eight,
                String.format(LanguageManager.achievements.topWave.description, "40"),
                "topWave8",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 640)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 40));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement topWave9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.nine,
                String.format(LanguageManager.achievements.topWave.description, "50"),
                "topWave9",
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.BOOST, BoostRewardID.HEALTH_INCREASE)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 50));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement totalGems1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.one,
                String.format(LanguageManager.achievements.totalGems.description, "1000"),
                "totalGems1",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 5)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.two,
                String.format(LanguageManager.achievements.totalGems.description, "5000"),
                "totalGems2",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 10)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 5000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.three,
                String.format(LanguageManager.achievements.totalGems.description, "10000"),
                "totalGems3",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 20)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 10000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.four,
                String.format(LanguageManager.achievements.totalGems.description, "25000"),
                "totalGems4",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 25000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.five,
                String.format(LanguageManager.achievements.totalGems.description, "50000"),
                "totalGems5",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 80)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 50000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.six,
                String.format(LanguageManager.achievements.totalGems.description, "100000"),
                "totalGems6",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 160)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 100000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.seven,
                String.format(LanguageManager.achievements.totalGems.description, "250000"),
                "totalGems7",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 320)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 250000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.eight,
                String.format(LanguageManager.achievements.totalGems.description, "500000"),
                "totalGems8",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 640)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 500000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalGems9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.nine,
                String.format(LanguageManager.achievements.totalGems.description, "1000000"),
                "totalGems9",
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.BOOST, BoostRewardID.CRYSTAL_CONVERT)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 1000000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement totalKills1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.one,
                String.format(LanguageManager.achievements.totalKills.description, "100"),
                "totalKills1",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 5)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 100));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.two,
                String.format(LanguageManager.achievements.totalKills.description, "200"),
                "totalKills2",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 10)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 200));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.three,
                String.format(LanguageManager.achievements.totalKills.description, "300"),
                "totalKills3",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 20)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 300));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.four,
                String.format(LanguageManager.achievements.totalKills.description, "500"),
                "totalKills4",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.five,
                String.format(LanguageManager.achievements.totalKills.description, "1000"),
                "totalKills5",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 80)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.six,
                String.format(LanguageManager.achievements.totalKills.description, "2000"),
                "totalKills6",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 160)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 2000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.seven,
                String.format(LanguageManager.achievements.totalKills.description, "3000"),
                "totalKills7",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 320)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 3000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.eight,
                String.format(LanguageManager.achievements.totalKills.description, "5000"),
                "totalKills8",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.CRYSTAL, 640)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 5000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement totalKills9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.nine,
                String.format(LanguageManager.achievements.totalKills.description, "10000"),
                "totalKills9",
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE,
                new AchievementReward(RewardType.BOOST, BoostRewardID.DAMAGE_REDUCTION)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 10000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static @NotNull Achievement uhcAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.uhc.name
                ),
                "uhcAlone",
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 40)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.uhc().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement uhcBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.uhc.name
                ),
                "uhcBalance",
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 100)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.uhc().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement uhcKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.uhc.name
                ),
                "uhcKills",
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 50)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.uhc().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static @NotNull Achievement uhcWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.uhc.name
                ),
                "uhcWave",
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE,
                new AchievementReward(RewardType.CRYSTAL, 75)
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    Challenge.uhc().getID()
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
}
