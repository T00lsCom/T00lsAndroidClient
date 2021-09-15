package com.t00ls.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.ui.adapter.BasePagerAdapter;
import com.t00ls.ui.fragment.view.RecyclerViewFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 123 on 2018/3/24.
 */

public class NewsFragment extends Fragment {

    @BindView(R.id.news_container)
    ViewPager mViewPager;

    @BindView(R.id.news_tabs)
    TabLayout mTabLayout;

    public static NewsFragment newInstance() {

        Bundle args = new Bundle();

        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        init(container);
        return view;
    }

    private void init(@Nullable ViewGroup container) {


        if (container != null) {

            List<Fragment> newsPages = new ArrayList<>();
            newsPages.add(RecyclerViewFactory.getRecyclerView(Constants.NEWS));
            newsPages.add(RecyclerViewFactory.getRecyclerView(Constants.VULS));
            newsPages.add(RecyclerViewFactory.getRecyclerView(Constants.PEOPLE));
            newsPages.add(RecyclerViewFactory.getRecyclerView(Constants.LAW));
            newsPages.add(RecyclerViewFactory.getRecyclerView(Constants.LOVE));


            BasePagerAdapter newsPagerAdapter = new BasePagerAdapter(getChildFragmentManager());
            newsPagerAdapter.setFragments(newsPages);
            mViewPager.setAdapter(newsPagerAdapter);
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
