package com.meditation.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Prefs {

    public static final String PREFS = "medi_prefs";
    public static final String KEY_NAME = "user_name";
    private static final String KEY_SESSIONS = "sessions";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_BEST_STREAK = "best_streak";
    private static final String KEY_LAST_DATE = "last_date";
    private static final String KEY_SOUND = "sound_enabled";
    private static final String KEY_VIBRO = "vibro_enabled";

    private final SharedPreferences sp;

    public Prefs(Context ctx) {
        sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /* ── name ─────────────────────────────────────────────── */

    public String getName() { return sp.getString(KEY_NAME, ""); }
    public void setName(String name) { sp.edit().putString(KEY_NAME, name).apply(); }

    /* ── settings ─────────────────────────────────────────── */

    public boolean isSoundEnabled() { return sp.getBoolean(KEY_SOUND, true); }
    public void setSoundEnabled(boolean v) { sp.edit().putBoolean(KEY_SOUND, v).apply(); }

    public boolean isVibroEnabled() { return sp.getBoolean(KEY_VIBRO, true); }
    public void setVibroEnabled(boolean v) { sp.edit().putBoolean(KEY_VIBRO, v).apply(); }

    /* ── sessions ─────────────────────────────────────────── */

    public static class Session {
        public final String category;
        public final int minutes;
        public final long timestamp;

        public Session(String category, int minutes, long timestamp) {
            this.category = category;
            this.minutes = minutes;
            this.timestamp = timestamp;
        }

        public String formattedDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, HH:mm", new Locale("ru"));
            return sdf.format(new Date(timestamp));
        }

        public String emoji() {
            switch (category) {
                case "Расслабление": return "🪷";
                case "Сон": return "🌙";
                case "Фокус": return "☀";
                default: return "〰";
            }
        }
    }

    public List<Session> getSessions() {
        List<Session> list = new ArrayList<>();
        String raw = sp.getString(KEY_SESSIONS, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = arr.length() - 1; i >= 0; i--) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(new Session(
                        obj.getString("cat"),
                        obj.getInt("min"),
                        obj.getLong("ts")));
            }
        } catch (JSONException ignored) {}
        return list;
    }

    public void addSession(String category, int minutes) {
        String raw = sp.getString(KEY_SESSIONS, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            JSONObject obj = new JSONObject();
            obj.put("cat", category);
            obj.put("min", minutes);
            obj.put("ts", System.currentTimeMillis());
            arr.put(obj);
            sp.edit().putString(KEY_SESSIONS, arr.toString()).apply();
        } catch (JSONException ignored) {}

        updateStreak();
    }

    /* ── streak ───────────────────────────────────────────── */

    private void updateStreak() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String today = sdf.format(new Date());
        String last = sp.getString(KEY_LAST_DATE, "");

        if (today.equals(last)) return; // already counted today

        int streak = sp.getInt(KEY_STREAK, 0);
        // check if yesterday
        try {
            Date lastDate = sdf.parse(last);
            long diff = (System.currentTimeMillis() - (lastDate != null ? lastDate.getTime() : 0)) / 86400000L;
            streak = (diff <= 1) ? streak + 1 : 1;
        } catch (Exception e) {
            streak = 1;
        }

        int best = sp.getInt(KEY_BEST_STREAK, 0);
        if (streak > best) best = streak;

        sp.edit()
                .putInt(KEY_STREAK, streak)
                .putInt(KEY_BEST_STREAK, best)
                .putString(KEY_LAST_DATE, today)
                .apply();
    }

    public int getStreak() { return sp.getInt(KEY_STREAK, 0); }
    public int getBestStreak() { return sp.getInt(KEY_BEST_STREAK, 0); }

    /* ── computed stats ───────────────────────────────────── */

    public int getTotalMinutes() {
        List<Session> sessions = getSessions();
        int total = 0;
        for (Session s : sessions) total += s.minutes;
        return total;
    }

    public int getTodaySessions() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String today = sdf.format(new Date());
        int count = 0;
        for (Session s : getSessions()) {
            if (sdf.format(new Date(s.timestamp)).equals(today)) count++;
        }
        return count;
    }

    public int getTotalSessions() { return getSessions().size(); }

    public int getAvgDuration() {
        List<Session> sessions = getSessions();
        if (sessions.isEmpty()) return 0;
        return getTotalMinutes() / sessions.size();
    }

    /* ── reset ────────────────────────────────────────────── */

    public void resetStats() {
        sp.edit()
                .remove(KEY_SESSIONS)
                .remove(KEY_STREAK)
                .remove(KEY_BEST_STREAK)
                .remove(KEY_LAST_DATE)
                .apply();
    }
}
