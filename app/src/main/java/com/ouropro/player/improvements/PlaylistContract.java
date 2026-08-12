package com.ouropro.player.improvements;

import com.google.gson.annotations.SerializedName;

/** Contrato estável para playlists remotas e locais. */
public final class PlaylistContract {
    private PlaylistContract() {
    }

    public static final String URL_FIELD = "playlist_url";
    public static final String NAME_FIELD = "playlist_name";

    public static final class Payload {
        @SerializedName(URL_FIELD)
        public final String playlistUrl;

        @SerializedName(NAME_FIELD)
        public final String playlistName;

        public Payload(String playlistUrl, String playlistName) {
            this.playlistUrl = playlistUrl;
            this.playlistName = playlistName;
        }
    }
}
