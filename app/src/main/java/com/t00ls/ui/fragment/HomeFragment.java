package com.t00ls.ui.fragment;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.api.ApiManager;
import com.t00ls.ui.activity.WebViewActivity;
import com.t00ls.ui.adapter.BaseAdapter;
import com.t00ls.ui.adapter.PageLoadAdapter;
import com.t00ls.viewmodel.EventViewModel;
import com.t00ls.viewmodel.InfoViewModel;
import com.t00ls.vo.InfoDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.t00ls.Constants.NAVI_DISMISS;
import static com.t00ls.Constants.NAVI_SHOW;

/**
 * Created by 123 on 2018/3/24.
 */

@Deprecated
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.news_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.sr_list)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.net_error_view)
    LinearLayout emptyView;

    PageLoadAdapter mPageLoadAdapterWrapper;

//    private NetWorkChangeReceiver mReceiver;

    private MutableLiveData<List<InfoDetail>> infoDetails;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

//        mReceiver = new NetWorkChangeReceiver();
//        getActivity().registerReceiver(mReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));



        MutableLiveData<Integer> scrollFlag = ViewModelProviders.of(getActivity()).get(EventViewModel.class).getScrollFlag();

        infoDetails = ViewModelProviders.of(this).get(InfoViewModel.class).getInfoDetails();

        mPageLoadAdapterWrapper = new PageLoadAdapter((pagePosition, pageSize) -> {
            Log.e(TAG, "onCreateView: " + pagePosition);
            ApiManager.getInstance().loadInfoDetail(Constants.ALL,pagePosition,HomeFragment.this);
        });

        mPageLoadAdapterWrapper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("path", mPageLoadAdapterWrapper.getDataSet().get(position).links);
                startActivity(intent);
            }

            @Override
            public void OnLongClick(int position) {

            }
        });


        infoDetails.observe(this, infoDetails1 -> {
            if (infoDetails1==null){
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                emptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                return;
            }
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            if (mRefreshLayout.isRefreshing()) {
                mPageLoadAdapterWrapper.clearData();
                mRefreshLayout.setRefreshing(false);
                mPageLoadAdapterWrapper.appendData(infoDetails1);
            }
            if (infoDetails1.isEmpty()) {
                mPageLoadAdapterWrapper.setHasMoreData(false);
            }else {
                mPageLoadAdapterWrapper.appendData(infoDetails1);
            }
        });
        mRecyclerView.setAdapter(mPageLoadAdapterWrapper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 10) {
                    scrollFlag.postValue(NAVI_DISMISS);
                } else if (dy < -10) {
                    scrollFlag.postValue(NAVI_SHOW);
                }
            }
        });

        mRefreshLayout.setOnRefreshListener(() -> {
            ApiManager.getInstance().loadInfoDetail(Constants.ALL, 1, HomeFragment.this);
            mPageLoadAdapterWrapper.setHasMoreData(true);
        });
        view.postInvalidate();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(mReceiver);
    }

//    class NetWorkChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            if (networkInfo != null && networkInfo.isAvailable()) {
//                mRefreshLayout.post(() -> {
//                    ApiManager.getInstance().loadInfoDetail(Constants.ALL, 1, HomeFragment.this);
//                    mRefreshLayout.setRefreshing(true);
//                });
//            }
//        }
//    }


}
