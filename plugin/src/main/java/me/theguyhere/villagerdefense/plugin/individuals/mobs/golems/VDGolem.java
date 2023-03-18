package me.theguyhere.villagerdefense.plugin.individuals.mobs.golems;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Golem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

public abstract class VDGolem extends VDMob {
    protected final Material buttonMat;

    protected VDGolem(Arena arena, Golem golem, String name, String lore, IndividualAttackType attackType, Material buttonMat) {
        super(lore, attackType);
        mob = golem;
        id = golem.getUniqueId();
        golem.setMetadata(TEAM, IndividualTeam.VILLAGER.getValue());
        golem.setMetadata(VD, new FixedMetadataValue(Main.plugin, arena.getId()));
        gameID = arena.getGameID();
        this.name = name;
        this.buttonMat = buttonMat;
        golem.setRemoveWhenFarAway(false);
        golem.setHealth(2);
        golem.setCustomNameVisible(true);
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return mob.getCustomName();
    }

    public abstract void incrementLevel();

    public abstract VDGolem respawn(Arena arena, Location location);

    public abstract ItemStack createDisplayButton();

    public abstract ItemStack createUpgradeButton();

    public void heal() {
        // Check if still alive
        if (mob.isDead())
            return;

        // Regeneration
        mob.getActivePotionEffects().forEach(potionEffect -> {
            if (PotionEffectType.REGENERATION.equals(potionEffect.getType()))
                changeCurrentHealth(5 * (1 + potionEffect.getAmplifier()));
        });

        updateNameTag();
    }

    public void kill() {
        // Check if still alive
        if (mob.isDead())
            return;

        // Kill
        mob.setHealth(0);
    }

    @Override
    protected void updateNameTag() {
        super.updateNameTag(ChatColor.GREEN);
    }
}
