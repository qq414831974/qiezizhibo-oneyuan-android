package com.qiezitv.http.provider;

import com.qiezitv.dto.http.ResponseEntity;
import com.qiezitv.model.oneyuan.LeagueVO;
import com.qiezitv.model.oneyuan.MatchStatus;
import com.qiezitv.model.oneyuan.MatchVO;
import com.qiezitv.model.page.Page;
import com.qiezitv.model.oneyuan.TimeLine;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface OneyuanServiceProvider {

    /**
     * 获取联赛列表
     *
     * @param map
     * @return
     */
    @GET("/service-admin/oneyuan/league")
    Call<ResponseEntity<Page<LeagueVO>>> getLeagueList(@QueryMap Map<String, Object> map);


    /**
     * 根据联赛id获取联赛信息
     *
     * @param id
     * @return
     */
    @GET("/service-admin/oneyuan/league/{id}")
    Call<ResponseEntity<LeagueVO>> getLeagueById(@Path("id") long id);

    /**
     * 获取比赛列表
     *
     * @param map
     * @return
     */
    @GET("/service-admin/oneyuan/match")
    Call<ResponseEntity<Page<MatchVO>>> getMatchList(@QueryMap Map<String, Object> map);


    /**
     * 根据id获取比赛
     *
     * @param id
     * @return
     */
    @GET("/service-admin/oneyuan/match/{id}")
    Call<ResponseEntity<MatchVO>> getMatchById(@Path("id") long id);


    /**
     * 获取比赛的状态
     *
     * @param matchId
     * @return
     */
    @GET("/service-admin/oneyuan/timeline/status")
    Call<ResponseEntity<MatchStatus>> getMatchStatusDetailById(@Query("matchId") Long matchId);


    /**
     * 修改比赛比分和状态
     *
     * @param match
     * @return
     */
    @PUT("/service-admin/oneyuan/timeline/status")
    Call<ResponseEntity<Boolean>> updateMatchScoreStatus(@Body MatchVO match);

    /**
     * 添加球赛统计信息
     *
     * @param timeLine
     * @return
     */
    @POST("/service-admin/oneyuan/timeline")
    Call<ResponseEntity<Boolean>> addTimeLine(@Body TimeLine timeLine);

    /**
     * 删除球赛统计信息
     *
     * @param id
     * @return
     */
    @DELETE("/service-admin/oneyuan/timeline")
    Call<ResponseEntity<Boolean>> deleteTimeLine(@Query("id") long id);
}
