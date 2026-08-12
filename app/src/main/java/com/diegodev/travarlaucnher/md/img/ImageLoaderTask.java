package com.diegodev.travarlaucnher.md.img;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes2.dex */
public class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
    private static final String IMAGE_DIR = "task/img/";
    private static final String TAG = "ImageLoaderTask";
    private final Context context;
    private final ImageView imageView;
    private final String reference;
    private String urlString;

    public ImageLoaderTask(ImageView imageView, Context context, String reference) {
        if (imageView == null || context == null || reference == null || reference.isEmpty()) {
            throw new IllegalArgumentException("Parâmetros inválidos para ImageLoaderTask");
        }
        this.imageView = imageView;
        this.context = context;
        this.reference = reference;
    }

    private String getDeviceId() {
        String uid = CryptoHelper.getDeviceIdentifier(this.context);
        return uid;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Bitmap doInBackground(String... params) {
        if (params == null || params.length == 0 || params[0] == null || params[0].isEmpty()) {
            Log.e(TAG, "URL da imagem está vazia ou nula.");
            return null;
        }
        this.urlString = params[0];
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL url = new URL(this.urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestProperty("device-id", getDeviceId());
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                Log.e(TAG, "Erro ao carregar imagem. Código HTTP: " + responseCode);
                return null;
            }
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            if (bitmap != null) {
                saveImageLocally(bitmap);
            }
            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar imagem", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e2) {
                    Log.e(TAG, "Erro ao fechar o InputStream", e2);
                }
            }
            if (0 != 0) {
                connection.disconnect();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.AsyncTask
    public void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Falha ao carregar a imagem final.");
            this.imageView.setImageResource(R.color.holo_red_dark);
        } else {
            this.imageView.setImageBitmap(bitmap);
            Log.d(TAG, "Imagem final carregada e definida no ImageView.");
        }
    }

    private void saveImageLocally(Bitmap bitmap) {
        File imageDir = new File(this.context.getFilesDir(), IMAGE_DIR);
        if (!imageDir.exists()) {
            boolean created = imageDir.mkdirs();
            if (!created) {
                Log.e(TAG, "Falha ao criar diretório para imagens: " + imageDir.getAbsolutePath());
                return;
            }
        }
        File tempFile = new File(imageDir, this.reference + "_temp.png");
        File finalFile = new File(imageDir, this.reference + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                if (finalFile.exists()) {
                    boolean deleted = finalFile.delete();
                    if (!deleted) {
                        Log.e(TAG, "Falha ao deletar o arquivo antigo: " + finalFile.getAbsolutePath());
                        fos.close();
                        return;
                    }
                }
                boolean renamed = tempFile.renameTo(finalFile);
                if (!renamed) {
                    Log.e(TAG, "Falha ao renomear o arquivo temporário para: " + finalFile.getAbsolutePath());
                } else {
                    Log.d(TAG, "Imagem salva com sucesso: " + finalFile.getAbsolutePath());
                }
                fos.close();
            } catch (Throwable th) {
                try {
                    fos.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao salvar a imagem localmente", e);
        }
    }
}
