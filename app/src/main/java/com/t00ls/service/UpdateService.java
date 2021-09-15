package com.t00ls.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;
import com.t00ls.BuildConfig;
import com.t00ls.util.PreferenceUtil;
import com.t00ls.util.UserAgentUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateService extends IntentService {

    public static final String TASK_COMPLETE = "complete";
    public static final String TASK_PROGRESS = "progress";
    public static final String TASK_ERROR = "error";


    String url;


    public UpdateService() {
        super("UpdateService");
    }


    public UpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        url = intent.getStringExtra("url");

        Map<String, List<String>> headers = new HashMap<>();
        List<String> userAgent = new ArrayList<>();
        List<String> cookies = new ArrayList<>();
        if (PreferenceUtil.readPreference("cookies", "UTH_auth", this)!=null) {
            cookies.add(PreferenceUtil.readPreference("cookies", "UTH_sid", this));
            cookies.add(PreferenceUtil.readPreference("cookies", "UTH_auth", this));
            headers.put("Cookies", cookies);
        }
        userAgent.add(UserAgentUtil.getUserAgent(this));
        headers.put("User-Agent", userAgent);


        DownloadTask task = new DownloadTask.Builder(url, getExternalCacheDir())
                .setHeaderMapFields(headers)
                .setFilename("T00ls.apk")
                .setMinIntervalMillisCallbackProcess(500)
                .setPassIfAlreadyCompleted(false)
                .build();

        task.enqueue(new DownloadListener3() {
            @Override
            protected void started(@NonNull DownloadTask task) {

            }

            @Override
            protected void completed(@NonNull DownloadTask task) {
                Intent intent1 = new Intent();
                intent1.setAction(TASK_COMPLETE);
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent1);
                installAPK(task.getFile());
            }

            @Override
            protected void canceled(@NonNull DownloadTask task) {

            }

            @Override
            protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(new Intent(TASK_ERROR));
            }

            @Override
            protected void warn(@NonNull DownloadTask task) {

            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                Intent intent1 = new Intent();
                intent1.setAction(TASK_PROGRESS);
                intent1.putExtra("progress", (int)(currentOffset/totalLength*100));
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent1);
            }
        });

    }


    private void installAPK(File file) {
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        }else {
            Uri uri = Uri.parse("file://" + file.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
}
