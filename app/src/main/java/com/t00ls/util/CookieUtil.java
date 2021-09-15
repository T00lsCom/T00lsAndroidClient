package com.t00ls.util;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Created by 123 on 2018/3/30.
 */

public class CookieUtil {
    public static void syncCookie(Context context, String url) {
        String UTH_sid = PreferenceUtil.readPreference("cookies", "UTH_sid", context);
        String UTH_auth = PreferenceUtil.readPreference("cookies", "UTH_auth", context);

        String UTH_cookietime = "UTH_cookietime=2592000";

        CookieSyncManager.createInstance(context);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
        }
        cookieManager.removeAllCookie();
        if (UTH_auth != null && UTH_sid != null) {

            cookieManager.setCookie(url, UTH_sid);
            cookieManager.setCookie(url, UTH_auth);
            cookieManager.setCookie(url, UTH_cookietime);

            CookieSyncManager.getInstance().sync();// To get instant sync instead of waiting for the timer to trigger, the host can call this.
        }
    }

}
