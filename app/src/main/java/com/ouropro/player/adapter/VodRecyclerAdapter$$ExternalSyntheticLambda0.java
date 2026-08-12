package com.ouropro.player.adapter;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.ouropro.player.models.CastModel;
import com.ouropro.player.models.CatchUpEpg;
import com.ouropro.player.models.CatchupModel;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.EpisodeModel;
import com.ouropro.player.models.MovieCreditModel;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SeriesModel;

/* JADX INFO: compiled from: R8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class VodRecyclerAdapter$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ int $r8$classId;
    public final /* synthetic */ RecyclerView.Adapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Object f$2;

    public /* synthetic */ VodRecyclerAdapter$$ExternalSyntheticLambda0(RecyclerView.Adapter adapter, int i, Object obj, int i2) {
        this.$r8$classId = i2;
        this.f$0 = adapter;
        this.f$1 = i;
        this.f$2 = obj;
    }

    public final void onClick(View view) {
        switch (this.$r8$classId) {
            case 0:
                ((VodRecyclerAdapter) this.f$0).lambda$onBindViewHolder$0(this.f$1, (MovieModel) this.f$2, view);
                break;
            case 1:
                ((CastRecyclerAdapter) this.f$0).lambda$onBindViewHolder$1((CastModel) this.f$2, this.f$1, view);
                break;
            case 2:
                ((DateRecyclerAdapter) this.f$0).lambda$onBindViewHolder$1(this.f$1, (CatchupModel) this.f$2, view);
                break;
            case 3:
                ((EpisodeHorizontalRecyclerAdapter) this.f$0).lambda$onBindViewHolder$0((EpisodeModel) this.f$2, this.f$1, view);
                break;
            case 4:
                ((EpisodeRecyclerAdapter) this.f$0).lambda$onBindViewHolder$1((EpisodeModel) this.f$2, this.f$1, view);
                break;
            case 5:
                ((MovieCreditRecyclerAdapter) this.f$0).lambda$onBindViewHolder$0(this.f$1, (MovieCreditModel) this.f$2, view);
                break;
            case 6:
                ((PlayEpisodeRecyclerAdapter) this.f$0).lambda$onBindViewHolder$0((EpisodeModel) this.f$2, this.f$1, view);
                break;
            case 7:
                ((ProgramRecyclerAdapter) this.f$0).lambda$onBindViewHolder$1((CatchUpEpg) this.f$2, this.f$1, view);
                break;
            case 8:
                ((RecyclerLiveCategoryAdapter) this.f$0).lambda$onBindViewHolder$1(this.f$1, (CategoryModel) this.f$2, view);
                break;
            case 9:
                ((RecyclerLiveChannelAdapter) this.f$0).lambda$onBindViewHolder$0(this.f$1, (EPGChannel) this.f$2, view);
                break;
            case 10:
                ((RecyclerLiveHomeAdapter) this.f$0).lambda$onBindViewHolder$1((EPGChannel) this.f$2, this.f$1, view);
                break;
            case 11:
                ((RecyclerSeriesHomeAdapter) this.f$0).lambda$onBindViewHolder$1((SeriesModel) this.f$2, this.f$1, view);
                break;
            case 12:
                ((RecyclerVodCategoryAdapter) this.f$0).lambda$onBindViewHolder$1(this.f$1, (CategoryModel) this.f$2, view);
                break;
            case 13:
                ((RecyclerVodHomeAdapter) this.f$0).lambda$onBindViewHolder$1((MovieModel) this.f$2, this.f$1, view);
                break;
            default:
                ((SeriesRecyclerAdapter) this.f$0).lambda$onBindViewHolder$0(this.f$1, (SeriesModel) this.f$2, view);
                break;
        }
    }

    public /* synthetic */ VodRecyclerAdapter$$ExternalSyntheticLambda0(RecyclerView.Adapter adapter, Object obj, int i, int i2) {
        this.$r8$classId = i2;
        this.f$0 = adapter;
        this.f$2 = obj;
        this.f$1 = i;
    }
}
