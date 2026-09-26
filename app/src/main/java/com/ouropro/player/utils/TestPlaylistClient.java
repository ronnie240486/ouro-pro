package com.ouropro.player.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.models.AppInfoModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Botão "TESTE" do OuroPro -- mesmo mecanismo já usado no Maximus/Future:
 * gera uma conta de teste de verdade num provedor externo (configurado no
 * painel como "gpcpro_server_url", com um link fixo de reserva caso o
 * painel não tenha isso configurado) e, em paralelo (melhor esforço, sem
 * travar nem atrasar o teste de verdade se falhar), registra o lead
 * (nome + WhatsApp) no NOSSO painel via /api/v5/maximus-test-result, com
 * app_id="ouropro" -- assim o lead aparece certo (como "Ouro Pro", não
 * "Maximus") no dashboard do revendedor.
 *
 * IMPORTANTE: diferente do Maximus (Kotlin, com uma "sessão" própria), o
 * OuroPro é um app Java antigo que guarda tudo dentro do próprio
 * AppInfoModel (SharedPreferences + arquivo local via Utils.saveToFile).
 * Por isso, aqui a lista de teste é inserida como MAIS UMA URL dentro de
 * appInfoModel.getResult() -- exatamente igual o que já acontece quando
 * alguém adiciona uma playlist manualmente em "Trocar lista" (ver
 * AddPlaylistDlgFragment.OnGetResponseResult) -- em vez de inventar um
 * jeito novo de guardar isso que o resto do app não saberia ler.
 */
public final class TestPlaylistClient {
    private TestPlaylistClient() {
    }

    private static final String PANEL_ROOT_PRIMARY = "https://renciaapp-production.up.railway.app";
    private static final String PANEL_ROOT_FALLBACK = "https://renciaapp.manus.space";
    // Mesmo provedor de teste externo usado pelo Maximus/Future -- só entra
    // em ação se o painel não tiver "gpcpro_server_url" configurado.
    private static final String TEST_REGISTER_FALLBACK = "https://nuvixtv.sigmab.pro/api/chatbot/Yen129WPEa/XYgD9JWr6V";
    private static final String USER_AGENT = "smart-tv";
    private static final String TEST_PLAYLIST_NAME = "TESTE";

    /**
     * Mostra o diálogo pedindo nome + WhatsApp e, ao confirmar, roda o
     * fluxo inteiro (rede em thread de fundo). Ao terminar com sucesso,
     * chama {@code onAppliedRestart} na UI thread -- normalmente pra
     * reabrir o app do zero já com a lista de teste aplicada.
     */
    public static void showTestLeadDialog(Context context, String mac, Runnable onAppliedRestart) {
        if (mac == null || mac.trim().isEmpty()) {
            Toast.makeText(context, "MAC do aparelho não identificado.", Toast.LENGTH_LONG).show();
            return;
        }
        final EditText nameInput = new EditText(context);
        nameInput.setHint("Seu nome");
        nameInput.setSingleLine(true);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        final EditText phoneInput = new EditText(context);
        phoneInput.setHint("WhatsApp (DDD + número)");
        phoneInput.setSingleLine(true);
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);

        int pad = (int) (24 * context.getResources().getDisplayMetrics().density);
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(pad, pad / 3, pad, 0);
        container.addView(nameInput);
        container.addView(phoneInput);

