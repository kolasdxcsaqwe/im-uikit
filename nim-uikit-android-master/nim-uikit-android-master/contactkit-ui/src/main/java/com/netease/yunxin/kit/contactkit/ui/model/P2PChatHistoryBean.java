package com.netease.yunxin.kit.contactkit.ui.model;

import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;
import com.netease.yunxin.kit.contactkit.ui.ContactConstant;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;

public class P2PChatHistoryBean extends BaseBean {

    IMMessage imMessage;
    NimUserInfo nimUserInfo;
    Friend friend;

    public P2PChatHistoryBean() {
        this.viewType = ContactConstant.SearchViewType.USER;
        this.router= RouterConstant.PATH_FUN_CHAT_P2P_PAGE;
    }

    public IMMessage getImMessage() {
        return imMessage;
    }

    public void setImMessage(IMMessage imMessage) {
        this.imMessage = imMessage;
    }

    public NimUserInfo getNimUserInfo() {
        return nimUserInfo;
    }

    public void setNimUserInfo(NimUserInfo nimUserInfo) {
        this.nimUserInfo = nimUserInfo;
    }

    public void setType(int viewType)
    {
        this.viewType=viewType;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }
}
