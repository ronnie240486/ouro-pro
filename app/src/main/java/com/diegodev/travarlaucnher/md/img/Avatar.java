package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class Avatar extends ImageView {
    private static final String DEFAULT_IMAGE_URL = "https://appxt.top/ibop22/api/logo.php";
    private static final String IMAGE_DIR = "task/img/";
    private static final String IMAGE_NAME = "avatar";
    private static final String TAG = "CustomImageView";

    public Avatar(Context context) {
        super(context);
        initialize();
    }

    public Avatar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public Avatar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, "Context é nulo no método initialize.");
            return;
        }
        File imageDir = new File(context.getFilesDir(), IMAGE_DIR);
        if (!imageDir.exists()) {
            boolean created = imageDir.mkdirs();
            if (!created) {
                Log.e(TAG, "Falha ao criar diretório para imagens: " + imageDir.getAbsolutePath());
                return;
            }
        }
        File imageFile = new File(imageDir, "avatar.png");
        if (!imageFile.exists() || !imageFile.isFile()) {
            Log.d(TAG, "Imagem local não encontrada. Carregando da URL padrão.");
            loadImageFromUrl();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (bitmap == null) {
            Log.e(TAG, "Falha ao decodificar a imagem local. Carregando da URL padrão.");
            loadImageFromUrl();
        } else {
            setImageBitmap(bitmap);
            loadImageFromUrl();
        }
    }

    private void loadImageFromUrl() {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, "Context é nulo no método loadImageFromUrl.");
            return;
        }
        try {
            new ImageLoaderTask(this, context, IMAGE_NAME).execute(DEFAULT_IMAGE_URL);
            Log.d(TAG, "Tarefa de download iniciada para: https://appxt.top/ibop22/api/logo.php");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar a tarefa de download da imagem.", e);
        }
    }
}
