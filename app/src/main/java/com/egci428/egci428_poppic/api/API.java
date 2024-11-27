package com.egci428.egci428_poppic.api;


import com.egci428.egci428_poppic.models.Artist;
import com.egci428.egci428_poppic.models.Song;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface API {

    @FormUrlEncoded
    @POST("api/login")
    Call<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET
    Call<ResponseBody> playSong(@Url String url);

    @GET("get/{songName}")
    Call<Song> getSongInfo(@Path("songName") String songName);

    @GET("api/all")
    Call<List<Song>> allSongs();

    @GET("/random/all")
    Call<List<Song>> discoverSongs();

    @GET("/random/artists")
    Call<List<Artist>> randomArtists();


}
