package me.theguyhere.villagerdefense.common;

import org.bukkit.ChatColor;

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

    // Formats chat text
    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
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
