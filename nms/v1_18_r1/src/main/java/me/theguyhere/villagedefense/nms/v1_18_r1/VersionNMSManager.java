package me.theguyhere.villagedefense.nms.v1_18_r1;

import me.theguyhere.villagedefense.nms.common.EntityID;
import me.theguyhere.villagedefense.nms.common.NMSManager;
import me.theguyhere.villagedefense.nms.common.TextPacketEntity;
import me.theguyhere.villagedefense.nms.common.VillagerPacketEntity;

/**
 * Manager class for a specific NMS version.
 */
public class VersionNMSManager implements NMSManager {
    public VersionNMSManager() {
    }

    @Override
    public TextPacketEntity newTextPacketEntity() {
        return new PacketEntityArmorStand(new EntityID());
    }

    @Override
    public VillagerPacketEntity newVillagerPacketEntity() {
        return new PacketEntityVillager(new EntityID());
    }
}
