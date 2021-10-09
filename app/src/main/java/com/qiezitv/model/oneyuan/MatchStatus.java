package com.qiezitv.model.oneyuan;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchStatus implements Serializable {
    private Long id;
    private Long matchId;
    //-1未开始，21结束
    private Integer status;
    //当前第几节，从1开始
    private Integer section;
    //当前第几对阵，从1开始
    private Integer againstIndex;
    //对阵分数，key为第几对阵，value为第几对阵分数
    private Map<Integer, String> score;
}
