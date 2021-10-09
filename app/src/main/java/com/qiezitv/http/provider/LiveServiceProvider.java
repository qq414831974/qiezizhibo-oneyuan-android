package com.qiezitv.http.provider;

import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.model.activity.ActivityVO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LiveServiceProvider {

    /**
     * 获取直播信息
     *
     * @param activityId
     * @return
     */
    @GET("/service-admin/activity/{id}")
    Call<ResponseEntity<ActivityVO>> getActivityVo(@Path("id") String activityId);

    /**
     * 获取直播信息
     *
     * @param activityId
     * @return
     */
    @GET("/service-admin/activity/{id}/quality")
    Call<ResponseEntity<Integer>> quality(@Path("id") String activityId);

}
