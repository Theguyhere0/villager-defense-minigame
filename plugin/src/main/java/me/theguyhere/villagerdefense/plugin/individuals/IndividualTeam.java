package me.theguyhere.villagerdefense.plugin.individuals;

import me.theguyhere.villagerdefense.plugin.Main;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public enum IndividualTeam {
    VILLAGER("villager"),
    MONSTER("monster")
    ;

    private final MetadataValue value;

    IndividualTeam(String s) {
        value = new FixedMetadataValue(Main.plugin, s);
    }

    public MetadataValue getValue() {
        return value;
    }
}
