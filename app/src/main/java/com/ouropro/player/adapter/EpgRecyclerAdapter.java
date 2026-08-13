package com.ouropro.player.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.R;
import com.ouropro.player.improvements.EpgReminderStore;
import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.utils.Utils;
import java.util.List;

public class EpgRecyclerAdapter extends RecyclerView.Adapter<EpgRecyclerAdapter.EpgViewHolder> {
    public interface BellClickListener {
        void onBell(CatchUpEpg program, boolean scheduled, View anchor);
    }

    public interface ChannelIdProvider {
        String getChannelId();
    }

    public Context context;
    public List<CatchUpEpg> epgList;
    private String channelId = "";
    private ChannelIdProvider channelIdProvider;
    private BellClickListener bellClickListener;

    public class EpgViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name;
        public TextView txt_time;
        public ImageButton epg_bell;

        public EpgViewHolder(@NonNull EpgRecyclerAdapter adapter, View view) {
            super(view);
            this.txt_time = view.findViewById(R.id.txt_time);
            this.txt_name = view.findViewById(R.id.txt_name);
            this.epg_bell = view.findViewById(R.id.epg_bell);
        }
    }

    public EpgRecyclerAdapter(Context context, List<CatchUpEpg> list) {
        this.context = context;
        this.epgList = list;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId == null ? "" : channelId;
        notifyDataSetChanged();
    }

    public void setBellClickListener(BellClickListener listener) {
        this.bellClickListener = listener;
    }

    public void setChannelIdProvider(ChannelIdProvider provider) {
        this.channelIdProvider = provider;
        notifyDataSetChanged();
    }

    private String currentChannelId() {
        if (this.channelIdProvider != null) {
            String value = this.channelIdProvider.getChannelId();
            return value == null ? "" : value;
        }
        return this.channelId;
    }

    public int getItemCount() {
        return this.epgList == null ? 0 : this.epgList.size();
    }

    public void setEpgList(List<CatchUpEpg> list) {
        this.epgList = list;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(@NonNull EpgViewHolder holder, int position) {
        CatchUpEpg program = this.epgList.get(position);
        holder.txt_name.setText(Utils.decode64String(program.getTitle()));
        try {
            holder.txt_time.setText(Utils.Offset(program.getStart_timestamp() * 1000, this.context) + " ~ " + Utils.Offset(program.getStop_timestamp() * 1000, this.context));
        } catch (Exception ignored) {
            holder.txt_time.setText("");
        }
        int textColor = position == 0 ? R.color.yellow : R.color.white;
        Insets$$ExternalSyntheticOutline0.m(this.context, textColor, holder.txt_name);
        Insets$$ExternalSyntheticOutline0.m(this.context, textColor, holder.txt_time);

        String currentChannelId = currentChannelId();
        boolean scheduled = EpgReminderStore.isScheduled(this.context, currentChannelId, program);
        holder.epg_bell.setColorFilter(scheduled ? Color.rgb(255, 211, 42) : Color.WHITE);
        holder.epg_bell.setContentDescription(scheduled ? "Lembrete ativado" : "Ativar lembrete");
        holder.epg_bell.setOnClickListener(view -> {
            String activeChannelId = currentChannelId();
            boolean next = !EpgReminderStore.isScheduled(this.context, activeChannelId, program);
            EpgReminderStore.setScheduled(this.context, activeChannelId, program, next);
            holder.epg_bell.setColorFilter(next ? Color.rgb(255, 211, 42) : Color.WHITE);
            holder.epg_bell.setContentDescription(next ? "Lembrete ativado" : "Ativar lembrete");
            if (this.bellClickListener != null) {
                this.bellClickListener.onBell(program, next, view);
            }
        });
        holder.epg_bell.setOnFocusChangeListener((view, focused) -> {
            view.setScaleX(focused ? 1.18f : 1.0f);
            view.setScaleY(focused ? 1.18f : 1.0f);
        });
    }

    @NonNull
    public EpgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpgViewHolder(this, Insets$$ExternalSyntheticOutline0.m(parent, R.layout.item_epg, parent, false));
    }
}
