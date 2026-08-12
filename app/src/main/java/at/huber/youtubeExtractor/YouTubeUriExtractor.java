package at.huber.youtubeExtractor;

import android.content.Context;
import android.util.SparseArray;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public abstract class YouTubeUriExtractor extends YouTubeExtractor {
    public YouTubeUriExtractor(Context context) {
        super(context);
    }

    @Override // at.huber.youtubeExtractor.YouTubeExtractor
    public final void onExtractionComplete(SparseArray<YtFile> sparseArray, VideoMeta videoMeta) {
        onUrisAvailable(videoMeta.getVideoId(), videoMeta.getTitle(), sparseArray);
    }

    public abstract void onUrisAvailable(String str, String str2, SparseArray<YtFile> sparseArray);
}
