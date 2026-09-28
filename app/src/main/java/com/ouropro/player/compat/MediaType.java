package com.ouropro.player.compat;

// BUG corrigido: mesma causa do Transition.java desta pasta -- esse arquivo
// existia em app/src/main/java/org/androidannotations/api/rest/MediaType.java,
// com o MESMO pacote/nome da classe real da biblioteca
// org.androidannotations:androidannotations-api:4.8.0 (dependência real,
// declarada no build.gradle). Usado em GetSubtitleLoginRequest.java e
// GetSubtitleLinkRequest.java só pelas duas constantes abaixo -- movido pra
// um pacote só do app pra não colidir mais com a biblioteca de verdade no
// merge do dex de release (mesmo erro "duplicate class").
public final class MediaType {
    public static final String APPLICATION_JSON = "application/json";
    public static final String ALL = "*/*";
    private MediaType() { }
}
