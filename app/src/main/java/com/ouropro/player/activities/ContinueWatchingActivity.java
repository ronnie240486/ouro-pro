package com.ouropro.player.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ouropro.player.R;
import com.ouropro.player.helper.GetSharedInfo;
import com.ouropro.player.helper.PreferenceHelper;
import com.ouropro.player.helper.RealmController;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.ResumeModel;
import com.ouropro.player.models.WordModels;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * First interactive-roadmap screen. It only consumes the resume preferences
 * already maintained by the original player and never writes catalog data.
 */
public class ContinueWatchingActivity extends AppCompatActivity {
    private PreferenceHelper preferenceHelper;
    private WordModels wordModels;
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(8, 8, 12));
        getWindow().setNavigationBarColor(Color.rgb(8, 8, 12));

        preferenceHelper = new PreferenceHelper(this);
        wordModels = GetSharedInfo.getWordModel(this);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(24), dp(22), dp(24), dp(28));
        content.setBackgroundColor(Color.rgb(8, 8, 12));
        scrollView.addView(content, new ScrollView.LayoutParams(-1, -2));
        setContentView(scrollView);

        TextView title = new TextView(this);
        title.setText("Continuar assistindo");
        title.setTextColor(Color.rgb(255, 211, 42));
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setPadding(0, 0, 0, dp(16));
        content.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(this);
        subtitle.setText("Retome de onde você parou");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(16);
        subtitle.setPadding(0, 0, 0, dp(18));
        content.addView(subtitle, new LinearLayout.LayoutParams(-1, -2));

        addMovieResumeItems();
        addSeriesResumeItems();

        if (content.getChildCount() <= 2) {
            TextView empty = new TextView(this);
            empty.setText("Nenhum conteúdo pausado ainda.");
            empty.setTextColor(Color.WHITE);
            empty.setTextSize(18);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(32), 0, dp(32));
            content.addView(empty, new LinearLayout.LayoutParams(-1, -2));
        }
    }

    private void addMovieResumeItems() {
        List<ResumeModel> resumes = preferenceHelper.getSharedPreferenceResumeModel();
        if (resumes == null || resumes.isEmpty()) {
            return;
        }
        addSectionTitle("Filmes");
        Set<String> added = new HashSet<>();
        int count = 0;
        for (ResumeModel resume : resumes) {
            if (resume == null || resume.getName() == null || resume.getName().trim().isEmpty() || count >= 12) {
                continue;
            }
            String name = resume.getName().trim();
            if (!added.add(name.toLowerCase())) {
                continue;
            }
            MovieModel movie = RealmController.with().getMovieByName(name);
            if (movie == null) {
                continue;
            }
            addResumeButton(movie.getName(), resume.getPro(), view -> openMovie(movie));
            count++;
        }
    }

    private void addSeriesResumeItems() {
        List<ResumeModel> resumes = preferenceHelper.getSharedPreferenceSeriesResumeModel();
        if (resumes == null || resumes.isEmpty()) {
            return;
        }
        addSectionTitle("Séries");
        Set<String> added = new HashSet<>();
        int count = 0;
        for (ResumeModel resume : resumes) {
            if (resume == null || resume.getName() == null || resume.getName().trim().isEmpty() || count >= 12) {
                continue;
            }
            String name = resume.getName().trim();
            if (!added.add(name.toLowerCase())) {
                continue;
            }
            addResumeButton(name, resume.getPro(), view -> {
                // The original SeriesActivity already exposes the resume category
                // and keeps season/episode position in the existing Realm flow.
                startActivity(new android.content.Intent(this, SeriesActivity.class));
            });
            count++;
        }
    }

    private void addSectionTitle(String text) {
        TextView section = new TextView(this);
        section.setText(text);
        section.setTextColor(Color.WHITE);
        section.setTextSize(20);
        section.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        section.setPadding(0, dp(14), 0, dp(8));
        content.addView(section, new LinearLayout.LayoutParams(-1, -2));
    }

    private void addResumeButton(String name, int progress, View.OnClickListener listener) {
        Button button = new Button(this);
        String percent = progress > 0 ? "  •  " + progress + "%" : "";
        button.setText(name + percent);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        button.setAllCaps(false);
        button.setPadding(dp(16), 0, dp(16), 0);
        button.setMinHeight(dp(54));
        button.setFocusable(true);
        button.setOnClickListener(listener);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(36, 31, 49));
        background.setCornerRadius(dp(8));
        background.setStroke(dp(1), Color.rgb(112, 82, 151));
        button.setBackground(background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(54));
        params.setMargins(0, 0, 0, dp(8));
        content.addView(button, params);
    }

    private void openMovie(MovieModel movie) {
        if (movie == null) {
            return;
        }
        android.content.Intent intent = new android.content.Intent(this,
                GetSharedInfo.isTVDevice(this) ? MoviePlayerActivity.class : com.ouropro.player.activities.mobile.MovieMobilePlayer.class);
        intent.putExtra("name", movie.getName());
        intent.putExtra("stream_id", movie.getStream_id());
        intent.putExtra("description", "");
        intent.putExtra("category_name", movie.getCategory_name());
        intent.putExtra("tmdb_id", movie.getTmdb_id());
        startActivity(intent);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
