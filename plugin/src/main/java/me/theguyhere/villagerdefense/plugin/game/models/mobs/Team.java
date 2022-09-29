package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.Main;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public enum Team {
    VILLAGER("villager"),
    MONSTER("monster")
    ;

    private final MetadataValue value;

    Team(String s) {
        value = new FixedMetadataValue(Main.plugin, s);
    }

    public MetadataValue getValue() {
        return value;
    }
}
