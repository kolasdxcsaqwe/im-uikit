package com.netease.yunxin.kit.contactkit.ui.model;

import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;
import com.netease.yunxin.kit.contactkit.ui.ContactConstant;

public class SearchMoreBean extends BaseBean {

    String text;
    int textRes;

    String keyWords;
    int type;


    public SearchMoreBean(String text,int type,String keyWords) {
        this.text = text;
        this.viewType = ContactConstant.SearchViewType.MORE;
        this.type=type;
        this.keyWords=keyWords;
    }

    public SearchMoreBean(int res,int type,String keyWords) {
        this.textRes= res;
        this.viewType = ContactConstant.SearchViewType.MORE;
        this.type=type;
        this.keyWords=keyWords;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextRes() {
        return textRes;
    }

    public void setTextRes(int textRes) {
        this.textRes = textRes;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
