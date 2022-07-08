package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.LeftClickNPCEvent;
import me.theguyhere.villagerdefense.plugin.events.RightClickNPCEvent;
import me.theguyhere.villagerdefense.plugin.events.SignGUIEvent;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.displays.Portal;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PacketListenerImp implements PacketListener {
    @Override
    public void onAttack(Player player, int entityID) {
        GameManager.getArenas().values().stream().filter(Objects::nonNull).map(Arena::getPortal)
                .filter(Objects::nonNull).map(Portal::getNpc).filter(Objects::nonNull).forEach(npc -> {
                    int npcId = npc.getEntityID();
                    if (npcId == entityID)
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                                        Bukkit.getPluginManager().callEvent(new LeftClickNPCEvent(player, npcId)));
        });
    }

    @Override
    public void onInteractMain(Player player, int entityID) {
        GameManager.getArenas().values().stream().filter(Objects::nonNull).map(Arena::getPortal)
                .filter(Objects::nonNull).map(Portal::getNpc).filter(Objects::nonNull).forEach(npc -> {
                    int npcId = npc.getEntityID();
                    if (npcId == entityID)
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                                Bukkit.getPluginManager().callEvent(new RightClickNPCEvent(player, npcId)));
                });
    }

    @Override
    public void onSignUpdate(Player player, String[] signLines) {
        Arena arena;
        String header = signLines[0];

        try {
            arena = GameManager.getArena(Integer.parseInt(header.substring(18, header.length() - 4)));
        } catch (ArenaNotFoundException | NumberFormatException | IndexOutOfBoundsException e) {
            return;
        }

        // Check for right sign GUI
        if (!(signLines[1].contains(CommunicationManager.format("&1===============")) &&
                signLines[3].contains(CommunicationManager.format("&1==============="))))
            return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                Bukkit.getPluginManager().callEvent(new SignGUIEvent(arena, player, signLines)));
    }
}
