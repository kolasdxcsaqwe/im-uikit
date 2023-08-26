package com.netease.yunxin.kit.contactkit.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    static final String tag="SPUtils";
    static SPUtils spUtils;

    static SharedPreferences sharedPref =null;

    public static final String loginData="loginData";

    public static final String ConfigData="ConfigData";


    public static SPUtils getInstance(Context context) {
        if(spUtils==null)
        {
            spUtils=new SPUtils();
            init(context);
        }

        return spUtils;
    }

    public static void init(Context context)
    {
        sharedPref = context.getSharedPreferences(
                tag, Context.MODE_PRIVATE);
    }

    public void saveNow(String key,String string)
    {
        if(sharedPref==null)return;
        sharedPref.edit().putString(key,string).commit();
    }

    public void save(String key,String string)
    {
        sharedPref.edit().putString(key,string).apply();
    }

    public void remove(String key)
    {
        sharedPref.edit().remove(key).apply();
    }

    public <T> T get(String tag,T value)
    {
        if(sharedPref==null)return null;

        if(value instanceof String)
        {
            return (T)(sharedPref.getString(tag, String.valueOf(value)));
        }
        if(value instanceof Long)
        {
            return (T) (Long.valueOf(sharedPref.getLong(tag, (Long)value)));
        }
        if(value instanceof Integer)
        {
            return (T)(Integer.valueOf(sharedPref.getInt(tag, (Integer)value)));
        }
        if(value instanceof Boolean)
        {
            return (T)(Boolean.valueOf(sharedPref.getBoolean(tag, (Boolean)value)));
        }
        if(value instanceof Float)
        {
            return (T)(Float.valueOf(sharedPref.getFloat(tag, (Float)value)));
        }

        return null;
    }
}
