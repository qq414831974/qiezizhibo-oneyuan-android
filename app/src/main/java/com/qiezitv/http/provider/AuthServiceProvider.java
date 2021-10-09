package com.qiezitv.http.provider;

import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.dto.AdminUserLoginRequest;
import com.qiezitv.dto.AdminUserRefreshTokenRequest;
import com.qiezitv.model.auth.AccessToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthServiceProvider {

    /**
     * 登录,采用oauth2授权标准
     *
     * @param request
     * @return
     */
    @POST("/service-admin/auth")
    Call<ResponseEntity<AccessToken>> login(@Body AdminUserLoginRequest request);

    /**
     * 刷新token
     *
     * @param request
     * @return
     */
    @POST("/service-admin/auth/refresh_token")
    Call<ResponseEntity<AccessToken>> refreshToken(@Body AdminUserRefreshTokenRequest request);

}
