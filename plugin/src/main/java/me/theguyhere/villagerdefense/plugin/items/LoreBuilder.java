package me.theguyhere.villagerdefense.plugin.items;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.weapons.VDWeapon;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class LoreBuilder {
	private final List<String> lores;

	public LoreBuilder() {
		lores = new ArrayList<>();
	}

	public LoreBuilder addDescription(String description) {
		lores.addAll(CommunicationManager.formatDescriptionList(ChatColor.GRAY, description,
			Constants.LORE_CHAR_LIMIT));
		return this;
	}

	public LoreBuilder addDescription(ChatColor base, String description) {
		lores.addAll(CommunicationManager.formatDescriptionList(base, description, Constants.LORE_CHAR_LIMIT));
		return this;
	}

	public LoreBuilder addSpace() {
		lores.add("");
		return this;
	}

	public LoreBuilder addEffect(String effect, String... replacements) {
		ColoredMessage[] newReplacements =
			Arrays
				.stream(replacements)
				.map(s -> new ColoredMessage(ChatColor.RED, s))
				.toArray(ColoredMessage[]::new);
		if (effect != null && !effect.isEmpty())
			lores.addAll(CommunicationManager.formatDescriptionList(
				ChatColor.LIGHT_PURPLE,
				CommunicationManager.format(new ColoredMessage(
					ChatColor.BLUE,
					LanguageManager.messages.effect
				), CommunicationManager.format(new ColoredMessage(
					ChatColor.LIGHT_PURPLE,
					effect
				), newReplacements)), Constants.LORE_CHAR_LIMIT
			));
		return this;
	}

	public LoreBuilder addRange(double previousRange, double currentRange) {
		if (currentRange > 0) {
			String range;
			if (previousRange == currentRange)
				range = Double.toString(currentRange);
			else range = previousRange + Constants.UPGRADE + currentRange;
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				String.format(LanguageManager.messages.range, LanguageManager.messages.blocks)
			), new ColoredMessage(
				ChatColor.DARK_AQUA,
				range
			)));
		}
		return this;
	}

	public LoreBuilder addDuration(double previousDuration, double currentDuration) {
		if (currentDuration > 0) {
			String duration;
			if (previousDuration == currentDuration)
				duration = Double.toString(currentDuration);
			else duration = previousDuration + Constants.UPGRADE + currentDuration;
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				String.format(LanguageManager.messages.duration, LanguageManager.messages.seconds)
			), new ColoredMessage(
				ChatColor.DARK_PURPLE,
				duration
			)));
		}
		return this;
	}

	public LoreBuilder addCooldown(double previousCooldown, double currentCooldown) {
		if (currentCooldown > 0) {
			String cooldown;
			if (previousCooldown == currentCooldown)
				cooldown = Double.toString(currentCooldown);
			else cooldown = previousCooldown + Constants.UPGRADE + currentCooldown;
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				String.format(LanguageManager.messages.cooldown, LanguageManager.messages.seconds)
			), cooldown));
		}
		return this;
	}

	public LoreBuilder addPrice(int price) {
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" + price));
		return this;
	}

	public LoreBuilder addArmor(int armor) {
		if (armor > 0)
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.armor
			), new ColoredMessage(ChatColor.AQUA, Integer.toString(armor))));
		return this;
	}

	public LoreBuilder addToughness(int toughness) {
		if (toughness > 0)
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.toughness
			), new ColoredMessage(ChatColor.DARK_AQUA, toughness + "%")));
		return this;
	}

	public LoreBuilder addWeight(int weight) {
		if (weight > 0)
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.weight
			), new ColoredMessage(ChatColor.DARK_PURPLE, Integer.toString(weight))));
		return this;
	}

	public LoreBuilder addDurability(int durability) {
		if (durability > 0)
			lores.add(CommunicationManager.format(
				VDItem.DURABILITY, new ColoredMessage(ChatColor.GREEN, Integer.toString(durability)).toString() +
					new ColoredMessage(ChatColor.WHITE, " / " + durability)));
		return this;
	}

	public LoreBuilder addHealthIndicator(int previousHealth, int currentHealth) {
		if (currentHealth > 0) {
			String health;
			if (previousHealth == currentHealth)
				health = Integer.toString(currentHealth);
			else health = previousHealth + Constants.UPGRADE + currentHealth;
			lores.add(new ColoredMessage(ChatColor.RED, Constants.HP + " " + health).toString());
		}
		return this;
	}

	public LoreBuilder addArmorIndicator(int previousArmor, int currentArmor) {
		if (currentArmor > 0) {
			String armor;
			if (previousArmor == currentArmor)
				armor = Integer.toString(currentArmor);
			else armor = previousArmor + Constants.UPGRADE + currentArmor;
			lores.add(new ColoredMessage(ChatColor.AQUA, Constants.ARMOR + " " + armor).toString());
		}
		return this;
	}

	public LoreBuilder addToughnessIndicator(int previousToughness, int currentToughness) {
		if (currentToughness > 0) {
			String toughness;
			if (previousToughness == currentToughness)
				toughness = currentToughness + "%";
			else toughness = previousToughness + "%" + Constants.UPGRADE + currentToughness + "%";
			lores.add(new ColoredMessage(ChatColor.DARK_AQUA, Constants.TOUGH + " " + toughness).toString());
		}
		return this;
	}

	public LoreBuilder addAttackIndicator(int previousAttackLow, int previousAttackHigh, int currentAttackLow,
		int currentAttackHigh) {
		if (currentAttackHigh > 0) {
			String attack;
			if (previousAttackLow == currentAttackLow && previousAttackHigh == currentAttackHigh)
				attack = currentAttackLow + "-" + currentAttackHigh;
			else attack =
				previousAttackLow + "-" + previousAttackHigh + Constants.UPGRADE + currentAttackLow + "-" +
					currentAttackHigh;
			lores.add(new ColoredMessage(ChatColor.GREEN, Constants.DAMAGE + " " + attack).toString());
		}
		return this;
	}

	private LoreBuilder addAttackType(ColoredMessage type) {
		lores.add(CommunicationManager.format(new ColoredMessage(
			ChatColor.BLUE,
			LanguageManager.messages.attackType
		), type));
		return this;
	}

	public LoreBuilder addNormalAttackType() {
		return addAttackType(new ColoredMessage(
			ChatColor.GREEN,
			LanguageManager.names.normal
		));
	}

	public LoreBuilder addCrushingAttackType() {
		return addAttackType(new ColoredMessage(
			ChatColor.YELLOW,
			LanguageManager.names.crushing
		));
	}

	public LoreBuilder addPenetratingAttackType() {
		return addAttackType(new ColoredMessage(
			ChatColor.RED,
			LanguageManager.names.penetrating
		));
	}

	public LoreBuilder addMainDamage(String previousDamage, String currentDamage) {
		if (currentDamage != null && !currentDamage.isEmpty()) {
			String damage;
			if (currentDamage.equals(previousDamage))
				damage = currentDamage;
			else damage = previousDamage + Constants.UPGRADE + currentDamage;
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.attackMainDamage
			), new ColoredMessage(
				ChatColor.RED,
				damage
			)));
		}
		return this;
	}

	public LoreBuilder addCriticalDamage(String previousDamage, String currentDamage) {
		if (currentDamage != null && !currentDamage.isEmpty()) {
			String damage;
			if (currentDamage.equals(previousDamage))
				damage = currentDamage;
			else damage = previousDamage + Constants.UPGRADE + currentDamage;
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.attackCritDamage
			), new ColoredMessage(
				ChatColor.DARK_PURPLE,
				damage
			)));
		}
		return this;
	}

	public LoreBuilder addSweepDamage(String previousDamage, String currentDamage) {
		if (currentDamage != null && !currentDamage.isEmpty()) {
			String damage;
			if (currentDamage.equals(previousDamage))
				damage = currentDamage;
			else damage = previousDamage + Constants.UPGRADE + currentDamage;
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.attackSweepDamage
			), new ColoredMessage(
				ChatColor.LIGHT_PURPLE,
				damage
			)));
		}
		return this;
	}

	public LoreBuilder addRangeDamage(String previousDamage, String currentDamage, boolean perBlock) {
		if (currentDamage != null && !currentDamage.isEmpty()) {
			String damage;
			if (currentDamage.equals(previousDamage))
				damage = currentDamage;
			else damage = previousDamage + Constants.UPGRADE + currentDamage;
			if (perBlock)
				damage = String.format(LanguageManager.messages.perBlock, damage);
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.attackRangeDamage
			), new ColoredMessage(
				ChatColor.DARK_AQUA,
				damage
			)));
		}
		return this;
	}

	public LoreBuilder addAttackSpeed(double speed) {
		if (speed > 0)
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.attackSpeed
			), Double.toString(speed)));
		return this;
	}

	public LoreBuilder addPierce(int pierce) {
		if (pierce > 0)
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.pierce
			), new ColoredMessage(ChatColor.GOLD, Integer.toString(pierce))));
		return this;
	}

	public LoreBuilder addAmmoCost(int ammoCost) {
		if (ammoCost >= 0)
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				LanguageManager.messages.ammoCost
			), new ColoredMessage(ChatColor.RED, Integer.toString(ammoCost))));
		return this;
	}

	public LoreBuilder addCapacity(int previousCapacity, int currentCapacity) {
		if (currentCapacity > 0) {
			String capacity;
			if (previousCapacity == currentCapacity)
				capacity = new ColoredMessage(ChatColor.GREEN, Integer.toString(currentCapacity)).toString() +
					new ColoredMessage(ChatColor.WHITE, "/" + currentCapacity);
			else capacity = new ColoredMessage(previousCapacity + Constants.UPGRADE) +
				new ColoredMessage(ChatColor.GREEN, Integer.toString(currentCapacity)).toString() +
				new ColoredMessage("/" + currentCapacity);
			lores.add(CommunicationManager.format(VDWeapon.CAPACITY, capacity));
		}
		return this;
	}

	public LoreBuilder addRefillRate(double previousRefill, double currentRefill) {
		if (currentRefill > 0) {
			String refill;
			if (previousRefill == currentRefill)
				refill = Double.toString(currentRefill);
			else refill = previousRefill + Constants.UPGRADE + currentRefill;
			lores.add(CommunicationManager.format(new ColoredMessage(
				ChatColor.BLUE,
				String.format(LanguageManager.messages.refill, LanguageManager.messages.seconds)
			), refill));
		}
		return this;
	}

	public LoreBuilder addHealthGain(int health) {
		if (health > 0)
			lores.add(new ColoredMessage(ChatColor.RED, "+" + health + " " + Constants.HP).toString());
		return this;
	}

	public LoreBuilder addHungerGain(int hunger) {
		if (hunger > 0)
			lores.add(new ColoredMessage(ChatColor.BLUE, "+" + hunger + " " + Constants.HUNGER).toString());
		return this;
	}

	public LoreBuilder addAbsorptionGain(int absorption) {
		if (absorption > 0)
			lores.add(new ColoredMessage(ChatColor.GOLD, "+" + absorption + " " + Constants.HP).toString());
		return this;
	}

	public List<String> build() {
		return lores;
	}
}
