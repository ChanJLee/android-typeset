package com.shanbay.lib.texas;

import androidx.collection.SparseArrayCompat;

import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.Recyclable;
import com.shanbay.lib.texas.utils.TexasUtils;

import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

public class TestUtils {
	public static void testRecycled(Recyclable recyclable) {
		try {
			Class<?> clazz = recyclable.getClass();
			while (clazz != null && clazz != Object.class) {
				// TODO for test
				if (clazz == Recyclable.class || clazz == DefaultRecyclable.class) {
					clazz = clazz.getSuperclass();
					continue;
				}

				Field[] fields = clazz.getDeclaredFields();
				for (int i = 0; fields != null && i < fields.length; ++i) {
					Field field = fields[i];
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					field.setAccessible(true);
					Class<?> type = field.getType();
					if (type.isPrimitive()) {
						if (type == boolean.class) {
							Assert.assertFalse((Boolean) field.get(recyclable));
						} else {
							Number number = (Number) field.get(recyclable);
							Assert.assertEquals(number.intValue(), 0);
						}
					} else {
						Object o = field.get(recyclable);
						if (o == null) {
							continue;
						}

						if (o instanceof Collection) {
							Collection collection = (Collection) o;
							if (collection.isEmpty()) {
								continue;
							}
						} if (o instanceof SparseArrayCompat) {
							SparseArrayCompat<?> compat = (SparseArrayCompat<?>) o;
							if (compat.isEmpty()) {
								continue;
							}
						}

						Assert.fail("check failed");
					}
				}
				clazz = clazz.getSuperclass();
			}
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T> boolean reflectCompare(T lhs, T rhs, String... whitelist) {
		Class<?> clazz = lhs.getClass();
		if (clazz == Object.class) {
			return TexasUtils.equals(lhs, rhs);
		}

		try {
			while (clazz != null && clazz != Object.class) {
				Field[] fields = clazz.getDeclaredFields();

				for (int i = 0; fields != null && i < fields.length; ++i) {
					Field field = fields[i];
					if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
						continue;
					}

					field.setAccessible(true);

					boolean ignore = false;
					for (int j = 0; whitelist != null && j < whitelist.length; ++j) {
						if (whitelist[j].equals(field.getName())) {
							ignore = true;
							break;
						}
					}

					if (ignore) {
						continue;
					}

					if (!TexasUtils.equals(field.get(lhs), field.get(rhs))) {
						return false;
					}
				}
				clazz = clazz.getSuperclass();
			}

			return true;
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
