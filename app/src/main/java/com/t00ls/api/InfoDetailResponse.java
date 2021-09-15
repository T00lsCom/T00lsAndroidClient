package com.t00ls.api;

import com.google.gson.annotations.SerializedName;
import com.t00ls.vo.InfoDetail;

import java.util.List;

/**
 * Created by 123 on 2018/3/25.
 */

public class InfoDetailResponse<T> {
    public String status;
    @SerializedName("articleslist")
    public List<T> data;
}
