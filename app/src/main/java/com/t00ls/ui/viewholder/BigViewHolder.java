package com.t00ls.ui.viewholder;

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
 * Created by 123 on 2018/3/27.
 */
@Deprecated
public class BigViewHolder extends RecyclerView.ViewHolder {

    private View itemView;

    @BindView(R.id.tv_title)
    TextView titleView;

    @BindView(R.id.tv_content)
    TextView contentView;

    @BindView(R.id.tv_comment)
    TextView commentView;

    @BindView(R.id.tv_like)
    TextView likeView;

    @BindView(R.id.tv_date)
    TextView dateView;

    @BindView(R.id.news_image)
    ImageView mImageView;

    public BigViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        ButterKnife.bind(this, itemView);
    }

    public void bind(InfoDetail infoDetail) {
        commentView.setText(infoDetail.replies + "回复");
        likeView.setText(infoDetail.views + "看过");
        dateView.setText(infoDetail.dateline);
        titleView.setText(infoDetail.subject);
        contentView.setText(infoDetail.message);
        if (infoDetail.imageUrl.equals("")) {
            mImageView.setVisibility(View.GONE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            Glide.with(itemView).load(Constants.BASE_URL + infoDetail.imageUrl).into(mImageView);
        }
    }
}
