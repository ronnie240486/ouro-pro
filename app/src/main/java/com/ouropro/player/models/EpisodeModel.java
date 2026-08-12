package com.ouropro.player.models;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.ouropro.player.improvements.M3USeriesNaming;
import io.realm.RealmObject;
import io.realm.internal.RealmObjectProxy;
import iptv.m3u.parser.M3UItem;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class EpisodeModel extends RealmObject implements Serializable {
    private String category_name;

    @SerializedName("container_extension")
    private String container_extension;
    private String episode_num;

    @SerializedName("id")
    private String id;
    private EpisodeInfoModel info;

    @SerializedName("season")
    private int season;
    private String season_name;
    private String series_name;
    private String stream_icon;

    @SerializedName("title")
    private String title;
    private String url;

    /* JADX WARN: Multi-variable type inference failed */
    public EpisodeModel() {
        if (this instanceof RealmObjectProxy) {
            ((RealmObjectProxy) this).realm$injectObjectContext();
        }
    }

    @Nullable
    public static EpisodeModel fromM3UItem(M3UItem m3UItem) {
        try {
            if (m3UItem == null || TextUtils.isEmpty(m3UItem.getStreamURL())) {
                return null;
            }
            EpisodeModel episodeModel = new EpisodeModel();
            episodeModel.setId(m3UItem.getChannelId());
            episodeModel.setCategory_name(TextUtils.isEmpty(m3UItem.getGroupTitle()) ? "All" : m3UItem.getGroupTitle());
            String title = TextUtils.isEmpty(m3UItem.getChannelName()) ? m3UItem.getStreamURL() : m3UItem.getChannelName();
            episodeModel.setTitle(title);
            String seriesName = M3USeriesNaming.seriesName(title);
            if (TextUtils.isEmpty(seriesName)) {
                return null;
            }
            episodeModel.setSeries_name(seriesName);
            episodeModel.setSeason_name(M3USeriesNaming.seasonName(title));
            episodeModel.setUrl(m3UItem.getStreamURL());
            if (!TextUtils.isEmpty(m3UItem.getLogoURL())) {
                episodeModel.setStream_icon(m3UItem.getLogoURL());
            }
            return episodeModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCategory_name() {
        return realmGet$category_name() == null ? "" : realmGet$category_name();
    }

    public String getContainer_extension() {
        return realmGet$container_extension();
    }

    public String getEpisode_num() {
        return realmGet$episode_num() == null ? "" : realmGet$episode_num();
    }

    public String getId() {
        return realmGet$id();
    }

    public EpisodeInfoModel getInfo() {
        return realmGet$info();
    }

    public int getSeason() {
        return realmGet$season();
    }

    public String getSeason_name() {
        return realmGet$season_name() == null ? "" : realmGet$season_name();
    }

    public String getSeries_name() {
        return realmGet$series_name() == null ? "" : realmGet$series_name();
    }

    public String getStream_icon() {
        return realmGet$stream_icon() == null ? "" : realmGet$stream_icon();
    }

    public String getTitle() {
        return realmGet$title();
    }

    public String getUrl() {
        return realmGet$url() == null ? "" : realmGet$url();
    }

    public String realmGet$category_name() {
        return this.category_name;
    }

    public String realmGet$container_extension() {
        return this.container_extension;
    }

    public String realmGet$episode_num() {
        return this.episode_num;
    }

    public String realmGet$id() {
        return this.id;
    }

    public EpisodeInfoModel realmGet$info() {
        return this.info;
    }

    public int realmGet$season() {
        return this.season;
    }

    public String realmGet$season_name() {
        return this.season_name;
    }

    public String realmGet$series_name() {
        return this.series_name;
    }

    public String realmGet$stream_icon() {
        return this.stream_icon;
    }

    public String realmGet$title() {
        return this.title;
    }

    public String realmGet$url() {
        return this.url;
    }

    public void realmSet$category_name(String str) {
        this.category_name = str;
    }

    public void realmSet$container_extension(String str) {
        this.container_extension = str;
    }

    public void realmSet$episode_num(String str) {
        this.episode_num = str;
    }

    public void realmSet$id(String str) {
        this.id = str;
    }

    public void realmSet$info(EpisodeInfoModel episodeInfoModel) {
        this.info = episodeInfoModel;
    }

    public void realmSet$season(int i) {
        this.season = i;
    }

    public void realmSet$season_name(String str) {
        this.season_name = str;
    }

    public void realmSet$series_name(String str) {
        this.series_name = str;
    }

    public void realmSet$stream_icon(String str) {
        this.stream_icon = str;
    }

    public void realmSet$title(String str) {
        this.title = str;
    }

    public void realmSet$url(String str) {
        this.url = str;
    }

    public void setCategory_name(String str) {
        realmSet$category_name(str);
    }

    public void setContainer_extension(String str) {
        realmSet$container_extension(str);
    }

    public void setEpisode_num(String str) {
        realmSet$episode_num(str);
    }

    public void setId(String str) {
        realmSet$id(str);
    }

    public void setInfo(EpisodeInfoModel episodeInfoModel) {
        realmSet$info(episodeInfoModel);
    }

    public void setSeason(int i) {
        realmSet$season(i);
    }

    public void setSeason_name(String str) {
        realmSet$season_name(str);
    }

    public void setSeries_name(String str) {
        realmSet$series_name(str);
    }

    public void setStream_icon(String str) {
        realmSet$stream_icon(str);
    }

    public void setTitle(String str) {
        realmSet$title(str);
    }

    public void setUrl(String str) {
        realmSet$url(str);
    }
}
