package com.ouropro.player.improvements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import iptv.m3u.parser.M3UItem;
import org.junit.Test;

public class M3USeriesNamingTest {
    private M3UItem item(String title, String group, String url) {
        M3UItem item = new M3UItem();
        item.setChannelName(title);
        item.setGroupTitle(group);
        item.setStreamURL(url);
        return item;
    }

    @Test
    public void recognizesCommonEpisodeMarkers() {
        assertTrue(M3USeriesNaming.isSeriesItem(item("The Office S01E02", "Series", "https://cdn.example/stream/1")));
        assertTrue(M3USeriesNaming.isSeriesItem(item("The Office 1x03", "TV", "https://cdn.example/stream/2")));
        assertTrue(M3USeriesNaming.isSeriesItem(item("The Office Temporada 1 Episódio 4", "TV", "https://cdn.example/stream/3")));
    }

    @Test
    public void extractsSeriesNameBeforeEpisodeMarker() {
        assertEquals("The Office", M3USeriesNaming.seriesName("The Office S01E02"));
        assertEquals("The Office", M3USeriesNaming.seriesName("The Office - 1x03 - Pilot"));
    }

    @Test
    public void extractsSeasonAndEpisodeNumbers() {
        assertEquals(1, M3USeriesNaming.seasonNumber("The Office S01E02"));
        assertEquals(2, M3USeriesNaming.episodeNumber("The Office S01E02"));
        assertEquals(2, M3USeriesNaming.seasonNumber("The Office 2x10"));
        assertEquals(10, M3USeriesNaming.episodeNumber("The Office 2x10"));
        assertEquals("The Office S02", M3USeriesNaming.seasonName("The Office Temporada 2 Episódio 10"));
    }

    @Test
    public void recognizesSeriesGroupWithoutUrlMarker() {
        assertTrue(M3USeriesNaming.isSeriesItem(item("The Office - Pilot", "Séries | Comédia", "https://cdn.example/stream/4")));
        assertFalse(M3USeriesNaming.isSeriesItem(item("Filme Ação", "Filmes", "https://cdn.example/movie/5.mp4")));
    }

    @Test
    public void doesNotClassifyMixedMovieSeriesBroadcastGroupAsSeries() {
        assertFalse(M3USeriesNaming.isSeriesItem(item("Space HD", "FILMES E SERIES", "https://cdn.example/45271.ts")));
        assertFalse(M3USeriesNaming.isSeriesItem(item("TNT Series HD", "FILMES E SERIES", "https://cdn.example/45298.ts")));
    }

    @Test
    public void preservesRealSeriesGroupsWithoutEpisodeMarker() {
        assertTrue(M3USeriesNaming.isSeriesItem(item("Arafta (Unidos pelo Destino)", "NOVELAS TURCAS LEGENDADAS", "https://cdn.example/series/1.mp4")));
        assertTrue(M3USeriesNaming.isSeriesItem(item("The Walking Dead", "24H SERIES", "https://cdn.example/series/2.mp4")));
    }

    @Test
    public void recognizesProviderGroupsWithEpisodeMarkers() {
        assertTrue(M3USeriesNaming.isSeriesItem(item("Reacher S04E03", "AMAZON PRIME VIDEO", "https://cdn.example/3.mp4")));
        assertTrue(M3USeriesNaming.isSeriesItem(item("O Urso S05E08", "STAR+", "https://cdn.example/4.mp4")));
        assertTrue(M3USeriesNaming.isSeriesItem(item("Silo S03E06", "APPLE TV+", "https://cdn.example/5.mp4")));
    }
}
