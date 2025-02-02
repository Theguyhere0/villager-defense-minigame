package me.theguyhere.villagerdefense.nms.v1_19_r1;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
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
                new SpawnEntityPacket(armorStandID, Registry.ENTITY_TYPE.getId(EntityType.ARMOR_STAND), location),
                EntityMetadataPacket.builder(armorStandID)
                        .setArmorStandMarker()
                        .setCustomName(text)
                        .build()
        );
    }
}
