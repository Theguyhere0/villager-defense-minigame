package me.theguyhere.villagerdefense.plugin.achievements;

import java.util.ArrayList;
import java.util.List;

class AchievementRequirement {
	private final AchievementMetric metric;
	private final List<FieldType> validFields = new ArrayList<>();

	private int integer;
	private String string;

	AchievementRequirement(AchievementMetric metric, int integer) {
		this.metric = metric;
		validFields.add(FieldType.INTEGER);
		this.integer = integer;
	}

	AchievementRequirement(AchievementMetric metric, String string) {
		this.metric = metric;
		validFields.add(FieldType.STRING);
		this.string = string;
	}

	AchievementRequirement(AchievementMetric metric, int integer, String string) {
		this.metric = metric;
		validFields.add(FieldType.INTEGER);
		validFields.add(FieldType.STRING);
		this.integer = integer;
		this.string = string;
	}

	AchievementMetric getMetric() {
		return metric;
	}

	int getInteger() throws InvalidAchievementReqValException {
		if (validFields.contains(FieldType.INTEGER))
			return integer;
		else throw new InvalidAchievementReqValException();
	}

	String getString() throws InvalidAchievementReqValException {
		if (validFields.contains(FieldType.STRING))
			return string;
		else throw new InvalidAchievementReqValException();
	}

	private enum FieldType {
		STRING,
		INTEGER
	}
}
