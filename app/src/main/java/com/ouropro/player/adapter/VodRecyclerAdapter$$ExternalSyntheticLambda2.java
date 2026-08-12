package com.ouropro.player.adapter;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SeriesModel;
import io.realm.RealmObject;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class VodRecyclerAdapter$$ExternalSyntheticLambda2 implements View.OnLongClickListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ RecyclerView.Adapter f$0;
    public final /* synthetic */ RealmObject f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ VodRecyclerAdapter$$ExternalSyntheticLambda2(RecyclerView.Adapter adapter, RealmObject realmObject, int i, int i2) {
        this.$r8$classId = i2;
        this.f$0 = adapter;
        this.f$1 = realmObject;
        this.f$2 = i;
    }

    public final boolean onLongClick(View view) {
        switch (this.$r8$classId) {
            case 0:
                return ((VodRecyclerAdapter) this.f$0).lambda$onBindViewHolder$2((MovieModel) this.f$1, this.f$2, view);
            case 1:
                return ((RecyclerLiveChannelAdapter) this.f$0).lambda$onBindViewHolder$2((EPGChannel) this.f$1, this.f$2, view);
            default:
                return ((SeriesRecyclerAdapter) this.f$0).lambda$onBindViewHolder$2((SeriesModel) this.f$1, this.f$2, view);
        }
    }
}
