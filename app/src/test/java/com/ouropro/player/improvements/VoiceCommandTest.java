package com.ouropro.player.improvements;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoiceCommandTest {
    @Test
    public void parsesOpenChannelCommandInPortuguese() {
        VoiceCommand command = VoiceCommand.parse("Abrir o canal São Paulo");

        assertEquals(VoiceCommand.Action.OPEN_CHANNEL, command.getAction());
        assertEquals("sao paulo", command.getQuery());
    }

    @Test
    public void parsesSearchCommandInPortuguese() {
        VoiceCommand command = VoiceCommand.parse("Pesquisar canal notícias");

        assertEquals(VoiceCommand.Action.SEARCH_CHANNEL, command.getAction());
        assertEquals("noticias", command.getQuery());
    }

    @Test
    public void recognizesLiveNavigation() {
        assertEquals(VoiceCommand.Action.OPEN_LIVE, VoiceCommand.parse("abrir canais").getAction());
    }

    @Test
    public void parsesNavigationAndPlaybackCommands() {
        assertEquals(VoiceCommand.Action.OPEN_MOVIES, VoiceCommand.parse("abrir filmes").getAction());
        assertEquals(VoiceCommand.Action.OPEN_SERIES, VoiceCommand.parse("abrir séries").getAction());
        assertEquals(VoiceCommand.Action.NEXT_CHANNEL, VoiceCommand.parse("próximo canal").getAction());
        assertEquals(VoiceCommand.Action.PREVIOUS_CHANNEL, VoiceCommand.parse("canal anterior").getAction());
        assertEquals(VoiceCommand.Action.PAUSE, VoiceCommand.parse("pausar").getAction());
        assertEquals(VoiceCommand.Action.PLAY, VoiceCommand.parse("continuar").getAction());
    }

    @Test
    public void parsesMovieAndSeriesTitles() {
        VoiceCommand movie = VoiceCommand.parse("Abrir filme Titanic");
        assertEquals(VoiceCommand.Action.OPEN_MOVIE_ITEM, movie.getAction());
        assertEquals("titanic", movie.getQuery());

        VoiceCommand series = VoiceCommand.parse("Abrir série The Last of Us");
        assertEquals(VoiceCommand.Action.OPEN_SERIES_ITEM, series.getAction());
        assertEquals("the last of us", series.getQuery());
    }

    @Test
    public void parsesMediaSearchCommands() {
        assertEquals(VoiceCommand.Action.SEARCH_MOVIE, VoiceCommand.parse("Pesquisar filme ação").getAction());
        assertEquals(VoiceCommand.Action.SEARCH_SERIES, VoiceCommand.parse("Buscar série policial").getAction());
    }

    @Test
    public void normalizesChannelNamesWithSpaces() {
        VoiceCommand command = VoiceCommand.parse("Abrir canal Space HD");
        assertEquals(VoiceCommand.Action.OPEN_CHANNEL, command.getAction());
        assertEquals("space hd", command.getQuery());
    }

    @Test
    public void treatsUntypedPhraseAsTitle() {
        VoiceCommand command = VoiceCommand.parse("Esqueceram de Mim");
        assertEquals(VoiceCommand.Action.OPEN_TITLE, command.getAction());
        assertEquals("esqueceram de mim", command.getQuery());
    }

    @Test
    public void rejectsEmptyCommands() {
        assertEquals(VoiceCommand.Action.UNKNOWN, VoiceCommand.parse("").getAction());
    }
}
