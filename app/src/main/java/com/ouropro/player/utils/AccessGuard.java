package com.ouropro.player.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.ouropro.player.apps.Constants;
import com.ouropro.player.apps.LTVApp;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.AppInfoModel;
import com.ouropro.player.remote.GetDataRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Bloqueio TOTAL do cliente.
 *
 * O app abre a tela inicial direto dos dados guardados no aparelho (início
 * rápido) e por isso nunca perguntava ao painel se o cliente continua liberado
 * -- um cliente bloqueado no painel seguia assistindo normalmente. Esta
 * verificação roda em segundo plano toda vez que a tela inicial abre (no máximo
 * a cada 2 minutos): se o painel disser que o cliente (cadastrado) está
 * bloqueado ou vencido, marca o acesso como bloqueado no aparelho e reabre o app
 * na tela de bloqueio. Falha de rede nunca bloqueia ninguém.
 */
public final class AccessGuard {
    private AccessGuard() {
    }

    private static final long MIN_INTERVAL_MS = 2 * 60 * 1000L;
    private static volatile long lastCheck = 0L;
    private static volatile boolean running = false;

    public static void checkAsync(Context context) {
        if (context == null) return;
        final Context app = context.getApplicationContext() != null ? context.getApplicationContext() : context;
        long now = System.currentTimeMillis();
        if (running || now - lastCheck < MIN_INTERVAL_MS) return;
        running = true;
        lastCheck = now;
        try {
            PreferenceHelper prefs = new PreferenceHelper(app);
            String data = Security.getStringData(Utils.getDeviceId(app), LTVApp.version_name, false, prefs.getSharedPreferenceDeviceType()).trim();
            GetDataRequest request = new GetDataRequest(app, 1000);
            request.setOnGetResponseListener(new GetDataRequest.OnGetResponseListener() {
                public void OnGetResponseResult(JSONObject json, int code) {
                    try {
                        handle(app, json);
                    } catch (Throwable ignored) {
                        // qualquer falha aqui deixa o app seguir normalmente.
                    } finally {
                        running = false;
                    }
                }
            });
            request.getResponse(Security.getJsonData(data), Constants.second_response_url);
        } catch (Throwable ignored) {
            running = false;
        }
    }

    private static void handle(final Context app, JSONObject json) throws Exception {
        if (json == null || !json.has("data")) return;
        JSONObject raw = new JSONObject(Security.getDecodedString(json.getString("data")));
        AppInfoModel fresh = (AppInfoModel) new Gson().fromJson(raw.toString(), AppInfoModel.class);
        if (fresh == null) return;

        // Cliente EXCLUÍDO do painel: o painel avisa "device_deleted". Só vale pra quem
        // era cliente cadastrado neste aparelho (teste e demonstração ficam de fora).
        if (raw.optBoolean("device_deleted", false)) {
            lockIfDeleted(app);
            return;
        }

        PreferenceHelper prefs = new PreferenceHelper(app);
        AppInfoModel cached = prefs.getSharedPreferenceAppInfo();

        // Sessão de TESTE (ou demonstração) deste aparelho: o botão TESTE cria o "lead" no painel
        // como Bloqueado e o teste vale pela data local (3 dias). Não pode derrubar o teste.
        if (cached != null && cached.isIs_google_pay()) return;

        // is_trial == 1 é MAC nunca cadastrado (sessão de TESTE local): a validade
        // do teste já é controlada pela data local, então não mexe aqui.
        boolean registered = fresh.getIs_trial() == 0;
        boolean blocked = registered && (fresh.getLock() == 1 || isPast(fresh.getExpiredDate()));

        if (blocked) {
            applyLock(app, cached != null ? cached : fresh);
        } else if (registered && cached != null && cached.getLock() == 1) {
            // estava bloqueado e o painel liberou de novo: grava os dados novos.
            prefs.setSharedPreferenceAppInfo(fresh);
            Utils.saveToFile(fresh);
        }
    }

    /**
     * O cliente deste aparelho foi excluído do painel. Se ele estava liberado como cliente
     * cadastrado (não é teste, não é demonstração), trava o acesso e reabre o app: a tela
     * inicial consulta o painel de novo e mostra a tela de quem não tem cadastro.
     * Devolve true se travou.
     */
    public static boolean lockIfDeleted(Context context) {
        try {
            if (context == null) return false;
            Context app = context.getApplicationContext() != null ? context.getApplicationContext() : context;
            AppInfoModel cached = new PreferenceHelper(app).getSharedPreferenceAppInfo();
            if (cached == null || cached.getIs_trial() != 0 || cached.getLock() == 1 || cached.isIs_google_pay()) {
                return false;
            }
            applyLock(app, cached);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void applyLock(final Context app, AppInfoModel target) {
        PreferenceHelper prefs = new PreferenceHelper(app);
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        target.setLock(1);
        target.setIs_trial(0);
        target.setIs_google_pay(false);
        target.setExpiredDate(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(yesterday.getTime()));
        prefs.setSharedPreferenceAppInfo(target);
        Utils.saveToFile(target);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                TestPlaylistClient.restartApp(app);
            }
        });
    }

    private static boolean isPast(String date) {
        try {
            long time = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date).getTime();
            return time - new Date().getTime() <= 0;
        } catch (Exception e) {
            return false;
        }
    }
}
