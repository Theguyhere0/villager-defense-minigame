package me.theguyhere.villagerdefense.plugin.entities;

import org.jetbrains.annotations.NotNull;

public enum VDTeam {
	VILLAGER("villager"),
	MONSTER("monster");

	private final String value;

	VDTeam(@NotNull String value) {
		this.value = value;
	}

	public @NotNull String getValue() {
		return value;
	}
}
