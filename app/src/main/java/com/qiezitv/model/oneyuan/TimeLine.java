package com.qiezitv.model.oneyuan;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeLine implements Serializable {
    private Long id;
    private Long matchId;
    private Long teamId;
    private Long playerId;
    private Integer eventType;
    private Integer againstIndex;
    private Integer section;
    //eventType为进球时，remark为进球个数
    private String remark;
    private String text;
}