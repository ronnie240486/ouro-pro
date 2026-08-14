package com.ouropro.player.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.R;
import com.ouropro.player.activities.TrailerSearchActivity;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.utils.ImageLoaderJava;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

/* JADX INFO: loaded from: classes.dex */
public class RecyclerVodHomeAdapter extends RecyclerView.Adapter<RecyclerVodHomeAdapter.LiveHomeViewHolder> {
    public Function3<MovieModel, Integer, Boolean, Unit> clickFunctionListener;
    public Context context;
    public List<MovieModel> models;

    public class LiveHomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_logo;
        public ImageView image_movie;
        public TextView txt_name;
        public TextView btn_trailer;

        public LiveHomeViewHolder(@NonNull RecyclerVodHomeAdapter recyclerVodHomeAdapter, View view) {
            super(view);
            this.txt_name = (TextView) view.findViewById(R.id.txt_name);
            this.btn_trailer = (TextView) view.findViewById(R.id.btn_trailer);
            this.image_movie = (ImageView) view.findViewById(R.id.image_channel);
            this.image_logo = (ImageView) view.findViewById(R.id.image_logo);
        }
    }

    public RecyclerVodHomeAdapter(Context context, List<MovieModel> list, Function3<MovieModel, Integer, Boolean, Unit> function3) {
        this.context = context;
        this.models = list;
        this.clickFunctionListener = function3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(MovieModel movieModel, int i, LiveHomeViewHolder liveHomeViewHolder, View view, boolean z) {
        if (!z) {
            liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.search_item_bg);
        } else {
            this.clickFunctionListener.invoke(movieModel, Integer.valueOf(i), Boolean.FALSE);
            liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.search_focused_bg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$1(MovieModel movieModel, int i, View view) {
        this.clickFunctionListener.invoke(movieModel, Integer.valueOf(i), Boolean.TRUE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onBindViewHolder$2(LiveHomeViewHolder liveHomeViewHolder, MovieModel movieModel, int i, View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 3) {
            liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.search_item_bg);
        }
        if (motionEvent.getAction() == 0) {
            liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.search_focused_bg);
            return true;
        }
        if (motionEvent.getAction() != 1) {
            return false;
        }
        liveHomeViewHolder.itemView.setBackgroundResource(R.drawable.search_item_bg);
        this.clickFunctionListener.invoke(movieModel, Integer.valueOf(i), Boolean.TRUE);
        return false;
    }

    public int getItemCount() {
        List<MovieModel> list = this.models;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void setModels(List<MovieModel> list) {
        this.models = list;
        notifyDataSetChanged();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void onBindViewHolder(@NonNull LiveHomeViewHolder liveHomeViewHolder, int i) {
        MovieModel movieModel = this.models.get(i);
        liveHomeViewHolder.txt_name.setText(movieModel.getName());
        ImageLoaderJava.imageLoadUrlWithVodHolder(this.context, liveHomeViewHolder.image_movie, movieModel.getStream_icon(), R.drawable.default_bg, liveHomeViewHolder.image_logo);
        liveHomeViewHolder.btn_trailer.setVisibility(View.VISIBLE);
        liveHomeViewHolder.btn_trailer.setOnClickListener(view -> TrailerSearchActivity.open(this.context, movieModel.getName()));
        liveHomeViewHolder.itemView.setOnFocusChangeListener(new CastRecyclerAdapter$$ExternalSyntheticLambda1(this, movieModel, i, liveHomeViewHolder, 10));
        liveHomeViewHolder.itemView.setOnClickListener(new VodRecyclerAdapter$$ExternalSyntheticLambda0(this, movieModel, i, 13));
        liveHomeViewHolder.itemView.setOnTouchListener(new RecyclerVodHomeAdapter$$ExternalSyntheticLambda0(this, liveHomeViewHolder, movieModel, i, 0));
    }

    @NonNull
    public LiveHomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LiveHomeViewHolder(this, Insets$$ExternalSyntheticOutline0.m(viewGroup, R.layout.item_home_vod, viewGroup, false));
    }
}
