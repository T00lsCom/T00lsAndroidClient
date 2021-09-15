package com.t00ls.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.t00ls.R;
import com.t00ls.ui.viewholder.BaseViewHolder;
import com.t00ls.vo.InfoDetail;


/**
 * Created by 123 on 2018/3/25.
 */

public class PageLoadAdapter extends BaseAdapter<InfoDetail> {

    private static final int mPageSize = 10;
    private boolean hasMoreData = true;
    private OnLoadListener mOnLoadListener;

    public PageLoadAdapter(OnLoadListener onLoadListener) {
        mOnLoadListener = onLoadListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.item_base_no_more) {
            return new NoMoreItemViewHolder(view);
        } else if (viewType == R.layout.item_base_progress_bar) {
            return new LoadingItemViewHolder(view);
        }else {
            return new BaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingItemViewHolder) {
            requestData(mPagePosition,mPageSize);
        } else if (holder instanceof NoMoreItemViewHolder) {

        }else {
            holder.itemView.setOnClickListener(v -> mOnItemClickListener.OnClick(position));
            ((BaseViewHolder)holder).bind(getDataSet().get(position));
        }
    }

    private void requestData(int pagePosition, int pageSize) {
        if (mOnLoadListener != null) {
            mOnLoadListener.load(pagePosition, pageSize);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            if (hasMoreData) {
                return R.layout.item_base_progress_bar;
            }else {
                return R.layout.item_base_no_more;
            }
        }else {
            return R.layout.item_base;
        }
    }

    @Override
    public int getItemCount() {
        return getDataSet().size() + 1;
    }

    public void setHasMoreData(boolean isHavingMoreData){
        hasMoreData = isHavingMoreData;
    }

    static class LoadingItemViewHolder extends RecyclerView.ViewHolder{
        public LoadingItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class NoMoreItemViewHolder extends RecyclerView.ViewHolder{
        public NoMoreItemViewHolder(View itemView) {
            super(itemView);
        }
    }



    public interface OnLoadListener{
        void load(int pagePosition, int pageSize);
    }

}
