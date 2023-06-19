package me.theguyhere.villagerdefense.nms.common;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PacketListener {

	void onAttack(Player player, int entityID);

	void onInteractMain(Player player, int entityID);

	void onSignUpdate(Player player, String[] signLines);

	boolean checkInvisible(ItemStack itemStack);
}
