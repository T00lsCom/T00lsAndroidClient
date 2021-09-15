package com.t00ls.ui.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 123 on 2018/3/24.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter {

    protected int mPagePosition = 1;

    protected List<T> dataSet = new ArrayList<>();

    protected OnItemClickListener mOnItemClickListener;

    public void updateData(List dataSet){
        this.dataSet.clear();
        appendData(dataSet);
    }

    public void appendData(List dataSet) {
        if (dataSet != null && !dataSet.isEmpty()) {
            this.dataSet.addAll(dataSet);
            mPagePosition++;
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if (dataSet != null && !dataSet.isEmpty()) {
            this.dataSet.clear();
            mPagePosition = 1;
            notifyDataSetChanged();
        }
    }

    public List<T> getDataSet() {
        return dataSet;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener{
        void OnClick(int position);

        void OnLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
