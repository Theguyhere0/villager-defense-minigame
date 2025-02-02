package me.theguyhere.villagerdefense.nms.v1_19_r2;

import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;

import java.util.Optional;

/**
 * Class to help build DataWatchers.
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
        setInvisible();
        packetSetter.writeDataWatcherEntry(
            DataWatcherKey.ARMOR_STAND_STATUS, (byte) (0x01 | 0x02 | 0x08 | 0x10)); // Small, no gravity, no base plate, marker
        return this;
    }

    DataWatcherPacketBuilder<T> setCustomName(String customName) {
        packetSetter.writeDataWatcherEntry(DataWatcherKey.CUSTOM_NAME, getCustomNameDataWatcherValue(customName));
        packetSetter.writeDataWatcherEntry(DataWatcherKey.CUSTOM_NAME_VISIBILITY, !Strings.isEmpty(customName));
        return this;
    }

    private Optional<Component> getCustomNameDataWatcherValue(String customName) {
        if (customName.length() > 300)
            customName = customName.substring(0, 300);
        if (!Strings.isEmpty(customName)) {
            return Optional.of(CraftChatMessage.fromString(customName, false, true)[0]);
        } else {
            return Optional.empty();
        }
    }

    T build() {
        packetSetter.writeDataWatcherEntriesEnd();
        return createPacket(packetSetter);
    }

    abstract T createPacket(PacketSetter packetSetter);
}
