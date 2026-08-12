package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class Logo extends ImageView {
    private static final String DEFAULT_IMAGE_URL = "https://renciaapp-ldyffp73.manus.space/api/v4/logo.php";
    private static final String IMAGE_DIR = "task/img/";
    private static final String IMAGE_NAME = "logo";
    private static final String OFFLINE_IMAGE_ASSET = "logo_offline.jpg";
    private static final String TAG = "CustomImageView";

    public Logo(Context context) {
        super(context);
        initialize();
    }

    public Logo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public Logo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, "Context é nulo.");
            loadOfflineAssetImage();
            return;
        }
        File imageDir = new File(context.getFilesDir(), IMAGE_DIR);
        if (!imageDir.exists() && !imageDir.mkdirs()) {
            Log.e(TAG, "Falha ao criar diretório.");
            loadOfflineAssetImage();
            return;
        }
        File imageFile = new File(imageDir, "logo.png");
        if (!imageFile.exists() || !imageFile.isFile()) {
            Log.d(TAG, "Imagem local não encontrada.");
            loadImageFromUrl();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (bitmap == null) {
            Log.e(TAG, "Falha ao decodificar imagem local.");
            loadOfflineAssetImage();
        } else {
            setImageBitmap(bitmap);
            loadImageFromUrl();
        }
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [com.diegodev.travarlaucnher.md.img.Logo$1] */
    private void loadImageFromUrl() {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, "Context é nulo na tarefa de download.");
            loadOfflineAssetImage();
            return;
        }
        try {
            new ImageLoaderTask(this, context, IMAGE_NAME) { // from class: com.diegodev.travarlaucnher.md.img.Logo.1
                /* JADX INFO: Access modifiers changed from: protected */
                /* JADX WARN: Can't rename method to resolve collision */
                public void onPostExecute(Bitmap result) {
                    if (result != null) {
                        Logo.this.setImageBitmap(result);
                    } else {
                        Log.e(Logo.TAG, "Falha no download da imagem.");
                        Logo.this.loadOfflineAssetImage();
                    }
                }
            }.execute(new String[]{DEFAULT_IMAGE_URL});
        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar download.", e);
            loadOfflineAssetImage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadOfflineAssetImage() {
        try {
            InputStream inputStream = getContext().getAssets().open(OFFLINE_IMAGE_ASSET);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            setImageBitmap(bitmap);
            Log.d(TAG, "Imagem offline carregada dos assets.");
        } catch (IOException e) {
            Log.e(TAG, "Erro ao carregar imagem offline dos assets.", e);
        }
    }
}
