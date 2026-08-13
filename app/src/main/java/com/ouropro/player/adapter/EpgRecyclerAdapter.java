package com.ouropro.player.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.R;
import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.utils.Utils;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
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

        public EpgViewHolder(@NonNull EpgRecyclerAdapter epgRecyclerAdapter, View view) {
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
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void setEpgList(List<CatchUpEpg> list) {
        this.epgList = list;
        notifyDataSetChanged();
    }

    public void setBellClickListener(BellClickListener listener) {
        this.bellClickListener = listener;
    }

    public void onBindViewHolder(@NonNull EpgViewHolder epgViewHolder, int i) {
        CatchUpEpg catchUpEpg = this.epgList.get(i);
        epgViewHolder.txt_name.setText(Utils.decode64String(catchUpEpg.getTitle()));
        try {
            epgViewHolder.txt_time.setText(Utils.Offset(catchUpEpg.getStart_timestamp() * 1000, this.context) + " ~ " + Utils.Offset(catchUpEpg.getStop_timestamp() * 1000, this.context));
        } catch (Exception unused) {
        }
        if (i != 0) {
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.white, epgViewHolder.txt_name);
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.white, epgViewHolder.txt_time);
        } else {
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.yellow, epgViewHolder.txt_name);
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.yellow, epgViewHolder.txt_time);
            epgViewHolder.itemView.requestFocus();
        }
        if (this.bellClickListener == null) {
            epgViewHolder.epg_bell.setVisibility(View.GONE);
        } else {
            epgViewHolder.epg_bell.setVisibility(View.VISIBLE);
            epgViewHolder.epg_bell.setFocusable(true);
            epgViewHolder.epg_bell.setClickable(true);
            if (this.bellClickListener.isScheduled(catchUpEpg)) {
                epgViewHolder.epg_bell.setColorFilter(Color.YELLOW);
            } else {
                epgViewHolder.epg_bell.clearColorFilter();
            }
            epgViewHolder.epg_bell.setOnFocusChangeListener((view, hasFocus) -> {
                view.setScaleX(hasFocus ? 1.25f : 1.0f);
                view.setScaleY(hasFocus ? 1.25f : 1.0f);
            });
            epgViewHolder.epg_bell.setOnClickListener(view -> this.bellClickListener.onBellClick(catchUpEpg));
        }
    }

    @NonNull
    public EpgViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EpgViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_epg, viewGroup, false));
    }
}
