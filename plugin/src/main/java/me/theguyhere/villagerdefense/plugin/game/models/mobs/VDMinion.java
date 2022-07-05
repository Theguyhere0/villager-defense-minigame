package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public abstract class VDMinion extends VDMob {
    protected LivingEntity minion;

    protected VDMinion(Arena arena, LivingEntity minion, String name, String lore, int level, AttackType attackType) {
        super(lore, level, attackType);
        this.minion = minion;
        id = minion.getUniqueId();
        hostile = true;
        Main.getMonstersTeam().addEntry(minion.getUniqueId().toString());
        minion.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        hpBarSize = 2;
        minion.setRemoveWhenFarAway(false);
        minion.setCanPickupItems(false);
        if (minion.isInsideVehicle())
            Objects.requireNonNull(minion.getVehicle()).remove();
        for (Entity passenger : minion.getPassengers())
            passenger.remove();
        minion.setHealth(1);
        minion.setCustomNameVisible(true);
    }

    @Override
    protected void updateNameTag() {
        super.updateNameTag(ChatColor.RED);
    }
}
