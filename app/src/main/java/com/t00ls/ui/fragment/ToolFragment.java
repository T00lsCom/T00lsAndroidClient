package com.t00ls.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.util.CookieUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 123 on 2018/3/24.
 */

public class ToolFragment extends Fragment {

    private WebView mWebView;

    private ArrayList<String> classifications;

    @BindView(R.id.tools_container)
    LinearLayout mLinearLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.t00ls_tabs)
    TabLayout toolTab;


    public static ToolFragment newInstance() {
        return new ToolFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);
        ButterKnife.bind(this, view);

        classifications = new ArrayList<>();
        classifications.add(Constants.DOMAIN);
        classifications.add(Constants.IP_SEARCH);
        classifications.add(Constants.NAVIAGATION);
        classifications.add(Constants.HASH);
        classifications.add(Constants.CONVERT);
        classifications.add(Constants.PYTHON_TOOL);
        classifications.add(Constants.ASCII);

        createWebView(classifications.get(0));

        toolTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                createWebView(classifications.get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                destroyWebView();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView(String classification) {
        mWebView = new WebView(getContext());
        mWebView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLinearLayout.addView(mWebView);

        CookieUtil.syncCookie(getContext(), Constants.BASE_URL + classification);
        mWebView.loadUrl(Constants.BASE_URL + classification);

//        Log.d("dev","webview_ua:   "+mWebView.getSettings().getUserAgentString());
        String ua = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(ua+" (T00ls.Net)");

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);


        mWebView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
            return false;
        });
    }

    private void destroyWebView() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            mWebView.clearCache(true);

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onDestroyView() {
        destroyWebView();
        super.onDestroyView();
    }
}
