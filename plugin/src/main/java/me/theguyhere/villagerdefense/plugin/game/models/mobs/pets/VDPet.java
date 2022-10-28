package me.theguyhere.villagerdefense.plugin.game.models.mobs.pets;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.Team;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Mob;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class VDPet extends VDMob {
    protected VDPet(Arena arena, Mob pet, String name, String lore, int level, AttackType attackType) {
        super(lore, level, attackType);
        mob = pet;
        id = pet.getUniqueId();
        pet.setMetadata(TEAM, Team.VILLAGER.getValue());
        pet.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        hpBarSize = 2;
        pet.setRemoveWhenFarAway(false);
        pet.setHealth(2);
        pet.setCustomNameVisible(true);
    }

    @Override
    protected void updateNameTag() {
        super.updateNameTag(ChatColor.GREEN);
    }
}
