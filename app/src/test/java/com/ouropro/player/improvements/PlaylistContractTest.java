package com.ouropro.player.improvements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.gson.Gson;

import org.junit.Test;

public class PlaylistContractTest {
    @Test
    public void serializesStablePlaylistFields() {
        String json = new Gson().toJson(new PlaylistContract.Payload(
                "https://example.test/playlist.m3u",
                "Minha Playlist"));

        assertEquals("https://example.test/playlist.m3u", new Gson().fromJson(json, PlaylistJson.class).playlistUrl);
        assertEquals("Minha Playlist", new Gson().fromJson(json, PlaylistJson.class).playlistName);
        assertFalse(json.contains("\"url\""));
        assertFalse(json.contains("\"name\""));
    }

    private static final class PlaylistJson {
        String playlistUrl;
        String playlistName;
    }
}
