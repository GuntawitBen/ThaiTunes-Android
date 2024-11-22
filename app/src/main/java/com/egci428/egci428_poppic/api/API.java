package com.egci428.egci428_poppic.api;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface API {

    @FormUrlEncoded
    @POST("api/login")
    Call<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @GET
    Call<ResponseBody> playSong(@Url String url);
}
