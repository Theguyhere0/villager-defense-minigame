package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class VDVillager extends VDMob {
    protected VDVillager(Arena arena, Villager villager, String name, String lore, int level) {
        super(lore, level, null);
        mob = villager;
        id = villager.getUniqueId();
        hostile = false;
        villager.setAdult();
        Main.getVillagersTeam().addEntry(villager.getUniqueId().toString());
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
