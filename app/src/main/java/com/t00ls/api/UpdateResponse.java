package com.t00ls.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 123 on 2018/4/11.
 */

public class UpdateResponse {
    public String info;
    public String version;
    @SerializedName("updateurl")
    public String updateUrl;
}
