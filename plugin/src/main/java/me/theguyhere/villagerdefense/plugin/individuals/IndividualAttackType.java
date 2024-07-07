package me.theguyhere.villagerdefense.plugin.individuals;

public enum IndividualAttackType {
	NORMAL("normal"),
	CRUSHING("crushing"),
	SLASHING("slashing"),
	PENETRATING("penetrating"),
	NONE("none"),
	DIRECT("direct");

	private final String string;

	IndividualAttackType(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}
}
