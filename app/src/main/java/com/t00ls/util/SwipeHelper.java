package com.t00ls.util;

import android.app.Activity;
import android.view.LayoutInflater;

import com.t00ls.R;
import com.t00ls.ui.fragment.view.BaseSwipeLayout;


/**
 * Created by 123 on 2018/4/2.
 */

public class SwipeHelper {

    private Activity mActivity;
    private BaseSwipeLayout mBaseSwipeLayout;


    public SwipeHelper(Activity activity) {
        mActivity = activity;
    }

    public void onActivityCreate() {
        mBaseSwipeLayout = (BaseSwipeLayout) LayoutInflater.from(mActivity).inflate(R.layout.swipe_layout, null);
        mBaseSwipeLayout.setOnFinishScroll(() -> {
            mActivity.finish();
        });
    }

    public void onPostCreated() {
        mBaseSwipeLayout.attachToActivity(mActivity);
    }

    public void setSwipeEdge(int edgeFlag) {
        mBaseSwipeLayout.setSwipeEdge(edgeFlag);
    }
}
