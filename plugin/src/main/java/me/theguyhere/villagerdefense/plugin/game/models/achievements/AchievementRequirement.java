package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import lombok.Getter;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidAchievementReqValException;

import java.util.ArrayList;
import java.util.List;

public class AchievementRequirement {
    @Getter
    private final AchievementMetric metric;
    private final List<String> validFields = new ArrayList<>();

    private int integer;
    private String string;

    public AchievementRequirement(AchievementMetric metric, int integer) {
        this.metric = metric;
        validFields.add("integer");
        this.integer = integer;
    }

    public AchievementRequirement(AchievementMetric metric, String string) {
        this.metric = metric;
        validFields.add("string");
        this.string = string;
    }

    public AchievementRequirement(AchievementMetric metric, int integer, String string) {
        this.metric = metric;
        validFields.add("integer");
        validFields.add("string");
        this.integer = integer;
        this.string = string;
    }

    public int getInteger() throws InvalidAchievementReqValException {
        if (validFields.contains("integer"))
            return integer;
        else throw new InvalidAchievementReqValException();
    }

    public String getString() throws InvalidAchievementReqValException {
        if (validFields.contains("string"))
            return string;
        else throw new InvalidAchievementReqValException();
    }
}
