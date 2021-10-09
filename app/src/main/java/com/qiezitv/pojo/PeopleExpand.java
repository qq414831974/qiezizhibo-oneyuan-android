package com.qiezitv.pojo;

import java.io.Serializable;

public class PeopleExpand implements Serializable {
    private Long baseMin;
    private Long baseMax;
    private Integer expandMin;
    private Integer expandMax;

    public Long getBaseMin() {
        return baseMin;
    }

    public void setBaseMin(Long baseMin) {
        this.baseMin = baseMin;
    }

    public Long getBaseMax() {
        return baseMax;
    }

    public void setBaseMax(Long baseMax) {
        this.baseMax = baseMax;
    }

    public Integer getExpandMin() {
        return expandMin;
    }

    public void setExpandMin(Integer expandMin) {
        this.expandMin = expandMin;
    }

    public Integer getExpandMax() {
        return expandMax;
    }

    public void setExpandMax(Integer expandMax) {
        this.expandMax = expandMax;
    }
}
