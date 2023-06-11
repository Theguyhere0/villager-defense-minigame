package me.theguyhere.villagerdefense.plugin.huds;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * A class to manage the sidebars of each player.
 */
public class SidebarManager {
	private static final String ACTIVE_PLAYER = "VDActivePlayerSidebar";

	public static void updateActivePlayerSidebar(VDPlayer player) {
		Scoreboard board = player
			.getPlayer()
			.getScoreboard();
		Arena arena = player.getArena();

		// Recreate sidebar
		removeSidebar(player.getPlayer());
		Objective obj = board.registerNewObjective(ACTIVE_PLAYER, Criteria.DUMMY,
			CommunicationManager.format("&6&l   " + arena.getName() + "  ")
		);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score13 = obj.getScore(CommunicationManager.format("&e" + LanguageManager.messages.wave + ": " +
			arena.getCurrentWave()));
		score13.setScore(13);

		Score score12 = obj.getScore(CommunicationManager.format("&a" + LanguageManager.messages.gems + ": " +
			(player.getGems() + (arena.getStatus() == ArenaStatus.WAITING ? player.getGemBoost() : 0))));
		score12.setScore(12);

		StringBuilder kit = new StringBuilder(player
			.getKit()
			.getName());
		if (player
			.getKit()
			.isMultiLevel()) {
			kit.append(" ");
			for (int i = 0; i < Math.max(0, player
				.getKit()
				.getLevel()); i++)
				kit.append("I");
		}
		Score score11 = obj.getScore(CommunicationManager.format("&b" + LanguageManager.messages.kit + ": " +
			kit));
		score11.setScore(11);

		int bonus = 0;
		for (Challenge challenge : player.getChallenges())
			bonus += challenge.getBonus();
		Score score9 = obj.getScore(CommunicationManager.format(String.format("&5" +
			LanguageManager.messages.challenges + ": (+%d%%)", bonus)));
		score9.setScore(9);

		if (player
			.getChallenges()
			.size() < 4)
			for (Challenge challenge : player.getChallenges()) {
				Score score8 = obj.getScore(CommunicationManager.format("  &5" + challenge.getName()));
				score8.setScore(8);
			}
		else {
			StringBuilder challenges = new StringBuilder();
			for (Challenge challenge : player.getChallenges())
				challenges.append(challenge
					.getName()
					.toCharArray()[0]);
			Score score8 = obj.getScore(CommunicationManager.format("  &5" + challenges));
			score8.setScore(8);
		}

		Score score7 = obj.getScore("");
		score7.setScore(7);

		Score score6 = obj.getScore(CommunicationManager.format("&d" + LanguageManager.messages.players + ": " +
			arena.getAlive()));
		score6.setScore(6);

		Score score5 = obj.getScore(LanguageManager.messages.ghosts + ": " + arena.getGhostCount());
		score5.setScore(5);

		Score score4 = obj.getScore(CommunicationManager.format("&7" + LanguageManager.messages.spectators +
			": " + arena.getSpectatorCount()));
		score4.setScore(4);

		Score score3 = obj.getScore(" ");
		score3.setScore(3);

		Score score2 = obj.getScore(CommunicationManager.format("&2" + LanguageManager.messages.villagers + ": " +
			arena.getVillagers()));
		score2.setScore(2);

		Score score1 = obj.getScore(CommunicationManager.format("&c" + LanguageManager.messages.enemies + ": " +
			arena.getEnemies()));
		score1.setScore(1);

		Score score = obj.getScore(CommunicationManager.format("&4" + LanguageManager.messages.kills + ": " +
			player.getKills()));
		score.setScore(0);
	}

	public static void removeSidebar(Player player) {
		Scoreboard board = player.getScoreboard();
		Objective sidebar = board.getObjective(DisplaySlot.SIDEBAR);

		// Remove if it exists
		if (sidebar != null)
			sidebar.unregister();
	}
}
