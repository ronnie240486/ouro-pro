package com.rtx.DNS;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes2.dex */
public class DownloadImageTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "MainActivity";
    private Context context;
    private String[] urls;

    public DownloadImageTask(Context context, String[] urls) {
        this.context = context;
        this.urls = urls;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Boolean doInBackground(Void... voids) {
        int i = 0;
        while (true) {
            try {
                String[] strArr = this.urls;
                if (i < strArr.length) {
                    String imageUrl = strArr[i];
                    URL downloadUrl = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    File cacheDir = this.context.getCacheDir();
                    File outputFile = new File(cacheDir, "image" + i + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();
                    connection.disconnect();
                    i++;
                } else {
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error downloading image: " + e.getMessage());
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostExecute(Boolean result) {
        if (result.booleanValue()) {
            Log.d(TAG, "Images downloaded successfully!");
            File cacheDir = this.context.getCacheDir();
            for (int i = 0; i < this.urls.length; i++) {
                File downloadedFile = new File(cacheDir, "image" + i + ".jpg");
                File oldFile = new File(cacheDir, "old_image" + i + ".jpg");
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                if (downloadedFile.renameTo(oldFile)) {
                    Log.d(TAG, "Image " + i + " replaced successfully!");
                } else {
                    Log.e(TAG, "Failed to replace image " + i + ".");
                }
            }
            return;
        }
        Log.e(TAG, "Failed to download images.");
    }
}
