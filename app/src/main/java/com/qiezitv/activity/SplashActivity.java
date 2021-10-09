package com.qiezitv.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.qiezitv.R;
import com.qiezitv.common.Constants;
import com.qiezitv.common.SharedPreferencesUtil;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.dto.AdminUserRefreshTokenRequest;
import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.http.provider.AuthServiceProvider;
import com.qiezitv.model.auth.AccessToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    public static final int RC_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限被用户同意,做相应的事情
                gotoNextActivity();
            } else {
                //权限被用户拒绝，显示提示
                showToast("请先给App对应权限");
                finish();
            }
        }
    }

    private void init() {
        requestPermission();
    }

    /**
     * 获取权限
     */
    private void requestPermission() {
        //1. 检查是否已经有该权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //2. 权限没有开启，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_CAMERA);
        } else {
            gotoNextActivity();
        }
    }

    private void gotoNextActivity() {
        SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance();
        boolean isGotoLogin = true;
        if (sp.getBoolean(Constants.SP_IS_AUTO_LOGIN, false)) {
            Long beginTime = System.currentTimeMillis();
            String tokenStr = sp.getString(Constants.SP_ACCESS_TOKEN, null);
            if (tokenStr != null) {
                isGotoLogin = false;
                try {
                    AccessToken accessToken = new Gson().fromJson(tokenStr, AccessToken.class);
                    if ((accessToken.getCreateTime() + (accessToken.getExpiresIn() - 60 * 60) * 1000) > System.currentTimeMillis()) {
                        Constants.ACCESS_TOKEN = RetrofitManager.formatAuthorizationHeader(accessToken.getAccessToken());
                        new Handler().postDelayed(() -> readyGoThenKill(MainActivity.class), Constants.SPLASH_STOP_TIME);
                    } else {
                        // 尝试刷新token
                        AuthServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(AuthServiceProvider.class);
                        Call<ResponseEntity<AccessToken>> response = request.refreshToken(new AdminUserRefreshTokenRequest(accessToken.getRefreshToken()));
                        response.enqueue(new Callback<ResponseEntity<AccessToken>>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseEntity<AccessToken>> call, @NonNull Response<ResponseEntity<AccessToken>> response) {
                                Long endTime = System.currentTimeMillis();
                                long leftWaitTime = 3000 - (endTime - beginTime);
                                Gson gson = new Gson();
                                boolean isGotoLogin = true;
                                if (response.code() == 200) {
                                    ResponseEntity<AccessToken> result = response.body();
                                    if (result == null) {
                                        Log.d(TAG, "请求失败:response==null");
                                    } else {
                                        Log.d(TAG, gson.toJson(result));
                                        if (result.getCode().equals("200")) {
                                            isGotoLogin = false;
                                            AccessToken accessToken = result.getData();
                                            accessToken.setCreateTime(new Date().getTime());
                                            sp.putString(Constants.SP_ACCESS_TOKEN, gson.toJson(accessToken));
                                            Constants.ACCESS_TOKEN = RetrofitManager.formatAuthorizationHeader(accessToken.getAccessToken());
                                        }
                                    }
                                } else {
                                    showToast("请求失败:" + response.toString());
                                }
                                if (isGotoLogin) {
                                    new Handler().postDelayed(() -> readyGoThenKill(LoginActivity.class), leftWaitTime);
                                } else {
                                    new Handler().postDelayed(() -> readyGoThenKill(MainActivity.class), leftWaitTime);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseEntity<AccessToken>> call, @NonNull Throwable t) {
                                Log.e(TAG, t.getMessage());
                                Long endTime = System.currentTimeMillis();
                                long leftWaitTime = 3000 - (endTime - beginTime);
                                new Handler().postDelayed(() -> readyGoThenKill(LoginActivity.class), leftWaitTime);
                            }
                        });
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        if (isGotoLogin) {
            new Handler().postDelayed(() -> readyGoThenKill(LoginActivity.class), Constants.SPLASH_STOP_TIME);
        }
    }
}
