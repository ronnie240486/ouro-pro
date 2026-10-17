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
        AppInfoModel fresh = (AppInfoModel) new Gson().fromJson(
                new JSONObject(Security.getDecodedString(json.getString("data"))).toString(), AppInfoModel.class);
        if (fresh == null) return;

        PreferenceHelper prefs = new PreferenceHelper(app);
        AppInfoModel cached = prefs.getSharedPreferenceAppInfo();

        // is_trial == 1 é MAC nunca cadastrado (sessão de TESTE local): a validade
        // do teste já é controlada pela data local, então não mexe aqui.
        boolean registered = fresh.getIs_trial() == 0;
        boolean blocked = registered && (fresh.getLock() == 1 || isPast(fresh.getExpiredDate()));

        if (blocked) {
            AppInfoModel target = cached != null ? cached : fresh;
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
        } else if (registered && cached != null && cached.getLock() == 1) {
            // estava bloqueado e o painel liberou de novo: grava os dados novos.
            prefs.setSharedPreferenceAppInfo(fresh);
            Utils.saveToFile(fresh);
        }
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
