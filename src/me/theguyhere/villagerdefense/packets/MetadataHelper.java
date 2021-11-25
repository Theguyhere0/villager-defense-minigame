package me.theguyhere.villagerdefense.packets;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import me.theguyhere.villagerdefense.tools.NMSVersion;

import java.util.List;
import java.util.Optional;

public class MetadataHelper {
    private static Serializer itemSerializer;
    private static Serializer intSerializer;
    private static Serializer byteSerializer;
    private static Serializer stringSerializer;
    private static Serializer booleanSerializer;
    private static Serializer chatComponentSerializer;

    private static int itemSlotIndex;
    private static int entityStatusIndex;
    private static int airLevelWatcherIndex;
    private static int customNameIndex;
    private static int customNameVisibleIndex;
    private static int noGravityIndex;
    private static int armorStandStatusIndex;
    private static int slimeSizeIndex;

    public static void init() {
        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_17_R1)) {
            itemSlotIndex = 8;
        } else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_14_R1)) {
            itemSlotIndex = 7;
        } else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_10_R1)) {
            itemSlotIndex = 6;
        } else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_9_R1)) {
            itemSlotIndex = 5;
        } else {
            itemSlotIndex = 10;
        }

        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_17_R1)) {
            armorStandStatusIndex = 15;
        } else if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_15_R1)) {
            armorStandStatusIndex = 14;
        } else {
            armorStandStatusIndex = 11;
        }

        entityStatusIndex = 0;
        airLevelWatcherIndex = 1;
        customNameIndex = 2;
        customNameVisibleIndex = 3;
        noGravityIndex = 5;
        slimeSizeIndex = 15;

        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_9_R1)) {
            itemSerializer = Registry.get(MinecraftReflection.getItemStackClass());
            intSerializer = Registry.get(Integer.class);
            byteSerializer = Registry.get(Byte.class);
            stringSerializer = Registry.get(String.class);
            booleanSerializer = Registry.get(Boolean.class);
        }

        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_13_R1)) {
            chatComponentSerializer = Registry.get(MinecraftReflection.getIChatBaseComponentClass(), true);
        }
    }

    public static void setEntityStatus(WrappedDataWatcher dataWatcher, byte statusBitmask) {
        requireMinimumVersion(NMSVersion.v1_9_R1);
        dataWatcher.setObject(new WrappedDataWatcherObject(entityStatusIndex, byteSerializer), statusBitmask);
    }


    public static WrappedWatchableObject getCustomNameWatchableObject(WrappedDataWatcher metadata) {
        return metadata.getWatchableObject(customNameIndex);
    }


    public static WrappedWatchableObject getCustomNameWatchableObject(List<WrappedWatchableObject> dataWatcherValues) {
        for (WrappedWatchableObject watchableObject : dataWatcherValues) {
            if (watchableObject.getIndex() == customNameIndex) {
                return watchableObject;
            }
        }

        return null;
    }


    public static Object getCustomNameNMSObject(WrappedWatchableObject customNameWatchableObject) {
        Object customNameNMSObject = customNameWatchableObject.getRawValue();
        if (customNameNMSObject == null) {
            return null;
        }

        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_13_R1)) {
            if (!(customNameNMSObject instanceof Optional)) {
                throw new IllegalArgumentException("Expected custom name of type " + Optional.class);
            }

            return ((Optional<?>) customNameNMSObject).orElse(null);

        } else {
            if (!(customNameNMSObject instanceof String)) {
                throw new IllegalArgumentException("Expected custom name of type " + String.class);
            }

            return customNameNMSObject;
        }
    }


    public static void setCustomNameNMSObject(WrappedWatchableObject customNameWatchableObject, Object customNameNMSObject) {
        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_13_R1)) {
            customNameWatchableObject.setValue(Optional.ofNullable(customNameNMSObject));
        } else {
            customNameWatchableObject.setValue(customNameNMSObject);
        }
    }


    public static void setCustomNameNMSObject(WrappedDataWatcher dataWatcher, Object customNameNMSObject) {
        requireMinimumVersion(NMSVersion.v1_9_R1);

        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_13_R1)) {
            dataWatcher.setObject(new WrappedDataWatcherObject(customNameIndex, chatComponentSerializer), Optional.ofNullable(customNameNMSObject));
        } else {
            dataWatcher.setObject(new WrappedDataWatcherObject(customNameIndex, stringSerializer), customNameNMSObject);
        }
    }


    public static void setCustomNameVisible(WrappedDataWatcher dataWatcher, boolean customNameVisible) {
        requireMinimumVersion(NMSVersion.v1_9_R1);
        dataWatcher.setObject(new WrappedDataWatcherObject(customNameVisibleIndex, booleanSerializer), customNameVisible);
    }


    public static void setNoGravity(WrappedDataWatcher dataWatcher, boolean noGravity) {
        requireMinimumVersion(NMSVersion.v1_9_R1);
        dataWatcher.setObject(new WrappedDataWatcherObject(noGravityIndex, booleanSerializer), noGravity);
    }


    public static void setArmorStandStatus(WrappedDataWatcher dataWatcher, byte statusBitmask) {
        requireMinimumVersion(NMSVersion.v1_9_R1);
        dataWatcher.setObject(new WrappedDataWatcherObject(armorStandStatusIndex, byteSerializer), statusBitmask);
    }


    public static void setItemMetadata(WrappedDataWatcher dataWatcher, Object nmsItemStack) {
        if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_9_R1)) {
            if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_11_R1)) {
                dataWatcher.setObject(new WrappedDataWatcherObject(itemSlotIndex, itemSerializer), nmsItemStack);
            } else {
                dataWatcher.setObject(new WrappedDataWatcherObject(itemSlotIndex, itemSerializer), Optional.of(nmsItemStack));
            }
            dataWatcher.setObject(new WrappedDataWatcherObject(airLevelWatcherIndex, intSerializer), 300);
            dataWatcher.setObject(new WrappedDataWatcherObject(entityStatusIndex, byteSerializer), (byte) 0);
        } else {
            dataWatcher.setObject(itemSlotIndex, nmsItemStack);
            dataWatcher.setObject(airLevelWatcherIndex, 300);
            dataWatcher.setObject(entityStatusIndex, (byte) 0);
        }
    }


    public static void setSlimeSize(WrappedDataWatcher dataWatcher, int size) {
        requireMinimumVersion(NMSVersion.v1_15_R1);
        dataWatcher.setObject(new WrappedDataWatcherObject(slimeSizeIndex, intSerializer), size);
    }


    private static void requireMinimumVersion(NMSVersion minimumVersion) {
        if (!NMSVersion.isGreaterEqualThan(minimumVersion)) {
            throw new UnsupportedOperationException("Method only available from NMS version " + minimumVersion);
        }
    }
}
