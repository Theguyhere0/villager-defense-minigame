package me.theguyhere.villagerdefense.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;

import java.util.Collections;
import java.util.List;

public class WrapperPlayServerEntityDestroy extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    public WrapperPlayServerEntityDestroy() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityDestroy(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Set the entities that will be destroyed.
     * @param entities - new value.
     */
    public void setEntities(int[] entities) {
        handle.getIntegerArrays().write(0, entities);
    }

    /**
     * Set the entities that will be destroyed.
     * @param entities - new value.
     */
    public void setEntities(List<Integer> entities) {
        setEntities(Ints.toArray(entities));
    }

    public void setEntity_1_17(Integer entity) {
        if (handle.getIntegers().size() > 0) {
            handle.getIntegers().write(0, entity);
        } else {
            handle.getIntLists().write(0, Collections.singletonList(entity));
        }
    }
}
