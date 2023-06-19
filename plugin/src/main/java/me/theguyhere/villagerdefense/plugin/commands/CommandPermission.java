package me.theguyhere.villagerdefense.plugin.commands;

enum CommandPermission {
	/**
	 * CommandPermission to edit arenas.
	 */
	USE("vd.use"),
	/**
	 * CommandPermission to modify crystal balance.
	 */
	CRYSTAL("vd.crystal"),
	/**
	 * CommandPermission to start arenas.
	 */
	START("vd.start"),
	/**
	 * Overall admin permission.
	 */
	ADMIN("vd.admin");

	private final String permission;

	CommandPermission(String permission) {
		this.permission = permission;
	}

	String getPermission() {
		return permission;
	}
}
