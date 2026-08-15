package com.ouropro.player.improvements;

import android.content.Context;
import android.widget.Toast;

import com.ouropro.player.adapter.EpgRecyclerAdapter;
import com.ouropro.player.models.CatchUpEpg;

/** Conecta os sinos da lista EPG ao armazenamento local de lembretes. */
public final class EpgReminderBinder {
    public interface StreamProvider {
        String getStreamId();
    }

    private EpgReminderBinder() {
    }

    public static void bind(Context context, EpgRecyclerAdapter adapter, StreamProvider provider) {
        if (context == null || adapter == null || provider == null) {
            return;
        }
        adapter.setBellClickListener(new EpgRecyclerAdapter.BellClickListener() {
            @Override
            public boolean isScheduled(CatchUpEpg program) {
                return EpgReminderStore.isScheduled(context, safeStream(provider.getStreamId()), program);
            }

            @Override
            public void onBellClick(CatchUpEpg program) {
                String streamId = safeStream(provider.getStreamId());
                boolean scheduled = EpgReminderStore.isScheduled(context, streamId, program);
                EpgReminderStore.setScheduled(context, streamId, program, !scheduled);
                Toast.makeText(context,
                        scheduled ? "Aviso removido" : "Aviso ativado para este programa",
                        Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static String safeStream(String streamId) {
        return streamId == null ? "" : streamId;
    }
}
