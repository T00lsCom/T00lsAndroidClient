package com.t00ls.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.ui.fragment.view.BaseSwipeLayout;
import com.t00ls.util.CookieUtil;
import com.t00ls.util.PreferenceUtil;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.webview_container)
    LinearLayout container;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.ibtn_back)
    ImageButton back;

    @BindView(R.id.swipe_layout)
    BaseSwipeLayout mBaseSwipeLayout;

    private WebView mWebView;

    private String path;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        back.setOnClickListener(v -> {
            finish();
        });

        mBaseSwipeLayout.setOnFinishScroll(this::finish);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        mWebView = new WebView(this);
        mWebView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(mWebView);

        CookieUtil.syncCookie(WebViewActivity.this.getApplicationContext(), Constants.BASE_URL + path);
        mWebView.loadUrl(Constants.BASE_URL + path);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

        });

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Map<String,String> headers = request.getRequestHeaders();

                String url = request.getUrl().toString();
                if (url.endsWith("login.html")) {
                    startActivity(new Intent(WebViewActivity.this,LoginActivity.class));
                    finish();
                    return null;
                }


                return super.shouldInterceptRequest(view, new WebResourceRequest() {
                    @Override
                    public Uri getUrl() {
                        return request.getUrl();
                    }

                    @Override
                    public boolean isForMainFrame() {
                        return false;
                    }

                    @Override
                    public boolean isRedirect() {
                        return false;
                    }

                    @Override
                    public boolean hasGesture() {
                        return false;
                    }

                    @Override
                    public String getMethod() {
                        return request.getMethod();
                    }

                    @Override
                    public Map<String, String> getRequestHeaders() {
                        headers.remove("UTH_sid");
                        headers.put("UTH_sid", PreferenceUtil.readPreference("cookies", "UTH_sid", WebViewActivity.this));
                        headers.put("UTH_auth", PreferenceUtil.readPreference("cookies", "UTH_auth", WebViewActivity.this));

                        return headers;
                    }
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        String ua = mWebView.getSettings().getUserAgentString();
//        Log.d("dev","webvie: "+ua);
        mWebView.getSettings().setUserAgentString(ua+" (T00ls.Net)");
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptEnabled(true);


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            mWebView.clearCache(true);

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
