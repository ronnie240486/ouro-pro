package at.huber.youtubeExtractor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Compatibilidade para o extrator legado; a reprodução principal usa ExoPlayer diretamente. */
public abstract class YouTubeExtractor extends AsyncTask<String, Void, SparseArray<YtFile>> {
    protected final Context context;

    public YouTubeExtractor(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected SparseArray<YtFile> doInBackground(String... urls) {
        return null;
    }

    public void extract(String url, boolean includeWebM, boolean parseDashManifest) {
        execute(url);
    }

    public void extract(String url) {
        execute(url);
    }

    @Override
    protected void onPostExecute(SparseArray<YtFile> result) {
        onExtractionComplete(result, null);
    }

    public abstract void onExtractionComplete(@Nullable SparseArray<YtFile> files, @Nullable VideoMeta videoMeta);

    public void setDefaultHttpProtocol(boolean enabled) { }
    public void setIncludeWebM(boolean enabled) { }
    public void setParseDashManifest(boolean enabled) { }
}
