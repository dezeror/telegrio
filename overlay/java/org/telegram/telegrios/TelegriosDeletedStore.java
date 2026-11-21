package org.telegram.telegrios;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Local store for the anti-delete feature (B7) and edit history (B8).
 * When the hook in MessagesController fires, deleted message text is saved here
 * with a [Deleted] marker; edited messages keep their original text.
 * Entries survive until the local cache is cleared.
 */
public final class TelegriosDeletedStore {

    private static final int MAX_PER_DIALOG = 200;

    private TelegriosDeletedStore() {}

    private static SharedPreferences prefs() {
        return org.telegram.messenger.ApplicationLoader.applicationContext
                .getSharedPreferences("telegrios_deleted_" + org.telegram.messenger.UserConfig.selectedAccount, Context.MODE_PRIVATE);
    }

    public static class Entry {
        public int mid;
        public String text;
        public String original; // for edits
        public long date;
        Entry(int mid, String text, String original, long date) {
            this.mid = mid; this.text = text; this.original = original; this.date = date;
        }
    }

    private static String key(long dialogId) {
        return "dlg_" + dialogId;
    }

    private static List<Entry> load(long dialogId) {
        List<Entry> list = new ArrayList<>();
        try {
            String raw = prefs().getString(key(dialogId), "[]");
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new Entry(o.optInt("mid"), o.optString("text"), o.optString("original"), o.optLong("date")));
            }
        } catch (Throwable ignore) {}
        return list;
    }

    private static void save(long dialogId, List<Entry> list) {
        try {
            if (list.size() > MAX_PER_DIALOG) {
                list = new ArrayList<>(list.subList(list.size() - MAX_PER_DIALOG, list.size()));
            }
            JSONArray arr = new JSONArray();
            for (Entry e : list) {
                JSONObject o = new JSONObject();
                o.put("mid", e.mid);
                o.put("text", e.text == null ? "" : e.text);
                o.put("original", e.original == null ? "" : e.original);
                o.put("date", e.date);
                arr.put(o);
            }
            prefs().edit().putString(key(dialogId), arr.toString()).apply();
        } catch (Throwable ignore) {}
    }

    /** Called by the anti-delete hook before a message is deleted remotely. */
    public static void rememberDeleted(long dialogId, int mid, String text) {
        if (!TelegriosConfig.isAntiDelete()) return;
        try {
            List<Entry> list = load(dialogId);
            list.add(new Entry(mid, text == null ? "" : text, "", System.currentTimeMillis() / 1000L));
            save(dialogId, list);
        } catch (Throwable ignore) {}
    }

    /** Called by the edit-history hook before a message is edited remotely. */
    public static void rememberOriginal(long dialogId, int mid, String original) {
        if (!TelegriosConfig.isEditHistory()) return;
        try {
            List<Entry> list = load(dialogId);
            for (Entry e : list) {
                if (e.mid == mid) { e.original = original == null ? "" : original; save(dialogId, list); return; }
            }
            list.add(new Entry(mid, "", original == null ? "" : original, System.currentTimeMillis() / 1000L));
            save(dialogId, list);
        } catch (Throwable ignore) {}
    }

    public static List<Entry> get(long dialogId) {
        return load(dialogId);
    }

    public static String findOriginal(long dialogId, int mid) {
        for (Entry e : load(dialogId)) {
            if (e.mid == mid && e.original != null && !e.original.isEmpty()) return e.original;
        }
        return null;
    }

    public static void clear(long dialogId) {
        prefs().edit().remove(key(dialogId)).apply();
    }
}
