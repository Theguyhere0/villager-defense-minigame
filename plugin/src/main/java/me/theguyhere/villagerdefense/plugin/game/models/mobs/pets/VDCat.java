package me.theguyhere.villagerdefense.plugin.game.models.mobs.pets;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.items.eggs.PetEgg;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.inventories.Buttons;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VDCat extends VDPet {
    public VDCat(Arena arena, Location location, VDPlayer owner, int level) {
        super(
                arena,
                (Tameable) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.CAT),
                LanguageManager.mobs.cat,
                LanguageManager.mobLore.cat,
                AttackType.NONE,
                2,
                Material.SALMON,
                owner
        );
        ((Cat) mob).setAdult();
        hpBarSize = 2;
        this.level = level;
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        setVeryLightWeight();
        setFastSpeed();
        updateNameTag();
    }

    @Override
    public VDPet respawn(Arena arena, Location location) {
        return new VDCat(arena, location, owner, level);
    }

    @Override
    public ItemStack createDisplayButton() {
        return ItemManager.createItem(buttonMat, mob.getCustomName(), CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.messages.petButton, Utils.LORE_CHAR_LIMIT));
    }

    @Override
    public ItemStack createUpgradeButton() {
        switch (level) {
            case 1:
                return PetEgg.create(2, PetEgg.PetEggType.CAT);
            case 2:
                return PetEgg.create(3, PetEgg.PetEggType.CAT);
            case 3:
                return PetEgg.create(4, PetEgg.PetEggType.CAT);
            case 4:
                return PetEgg.create(5, PetEgg.PetEggType.CAT);
            default:
                return Buttons.noUpgrade();
        }
    }

    @Override
    public void incrementLevel() {
        level++;
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        updateNameTag();
    }

    /**
     * Returns the proper health for the mob.
     * @param level The mob's level.
     * @return The health for the mob.
     */
    public static int getHealth(int level) {
        switch (level) {
            case 1:
                return 160;
            case 2:
                return 200;
            case 3:
                return 240;
            case 4:
                return 275;
            case 5:
                return 300;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper armor for the mob.
     * @param level The mob's level.
     * @return The armor for the mob.
     */
    public static int getArmor(int level) {
        switch (level) {
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 4;
            case 5:
                return 7;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper toughness for the mob.
     * @param level The mob's level.
     * @return The toughness for the mob.
     */
    public static double getToughness(int level) {
        switch (level) {
            case 1:
                return .05;
            case 2:
                return .1;
            case 3:
                return .15;
            case 4:
                return .2;
            case 5:
                return .25;
            default:
                return 0;
        }
    }

    public static int getHeal(int level) {
        switch (level) {
            case 1:
                return 5;
            case 2:
                return 7;
            case 3:
                return 10;
            case 4:
                return 15;
            case 5:
                return 20;
            default:
                return 0;
        }
    }
}
