package com.github.scroogemcfawk.mastodon.api;

import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@NoArgsConstructor
public class Configuration implements AutoCloseable
{
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private MastodonApiService mastodonApiService;

    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().build();
        }
        return okHttpClient;
    }

    private Retrofit getRetrofit(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return retrofit;
    }

    private MastodonApiService getMastodonApiService(String baseUrl) {
        if (baseUrl == null) {
            baseUrl = "https://mastodon.world/";
        }
        if (mastodonApiService == null) {
            mastodonApiService = getRetrofit(baseUrl).create(MastodonApiService.class);
        }
        return mastodonApiService;
    }

    public MastodonApiService getService() {
        return getMastodonApiService(null);
    }


    @Override
    public void close()
    {
        if (okHttpClient == null) return;
        okHttpClient.dispatcher().executorService().shutdown();
        okHttpClient.connectionPool().evictAll();
    }
}
