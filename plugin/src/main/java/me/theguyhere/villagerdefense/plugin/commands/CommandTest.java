package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.Ammo;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.Bow;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to test stuff.
 */
class CommandTest {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.TEST.getArg()))
            return;
        Player player = CommandGuard.checkSenderPlayer(sender);
        CommandGuard.checkSenderPermissions(player, Permission.ADMIN);
        CommandGuard.checkDebugLevelGreaterEqual(sender, 3);

        // Implement test
        PlayerManager.giveItem(player, Bow.create(Bow.BowType.T3), "whoops");
        PlayerManager.giveItem(player, Ammo.create(Ammo.AmmoType.T1), "whoops");

        // Confirm
        PlayerManager.notifySuccess(player, "Test Complete");
    }
}
