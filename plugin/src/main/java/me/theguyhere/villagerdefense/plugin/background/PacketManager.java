package me.theguyhere.villagerdefense.plugin.background;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.Main;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class PacketManager {
	public static void createBorderEffect(Player player, Location location, double healthRatio) {
		// Protect from invalid health ratios
		if (healthRatio > 1 || healthRatio < 0)
			return;

		PacketContainer packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_CENTER);
		packetContainer
			.getDoubles()
			.write(0, location.getX());
		packetContainer
			.getDoubles()
			.write(1, location.getZ());
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
		packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_SIZE);
		packetContainer
			.getDoubles()
			.write(0, (double) Constants.BORDER_SIZE);
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
		packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);
		packetContainer
			.getIntegers()
			.write(0, Math.max((int) (Constants.BORDER_SIZE * (4 - 7 * healthRatio)), 0));
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
			.write(0, border
				.getCenter()
				.getX());
		packetContainer
			.getDoubles()
			.write(1, border
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
			.write(0, border.getSize());
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
		packetContainer = Main
			.getProtocolManager()
			.createPacket(PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE);
		packetContainer
			.getIntegers()
			.write(0, border.getWarningDistance());
		Main
			.getProtocolManager()
			.sendServerPacket(player, packetContainer);
	}
}