        final String macFinal = mac;
        new AlertDialog.Builder(context)
                .setTitle("Seu teste aqui")
                .setMessage("Informe seu nome e WhatsApp pra gerar o teste no painel.")
                .setView(container)
                .setPositiveButton("Gerar teste", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String phone = phoneInput.getText().toString().trim();
                    if (name.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(context, "Preencha nome e WhatsApp pra gerar o teste", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(context, "Gerando teste...", Toast.LENGTH_SHORT).show();
                    runTestFlow(context.getApplicationContext(), macFinal, name, phone, onAppliedRestart);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private static void runTestFlow(final Context appContext, final String mac, final String name, final String phone, final Runnable onAppliedRestart) {
        final Handler main = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            // 1) melhor esforço: registra o lead no NOSSO painel -- nunca
            // deve travar nem atrasar o teste de verdade se isso falhar.
            reportTestLead(mac, name, phone);

            // 2) pede a conta de teste de verdade pro provedor externo.
            JSONObject account = registerTestDevice(mac);
            String dns = account == null ? "" : account.optString("dns", "");
            String username = account == null ? "" : account.optString("username", "");
            String password = account == null ? "" : account.optString("password", "");
            if (dns.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
                main.post(() -> Toast.makeText(appContext, "Não foi possível gerar o teste agora. Tente novamente em instantes.", Toast.LENGTH_LONG).show());
                return;
            }
            String playlistUrl = buildPlaylistUrl(dns.trim(), username.trim(), password.trim());

            applyTestPlaylist(appContext, playlistUrl);
            main.post(() -> {
                Toast.makeText(appContext, "Teste gerado! Abrindo...", Toast.LENGTH_SHORT).show();
                if (onAppliedRestart != null) onAppliedRestart.run();
            });
        }).start();
    }

    private static String buildPlaylistUrl(String dns, String username, String password) {
        String server = dns.toLowerCase(Locale.ROOT).startsWith("http") ? dns : "http://" + dns;
        if (server.endsWith("/")) server = server.substring(0, server.length() - 1);
        try {
            return server + "/get.php?username=" + URLEncoder.encode(username, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8") + "&type=m3u_plus&output=ts";
        } catch (Exception e) {
            return server + "/get.php?username=" + username + "&password=" + password + "&type=m3u_plus&output=ts";
        }
    }

    /** Insere a lista de teste como playlist ativa, do mesmo jeito que "Adicionar playlist" já faz. */
    private static void applyTestPlaylist(Context context, String playlistUrl) {
        PreferenceHelper prefs = new PreferenceHelper(context);
        AppInfoModel info = prefs.getSharedPreferenceAppInfo();
        if (info == null) info = new AppInfoModel();

        List<AppInfoModel.UrlModel> urls = new ArrayList<>(info.getResult());
        // remove um "TESTE" anterior pra não acumular listas de teste velhas.
        for (int i = urls.size() - 1; i >= 0; i--) {
            if (TEST_PLAYLIST_NAME.equalsIgnoreCase(urls.get(i).getName())) urls.remove(i);
        }
        AppInfoModel.UrlModel testModel = new AppInfoModel.UrlModel();
        testModel.setId("teste-" + System.currentTimeMillis());
        testModel.setName(TEST_PLAYLIST_NAME);
        testModel.setUrl(playlistUrl);
        testModel.setType("general");
        urls.add(0, testModel);
        info.setResult(urls);

        // Libera a tela caso o registro local ainda estivesse marcado como
        // bloqueado/expirado -- sem isso o teste ficaria preso na mesma
        // tela de bloqueio de onde a pessoa acabou de sair.
        info.setLock(0);
        info.setIs_google_pay(true);
        Calendar farFuture = Calendar.getInstance();
        farFuture.add(Calendar.DAY_OF_YEAR, 3);
        info.setExpiredDate(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(farFuture.getTime()));

        prefs.setSharedPreferenceAppInfo(info);
        Utils.saveToFile(info);
        prefs.setSharedPreferencePlaylistPosition(0);
    }

    private static void reportTestLead(String mac, String name, String phone) {
        try {
            postJson(PANEL_ROOT_PRIMARY + "/api/v5/maximus-test-result", leadPayload(mac, name, phone));
            return;
        } catch (Exception ignored) {
            // cai pro fallback abaixo
        }
        try {
            postJson(PANEL_ROOT_FALLBACK + "/api/v5/maximus-test-result", leadPayload(mac, name, phone));
        } catch (Exception ignored) {
            // best-effort -- nunca deve travar o teste de verdade.
        }
    }

    private static JSONObject leadPayload(String mac, String name, String phone) throws Exception {
        JSONObject body = new JSONObject();
        body.put("mac", mac);
        body.put("name", name);
        body.put("phone", phone);
        body.put("app_id", "ouropro");
        return body;
    }

    private static JSONObject registerTestDevice(String mac) {
        String registerUrl = resolveRegisterUrl(mac);
        try {
            JSONObject body = new JSONObject();
            body.put("mac", mac);
            String separator = registerUrl.contains("?") ? "&" : "?";
            return postJson(registerUrl + separator + "mac=" + URLEncoder.encode(mac, "UTF-8"), body);
        } catch (Exception e) {
            return null;
        }
    }

    /** Railway primeiro, cai pro Manus, e por último pro link fixo de reserva. */
    private static String resolveRegisterUrl(String mac) {
        try {
            String guim = getText(PANEL_ROOT_PRIMARY + "/api/guim.php?mac=" + URLEncoder.encode(mac, "UTF-8"));
            String url = new JSONObject(guim).optString("gpcpro_server_url", "").trim();
            if (!url.isEmpty()) return url;
        } catch (Exception ignored) {
            // tenta o próximo
        }
        try {
            String guim = getText(PANEL_ROOT_FALLBACK + "/api/guim.php?mac=" + URLEncoder.encode(mac, "UTF-8"));
            String url = new JSONObject(guim).optString("gpcpro_server_url", "").trim();
            if (!url.isEmpty()) return url;
        } catch (Exception ignored) {
            // usa o fallback fixo
        }
        return TEST_REGISTER_FALLBACK;
    }

    private static String getText(String urlString) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("Accept", "application/json, text/plain, */*");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        try {
            return readStream(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }

    private static JSONObject postJson(String urlString, JSONObject body) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept", "application/json, text/plain, */*");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        OutputStream out = connection.getOutputStream();
        try {
            out.write(body.toString().getBytes(StandardCharsets.UTF_8));
        } finally {
            out.close();
        }
        int code = connection.getResponseCode();
        InputStream stream = (code >= 200 && code < 300) ? connection.getInputStream() : connection.getErrorStream();
        String text = stream == null ? "" : readStream(stream);
        connection.disconnect();
        if (code < 200 || code >= 300) throw new Exception("HTTP " + code);
        return text.trim().isEmpty() ? new JSONObject() : new JSONObject(text);
    }

    private static String readStream(InputStream stream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return sb.toString();
    }

    /**
     * Reabre o app do zero pela própria tela inicial dele -- mais simples e
     * seguro do que tentar adivinhar qual Activity específica chamou o
     * diálogo de teste (DescriptionDlgFragment e NoConnectionDlgFragment
     * aparecem em vários lugares diferentes do app).
     */
    public static void restartApp(Context context) {
        android.content.Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent != null) {
            launchIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(launchIntent);
        }
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).finish();
        }
    }
}
