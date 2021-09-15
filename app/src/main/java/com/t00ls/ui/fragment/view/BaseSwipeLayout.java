package com.t00ls.ui.fragment.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by 123 on 2018/4/2.
 */

public class BaseSwipeLayout extends FrameLayout {

    private Activity mActivity;

    private View dragView;

    private ViewDragHelper mViewDragHelper;

    private Point mAutoBackOriginalPoint = new Point();
    private Point mCurArrivePoint = new Point();

    private int mCurEdgeFlag = ViewDragHelper.EDGE_LEFT;
    private int mSwipeEdge = ViewDragHelper.EDGE_LEFT;

    private OnFinishScroll mOnFinishScroll;

    public BaseSwipeLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public BaseSwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseSwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                mCurArrivePoint.x = left;
                if (mCurEdgeFlag != ViewDragHelper.EDGE_BOTTOM) {
                    return left;
                }
                return 0;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                mCurArrivePoint.y = top;
                if (mCurEdgeFlag == ViewDragHelper.EDGE_BOTTOM) {
                    return top;
                }
                return 0;
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                switch (mCurEdgeFlag) {
                    case ViewDragHelper.EDGE_LEFT:
                        if (mCurArrivePoint.x > getWidth() / 3) {
                            mViewDragHelper.settleCapturedViewAt(getWidth(), mAutoBackOriginalPoint.y);
                        }else {
                            mViewDragHelper.settleCapturedViewAt(mAutoBackOriginalPoint.x, mAutoBackOriginalPoint.y);
                        }
                        break;
                    case ViewDragHelper.EDGE_RIGHT:
                        if (mCurArrivePoint.x < -getWidth() / 3) {
                            mViewDragHelper.settleCapturedViewAt(-getWidth(), mAutoBackOriginalPoint.y);
                        } else {
                            mViewDragHelper.settleCapturedViewAt(mAutoBackOriginalPoint.x, mAutoBackOriginalPoint.y);
                        }
                        break;
                }

                mCurArrivePoint.x = 0;
                mCurArrivePoint.y = 0;
                invalidate();

            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                switch (mCurEdgeFlag) {
                    case ViewDragHelper.EDGE_LEFT:
                        if (left >= getWidth()) {
                            if (mOnFinishScroll != null) {
                                mOnFinishScroll.onComplete();
                            }
                        }
                        break;
                    case ViewDragHelper.EDGE_RIGHT:
                        if (left <= -getWidth()) {
                            if (mOnFinishScroll != null) {
                                mOnFinishScroll.onComplete();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                mCurEdgeFlag = edgeFlags;
                if (dragView == null) {
                    dragView = getChildAt(0);
                }
                mViewDragHelper.captureChildView(dragView, pointerId);
            }
        });

        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public void attachToActivity(Activity activity) {
        mActivity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decorView.removeView(decorChild);
        addView(decorChild);
        decorView.addView(this);
    }


    public void setOnFinishScroll(OnFinishScroll onFinishScroll) {
        mOnFinishScroll = onFinishScroll;
    }

    public void setSwipeEdge(int swipeEdge) {
        mSwipeEdge = swipeEdge;
    }

    public interface OnFinishScroll{
        void onComplete();
    }
}
