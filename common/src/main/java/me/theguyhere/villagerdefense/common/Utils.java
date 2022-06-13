package me.theguyhere.villagerdefense.common;

import java.lang.reflect.Field;
import java.util.Collection;

public class Utils {
    private static final int SECONDS_TO_TICKS = 20;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int SECONDS_TO_MILLIS = 1000;

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

    public static int angleToByte(float angle) {
        return (int) (angle / 360f * 256);
    }

    /**
     * Read reflection.
     *
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
     * Write reflection.
     *
     * @param instance Object to perform reflection on.
     * @param name Name of field.
     * @param newValue New value to write to the field.
     */
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
     *
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
