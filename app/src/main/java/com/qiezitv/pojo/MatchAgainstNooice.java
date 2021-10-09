package com.qiezitv.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class MatchAgainstNooice implements Serializable {
    private Long hostNooice;
    private Long guestNooice;
}
