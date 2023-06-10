package me.theguyhere.villagerdefense.nms.v1_18_r2;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public class TileEntityDataPacket extends VersionNMSPacket {
	private final Packet<?> rawPacket;

	TileEntityDataPacket(BlockPos position, int type, CompoundTag nbtTagCompound) {
		PacketSetter packetSetter = PacketSetter.get();

		// Block entity data
		packetSetter.writeBlockPos(position);
		packetSetter.writeVarInt(type);
		packetSetter.writeNbt(nbtTagCompound);

		rawPacket = new ClientboundBlockEntityDataPacket(packetSetter);
	}

	@Override
	public Packet<?> getRawPacket() {
		return rawPacket;
	}
}
