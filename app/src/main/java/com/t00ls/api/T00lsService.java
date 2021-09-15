package com.t00ls.api;


import com.t00ls.vo.InfoDetail;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by 123 on 2018/3/25.
 */

public interface T00lsService {
    @POST("{class}-articles.json")
    Call<InfoDetailResponse<InfoDetail>> getClassDetail(@Path("class") String classification,
                                                        @Query("page") Integer page,
                                                        @Header("cookie") String cookie);

    @POST("members-{info}.json")
    Call<MemberInfoResponse> getMemberInfo(@Path("info") String info,
                                           @Header("cookie") String cookie);

    @POST("login.json?action=login")
    Call<BaseResponse> login(@Query("username") String username,
                             @Query("password") String password,
                             @Query("questionid") Integer questionId,
                             @Query("answer") String answer);

    @POST("app-update.json?type=android")
    @FormUrlEncoded
    Call<UpdateResponse> update(@Field("network") String network,
                                @Field("phone_model") String phoneModel,
                                @Field("phone_version") String phoneVersion,
                                @Field("app_version") String appVersion,
                                @Field("phone_info") String phoneInfo,
                                @Header("cookie") String cookies);

    @POST("ajax-sign.json")
    Call<BaseResponse> sign(@Header("cookie") String cookies,
                            @Query("formhash") String formhash,
                            @Query("signsubmit") String signSubmit);
}
