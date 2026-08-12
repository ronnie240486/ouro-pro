package com.ouropro.player.models;

import androidx.core.graphics.Insets$$ExternalSyntheticOutline0;
import java.io.Serializable;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MovieInfo implements Serializable {
    private String actors;
    private String age;
    private String backdrop;
    private List<String> backdrop_path;
    private String cast;
    private String category_id;
    private String country;
    private String cover_big;
    private String description;
    private String director;
    private String duration;
    private String duration_secs;
    private String episode_run_time;
    private String genre;
    private String kinopoisk_url;
    private String movie_image;
    private String name;
    private String o_name;
    private String plot;
    private String rating;
    private String rating_count_kinopoisk;
    private String rating_mpaa;
    private String releasedate;
    private String tmdb_id;
    private String youtube_trailer;

    public String getAge() {
        String str = this.age;
        return str == null ? "" : str;
    }

    public String getBackdrop() {
        return this.backdrop;
    }

    public String getCast() {
        String str = this.cast;
        return str == null ? "" : str;
    }

    public String getCategory_id() {
        return this.category_id;
    }

    public String getCountry() {
        return this.country;
    }

    public String getCover_big() {
        List<String> list = this.backdrop_path;
        if (list != null && list.size() != 0) {
            return this.backdrop_path.get(0);
        }
        String str = this.backdrop;
        if (str != null && !str.isEmpty()) {
            return this.backdrop;
        }
        String str2 = this.cover_big;
        return (str2 == null || str2.isEmpty()) ? "" : this.cover_big;
    }

    public String getDescription() {
        String str = this.description;
        if (str != null && !str.isEmpty()) {
            return this.description;
        }
        String str2 = this.plot;
        return str2 == null ? "" : str2;
    }

    public String getDirector() {
        return this.director;
    }

    public String getDuration() {
        return this.duration;
    }

    public String getDuration_secs() {
        String str = this.duration_secs;
        return str == null ? "" : str;
    }

    public String getGenre() {
        String str = this.genre;
        return str == null ? "" : str;
    }

    public String getKinopoisk_url() {
        return this.kinopoisk_url;
    }

    public String getMovie_image() {
        return this.movie_image;
    }

    public String getName() {
        return this.name;
    }

    public String getO_name() {
        return this.o_name;
    }

    public String getPlot() {
        return this.plot;
    }

    public float getRating() {
        String str = this.rating;
        if (str == null || str.isEmpty()) {
            return 0.0f;
        }
        return Float.parseFloat(this.rating) / 2.0f;
    }

    public String getRating_count_kinopoisk() {
        return this.rating_count_kinopoisk;
    }

    public String getRating_mpaa() {
        return this.rating_mpaa;
    }

    public String getReleasedate() {
        String str = this.releasedate;
        return str == null ? "" : str;
    }

    public String getTmdb_id() {
        String str = this.tmdb_id;
        return str == null ? "" : str;
    }

    public String getYoutube_trailer() {
        String str = this.youtube_trailer;
        return str == null ? "" : str;
    }

    public String toString() {
        StringBuilder sbM = Insets$$ExternalSyntheticOutline0.m("MovieInfo{movie_image='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.movie_image, '\'', ", youtube_trailer='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.youtube_trailer, '\'', ", genre='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.genre, '\'', ", plot='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.plot, '\'', ", cast='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.cast, '\'', ", rating='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.rating, '\'', ", director='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.director, '\'', ", releasedate='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.releasedate, '\'', ", tmdb_id='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.tmdb_id, '\'', ", kinopoisk_url='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.kinopoisk_url, '\'', ", name='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.name, '\'', ", o_name='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.o_name, '\'', ", cover_big='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.cover_big, '\'', ", episode_run_time='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.episode_run_time, '\'', ", actors='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.actors, '\'', ", description='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.description, '\'', ", age='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.age, '\'', ", rating_mpaa='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.rating_mpaa, '\'', ", rating_count_kinopoisk='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.rating_count_kinopoisk, '\'', ", country='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.country, '\'', ", duration='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.duration, '\'', ", backdrop='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.backdrop, '\'', ", duration_secs='");
        Insets$$ExternalSyntheticOutline0.m(sbM, this.duration_secs, '\'', ", backdrop_path='");
        sbM.append(this.backdrop_path);
        sbM.append('\'');
        sbM.append(", category_id='");
        sbM.append(this.category_id);
        sbM.append('\'');
        sbM.append('}');
        return sbM.toString();
    }
}
