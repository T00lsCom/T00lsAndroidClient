package com.t00ls.ui.activity;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.T00lsApp;
import com.t00ls.api.ApiManager;
import com.t00ls.api.UpdateResponse;
import com.t00ls.service.UpdateService;
import com.t00ls.ui.fragment.AboutFragment;
import com.t00ls.ui.fragment.ArticleFragment;
import com.t00ls.ui.fragment.HomeFragment_new;
import com.t00ls.ui.fragment.NewsFragment;
import com.t00ls.ui.fragment.ToolFragment;
import com.t00ls.util.NavigationViewHelper;
import com.t00ls.util.PreferenceUtil;
import com.t00ls.viewmodel.EventViewModel;
import com.t00ls.viewmodel.InfoViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.t00ls.service.UpdateService.TASK_COMPLETE;
import static com.t00ls.service.UpdateService.TASK_ERROR;
import static com.t00ls.service.UpdateService.TASK_PROGRESS;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {


    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    FragmentManager fragmentManager;

    AlertDialog updateDialog;

    boolean isUpdateSelected = false;

    int navigationTop;

    String versionName;

    private UpdateReceiver mUpdateReceiver;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        requestPermission();

        initReceiver();

        toolbar.setLogo(R.mipmap.logo);
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationViewHelper.disableShiftMode(navigation);

        versionName = getString(R.string.version_name);

        MutableLiveData<Integer> scrollFlag = ViewModelProviders.of(this).get(EventViewModel.class).getScrollFlag();

        MutableLiveData<UpdateResponse> update = ViewModelProviders.of(this).get(InfoViewModel.class).getUpdateResp();

        navigation.post(() ->
                navigationTop = navigation.getTop());


        ApiManager.getInstance().requireUpdate(this);

        update.observe(this, updateResponse -> {
            if (updateResponse == null) {
                Toast.makeText(this,"更新未响应",Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("dev","versionname:"+versionName+"    resversion"+updateResponse.version );
            if (!versionName.equals(updateResponse.version)) {
                initUpdateDialog(updateResponse.info, updateResponse.updateUrl);
                updateDialog.show();
            }
        });


        scrollFlag.observe(this, (Integer flag) -> {
            if (flag == Constants.NAVI_DISMISS) {
                naviDismissAnim();
            } else if (flag == Constants.NAVI_SHOW) {
                naviShowAnim();
            }
        });

        AboutFragment aboutFragment = AboutFragment.newInstance();
        ArticleFragment articleFragment = ArticleFragment.newInstance();
        HomeFragment_new homeFragment = HomeFragment_new.newInstance();
        NewsFragment newsFragment = NewsFragment.newInstance();
        ToolFragment toolFragment = ToolFragment.newInstance();

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.commit();


        BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.fragment_container, homeFragment, "home");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_news:
                    fragmentTransaction.replace(R.id.fragment_container, newsFragment, "news");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_article:
                    fragmentTransaction.replace(R.id.fragment_container, articleFragment, "article");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_tools:
                    fragmentTransaction.replace(R.id.fragment_container, toolFragment, "tools");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_about_me:
                    fragmentTransaction.replace(R.id.fragment_container, aboutFragment, "about");
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        };

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private void initReceiver() {
        mUpdateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TASK_COMPLETE);
        intentFilter.addAction(TASK_PROGRESS);
        intentFilter.addAction(TASK_ERROR);
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver, intentFilter);
    }

    private void initUpdateDialog(String message, String url) {
        message = message.replace("\\n", "\n");
        View view = View.inflate(this, R.layout.dialog_update, null);

        TextView textView = view.findViewById(R.id.tv_info);

        textView.setText(message);


        updateDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("版本更新")
                .setPositiveButton("立即更新", null)
                .setNegativeButton("暂不更新", null)
                .create();

        updateDialog.setOnShowListener(dialog -> {
            Button positiveBtn = updateDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            positiveBtn.setOnClickListener(v -> {
                if (isUpdateSelected)
                    return;
                Intent intent = new Intent(MainActivity.this, UpdateService.class);
                intent.putExtra("url", url);
                startService(intent);
                isUpdateSelected = true;
            });


        });

        updateDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (navigation.getSelectedItemId()) {
            case R.id.navigation_home:
            case R.id.navigation_news:
            case R.id.navigation_article:
            case R.id.navigation_tools:
                menu.findItem(R.id.login_or_logout).setVisible(false);
                menu.findItem(R.id.about_t00ls).setVisible(true);
                break;
            case R.id.navigation_about_me:
                if (PreferenceUtil.readPreference("cookies", "UTH_auth", this) != null) {
                    menu.findItem(R.id.login_or_logout).setTitle("注销");
                } else {
                    menu.findItem(R.id.login_or_logout).setTitle("登录");
                }
                menu.findItem(R.id.login_or_logout).setVisible(true);
                menu.findItem(R.id.about_t00ls).setVisible(false);
                break;

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_t00ls:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.login_or_logout:
                if (PreferenceUtil.readPreference("cookies", "UTH_auth", this) != null) {
                    confirmLogOut();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmLogOut() {
        MutableLiveData<Integer> refreshUser = ((T00lsApp) getApplication()).getUserInfoViewModel().getRefreshUserData();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("确认注销？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    logout();
                    Toast.makeText(this, "注销成功！", Toast.LENGTH_LONG).show();
                    refreshUser.postValue(Constants.REFRESH_USER_DATA);
                });
        builder.show();
    }

    private void logout() {
        PreferenceUtil.clearPreference("cookies", "UTH_sid", this);
        PreferenceUtil.clearPreference("cookies", "UTH_auth", this);
        PreferenceUtil.clearPreference("user_info", "user_info", this);
    }

    private void naviDismissAnim() {
        navigation.animate()
                .setDuration(100)
                .translationY(navigation.getHeight())
                .start();
    }

    private void naviShowAnim() {
        navigation.animate()
                .setDuration(100)
                .translationY(0)
                .start();
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    void requestPermission() {

    }

    class UpdateReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            ProgressBar mProgressBar = updateDialog.findViewById(R.id.dialog_progress);
            TextView mTextView = updateDialog.findViewById(R.id.tv_info);
            switch (intent.getAction()) {
                case TASK_COMPLETE:
                    updateDialog.dismiss();
                    break;
                case TASK_PROGRESS:
                    Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                    updateDialog.getButton(DialogInterface.BUTTON_POSITIVE).setClickable(false);
                    updateDialog.setTitle("正在下载");
                    mTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(intent.getIntExtra("progress", 0));
                    break;
                case TASK_ERROR:
                    Toast.makeText(MainActivity.this, "更新服务器异常", Toast.LENGTH_LONG).show();
                    updateDialog.dismiss();

            }
        }
    }

}
