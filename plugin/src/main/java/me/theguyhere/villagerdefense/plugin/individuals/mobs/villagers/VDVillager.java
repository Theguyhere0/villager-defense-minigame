package me.theguyhere.villagerdefense.plugin.individuals.mobs.villagers;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class VDVillager extends VDMob {
    protected VDVillager(Arena arena, Villager villager, String name, String lore) {
        super(lore, null);
        mob = villager;
        id = villager.getUniqueId();
        villager.setAdult();
        villager.setMetadata(TEAM, IndividualTeam.VILLAGER.getValue());
        villager.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        hpBarSize = 2;
        villager.setRemoveWhenFarAway(false);
        villager.setHealth(2);
        villager.setCustomNameVisible(true);
        villager.setVillagerExperience(1);
        villager.setVillagerType(Villager.Type.valueOf(arena.getVillagerType().toUpperCase()));
    }

    @Override
    protected void updateNameTag() {
        super.updateNameTag(ChatColor.DARK_GREEN);
    }
}
