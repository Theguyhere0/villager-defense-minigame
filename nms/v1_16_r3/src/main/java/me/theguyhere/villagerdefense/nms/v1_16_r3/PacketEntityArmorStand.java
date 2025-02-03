package me.theguyhere.villagerdefense.nms.v1_16_r3;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.IRegistry;
import org.bukkit.Location;

/**
 * An armor stand entity constructed out of packets.
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
                new SpawnEntityLivingPacket(armorStandID, IRegistry.ENTITY_TYPE.a(EntityTypes.ARMOR_STAND), location),
                EntityMetadataPacket.builder(armorStandID)
                        .setArmorStandMarker()
                        .setCustomName(text)
                        .build()
        );
    }
}
