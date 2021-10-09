package com.qiezitv.view.notify;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiezitv.R;


/**
 * 通知类窗体
 */
public class NotifyDialogBuilder extends Dialog implements DialogInterface {

    /* 默认颜色 */
    private static final String DEF_TEXT_COLOR = "#FFFFFFFF";
    private static final String DEF_DIVIDER_COLOR = "#FF167EFC";
    private static final String DEF_MSG_COLOR = "#FF000000";
    private static final String DEF_DIALOG_COLOR = "#FFFFFFFF";

    private LinearLayout parentPanel;
    private RelativeLayout rootView;
    private LinearLayout contentPanel;
    private LinearLayout topPanel;
    private FrameLayout customPanel;
    private View dialogView;
    private View divider;
    private TextView title;
    private TextView message;
    private ImageView titleIcon;
    private Button negativeBtn;
    private Button positiveBtn;
    private ImageView msgImage;

    private int mDuration = -1;

    public NotifyDialogBuilder(Context context) {
        this(context, R.style.notify_dialog_style);
    }

    public NotifyDialogBuilder(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    private void init(Context context) {
        dialogView = View.inflate(context, R.layout.dialog_notify_layout, null);

        parentPanel = (LinearLayout) dialogView.findViewById(R.id.ll_parent_panel);
        rootView = (RelativeLayout) dialogView.findViewById(R.id.rl_dialog_view);
        topPanel = (LinearLayout) dialogView.findViewById(R.id.ll_top_panel);
        contentPanel = (LinearLayout) dialogView.findViewById(R.id.ll_content_panel);
        customPanel = (FrameLayout) dialogView.findViewById(R.id.fl_custom_panel);

        title = (TextView) dialogView.findViewById(R.id.iv_left);
        message = (TextView) dialogView.findViewById(R.id.tv_message);
        titleIcon = (ImageView) dialogView.findViewById(R.id.iv_title_icon);
        divider = dialogView.findViewById(R.id.v_title_divider);
        negativeBtn = (Button) dialogView.findViewById(R.id.btn_negative);
        positiveBtn = (Button) dialogView.findViewById(R.id.btn_positive);

        msgImage = (ImageView) dialogView.findViewById(R.id.msg_image);

        message.setMaxHeight(context.getResources().getDisplayMetrics().heightPixels / 2);
        setContentView(dialogView);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                parentPanel.setVisibility(View.VISIBLE);
                start(new Slit());
            }
        });
    }

    /**
     * 设置默认样式
     */
    public void toDefault() {
        title.setTextColor(Color.parseColor(DEF_TEXT_COLOR));
        divider.setBackgroundColor(Color.parseColor(DEF_DIVIDER_COLOR));
        message.setTextColor(Color.parseColor(DEF_MSG_COLOR));
        parentPanel.setBackgroundColor(Color.parseColor(DEF_DIALOG_COLOR));
    }

    /**
     * 设置分割线颜色
     */
    public NotifyDialogBuilder withDividerColor(String colorString) {
        divider.setBackgroundColor(Color.parseColor(colorString));

        return this;
    }

    /**
     * 设置Title
     */
    public NotifyDialogBuilder withTitle(CharSequence titleMsg) {
        toggleView(topPanel, title);
        title.setText(titleMsg);

        return this;
    }

    public NotifyDialogBuilder withTitle(int resId) {
        toggleView(topPanel, title);
        title.setText(resId);

        return this;
    }

    /**
     * 设置Title颜色
     */
    public NotifyDialogBuilder withTitleColor(String colorString) {
        title.setTextColor(Color.parseColor(colorString));

        return this;
    }

    /**
     * 设置标题图标
     */
    public NotifyDialogBuilder withTitleIcon(int drawableResId) {
        titleIcon.setImageResource(drawableResId);

        return this;
    }

    /**
     * 设置标题图标
     */
    public NotifyDialogBuilder withTitleIcon(Drawable icon) {
        titleIcon.setImageDrawable(icon);

        return this;
    }

    /**
     * 设置消息内容
     */
    public NotifyDialogBuilder withMessage(int textResId) {
        toggleView(contentPanel, textResId);
        message.setText(textResId);

        return this;
    }

    /**
     * 设置消息内容
     */
    public NotifyDialogBuilder withMessage(CharSequence msg) {
        toggleView(contentPanel, msg);
        message.setText(msg);

        return this;
    }

    public NotifyDialogBuilder withMsgImage(int resId) {
        toggleView(msgImage, resId);
        msgImage.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置消息字体颜色
     */
    public NotifyDialogBuilder withMessageColor(String colorString) {
        message.setTextColor(Color.parseColor(colorString));

        return this;
    }

    /**
     * 设置动画时间
     */
    public NotifyDialogBuilder withDuration(int duration) {
        this.mDuration = duration;

        return this;
    }

    /**
     * 设置左边按钮文字
     */
    public NotifyDialogBuilder withNegativeBtnText(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            negativeBtn.setVisibility(View.VISIBLE);
            negativeBtn.setText(text);
        }

        return this;
    }

    /**
     * 设置右边按钮文字
     */
    public NotifyDialogBuilder withPositiveBtnText(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            positiveBtn.setVisibility(View.VISIBLE);
            positiveBtn.setText(text);
        }

        return this;
    }

    /**
     * 设置左边按钮点击事件
     */
    public NotifyDialogBuilder setNegativeBtnClick(View.OnClickListener click) {
        negativeBtn.setVisibility(View.VISIBLE);
        negativeBtn.setOnClickListener(click);

        return this;
    }

    /**
     * 设置右边按钮点击事件
     */
    public NotifyDialogBuilder setPositiveClick(View.OnClickListener click) {
        positiveBtn.setVisibility(View.VISIBLE);
        positiveBtn.setOnClickListener(click);

        return this;
    }

    /**
     * 设置自定义布局
     */
    public NotifyDialogBuilder setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        setCustomView(customView, context);

        return this;
    }

    /**
     * 设置自定义布局
     */
    public NotifyDialogBuilder setCustomView(View view, Context context) {
        if (customPanel.getChildCount() > 0) {
            customPanel.removeAllViews();
        }
        contentPanel.setVisibility(View.GONE);
        customPanel.addView(view);

        return this;
    }

    /**
     * 设置点击外部是否可取消
     */
    public NotifyDialogBuilder isCancelableOnTouchOutside(boolean cancelable) {
        this.setCanceledOnTouchOutside(cancelable);

        return this;
    }

    /**
     * 设置是否可被返回按钮取消
     */
    public NotifyDialogBuilder isCancelable(boolean cancelable) {
        this.setCancelable(cancelable);

        return this;
    }

    private void toggleView(View view, Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void show() {
        if (TextUtils.isEmpty(title.getText())) {
            dialogView.findViewById(R.id.ll_top_panel).setVisibility(View.GONE);
        }
        super.show();
    }

    /**
     * 执行动画
     */
    private void start(BaseEffects animator) {
        if (mDuration != -1) {
            animator.setDuration(Math.abs(mDuration));
        }
        animator.start(rootView);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        negativeBtn.setVisibility(View.GONE);
        positiveBtn.setVisibility(View.GONE);
    }
}
