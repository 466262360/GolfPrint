package com.mashangyou.golfprint.api;


import com.mashangyou.golfprint.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static Api api;

    //为了重置BASE_URL
    public static void setApiNull() {
        api = null;
    }

    public static Api getApi() {
        if (api == null) {
            synchronized (RetrofitManager.class) {
                if (api == null) {
                    api = new RetrofitManager().getRetrofit();
                }
            }
        }
        return api;
    }


    public Api getRetrofit() {

        Api api = null;
        api = initRetrofit(initOkHttp()).create(Api.class);
        return api;
    }

    private Retrofit initRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    private OkHttpClient initOkHttp() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();
    }


}
