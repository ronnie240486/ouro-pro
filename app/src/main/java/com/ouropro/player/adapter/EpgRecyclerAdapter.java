package com.ouropro.player.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;

import com.ouropro.player.R;
import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.utils.Utils;

import java.util.List;

public class EpgRecyclerAdapter extends RecyclerView.Adapter<EpgRecyclerAdapter.EpgViewHolder> {
    public interface BellClickListener {
        boolean isScheduled(CatchUpEpg program);
        void onBellClick(CatchUpEpg program);
    }

    public Context context;
    public List<CatchUpEpg> epgList;
    public BellClickListener bellClickListener;

    public class EpgViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_time;
        public TextView txt_name;
        public ImageView epg_bell;

        public EpgViewHolder(@NonNull EpgRecyclerAdapter adapter, View view) {
            super(view);
            this.txt_time = (TextView) view.findViewById(R.id.txt_time);
            this.txt_name = (TextView) view.findViewById(R.id.txt_name);
            this.epg_bell = (ImageView) view.findViewById(R.id.epg_bell);
        }
    }

    public EpgRecyclerAdapter(Context context, List<CatchUpEpg> list) {
        this.context = context;
        this.epgList = list;
    }

    public int getItemCount() {
        List<CatchUpEpg> list = this.epgList;
        return list == null ? 0 : list.size();
    }

    public void setEpgList(List<CatchUpEpg> list) {
        this.epgList = list;
        notifyDataSetChanged();
    }

    public void setBellClickListener(BellClickListener listener) {
        this.bellClickListener = listener;
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

        if (this.bellClickListener == null) {
            holder.epg_bell.setVisibility(View.GONE);
            return;
        }

        holder.epg_bell.setVisibility(View.VISIBLE);
        holder.epg_bell.setFocusable(true);
        holder.epg_bell.setFocusableInTouchMode(true);
        holder.epg_bell.setClickable(true);
        boolean scheduled = this.bellClickListener.isScheduled(program);
        holder.epg_bell.setColorFilter(scheduled ? Color.rgb(255, 211, 42) : Color.WHITE);
        holder.epg_bell.setContentDescription(scheduled ? "Lembrete ativado" : "Ativar lembrete");
        holder.epg_bell.setOnFocusChangeListener((view, hasFocus) -> {
            view.setScaleX(hasFocus ? 1.25f : 1.0f);
            view.setScaleY(hasFocus ? 1.25f : 1.0f);
        });
        holder.epg_bell.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return false;
            }
            ViewParent parent = holder.itemView.getParent();
            RecyclerView recyclerView = parent instanceof RecyclerView ? (RecyclerView) parent : null;
            if (recyclerView == null) {
                return false;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && currentPosition + 1 < getItemCount()) {
                requestBellFocus(recyclerView, currentPosition + 1);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (currentPosition > 0) {
                    requestBellFocus(recyclerView, currentPosition - 1);
                } else {
                    View channelList = recyclerView.getRootView().findViewById(R.id.recycler_channel);
                    if (channelList != null) {
                        channelList.requestFocus();
                    } else {
                        recyclerView.requestFocus();
                    }
                }
                return true;
            }
            return false;
        });
        holder.epg_bell.setOnClickListener(view -> this.bellClickListener.onBellClick(program));
    }

    private void requestBellFocus(RecyclerView recyclerView, int position) {
        recyclerView.scrollToPosition(position);
        recyclerView.post(() -> {
            RecyclerView.ViewHolder nextHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (nextHolder != null) {
                View nextBell = nextHolder.itemView.findViewById(R.id.epg_bell);
                if (nextBell != null) {
                    nextBell.requestFocus();
                }
            }
        });
    }

    @NonNull
    public EpgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpgViewHolder(this, Insets$$ExternalSyntheticOutline0.m(parent, R.layout.item_epg, parent, false));
    }
}
