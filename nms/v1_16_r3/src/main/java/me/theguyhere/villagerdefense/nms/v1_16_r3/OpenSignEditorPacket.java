package me.theguyhere.villagerdefense.nms.v1_16_r3;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenSignEditor;

public class OpenSignEditorPacket extends VersionNMSPacket {
	private final Packet<?> rawPacket;

	OpenSignEditorPacket(BlockPosition position) {
		PacketSetter packetSetter = PacketSetter.get();

		// Sign location
		packetSetter.writePosition(position);

		rawPacket = writeData(new PacketPlayOutOpenSignEditor(), packetSetter);
	}

	@Override
	public Packet<?> getRawPacket() {
		return rawPacket;
	}
}
