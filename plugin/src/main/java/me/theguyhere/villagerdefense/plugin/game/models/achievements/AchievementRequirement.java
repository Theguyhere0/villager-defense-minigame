package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import me.theguyhere.villagerdefense.plugin.exceptions.InvalidAchievementReqValException;

import java.util.ArrayList;
import java.util.List;

public class AchievementRequirement {
    private final AchievementMetric metric;
    private final List<String> validFields = new ArrayList<>();

    private int integer;
    private String string;
    private boolean and = true;

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

    public AchievementRequirement(AchievementMetric metric, int integer, String string, boolean and) {
        this.metric = metric;
        validFields.add("integer");
        validFields.add("string");
        this.integer = integer;
        this.string = string;
        this.and = and;
    }

    public AchievementMetric getMetric() {
        return metric;
    }

    public List<String> getValidFields() {
        return validFields;
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

    public boolean isAnd() {
        return and;
    }
}
