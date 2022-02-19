package me.theguyhere.villagerdefense.plugin.tools;

import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.nms.v1_18_r1.VersionNMSManager;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum NMSVersion {
//    v1_8_R2,
//    v1_8_R3,
//    v1_9_R1,
//    v1_9_R2,
//    v1_10_R1,
//    v1_11_R1,
//    v1_12_R1,
//    v1_13_R1,
//    v1_13_R2,
//    v1_14_R1,
//    v1_15_R1,
//    v1_16_R1,
//    v1_16_R2,
//    v1_16_R3,
//    v1_17_R1,
    v1_18_R1(new VersionNMSManager());

    private static final NMSVersion CURRENT_VERSION = extractCurrentVersion();

    private final NMSManager nmsManager;

    NMSVersion(NMSManager nmsManager) {
        this.nmsManager = nmsManager;
    }

    private static NMSVersion extractCurrentVersion() {
        String nmsVersionName = extractNMSVersion();

        if (nmsVersionName != null) {
            try {
                return valueOf(nmsVersionName);
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean isValid() {
        return CURRENT_VERSION != null;
    }

    public static NMSVersion getCurrent() {
        if (CURRENT_VERSION == null) {
            throw new IllegalStateException("Current version not set");
        }
        return CURRENT_VERSION;
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }

    public static boolean isGreaterEqualThan(NMSVersion other) {
        return getCurrent().ordinal() >= other.ordinal();
    }

    public static boolean isBetween(NMSVersion from, NMSVersion to) {
        return from.ordinal() <= getCurrent().ordinal() && getCurrent().ordinal() <= to.ordinal();
    }

    /**
     * This method uses a regex to get the NMS package part that changes with every update.
     * Example: v1_13_R2
     * @return the NMS package part or null if not found.
     */
    private static String extractNMSVersion() {
        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }
}
