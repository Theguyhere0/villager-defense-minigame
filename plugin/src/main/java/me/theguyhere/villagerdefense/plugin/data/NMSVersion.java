package me.theguyhere.villagerdefense.plugin.data;

import lombok.Getter;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public enum NMSVersion {
    v1_16_R3(new me.theguyhere.villagerdefense.nms.v1_16_r3.VersionNMSManager()),
    v1_17_R1(new me.theguyhere.villagerdefense.nms.v1_17_r1.VersionNMSManager()),
    v1_18_R1(new me.theguyhere.villagerdefense.nms.v1_18_r1.VersionNMSManager()),
    v1_18_R2(new me.theguyhere.villagerdefense.nms.v1_18_r2.VersionNMSManager()),
    v1_19_R1(new me.theguyhere.villagerdefense.nms.v1_19_r1.VersionNMSManager()),
    v1_19_R2(new me.theguyhere.villagerdefense.nms.v1_19_r2.VersionNMSManager()),
    v1_19_R3(new me.theguyhere.villagerdefense.nms.v1_19_r3.VersionNMSManager()),
    v1_20_R1(new me.theguyhere.villagerdefense.nms.v1_20_r1.VersionNMSManager()),
    v1_20_R2(new me.theguyhere.villagerdefense.nms.v1_20_r2.VersionNMSManager()),
    v1_20_R3(new me.theguyhere.villagerdefense.nms.v1_20_r3.VersionNMSManager()),
    v1_20_R4(new me.theguyhere.villagerdefense.nms.v1_20_r4.VersionNMSManager()),
    v1_21_R1(new me.theguyhere.villagerdefense.nms.v1_21_r1.VersionNMSManager()),
    v1_21_R2(new me.theguyhere.villagerdefense.nms.v1_21_r2.VersionNMSManager()),
    v1_21_R3(new me.theguyhere.villagerdefense.nms.v1_21_r3.VersionNMSManager()),
    v1_21_R4(new me.theguyhere.villagerdefense.nms.v1_21_r4.VersionNMSManager()),
    ;

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

    public static boolean isGreaterEqualThan(NMSVersion other) {
        return getCurrent().ordinal() >= other.ordinal();
    }

    public static boolean isBetween(NMSVersion from, NMSVersion to) {
        return from.ordinal() <= getCurrent().ordinal() && getCurrent().ordinal() <= to.ordinal();
    }

    /**
     * This method uses a regex to get the NMS package part that changes with every update.
     * Example: v1_16_R3
     * @return the NMS package part or null if not found.
     */
    private static String extractNMSVersion() {
        String[] versionParts = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int majorVer = Integer.parseInt(versionParts[1]);
        int minorVer = 0;
        if (versionParts.length > 2) {
            minorVer = Integer.parseInt(versionParts[2]);
        }

        // If we're on 1.20.5 or higher, the server package may not be relocated on Paper
        if (majorVer == 20 && minorVer >= 5) {
            return "v1_20_R4";
        } else if (majorVer == 21) {
            switch (minorVer) {
                case 0:
                case 1:
                    return "v1_21_R1";
                case 2:
                case 3:
                    return "v1_21_R2";
                case 4:
                    return "v1_21_R3";
                case 5:
                    return "v1_21_R4";
            }
        }

        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }
}
