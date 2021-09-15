package com.t00ls.api;

import android.util.Log;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 123 on 2018/3/25.
 */
public class T00lsServiceTest {

    private static final String TAG = "T00lsServiceTest";

    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    @Before
    public void setUp() {
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(5L, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.t00ls.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    @Test
    @Ignore
    public void getClassDetail() throws Exception {
        T00lsService t00lsService = mRetrofit.create(T00lsService.class);
        Call<BaseResponse> call = t00lsService.login("masteraux1", "Masterau2016/");
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Map<String,List<String>> headers = response.headers().toMultimap();
                for (String str : headers.get("Set-Cookie")) {
                    Log.e(TAG, "onResponse: "+str );
                }

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage() );
            }
        });
        Thread.sleep(10000);
    }

}