package com.t00ls.ui.viewholder;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.t00ls.Constants;
import com.t00ls.R;
import com.t00ls.vo.InfoDetail;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 123 on 2018/3/26.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private View itemView;

    @BindView(R.id.tv_comment)
    TextView commentView;
    @BindView(R.id.tv_like)
    TextView likeView;
    @BindView(R.id.tv_date)
    TextView dateView;
    @BindView(R.id.tv_title)
    TextView titleView;
    @BindView(R.id.iv_text_image)
    ImageView userImage;

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
    }

    @SuppressLint("SetTextI18n")
    public void bind(InfoDetail infoDetail) {
        commentView.setText(infoDetail.replies+"评");
        likeView.setText(infoDetail.views+"读");
        dateView.setText(infoDetail.dateline);
        titleView.setText(infoDetail.subject);
        if (infoDetail.imageUrl.equals("")) {
            userImage.setVisibility(View.GONE);
        } else {
            userImage.setVisibility(View.VISIBLE);
            Glide.with(itemView).load(Constants.BASE_URL + infoDetail.imageUrl).into(userImage);
        }
    }
}