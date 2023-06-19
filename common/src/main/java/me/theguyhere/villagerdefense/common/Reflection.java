package me.theguyhere.villagerdefense.common;

import java.lang.reflect.Field;

/**
 * A class that holds reflection methods.
 */
public class Reflection {
	/**
	 * Read reflection.
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
	 * Read reflection.
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
	 * Write reflection.
	 *
	 * @param instance Object to perform reflection on.
	 * @param name     Name of field.
	 * @param newValue New value to write to the field.
	 */
	@SuppressWarnings("unused")
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
}
