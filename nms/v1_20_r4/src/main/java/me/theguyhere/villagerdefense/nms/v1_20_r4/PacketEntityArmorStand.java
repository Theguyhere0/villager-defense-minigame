package me.theguyhere.villagerdefense.nms.v1_20_r4;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import org.bukkit.Location;

/**
 * An armor stand entity constructed out of packets.
 *
 * Class structure borrowed from filoghost.
 */
class PacketEntityArmorStand implements TextPacketEntity {
    private final EntityID armorStandID;

    PacketEntityArmorStand(EntityID armorStandID) {
        this.armorStandID = armorStandID;
    }

    @Override
    public PacketGroup newDestroyPackets() {
        return new EntityDestroyPacket(armorStandID);
    }

    @Override
    public PacketGroup newSpawnPackets(Location location, String text) {
        return PacketGroup.of(
                new SpawnEntityPacket(armorStandID, EntityTypeID.ARMOR_STAND, location),
                EntityMetadataPacket.builder(armorStandID)
                        .setArmorStandMarker()
                        .setCustomName(text)
                        .build()
        );
    }
}
