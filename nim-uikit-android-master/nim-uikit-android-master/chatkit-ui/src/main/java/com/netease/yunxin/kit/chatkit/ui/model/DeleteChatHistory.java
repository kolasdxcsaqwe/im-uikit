package com.netease.yunxin.kit.chatkit.ui.model;

import androidx.annotation.NonNull;

import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;
import com.netease.yunxin.kit.corekit.event.BaseEvent;

public class DeleteChatHistory extends BaseEvent {

    public static final String EVENT_TYPE = "DeleteChatHistory";

    @NonNull
    @Override
    public String getType() {
        return EVENT_TYPE;
    }
}
