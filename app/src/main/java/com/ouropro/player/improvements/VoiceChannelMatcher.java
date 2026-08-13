package com.ouropro.player.improvements;

import com.ouropro.player.models.EPGChannel;

import java.util.ArrayList;
import java.util.List;

/** Resolve um texto falado contra a lista de canais já carregada no dispositivo. */
public final class VoiceChannelMatcher {
    private VoiceChannelMatcher() {
    }

    public static EPGChannel findUniqueMatch(Iterable<EPGChannel> channels, String rawQuery) {
        if (channels == null) {
            return null;
        }
        String query = VoiceCommand.normalize(rawQuery);
        if (query.isEmpty()) {
            return null;
        }

        EPGChannel exact = null;
        int exactCount = 0;
        EPGChannel candidate = null;
        int candidateScore = 0;
        int candidateCount = 0;

        for (EPGChannel channel : channels) {
            if (channel == null) {
                continue;
            }
            String name = VoiceCommand.normalize(channel.getName());
            String number = VoiceCommand.normalize(channel.getNum());
            if (query.equals(name) || query.equals(number)) {
                exact = channel;
                exactCount++;
                continue;
            }

            int score = 0;
            if (name.startsWith(query)) {
                score = 3;
            } else if (name.contains(query)) {
                score = 2;
            } else if (!number.isEmpty() && number.contains(query)) {
                score = 1;
            }
            if (score > 0) {
                if (score > candidateScore) {
                    candidate = channel;
                    candidateScore = score;
                    candidateCount = 1;
                } else if (score == candidateScore) {
                    candidateCount++;
                }
            }
        }

        if (exactCount > 0) {
            return exact;
        }
        if (candidateCount == 0) {
            return null;
        }
        return candidate;
    }

    /** Retorna um canal somente quando nome ou número completo coincide uma única vez. */
    public static EPGChannel findExactMatch(Iterable<EPGChannel> channels, String rawQuery) {
        if (channels == null) {
            return null;
        }
        String query = VoiceCommand.normalize(rawQuery);
        EPGChannel result = null;
        int count = 0;
        for (EPGChannel channel : channels) {
            if (channel == null) {
                continue;
            }
            String name = VoiceCommand.normalize(channel.getName());
            String number = VoiceCommand.normalize(channel.getNum());
            if (query.equals(name) || (!number.isEmpty() && query.equals(number))) {
                result = channel;
                count++;
            }
        }
        return count == 1 ? result : null;
    }

    /** Retorna todos os canais cujo nome ou número contém a consulta normalizada. */
    public static List<EPGChannel> findMatches(Iterable<EPGChannel> channels, String rawQuery) {
        List<EPGChannel> matches = new ArrayList<>();
        if (channels == null) {
            return matches;
        }
        String query = VoiceCommand.normalize(rawQuery);
        if (query.isEmpty()) {
            return matches;
        }
        for (EPGChannel channel : channels) {
            if (channel == null) {
                continue;
            }
            String name = VoiceCommand.normalize(channel.getName());
            String number = VoiceCommand.normalize(channel.getNum());
            if (name.contains(query) || (!number.isEmpty() && number.contains(query))) {
                matches.add(channel);
            }
        }
        return matches;
    }
}
