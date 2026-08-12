package com.ouropro.player.improvements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

/** Reconhece uma frase curta e a entrega como comando determinístico. */
public final class VoiceCommandController implements RecognitionListener {
    public interface Listener {
        void onVoiceCommand(VoiceCommand command);

        void onVoiceState(String state);

        void onVoiceError(String message);
    }

    private final Context context;
    private final Listener listener;
    private SpeechRecognizer recognizer;
    private boolean listening;

    public VoiceCommandController(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public static boolean isAvailable(Context context) {
        return SpeechRecognizer.isRecognitionAvailable(context);
    }

    public void start() {
        if (!isAvailable(context)) {
            listener.onVoiceError("O reconhecimento de voz não está disponível neste dispositivo");
            return;
        }
        if (recognizer == null) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context);
            recognizer.setRecognitionListener(this);
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        listening = true;
        listener.onVoiceState("Ouvindo… diga, por exemplo: abrir canal notícias");
        try {
            recognizer.startListening(intent);
        } catch (RuntimeException exception) {
            listening = false;
            listener.onVoiceError("Não foi possível iniciar o microfone; tente novamente");
        }
    }

    public void stop() {
        listening = false;
        if (recognizer != null) {
            recognizer.stopListening();
        }
    }

    public void destroy() {
        listening = false;
        if (recognizer != null) {
            recognizer.destroy();
            recognizer = null;
        }
    }

    public void onResults(Bundle results) {
        listening = false;
        ArrayList<String> matches = results == null
                ? null : results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null || matches.isEmpty()) {
            listener.onVoiceError("Não entendi o comando");
            return;
        }
        listener.onVoiceCommand(VoiceCommand.parse(matches.get(0)));
    }

    public void onError(int error) {
        listening = false;
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Não foi possível acessar o microfone";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Permissão de microfone não concedida";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "O serviço de voz não respondeu; tente novamente";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "Não entendi o comando";
                break;
            default:
                message = "Não foi possível reconhecer o comando";
                break;
        }
        listener.onVoiceError(message);
    }

    public void onReadyForSpeech(Bundle params) {
    }

    public void onBeginningOfSpeech() {
    }

    public void onRmsChanged(float rmsdB) {
    }

    public void onBufferReceived(byte[] buffer) {
    }

    public void onEndOfSpeech() {
        if (listening) {
            listener.onVoiceState("Processando comando…");
        }
    }

    public void onPartialResults(Bundle partialResults) {
    }

    public void onEvent(int eventType, Bundle params) {
    }
}
