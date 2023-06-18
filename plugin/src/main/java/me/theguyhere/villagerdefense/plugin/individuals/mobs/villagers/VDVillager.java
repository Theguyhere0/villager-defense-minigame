package me.theguyhere.villagerdefense.plugin.individuals.mobs.villagers;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class VDVillager extends VDMob {
	protected VDVillager(Arena arena, Villager villager, String name, String lore) {
		super(lore, null);
		mob = villager;
		id = villager.getUniqueId();
		Main.getVillagersTeam().addEntry(id.toString());
		villager.setAdult();
		PersistentDataContainer dataContainer = villager.getPersistentDataContainer();
		dataContainer.set(ARENA_ID, PersistentDataType.INTEGER, arena.getId());
		dataContainer.set(TEAM, PersistentDataType.STRING, IndividualTeam.VILLAGER.getValue());
		wave = arena.getCurrentWave();
		this.name = name;
		hpBarSize = 2;
		villager.setRemoveWhenFarAway(false);
		villager.setHealth(2);
		villager.setCustomNameVisible(true);
		villager.setVillagerExperience(1);
		villager.setVillagerType(Villager.Type.valueOf(arena
			.getVillagerType()
			.toUpperCase()));
	}

	@Override
	protected void updateNameTag() {
		super.updateNameTag(ChatColor.DARK_GREEN);
	}
}
