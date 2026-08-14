package com.ouropro.player.improvements;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** Downloader interno de APK com validação antes da instalação. */
public final class InAppApkUpdateTask extends AsyncTask<String, Integer, InAppApkUpdateTask.Result> {
    public interface Listener {
        void onStarted();
        void onProgress(int progress);
        void onLatest(String message);
        void onReady(File file);
        void onError(String message);
    }

    public static final class Result {
        private final File file;
        private final String error;
        private final long installedVersion;
        private final long availableVersion;
        private final boolean latest;

        private Result(File file, String error, long installedVersion, long availableVersion, boolean latest) {
            this.file = file;
            this.error = error;
            this.installedVersion = installedVersion;
            this.availableVersion = availableVersion;
            this.latest = latest;
        }

        static Result ready(File file, long installed, long available) {
            return new Result(file, null, installed, available, false);
        }

        static Result latest(long installed, long available) {
            return new Result(null, null, installed, available, true);
        }

        static Result error(String message) {
            return new Result(null, message, -1L, -1L, false);
        }
    }

    private final Context context;
    private final Listener listener;

    public InAppApkUpdateTask(Context context, Listener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onStarted();
        }
    }

    @Override
    protected Result doInBackground(String... urls) {
        if (urls == null || urls.length == 0 || urls[0] == null || urls[0].trim().isEmpty()) {
            return Result.error("Link de atualização vazio.");
        }
        File target = new File(context.getExternalFilesDir(null), "ouropro-update.apk");
        if (target.exists() && !target.delete()) {
            return Result.error("Não foi possível substituir o arquivo temporário da atualização.");
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urls[0].trim()).openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(60000);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/vnd.android.package-archive, application/octet-stream, */*");
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                return Result.error("O servidor retornou HTTP " + code + ".");
            }
            long contentLength = connection.getContentLengthLong();
            try (InputStream input = connection.getInputStream(); FileOutputStream output = new FileOutputStream(target)) {
                byte[] buffer = new byte[32768];
                long copied = 0L;
                int read;
                while ((read = input.read(buffer)) != -1) {
                    if (isCancelled()) {
                        return Result.error("Download cancelado.");
                    }
                    output.write(buffer, 0, read);
                    copied += read;
                    if (contentLength > 0L) {
                        publishProgress((int) Math.min(100L, copied * 100L / contentLength));
                    }
                }
            }
            if (target.length() < 4096L || !looksLikeZip(target)) {
                return Result.error("O link retornou uma página ou um arquivo que não é APK.");
            }
            PackageManager packageManager = context.getPackageManager();
            PackageInfo installed = packageManager.getPackageInfo(context.getPackageName(), 0);
            PackageInfo downloaded = packageManager.getPackageArchiveInfo(target.getAbsolutePath(), PackageManager.GET_META_DATA);
            if (downloaded == null || downloaded.applicationInfo == null) {
                return Result.error("O arquivo baixado não contém um APK Android válido.");
            }
            if (!context.getPackageName().equals(downloaded.packageName)) {
                return Result.error("O APK baixado pertence a outro aplicativo.");
            }
            long installedVersion = getVersionCode(installed);
            long availableVersion = getVersionCode(downloaded);
            if (availableVersion <= installedVersion) {
                return Result.latest(installedVersion, availableVersion);
            }
            return Result.ready(target, installedVersion, availableVersion);
        } catch (Exception error) {
            return Result.error("Não foi possível baixar o APK: " + error.getClass().getSimpleName() + ".");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean looksLikeZip(File file) {
        try (FileInputStream input = new FileInputStream(file)) {
            return input.read() == 'P' && input.read() == 'K';
        } catch (Exception ignored) {
            return false;
        }
    }

    private long getVersionCode(PackageInfo info) {
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            return info.getLongVersionCode();
        }
        return info.versionCode;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (listener != null && values != null && values.length > 0) {
            listener.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (listener == null || result == null) {
            return;
        }
        if (result.latest) {
            listener.onLatest("Seu APK já está na última versão.\nInstalada: v" + result.installedVersion + "\nEncontrada no link: v" + result.availableVersion);
        } else if (result.file != null) {
            listener.onReady(result.file);
        } else {
            listener.onError(result.error == null ? "Não foi possível validar a atualização." : result.error);
        }
    }
}
