package com.ouropro.player.net;

import com.ouropro.player.models.EPGChannel;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class FetchChannelsTask extends NetworkTask<Void, Void, List<EPGChannel>> {
    private LoadChannelsCommand command;

    public final List<EPGChannel> doNetworkAction() throws JSONException, IOException {
        LoadChannelsCommand loadChannelsCommand = new LoadChannelsCommand();
        this.command = loadChannelsCommand;
        return loadChannelsCommand.execute();
    }

    public final void onPreExecute() {
    }
}
