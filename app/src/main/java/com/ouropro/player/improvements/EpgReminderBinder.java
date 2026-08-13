package com.ouropro.player.improvements;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.ouropro.player.adapter.EpgRecyclerAdapter;
import com.ouropro.player.models.CatchUpEpg;

/** Liga o sino do EPG ao canal atual em qualquer Activity. */
public final class EpgReminderBinder {
    public interface ChannelIdProvider {
        String getChannelId();
    }

    private EpgReminderBinder() {
    }

    public static void bind(Context context, EpgRecyclerAdapter adapter, ChannelIdProvider provider) {
        adapter.setChannelIdProvider(provider == null ? () -> "" : provider::getChannelId);
        adapter.setBellClickListener((CatchUpEpg program, boolean scheduled, View anchor) ->
                Toast.makeText(context, scheduled ? "Lembrete ativado" : "Lembrete removido", Toast.LENGTH_SHORT).show());
    }
}
