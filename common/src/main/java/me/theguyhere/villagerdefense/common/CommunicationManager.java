package me.theguyhere.villagerdefense.common;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommunicationManager {
    /**
     * The amount of debug information to display in the console.
     *
     * 3 (Developer) - All errors and information tracked will be displayed. Certain behavior will be overridden.
     * 2 (Verbose) - All errors and information tracked will be displayed.
     * 1 (Normal) - Errors that drastically reduce performance and important information will be displayed.
     * 0 (Quiet) - Only the most urgent error messages will be displayed.
     */
    private static int debugLevel = 0;

    public static int getDebugLevel() {
        return debugLevel;
    }

    public static void setDebugLevel(int newDebugLevel) {
        debugLevel = newDebugLevel;
    }

    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String format(@NotNull ChatColor base, String msg) {
        return base + msg;
    }

    public static String[] formatDescriptionArr(@NotNull ChatColor base, String description) {
        return formatDescriptionList(base, description).toArray(new String[0]);
    }

    public static List<String> formatDescriptionList(@NotNull ChatColor base, String description) {
        int charLimit = 30 + base.toString().length();
        String[] descArray = description.split(" ");
        List<String> descLines = new ArrayList<>();
        StringBuilder line = new StringBuilder(base.toString());

        for (String s : descArray) {
            // Always add to a line if empty or line remains under character limit
            if (line.length() == 0 || line.length() + s.length() <= charLimit)
                line.append(s).append(" ");

            // Start new line if next word makes line longer than character limit
            else {
                line.deleteCharAt(line.length() - 1);
                descLines.add(format(line.toString()));
                line = new StringBuilder(base.toString()).append(s).append(" ");
            }
        }

        // Add last line
        line.deleteCharAt(line.length() - 1);
        descLines.add(line.toString());

        return descLines;
    }

    public static String format(@NotNull ChatColor base, String msg, @NotNull ChatColor replace, String value) {
        return base + String.format(msg, replace + value + base);
    }

    public static String format(@NotNull ChatColor base, String msg, @NotNull ChatColor replace, String value1,
                                String value2) {
        return base + String.format(msg, replace + value1 + base, replace + value2 + base);
    }

    // Formats plugin notifications
    public static String notify(String msg) {
        return format("&2VD: &f" + msg);
    }

    public static void debugError(String msg, int debugLevel) {
        debugError(msg, debugLevel, false);
    }

    public static void debugError(String msg, int debugLevel, boolean stackTrace) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            Log.warning(msg);

            if (stackTrace)
                Thread.dumpStack();
        }
    }

    public static void debugError(String msg, int debugLevel, boolean stackTrace, Exception e) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            Log.warning(msg);

            if (stackTrace)
                e.printStackTrace();
        }
    }

    public static void debugInfo(String msg, int debugLevel) {
        debugInfo(msg, debugLevel, false);
    }

    public static void debugInfo(String msg, int debugLevel, boolean stackTrace) {
        if (CommunicationManager.debugLevel >= debugLevel) {
            Log.info(msg);

            if (stackTrace)
                Thread.dumpStack();
        }
    }
}
