package com.t00ls.ui.fragment.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.t00ls.ui.fragment.CommonChildFragment;

/**
 * Created by 123 on 2018/3/24.
 */

public class RecyclerViewFactory {

    public static Fragment getRecyclerView(String classification) {
        Bundle bundle = new Bundle();
        bundle.putString("classification",classification);
        return CommonChildFragment.newInstance(bundle);
    }
}
