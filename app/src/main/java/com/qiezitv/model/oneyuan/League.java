package com.qiezitv.model.oneyuan;

import com.qiezitv.pojo.LeagueGroup;
import com.qiezitv.pojo.LeagueRegulations;
import com.qiezitv.pojo.LeagueRound;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class League implements Serializable {
    private Long id;
    private Long parentId;
    private Boolean isParent;
    private String name;
    private String shortName;
    private String englishName;
    private String majorSponsor;
    private String sponsor;
    private List<String> place;
    private Integer type;
    private Integer ruleType;
    private LeagueRound round;
    private LeagueGroup subgroup;
    private LeagueRegulations regulations;
    private String poster;
    private Date dateBegin;
    private Date dateEnd;
    private Long phoneNumber;
    private String wechat;
    private String headImg;
    private String description;
    private Integer sortIndex;
    private String country;
    private String province;
    private String city;
    private Date createTime;
    private Integer areaType;
    private Integer wechatType;
}
