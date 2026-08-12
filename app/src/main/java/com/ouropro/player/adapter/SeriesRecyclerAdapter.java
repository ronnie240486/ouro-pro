package com.ouropro.player.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ouropro.player.R;
import com.ouropro.player.models.SeriesModel;
import com.ouropro.player.models.EpisodeModel;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.utils.ImageLoaderJava;
import io.realm.RealmResults;

/* JADX INFO: loaded from: classes.dex */
public class SeriesRecyclerAdapter extends RealmRecyclerViewAdapter<SeriesModel, SeriesRecyclerAdapter.VodViewHolder> {
    public Context context;
    public boolean is_grid;
    public ItemClickListener mItemClickListener;
    public int selected_pos;

    public interface ItemClickListener {
        void onFavClick(SeriesModel seriesModel, int i);

        void onFocusPosition(int i);

        void onItemClick(SeriesModel seriesModel, int i);

        void onUnFavClick(SeriesModel seriesModel, int i);
    }

    public class VodViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_fav;
        public RoundedImageView image_logo;
        public RoundedImageView image_vod;
        public TextView txt_name;

        public VodViewHolder(@NonNull SeriesRecyclerAdapter seriesRecyclerAdapter, View view) {
            super(view);
            this.image_vod = (RoundedImageView) view.findViewById(R.id.image_vod);
            this.image_logo = (RoundedImageView) view.findViewById(R.id.image_logo);
            this.txt_name = (TextView) view.findViewById(R.id.txt_name);
            this.image_fav = (ImageView) view.findViewById(R.id.image_fav);
        }
    }

    public SeriesRecyclerAdapter(Context context, @Nullable RealmResults<SeriesModel> realmResults, boolean z) {
        super(realmResults, true, true);
        this.selected_pos = -1;
        this.context = context;
        this.is_grid = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(int i, SeriesModel seriesModel, View view) {
        int i2 = this.selected_pos;
        this.selected_pos = i;
        notifyItemChanged(i2);
        notifyItemChanged(i);
        this.mItemClickListener.onItemClick(seriesModel, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$1(VodViewHolder vodViewHolder, int i, View view, boolean z) {
        if (!z) {
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.white, vodViewHolder.txt_name);
            vodViewHolder.itemView.setBackgroundResource(R.drawable.item_group_bg);
            vodViewHolder.itemView.setScaleX(0.95f);
            vodViewHolder.itemView.setScaleY(0.95f);
            vodViewHolder.txt_name.setSelected(false);
            return;
        }
        Insets$$ExternalSyntheticOutline0.m(this.context, R.color.black, vodViewHolder.txt_name);
        vodViewHolder.itemView.setBackgroundResource(R.drawable.item_vod_selected_bg);
        vodViewHolder.txt_name.setSelected(true);
        vodViewHolder.itemView.setScaleX(1.0f);
        vodViewHolder.itemView.setScaleY(1.0f);
        this.mItemClickListener.onFocusPosition(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onBindViewHolder$2(SeriesModel seriesModel, int i, View view) {
        if (seriesModel.isIs_favorite()) {
            this.mItemClickListener.onUnFavClick(seriesModel, i);
            return true;
        }
        this.mItemClickListener.onFavClick(seriesModel, i);
        return true;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void onBindViewHolder(@NonNull VodViewHolder vodViewHolder, int i) {
        SeriesModel item = getItem(i);
        vodViewHolder.txt_name.setText(item.getName());
        if (item.isIs_favorite()) {
            vodViewHolder.image_fav.setVisibility(0);
        } else {
            vodViewHolder.image_fav.setVisibility(8);
        }
        String cover = item.getStream_icon();
        if (cover == null || cover.trim().isEmpty()) {
            EpisodeModel firstEpisode = RealmController.with().getFirstEpisodeBySeriesName(item.getName());
            if (firstEpisode != null) {
                cover = firstEpisode.getStream_icon();
            }
        }
        ImageLoaderJava.imageLoadUrlWithVodHolder(this.context, vodViewHolder.image_vod, cover, R.drawable.default_bg, vodViewHolder.image_logo);
        vodViewHolder.itemView.setOnClickListener(new VodRecyclerAdapter$$ExternalSyntheticLambda0(this, i, item, 14));
        vodViewHolder.itemView.setOnFocusChangeListener(new VodRecyclerAdapter$$ExternalSyntheticLambda1(this, vodViewHolder, i, 3));
        vodViewHolder.itemView.setOnLongClickListener(new VodRecyclerAdapter$$ExternalSyntheticLambda2(this, item, i, 2));
        if (this.selected_pos != i) {
            vodViewHolder.itemView.setBackgroundResource(R.drawable.item_group_bg);
            vodViewHolder.itemView.setScaleX(0.95f);
            vodViewHolder.itemView.setScaleY(0.95f);
            vodViewHolder.txt_name.setSelected(false);
            Insets$$ExternalSyntheticOutline0.m(this.context, R.color.white, vodViewHolder.txt_name);
            return;
        }
        vodViewHolder.itemView.setBackgroundResource(R.drawable.item_vod_selected_bg);
        vodViewHolder.txt_name.setSelected(true);
        vodViewHolder.itemView.setScaleX(1.0f);
        vodViewHolder.itemView.setScaleY(1.0f);
        Insets$$ExternalSyntheticOutline0.m(this.context, R.color.black, vodViewHolder.txt_name);
        this.mItemClickListener.onFocusPosition(i);
    }

    @NonNull
    public VodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return this.is_grid ? new VodViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_vod_grid, viewGroup, false)) : new VodViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_vod, viewGroup, false));
    }
}
