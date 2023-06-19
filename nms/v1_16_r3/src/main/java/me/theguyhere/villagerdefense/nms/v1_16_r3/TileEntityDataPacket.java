package me.theguyhere.villagerdefense.nms.v1_16_r3;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutTileEntityData;

public class TileEntityDataPacket extends VersionNMSPacket {
	private final Packet<?> rawPacket;

	TileEntityDataPacket(BlockPosition position, int type, NBTTagCompound nbtTagCompound) {
		PacketSetter packetSetter = PacketSetter.get();

		// Block entity data
		packetSetter.writePosition(position);
		packetSetter.writeVarInt(type);
		packetSetter.writeNBTTagCompound(nbtTagCompound);

		rawPacket = writeData(new PacketPlayOutTileEntityData(), packetSetter);
	}

	@Override
	public Packet<?> getRawPacket() {
		return rawPacket;
	}
}
