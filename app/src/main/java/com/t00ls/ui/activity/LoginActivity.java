package com.t00ls.ui.activity;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.T00lsApp;
import com.t00ls.api.ApiManager;
import com.t00ls.util.PreferenceUtil;
import com.t00ls.viewmodel.EventViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static String[] questions = {"母亲的名字", "爷爷的名字", "父亲出生的城市","您其中一位老师的名字", "您个人计算机的型号", "您最喜欢的餐馆名称", "驾驶执照的最后四位数字"};

    @BindView(R.id.login_ui)
    LinearLayout loginUi;

    @BindView(R.id.loading_ui)
    ProgressBar mProgressBar;

    @BindView(R.id.til_username)
    TextInputLayout userName;

    @BindView(R.id.til_password)
    TextInputLayout password;

    @BindView(R.id.til_answer)
    TextInputLayout answer;

    @BindView(R.id.sp_question)
    Spinner mSpinner;

    @BindView(R.id.btn_login)
    Button login;

    private int questionId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setFinishOnTouchOutside(true);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, questions);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionId = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        login.setOnClickListener(v -> {
            if (userName.getEditText().getText().toString().equals("")) {
                userName.setErrorEnabled(true);
                userName.setError("请输入用户名");
                if (password.getEditText().getText().toString().equals("")) {
                    password.setErrorEnabled(true);
                    password.setError("请输入密码");
                    return;
                }
                password.setErrorEnabled(false);
                return;
            } else if (answer.getEditText().getText().toString().equals("")) {
                answer.setErrorEnabled(true);
                answer.setError("请输入回答");
                return;
            }
            setFinishOnTouchOutside(false);
            userName.setErrorEnabled(false);
            password.setErrorEnabled(false);
            answer.setErrorEnabled(false);
            loginUi.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            ApiManager.getInstance().login(userName.getEditText().getText().toString(),
                    password.getEditText().getText().toString(),
                    questionId, answer.getEditText().getText().toString(),
                    LoginActivity.this);

            dismissSoftInput();
        });

        MutableLiveData<List<String>> cookies = ViewModelProviders.of(this).get(EventViewModel.class).getCookies();
        MutableLiveData<Integer> refreshUserData = ((T00lsApp) getApplication()).getUserInfoViewModel().getRefreshUserData();

        cookies.observe(this, cookiess -> {
            loginUi.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            setFinishOnTouchOutside(true);
            if (cookiess == null) {
                Toast.makeText(LoginActivity.this, "服务器未响应", Toast.LENGTH_LONG).show();
                return;
            }else if (cookiess.get(0).equals(Constants.ALREADY_LOGIN)) {
                Toast.makeText(LoginActivity.this, "您已登陆！", Toast.LENGTH_LONG).show();
                return;
            } else if (cookiess.get(0).equals(Constants.WRONG_LOGIN)) {
                Toast.makeText(LoginActivity.this, "用户名或密码错误，剩余" + cookiess.get(1) + "次封锁IP", Toast.LENGTH_LONG).show();
                return;
            } else if (cookiess.get(0).equals(Constants.IP_LOCKDOWN)) {
                Toast.makeText(LoginActivity.this, "IP已被封锁，请15分钟后重试！", Toast.LENGTH_LONG).show();
                return;
            } else if (cookiess.get(0).equals(Constants.LOGIN_INVALID)) {
                Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
                return;
            } else if (cookiess.get(0).equals(Constants.LOGIN_QUESTION_INVALID)) {
                Toast.makeText(LoginActivity.this, "答案不正确！", Toast.LENGTH_LONG).show();
                return;
            } else if (cookiess.get(0).equals(Constants.WRONG_ACTION)) {
                Toast.makeText(LoginActivity.this, "错误的方法！", Toast.LENGTH_LONG).show();
                return;
            } else if (cookiess.get(0).equals(Constants.NEED_ACTIVATION)) {
                Toast.makeText(LoginActivity.this, "账户需要激活！", Toast.LENGTH_LONG).show();
                return;
            }
            for (String cookie : cookiess) {
                String[] result = cookie.split("=");
                PreferenceUtil.storePreference("cookies", result[0], cookie, LoginActivity.this);
            }
//            storeUserNameAndPassword();
            Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_LONG).show();
            refreshUserData.postValue(Constants.REFRESH_USER_DATA);
            finish();

        });

    }


    private void dismissSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
