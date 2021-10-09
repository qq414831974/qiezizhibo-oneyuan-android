package com.qiezitv.pojo;

import com.qiezitv.model.oneyuan.Team;

import java.io.Serializable;

import lombok.Data;

@Data
public class MatchAgainstVO implements Serializable {
    private Team hostTeam;
    private Team guestTeam;
}
