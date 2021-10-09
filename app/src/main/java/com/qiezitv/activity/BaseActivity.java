package com.qiezitv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.qiezitv.R;
import com.qiezitv.common.FinishActivityManager;


/**
 * 基础Activity，包含基础标题栏和全局变量的初始化等。
 * <p/>
 */
public class BaseActivity extends FragmentActivity {

    protected FragmentActivity mAct;
    protected FragmentManager mFManager;

    //标题栏
//    protected BaseTitleBar mTitleBar = null;
    //全局变量
//    protected GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        globalData = GlobalData.getInstance();
        mAct = this;
        mFManager = mAct.getSupportFragmentManager();
        FinishActivityManager.getManager().checkWeakReference();
        FinishActivityManager.getManager().addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(layoutResID, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.setContentView(R.layout.view_null);
        System.gc();
    }

    /**
     * 显示titleBar
     *
     * @param layoutResID
     * @param title       null 时不显示titleBar
     */
    public void setContentView(int layoutResID, String title) {
        if (title != null) {
//            setTheme(R.style.top);
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//            mTitleBar = new BaseTitleBar();
        }

        super.setContentView(layoutResID);

       /* if (mTitleBar != null) {//mTitleBar.init()必须在setContentView()之后使用
            mTitleBar.init(this, title);
        }*/
    }

    /**
     * 显示Toast
     *
     * @param msg 内容
     */
    public void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * startActivity
     *
     * @param clazz
     */
    protected void readyGo(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivity then finish
     *
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

//    /**
//     * 判断是否已登入系统
//     *
//     * @return true-已登入 / false-未登录
//     */
//    public boolean isLogin() {
//        return !TextUtils.isEmpty(globalData.getUserUuid());
//    }
}
