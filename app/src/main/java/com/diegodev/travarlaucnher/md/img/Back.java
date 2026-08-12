package com.diegodev.travarlaucnher.md.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class Back extends ImageView {
    private static final String DEFAULT_IMAGE_URL = "https://renciaapp-ldyffp73.manus.space/api/v4/bg.php";
    private static final String IMAGE_DIR = "task/img/";
    private static final String IMAGE_NAME = "background";
    private static final String TAG = "CustomBacground";

    public Back(Context context) {
        super(context);
        initialize();
    }

    public Back(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public Back(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Context context = getContext();
        setBackgroundColor(-16777216);
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
        File imageFile = new File(imageDir, "background.png");
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

    /* JADX WARN: Type inference failed for: r2v1, types: [com.diegodev.travarlaucnher.md.img.Back$1] */
    private void loadImageFromUrl() {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, "Context é nulo no método loadImageFromUrl.");
            return;
        }
        try {
            new ImageLoaderTask(this, context, IMAGE_NAME) { // from class: com.diegodev.travarlaucnher.md.img.Back.1
                /* JADX INFO: Access modifiers changed from: protected */
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.diegodev.travarlaucnher.md.img.ImageLoaderTask, android.os.AsyncTask
                public void onPostExecute(Bitmap result) {
                    if (result != null) {
                        Back.this.setImageBitmap(result);
                    } else {
                        Back.this.setBackgroundColor(-16777216);
                        Log.e(Back.TAG, "Falha ao baixar a imagem. Fundo preto aplicado.");
                    }
                }
            }.execute(new String[]{DEFAULT_IMAGE_URL});
        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar a tarefa de download da imagem.", e);
            setBackgroundColor(-16777216);
        }
    }
}
