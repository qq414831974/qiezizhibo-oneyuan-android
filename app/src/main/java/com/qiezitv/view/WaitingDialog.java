package com.qiezitv.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qiezitv.R;


/**
 * 等待框
 */
public class WaitingDialog extends Dialog {

    private TextView waitingShow = null;
    private String content;

    public WaitingDialog(Activity activity, String content) {
        this(activity, R.style.waiting_dialog_style, content);
    }

    public WaitingDialog(Activity activity, int theme, String content) {
        super(activity, theme);
        this.content = content;

        setOwnerActivity(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_waiting);
        initView();
    }

    private void initView() {
        setCanceledOnTouchOutside(false);

        waitingShow = findViewById(R.id.pub_tv_waiting_show);

        if (TextUtils.isEmpty(content)) {
            waitingShow.setVisibility(View.GONE);
        } else {
            setContent(content);
        }
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    /**
     * 设置提醒内容
     */
    public void setContent(String content) {
        if (TextUtils.isEmpty(content)) {
            waitingShow.setVisibility(View.GONE);
        } else {
            waitingShow.setVisibility(View.VISIBLE);
            waitingShow.setText(content);
        }
    }
}
