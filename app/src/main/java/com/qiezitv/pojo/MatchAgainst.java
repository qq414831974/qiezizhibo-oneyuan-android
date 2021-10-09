package com.qiezitv.pojo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchAgainst implements Serializable {
    private Long hostTeamId;
    private Long guestTeamId;
}
