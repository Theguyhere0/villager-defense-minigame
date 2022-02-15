package me.theguyhere.villagedefense.nms.v1_18_r1;

import me.theguyhere.villagedefense.nms.common.EntityID;
import me.theguyhere.villagedefense.nms.common.PacketGroup;
import me.theguyhere.villagedefense.nms.common.TextPacketEntity;
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
                new SpawnEntityLivingPacket(armorStandID, EntityTypeID.ARMOR_STAND, location),
                EntityMetadataPacket.builder(armorStandID)
                        .setArmorStandMarker()
                        .setCustomName(text)
                        .build()
        );
    }
}
