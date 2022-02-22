package me.theguyhere.villagerdefense.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging class to standardize console messages from the plugin.
 */
public class Log {
    private static final Logger log = Logger.getLogger("Minecraft");

    public static void warning(String msg) {
        log.log(Level.WARNING,"[VillagerDefense] " + msg);
    }

    public static void info(String msg) {
        log.info("[VillagerDefense] " + msg);
    }
}
