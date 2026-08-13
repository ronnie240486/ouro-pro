package com.ouropro.player.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.ouropro.player.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Catálogo de rádios públicas por gênero, com reprodução direta via ExoPlayer. */
public class RadioActivity extends AppCompatActivity {
    private final List<RadioStation> allStations = new ArrayList<>();
    private final List<RadioStation> visibleStations = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private StationAdapter stationAdapter;
    private LinearLayout categoryContainer;
    private TextView nowPlaying;
    private ExoPlayer player;
    private String selectedCategory = "Todas";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildScreen();
        loadCatalog();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(12));
        root.setBackgroundColor(Color.rgb(22, 18, 29));

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = text("‹  Voltar", 18, Color.WHITE);
        back.setFocusable(true);
        back.setOnClickListener(v -> finish());
        header.addView(back, new LinearLayout.LayoutParams(dp(120), dp(48)));
        TextView title = text("RÁDIOS", 24, Color.rgb(255, 210, 50));
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        header.addView(title, new LinearLayout.LayoutParams(0, dp(48), 1));
        nowPlaying = text("Escolha uma categoria e uma rádio", 14, Color.LTGRAY);
        nowPlaying.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        header.addView(nowPlaying, new LinearLayout.LayoutParams(dp(390), dp(48)));
        root.addView(header);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        ScrollView categoryScroll = new ScrollView(this);
        categoryContainer = new LinearLayout(this);
        categoryContainer.setOrientation(LinearLayout.VERTICAL);
        categoryContainer.setPadding(0, dp(8), dp(12), 0);
        categoryScroll.addView(categoryContainer, new ScrollView.LayoutParams(dp(260), ViewGroup.LayoutParams.MATCH_PARENT));
        content.addView(categoryScroll, new LinearLayout.LayoutParams(dp(270), ViewGroup.LayoutParams.MATCH_PARENT));

        RecyclerView stationList = new RecyclerView(this);
        stationList.setLayoutManager(new LinearLayoutManager(this));
        stationAdapter = new StationAdapter();
        stationList.setAdapter(stationAdapter);
        content.addView(stationList, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        root.addView(content);
        setContentView(root);
    }

    private void loadCatalog() {
        try (InputStream input = getAssets().open("radio_stations.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                List<String> fields = parseCsv(line);
                if (fields.size() < 7) {
                    continue;
                }
                RadioStation station = new RadioStation(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4), fields.get(5));
                if (station.streamUrl.isEmpty()) {
                    continue;
                }
                allStations.add(station);
            }
        } catch (Exception error) {
            Toast.makeText(this, "Não foi possível carregar o catálogo de rádios", Toast.LENGTH_LONG).show();
        }
        Map<String, Boolean> unique = new LinkedHashMap<>();
        for (RadioStation station : allStations) {
            unique.put(station.category, true);
        }
        categories.clear();
        categories.add("Todas");
        categories.addAll(unique.keySet());
        Collections.sort(categories.subList(1, categories.size()), String.CASE_INSENSITIVE_ORDER);
        rebuildCategoryButtons();
        applyCategory("Todas");
    }

    private void rebuildCategoryButtons() {
        categoryContainer.removeAllViews();
        for (String category : categories) {
            AppCompatButton button = new AppCompatButton(this);
            button.setText(category);
            button.setTextColor(Color.WHITE);
            button.setTextSize(14);
            button.setAllCaps(false);
            button.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            button.setPadding(dp(16), 0, dp(8), 0);
            button.setFocusable(true);
            button.setOnClickListener(v -> applyCategory(category));
            categoryContainer.addView(button, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(46)));
        }
    }

    private void applyCategory(String category) {
        selectedCategory = category;
        visibleStations.clear();
        for (RadioStation station : allStations) {
            if ("Todas".equals(category) || category.equalsIgnoreCase(station.category)) {
                visibleStations.add(station);
            }
        }
        stationAdapter.notifyDataSetChanged();
    }

    private void play(RadioStation station) {
        try {
            if (player != null) {
                player.release();
            }
            player = new ExoPlayer.Builder(this).build();
            player.setMediaItem(MediaItem.fromUri(station.streamUrl));
            player.prepare();
            player.play();
            nowPlaying.setText("Tocando: " + station.name);
        } catch (Exception error) {
            nowPlaying.setText("Falha ao abrir: " + station.name);
            Toast.makeText(this, "Esta rádio não respondeu; tente outra estação", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    private TextView text(String value, float size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private List<String> parseCsv(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private final class StationAdapter extends RecyclerView.Adapter<StationAdapter.Holder> {
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = text("", 16, Color.WHITE);
            view.setGravity(Gravity.CENTER_VERTICAL);
            view.setPadding(dp(18), 0, dp(12), 0);
            view.setFocusable(true);
            view.setBackgroundResource(R.drawable.home_small_item_bg);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            RadioStation station = visibleStations.get(position);
            holder.view.setText(station.name + "  •  " + station.city + "  [" + station.genre + "]");
            holder.view.setOnClickListener(v -> play(station));
        }

        @Override
        public int getItemCount() {
            return visibleStations.size();
        }

        final class Holder extends RecyclerView.ViewHolder {
            final TextView view;
            Holder(TextView view) {
                super(view);
                this.view = view;
            }
        }
    }

    private static final class RadioStation {
        final String name;
        final String category;
        final String country;
        final String city;
        final String genre;
        final String streamUrl;

        RadioStation(String name, String category, String country, String city, String genre, String streamUrl) {
            this.name = name;
            this.category = category;
            this.country = country;
            this.city = city;
            this.genre = genre;
            this.streamUrl = streamUrl;
        }
    }
}
