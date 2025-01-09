package me.theguyhere.villagerdefense.plugin.entities.mobs;

import lombok.Getter;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.displays.Popup;
import me.theguyhere.villagerdefense.plugin.entities.Attacker;
import me.theguyhere.villagerdefense.plugin.entities.VDEntity;
import me.theguyhere.villagerdefense.plugin.entities.players.LegacyVDPlayer;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.huds.SidebarManager;
import me.theguyhere.villagerdefense.plugin.entities.players.PlayerNotFoundException;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class VDMob {
	/**
	 * Minecraft entity to build on.
	 * Not customizable.
	 */
	protected Mob mob;
	protected UUID id;
	protected final String lore;
	protected final Map<UUID, Integer> damageMap = new HashMap<>();
	protected final Arena arena;
	@Getter
	protected int wave;
	protected String name;
	protected int hpBarSize;

	protected int level;
	protected int maxHealth = 0;
	protected int currentHealth = 0;
	protected int armor = 0;
	protected int toughness = 0;
	protected int damage = 0;
	protected double damageSpread = 0;
	@Getter
	protected PotionEffectType effectType;
	protected int effectLevel = 0;
	@Getter
	protected int effectDuration = 0;
	@Getter
	protected int pierce = 0;
	@Getter
	protected final Attacker.AttackType attackType;
	protected int loot = 0;
	@Deprecated
	protected double lootSpread = 0;

	public static final NamespacedKey ARENA_ID = new NamespacedKey(Main.plugin, "VDArenaID");
	public static final NamespacedKey TEAM = new NamespacedKey(Main.plugin, "VDTeam");

	protected VDMob(Arena arena, String lore, Attacker.AttackType attackType) {
		this.arena = arena;
		this.lore = lore;
		this.attackType = attackType;
	}

	public Mob getEntity() {
		return mob;
	}

	public UUID getID() {
		return id;
	}

	public int takeDamage(
		int damage, @NotNull Attacker.AttackType attackType, @Nullable Player attacker
	) {
		// Apply defense based on attack type
		switch (attackType) {
			case NORMAL:
				damage = (int) (Math.max(damage - armor, 0) * Math.max(0, 1 - toughness / 100d));
				break;
			case SLASHING:
				damage = Math.max(damage - armor, 0);
				break;
			case CRUSHING:
				damage = (int) (Math.max(damage - armor / 2, 0) * Math.max(0, 1 - toughness / 200d));
				break;
			case PENETRATING:
				damage = (int) (damage * Math.max(0, 1 - toughness / 100d));
				break;
			case DIRECT:
				break;
			default:
				damage = 0;
		}

		// Final damage display
		if (attacker != null)
			try {
				Popup.create(getEntity().getEyeLocation(),
					new ColoredMessage(ChatColor.RED, "-" + damage + Constants.HP).toString(), 1,
					attacker.getPlayer()
				);
			}
			catch (InvalidLocationException ignored) {
			}

		UUID attackerID = attacker == null ? null : attacker.getUniqueId();

		// Fatal damage
		if (damage >= currentHealth) {
			addDamage(currentHealth, attackerID);
			Random r = new Random();
			int finalGems = (int) (loot * (1 + (r.nextDouble() * 2 - 1) * lootSpread));

			// Reward for all damagers
			damageMap.forEach((id, contribution) -> {
				int gems = (int) (finalGems * (double) contribution / maxHealth);
				try {
					LegacyVDPlayer gamer = arena.getPlayer(id);

					// Check if player has gem increase achievement and is boosted
					if (gamer.isBoosted() && PlayerManager.hasAchievement(id, Achievement
						.topBalance9()
						.getID()))
						gems = (int) (gems * 1.1);
					gamer.addGems(gems);

					// Create popup
					try {
						Popup.create(getEntity()
								.getLocation()
								.add(0, 1, 0),
							new ColoredMessage(ChatColor.GREEN, "+" + gems + Constants.GEM).toString(), 2.5,
							gamer.getPlayer()
						);
					}
					catch (InvalidLocationException ignored) {
					}

					// Update player stats
					PlayerManager.setTotalGems(id, PlayerManager.getTotalGems(id) + gems);
					if (PlayerManager.getTopBalance(id) < gamer.getGems())
						PlayerManager.setTopBalance(id, gamer.getGems());

					// Update scoreboard
					SidebarManager.updateActivePlayerSidebar(gamer);
				}
				catch (PlayerNotFoundException ignored) {
				}
			});

			// Reward kill to dealer of final blow
			try {
				arena
					.getPlayer(attacker)
					.incrementKills();
			}
			catch (PlayerNotFoundException ignored) {
			}

			// Kill
			getEntity().setHealth(0);
		}

		// Non-fatal damage
		else {
			addDamage(damage, attackerID);
		}

		// Update entity name
		updateNameTag();

		return damage;
	}

	protected void addDamage(int damage, UUID id) {
		currentHealth -= damage;
		if (id == null)
			return;
		if (damageMap.containsKey(id))
			damageMap.replace(id, damageMap.get(id) + damage);
		else damageMap.put(id, damage);
	}

	/**
	 * Takes final health difference and applies the difference, checking for absorption, death, and performing
	 * notifications.
	 *
	 * @param dif Final health difference.
	 */
	public void changeCurrentHealth(int dif) {
		// Make sure health was initialized properly
		if (maxHealth <= 0)
			return;

		// Update true health
		currentHealth = Math.min(Math.max(currentHealth + dif, 0), maxHealth);
	}

	public int dealRawDamage() {
		Random r = new Random();
		AtomicInteger increase = new AtomicInteger();
		mob
			.getActivePotionEffects()
			.forEach(potionEffect -> {
				if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
					increase.addAndGet(1 + potionEffect.getAmplifier());
				else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
					increase.addAndGet(-1 - potionEffect.getAmplifier());
			});
		return (int) (this.damage * (1 + (r.nextDouble() * 2 - 1) * damageSpread) * (1 + .1 * increase.get()));
	}

	public PotionEffect dealEffect() {
		return effectType == null ? null : new PotionEffect(effectType, Calculator.secondsToTicks(effectDuration),
			effectLevel - 1
		);
	}

	/**
	 * Sets the proper health for the mob.
	 */
	protected void setHealth(int health) {
		maxHealth = health;
		currentHealth = maxHealth;
	}

	/**
	 * Sets the proper damage for the mob.
	 *
	 * @param base   Base damage.
	 * @param spread Damage spread in terms of proportion.
	 */
	protected void setDamage(int base, double spread) {
		damage = base;
		damageSpread = spread;
	}

	// Sets the proper effect type, if there is one
	protected void setEffectType(PotionEffectType effectType) {
		this.effectType = effectType;
	}

	/**
	 * Sets the proper loot for the mob.
	 *
	 * @param value  The value of the mob.
	 * @param spread Spread in gem drop.
	 */
	@Deprecated
	protected void setLoot(int value, double spread) {
		loot = (int) Math.pow(value, .9);
		lootSpread = spread;
	}

	// Set name properly
	protected void updateNameTag(ChatColor color) {
		int healthLength = Integer
			.toString(currentHealth)
			.length();
		int trueSize = hpBarSize * 4 + healthLength;
		int bars = (int) ((double) currentHealth / maxHealth * trueSize);
		StringBuilder healthIndicator = new StringBuilder(new String(new char[bars])
			.replace("\0", Constants.HP_BAR))
			.append(new String(new char[trueSize - bars]).replace("\0", " "));
		healthIndicator.replace(hpBarSize * 2, hpBarSize * 2 + healthLength, "&b" + currentHealth + color);
		getEntity().setCustomName(CommunicationManager.format(
			new ColoredMessage(color, LanguageManager.messages.mobName),
			new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
			new ColoredMessage(color, name),
			new ColoredMessage(ChatColor.RESET, CommunicationManager.format(
				String.format("&7[" + color + "%s&7]", healthIndicator)))
		));
	}

	protected abstract void updateNameTag();

	public static boolean isTeam(Entity entity, VDEntity.Team team) {
		return team
			.getValue()
			.equals(entity
				.getPersistentDataContainer()
				.get(TEAM, PersistentDataType.STRING));
	}

	public static boolean areSameTeam(Entity e1, Entity e2) {
		return Objects.equals(
			e1
				.getPersistentDataContainer()
				.get(TEAM, PersistentDataType.STRING),
			e2
				.getPersistentDataContainer()
				.get(TEAM, PersistentDataType.STRING)
		);
	}

	public static boolean isVDMob(Entity entity) {
		return entity
			.getPersistentDataContainer()
			.has(ARENA_ID, PersistentDataType.INTEGER);
	}

	public static int getArenaID(Entity entity) {
		Integer result = entity
			.getPersistentDataContainer()
			.get(ARENA_ID, PersistentDataType.INTEGER);
		if (result == null)
			return -1;
		else return result;
	}
}
