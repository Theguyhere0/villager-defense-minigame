package me.theguyhere.villagerdefense.plugin.entities.mobs.golems;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.entities.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.entities.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.entities.mobs.VDMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Golem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public abstract class VDGolem extends VDMob {
	protected final Material buttonMat;
	protected final Location home;

	protected VDGolem(
		Arena arena, Golem golem, String name, String lore, IndividualAttackType attackType,
		Material buttonMat, Location home
	) {
		super(arena, lore, attackType);
		mob = golem;
		id = golem.getUniqueId();
		PersistentDataContainer dataContainer = golem.getPersistentDataContainer();
		dataContainer.set(ARENA_ID, PersistentDataType.INTEGER, arena.getId());
		dataContainer.set(TEAM, PersistentDataType.STRING, IndividualTeam.VILLAGER.getValue());
		this.name = name;
		this.buttonMat = buttonMat;
		this.home = home;
		golem.setRemoveWhenFarAway(false);
		golem.setHealth(2);
		golem.setCustomNameVisible(true);
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

		// Regeneration
		mob
			.getActivePotionEffects()
			.forEach(potionEffect -> {
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
