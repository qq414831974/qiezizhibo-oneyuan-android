package com.qiezitv.pojo;

import java.io.Serializable;

public class LeagueRegulations implements Serializable {
    private Integer section;
    private Integer minutePerSection;

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public Integer getMinutePerSection() {
        return minutePerSection;
    }

    public void setMinutePerSection(Integer minutePerSection) {
        this.minutePerSection = minutePerSection;
    }
}