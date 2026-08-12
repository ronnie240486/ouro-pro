package com.diegodev.travarlaucnher.md;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.diegodev.travarlaucnher.R;
import com.diegodev.travarlaucnher.md.img.EncryptedApiCaller;
import com.diegodev.travarlaucnher.md.img.LogoMovie;

/* JADX INFO: loaded from: classes2.dex */
public class MainActivity2 extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        EncryptedApiCaller.callEncryptedMoviesApi(this);
        LogoMovie.setImageFromUrl("https://image.tmdb.org/t/p/w342/A8HbTd0FemZyFCh5qvJFpHGiwF8.jpg");
        WebView.setWebContentsDebuggingEnabled(true);
    }
}
