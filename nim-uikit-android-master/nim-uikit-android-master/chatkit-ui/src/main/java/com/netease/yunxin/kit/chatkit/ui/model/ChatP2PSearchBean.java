package com.netease.yunxin.kit.chatkit.ui.model;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;

public class ChatP2PSearchBean extends BaseBean {

    IMMessage imMessage;
    NimUserInfo userInfo;

    public IMMessage getImMessage() {
        return imMessage;
    }

    public void setImMessage(IMMessage imMessage) {
        this.imMessage = imMessage;
    }

    public NimUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(NimUserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
