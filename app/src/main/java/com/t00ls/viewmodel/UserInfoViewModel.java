package com.t00ls.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

/**
 * Created by 123 on 2018/3/29.
 */

public class UserInfoViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> refreshUserData;

    private MutableLiveData<String> isSignedToday;

    public UserInfoViewModel(@NonNull Application application) {
        super(application);
        // [可选]设置是否打开debug输出，上线时请关闭，Logcat标签为"MtaSDK"
//        StatConfig.setDebugEnable(true);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this.getApplication());
    }

    public MutableLiveData<Integer> getRefreshUserData() {
        if (refreshUserData == null) {
            refreshUserData = new MutableLiveData<>();
            return refreshUserData;
        }
        return refreshUserData;
    }


    public MutableLiveData<String> getIsSignedToday() {
        if (isSignedToday == null) {
            isSignedToday = new MutableLiveData<>();
            return isSignedToday;
        }
        return isSignedToday;
    }
}
