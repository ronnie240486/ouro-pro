package com.ouropro.player.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

/** Catálogo de rádios públicas por categoria, com reprodução direta via ExoPlayer. */
public class RadioActivity extends AppCompatActivity {
    private static final int GOLD = Color.rgb(255, 211, 42);
    private static final int PAGE = Color.rgb(18, 15, 25);
    private static final int CARD = Color.rgb(39, 33, 50);
    private static final int CARD_FOCUSED = Color.rgb(82, 63, 105);
    private static final int MUTED = Color.rgb(190, 182, 202);

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
        root.setPadding(dp(20), dp(14), dp(20), dp(14));
        root.setBackgroundColor(PAGE);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = text("‹  Voltar", 18, Color.WHITE);
        back.setFocusable(true);
        back.setClickable(true);
        back.setOnClickListener(v -> finish());
        header.addView(back, new LinearLayout.LayoutParams(dp(132), dp(52)));

        ImageView radioMark = new ImageView(this);
        radioMark.setImageResource(R.drawable.radio_icon_user);
        radioMark.setPadding(dp(4), dp(4), dp(4), dp(4));
        header.addView(radioMark, new LinearLayout.LayoutParams(dp(46), dp(46)));

        TextView title = text("RÁDIOS", 25, GOLD);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(title, new LinearLayout.LayoutParams(dp(160), dp(52)));

        nowPlaying = text("Escolha uma categoria e uma rádio", 14, MUTED);
        nowPlaying.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        nowPlaying.setSingleLine(true);
        header.addView(nowPlaying, new LinearLayout.LayoutParams(0, dp(52), 1));
        root.addView(header);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        ScrollView categoryScroll = new ScrollView(this);
        categoryScroll.setClipToPadding(false);
        categoryContainer = new LinearLayout(this);
        categoryContainer.setOrientation(LinearLayout.VERTICAL);
        categoryContainer.setPadding(0, dp(10), dp(14), dp(8));
        categoryScroll.addView(categoryContainer, new ScrollView.LayoutParams(dp(290), ViewGroup.LayoutParams.WRAP_CONTENT));
        content.addView(categoryScroll, new LinearLayout.LayoutParams(dp(300), ViewGroup.LayoutParams.MATCH_PARENT));

        RecyclerView stationList = new RecyclerView(this);
        stationList.setClipToPadding(false);
        stationList.setPadding(dp(2), dp(10), 0, dp(8));
        stationList.setLayoutManager(new GridLayoutManager(this, 2));
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
                if (fields.size() < 8) {
                    continue;
                }
                RadioStation station = new RadioStation(
                        fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4), fields.get(5), fields.get(6), fields.get(7));
                if (!station.name.isEmpty() && !station.streamUrl.isEmpty()) {
                    allStations.add(station);
                }
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
            int count = 0;
            for (RadioStation station : allStations) {
                if ("Todas".equals(category) || category.equalsIgnoreCase(station.category)) {
                    count++;
                }
            }
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(14), 0, dp(14), 0);
            card.setFocusable(true);
            card.setClickable(true);
            card.setBackground(cardBackground(category.equals(selectedCategory), false));
            card.setContentDescription("Categoria " + category);

            ImageView icon = new ImageView(this);
            icon.setImageResource(R.drawable.radio_icon_user);
            icon.setPadding(dp(6), dp(6), dp(6), dp(6));
            card.addView(icon, new LinearLayout.LayoutParams(dp(36), dp(36)));

            TextView label = text(category, 14, Color.WHITE);
            label.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            label.setGravity(Gravity.CENTER_VERTICAL);
            card.addView(label, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

            TextView number = text(String.valueOf(count), 13, GOLD);
            number.setGravity(Gravity.CENTER);
            card.addView(number, new LinearLayout.LayoutParams(dp(36), ViewGroup.LayoutParams.MATCH_PARENT));

            card.setOnClickListener(v -> applyCategory(category));
            card.setOnFocusChangeListener((v, focused) -> v.setBackground(cardBackground(focused || category.equals(selectedCategory), focused)));
            categoryContainer.addView(card, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(58)));
            LinearLayout.LayoutParams margin = (LinearLayout.LayoutParams) card.getLayoutParams();
            margin.bottomMargin = dp(10);
            card.setLayoutParams(margin);
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
        rebuildCategoryButtons();
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
            nowPlaying.setText("Não foi possível abrir: " + station.name);
            Toast.makeText(this, "Esta rádio não respondeu; tente outra estação", Toast.LENGTH_SHORT).show();
        }
    }

    private GradientDrawable cardBackground(boolean selected, boolean focused) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(selected || focused ? CARD_FOCUSED : CARD);
        background.setCornerRadius(dp(12));
        background.setStroke(dp(focused ? 2 : 1), focused ? GOLD : Color.rgb(67, 57, 78));
        return background;
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
            LinearLayout card = new LinearLayout(RadioActivity.this);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setFocusable(true);
            card.setClickable(true);
            card.setBackground(cardBackground(false, false));

            ImageView logo = new ImageView(RadioActivity.this);
            logo.setImageResource(R.drawable.radio_icon_user);
            logo.setPadding(dp(8), dp(8), dp(8), dp(8));
            card.addView(logo, new LinearLayout.LayoutParams(dp(58), dp(58)));

            LinearLayout details = new LinearLayout(RadioActivity.this);
            details.setOrientation(LinearLayout.VERTICAL);
            details.setGravity(Gravity.CENTER_VERTICAL);
            card.addView(details, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

            TextView name = text("", 15, Color.WHITE);
            name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            name.setMaxLines(2);
            details.addView(name, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

            TextView location = text("", 11, MUTED);
            location.setSingleLine(true);
            details.addView(location, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(22)));
            return new Holder(card, logo, name, location);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            RadioStation station = visibleStations.get(position);
            holder.name.setText(station.name);
            String location = station.city.isEmpty() ? station.country : station.city + " • " + station.country;
            holder.location.setText(location.isEmpty() ? "Rádio online" : location);
            Glide.with(RadioActivity.this)
                    .load(station.logoUrl)
                    .placeholder(R.drawable.radio_icon_user)
                    .error(R.drawable.radio_icon_user)
                    .into(holder.logo);
            holder.card.setBackground(cardBackground(false, false));
            holder.card.setOnFocusChangeListener((v, focused) -> v.setBackground(cardBackground(false, focused)));
            holder.card.setOnClickListener(v -> play(station));
        }

        @Override
        public int getItemCount() {
            return visibleStations.size();
        }

        final class Holder extends RecyclerView.ViewHolder {
            final LinearLayout card;
            final ImageView logo;
            final TextView name;
            final TextView location;

            Holder(LinearLayout card, ImageView logo, TextView name, TextView location) {
                super(card);
                this.card = card;
                this.logo = logo;
                this.name = name;
                this.location = location;
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
        final String logoUrl;
        final String sourceUrl;

        RadioStation(String name, String category, String country, String city, String genre, String streamUrl, String logoUrl, String sourceUrl) {
            this.name = clean(name);
            this.category = clean(category);
            this.country = clean(country);
            this.city = clean(city);
            this.genre = clean(genre);
            this.streamUrl = clean(streamUrl);
            this.logoUrl = clean(logoUrl);
            this.sourceUrl = clean(sourceUrl);
        }

        private static String clean(String value) {
            return value == null ? "" : value.trim();
        }
    }
}
