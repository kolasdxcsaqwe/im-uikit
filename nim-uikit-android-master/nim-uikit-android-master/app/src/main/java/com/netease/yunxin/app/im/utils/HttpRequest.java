package com.netease.yunxin.app.im.utils;

import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpRequest {

    static final String baseUrl="http://im.juhai.xyz/im-qtapi-test/";

    public static final String register="user/register";
    public static final String login="user/login";
    public static final String logout="user/logout";
    public static final String updatePwd="user/updatePwd";

    static OkHttpClient okHttpClient;

    public static void post(String url, RequestBody requestBody, Callback callback)
    {
        if(okHttpClient==null)
        {
            okHttpClient=new OkHttpClient();
        }

        Request request=new Request.Builder().addHeader("content-type","application/json")
                .url(baseUrl+url).post(requestBody).build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void post(String url, List<Head> heads, RequestBody requestBody, Callback callback)
    {
        if(okHttpClient==null)
        {
            okHttpClient=new OkHttpClient();
        }

        if(heads!=null)
        {
            Request.Builder request=new Request.Builder();
            request.addHeader("content-type","application/json");
            for (int i = 0; i < heads.size(); i++) {
                request.addHeader(heads.get(i).getKey(),heads.get(i).getValue());
            }
            request.url(baseUrl+url).post(requestBody);
            okHttpClient.newCall(request.build()).enqueue(callback);
        }

    }

    public static class Head
    {
        String key;String Value;

        public Head(String key, String value) {
            this.key = key;
            Value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            Value = value;
        }
    }
}
