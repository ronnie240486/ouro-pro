package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/* JADX INFO: loaded from: classes2.dex */
public class LogoMovie extends ImageView {
    private static final String TAG = "LogoMovie";
    private static LogoMovie instance;

    public LogoMovie(Context context) {
        super(context);
        instance = this;
    }

    public LogoMovie(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = this;
    }

    public LogoMovie(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        instance = this;
    }

    public static void setImageFromUrl(String imageUrl) {
        if (instance == null) {
            Log.e(TAG, "Instância de LogoMovie ainda não foi inicializada.");
            return;
        }
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            Log.d(TAG, "URL da imagem está vazia. Definindo visibilidade como GONE.");
            instance.setVisibility(8);
        } else {
            instance.setVisibility(0);
            new ImageLoaderTask(instance).execute(imageUrl);
        }
    }

    private static class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public ImageLoaderTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Code duplicated, block: B:20:0x0074 A[Catch: Exception -> 0x0070, TRY_LEAVE, TryCatch #0 {Exception -> 0x0070, blocks: (B:16:0x006c, B:20:0x0074), top: B:35:0x006c }] */
        /* JADX WARN: Code duplicated, block: B:30:0x0088 A[Catch: Exception -> 0x0084, TRY_LEAVE, TryCatch #3 {Exception -> 0x0084, blocks: (B:26:0x0080, B:30:0x0088), top: B:40:0x0080 }] */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(String... params) {
            String imageUrl = params[0];
            HttpURLConnection connection = null;
            InputStream input = null;
            try {
                try {
                    URL url = new URL(imageUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setUseCaches(true);
                    connection.connect();
                    input = new BufferedInputStream(connection.getInputStream());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inSampleSize = 2;
                    Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(input, null, options);
                    try {
                        input.close();
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (Exception e) {
                        Log.e(LogoMovie.TAG, "Erro ao fechar os recursos da conexão.", e);
                    }
                    return bitmapDecodeStream;
                } catch (Throwable th) {
                    if (input != null) {
                        try {
                            input.close();
                            if (connection != null) {
                                connection.disconnect();
                            }
                        } catch (Exception e2) {
                            Log.e(LogoMovie.TAG, "Erro ao fechar os recursos da conexão.", e2);
                            throw th;
                        }
                    } else if (connection != null) {
                        connection.disconnect();
                    }
                    throw th;
                }
            } catch (Exception e3) {
                Log.e(LogoMovie.TAG, "Erro ao carregar a imagem: " + imageUrl, e3);
                if (input != null) {
                    try {
                        input.close();
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (Exception e4) {
                        Log.e(LogoMovie.TAG, "Erro ao fechar os recursos da conexão.", e4);
                        return null;
                    }
                } else if (connection != null) {
                    connection.disconnect();
                }
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                Log.e(LogoMovie.TAG, "Falha ao carregar a imagem.");
                this.imageView.setVisibility(8);
            } else {
                this.imageView.setImageBitmap(bitmap);
                Log.d(LogoMovie.TAG, "Imagem carregada com sucesso.");
            }
        }
    }
}
