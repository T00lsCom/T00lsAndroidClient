package com.t00ls.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.t00ls.api.UpdateResponse;
import com.t00ls.vo.InfoDetail;
import com.t00ls.vo.MemberInfo;

import java.util.List;

/**
 * Created by 123 on 2018/3/26.
 */

public class InfoViewModel extends ViewModel{
    private MutableLiveData<List<InfoDetail>> infoDetails;

    private MutableLiveData<MemberInfo> memberInfo;

    private MutableLiveData<UpdateResponse> updateResp;

    public MutableLiveData<List<InfoDetail>> getInfoDetails() {
        if (infoDetails == null) {
            infoDetails = new MutableLiveData<>();
            return infoDetails;
        }
        return infoDetails;
    }

    public MutableLiveData<MemberInfo> getMemberInfo() {
        if (memberInfo == null) {
            memberInfo = new MutableLiveData<>();
            return memberInfo;
        }
        return memberInfo;
    }

    public MutableLiveData<UpdateResponse> getUpdateResp() {
        if (updateResp == null) {
            updateResp = new MutableLiveData<>();
            return updateResp;
        }
        return updateResp;
    }
}
