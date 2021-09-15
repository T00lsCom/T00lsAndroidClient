package com.t00ls;

import android.app.Application;

import com.t00ls.viewmodel.UserInfoViewModel;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

/**
 * Created by 123 on 2018/3/29.
 */

public class T00lsApp extends Application {

    private UserInfoViewModel mUserInfoViewModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mUserInfoViewModel = new UserInfoViewModel(this);
        CrashReport.initCrashReport(getApplicationContext());


    }
    public UserInfoViewModel getUserInfoViewModel() {
        return mUserInfoViewModel;
    }
}
