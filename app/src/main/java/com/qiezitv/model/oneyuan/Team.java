package com.qiezitv.model.oneyuan;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team implements Serializable {
    private Long id;
    private String name;
    private String englishName;
    private String shortName;
    private String headImg;
    private String remark;
    private String province;
    private String city;
    private Integer wechatType;
}
