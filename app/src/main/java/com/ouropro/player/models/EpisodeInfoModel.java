package com.ouropro.player.models;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.internal.RealmObjectProxy;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class EpisodeInfoModel extends RealmObject implements Serializable {

    @SerializedName("bitrate")
    private int bitrate;

    @SerializedName("duration")
    private String duration;

    @SerializedName("duration_secs")
    private int duration_secs;

    @SerializedName("movie_image")
    private String movie_image;

    @SerializedName("name")
    private String name;

    @SerializedName("plot")
    private String plot;

    @SerializedName("rating")
    private String rating;

    @SerializedName("releasedate")
    private String releasedate;

    @SerializedName("tmdb_id")
    public String tmdb_id;

    /* JADX WARN: Multi-variable type inference failed */
    public EpisodeInfoModel() {
        if (this instanceof RealmObjectProxy) {
            ((RealmObjectProxy) this).realm$injectObjectContext();
        }
    }

    public int getBitrate() {
        return realmGet$bitrate();
    }

    public String getDuration() {
        return realmGet$duration();
    }

    public int getDuration_secs() {
        return realmGet$duration_secs();
    }

    public String getMovie_image() {
        return realmGet$movie_image() == null ? "" : realmGet$movie_image();
    }

    public String getName() {
        return realmGet$name();
    }

    public String getPlot() {
        return realmGet$plot() == null ? "" : realmGet$plot();
    }

    public float getRating() {
        if (realmGet$rating() == null || realmGet$rating().isEmpty()) {
            return 0.0f;
        }
        return Float.parseFloat(realmGet$rating());
    }

    public String getReleasedate() {
        return realmGet$releasedate();
    }

    public String getTmdb_id() {
        return realmGet$tmdb_id() == null ? "" : realmGet$tmdb_id();
    }

    public int realmGet$bitrate() {
        return this.bitrate;
    }

    public String realmGet$duration() {
        return this.duration;
    }

    public int realmGet$duration_secs() {
        return this.duration_secs;
    }

    public String realmGet$movie_image() {
        return this.movie_image;
    }

    public String realmGet$name() {
        return this.name;
    }

    public String realmGet$plot() {
        return this.plot;
    }

    public String realmGet$rating() {
        return this.rating;
    }

    public String realmGet$releasedate() {
        return this.releasedate;
    }

    public String realmGet$tmdb_id() {
        return this.tmdb_id;
    }

    public void realmSet$bitrate(int i) {
        this.bitrate = i;
    }

    public void realmSet$duration(String str) {
        this.duration = str;
    }

    public void realmSet$duration_secs(int i) {
        this.duration_secs = i;
    }

    public void realmSet$movie_image(String str) {
        this.movie_image = str;
    }

    public void realmSet$name(String str) {
        this.name = str;
    }

    public void realmSet$plot(String str) {
        this.plot = str;
    }

    public void realmSet$rating(String str) {
        this.rating = str;
    }

    public void realmSet$releasedate(String str) {
        this.releasedate = str;
    }

    public void realmSet$tmdb_id(String str) {
        this.tmdb_id = str;
    }

    public void setMovie_image(String str) {
        realmSet$movie_image(str);
    }

    public void setPlot(String str) {
        realmSet$plot(str);
    }

    public void setTmdb_id(String str) {
        realmSet$tmdb_id(str);
    }
}
