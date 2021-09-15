package com.t00ls.ui.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.t00ls.R;
import com.t00ls.T00lsApp;
import com.t00ls.api.ApiManager;
import com.t00ls.ui.activity.LoginActivity;
import com.t00ls.util.PreferenceUtil;
import com.t00ls.viewmodel.InfoViewModel;
import com.t00ls.vo.MemberInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 123 on 2018/3/24.
 */

public class AboutFragment extends Fragment {

    @BindView(R.id.background_color)
    LinearLayout layout;

    @BindView(R.id.user_list)
    ListView userInfo;

    @BindView(R.id.cv_user_info)
    CardView userInfoCard;

    @BindView(R.id.tv_username)
    TextView userName;

    @BindView(R.id.cir_user_image)
    CircleImageView userImage;

    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.user_sign)
    TextView userSignInfo;

    @BindView(R.id.tv_click_to_sign)
    TextView clickToSign;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);
        ButterKnife.bind(this, view);

        MutableLiveData<Integer> refreshFlag = ((T00lsApp) getActivity().getApplication()).getUserInfoViewModel().getRefreshUserData();
        MutableLiveData<MemberInfo> memberInfo = ViewModelProviders.of(this).get(InfoViewModel.class).getMemberInfo();


        showUserInfo();

        ApiManager.getInstance().requireUserInfo(AboutFragment.this);
        refreshFlag.observe(this,refreshFlag1->
            ApiManager.getInstance().requireUserInfo(AboutFragment.this)
        );
        memberInfo.observe(this,memberInfos -> {
            if (memberInfos == null) {
                userSignInfo.setVisibility(View.GONE);
                clickToSign.setVisibility(View.GONE);
                userName.setText("未知用户");
                emptyView.setVisibility(View.VISIBLE);
                userInfo.setVisibility(View.GONE);
                Glide.with(this).load(R.drawable.noavatar_middle).into(userImage);
            }else {
                saveUserInfo(memberInfos, getContext());
                showUserInfo();
            }
        });


        userInfoCard.setOnClickListener(v ->
            startActivity(new Intent(AboutFragment.this.getActivity(), LoginActivity.class))
        );

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void showUserInfo() {


        MemberInfo memberInfo1 = readUserInfo(getContext());
        if (memberInfo1 != null) {
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),
                    getUserList(memberInfo1),
                    android.R.layout.simple_list_item_2,
                    new String[]{"key", "value"},
                    new int[]{android.R.id.text1, android.R.id.text2});
            userSignInfo.setVisibility(View.VISIBLE);
            userSignInfo.setText("已签到"+memberInfo1.signTimes+"天");
            userName.setText(memberInfo1.username);
            userInfo.setAdapter(simpleAdapter);
            userInfo.setVisibility(View.VISIBLE);

            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(-1, -1);
            userInfo.setLayoutParams(params1);

            emptyView.setVisibility(View.GONE);
            Glide.with(this).load(memberInfo1.userImage).into(userImage);
            clickToSign.setVisibility(View.VISIBLE);

            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int dpV = (int) metrics.density;

            Log.e("dpV", String.valueOf(dpV));
            Log.e("screenHeight", String.valueOf(screenHeight));



            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) userInfoCard.getLayoutParams();
            params.height = screenHeight - dpV *(140 + 62 + 62);
            userInfoCard.setLayoutParams(params);

            if (memberInfo1.isSignedToday.equals("1")) {
                clickToSign.setText("今日已签到");
                clickToSign.setClickable(false);
            } else if (memberInfo1.isSignedToday.equals("0")) {
                clickToSign.setText("签到领tubi");
                clickToSign.setClickable(true);
            }
        }
    }

    private void saveUserInfo(MemberInfo memberInfo, Context context) {
        PreferenceUtil.setObject("user_info", "user_info", memberInfo, context);
    }

    private MemberInfo readUserInfo(Context context) {
        return PreferenceUtil.getObject("user_info", "user_info", MemberInfo.class, context);
    }

    private List<Map<String, Object>> getUserList(MemberInfo memberInfo) {
        List<Map<String, Object>> listItem = new ArrayList<>();
        listItem.add(createItemInfo("用户组", memberInfo.userGroup));
        if (!memberInfo.introduction.equals("")&&!memberInfo.introduction.equals(" "))
            listItem.add(createItemInfo("认证", memberInfo.introduction));
        if (!memberInfo.status.equals(""))
            listItem.add(createItemInfo("用户状态", memberInfo.status));
        listItem.add(createItemInfo("邮箱地址", memberInfo.email));
        listItem.add(createItemInfo("评论数", memberInfo.comments));
        listItem.add(createItemInfo("文章数", memberInfo.articles));
        listItem.add(createItemInfo("坛龄", memberInfo.days.toString()));
        listItem.add(createItemInfo("TCV", memberInfo.tcv));
        listItem.add(createItemInfo("TuBi", memberInfo.tubi));
        return listItem;
    }

    private Map<String, Object> createItemInfo(String infoType,String infoContent) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", infoType);
        map.put("value", infoContent);
        return map;
    }

    @OnClick(R.id.tv_click_to_sign)
    void clickToSign(View view){
        ApiManager.getInstance().signToday(this);
        ((TextView) view).setText("签到中...");
    }
}
