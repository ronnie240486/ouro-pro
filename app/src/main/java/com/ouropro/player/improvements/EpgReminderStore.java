package com.ouropro.player.improvements;

import android.content.Context;
import android.content.SharedPreferences;

import com.ouropro.player.models.CatchUpEpg;

import java.util.Locale;

/** Persistência simples e local dos avisos de programas do EPG. */
public final class EpgReminderStore {
    private static final String PREFS = "ouropro_epg_reminders";
    private static final String PREFIX = "reminder_";

    private EpgReminderStore() {
    }

    public static String key(String channelId, CatchUpEpg program) {
        String title = program == null ? "" : VoiceCommand.normalize(program.getTitle());
        long start = program == null ? 0L : program.getStart_timestamp();
        return key(channelId, title, start);
    }

    public static String key(String channelId, String title, long startTimestamp) {
        String raw = String.format(Locale.ROOT, "%s|%s|%d", channelId == null ? "" : channelId,
                VoiceCommand.normalize(title), startTimestamp);
        return PREFIX + Integer.toHexString(raw.hashCode());
    }

    public static boolean isScheduled(Context context, String channelId, CatchUpEpg program) {
        return prefs(context).getBoolean(key(channelId, program), false);
    }

    public static void setScheduled(Context context, String channelId, CatchUpEpg program, boolean scheduled) {
        prefs(context).edit().putBoolean(key(channelId, program), scheduled).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
