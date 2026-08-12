package com.ouropro.player.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.R;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.EPGChannel;
import io.realm.RealmResults;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

/* JADX INFO: loaded from: classes.dex */
public class RecyclerLiveCategoryAdapter extends RecyclerView.Adapter<LiveHomeViewHolder> {
    public List<CategoryModel> categoryModels;
    public Function3<CategoryModel, Integer, Boolean, Unit> clickFunctionListener;
    public Context context;
    public boolean is_first = true;
    public boolean is_grid;
    public boolean is_m3u;
    public int selected_position;

    public class LiveHomeViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_count;
        public TextView txt_name;

        public LiveHomeViewHolder(@NonNull RecyclerLiveCategoryAdapter recyclerLiveCategoryAdapter, View view) {
            super(view);
            this.txt_name = (TextView) view.findViewById(R.id.txt_name);
            this.txt_count = (TextView) view.findViewById(R.id.txt_count);
        }
    }

    public RecyclerLiveCategoryAdapter(Context context, List<CategoryModel> list, boolean z, boolean z2, int i, Function3<CategoryModel, Integer, Boolean, Unit> function3) {
        this.context = context;
        this.categoryModels = list;
        this.clickFunctionListener = function3;
        this.is_m3u = z;
        this.selected_position = i;
        this.is_grid = z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(LiveHomeViewHolder liveHomeViewHolder, CategoryModel categoryModel, int i, View view, boolean z) {
        if (!z) {
            if (this.is_grid) {
                liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.item_channel_grid_bg);
            } else {
                liveHomeViewHolder.itemView.setBackgroundResource(R.color.item_channel_bg);
            }
            liveHomeViewHolder.txt_name.setSelected(false);
            return;
        }
        if (this.is_grid) {
            liveHomeViewHolder.txt_name.setSelected(true);
            this.clickFunctionListener.invoke(categoryModel, Integer.valueOf(i), Boolean.FALSE);
            liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.item_channel_grid_selected_bg);
        } else {
            if (this.is_first) {
                this.is_first = false;
                return;
            }
            liveHomeViewHolder.txt_name.setSelected(true);
            this.clickFunctionListener.invoke(categoryModel, Integer.valueOf(i), Boolean.FALSE);
            liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.live_teim_focus_bg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$1(int i, CategoryModel categoryModel, View view) {
        int i2 = this.selected_position;
        this.selected_position = i;
        notifyItemChanged(i2);
        notifyItemChanged(this.selected_position);
        this.clickFunctionListener.invoke(categoryModel, Integer.valueOf(i), Boolean.TRUE);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<CategoryModel> list = this.categoryModels;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void setCategoryPosition(int i) {
        int i2 = this.selected_position;
        this.selected_position = i;
        notifyItemChanged(i2);
        notifyItemChanged(this.selected_position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(@NonNull LiveHomeViewHolder liveHomeViewHolder, @SuppressLint({"RecyclerView"}) int i) {
        CategoryModel categoryModel = this.categoryModels.get(i);
        liveHomeViewHolder.txt_name.setText(categoryModel.getName());
        RealmResults<EPGChannel> liveChannelsByCategory = RealmController.with().getLiveChannelsByCategory(categoryModel, "", this.is_m3u, 0);
        if (this.is_grid) {
            liveHomeViewHolder.txt_count.setText(liveChannelsByCategory.size() + " channels");
        } else {
            liveHomeViewHolder.txt_count.setText(String.valueOf(liveChannelsByCategory.size()));
        }
        liveHomeViewHolder.itemView.setOnFocusChangeListener(new CastRecyclerAdapter$$ExternalSyntheticLambda1(this, liveHomeViewHolder, categoryModel, i, 5));
        liveHomeViewHolder.itemView.setOnClickListener(new VodRecyclerAdapter$$ExternalSyntheticLambda0(this, i, categoryModel, 8));
        if (this.selected_position == i) {
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.yellow, liveHomeViewHolder.txt_name);
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.yellow, liveHomeViewHolder.txt_count);
            return;
        }
        Insets$$ExternalSyntheticOutline0.m(this.context, R.color.white, liveHomeViewHolder.txt_name);
        Insets$$ExternalSyntheticOutline0.m(this.context, R.color.white, liveHomeViewHolder.txt_count);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    public LiveHomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return this.is_grid ? new LiveHomeViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_live_category_grid, viewGroup, false)) : new LiveHomeViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_live_category, viewGroup, false));
    }
}
