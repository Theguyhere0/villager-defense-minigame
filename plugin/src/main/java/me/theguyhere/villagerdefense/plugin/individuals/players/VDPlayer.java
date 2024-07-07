package me.theguyhere.villagerdefense.plugin.individuals.players;

import com.google.common.util.concurrent.AtomicDouble;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.huds.BottomBarController;
import me.theguyhere.villagerdefense.plugin.huds.SidebarManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDHorse;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDPet;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.items.menuItems.SlotGuard;
import me.theguyhere.villagerdefense.plugin.items.menuItems.VDMenuItem;
import me.theguyhere.villagerdefense.plugin.items.weapons.VDWeapon;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A class holding data about players in a Villager Defense game.
 */
public class VDPlayer {
	/**
	 * UUID of corresponding {@link Player}.
	 */
	private final UUID player;
	/**
	 * Status of the this {@link VDPlayer}.
	 */
	private Status status;
	/**
	 * The arena that this {@link VDPlayer} belongs in.
	 */
	private final Arena arena;

	// Important arena stats
	private int maxHealth = 0;
	private int currentHealth = 0;
	private int absorption = 0;
	private final Map<String, Integer> damageValues = new HashMap<>();
	private final AtomicDouble damageMultiplier = new AtomicDouble(1);
	private int armor = 0;
	private int toughness = 0;
	private double weight = 1;
	private boolean ability = false;
	private boolean range = false;
	private boolean perBlock = false;
	private IndividualAttackType attackType = IndividualAttackType.NORMAL;
	/**
	 * The time until weapon cooldown us up.
	 */
	private long weaponCooldown = 0;
	/**
	 * The time until ability cooldown us up.
	 */
	private long abilityCooldown = 0;
	/**
	 * Gem balance.
	 */
	private int gems = 0;
	/**
	 * Kill count.
	 */
	private int kills = 0;
	/**
	 * Pets following the player.
	 */
	private final List<VDPet> pets = new ArrayList<>();
	/**
	 * Maximum pet slots available for use.
	 */
	private int petSlots = 0;
	/**
	 * The wave at which the player joined the game as an active player.
	 */
	private int joinedWave = 0;
	/**
	 * The number of times this player violated arena boundaries.
	 */
	private int infractions = 0;
	/**
	 * The {@link Kit} the player will play with.
	 */
	private Kit kit = Kit.none();
	/**
	 * The level of tiered essence the player has.
	 */
	private int tieredEssenceLevel = 0;
	/**
	 * The list of {@link Challenge}'s the player will take on.
	 */
	private List<Challenge> challenge = new ArrayList<>();
	/**
	 * The list of UUIDs of those that damaged the player.
	 */
	private final List<UUID> enemies = new ArrayList<>();
	/**
	 * Helmet {@link ItemStack} held for ninja ability.
	 */
	private ItemStack helmet;
	/**
	 * Chestplate {@link ItemStack} held for ninja ability.
	 */
	private ItemStack chestplate;
	/**
	 * Leggings {@link ItemStack} held for ninja ability.
	 */
	private ItemStack leggings;
	/**
	 * Boots {@link ItemStack} held for ninja ability.
	 */
	private ItemStack boots;
	/**
	 * Whether permanent boosts are on or not.
	 */
	private boolean boost = true;
	/**
	 * Number of gems to be converted from crystals.
	 */
	private int gemBoost = 0;

	public VDPlayer(Player player, Arena arena, boolean spectating) {
		this.player = player.getUniqueId();
		this.arena = arena;
		if (spectating)
			status = Status.SPECTATOR;
		else status = Status.ALIVE;
		player.setScoreboard(Main.getVdBoard());
	}

	public UUID getID() {
		return player;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}

