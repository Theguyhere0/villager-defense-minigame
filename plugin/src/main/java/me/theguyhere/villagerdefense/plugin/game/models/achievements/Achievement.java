package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidAchievementReqTypeException;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Achievement {
    /** The name of the achievement.*/
    private final String name;
    /** The main description for the achievement.*/
    private final String description;
    /** The material used for GUI buttons relating to this achievement.*/
    private final Material buttonMaterial;
    /** The type of achievement this will be.*/
    private final AchievementType type;
    /** The requirements to get this achievement.*/
    private final List<AchievementRequirement> requirements = new ArrayList<>();

    public Achievement(String name, String description, Material buttonMaterial, AchievementType type) {
        this.name = name;
        this.description = description;
        this.buttonMaterial = buttonMaterial;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    private String getName(boolean obtained) {
        if (obtained)
            return CommunicationManager.format("&6&l" + name);
        else return CommunicationManager.format("&7&l" + name);
    }

    private String[] getDescription() {
        return CommunicationManager.formatDescriptionList(ChatColor.GRAY, description).toArray(new String[]{});
    }

    private Material getButtonMaterial(boolean obtained) {
        if (obtained)
            return buttonMaterial;
        else return Material.GUNPOWDER;
    }

    public AchievementType getType() {
        return type;
    }

    public ItemStack getButton(boolean obtained) {
        return ItemManager.createItem(getButtonMaterial(obtained), getName(obtained), getDescription());
    }

    public List<AchievementRequirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(AchievementRequirement requirement) throws InvalidAchievementReqTypeException {
        if (requirement.getMetric().getType() != type)
            throw new InvalidAchievementReqTypeException();
        else requirements.add(requirement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && buttonMaterial == that.buttonMaterial && type == that.type && Objects.equals(requirements, that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, buttonMaterial, type, requirements);
    }

    public static Achievement allAbility() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allAbility.name,
                LanguageManager.achievements.allAbility.description,
                Material.CHORUS_FRUIT,
                AchievementType.KIT
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.mage.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.knight.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.messenger.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.monk.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.ninja.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.priest.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.templar.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.siren.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.warrior.name
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement allChallenges() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allChallenges.name,
                LanguageManager.achievements.allChallenges.description,
                Material.HEART_OF_THE_SEA,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.amputee.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.blind.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.clumsy.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.dwarf.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.explosive.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.featherweight.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.pacifist.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.naked.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.uhc.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 3));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement allEffect() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allEffect.name,
                LanguageManager.achievements.allEffect.description,
                Material.BREWING_STAND,
                AchievementType.KIT
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.blacksmith.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.giant.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.merchant.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.witch.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.vampire.name
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement allGift() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allGift.name,
                LanguageManager.achievements.allGift.description,
                Material.CHEST_MINECART,
                AchievementType.KIT
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.orc.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.farmer.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.soldier.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.alchemist.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.trader.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.tailor.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.reaper.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.summoner.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.phantom.name
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement allKits() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allKits.name,
                LanguageManager.achievements.allKits.description,
                Material.ENDER_CHEST,
                AchievementType.KIT
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.orc.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.farmer.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.soldier.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.alchemist.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.trader.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.tailor.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.reaper.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.summoner.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.phantom.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.mage.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.knight.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.messenger.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.monk.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.ninja.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.priest.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.templar.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.siren.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.warrior.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.blacksmith.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    2,
                    LanguageManager.kits.giant.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.merchant.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.witch.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    1,
                    LanguageManager.kits.vampire.name
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement allMaxedAbility() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.allMaxedAbility.name,
                LanguageManager.achievements.allMaxedAbility.description,
                Material.CHORUS_FLOWER,
                AchievementType.KIT
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.mage.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.knight.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.messenger.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.monk.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.ninja.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.priest.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.templar.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.siren.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.warrior.name
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement alone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.alone.name,
                LanguageManager.achievements.alone.description,
                Material.ENDER_PEARL,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement amputeeAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.amputee.name
                ),
                Material.BAMBOO,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.amputee.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement amputeeBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.amputee.name
                ),
                Material.BAMBOO,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.amputee.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement amputeeKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.amputee.name
                ),
                Material.BAMBOO,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.amputee.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement amputeeWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.amputee.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.amputee.name
                ),
                Material.BAMBOO,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.amputee.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement blindAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.blind.name
                ),
                Material.INK_SAC,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.blind.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement blindBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.blind.name
                ),
                Material.INK_SAC,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.blind.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement blindKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.blind.name
                ),
                Material.INK_SAC,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.blind.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement blindWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.blind.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.blind.name
                ),
                Material.INK_SAC,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.blind.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement clumsyAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.clumsy.name
                ),
                Material.ICE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.clumsy.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement clumsyBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.clumsy.name
                ),
                Material.ICE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.clumsy.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement clumsyKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.clumsy.name
                ),
                Material.ICE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.clumsy.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement clumsyWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.clumsy.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.clumsy.name
                ),
                Material.ICE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.clumsy.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement dwarfAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.dwarf.name
                ),
                Material.DEAD_BUSH,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.dwarf.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement dwarfBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.dwarf.name
                ),
                Material.DEAD_BUSH,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.dwarf.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement dwarfKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.dwarf.name
                ),
                Material.DEAD_BUSH,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.dwarf.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement dwarfWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.dwarf.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.dwarf.name
                ),
                Material.DEAD_BUSH,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.dwarf.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement explosiveAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.explosive.name
                ),
                Material.TNT,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.explosive.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement explosiveBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.explosive.name
                ),
                Material.TNT,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.explosive.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement explosiveKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.explosive.name
                ),
                Material.TNT,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.explosive.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement explosiveWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.explosive.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.explosive.name
                ),
                Material.TNT,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.explosive.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement featherweightAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.featherweight.name
                ),
                Material.FEATHER,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.featherweight.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement featherweightBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.featherweight.name
                ),
                Material.FEATHER,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.featherweight.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement featherweightKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.featherweight.name
                ),
                Material.FEATHER,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.featherweight.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement featherweightWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.featherweight.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.featherweight.name
                ),
                Material.FEATHER,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.featherweight.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement maxedAbility() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.maxedAbility.name,
                LanguageManager.achievements.maxedAbility.description,
                Material.POPPED_CHORUS_FRUIT,
                AchievementType.KIT
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.mage.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.knight.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.messenger.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.monk.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.ninja.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.priest.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.templar.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.siren.name,
                    false
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.KIT_OWN,
                    3,
                    LanguageManager.kits.warrior.name,
                    false
            ));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement nakedAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.naked.name
                ),
                Material.ARMOR_STAND,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.naked.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement nakedBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.naked.name
                ),
                Material.ARMOR_STAND,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.naked.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement nakedKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.naked.name
                ),
                Material.ARMOR_STAND,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.naked.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement nakedWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.naked.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.naked.name
                ),
                Material.ARMOR_STAND,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.naked.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement pacifistAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.pacifist.name
                ),
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.pacifist.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement pacifistBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.pacifist.name
                ),
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.pacifist.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement pacifistKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.pacifist.name
                ),
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.pacifist.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement pacifistWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifist.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.pacifist.name
                ),
                Material.TURTLE_HELMET,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.pacifist.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement pacifistUhc() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.pacifistUhc.name,
                String.format(
                        LanguageManager.achievements.pacifistUhc.description,
                        LanguageManager.challenges.pacifist.name + " & " + LanguageManager.challenges.uhc.name
                ),
                Material.DAMAGED_ANVIL,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.pacifist.name
            ));
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.uhc.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 10));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement topBalance1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.one,
                String.format(LanguageManager.achievements.topBalance.description, "100"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 100));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.two,
                String.format(LanguageManager.achievements.topBalance.description, "250"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 250));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.three,
                String.format(LanguageManager.achievements.topBalance.description, "500"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.four,
                String.format(LanguageManager.achievements.topBalance.description, "1000"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.five,
                String.format(LanguageManager.achievements.topBalance.description, "2500"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 2500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.six,
                String.format(LanguageManager.achievements.topBalance.description, "5000"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 5000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.seven,
                String.format(LanguageManager.achievements.topBalance.description, "10000"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 10000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.eight,
                String.format(LanguageManager.achievements.topBalance.description, "25000"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 25000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topBalance9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topBalance.nine,
                String.format(LanguageManager.achievements.topBalance.description, "50000"),
                Material.EMERALD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_BALANCE, 50000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement topKills1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.one,
                String.format(LanguageManager.achievements.topKills.description, "10"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 10));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.two,
                String.format(LanguageManager.achievements.topKills.description, "20"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.three,
                String.format(LanguageManager.achievements.topKills.description, "30"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 30));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.four,
                String.format(LanguageManager.achievements.topKills.description, "50"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 50));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.five,
                String.format(LanguageManager.achievements.topKills.description, "100"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 100));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.six,
                String.format(LanguageManager.achievements.topKills.description, "200"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 200));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.seven,
                String.format(LanguageManager.achievements.topKills.description, "300"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 300));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.eight,
                String.format(LanguageManager.achievements.topKills.description, "500"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topKills9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topKills.nine,
                String.format(LanguageManager.achievements.topKills.description, "1000"),
                Material.ZOMBIE_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_KILLS, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement topWave1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.one,
                String.format(LanguageManager.achievements.topWave.description, "5"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 5));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.two,
                String.format(LanguageManager.achievements.topWave.description, "10"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 10));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.three,
                String.format(LanguageManager.achievements.topWave.description, "15"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 15));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.four,
                String.format(LanguageManager.achievements.topWave.description, "20"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.five,
                String.format(LanguageManager.achievements.topWave.description, "25"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 25));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.six,
                String.format(LanguageManager.achievements.topWave.description, "30"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 30));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.seven,
                String.format(LanguageManager.achievements.topWave.description, "35"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 35));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.eight,
                String.format(LanguageManager.achievements.topWave.description, "40"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 40));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement topWave9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.topWave.nine,
                String.format(LanguageManager.achievements.topWave.description, "50"),
                Material.GOLDEN_SWORD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOP_WAVE, 50));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement totalGems1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.one,
                String.format(LanguageManager.achievements.totalGems.description, "1000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.two,
                String.format(LanguageManager.achievements.totalGems.description, "5000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 5000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.three,
                String.format(LanguageManager.achievements.totalGems.description, "10000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 10000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.four,
                String.format(LanguageManager.achievements.totalGems.description, "25000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 25000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.five,
                String.format(LanguageManager.achievements.totalGems.description, "50000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 50000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.six,
                String.format(LanguageManager.achievements.totalGems.description, "100000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 100000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.seven,
                String.format(LanguageManager.achievements.totalGems.description, "250000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 250000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.eight,
                String.format(LanguageManager.achievements.totalGems.description, "500000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 500000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalGems9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalGems.nine,
                String.format(LanguageManager.achievements.totalGems.description, "1000000"),
                Material.EMERALD_BLOCK,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_GEMS, 1000000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement totalKills1() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.one,
                String.format(LanguageManager.achievements.totalKills.description, "100"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 100));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills2() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.two,
                String.format(LanguageManager.achievements.totalKills.description, "200"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 200));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills3() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.three,
                String.format(LanguageManager.achievements.totalKills.description, "300"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 300));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills4() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.four,
                String.format(LanguageManager.achievements.totalKills.description, "500"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 500));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills5() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.five,
                String.format(LanguageManager.achievements.totalKills.description, "1000"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 1000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills6() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.six,
                String.format(LanguageManager.achievements.totalKills.description, "2000"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 2000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills7() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.seven,
                String.format(LanguageManager.achievements.totalKills.description, "3000"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 3000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills8() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.eight,
                String.format(LanguageManager.achievements.totalKills.description, "5000"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 5000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement totalKills9() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.totalKills.nine,
                String.format(LanguageManager.achievements.totalKills.description, "10000"),
                Material.DRAGON_HEAD,
                AchievementType.HIGH_SCORE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.TOTAL_KILLS, 10000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }

    public static Achievement uhcAlone() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.alone,
                String.format(
                        LanguageManager.achievements.challengeDescription.alone,
                        LanguageManager.challenges.uhc.name
                ),
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.uhc.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.ACTIVE_PLAYERS, 1));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 2));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement uhcBalance() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.balance,
                String.format(
                        LanguageManager.achievements.challengeDescription.balance,
                        LanguageManager.challenges.uhc.name
                ),
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.uhc.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.GEMS, 8000));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement uhcKills() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.kills,
                String.format(
                        LanguageManager.achievements.challengeDescription.kills,
                        LanguageManager.challenges.uhc.name
                ),
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.uhc.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.KILLS, 150));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
    public static Achievement uhcWave() {
        Achievement achievement = new Achievement(
                LanguageManager.achievements.uhc.wave,
                String.format(
                        LanguageManager.achievements.challengeDescription.wave,
                        LanguageManager.challenges.uhc.name
                ),
                Material.GOLDEN_APPLE,
                AchievementType.INSTANCE
        );

        try {
            achievement.addRequirement(new AchievementRequirement(
                    AchievementMetric.CHALLENGE,
                    LanguageManager.challenges.uhc.name
            ));
            achievement.addRequirement(new AchievementRequirement(AchievementMetric.WAVE, 20));
        } catch (InvalidAchievementReqTypeException e) {
            CommunicationManager.debugErrorShouldNotHappen();
        }

        return achievement;
    }
}
