package com.ouropro.player.models;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;

import org.junit.Test;

public class AppInfoModelPlaylistTest {
    @Test
    public void acceptsPanelPlaylistAliases() {
        String json = "{\"playlists\":[{\"playlist_url\":\"http://example.test/list.m3u\",\"playlist_name\":\"Minha lista\"}]}";
        AppInfoModel model = new Gson().fromJson(json, AppInfoModel.class);
        assertEquals(1, model.getResult().size());
        assertEquals("http://example.test/list.m3u", model.getResult().get(0).getUrl());
        assertEquals("Minha lista", model.getResult().get(0).getName());
    }
}
