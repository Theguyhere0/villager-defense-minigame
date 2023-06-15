package me.theguyhere.villagerdefense.plugin.background.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.displays.NPCLeftClickEvent;
import me.theguyhere.villagerdefense.plugin.displays.NPCRightClickEvent;
import me.theguyhere.villagerdefense.plugin.displays.Portal;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.guis.SignGUIEvent;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PacketManager {
	public static void onAttack(Player player, int entityID) {
		GameController
			.getArenas()
			.values()
			.stream()
			.filter(Objects::nonNull)
			.map(Arena::getPortal)
			.filter(Objects::nonNull)
			.map(Portal::getNpc)
			.filter(Objects::nonNull)
			.forEach(npc -> {
				int npcId = npc.getEntityID();
				if (npcId == entityID)
					Bukkit
						.getScheduler()
						.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
							Bukkit
								.getPluginManager()
								.callEvent(new NPCLeftClickEvent(player, npcId)));
			});
	}

	public static void onInteractMain(Player player, int entityID) {
		GameController
			.getArenas()
			.values()
			.stream()
			.filter(Objects::nonNull)
			.map(Arena::getPortal)
			.filter(Objects::nonNull)
			.map(Portal::getNpc)
			.filter(Objects::nonNull)
			.forEach(npc -> {
				int npcId = npc.getEntityID();
				if (npcId == entityID)
					Bukkit
						.getScheduler()
						.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
							Bukkit
								.getPluginManager()
								.callEvent(new NPCRightClickEvent(player, npcId)));
			});
	}

	public static void onSignUpdate(Player player, String[] signLines) {
		Arena arena;
		String header = signLines[0];

		try {
			arena = GameController.getArena(Integer.parseInt(header.substring(18, header.length() - 4)));
		}
		catch (ArenaNotFoundException | NumberFormatException | IndexOutOfBoundsException e) {
			return;
		}

		// Check for right sign GUI
		if (!(signLines[1].contains(CommunicationManager.format("&1===============")) &&
			signLines[3].contains(CommunicationManager.format("&1==============="))))
			return;

		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
				Bukkit
					.getPluginManager()
					.callEvent(new SignGUIEvent(arena, player, signLines)));
	}

	public static boolean checkInvisible(ItemStack itemStack) {
		if (itemStack == null || !itemStack.hasItemMeta())
			return false;
		PersistentDataContainer dataContainer = Objects
			.requireNonNull(itemStack.getItemMeta())
			.getPersistentDataContainer();
		Boolean invisible = dataContainer.get(VDItem.INVISIBLE, PersistentDataType.BOOLEAN);
		if (invisible == null)
			return false;
		else return invisible;
	}

	public static void createBorderEffect(Player player, Location location, double healthRatio) {
		// Protect from invalid health ratios
		if (healthRatio > 1 || healthRatio < 0)
			return;

		PacketContainer packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_CENTER);
		packetContainer
			.getDoubles()
			.writeSafely(0, location.getX())
			.writeSafely(1, location.getZ());
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
		packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_SIZE);
		packetContainer
			.getDoubles()
			.writeSafely(0, (double) Constants.BORDER_SIZE);
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
		packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);
		packetContainer
			.getIntegers()
			.writeSafely(0, Math.max((int) (Constants.BORDER_SIZE * (4 - 7 * healthRatio)), 0));
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
	}

	public static void resetBorderEffect(Player player, WorldBorder border) {
		PacketContainer packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_CENTER);
		packetContainer
			.getDoubles()
			.writeSafely(0, border
				.getCenter()
				.getX())
			.writeSafely(1, border
				.getCenter()
				.getZ());
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
		packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_SIZE);
		packetContainer
			.getDoubles()
			.writeSafely(0, border.getSize());
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
		packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);
		packetContainer
			.getIntegers()
			.writeSafely(0, border.getWarningDistance());
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
	}

	public static void refreshPlayerEquipmentForOnline(Player player) {
		PacketContainer packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

		// Set entity id
		packetContainer
			.getIntegers()
			.writeSafely(0, player.getEntityId());

		// Read in equipment
		List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = new ArrayList<>();
		pairList.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, player
			.getInventory()
			.getItemInMainHand()));
		pairList.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, player
			.getInventory()
			.getItemInOffHand()));
		pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, player
			.getInventory()
			.getHelmet()));
		pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, player
			.getInventory()
			.getChestplate()));
		pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, player
			.getInventory()
			.getLeggings()));
		pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, player
			.getInventory()
			.getBoots()));

		List<Pair<EnumWrappers.ItemSlot, ItemStack>> newPairList = new ArrayList<>();
		// Remove any invisible items
		pairList.forEach(pair -> {
			if (PacketManager.checkInvisible(pair.getSecond()))
				newPairList.add(new Pair<>(pair.getFirst(), null));
			else newPairList.add(new Pair<>(pair.getFirst(), pair.getSecond()));
		});

		packetContainer
			.getSlotStackPairLists()
			.writeSafely(0, newPairList);

		// Only send to online players in the same world
		Bukkit
			.getOnlinePlayers()
			.forEach(otherPlayer -> {
				if (Objects.equals(otherPlayer
					.getLocation()
					.getWorld(), player
					.getLocation()
					.getWorld()))
					Main
						.getProtocolManager()
						.sendServerPacket(otherPlayer, packetContainer);
			});
	}
}
