package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandPlayerException;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Executes command to reload plugin data.
 */
class CommandReloadPlugin {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.RELOAD.getArg()))
            return;
        CommandGuard.checkSenderPermissions(sender, Permission.ADMIN);

        // Try to get a UUID
        Player player;
        UUID uuid;
        try {
            player = CommandGuard.checkSenderPlayer(sender);
            uuid = player.getUniqueId();
        } catch (CommandPlayerException e) {
            player = null;
            uuid = null;
        }

        // Safeguard
        if (!CommandExecImp.reload.containsKey(uuid) || CommandExecImp.reload.get(uuid) < System.currentTimeMillis()) {
            // Notify of safeguard measures
            if (player != null)
                PlayerManager.notifyAlert(player, "Are you sure you want to reload the plugin? " +
                        "Re-send the command within 10 seconds to confirm.");
            else CommunicationManager.debugInfo("Are you sure you want to reload the plugin? " +
                    "Re-send the command within 10 seconds to confirm.", 0);

            // Keep track of trigger
            CommandExecImp.reload.put(uuid, System.currentTimeMillis() + Utils.secondsToMillis(10));

            return;
        }

        // Notify of reload
        if (player != null)
            PlayerManager.notifyAlert(player, "Reloading plugin data in 5 seconds");
        else CommunicationManager.debugInfo("Reloading plugin data in 5 seconds", 0);

        // Close all arenas
        GameManager.closeArenas();

        // Reload plugin after 11 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Main.plugin.reload(),
                Utils.secondsToTicks(5));
    }
}