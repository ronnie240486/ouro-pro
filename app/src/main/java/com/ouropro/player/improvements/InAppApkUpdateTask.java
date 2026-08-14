package com.ouropro.player.improvements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipFile;

@SuppressWarnings("deprecation")
public final class InAppApkUpdateTask extends AsyncTask<String, Integer, InAppApkUpdateTask.Result> {
    public interface Listener {
        void onSuccess(File apk);
        void onFailure(String message);
    }

    public static final class Result {
        final File file;
        final String error;

        Result(File file, String error) {
            this.file = file;
            this.error = error;
        }

        static Result success(File file) {
            return new Result(file, null);
        }

        static Result failure(String error) {
            return new Result(null, error);
        }
    }

    private final Context context;
    private final Listener listener;
    private final ProgressDialog progress;
    private boolean sawNotNewerVersion;

    public InAppApkUpdateTask(Context context, String message, Listener listener) {
        this.context = context;
        this.listener = listener;
        this.progress = new ProgressDialog(context);
        this.progress.setMessage(message == null || message.isEmpty() ? "Baixando atualização..." : message);
        this.progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progress.setIndeterminate(true);
        this.progress.setCancelable(true);
    }

    @Override
    protected void onPreExecute() {
        progress.show();
    }

    @Override
    protected Result doInBackground(String... urls) {
        if (urls == null || urls.length == 0) {
            return Result.failure("Link de atualização vazio");
        }
        String lastError = "Nenhum APK válido foi encontrado";
        for (String candidate : urls) {
            if (isCancelled()) {
                return Result.failure("Download cancelado");
            }
            if (candidate == null || candidate.trim().isEmpty()) {
                continue;
            }
            Result attempt = downloadAndValidate(candidate.trim());
            if (attempt.file != null) {
                return attempt;
            }
            if (attempt.error != null && !attempt.error.isEmpty()) {
                lastError = attempt.error;
            }
        }
        if (sawNotNewerVersion) {
            return Result.failure("Seu APK já está na última versão");
        }
        return Result.failure("Não foi possível baixar uma atualização válida. Verifique o link direto do APK no painel. Último erro: " + lastError);
    }

    private Result downloadAndValidate(String rawUrl) {
        HttpURLConnection connection = null;
        File apk = new File(context.getExternalFilesDir(null), "ouropro-update.apk");
        try {
            URL url = new URL(rawUrl);
            if (!("http".equalsIgnoreCase(url.getProtocol()) || "https".equalsIgnoreCase(url.getProtocol()))) {
                return Result.failure("O link precisa ser HTTP ou HTTPS");
            }
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(90000);
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/vnd.android.package-archive, application/octet-stream");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.connect();
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                return Result.failure("Servidor respondeu HTTP " + code);
            }
            String contentType = connection.getContentType();
            int total = connection.getContentLength();
            if (total > 0 && total > 120 * 1024 * 1024) {
                return Result.failure("APK muito grande");
            }
            if (apk.exists() && !apk.delete()) {
                return Result.failure("Não foi possível preparar o arquivo");
            }
            try (InputStream input = new BufferedInputStream(connection.getInputStream());
                 BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(apk))) {
                byte[] buffer = new byte[16384];
                long copied = 0L;
                int read;
                while ((read = input.read(buffer)) != -1) {
                    if (isCancelled()) {
                        return Result.failure("Download cancelado");
                    }
                    copied += read;
                    if (copied > 120L * 1024L * 1024L) {
                        return Result.failure("APK muito grande");
                    }
                    output.write(buffer, 0, read);
                    if (total > 0) {
                        publishProgress((int) Math.min(100L, (copied * 100L) / total));
                    }
                }
                output.flush();
            }
            if (!isValidApk(apk)) {
                apk.delete();
                String type = contentType == null ? "tipo desconhecido" : contentType;
                return Result.failure("O servidor entregou " + type + ", não um APK OuroPro compatível");
            }
            return Result.success(apk);
        } catch (Exception e) {
            if (apk.exists()) {
                apk.delete();
            }
            return Result.failure("Falha no download: " + e.getClass().getSimpleName());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean isValidApk(File apk) throws Exception {
        if (!apk.isFile() || apk.length() < 1024L) {
            return false;
        }
        try (FileInputStream input = new FileInputStream(apk)) {
            if (input.read() != 'P' || input.read() != 'K') {
                return false;
            }
        }
        try (ZipFile zip = new ZipFile(apk)) {
            if (zip.getEntry("AndroidManifest.xml") == null) {
                return false;
            }
        }
        PackageInfo downloaded = context.getPackageManager().getPackageArchiveInfo(apk.getAbsolutePath(), 0);
        if (downloaded == null || !context.getPackageName().equals(downloaded.packageName)) {
            return false;
        }
        try {
            PackageInfo installed = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long downloadedCode = Build.VERSION.SDK_INT >= 28 ? downloaded.getLongVersionCode() : downloaded.versionCode;
            long installedCode = Build.VERSION.SDK_INT >= 28 ? installed.getLongVersionCode() : installed.versionCode;
            if (downloadedCode <= installedCode) {
                sawNotNewerVersion = true;
                return false;
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values != null && values.length > 0) {
            progress.setIndeterminate(false);
            progress.setMax(100);
            progress.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (progress.isShowing()) {
            progress.dismiss();
        }
        if (result.file != null) {
            listener.onSuccess(result.file);
        } else {
            listener.onFailure(result.error == null ? "Não foi possível atualizar" : result.error);
        }
    }
}
