package com.t00ls.ui.fragment;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * Created by 123 on 2018/3/27.
 */

public class CommonChildFragment extends Fragment {

    private static final String TAG = "CommonChildFragment";

    private String classification;

    @BindView(R.id.base_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.sr_list)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.net_error_view)
    LinearLayout emptyView;

    private Bundle mBundle;

    public static CommonChildFragment newInstance(Bundle bundle) {
        CommonChildFragment commonChildFragment = new CommonChildFragment();
        commonChildFragment.setArguments(bundle);
        return commonChildFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.child_fragment, container, false);
        ButterKnife.bind(this, view);


        classification = mBundle.getString("classification");

        MutableLiveData<List<InfoDetail>> infoDetails = ViewModelProviders.of(this).get(InfoViewModel.class).getInfoDetails();
        MutableLiveData<Integer> scrollFlag = ViewModelProviders.of(getParentFragment().getActivity()).get(EventViewModel.class).getScrollFlag();

        PageLoadAdapter pageLoadAdapter = new PageLoadAdapter((pagePosition, pageSize) -> {
            ApiManager.getInstance().loadInfoDetail(classification, pagePosition, CommonChildFragment.this);
        });

        pageLoadAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("path", pageLoadAdapter.getDataSet().get(position).links);
                startActivity(intent);
            }

            @Override
            public void OnLongClick(int position) {

            }
        });

        infoDetails.observe(this, infoDetails1 -> {
            if (infoDetails1 == null) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                emptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                return;
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            if (mRefreshLayout.isRefreshing()) {
                mRefreshLayout.setRefreshing(false);
                pageLoadAdapter.clearData();
            }
            if (infoDetails1.isEmpty()) {
                pageLoadAdapter.setHasMoreData(false);
            }
            pageLoadAdapter.appendData(infoDetails1);
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(pageLoadAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 10) {
                    scrollFlag.postValue(Constants.NAVI_DISMISS);
                } else if (dy < -10) {
                    scrollFlag.postValue(Constants.NAVI_SHOW);
                }
            }
        });

        mRefreshLayout.setOnRefreshListener(() -> {
            ApiManager.getInstance().loadInfoDetail(classification, 1, CommonChildFragment.this);
            pageLoadAdapter.setHasMoreData(true);
        });


        return view;
    }


}
