package com.ouropro.player.remote;

import com.ouropro.player.models.CastResponse;
import com.ouropro.player.models.CatchUpEpgResponse;
import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.EPGChannel;
import com.ouropro.player.models.InfoSerie;
import com.ouropro.player.models.LoginResponse;
import com.ouropro.player.models.MovieCreditResponse;
import com.ouropro.player.models.MovieInfoResponse;
import com.ouropro.player.models.MovieModel;
import com.ouropro.player.models.SeriesModel;
import com.ouropro.player.models.TMDBResponse;
import com.ouropro.player.models.TMDBVideoResponse;
import java.util.List;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/* JADX INFO: loaded from: classes.dex */
public interface APIService {
    @GET("/player_api.php?")
    Call<LoginResponse> authentication(@Query("username") String str, @Query("password") String str2);

    @GET
    Call<CastResponse> getCastModels(@Url String str);

    @Streaming
    @GET("/xmltv.php")
    Call<ResponseBody> getEpgXml(@Query("username") String str, @Query("password") String str2);

    @GET
    Call<MovieCreditResponse> getMovieCreditModels(@Url String str);

    @GET
    Call<TMDBResponse> getRSS(@Url String str);

    @GET
    Call<TMDBVideoResponse> getTmdbVideoModels(@Url String str);

    @GET("/player_api.php?action=get_simple_data_table")
    Call<CatchUpEpgResponse> get_full_epg(@Query("username") String str, @Query("password") String str2, @Query("stream_id") String str3);

    @GET("/player_api.php?action=get_live_categories")
    Call<List<CategoryModel>> get_live_categories(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_live_streams&category_id=*")
    Call<List<EPGChannel>> get_live_streams(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_live_streams")
    Call<List<EPGChannel>> get_second_live_streams(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_series")
    Call<List<SeriesModel>> get_second_series(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_vod_streams")
    Call<List<MovieModel>> get_second_vod_streams(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_series&category_id=*")
    Call<List<SeriesModel>> get_series(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_series_categories")
    Call<List<CategoryModel>> get_series_categories(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_series_info")
    Call<InfoSerie> get_series_info(@Query("username") String str, @Query("password") String str2, @Query("series_id") String str3);

    @GET("/player_api.php?action=get_series_info")
    Call<JSONObject> get_series_info_response(@Query("username") String str, @Query("password") String str2, @Query("series_id") String str3);

    @GET("/player_api.php?action=get_short_epg")
    Call<CatchUpEpgResponse> get_short_epg(@Query("username") String str, @Query("password") String str2, @Query("stream_id") String str3);

    @GET("/player_api.php?action=get_vod_categories")
    Call<List<CategoryModel>> get_vod_categories(@Query("username") String str, @Query("password") String str2);

    @GET("/player_api.php?action=get_vod_info")
    Call<MovieInfoResponse> get_vod_info(@Query("username") String str, @Query("password") String str2, @Query("vod_id") String str3);

    @GET("/player_api.php?action=get_vod_streams&category_id=*")
    Call<List<MovieModel>> get_vod_streams(@Query("username") String str, @Query("password") String str2);
}
