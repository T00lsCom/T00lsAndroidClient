package com.t00ls.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 123 on 2018/3/25.
 */

public class InfoDetail {
    public String tid;
    public String subject;
    public String message;
    public String dateline;
    public String attachment;
    public Integer fid;
    public Integer views;
    public String links;
    public String flinks;
    public Integer replies;
    @SerializedName("pic")
    public String imageUrl;
    @SerializedName("fname")
    public String classfication;
}


