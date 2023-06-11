package me.theguyhere.villagerdefense.plugin.achievements;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AchievementListener implements Listener {
	@EventHandler
	public void onAchievementGetEvent(AchievementEvent e) {
		Player player = e.getPlayer();
		Achievement achievement = e.getAchievement();

		// Record achievement
		PlayerManager.addAchievement(player.getUniqueId(), achievement.getID());

		// Notify player of achievement
		PlayerManager.notifySuccess(
			player,
			LanguageManager.confirms.achievement,
			new ColoredMessage(ChatColor.AQUA, achievement.getName())
		);
		player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);

		// Check for crystal reward
		if (achievement
			.getReward()
			.getType() == AchievementReward.Type.CRYSTAL) {
			int reward;

			// Add crystals
			if (Main.hasCustomEconomy())
				reward = (int) (achievement
					.getReward()
					.getValue() *
					Main.plugin
						.getConfig()
						.getDouble("vaultEconomyMult"));
			else reward = achievement
				.getReward()
				.getValue();
			PlayerManager.depositCrystalBalance(player.getUniqueId(), reward);
			PlayerManager.notifySuccess(
				player,
				LanguageManager.confirms.crystalAdd,
				new ColoredMessage(ChatColor.AQUA, Integer.toString(reward)),
				new ColoredMessage(ChatColor.AQUA, reward > 1 ? LanguageManager.names.crystals :
					LanguageManager.names.crystal)
			);
		}

		else if (achievement
			.getReward()
			.getType() == AchievementReward.Type.BOOST)
			// Notify of boost
			PlayerManager.notifySuccess(player, LanguageManager.confirms.boostAdd);
	}
}
