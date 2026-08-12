package com.ouropro.player.net;

import com.ouropro.player.models.MovieModel;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class FetchVideosTask extends NetworkTask<Void, Void, List<MovieModel>> {
    private LoadVideosCommand command;

    @Override // com.ouropro.player.net.NetworkTask
    public final List<MovieModel> doNetworkAction() throws JSONException, IOException {
        LoadVideosCommand loadVideosCommand = new LoadVideosCommand();
        this.command = loadVideosCommand;
        return loadVideosCommand.execute();
    }

    @Override // com.ouropro.player.net.NetworkTask, android.os.AsyncTask
    public final void onPreExecute() {
    }
}
