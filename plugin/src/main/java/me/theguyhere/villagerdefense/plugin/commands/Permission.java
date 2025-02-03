package me.theguyhere.villagerdefense.plugin.commands;

enum Permission {
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

    Permission(String permission) {
        this.permission = permission;
    }

    String getPermission() {
        return permission;
    }
}
