package me.chan.texas.misc;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.BuildConfig;

public class ResourceManager {
    private static final List<Unit<Object>> mUnits = new ArrayList<>();

    public static <T> void hold(T o, Listener<T> listener) {
        if (!BuildConfig.DEBUG) {
            return;
        }

        mUnits.add((Unit<Object>) new Unit<T>(o, listener));
    }

    public static void check() {
        if (!BuildConfig.DEBUG) {
            return;
        }

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (Unit<Object> unit : mUnits) {
                if (!unit.l.isReleased(unit.o)) {
                    throw new RuntimeException("check object release failed");
                }
            }
            mUnits.clear();
        }).start();
    }

    public interface Listener<T> {
        boolean isReleased(T o);
    }

    private static class Unit<T> {
        private final T o;
        private final Listener<T> l;

        public Unit(T o, Listener<T> l) {
            this.o = o;
            this.l = l;
        }
    }
}
