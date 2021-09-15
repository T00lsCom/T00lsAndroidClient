package com.t00ls.vo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 123 on 2018/3/25.
 */

public class MemberInfo implements Serializable {
    public String username;
    public String email;
    @SerializedName("posts")
    public String comments;
    public String nickname;
    @SerializedName("threads")
    public String articles;
    @SerializedName("extcredits1")
    public String tcv;
    @SerializedName("extcredits2")
    public String tubi;
    @SerializedName("age")
    public Integer days;
    @SerializedName("authortitle")
    public String userGroup;
    @SerializedName("field_1")
    public String introduction;
    @SerializedName("customstatus")
    public String status;
    @SerializedName("avatar")
    public String userImage;
    @SerializedName("sign_times")
    public String signTimes;
    @SerializedName("sign_today")
    public String isSignedToday;
    public String formhash;
}
