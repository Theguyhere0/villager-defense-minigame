package me.theguyhere.villagerdefense.plugin.entities;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * A template for any entity that can deal attacks.
 */
public interface Attacker {
	/**
	 * Tags for the type of attack, determining how resistances apply.
	 */
	@Getter
	enum AttackType {
		NORMAL("normal"),
		CRUSHING("crushing"),
		SLASHING("slashing"),
		PENETRATING("penetrating"),
		NONE("none"),
		DIRECT("direct");

		@NotNull
		private final String value;

		AttackType(@NotNull String value) {
			this.value = value;
		}
	}
}