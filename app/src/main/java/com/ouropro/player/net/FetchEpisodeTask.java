package com.ouropro.player.net;

import com.ouropro.player.models.EpisodeModel;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class FetchEpisodeTask extends NetworkTask<Void, Void, List<EpisodeModel>> {
    private LoadEpisodeCommand command;

    public final List<EpisodeModel> doNetworkAction() throws JSONException, IOException {
        LoadEpisodeCommand loadEpisodeCommand = new LoadEpisodeCommand();
        this.command = loadEpisodeCommand;
        return loadEpisodeCommand.execute();
    }

    public final void onPreExecute() {
    }
}
