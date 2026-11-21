package org.telegram.telegrios;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection helpers. Telegrios-native settings are resolved against TelegriosConfig
 * by config id (e.g. "liquidGlass" -> isLiquidGlass()/setLiquidGlass()).
 * Nagram/NekoX settings are resolved by owner class + ConfigItem name.
 * All lookups are failure-tolerant: a missing key disables its row, never crashes.
 */
public final class TelegriosReflection {

    private TelegriosReflection() {}

    private static String pascal(String id) {
        String[] parts = id.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    // ---- TelegriosConfig ----

    public static boolean specBool(String id) {
        try {
            Method m = TelegriosConfig.class.getMethod("is" + pascal(id));
            return (Boolean) m.invoke(null);
        } catch (Throwable t) {
            return false;
        }
    }

    public static void setSpecBool(String id, boolean v) {
        try {
            Method m = TelegriosConfig.class.getMethod("set" + pascal(id), boolean.class);
            m.invoke(null, v);
        } catch (Throwable ignore) {}
    }

    public static int specInt(String id) {
        try {
            Method m = TelegriosConfig.class.getMethod("get" + pascal(id));
            return (Integer) m.invoke(null);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static void setSpecInt(String id, int v) {
        try {
            Method m = TelegriosConfig.class.getMethod("set" + pascal(id), int.class);
            m.invoke(null, v);
        } catch (Throwable ignore) {}
    }

    public static String specStr(String id) {
        try {
            Method m = TelegriosConfig.class.getMethod("get" + pascal(id));
            return (String) m.invoke(null);
        } catch (Throwable t) {
            return "";
        }
    }

    public static void setSpecStr(String id, String v) {
        try {
            Method m = TelegriosConfig.class.getMethod("set" + pascal(id), String.class);
            m.invoke(null, v);
        } catch (Throwable ignore) {}
    }

    // ---- Nagram (NekoConfig static fields) / NaConfig (Kotlin object) ----

    /** Resolves ConfigItem instance: NekoConfig -> public static field; NaConfig -> INSTANCE.getXxx(). */
    public static Object resolveConfigItem(String ownerClass, String itemName) {
        try {
            Class<?> c = Class.forName(ownerClass);
            try {
                Field f = c.getField(itemName);
                return f.get(null);
            } catch (NoSuchFieldException nsf) {
                Object instance = c.getField("INSTANCE").get(null);
                String getter = "get" + Character.toUpperCase(itemName.charAt(0)) + itemName.substring(1);
                Method m = c.getMethod(getter);
                return m.invoke(instance);
            }
        } catch (Throwable t) {
            return null;
        }
    }

    private static Class<?> configItemClass() {
        try {
            return Class.forName("tw.nekomimi.nekogram.config.ConfigItem");
        } catch (Throwable t) {
            return null;
        }
    }

    public static boolean isConfigItem(Object o) {
        Class<?> c = configItemClass();
        return c != null && c.isInstance(o);
    }

    public static boolean naBool(String ownerClass, String itemName, boolean invert) {
        Object item = resolveConfigItem(ownerClass, itemName);
        if (item == null) return false;
        try {
            boolean v = (Boolean) item.getClass().getMethod("Bool").invoke(item);
            return invert ? !v : v;
        } catch (Throwable t) {
            // some toggles are plain static booleans (e.g. NekoConfig.sendReadMessagePackets)
            try {
                Class<?> c = Class.forName(ownerClass);
                Field f = c.getField(itemName);
                boolean v = f.getBoolean(null);
                return invert ? !v : v;
            } catch (Throwable t2) {
                return false;
            }
        }
    }

    public static void setNaBool(String ownerClass, String itemName, boolean invert, boolean v) {
        Object item = resolveConfigItem(ownerClass, itemName);
        if (item != null) {
            try {
                item.getClass().getMethod("setConfigBool", boolean.class).invoke(item, invert ? !v : v);
                return;
            } catch (Throwable ignore) {}
            try {
                Class<?> c = Class.forName(ownerClass);
                Field f = c.getField(itemName);
                f.setBoolean(null, invert ? !v : v);
                return;
            } catch (Throwable ignore) {}
        }
        // last resort: static field assignment without ConfigItem
        try {
            Class<?> c = Class.forName(ownerClass);
            Field f = c.getField(itemName);
            f.setBoolean(null, invert ? !v : v);
        } catch (Throwable ignore) {}
    }

    public static int naInt(String ownerClass, String itemName) {
        Object item = resolveConfigItem(ownerClass, itemName);
        if (item == null) return 0;
        try {
            return (Integer) item.getClass().getMethod("Int").invoke(item);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static void setNaInt(String ownerClass, String itemName, int v) {
        Object item = resolveConfigItem(ownerClass, itemName);
        if (item == null) return;
        try {
            item.getClass().getMethod("setConfigInt", int.class).invoke(item, v);
        } catch (Throwable ignore) {}
    }
}
