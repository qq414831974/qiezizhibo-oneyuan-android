package com.qiezitv.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * [SharedPreferences工具类]
 */
public class SharedPreferencesUtil {

    private static SharedPreferencesUtil mInstance;

    public static void setInstance(SharedPreferencesUtil sharedPreferencesUtil) {
        mInstance = sharedPreferencesUtil;
    }

    public static SharedPreferencesUtil getInstance() {
        if (mInstance == null) {
            throw new NullPointerException();
        }
        return mInstance;
    }

    private SharedPreferences mSharedPreferences;

    private SharedPreferencesUtil(SharedPreferences sp) {
        mSharedPreferences = sp;
    }

    public SharedPreferencesUtil(Context context, String fileName) {
        this(context.getSharedPreferences(fileName, Context.MODE_PRIVATE));
    }

    private Editor getEditor() {
        return mSharedPreferences.edit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public boolean putBoolean(String key, boolean value) {
        Editor editor = getEditor();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public boolean putInt(String key, int value) {
        Editor editor = getEditor();
        editor.putInt(key, value);
        return editor.commit();
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public boolean putLong(String key, long value) {
        Editor editor = getEditor();
        editor.putLong(key, value);
        return editor.commit();
    }

    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    public boolean putFloat(String key, float value) {
        Editor editor = getEditor();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public boolean putString(String key, String value) {
        Editor editor = getEditor();
        editor.putString(key, value);
        return editor.commit();
    }

    public void remove(String key) {
        getEditor().remove(key);
    }

}
