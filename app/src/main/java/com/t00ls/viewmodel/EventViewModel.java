package com.t00ls.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * Created by 123 on 2018/3/27.
 */

public class EventViewModel extends ViewModel {
    private MutableLiveData<Integer> scrollFlag;
    private MutableLiveData<List<String>> cookies;

    public MutableLiveData<Integer> getScrollFlag() {
        if (scrollFlag == null) {
            scrollFlag = new MutableLiveData<>();
            return scrollFlag;
        }
        return scrollFlag;
    }

    public MutableLiveData<List<String>> getCookies() {
        if (cookies == null) {
            cookies = new MutableLiveData<>();
            return cookies;
        }
        return cookies;
    }

}
