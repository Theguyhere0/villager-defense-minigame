package me.theguyhere.villagerdefense.common;

import java.lang.reflect.Field;
import java.util.Collection;

public class Utils {
    private static final int SECONDS_TO_TICKS = 20;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int SECONDS_TO_MILLIS = 1000;

    public static final int LORE_CHAR_LIMIT = 30;
    public static final String HP = "\u2764";
    public static final String ARMOR = "\u2720";
    public static final String TOUGH = "\u2756";
    public static final String DAMAGE = "\u2694";
    public static final String GEM = "\u2666";
    public static final String EXP = "\u2605";
    public static final String HP_BAR = "\u258c";
    public static final String BLOCK = "\u25a0";
    public static final String ARROW = "\u27b6";

    // Convert seconds to ticks
    public static int secondsToTicks(double seconds) {
        return (int) (seconds * SECONDS_TO_TICKS);
    }

    // Convert minutes to seconds
    public static int minutesToSeconds(double minutes) {
        return (int) (minutes * MINUTES_TO_SECONDS);
    }

    // Convert seconds to milliseconds
    public static int secondsToMillis(double seconds) {
        return (int) (seconds * SECONDS_TO_MILLIS);
    }

    // Convert milliseconds to seconds
    public static double millisToSeconds(double millis) {
        return millis / SECONDS_TO_MILLIS;
    }

    // Convert degrees to byte
    public static int degreesToByte(float angle) {
        return (int) (angle / 360f * 256);
    }

    /**
     * Read reflection.
     * @param instance Object to perform reflection on.
     * @param name Name of field.
     * @return Field value
     */
    public static Object getFieldValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Read reflection.
     * @param instance Object to perform reflection on.
     * @param name Name of field.
     * @param t Class to cast and return as.
     * @return Field value
     */
    public static <T> T getFieldValue(Object instance, String name, Class<T> t) {
        T result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            result = t.cast(field.get(instance));

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Write reflection.
     * @param instance Object to perform reflection on.
     * @param name Name of field.
     * @param newValue New value to write to the field.
     */
    @SuppressWarnings("unused")
    public static void setFieldValue(Object instance, String name, Object newValue) {
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            field.set(instance, newValue);

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the next smallest whole number based on some existing whole numbers.
     * @param existingWholes Existing whole numbers
     * @return Next smallest unique whole number
     */
    public static int nextSmallestUniqueWhole(Collection<Integer> existingWholes) {
        for (int i = 0; i <= existingWholes.size(); i++) {
            if (!existingWholes.contains(i))
                return i;
        }
        // Should never reach here
        return 0;
    }
}
