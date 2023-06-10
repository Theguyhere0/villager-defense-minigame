package me.theguyhere.villagerdefense.nms.v1_16_r3;

import io.netty.handler.codec.EncoderException;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.DataWatcherSerializer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.VillagerData;

import java.util.Optional;

/**
 * A class to simplify interactions with serializing and managing DataWatchers.
 * <p>
 * This class was borrowed from filoghost to learn about using DataWatchers.
 *
 * @param <T>
 */
class DataWatcherKey<T> {
	private static final DataWatcherSerializer<Byte> BYTE_SERIALIZER = DataWatcherRegistry.a;
	private static final DataWatcherSerializer<Boolean> BOOLEAN_SERIALIZER = DataWatcherRegistry.i;
	private static final DataWatcherSerializer<Optional<IChatBaseComponent>> OPTIONAL_CHAT_COMPONENT_SERIALIZER =
		DataWatcherRegistry.f;
	private static final DataWatcherSerializer<VillagerData> VILLAGER_DATA_SERIALIZER = DataWatcherRegistry.q;

	static final DataWatcherKey<Byte> ENTITY_STATUS = new DataWatcherKey<>(0, BYTE_SERIALIZER);
	static final DataWatcherKey<Optional<IChatBaseComponent>> CUSTOM_NAME = new DataWatcherKey<>(
		2,
		OPTIONAL_CHAT_COMPONENT_SERIALIZER
	);
	static final DataWatcherKey<Boolean> CUSTOM_NAME_VISIBILITY = new DataWatcherKey<>(3, BOOLEAN_SERIALIZER);
	static final DataWatcherKey<Byte> ARMOR_STAND_STATUS = new DataWatcherKey<>(14, BYTE_SERIALIZER);
	static final DataWatcherKey<VillagerData> VILLAGER_DATA = new DataWatcherKey<>(17, VILLAGER_DATA_SERIALIZER);

	private final int index;
	private final DataWatcherSerializer<T> serializer;
	private final int serializerTypeID;

	private DataWatcherKey(int index, DataWatcherSerializer<T> serializer) {
		this.index = index;
		this.serializer = serializer;
		this.serializerTypeID = DataWatcherRegistry.b(serializer);
		if (serializerTypeID < 0) {
			throw new EncoderException("Could not find serializer ID of " + serializer);
		}
	}

	int getIndex() {
		return index;
	}

	DataWatcherSerializer<T> getSerializer() {
		return serializer;
	}

	int getSerializerTypeID() {
		return serializerTypeID;
	}
}
