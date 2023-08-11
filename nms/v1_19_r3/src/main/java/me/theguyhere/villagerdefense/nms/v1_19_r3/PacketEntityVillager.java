package me.theguyhere.villagerdefense.nms.v1_19_r3;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import org.bukkit.Location;

/**
 * A villager entity constructed out of packets.
 */
class PacketEntityVillager implements VillagerPacketEntity {
    private final EntityID villagerID;

    PacketEntityVillager(EntityID villagerID) {
        this.villagerID = villagerID;
    }

    @Override
    public PacketGroup newDestroyPackets() {
        return new EntityDestroyPacket(villagerID);
    }

    @Override
    public PacketGroup newSpawnPackets(Location location) {
        return PacketGroup.of(
                new SpawnEntityPacket(villagerID, EntityTypeID.VILLAGER, location, location.getPitch()),
                new EntityHeadRotationPacket(villagerID, location.getYaw())
        );
    }

    @Override
    public int getEntityID() {
        return villagerID.getNumericID();
    }
}
