package com.qiezitv.model.auth;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccessToken implements Serializable {

    // "accessToken":"e7f6d65b-1841-4ae0-8982-e2bda647f32e","expires_in":"41332","refreshToken":"926034f7-b6b7-45da-ba65-d7d96e53c9fe"

    private String accessToken;
    @SerializedName("expires_in")
    private Long expiresIn;
    private String refreshToken;
    /** 记录获取token的时间 */
    private Long createTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
