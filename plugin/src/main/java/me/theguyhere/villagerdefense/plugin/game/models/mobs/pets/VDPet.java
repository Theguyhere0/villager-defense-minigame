package me.theguyhere.villagerdefense.plugin.game.models.mobs.pets;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.Team;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class VDPet extends VDMob {
    private final int slots;
    protected final Material buttonMat;
    protected final Player owner;

    protected VDPet(Arena arena, Tameable pet, String name, String lore, AttackType attackType, int slots,
                    Material buttonMat, Player owner) {
        super(lore, attackType);
        mob = pet;
        this.owner = owner;
        pet.setOwner(owner);
        id = pet.getUniqueId();
        pet.setMetadata(TEAM, Team.VILLAGER.getValue());
        pet.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        wave = arena.getCurrentWave();
        this.name = name;
        this.slots = slots;
        this.buttonMat = buttonMat;
        pet.setRemoveWhenFarAway(false);
        pet.setHealth(2);
        pet.setCustomNameVisible(true);
    }

    public int getLevel() {
        return level;
    }

    public int getSlots() {
        return slots;
    }

    public abstract void incrementLevel();

    public abstract VDPet respawn(Arena arena, Location location);

    public abstract ItemStack createButton();

    @Override
    protected void updateNameTag() {
        super.updateNameTag(ChatColor.GREEN);
    }
}
