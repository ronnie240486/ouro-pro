package com.ouropro.player.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.R;
import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.utils.Utils;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class EpgRecyclerAdapter extends RecyclerView.Adapter<EpgViewHolder> {
    public Context context;
    public List<CatchUpEpg> epgList;

    public class EpgViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name;
        public TextView txt_time;

        public EpgViewHolder(@NonNull EpgRecyclerAdapter epgRecyclerAdapter, View view) {
            super(view);
            this.txt_time = (TextView) view.findViewById(R.id.txt_time);
            this.txt_name = (TextView) view.findViewById(R.id.txt_name);
        }
    }

    public EpgRecyclerAdapter(Context context, List<CatchUpEpg> list) {
        this.context = context;
        this.epgList = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
            return;
        }
        Insets$$ExternalSyntheticOutline0.m(this.context, R.color.yellow, epgViewHolder.txt_name);
        Insets$$ExternalSyntheticOutline0.m(this.context, R.color.yellow, epgViewHolder.txt_time);
        epgViewHolder.itemView.requestFocus();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    public EpgViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EpgViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_epg, viewGroup, false));
    }
}
