package com.netease.yunxin.kit.chatkit.ui.page.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.yunxin.kit.chatkit.ui.model.ChatP2PSearchBean;
import com.netease.yunxin.kit.chatkit.ui.model.ChatSearchBean;
import com.netease.yunxin.kit.chatkit.ui.view.message.viewholder.SearchMessageEmptyViewHolder;
import com.netease.yunxin.kit.common.ui.viewholder.BaseViewHolder;
import com.netease.yunxin.kit.common.ui.viewholder.IViewHolderFactory;
import com.netease.yunxin.kit.common.ui.viewholder.ViewHolderClickListener;

import java.util.ArrayList;
import java.util.List;

public class SearchMessageP2PAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final String TAG = "SearchMessageAdapter";
    private IViewHolderFactory viewHolderFactory;
    private final List<ChatP2PSearchBean> dataList = new ArrayList<>();
    private ViewHolderClickListener clickListener;

    public void setData(List<ChatP2PSearchBean> data) {
        dataList.clear();
        if (data != null) {
            dataList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void addForwardData(List<ChatP2PSearchBean> data) {
        if (data != null) {
            dataList.addAll(0, data);
        }
    }

    public void appendData(List<ChatP2PSearchBean> data) {
        if (data != null) {
            dataList.addAll(data);
        }
    }

    public void removeData(ChatP2PSearchBean data) {
        if (data == null) {
            return;
        }
        int index = -1;
        for (int j = 0; j < dataList.size(); j++) {
            if (data.equals(dataList.get(j))) {
                index = j;
                break;
            }
        }
        if (index > -1) {
            removeData(index);
        }
    }

    public void removeData(int position) {
        if (position >= 0 && position < dataList.size()) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setViewHolderFactory(IViewHolderFactory factory) {
        this.viewHolderFactory = factory;
    }

    public void setViewHolderClickListener(ViewHolderClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = null;
        if (viewHolderFactory != null) {
            baseViewHolder = viewHolderFactory.createViewHolder(parent, viewType);
        }
        if (baseViewHolder == null) {
            TextView emptyTv = new TextView(parent.getContext());
            baseViewHolder = new SearchMessageEmptyViewHolder(emptyTv);
        }
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBindData(dataList.get(position), position);
        holder.setItemOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public ChatP2PSearchBean getData(int index) {
        if (index >= 0 && index < dataList.size()) {
            return dataList.get(index);
        }
        return null;
    }
}
