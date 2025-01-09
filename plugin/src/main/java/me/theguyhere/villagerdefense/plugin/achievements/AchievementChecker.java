package me.theguyhere.villagerdefense.plugin.achievements;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.entities.players.LegacyVDPlayer;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AchievementChecker {
	private static void checkHighScoreAchievement(Achievement achievement, Player player) {
		UUID id = player.getUniqueId();

		// Check if player already has achievement
		if (PlayerManager.hasAchievement(id, achievement.getID()))
			return;

		// Verify correct achievement type
		if (achievement.getType() != Achievement.Type.HIGH_SCORE)
			return;

		List<Boolean> targets = new ArrayList<>();

		// Verify each requirement
		for (AchievementRequirement requirement : achievement.getRequirements()) {
			try {
				if (requirement.getMetric() == AchievementMetric.TOP_BALANCE) {
					if (PlayerManager.getTopBalance(id) < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.TOP_KILLS) {
					if (PlayerManager.getTopKills(id) < requirement.getInteger())
						targets.add(false);
					else targets.add(true);

				}
				if (requirement.getMetric() == AchievementMetric.TOP_WAVE) {
					if (PlayerManager.getTopWave(id) < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				if (requirement.getMetric() == AchievementMetric.TOTAL_GEMS) {
					if (PlayerManager.getTotalGems(id) < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				if (requirement.getMetric() == AchievementMetric.TOTAL_KILLS) {
					if (PlayerManager.getTotalKills(id) < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
			}
			catch (InvalidAchievementReqValException e) {
				CommunicationManager.debugErrorShouldNotHappen();
			}
		}

		if (achievement.isConjunctive() && !targets.contains(false) ||
			!achievement.isConjunctive() && targets.contains(true))
			Bukkit
				.getPluginManager()
				.callEvent(new AchievementEvent(player, achievement));
	}

	private static void checkInstanceAchievement(Achievement achievement, LegacyVDPlayer player) {
		// Protect from null players when player quits
		if (player.getPlayer() == null)
			return;

		UUID id = player.getId();

		// Check if player already has achievement
		if (PlayerManager.hasAchievement(id, achievement.getID()))
			return;

		// Verify correct achievement type
		if (achievement.getType() != Achievement.Type.INSTANCE)
			return;

		Arena arena = player.getArena();
		List<Boolean> targets = new ArrayList<>();

		// Verify each requirement
		for (AchievementRequirement requirement : achievement.getRequirements()) {
			try {
				if (requirement.getMetric() == AchievementMetric.WAVE) {
					if (arena.getCurrentWave() < requirement.getInteger() && arena.getStatus() == ArenaStatus.ENDING ||
						arena.getCurrentWave() <= requirement.getInteger() &&
							arena.getStatus() == ArenaStatus.ACTIVE)
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.GEMS) {
					if (player.getGems() < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.KIT) {
					if (!requirement
						.getString()
						.equals(player
							.getKit()
							.getID()))
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.CHALLENGE) {
					if (!player
						.getChallenges()
						.contains(Challenge.getChallengeByID(requirement.getString())))
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.PLAYERS) {
					if (arena.getAlive() < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.GHOSTS) {
					if (arena.getGhostCount() < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.SPECTATORS) {
					if (arena.getSpectatorCount() < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.ENEMIES) {
					if (arena.getEnemies() < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.KILLS) {
					if (player.getKills() < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
				else if (requirement.getMetric() == AchievementMetric.ACTIVE_PLAYERS) {
					if (arena.getActiveCount() < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
			}
			catch (InvalidAchievementReqValException e) {
				CommunicationManager.debugErrorShouldNotHappen();
			}
		}

		if (achievement.isConjunctive() && !targets.contains(false) ||
			!achievement.isConjunctive() && targets.contains(true))
			Bukkit
				.getPluginManager()
				.callEvent(new AchievementEvent(player.getPlayer(), achievement));
	}

	private static void checkKitAchievement(Achievement achievement, Player player) {
		UUID id = player.getUniqueId();

		// Check if player already has achievement
		if (PlayerManager.hasAchievement(id, achievement.getID()))
			return;

		// Verify correct achievement type
		if (achievement.getType() != Achievement.Type.KIT)
			return;

		List<Boolean> targets = new ArrayList<>();

		// Verify each requirement
		for (AchievementRequirement requirement : achievement.getRequirements()) {
			try {
				if (requirement.getInteger() == 1) {
					if (!PlayerManager.hasSingleTierKit(id, requirement.getString()))
						targets.add(false);
					else targets.add(true);
				}
				else {
					if (PlayerManager.getMultiTierKitLevel(id, requirement.getString()) < requirement.getInteger())
						targets.add(false);
					else targets.add(true);
				}
			}
			catch (InvalidAchievementReqValException e) {
				CommunicationManager.debugErrorShouldNotHappen();
			}
		}

		if (achievement.isConjunctive() && !targets.contains(false) ||
			!achievement.isConjunctive() && targets.contains(true))
			Bukkit
				.getPluginManager()
				.callEvent(new AchievementEvent(player, achievement));
	}

	public static void checkDefaultHighScoreAchievements(Player player) {
		checkHighScoreAchievement(Achievement.topBalance1(), player);
		checkHighScoreAchievement(Achievement.topBalance2(), player);
		checkHighScoreAchievement(Achievement.topBalance3(), player);
		checkHighScoreAchievement(Achievement.topBalance4(), player);
		checkHighScoreAchievement(Achievement.topBalance5(), player);
		checkHighScoreAchievement(Achievement.topBalance6(), player);
		checkHighScoreAchievement(Achievement.topBalance7(), player);
		checkHighScoreAchievement(Achievement.topBalance8(), player);
		checkHighScoreAchievement(Achievement.topBalance9(), player);
		checkHighScoreAchievement(Achievement.topKills1(), player);
		checkHighScoreAchievement(Achievement.topKills2(), player);
		checkHighScoreAchievement(Achievement.topKills3(), player);
		checkHighScoreAchievement(Achievement.topKills4(), player);
		checkHighScoreAchievement(Achievement.topKills5(), player);
		checkHighScoreAchievement(Achievement.topKills6(), player);
		checkHighScoreAchievement(Achievement.topKills7(), player);
		checkHighScoreAchievement(Achievement.topKills8(), player);
		checkHighScoreAchievement(Achievement.topKills9(), player);
		checkHighScoreAchievement(Achievement.topWave1(), player);
		checkHighScoreAchievement(Achievement.topWave2(), player);
		checkHighScoreAchievement(Achievement.topWave3(), player);
		checkHighScoreAchievement(Achievement.topWave4(), player);
		checkHighScoreAchievement(Achievement.topWave5(), player);
		checkHighScoreAchievement(Achievement.topWave6(), player);
		checkHighScoreAchievement(Achievement.topWave7(), player);
		checkHighScoreAchievement(Achievement.topWave8(), player);
		checkHighScoreAchievement(Achievement.topWave9(), player);
		checkHighScoreAchievement(Achievement.totalGems1(), player);
		checkHighScoreAchievement(Achievement.totalGems2(), player);
		checkHighScoreAchievement(Achievement.totalGems3(), player);
		checkHighScoreAchievement(Achievement.totalGems4(), player);
		checkHighScoreAchievement(Achievement.totalGems5(), player);
		checkHighScoreAchievement(Achievement.totalGems6(), player);
		checkHighScoreAchievement(Achievement.totalGems7(), player);
		checkHighScoreAchievement(Achievement.totalGems8(), player);
		checkHighScoreAchievement(Achievement.totalGems9(), player);
		checkHighScoreAchievement(Achievement.totalKills1(), player);
		checkHighScoreAchievement(Achievement.totalKills2(), player);
		checkHighScoreAchievement(Achievement.totalKills3(), player);
		checkHighScoreAchievement(Achievement.totalKills4(), player);
		checkHighScoreAchievement(Achievement.totalKills5(), player);
		checkHighScoreAchievement(Achievement.totalKills6(), player);
		checkHighScoreAchievement(Achievement.totalKills7(), player);
		checkHighScoreAchievement(Achievement.totalKills8(), player);
		checkHighScoreAchievement(Achievement.totalKills9(), player);
	}

	public static void checkDefaultInstanceAchievements(LegacyVDPlayer player) {
		checkInstanceAchievement(Achievement.allChallenges(), player);
		checkInstanceAchievement(Achievement.alone(), player);
		checkInstanceAchievement(Achievement.amputeeAlone(), player);
		checkInstanceAchievement(Achievement.amputeeBalance(), player);
		checkInstanceAchievement(Achievement.amputeeKills(), player);
		checkInstanceAchievement(Achievement.amputeeWave(), player);
		checkInstanceAchievement(Achievement.blindAlone(), player);
		checkInstanceAchievement(Achievement.blindBalance(), player);
		checkInstanceAchievement(Achievement.blindKills(), player);
		checkInstanceAchievement(Achievement.blindWave(), player);
		checkInstanceAchievement(Achievement.clumsyAlone(), player);
		checkInstanceAchievement(Achievement.clumsyBalance(), player);
		checkInstanceAchievement(Achievement.clumsyKills(), player);
		checkInstanceAchievement(Achievement.clumsyWave(), player);
		checkInstanceAchievement(Achievement.dwarfAlone(), player);
		checkInstanceAchievement(Achievement.dwarfBalance(), player);
		checkInstanceAchievement(Achievement.dwarfKills(), player);
		checkInstanceAchievement(Achievement.dwarfWave(), player);
		checkInstanceAchievement(Achievement.explosiveAlone(), player);
		checkInstanceAchievement(Achievement.explosiveBalance(), player);
		checkInstanceAchievement(Achievement.explosiveKills(), player);
		checkInstanceAchievement(Achievement.explosiveWave(), player);
		checkInstanceAchievement(Achievement.featherweightAlone(), player);
		checkInstanceAchievement(Achievement.featherweightBalance(), player);
		checkInstanceAchievement(Achievement.featherweightKills(), player);
		checkInstanceAchievement(Achievement.featherweightWave(), player);
		checkInstanceAchievement(Achievement.nakedAlone(), player);
		checkInstanceAchievement(Achievement.nakedBalance(), player);
		checkInstanceAchievement(Achievement.nakedKills(), player);
		checkInstanceAchievement(Achievement.nakedWave(), player);
		checkInstanceAchievement(Achievement.pacifistAlone(), player);
		checkInstanceAchievement(Achievement.pacifistBalance(), player);
		checkInstanceAchievement(Achievement.pacifistKills(), player);
		checkInstanceAchievement(Achievement.pacifistWave(), player);
		checkInstanceAchievement(Achievement.pacifistUhc(), player);
		checkInstanceAchievement(Achievement.uhcAlone(), player);
		checkInstanceAchievement(Achievement.uhcBalance(), player);
		checkInstanceAchievement(Achievement.uhcKills(), player);
		checkInstanceAchievement(Achievement.uhcWave(), player);
	}

	public static void checkDefaultKitAchievements(Player player) {
		checkKitAchievement(Achievement.allAbility(), player);
		checkKitAchievement(Achievement.allEffect(), player);
		checkKitAchievement(Achievement.allGift(), player);
		checkKitAchievement(Achievement.allKits(), player);
		checkKitAchievement(Achievement.allMaxedAbility(), player);
		checkKitAchievement(Achievement.maxedAbility(), player);
	}
}
