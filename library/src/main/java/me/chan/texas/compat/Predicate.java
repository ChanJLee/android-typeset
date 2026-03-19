package me.chan.texas.compat;

@FunctionalInterface
public interface Predicate<T> {
	/**
	 * @param v v
	 * @return true 表示通过，false 表示不通过
	 */
	boolean test(T v);
}
