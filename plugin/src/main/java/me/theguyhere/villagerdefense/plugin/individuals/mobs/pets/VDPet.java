package me.theguyhere.villagerdefense.plugin.individuals.mobs.pets;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public abstract class VDPet extends VDMob {
    private final int slots;
    protected final Material buttonMat;
    protected final VDPlayer owner;

    protected VDPet(Arena arena, Tameable pet, String name, String lore, IndividualAttackType attackType, int slots,
                    Material buttonMat, VDPlayer owner) {
        super(lore, attackType);
        mob = pet;
        this.owner = owner;
        pet.setOwner(owner.getPlayer());
        id = pet.getUniqueId();
        PersistentDataContainer dataContainer = pet.getPersistentDataContainer();
        dataContainer.set(ARENA_ID, PersistentDataType.INTEGER, arena.getId());
        dataContainer.set(TEAM, PersistentDataType.STRING, IndividualTeam.VILLAGER.getValue());
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

    public String getName() {
        return mob.getCustomName();
    }

    public abstract void incrementLevel();

    public abstract VDPet respawn(Arena arena, Location location);

    public abstract ItemStack createDisplayButton();

    public abstract ItemStack createUpgradeButton();

    public VDPlayer getOwner() {
        return owner;
    }

    public void heal() {
        // Check if still alive
        if (mob.isDead())
            return;

        // Natural heal
        if (!owner.getChallenges().contains(Challenge.uhc())) {
            int hunger = owner.getPlayer().getFoodLevel();
            if (hunger >= 20)
                changeCurrentHealth(6);
            else if (hunger >= 16)
                changeCurrentHealth(5);
            else if (hunger >= 10)
                changeCurrentHealth(3);
            else if (hunger >= 4)
                changeCurrentHealth(2);
            else if (hunger > 0)
                changeCurrentHealth(1);
        }

        // Regeneration
        mob.getActivePotionEffects().forEach(potionEffect -> {
            if (PotionEffectType.REGENERATION.equals(potionEffect.getType()))
                changeCurrentHealth(5 * (1 + potionEffect.getAmplifier()));
        });

        updateNameTag();
    }

    public void heal(int health) {
        // Check if still alive
        if (mob.isDead())
            return;

        // Heal and update
        changeCurrentHealth(health);
        updateNameTag();
    }

    public void kill() {
        // Check if still alive
        if (mob.isDead())
            return;

        // Kill
        takeDamage(currentHealth, IndividualAttackType.DIRECT, null, owner.getArena());
        updateNameTag();
    }

    @Override
    protected void updateNameTag() {
        super.updateNameTag(ChatColor.GREEN);
    }
}
