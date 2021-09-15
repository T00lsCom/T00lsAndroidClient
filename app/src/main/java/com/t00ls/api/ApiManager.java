package com.t00ls.api;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.util.InternetUtil;
import com.t00ls.util.MD5Util;
import com.t00ls.util.PreferenceUtil;
import com.t00ls.util.UserAgentUtil;
import com.t00ls.viewmodel.EventViewModel;
import com.t00ls.viewmodel.InfoViewModel;
import com.t00ls.viewmodel.UserInfoViewModel;
import com.t00ls.vo.InfoDetail;
import com.t00ls.vo.MemberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 123 on 2018/3/25.
 */

public class ApiManager {

    private static final String TAG = "ApiManager";

    private Retrofit mRetrofit;

    private static ApiManager mApiManager;


    private ApiManager() {
    }

    public static ApiManager getInstance() {
        if (mApiManager == null) {
            mApiManager = new ApiManager();
        }
        return mApiManager;
    }

    public void loadInfoDetail(String classification, Integer page, Fragment fragment) {

        MutableLiveData<List<InfoDetail>> infoDetails = ViewModelProviders.of(fragment).get(InfoViewModel.class).getInfoDetails();
        initRetrofit(fragment.getContext());

        T00lsService t00lsService = mRetrofit.create(T00lsService.class);
        Call<InfoDetailResponse<InfoDetail>> call;
        if (PreferenceUtil.readPreference("cookies", "UTH_auth", fragment.getContext()) == null) {
            call = t00lsService.getClassDetail(classification, page, null);
        } else {
            call = t00lsService.getClassDetail(classification, page, PreferenceUtil.readPreference("cookies", "UTH_sid",
                    fragment.getContext()) + "; " + PreferenceUtil.readPreference("cookies", "UTH_auth", fragment.getContext()));
        }

        call.enqueue(new Callback<InfoDetailResponse<InfoDetail>>() {
            @Override
            public void onResponse(@NonNull Call<InfoDetailResponse<InfoDetail>> call, @NonNull Response<InfoDetailResponse<InfoDetail>> response) {
                infoDetails.postValue(response.body().data);
            }

            @Override
            public void onFailure(Call<InfoDetailResponse<InfoDetail>> call, Throwable t) {
                infoDetails.postValue(null);
            }
        });

    }

