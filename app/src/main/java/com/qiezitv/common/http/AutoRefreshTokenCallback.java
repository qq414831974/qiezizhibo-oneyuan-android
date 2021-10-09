package com.qiezitv.common.http;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.qiezitv.common.Constants;
import com.qiezitv.common.SharedPreferencesUtil;
import com.qiezitv.dto.AdminUserRefreshTokenRequest;
import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.http.provider.AuthServiceProvider;
import com.qiezitv.model.auth.AccessToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class AutoRefreshTokenCallback<T> implements Callback<T> {
    private static final String TAG = AutoRefreshTokenCallback.class.getSimpleName();
    private boolean isRefreshTokenExecuted = false;

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        T result = response.body();
        if (response.isSuccessful() && result != null) {
            if (result instanceof ResponseEntity) {
                if (((ResponseEntity<?>) result).getCode().equals("200")) {
                    onSuccess(result);
                } else {
                    onFail(response, null);
                }
            } else {
                onSuccess(result);
            }
        } else if (response.code() == 401 || response.code() == 403) {
            if (isRefreshTokenExecuted) {
                onRefreshTokenFail();
                return;
            }
            isRefreshTokenExecuted = true;
            Log.d(TAG, "AutoRefreshToken");
            // 自动刷新token
            SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance();
            String tokenStr = sp.getString(Constants.SP_ACCESS_TOKEN, null);
            AuthServiceProvider request = RetrofitManager.getInstance().getRetrofit().create(AuthServiceProvider.class);
            Gson gson = new Gson();

            new Thread(() -> {
                try {
                    AccessToken accessToken = gson.fromJson(tokenStr, AccessToken.class);
                    Call<ResponseEntity<AccessToken>> refreshTokenCall = request.refreshToken(new AdminUserRefreshTokenRequest(accessToken.getRefreshToken()));
                    Response<ResponseEntity<AccessToken>> refreshTokenResponse = refreshTokenCall.execute();
                    if (refreshTokenResponse.isSuccessful()
                            && refreshTokenResponse.body() != null
                            && refreshTokenResponse.body().getCode().equals("200")) {
                        accessToken = refreshTokenResponse.body().getData();
                        accessToken.setCreateTime(new Date().getTime());
                        sp.putString(Constants.SP_ACCESS_TOKEN, gson.toJson(accessToken));
                        Constants.ACCESS_TOKEN = RetrofitManager.formatAuthorizationHeader(accessToken.getAccessToken());

                        // 重新进行业务逻辑请求
                        Call<T> retryCall = call.clone();
                        retryCall.enqueue(AutoRefreshTokenCallback.this);
                    } else {
                        Constants.ACCESS_TOKEN = null;
                        sp.remove(Constants.SP_ACCESS_TOKEN);
                        onRefreshTokenFail();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Constants.ACCESS_TOKEN = null;
                    sp.remove(Constants.SP_ACCESS_TOKEN);
                    onRefreshTokenFail();
                }
            }).start();
        } else {
            Gson gson = new Gson();
            Response res = null;
            try {
                res = Response.success(gson.fromJson(response.errorBody().string(), ResponseEntity.class));
            } catch (JsonSyntaxException | IOException e) {
                res = null;
            }
            onFail(res, null);
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        t.printStackTrace();
        onFail(null, t);
    }

    public abstract void onRefreshTokenFail();

    public abstract void onSuccess(T result);

    public abstract void onFail(@Nullable Response<T> response, @Nullable Throwable t);

}
