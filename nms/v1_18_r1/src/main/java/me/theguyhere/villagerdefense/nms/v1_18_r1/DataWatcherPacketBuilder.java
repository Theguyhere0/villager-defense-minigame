package me.theguyhere.villagerdefense.nms.v1_18_r1;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;

import java.util.Optional;

/**
 * Class to help build DataWatchers.
 * <p>
 * This class was borrowed from filoghost.
 *
 * @param <T> Packet type.
 */
abstract class DataWatcherPacketBuilder<T> {
	private final PacketSetter packetSetter;

	DataWatcherPacketBuilder(PacketSetter packetSetter) {
		this.packetSetter = packetSetter;
	}

	DataWatcherPacketBuilder<T> setInvisible() {
		packetSetter.writeDataWatcherEntry(DataWatcherKey.ENTITY_STATUS, (byte) 0x20); // Invisible
		return this;
	}

	DataWatcherPacketBuilder<T> setArmorStandMarker() {
		packetSetter.writeDataWatcherEntry(
			DataWatcherKey.ARMOR_STAND_STATUS, (byte) (0x01 | 0x02 | 0x08 | 0x10)); // Small, no gravity, no
		// baseplate, marker
		return this;
	}

	DataWatcherPacketBuilder<T> setCustomName(String customName) {
		packetSetter.writeDataWatcherEntry(DataWatcherKey.CUSTOM_NAME, getCustomNameDataWatcherValue(customName));
		packetSetter.writeDataWatcherEntry(DataWatcherKey.CUSTOM_NAME_VISIBILITY, !Strings.isEmpty(customName));
		return this;
	}

	DataWatcherPacketBuilder<T> setVillagerType(String type) {
		packetSetter.writeDataWatcherEntry(DataWatcherKey.VILLAGER_DATA, getVillagerDataDataWatcherValue(type));
		return this;
	}

	private Optional<Component> getCustomNameDataWatcherValue(String customName) {
		if (customName.length() > 300)
			customName = customName.substring(0, 300);
		if (!Strings.isEmpty(customName)) {
			return Optional.of(CraftChatMessage.fromString(customName, false, true)[0]);
		}
		else {
			return Optional.empty();
		}
	}

	private VillagerData getVillagerDataDataWatcherValue(String type) {
		VillagerType villagerType;
		switch (type) {
			case "desert":
				villagerType = VillagerType.DESERT;
				break;
			case "jungle":
				villagerType = VillagerType.JUNGLE;
				break;
			case "savanna":
				villagerType = VillagerType.SAVANNA;
				break;
			case "snow":
				villagerType = VillagerType.SNOW;
				break;
			case "swamp":
				villagerType = VillagerType.SWAMP;
				break;
			case "taiga":
				villagerType = VillagerType.TAIGA;
				break;
			default:
				villagerType = VillagerType.PLAINS;
				break;
		}
		return new VillagerData(villagerType, null, 0);
	}

	T build() {
		packetSetter.writeDataWatcherEntriesEnd();
		return createPacket(packetSetter);
	}

	abstract T createPacket(PacketSetter packetSetter);
}
