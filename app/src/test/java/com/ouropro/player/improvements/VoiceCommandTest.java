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
    public void rejectsUnknownCommands() {
        assertEquals(VoiceCommand.Action.UNKNOWN, VoiceCommand.parse("aumentar volume").getAction());
    }
}
