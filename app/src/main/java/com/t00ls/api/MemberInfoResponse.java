package com.t00ls.api;

import com.google.gson.annotations.SerializedName;
import com.t00ls.vo.MemberInfo;

/**
 * Created by 123 on 2018/3/27.
 */

public class MemberInfoResponse {
    public String status;
    @SerializedName("memberinfo")
    public MemberInfo mMemberInfo;
}
