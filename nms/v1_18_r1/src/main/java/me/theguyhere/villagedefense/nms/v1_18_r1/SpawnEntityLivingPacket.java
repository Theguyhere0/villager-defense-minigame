package me.theguyhere.villagedefense.nms.v1_18_r1;

import me.theguyhere.villagedefense.nms.common.EntityID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;

/**
 * Packet class for spawning living entities.
 *
 * This class format was borrowed from filoghost.
 */
class SpawnEntityLivingPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    SpawnEntityLivingPacket(EntityID entityID, int entityTypeID, Location location) {
        this(entityID, entityTypeID, location, 0);
    }

    SpawnEntityLivingPacket(EntityID entityID, int entityTypeID, Location location, float headPitch) {
        PacketSetter packetSetter = PacketSetter.get();

        // Entity info
        packetSetter.writeVarInt(entityID.getNumericID());
        packetSetter.writeUUID(entityID.getUUID());
        packetSetter.writeVarInt(entityTypeID);

        // Position
        packetSetter.writeDouble(location.getX());
        packetSetter.writeDouble(location.getY());
        packetSetter.writeDouble(location.getZ());

        // Rotation
        packetSetter.writeByte((int) location.getYaw());
        packetSetter.writeByte((int) location.getPitch());

        // Head pitch
        packetSetter.writeByte((int) headPitch);

        // Velocity
        packetSetter.writeShort(0);
        packetSetter.writeShort(0);
        packetSetter.writeShort(0);

        this.rawPacket = new PacketPlayOutSpawnEntityLiving(packetSetter);
    }


    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
