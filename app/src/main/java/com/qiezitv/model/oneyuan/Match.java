package com.qiezitv.model.oneyuan;

import com.qiezitv.pojo.MatchAgainst;
import com.qiezitv.pojo.PeopleExpand;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Match implements Serializable {
    private Long id;
    private String name;
    private Long leagueId;
    private String activityId;
    //直播开启
    private Boolean available;
    private Map<Integer, MatchAgainst> againsts;
    private Date startTime;
    //总共几小节
    private Integer section;
    private Integer minutePerSection;
    private String place;
    private String playPath;
    private String poster;
    private List<Integer> type;
    private String round;
    private String subgroup;
    private String remark;
    private PeopleExpand expand;
    private Integer areaType;
    private Integer wechatType;
}
