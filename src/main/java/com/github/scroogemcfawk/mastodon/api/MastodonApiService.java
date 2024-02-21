package com.github.scroogemcfawk.mastodon.api;

import com.github.scroogemcfawk.mastodon.api.entity.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MastodonApiService
{
    @POST("/api/v1/apps")
    Call<Application> registerApp(@Body ApplicationRegisterRequestBody body);

    @POST("/oauth/token")
    Call<Token> getToken(@Body ApplicationGetTokenRequestBody body);

    @GET("/api/v1/apps/verify_credentials")
    Call<Application> verifyApp(@Header("Authorization") String token);

    @POST("/api/v1/accounts")
    Call<Token> registerAccount(@Header("Authorization") String token, @Body AccountRegisterRequestBody body);

    @GET("/api/v1/accounts/verify_credentials")
    Call<ResponseBody> verifyAccount(@Header("Authorization") String token);
}
