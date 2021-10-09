package com.qiezitv.http.provider;

import com.qiezitv.dto.http.ResponseEntity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SysLogServiceProvider {

    /**
     * 上传日志
     *
     * @param body
     * @return
     */
    @POST("/service-admin/sys/log")
    Call<ResponseEntity<Boolean>> addSyslog(@Body Map<String, String> body);

}
