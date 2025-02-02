package me.theguyhere.villagerdefense.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A class that holds reflection methods.
 */

@SuppressWarnings({"CallToPrintStackTrace", "unused"})
public class Reflections {
    /**
     * Read field reflection.
     *
     * @param instance Object to perform reflection on.
     * @param name     Name of field.
     * @return Field value
     */
    public static Object getFieldValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance
                .getClass()
                .getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Read field reflection for a field in superclass.
     *
     * @param instance Object to perform reflection on.
     * @param name     Name of field.
     * @return Field value
     */
    public static Object getSuperFieldValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance
                .getClass()
                .getSuperclass()
                .getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Read field reflection with specified return value.
     *
     * @param instance Object to perform reflection on.
     * @param name     Name of field.
     * @param t        Class to cast and return as.
     * @return Field value
     */
    public static <T> T getFieldValue(Object instance, String name, Class<T> t) {
        T result = null;

        try {
            Field field = instance
                .getClass()
                .getDeclaredField(name);
            field.setAccessible(true);

            result = t.cast(field.get(instance));

            field.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Write field reflection.
     *
     * @param instance Object to perform reflection on.
     * @param name     Name of field.
     * @param newValue New value to write to the field.
     */
    public static void setFieldValue(Object instance, String name, Object newValue) {
        try {
            Field field = instance
                .getClass()
                .getDeclaredField(name);
            field.setAccessible(true);

            field.set(instance, newValue);

            field.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoke method reflection.
     *
     * @param instance Object to perform reflection on.
     * @param name     Name of method.
     * @param args     Any arguments to pass into the method.
     */
    public static void invokeMethod(Object instance, String name, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = instance
                .getClass()
                .getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);

            method.invoke(instance, args);

            method.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoke method reflection targeting a specific class.
     *
     * @param instance Object to perform reflection on.
     * @param clazz    The class to find the method in.
     * @param name     Name of method.
     * @param args     Any arguments to pass into the method.
     */
    public static void invokeMethod(Object instance, Class<?> clazz, String name, Class<?>[] parameterTypes,
                                    Object... args) {
        try {
            Method method = clazz
                .getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);

            method.invoke(instance, args);

            method.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
