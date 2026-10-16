package com.ouropro.player.utils;

import java.util.Locale;

/**
 * Acesso de DEMONSTRAÇÃO para a revisão do Google Play.
 *
 * Problema real (rejeição "Falta de credenciais de login"): o Ouro Pro não tem
 * usuário e senha -- ele é ativado pelo MAC do aparelho. O revisor do Google
 * abre o app, cai na tela "Trial is ended" e o botão TESTE depende de um provedor
 * externo que falhou pra ele ("Não foi possível gerar o teste agora"). Sem
 * conseguir ver o conteúdo, o Google recusa a atualização.
 *
 * Como funciona: na tela do TESTE, em vez de um nome qualquer, o revisor digita
 * o CÓDIGO abaixo no campo "Seu nome" (o WhatsApp pode ser qualquer número). Se
 * o código bater, o app aplica a lista de demonstração e abre normalmente. Esse
 * código e as instruções vão SÓ no Play Console, em "Detalhes de login".
 * Qualquer outro nome segue o fluxo normal do teste, sem nenhuma mudança.
 *
 * Fica DESLIGADO enquanto DEMO_PLAYLIST_URL estiver vazio.
 */
public final class ReviewAccess {
    private ReviewAccess() {
    }

    /** Código que o revisor digita no campo "Seu nome". */
    private static final String ACCESS_CODE = "REVISAOGOOGLE";

    /**
     * Lista de demonstração (URL M3U/Xtream completa, ex.:
     * http://servidor:porta/get.php?username=...&password=...&type=m3u_plus&output=ts).
     * Use uma linha só pra revisão, com poucas conexões -- ela fica dentro do app.
     */
    private static final String DEMO_PLAYLIST_URL = "";

    /** Devolve a lista de demonstração se o texto digitado for o código; senão, null. */
    public static String playlistFor(String typedName) {
        if (DEMO_PLAYLIST_URL.trim().isEmpty() || typedName == null) return null;
        String normalized = typedName.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
        return normalized.equals(ACCESS_CODE) ? DEMO_PLAYLIST_URL.trim() : null;
    }
}
