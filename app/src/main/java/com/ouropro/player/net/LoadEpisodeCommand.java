package com.ouropro.player.net;

import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.models.EpisodeModel;
import iptv.m3u.parser.M3UItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class LoadEpisodeCommand {
    public ArrayList<EpisodeModel> execute() throws IOException {
        LTVApp lTVApp = LTVApp.getInstance();
        ArrayList<EpisodeModel> arrayList = new ArrayList<>();
        Iterator<M3UItem> it = lTVApp.getM3USeriesItems().iterator();
        while (it.hasNext()) {
            EpisodeModel episodeModelFromM3UItem = EpisodeModel.fromM3UItem(it.next());
            if (episodeModelFromM3UItem != null) {
                arrayList.add(episodeModelFromM3UItem);
            }
        }
        return arrayList;
    }
}
