package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import me.theguyhere.villagerdefense.plugin.exceptions.InvalidAchievementReqValException;

import java.util.ArrayList;
import java.util.List;

public class AchievementRequirement {
    private final AchievementMetric metric;
    private final List<FieldType> validFields = new ArrayList<>();

    private int integer;
    private String string;

    public AchievementRequirement(AchievementMetric metric, int integer) {
        this.metric = metric;
        validFields.add(FieldType.INTEGER);
        this.integer = integer;
    }

    public AchievementRequirement(AchievementMetric metric, String string) {
        this.metric = metric;
        validFields.add(FieldType.STRING);
        this.string = string;
    }

    public AchievementRequirement(AchievementMetric metric, int integer, String string) {
        this.metric = metric;
        validFields.add(FieldType.INTEGER);
        validFields.add(FieldType.STRING);
        this.integer = integer;
        this.string = string;
    }

    public AchievementMetric getMetric() {
        return metric;
    }

    public int getInteger() throws InvalidAchievementReqValException {
        if (validFields.contains(FieldType.INTEGER))
            return integer;
        else throw new InvalidAchievementReqValException();
    }

    public String getString() throws InvalidAchievementReqValException {
        if (validFields.contains(FieldType.STRING))
            return string;
        else throw new InvalidAchievementReqValException();
    }
    
    private enum FieldType {
        STRING,
        INTEGER
    }
}
