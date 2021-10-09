package com.qiezitv.model.activity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Activity implements Serializable {
    private String id;
    private String name;
    private Date createTime;
    private Date startTime;
    private Date endTime;
    private Integer wechatType;
    //拉流id
    private String ingestStreamId;
    //拉流url
    private String ingestStreamUrl;
    //推流地址
    private String pushStreamUrl;
    //播放地址
    private ActivityPullUrls pullStreamUrls;

    @Data
    public static final class ActivityPullUrls {
        private String rtmp;
        private String flv;
        private String hls;
        private String udp;
    }
}
