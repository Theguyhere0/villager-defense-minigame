package me.theguyhere.villagerdefense.plugin.commands;

enum Permission {
    /** Permission to edit arenas.*/
    USE("vd.use"),
    /** Permission to modify crystal balance.*/
    CRYSTAL("vd.crystal"),
    /** Permission to start arenas.*/
    START("vd.start"),
    /** Overall admin permission.*/
    ADMIN("vd.admin")
    ;

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    String getPermission() {
        return permission;
    }
}
