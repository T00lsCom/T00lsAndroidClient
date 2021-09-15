package com.t00ls.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.t00ls.R;
import com.t00ls.ui.fragment.view.BaseSwipeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tv_email)
    TextView email;

    @BindView(R.id.tv_weibo)
    TextView weibo;

    @BindView(R.id.tv_third_party)
    TextView thirdParty;

    @BindView(R.id.swipe_layout)
    BaseSwipeLayout mBaseSwipeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        mBaseSwipeLayout.setOnFinishScroll(this::finish);

        SpannableString emailString = new SpannableString("任何事项请联系官方邮箱:admin@t00ls.cc");
        emailString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, "admin@t00ls.cc");
                startActivity(Intent.createChooser(intent, "请选择邮箱"));
            }
        }, 12, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        email.setText(emailString);
        email.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString weiboString = new SpannableString("新浪微博:http://weibo.com/t00lsnet");
        weiboString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://weibo.com/t00lsnet"));
                startActivity(intent);
            }
        }, 5, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        weibo.setText(weiboString);
        weibo.setMovementMethod(LinkMovementMethod.getInstance());

        String string = "CircleImageView\n" +
                "\n"+
                "https://github.com/hdodenhof/CircleImageView\n" +
                "\n"+
                "ButterKnife\n" +
                "\n"+
                "https://github.com/JakeWharton/butterknife\n" +
                "\n"+
                "PermissionDispatcher\n" +
                "\n"+
                "https://github.com/permissions-dispatcher/PermissionsDispatcher\n"+
                "\n"+
                "OkDownload\n"+
                "\n"+
                "https://github.com/lingochamp/okdownload"
                ;

        thirdParty.setText(string);

    }

}
