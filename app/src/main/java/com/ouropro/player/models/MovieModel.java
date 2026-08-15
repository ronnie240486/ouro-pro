package com.ouropro.player.models;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.internal.RealmObjectProxy;
import iptv.m3u.parser.M3UItem;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class MovieModel extends RealmObject implements Serializable {

    @SerializedName("added")
    private String added;

    @SerializedName("category_id")
    private String category_id;
    private String category_name;

    @SerializedName("custom_sid")
    private String custom_sid;

    @SerializedName("container_extension")
    private String extension;
    private boolean is_favorite;
    private boolean is_locked;
    private boolean is_recent;

    @SerializedName("name")
    private String name;

    @SerializedName("num")
    private int num;
    private int pro;

    @SerializedName("rating")
    private String rating;
    private long recent_mil;

    @SerializedName("stream_icon")
    private String stream_icon;

    @SerializedName("stream_id")
    private String stream_id;

    @SerializedName("stream_type")
    private String stream_type;
    private long time;

    @SerializedName("tmdb_id")
    private String tmdb_id;
    private String type;
    private String url;

    /* JADX WARN: Multi-variable type inference failed */
    public MovieModel() {
        if (this instanceof RealmObjectProxy) {
            ((RealmObjectProxy) this).realm$injectObjectContext();
        }
        realmSet$pro(0);
        realmSet$time(0L);
        realmSet$recent_mil(0L);
        realmSet$is_locked(false);
        realmSet$is_favorite(false);
        realmSet$is_recent(false);
    }

    @Nullable
    public static MovieModel fromM3UItem(M3UItem m3UItem) {
        try {
            MovieModel movieModel = new MovieModel();
            movieModel.setCategory_name(TextUtils.isEmpty(m3UItem.getGroupTitle()) ? "All" : m3UItem.getGroupTitle());
            if (!TextUtils.isEmpty(m3UItem.getChannelName())) {
                movieModel.setName(m3UItem.getChannelName());
            }
            if (!TextUtils.isEmpty(m3UItem.getStreamURL())) {
                movieModel.setUrl(m3UItem.getStreamURL());
                if (movieModel.getUrl().contains("movie")) {
                    try {
                        String str = movieModel.getUrl().split("/")[6];
                        if (str.contains(".")) {
                            movieModel.setStream_id(str.split("\\.")[0]);
                        } else {
                            movieModel.setStream_id(str);
                        }
                    } catch (Exception unused) {
                        movieModel.setStream_id("0");
                    }
                } else {
                    try {
                        movieModel.setStream_id(movieModel.getUrl().split("/")[5]);
                    } catch (Exception unused2) {
                        movieModel.setStream_id("0");
                    }
                }
            }
            if (!TextUtils.isEmpty(m3UItem.getLogoURL())) {
                movieModel.setStream_icon(m3UItem.getLogoURL());
            }
            return movieModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAdded() {
        return (realmGet$added() == null || realmGet$added().isEmpty()) ? "0" : realmGet$added();
    }

    public String getCategory_id() {
        return realmGet$category_id() == null ? "" : realmGet$category_id();
    }

    public String getCategory_name() {
        return realmGet$category_name() == null ? "!@#%" : realmGet$category_name();
    }

    public String getCustom_sid() {
        return realmGet$custom_sid();
    }

    public String getExtension() {
        return realmGet$extension();
    }

    public String getName() {
        return realmGet$name();
    }

    public int getNum() {
        return realmGet$num();
    }

    public int getPro() {
        return realmGet$pro();
    }

    public String getRating() {
        return realmGet$rating();
    }

    public long getRecent_mil() {
        return realmGet$recent_mil();
    }

    public String getStream_icon() {
        return realmGet$stream_icon() == null ? "" : realmGet$stream_icon();
    }

    public String getStream_id() {
        return realmGet$stream_id() == null ? "" : realmGet$stream_id();
    }

    public String getStream_type() {
        return realmGet$stream_type();
    }

    public long getTime() {
        return realmGet$time();
    }

    public String getTmdb_id() {
        return realmGet$tmdb_id() == null ? "" : realmGet$tmdb_id();
    }

    public String getType() {
        return realmGet$type();
    }

    public String getUrl() {
        return realmGet$url();
    }

    public boolean isIs_favorite() {
        return realmGet$is_favorite();
    }

    public boolean isIs_locked() {
        return realmGet$is_locked();
    }

    public boolean isIs_recent() {
        return realmGet$is_recent();
    }

    public String realmGet$added() {
        return this.added;
    }

    public String realmGet$category_id() {
        return this.category_id;
    }

    public String realmGet$category_name() {
        return this.category_name;
    }

    public String realmGet$custom_sid() {
        return this.custom_sid;
    }

    public String realmGet$extension() {
        return this.extension;
    }

    public boolean realmGet$is_favorite() {
        return this.is_favorite;
    }

    public boolean realmGet$is_locked() {
        return this.is_locked;
    }

    public boolean realmGet$is_recent() {
        return this.is_recent;
    }

    public String realmGet$name() {
        return this.name;
    }

    public int realmGet$num() {
        return this.num;
    }

    public int realmGet$pro() {
        return this.pro;
    }

    public String realmGet$rating() {
        return this.rating;
    }

    public long realmGet$recent_mil() {
        return this.recent_mil;
    }

    public String realmGet$stream_icon() {
        return this.stream_icon;
    }

    public String realmGet$stream_id() {
        return this.stream_id;
    }

    public String realmGet$stream_type() {
        return this.stream_type;
    }

    public long realmGet$time() {
        return this.time;
    }

    public String realmGet$tmdb_id() {
        return this.tmdb_id;
    }

    public String realmGet$type() {
        return this.type;
    }

    public String realmGet$url() {
        return this.url;
    }

    public void realmSet$added(String str) {
        this.added = str;
    }

    public void realmSet$category_id(String str) {
        this.category_id = str;
    }

    public void realmSet$category_name(String str) {
        this.category_name = str;
    }

    public void realmSet$custom_sid(String str) {
        this.custom_sid = str;
    }

    public void realmSet$extension(String str) {
        this.extension = str;
    }

    public void realmSet$is_favorite(boolean z) {
        this.is_favorite = z;
    }

    public void realmSet$is_locked(boolean z) {
        this.is_locked = z;
    }

    public void realmSet$is_recent(boolean z) {
        this.is_recent = z;
    }

    public void realmSet$name(String str) {
        this.name = str;
    }

    public void realmSet$num(int i) {
        this.num = i;
    }

    public void realmSet$pro(int i) {
        this.pro = i;
    }

    public void realmSet$rating(String str) {
        this.rating = str;
    }

    public void realmSet$recent_mil(long j) {
        this.recent_mil = j;
    }

    public void realmSet$stream_icon(String str) {
        this.stream_icon = str;
    }

    public void realmSet$stream_id(String str) {
        this.stream_id = str;
    }

    public void realmSet$stream_type(String str) {
        this.stream_type = str;
    }

    public void realmSet$time(long j) {
        this.time = j;
    }

    public void realmSet$tmdb_id(String str) {
        this.tmdb_id = str;
    }

    public void realmSet$type(String str) {
        this.type = str;
    }

    public void realmSet$url(String str) {
        this.url = str;
    }

    public void setAdded(String str) {
        realmSet$added(str);
    }

    public void setCategory_id(String str) {
        realmSet$category_id(str);
    }

    public void setCategory_name(String str) {
        realmSet$category_name(str);
    }

    public void setCustom_sid(String str) {
        realmSet$custom_sid(str);
    }

    public void setExtension(String str) {
        realmSet$extension(str);
    }

    public void setIs_favorite(boolean z) {
        realmSet$is_favorite(z);
    }

    public void setIs_locked(boolean z) {
        realmSet$is_locked(z);
    }

    public void setIs_recent(boolean z) {
        realmSet$is_recent(z);
    }

    public void setName(String str) {
        realmSet$name(str);
    }

    public void setNum(int i) {
        realmSet$num(i);
    }

    public void setPro(int i) {
        realmSet$pro(i);
    }

    public void setRating(String str) {
        realmSet$rating(str);
    }

    public void setRecent_mil(long j) {
        realmSet$recent_mil(j);
    }

    public void setStream_icon(String str) {
        realmSet$stream_icon(str);
    }

    public void setStream_id(String str) {
        realmSet$stream_id(str);
    }

    public void setStream_type(String str) {
        realmSet$stream_type(str);
    }

    public void setTime(long j) {
        realmSet$time(j);
    }

    public void setTmdb_id(String str) {
        realmSet$tmdb_id(str);
    }

    public void setType(String str) {
        realmSet$type(str);
    }

    public void setUrl(String str) {
        realmSet$url(str);
    }
}
