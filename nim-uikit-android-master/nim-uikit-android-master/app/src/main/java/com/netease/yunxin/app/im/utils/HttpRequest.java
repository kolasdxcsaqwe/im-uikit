package com.netease.yunxin.app.im.utils;

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

}
