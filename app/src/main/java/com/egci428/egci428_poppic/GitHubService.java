package com.egci428.egci428_poppic;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public interface GitHubService {
    @PUT("repos/{owner}/{repo}/contents/{path}")
    Call<Void> uploadFile(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("path") String path,
            @Header("Authorization") String authToken,
            @Body Post file
    );

    static GitHubService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GitHubService.class);
    }
}

