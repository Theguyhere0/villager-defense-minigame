package me.theguyhere.villagerdefense.nms.common;

import org.bukkit.entity.Player;

public interface PacketListener {

    void onAttack(Player player, int entityID);

    void onInteractMain(Player player, int entityID);

    void onSignUpdate(Player player, String[] signLines);
}
