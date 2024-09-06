package me.theguyhere.villagerdefense.plugin.entities.mobs.pets;

import lombok.Getter;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.entities.Attacker;
import me.theguyhere.villagerdefense.plugin.entities.VDEntity;
import me.theguyhere.villagerdefense.plugin.entities.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.entities.players.VDPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public abstract class VDPet extends VDMob {
	@Getter
	private final int slots;
	protected final Material buttonMat;
	@Getter
	protected final VDPlayer owner;

	protected VDPet(
		Arena arena, Tameable pet, String name, String lore, Attacker.AttackType attackType, int slots,
		Material buttonMat, VDPlayer owner
	) {
		super(arena, lore, attackType);
		mob = pet;
		this.owner = owner;
		pet.setOwner(owner.getPlayer());
		id = pet.getUniqueId();
		PersistentDataContainer dataContainer = pet.getPersistentDataContainer();
		dataContainer.set(ARENA_ID, PersistentDataType.INTEGER, arena.getId());
		dataContainer.set(TEAM, PersistentDataType.STRING, VDEntity.Team.VILLAGER.getValue());
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

	public String getName() {
		return CommunicationManager.format(
			new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.mobName),
			new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
			new ColoredMessage(ChatColor.GREEN, name),
			new ColoredMessage("")
		);
	}

	public abstract void incrementLevel();

	public abstract boolean isMaxed();

	public abstract void respawn(boolean forced);

	public abstract ItemStack createDisplayButton();

	public abstract ItemStack createUpgradeButton();

	public void heal() {
		// Check if still alive
		if (mob.isDead())
			return;

		// Natural heal
		if (!owner
			.getChallenges()
			.contains(Challenge.uhc())) {
			int hunger = owner
				.getPlayer()
				.getFoodLevel();
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
		mob
			.getActivePotionEffects()
			.forEach(potionEffect -> {
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
		takeDamage(currentHealth, Attacker.AttackType.DIRECT, null);
		updateNameTag();
	}

	@Override
	protected void updateNameTag() {
		super.updateNameTag(ChatColor.GREEN);
	}
}
