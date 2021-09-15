package com.t00ls.util;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;

/**
 * Created by 123 on 2018/4/5.
 */

public class UserAgentUtil {

    public static String getUserAgent(Context context) {
        String userAgent;
        try {
            userAgent = WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
//        Log.d("dev","api_ua:  "+sb.toString()+ " (T00ls.Net)");
        return sb.toString()+" (T00ls.Net)";
    }

}
