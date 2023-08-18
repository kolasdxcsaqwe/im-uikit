package com.netease.yunxin.kit.chatkit.ui.page.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MsgSearchOption;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.yunxin.kit.chatkit.ui.model.ChatP2PSearchBean;
import com.netease.yunxin.kit.chatkit.ui.model.ChatSearchBean;
import com.netease.yunxin.kit.common.ui.viewmodel.BaseViewModel;
import com.netease.yunxin.kit.common.ui.viewmodel.FetchResult;
import com.netease.yunxin.kit.common.ui.viewmodel.LoadStatus;
import com.netease.yunxin.kit.corekit.im.IMKitClient;

import java.util.ArrayList;
import java.util.List;

public class SearchMessageP2PViewModel extends BaseViewModel {

    private final MutableLiveData<FetchResult<List<ChatP2PSearchBean>>> searchMessageLiveData =
            new MutableLiveData<>();


    public MutableLiveData<FetchResult<List<ChatP2PSearchBean>>> getSearchMessageLiveData() {
        return searchMessageLiveData;
    }


    public void clearData()
    {
        FetchResult<List<ChatP2PSearchBean>> listFetchResult = new FetchResult<>(LoadStatus.Success);
        searchMessageLiveData.postValue(listFetchResult);
    }

    public void searchWords(String sessionId,String text) {
        MsgSearchOption msgSearchOption = new MsgSearchOption();
        msgSearchOption.setSearchContent(text);
        msgSearchOption.setLimit(100);

        NIMClient.getService(MsgService.class).searchMessage(SessionTypeEnum.P2P, sessionId, msgSearchOption)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (result != null && result.size() > 0) {
                            List<ChatP2PSearchBean> list = new ArrayList<>();
                            for (int i = 0; i < result.size(); i++) {
                                NimUserInfo nimUserInfo = NIMClient.getService(UserService.class).getUserInfo(result.get(i).getFromAccount());
                                ChatP2PSearchBean chatP2PSearchBean = new ChatP2PSearchBean();
                                chatP2PSearchBean.setUserInfo(nimUserInfo);
                                chatP2PSearchBean.setImMessage(result.get(i));
                                list.add(chatP2PSearchBean);
                            }
                            FetchResult<List<ChatP2PSearchBean>> listFetchResult = new FetchResult<>(LoadStatus.Success);
                            listFetchResult.setData(list);
                            searchMessageLiveData.postValue(listFetchResult);
                        }
                    }
                });
    }
}
