package com.qiezitv.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.qiezitv.R;
import com.qiezitv.common.Constants;
import com.qiezitv.common.SharedPreferencesUtil;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.http.provider.AuthServiceProvider;
import com.qiezitv.dto.AdminUserLoginRequest;
import com.qiezitv.model.auth.AccessToken;
import com.qiezitv.view.WaitingDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText etUser;
    private EditText etPassword;

    private boolean isAutoLogin;
    private boolean isRemember;
    private SharedPreferencesUtil sp;

    private WaitingDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        etUser = findViewById(R.id.et_user);
        etPassword = findViewById(R.id.et_password);
        CheckBox cbAutoLogin = findViewById(R.id.cb_auto_login);
        CheckBox cbRemember = findViewById(R.id.cb_remember);
        Button btnLogin = findViewById(R.id.btn_login);

        sp = SharedPreferencesUtil.getInstance();
        // 填充数据
        etUser.setText(sp.getString(Constants.SP_LOGIN_USER, ""));
        isRemember = sp.getBoolean(Constants.SP_IS_REMEMBER, false);
        if (isRemember) {
            cbRemember.setChecked(true);
            etPassword.setText(sp.getString(Constants.SP_LOGIN_PASSWORD, ""));
        }
        sp.putBoolean(Constants.SP_IS_AUTO_LOGIN, false);
        isAutoLogin = false;

        cbRemember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "cbRemember.onCheckedChanged:" + isChecked);
            sp.putBoolean(Constants.SP_IS_REMEMBER, isChecked);
            isRemember = isChecked;
        });

        cbAutoLogin.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            Log.d(TAG, "cbAutoLogin.onCheckedChanged:" + isChecked);
            sp.putBoolean(Constants.SP_IS_AUTO_LOGIN, isChecked);
            isAutoLogin = isChecked;
        }));

        btnLogin.setOnClickListener(v -> {
            AuthServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(AuthServiceProvider.class);
            AdminUserLoginRequest loginBean = new AdminUserLoginRequest();
            loginBean.setUserName(etUser.getText().toString().trim());
            loginBean.setPassword(etPassword.getText().toString().trim());
            Call<ResponseEntity<AccessToken>> response = request.login(loginBean);
            response.enqueue(new Callback<ResponseEntity<AccessToken>>() {
                @Override
                public void onResponse(Call<ResponseEntity<AccessToken>> call, Response<ResponseEntity<AccessToken>> response) {
                    runOnUiThread(() -> {
                        if (waitingDialog != null && !isDestroyed()) {
                            waitingDialog.dismiss();
                        }
                    });
                    Gson gson = new Gson();
                    if (response.code() == 200) {
                        ResponseEntity<AccessToken> result = response.body();
                        if (result == null) {
                            showToast("请求失败:response==null");
                        } else {
                            Log.d(TAG, gson.toJson(result));
                            if (result.getCode().equals("200")) {
                                sp.putString(Constants.SP_LOGIN_USER, etUser.getText().toString().trim());
                                if (isRemember) {
                                    sp.putString(Constants.SP_LOGIN_PASSWORD, etPassword.getText().toString().trim());
                                }
                                AccessToken accessToken = result.getData();
                                accessToken.setCreateTime(new Date().getTime());
                                sp.putString(Constants.SP_ACCESS_TOKEN, gson.toJson(accessToken));
                                Constants.ACCESS_TOKEN = RetrofitManager.formatAuthorizationHeader(accessToken.getAccessToken());
                                readyGoThenKill(MainActivity.class);
                            } else {
                                showToast("请求失败:" + gson.toJson(result));
                            }
                        }
                    } else {
                        try {
                            showToast("请求失败:" + (response.errorBody() != null ? response.errorBody().string() : response.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseEntity<AccessToken>> call, Throwable t) {
                    runOnUiThread(() -> {
                        if (waitingDialog != null && !isDestroyed()) {
                            waitingDialog.dismiss();
                        }
                    });
                    Log.e(TAG, t.getMessage());
                    showToast("网络请求失败:" + t.getMessage());
                }
            });
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(this, "");
                waitingDialog.setCanceledOnTouchOutside(false);
            }
            waitingDialog.show();
        });
    }


}