    public void login(String username, String password,Integer questionId,String answer, FragmentActivity activity) {
        MutableLiveData<List<String>> cookiess = ViewModelProviders.of(activity).get(EventViewModel.class).getCookies();

        initRetrofit(activity);
        T00lsService t00lsService = mRetrofit.create(T00lsService.class);

        Call<BaseResponse> call = t00lsService.login(username, MD5Util.encrypt(password), questionId, answer);

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {


                List<String> cookies = new ArrayList();
                if (response.body().message.equals("loginsuccess")) {
                    cookies = response.headers().toMultimap().get("Set-Cookie");
                    List<String> pure = new ArrayList<>();
                    for (String cookie : cookies) {
                        if (cookie.startsWith("UTH_sid") || cookie.startsWith("UTH_auth")) {
                            String[] result = cookie.split(";");
                            pure.add(result[0]);
                        }

                    }
                    cookiess.postValue(pure);
                } else if (response.body().message.equals(Constants.ALREADY_LOGIN)) {
                    cookies.add(Constants.ALREADY_LOGIN);
                    cookiess.postValue(cookies);
                } else if (response.body().message.matches("[0-9]+")) {
                    cookies.add(Constants.WRONG_LOGIN);
                    cookies.add(response.body().message);
                    cookiess.postValue(cookies);
                } else if (response.body().message.equals(Constants.IP_LOCKDOWN)) {
                    cookies.add(Constants.IP_LOCKDOWN);
                    cookiess.postValue(cookies);
                } else if (response.body().message.equals(Constants.NEED_ACTIVATION)) {
                    cookies.add(Constants.NEED_ACTIVATION);
                    cookiess.postValue(cookies);
                } else if (response.body().message.equals(Constants.WRONG_ACTION)) {
                    cookies.add(Constants.WRONG_ACTION);
                    cookiess.postValue(cookies);
                } else if (response.body().message.equals(Constants.LOGIN_QUESTION_INVALID)) {
                    cookies.add(Constants.LOGIN_QUESTION_INVALID);
                    cookiess.postValue(cookies);
                } else if (response.body().message.equals(Constants.LOGIN_INVALID)) {
                    cookies.add(Constants.LOGIN_INVALID);
                    cookiess.postValue(cookies);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                cookiess.postValue(null);
            }
        });
    }

    public void requireUserInfo(Fragment fragment) {
        MutableLiveData<MemberInfo> memberInfo = ViewModelProviders.of(fragment).get(InfoViewModel.class).getMemberInfo();

        initRetrofit(fragment.getContext());

        if (PreferenceUtil.readPreference("cookies", "UTH_auth", fragment.getActivity()) == null){
            memberInfo.postValue(null);
        }
        T00lsService t00lsService = mRetrofit.create(T00lsService.class);
        Call<MemberInfoResponse> call = t00lsService.getMemberInfo(Constants.PROFILE, PreferenceUtil.readPreference("cookies", "UTH_sid", fragment.getActivity()) + "; " + PreferenceUtil.readPreference("cookies", "UTH_auth", fragment.getActivity()));

        call.enqueue(new Callback<MemberInfoResponse>() {
            @Override
            public void onResponse(Call<MemberInfoResponse> call, Response<MemberInfoResponse> response) {
                MemberInfo memberInfo1 = response.body().mMemberInfo;
                memberInfo.postValue(memberInfo1);
            }

            @Override
            public void onFailure(Call<MemberInfoResponse> call, Throwable t) {
                memberInfo.postValue(null);
            }
        });
    }

    public void requireUpdate(FragmentActivity activity) {

        MutableLiveData<UpdateResponse> updateResp = ViewModelProviders.of(activity).get(InfoViewModel.class).getUpdateResp();

        initRetrofit(activity);

        T00lsService t00lsService = mRetrofit.create(T00lsService.class);
        Call<UpdateResponse> call;
        if (PreferenceUtil.readPreference("cookies","UTH_auth",activity)!=null) {
            call = t00lsService.update(
                    InternetUtil.getNetworkState(activity),
                    Build.MODEL,
                    Build.VERSION.RELEASE,
                    activity.getString(R.string.version_name),
                    Build.DEVICE + " " + Build.SERIAL,
                    PreferenceUtil.readPreference("cookies", "UTH_sid", activity) + "; " + PreferenceUtil.readPreference("cookies", "UTH_auth", activity)
            );
        }else {
            call = t00lsService.update(
                    InternetUtil.getNetworkState(activity),
                    Build.MODEL,
                    Build.VERSION.RELEASE,
                    activity.getString(R.string.version_name),
                    Build.DEVICE+" "+Build.SERIAL,
                    null
            );
        }
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                updateResp.postValue(response.body());
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                updateResp.postValue(null);
            }
        });
    }

    public void signToday(Fragment fragment) {

        MutableLiveData<String> signFlag = ViewModelProviders.of(fragment).get(UserInfoViewModel.class).getIsSignedToday();

        initRetrofit(fragment.getContext());

        T00lsService t00lsService = mRetrofit.create(T00lsService.class);
        if (PreferenceUtil.getObject("user_info",
                "user_info",
                MemberInfo.class,
                fragment.getContext()) == null&&PreferenceUtil.readPreference("cookies","UTH_auth",fragment.getActivity())==null) {
            return;
        }
        Call<BaseResponse> call = t00lsService.sign(
                PreferenceUtil.readPreference("cookies", "UTH_sid", fragment.getActivity()) + "; " + PreferenceUtil.readPreference("cookies", "UTH_auth", fragment.getActivity()),
                PreferenceUtil.getObject("user_info",
                "user_info",
                MemberInfo.class,
                fragment.getContext()).formhash, "apply");
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                requireUserInfo(fragment);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
            }
        });
    }

    private void initRetrofit(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .removeHeader("User-Agent")
                            .addHeader("User-Agent", UserAgentUtil.getUserAgent(context))
                            .build();
                    return chain.proceed(request);
                })
                .readTimeout(9, TimeUnit.SECONDS)
                .build();
        if (mRetrofit == null)
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
    }

}
