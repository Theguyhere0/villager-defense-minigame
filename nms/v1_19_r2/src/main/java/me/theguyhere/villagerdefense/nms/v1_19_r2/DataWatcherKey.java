package me.theguyhere.villagerdefense.nms.v1_19_r2;

import io.netty.handler.codec.EncoderException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.npc.VillagerData;

import java.util.Optional;

/**
 * A class to simplify interactions with serializers.
 * <p>
 * This class was borrowed from filoghost to learn about serializing.
 */
class DataWatcherKey<T> {
    private static final EntityDataSerializer<Byte> BYTE_SERIALIZER = EntityDataSerializers.BYTE;
    private static final EntityDataSerializer<Boolean> BOOLEAN_SERIALIZER = EntityDataSerializers.BOOLEAN;
    private static final EntityDataSerializer<Optional<Component>> OPTIONAL_CHAT_COMPONENT_SERIALIZER =
            EntityDataSerializers.OPTIONAL_COMPONENT;
    private static final EntityDataSerializer<VillagerData> VILLAGER_DATA_SERIALIZER =
            EntityDataSerializers.VILLAGER_DATA;

    static final DataWatcherKey<Byte> ENTITY_STATUS = new DataWatcherKey<>(0, BYTE_SERIALIZER);
    static final DataWatcherKey<Optional<Component>> CUSTOM_NAME = new DataWatcherKey<>(2,
            OPTIONAL_CHAT_COMPONENT_SERIALIZER);
    static final DataWatcherKey<Boolean> CUSTOM_NAME_VISIBILITY = new DataWatcherKey<>(3, BOOLEAN_SERIALIZER);
    static final DataWatcherKey<Byte> ARMOR_STAND_STATUS = new DataWatcherKey<>(15, BYTE_SERIALIZER);
    static final DataWatcherKey<VillagerData> VILLAGER_DATA = new DataWatcherKey<>(18, VILLAGER_DATA_SERIALIZER);

    private final int index;
    private final EntityDataSerializer<T> serializer;
    private final int serializerTypeID;

    private DataWatcherKey(int index, EntityDataSerializer<T> serializer) {
        this.index = index;
        this.serializer = serializer;
        this.serializerTypeID = EntityDataSerializers.getSerializedId(serializer);
        if (serializerTypeID < 0) {
            throw new EncoderException("Could not find serializer ID of " + serializer);
        }
    }

    int getIndex() {
        return index;
    }

    EntityDataSerializer<T> getSerializer() {
        return serializer;
    }

    int getSerializerTypeID() {
        return serializerTypeID;
    }
}