	public Arena getArena() {
		return arena;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealthInit(int maxHealth) {
		this.maxHealth = Math.max(maxHealth, 0);
		currentHealth = Math.max(maxHealth, 0);
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = Math.max(maxHealth, 0);
		getPlayer().setHealth(currentHealth *
			Objects
				.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
				.getValue() / maxHealth);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean hasMaxHealth() {
		return currentHealth == maxHealth;
	}

	public void addAbsorption(int absorption) {
		this.absorption += absorption;
	}

	public void addAbsorptionUpTo(int absorption) {
		if (this.absorption < absorption)
			this.absorption = absorption;
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

		// Handle absorption
		int trueDif = dif;
		if (dif < 0) {
			trueDif = Math.min(0, absorption + dif);
			absorption = Math.max(absorption + dif, 0);
		}

		// Update true health
		currentHealth = Math.min(Math.max(currentHealth + trueDif, 0), maxHealth);

		// Set health warning
		NMSVersion
			.getCurrent()
			.getNmsManager()
			.createBorderWarning(
				arena
					.getPlayerSpawn()
					.getLocation(),
				currentHealth / (double) maxHealth
			)
			.sendTo(getPlayer());

		// Check for death
		if (this.currentHealth == 0) {
			// Check if player has resurrection achievement and is boosted
			Random random = new Random();
			if (boost && random.nextDouble() < .1 &&
				PlayerManager.hasAchievement(getPlayer().getUniqueId(), Achievement
					.allChallenges()
					.getID())) {
				PlayerManager.giveTotemEffect(getPlayer());
				currentHealth = maxHealth / 2;
				return;
			}

			// Set player to fake death mode
			PlayerManager.fakeDeath(this);

			// Kill off pets
			pets.forEach(VDPet::kill);

			// Check for explosive challenge
			if (getChallenges().contains(Challenge.explosive())) {
				// Create an explosion
				getPlayer()
					.getWorld()
					.createExplosion(getPlayer().getLocation(), 1.75F, false, false);

				// Drop all items and clear inventory
				getPlayer()
					.getInventory()
					.forEach(itemStack -> {
						if (itemStack != null && !VDMenuItem.matches(itemStack) && !VDAbility.matches(itemStack))
							getPlayer()
								.getWorld()
								.dropItemNaturally(getPlayer().getLocation(), itemStack);
					});
				getPlayer()
					.getInventory()
					.clear();
				tieredEssenceLevel = 0;
			}

			// Notify player of their own death
			getPlayer().sendTitle(
				new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.death1).toString(),
				new ColoredMessage(ChatColor.RED, LanguageManager.messages.death2).toString(),
				Calculator.secondsToTicks(.5), Calculator.secondsToTicks(2.5), Calculator.secondsToTicks(1)
			);

			// Notify everyone else of player death
			arena
				.getPlayers()
				.forEach(fighter -> {
					if (!fighter
						.getPlayer()
						.getUniqueId()
						.equals(getPlayer().getUniqueId()))
						PlayerManager.notifyAlert(
							fighter.getPlayer(),
							String.format(LanguageManager.messages.death, getPlayer().getName())
						);
					if (arena.hasPlayerDeathSound())
						try {
							fighter
								.getPlayer()
								.playSound(arena
										.getPlayerSpawn()
										.getLocation()
										.add(0, 5, 0),
									Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10,
									.75f
								);
						}
						catch (NullPointerException err) {
							CommunicationManager.debugError(err.getMessage(), CommunicationManager.DebugLevel.QUIET);
						}
				});

			// Update scoreboards
			arena.updateScoreboards();

			// Check for game end condition
			if (arena.getAlive() == 0)
				try {
					arena.endGame();
				}
				catch (ArenaException e) {
					CommunicationManager.debugError("%s: %s", CommunicationManager.DebugLevel.QUIET, arena.getName(),
						e.getMessage());
				}
		}
	}

	public void updateDamageMultiplier() {
		// Reset
		damageMultiplier.set(1);

		// Calculate boosts or reductions
		getPlayer()
			.getActivePotionEffects()
			.forEach(potionEffect -> {
				if (PotionEffectType.INCREASE_DAMAGE.equals(potionEffect.getType()))
					damageMultiplier.addAndGet(.1 * (potionEffect.getAmplifier() + 1));
				else if (PotionEffectType.WEAKNESS.equals(potionEffect.getType()))
					damageMultiplier.addAndGet(-.1 * (potionEffect.getAmplifier() + 1));
			});
		if (boost && PlayerManager.hasAchievement(player, Achievement
			.topKills9()
			.getID()))
			damageMultiplier.addAndGet(.1);
		if (getPlayer().isInsideVehicle())
			damageMultiplier.addAndGet(VDHorse.getDamageBoost(getPets()
				.stream()
				.filter(pet -> pet instanceof VDHorse)
				.collect(Collectors.toList())
				.get(0)
				.getLevel()));
	}

	public void updateMainHand(ItemStack main) {
		// Reset values
		damageValues.clear();
		ability = false;
		range = false;
		perBlock = false;
		attackType = IndividualAttackType.NORMAL;

		// Check for an ability
		if (VDAbility.matches(main) || VDAbility.matches(getPlayer()
			.getInventory()
			.getItemInOffHand()))
			ability = true;

		// Ignore non-plugin items
		if (!VDItem.matches(main))
			return;

		PersistentDataContainer dataContainer =
			Objects
				.requireNonNull(main.getItemMeta())
				.getPersistentDataContainer();

		Integer integer = dataContainer.get(AttackClass.MAIN.straight(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.MAIN.straightID, integer);
		integer = dataContainer.get(AttackClass.MAIN.low(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.MAIN.lowID, integer);
		integer = dataContainer.get(AttackClass.MAIN.high(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.MAIN.highID, integer);
		integer = dataContainer.get(AttackClass.CRITICAL.straight(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.CRITICAL.straightID, integer);
		integer = dataContainer.get(AttackClass.CRITICAL.low(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.CRITICAL.lowID, integer);
		integer = dataContainer.get(AttackClass.CRITICAL.high(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.CRITICAL.highID, integer);
		integer = dataContainer.get(AttackClass.SWEEP.straight(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.SWEEP.straightID, integer);
		integer = dataContainer.get(AttackClass.SWEEP.low(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.SWEEP.lowID, integer);
		integer = dataContainer.get(AttackClass.SWEEP.high(), PersistentDataType.INTEGER);
		if (integer != null)
			damageValues.put(AttackClass.SWEEP.highID, integer);
		integer = dataContainer.get(AttackClass.RANGE.straight(), PersistentDataType.INTEGER);
		if (integer != null) {
			damageValues.put(AttackClass.RANGE.straightID, integer);
			range = true;
		}
		integer = dataContainer.get(AttackClass.RANGE.low(), PersistentDataType.INTEGER);
		if (integer != null) {
			damageValues.put(AttackClass.RANGE.lowID, integer);
			range = true;
		}
		integer = dataContainer.get(AttackClass.RANGE.high(), PersistentDataType.INTEGER);
		if (integer != null) {
			damageValues.put(AttackClass.RANGE.highID, integer);
			range = true;
		}

		String s = dataContainer.get(VDWeapon.ATTACK_TYPE_KEY, PersistentDataType.STRING);
		if (IndividualAttackType.PENETRATING
			.toString()
			.equals(s))
			attackType = IndividualAttackType.PENETRATING;
		else if (IndividualAttackType.CRUSHING
			.toString()
			.equals(s))
			attackType = IndividualAttackType.CRUSHING;

		s = dataContainer.get(VDWeapon.PER_BLOCK_KEY, PersistentDataType.STRING);
		if (s != null)
			perBlock = true;
	}

	public void updateMainHand() {
		updateMainHand(getPlayer()
			.getInventory()
			.getItemInMainHand());
	}

	public void updateOffHand(ItemStack off) {
		// Recheck for ability
		ability = VDAbility.matches(off) || VDAbility.matches(getPlayer()
			.getInventory()
			.getItemInMainHand());
	}

	public void updateArmor() {
		armor = 0;
		toughness = 0;
		weight = 1;

		readArmorLore(Objects
			.requireNonNull(getPlayer().getEquipment())
			.getHelmet());
		readArmorLore(Objects
			.requireNonNull(getPlayer().getEquipment())
			.getChestplate());
		readArmorLore(Objects
			.requireNonNull(getPlayer().getEquipment())
			.getLeggings());
		readArmorLore(Objects
			.requireNonNull(getPlayer().getEquipment())
			.getBoots());

		// Resistance effect
		getPlayer()
			.getActivePotionEffects()
			.forEach(potionEffect -> {
				if (PotionEffectType.DAMAGE_RESISTANCE.equals(potionEffect.getType())) {
					armor += 10 * (1 + potionEffect.getAmplifier());
					toughness += 8 * (1 + potionEffect.getAmplifier());
				}
			});

		// Apply boosts
		if (boost && PlayerManager.hasAchievement(player, Achievement
			.totalKills9()
			.getID()))
			armor += 5;
		if (boost && PlayerManager.hasAchievement(player, Achievement
			.allKits()
			.getID()))
			toughness += 4;

		// Set speed
		Objects
			.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
			.setBaseValue(.1 * weight);
	}

	public String getStatusBar() {
		// Make sure health was properly initialized
		if (maxHealth <= 0)
			return "";

		// Calculate damage
		String damage;
		List<Integer> values = new ArrayList<>(damageValues.values());
		values.sort(Comparator.comparingInt(Integer::intValue));
		values.replaceAll(original -> (int) (original * damageMultiplier.get()));
		if (values.isEmpty())
			damage = "0";
		else if (values.size() == 1)
			damage = Integer.toString((values.get(0)));
		else damage = values.get(0) + "-" + values.get(values.size() - 1);


		// Construct status bar
		String SPACE = "    ";
		String middleText = new ColoredMessage(ChatColor.AQUA, Constants.ARMOR + " " + armor) + SPACE +
			new ColoredMessage(ChatColor.DARK_AQUA, Constants.TOUGH + " " + toughness + "%");
		if (ability && remainingAbilityCooldown() > 0)
			middleText = CommunicationManager.format(new ColoredMessage(
				ChatColor.DARK_RED,
				LanguageManager.messages.cooldown
			), new ColoredMessage(
				ChatColor.AQUA,
				Double.toString(Math.round(Calculator.millisToSeconds(remainingAbilityCooldown()) * 10) / 10d)
			));
		ChatColor endTextColor;
		if (attackType == IndividualAttackType.CRUSHING)
			endTextColor = ChatColor.YELLOW;
		else if (attackType == IndividualAttackType.PENETRATING)
			endTextColor = ChatColor.RED;
		else endTextColor = ChatColor.GREEN;

		return new ColoredMessage(absorption > 0 ? ChatColor.GOLD :
			ChatColor.RED, Constants.HP + " " + (currentHealth + absorption) + "/" + maxHealth) +
			SPACE +
			middleText +
			SPACE +
			new ColoredMessage(endTextColor, (range ? Constants.ARROW : Constants.DAMAGE) + " " + damage +
				(perBlock ? " /" + Constants.BLOCK : ""));
	}

	public void heal() {
		// Natural heal
		if (!challenge.contains(Challenge.uhc())) {
			int hunger = getPlayer().getFoodLevel();
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
		getPlayer()
			.getActivePotionEffects()
			.forEach(potionEffect -> {
				if (PotionEffectType.REGENERATION.equals(potionEffect.getType()))
					changeCurrentHealth(5 * (1 + potionEffect.getAmplifier()));
			});

		// Apply boost
		if (boost && PlayerManager.hasAchievement(player, Achievement
			.allEffect()
			.getID()))
			changeCurrentHealth(1);

		// Update normal health display
		getPlayer().setHealth(Math.max(
			currentHealth *
				Objects
					.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
					.getValue() / maxHealth,
			1
		));
		getPlayer().setAbsorptionAmount(absorption *
			Objects
				.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
				.getValue() / maxHealth);
	}

	public void takeDamage(int damage, @NotNull IndividualAttackType attackType) {
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

		// Realize damage
		changeCurrentHealth(-damage);

		// Damage armor
		if (attackType == IndividualAttackType.NORMAL || attackType == IndividualAttackType.CRUSHING ||
			attackType == IndividualAttackType.PENETRATING || attackType == IndividualAttackType.SLASHING)
			Arrays
				.stream(getPlayer()
					.getInventory()
					.getArmorContents())
				.filter(Objects::nonNull)
				.forEach(armor ->
					Bukkit
						.getPluginManager()
						.callEvent(new PlayerItemDamageEvent(getPlayer(), armor, 0)));

		// Update normal health display
		getPlayer().setHealth(Math.max(
			currentHealth *
				Objects
					.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
					.getValue() / maxHealth,
			1
		));
		getPlayer().setAbsorptionAmount(absorption *
			Objects
				.requireNonNull(getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH))
				.getValue() / maxHealth);
	}

	public void combust(int ticks) {
		if (getPlayer().getFireTicks() < ticks)
			getPlayer().setFireTicks(ticks);
	}

	public int dealRawDamage(@NotNull AttackClass playerAttackClass, double mainMult) {
		Random r = new Random();
		double damage;

		switch (playerAttackClass) {
			case MAIN:
				if (damageValues.isEmpty())
					damage = 0;
				else if (damageValues.containsKey(AttackClass.MAIN.straightID))
					damage = (damageValues.get(AttackClass.MAIN.straightID)) * mainMult;
				else damage = (damageValues.get(AttackClass.MAIN.lowID) +
						r.nextInt(damageValues.get(AttackClass.MAIN.highID) -
							damageValues.get(AttackClass.MAIN.lowID))) * mainMult;
				break;
			case CRITICAL:
				if (damageValues.containsKey(AttackClass.CRITICAL.straightID))
					damage = damageValues.get(AttackClass.CRITICAL.straightID);
				else damage = damageValues.get(AttackClass.CRITICAL.lowID) +
					r.nextInt(damageValues.get(AttackClass.CRITICAL.highID) -
						damageValues.get(AttackClass.CRITICAL.lowID));
				break;
			case SWEEP:
				if (damageValues.containsKey(AttackClass.SWEEP.straightID))
					damage = damageValues.get(AttackClass.SWEEP.straightID);
				else damage = damageValues.get(AttackClass.SWEEP.lowID) +
					r.nextInt(damageValues.get(AttackClass.SWEEP.highID) -
						damageValues.get(AttackClass.SWEEP.lowID));
				break;
			case RANGE:
				if (damageValues.containsKey(AttackClass.RANGE.straightID))
					damage = damageValues.get(AttackClass.RANGE.straightID);
				else damage = damageValues.get(AttackClass.RANGE.lowID) +
					r.nextInt(damageValues.get(AttackClass.RANGE.highID) -
						damageValues.get(AttackClass.RANGE.lowID));
				break;
			default:
				damage = 0;
		}

		return (int) (damage * damageMultiplier.get());
	}

	public boolean isPerBlock() {
		return perBlock;
	}

	public long remainingWeaponCooldown() {
		return Math.max(weaponCooldown - System.currentTimeMillis(), 0);
	}

	public void triggerWeaponCooldown(int cooldown) {
		weaponCooldown = System.currentTimeMillis() + cooldown;
	}

	public long remainingAbilityCooldown() {
		return Math.max(abilityCooldown - System.currentTimeMillis(), 0);
	}

	public void triggerAbilityCooldown(int cooldown) {
		abilityCooldown = System.currentTimeMillis() + cooldown;
	}

	public IndividualAttackType getAttackType() {
		return attackType;
	}

	public int getGems() {
		return gems;
	}

	public int getKills() {
		return kills;
	}

	public void addGems(int change) {
		gems += change;
	}

	/**
	 * Checks whether the player can afford a shop item.
	 *
	 * @param cost Item cost.
	 * @return Boolean indicating whether the item was affordable.
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canAfford(int cost) {
		return cost <= gems;
	}

	public void incrementKills() {
		kills++;
	}

	public Kit getKit() {
		return kit;
	}

	public int getTieredEssenceLevel() {
		return tieredEssenceLevel;
	}

	public void incrementTieredEssenceLevel() {
		tieredEssenceLevel++;
	}

	public List<Challenge> getChallenges() {
		return challenge;
	}

	public void addChallenge(Challenge toBeAdded) {
		if (!challenge.contains(toBeAdded))
			challenge.add(toBeAdded);
	}

	public void removeChallenge(Challenge toBeRemoved) {
		challenge.remove(toBeRemoved);
	}

	public void resetChallenges() {
		challenge = new ArrayList<>();
	}

	public List<UUID> getEnemies() {
		return enemies;
	}

	public void addEnemy(UUID toBeAdded) {
		if (!enemies.contains(toBeAdded))
			enemies.add(toBeAdded);
	}

	public boolean isBoosted() {
		return boost;
	}

	public void toggleBoost() {
		boost = !boost;
	}

	public int getGemBoost() {
		return gemBoost;
	}

	public void setGemBoost(int gemBoost) {
		this.gemBoost = gemBoost;
		SidebarManager.updateActivePlayerSidebar(this);
	}

	public void addPet(VDPet pet) {
		pets.add(pet);
		arena.addMob(pet);
	}

	public void removePet(int index) {
		pets
			.get(index)
			.getEntity()
			.remove();
		arena.removeMob(pets
			.get(index)
			.getID());
		pets.remove(index);
	}

	public void respawnPets() {
		for (VDPet pet : pets) {
			pet.respawn(false);
		}
	}

	public int getPetSlots() {
		return petSlots;
	}

	public int getRemainingPetSlots() {
		// Calculate remaining slots
		AtomicInteger remaining = new AtomicInteger(petSlots);
		pets.forEach(pet -> remaining.addAndGet(-pet.getSlots()));

		return remaining.get();
	}

	public List<VDPet> getPets() {
		return pets;
	}

	public int getJoinedWave() {
		return joinedWave;
	}

	public void setJoinedWave(int joinedWave) {
		this.joinedWave = joinedWave;
	}

	public int incrementInfractions() {
		return ++infractions;
	}

	public void resetInfractions() {
		infractions = 0;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	/**
	 * Removes armor from the player while they are invisible under the ninja ability.
	 */
	public void hideArmor() {
		helmet = getPlayer()
			.getInventory()
			.getHelmet();
		getPlayer()
			.getInventory()
			.setHelmet(SlotGuard.createForHelmet(getPlayer()));
		chestplate = getPlayer()
			.getInventory()
			.getChestplate();
		getPlayer()
			.getInventory()
			.setChestplate(SlotGuard.create());
		leggings = getPlayer()
			.getInventory()
			.getLeggings();
		getPlayer()
			.getInventory()
			.setLeggings(SlotGuard.create());
		boots = getPlayer()
			.getInventory()
			.getBoots();
		getPlayer()
			.getInventory()
			.setBoots(SlotGuard.create());
		updateArmor();
	}

	/**
	 * Returns armor to the player after the ninja ability wears out.
	 */
	public void exposeArmor() {
		getPlayer()
			.getInventory()
			.setHelmet(helmet);
		getPlayer()
			.getInventory()
			.setChestplate(chestplate);
		getPlayer()
			.getInventory()
			.setLeggings(leggings);
		getPlayer()
			.getInventory()
			.setBoots(boots);
		updateArmor();
	}

	/**
	 * Gives items on spawn or respawn based on kit selected
	 */
	public void giveItems() {
		// Set slot guards
		if (getChallenges().contains(Challenge.amputee())) {
			for (int i = 9; i < 36; i++) {
				getPlayer().getInventory().setItem(i, SlotGuard.create());
			}
		}
		if (getChallenges().contains(Challenge.naked())) {
			getPlayer().getInventory().setHelmet(SlotGuard.createForHelmet(getPlayer()));
			for (int i = 36; i < 39; i++) {
				getPlayer().getInventory().setItem(i, SlotGuard.create());
			}
		}

		for (ItemStack item : getKit().getItems()) {
			EntityEquipment equipment = getPlayer().getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (item
				.getType()
				.toString()
				.contains("HELMET") && Objects
				.requireNonNull(equipment)
				.getHelmet() == null)
				equipment.setHelmet(item);
			else if (item
				.getType()
				.toString()
				.contains("CHESTPLATE") &&
				Objects
					.requireNonNull(equipment)
					.getChestplate() == null)
				equipment.setChestplate(item);
			else if (item
				.getType()
				.toString()
				.contains("LEGGINGS") &&
				Objects
					.requireNonNull(equipment)
					.getLeggings() == null)
				equipment.setLeggings(item);
			else if (item
				.getType()
				.toString()
				.contains("BOOTS") &&
				Objects
					.requireNonNull(equipment)
					.getBoots() == null)
				equipment.setBoots(item);
			else {
				if (boost && PlayerManager.hasAchievement(player, Achievement
					.allMaxedAbility()
					.getID()))
					PlayerManager.giveItem(getPlayer(), ItemStackBuilder.modifyCooldown(item, .9),
						LanguageManager.errors.inventoryFull
					);
				else PlayerManager.giveItem(getPlayer(), item, LanguageManager.errors.inventoryFull);
			}
		}

		// Set shop
		getPlayer().getInventory().setItem(8, Shop.create());

		updateArmor();
		updateOffHand(null);
		updateMainHand();
	}

	/**
	 * Sets up attributes properly after dying or first spawning.
	 */
	public void setupAttributes(boolean first) {
		int maxHealth = 500;

		// Set health for people with giant kits
		if (Kit
			.giant()
			.setKitLevel(1)
			.equals(getKit())) {
			maxHealth = 550;
		}
		else if (Kit
			.giant()
			.setKitLevel(2)
			.equals(getKit())) {
			maxHealth = 600;
		}

		// Set health for people with health boost and are boosted
		if (boost && PlayerManager.hasAchievement(player, Achievement
			.topWave9()
			.getID())) {
			maxHealth += 50;
		}

		// Set health for people with dwarf challenge
		if (getChallenges().contains(Challenge.dwarf())) {
			maxHealth /= 2;
		}

		// Give blindness to people with that challenge
		if (getChallenges().contains(Challenge.blind()))
			getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 0));

		// Set up health and damage
		setMaxHealthInit(maxHealth);

		// Set up status bar
		updateDamageMultiplier();
		BottomBarController.startStatusBar(this);

		// Only run the first time
		if (first) {
			// Set up pet slots
			if (Kit
				.trainer()
				.setKitLevel(1)
				.equals(getKit()))
				petSlots = 4;
			else if (Kit
				.trainer()
				.setKitLevel(2)
				.equals(getKit()))
				petSlots = 5;
			else petSlots = 3;
		}
	}

	private void readArmorLore(ItemStack armor) {
		if (armor == null)
			return;

		ItemMeta meta = armor.getItemMeta();

		if (meta == null)
			return;

		Integer integer = meta
			.getPersistentDataContainer()
			.get(VDArmor.ARMOR_KEY, PersistentDataType.INTEGER);
		if (integer != null)
			this.armor += integer;

		integer = meta
			.getPersistentDataContainer()
			.get(VDArmor.TOUGHNESS_KEY, PersistentDataType.INTEGER);
		if (integer != null)
			this.toughness += integer;

		integer = meta
			.getPersistentDataContainer()
			.get(VDArmor.WEIGHT_KEY, PersistentDataType.INTEGER);
		if (integer != null)
			this.weight += integer * .01;
	}

	/**
	 * Status of players in Villager Defense. Possible status:<ul>
	 * <li>{@link #ALIVE}</li>
	 * <li>{@link #GHOST}</li>
	 * <li>{@link #SPECTATOR}</li>
	 * </ul>
	 */
	public enum Status {
		/**
		 * Player is alive and active in the game.
		 */
		ALIVE,
		/**
		 * Player is dead but active in the game.
		 */
		GHOST,
		/**
		 * Player is spectating in the game.
		 */
		SPECTATOR
	}

	public enum AttackClass {
		MAIN("mainLow", "mainHigh", "main"),
		CRITICAL("critLow", "critHigh", "crit"),
		SWEEP("sweepLow", "sweepHigh", "sweep"),
		RANGE("rangeLow", "rangeHigh", "range");

		private final String lowID;
		private final String highID;
		private final String straightID;

		AttackClass(String lowID, String highID, String straightID) {
			this.lowID = lowID;
			this.highID = highID;
			this.straightID = straightID;
		}

		public NamespacedKey low() {
			return new NamespacedKey(Main.plugin, lowID.toLowerCase());
		}

		public NamespacedKey straight() {
			return new NamespacedKey(Main.plugin, straightID.toLowerCase());
		}

		public NamespacedKey high() {
			return new NamespacedKey(Main.plugin, highID.toLowerCase());
		}
	}
}
