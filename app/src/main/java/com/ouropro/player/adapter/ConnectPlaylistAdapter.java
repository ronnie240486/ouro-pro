package com.ouropro.player.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.R;
import com.ouropro.player.apps.SideMenu;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

/* JADX INFO: loaded from: classes.dex */
public class ConnectPlaylistAdapter extends RecyclerView.Adapter<ConnectPlaylistAdapter.ConnectListViewHolder> {
    public Function3<SideMenu, Integer, Boolean, Unit> clickListenerFunction;
    public List<SideMenu> data;

    public class ConnectListViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_name;

        public ConnectListViewHolder(@NonNull ConnectPlaylistAdapter connectPlaylistAdapter, View view) {
            super(view);
            this.txt_name = (TextView) view.findViewById(R.id.txt_name);
        }
    }

    public ConnectPlaylistAdapter(Context context, List<SideMenu> list, Function3<SideMenu, Integer, Boolean, Unit> function3) {
        this.data = list;
        this.clickListenerFunction = function3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(int i, View view) {
        this.clickListenerFunction.invoke(this.data.get(i), Integer.valueOf(i), Boolean.TRUE);
    }

    public int getItemCount() {
        return this.data.size();
    }

    public void onBindViewHolder(@NonNull ConnectListViewHolder connectListViewHolder, int i) {
        connectListViewHolder.txt_name.setText(this.data.get(i).getName());
        connectListViewHolder.itemView.setOnClickListener(new SeasonRecyclerAdapter$$ExternalSyntheticLambda0(this, i, 1));
    }

    @NonNull
    public ConnectListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ConnectListViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_connect, viewGroup, false));
    }
}
