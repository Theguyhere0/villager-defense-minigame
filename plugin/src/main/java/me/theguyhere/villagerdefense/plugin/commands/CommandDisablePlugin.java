package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Executes command to disable the plugin.
 */
class CommandDisablePlugin {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.DISABLE.getArg()))
            return;
        CommandGuard.checkSenderPermissions(sender, CommandPermission.ADMIN);

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
        if (!CommandExecImp.disable.containsKey(uuid) ||
                CommandExecImp.disable.get(uuid) < System.currentTimeMillis()) {
            // Notify of safeguard measures
            if (player != null)
                PlayerManager.notifyAlert(player, "Are you sure you want to disable the plugin? " +
                        "Re-send the command within 10 seconds to confirm.");
            else CommunicationManager.debugInfo("Are you sure you want to disable the plugin? " +
                    "Re-send the command within 10 seconds to confirm.", 0);

            // Keep track of trigger
            CommandExecImp.disable.put(uuid, System.currentTimeMillis() + Utils.secondsToMillis(10));

            return;
        }

        // Notify of disable
        if (player != null)
            PlayerManager.notifyAlert(player, "Disabling the plugin");
        else CommunicationManager.debugInfo("Disabling the plugin", 0);

        Bukkit.getPluginManager().disablePlugin(Main.plugin);
    }
}
