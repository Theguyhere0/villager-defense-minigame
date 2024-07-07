package me.theguyhere.villagerdefense.plugin.entities;

import org.jetbrains.annotations.NotNull;

public enum IndividualTeam {
	VILLAGER("villager"),
	MONSTER("monster");

	private final String value;

	IndividualTeam(@NotNull String value) {
		this.value = value;
	}

	public @NotNull String getValue() {
		return value;
	}
}
