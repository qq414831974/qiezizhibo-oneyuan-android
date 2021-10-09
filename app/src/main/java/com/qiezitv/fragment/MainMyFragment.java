package com.qiezitv.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qiezitv.R;
import com.qiezitv.activity.LoginActivity;
import com.qiezitv.common.Constants;
import com.qiezitv.common.SharedPreferencesUtil;
import com.qiezitv.view.notify.NotifyDialogBuilder;

public class MainMyFragment extends BaseFragment {

    private NotifyDialogBuilder notifyDialog = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main_my;
    }

    @Override
    protected void onBindFragment(View view) {
        TextView tvUserName = view.findViewById(R.id.tv_user_name);
        Button btnLoginOut = view.findViewById(R.id.btn_login_out);
        btnLoginOut.setOnClickListener(onClickListener);

        tvUserName.setText(SharedPreferencesUtil.getInstance().getString(Constants.SP_LOGIN_USER, ""));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void lazyLoad() {
    }

    private View.OnClickListener onClickListener = v -> {
        if (v.getId() == R.id.btn_login_out) {
            showLoginOutDialog();
        }
    };

    /**
     * 显示退出提示框
     */
    private void showLoginOutDialog() {
        notifyDialog = new NotifyDialogBuilder(getActivity());
//        notifyDialog.setTitleText("提示");
        notifyDialog.withMessage("您确认要退出吗?");
        notifyDialog.setCancelable(true);
        notifyDialog.setNegativeBtnClick(v -> notifyDialog.dismiss());
        notifyDialog.setPositiveClick(v -> {
            SharedPreferencesUtil.getInstance().remove(Constants.SP_ACCESS_TOKEN);
            readyGo(LoginActivity.class);
            getActivity().finish();
        });
        notifyDialog.show();
    }

}
