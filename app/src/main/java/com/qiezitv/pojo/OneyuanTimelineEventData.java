package com.qiezitv.pojo;

import com.qiezitv.R;

import java.util.HashMap;
import java.util.Map;

import static com.qiezitv.pojo.OneyuanEvent.START;
import static com.qiezitv.pojo.OneyuanEvent.NEXT_SETION;
import static com.qiezitv.pojo.OneyuanEvent.PRE_SETION;
import static com.qiezitv.pojo.OneyuanEvent.SWITCH_AGAINST;
import static com.qiezitv.pojo.OneyuanEvent.FINISH;
import static com.qiezitv.pojo.OneyuanEvent.GOAL_ONE;
import static com.qiezitv.pojo.OneyuanEvent.GOAL_TWO;
import static com.qiezitv.pojo.OneyuanEvent.GOAL_THREE;
import static com.qiezitv.pojo.OneyuanEvent.GOAL_ONE_REVERSE;
import static com.qiezitv.pojo.OneyuanEvent.GOAL_TWO_REVERSE;
import static com.qiezitv.pojo.OneyuanEvent.GOAL_THREE_REVERSE;

public class OneyuanTimelineEventData {
    private int backgroundResourceId;
    private String text;

    public OneyuanTimelineEventData(int backgroundResourceId, String text) {
        this.backgroundResourceId = backgroundResourceId;
        this.text = text;
    }

    public int getBackgroundResourceId() {
        return backgroundResourceId;
    }

    public void setBackgroundResourceId(int backgroundResourceId) {
        this.backgroundResourceId = backgroundResourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Map<Integer, OneyuanTimelineEventData> statusEventDataMap = new HashMap<Integer, OneyuanTimelineEventData>() {
        {
            put(START, new OneyuanTimelineEventData(R.drawable.ic_result_ball, "比赛开始"));
            put(NEXT_SETION, new OneyuanTimelineEventData(R.drawable.ic_result_calendar, "下一节"));
            put(PRE_SETION, new OneyuanTimelineEventData(R.drawable.ic_result_calendar, "上一节"));
            put(SWITCH_AGAINST, new OneyuanTimelineEventData(R.drawable.ic_result_vest, "切换对阵"));
            put(FINISH, new OneyuanTimelineEventData(R.drawable.ic_result_flag, "比赛结束"));
        }
    };
    public static Map<Integer, OneyuanTimelineEventData> timelineEventDataMap = new HashMap<Integer, OneyuanTimelineEventData>() {
        {
            put(GOAL_ONE, new OneyuanTimelineEventData(R.drawable.ic_result_start, "一分球"));
            put(GOAL_TWO, new OneyuanTimelineEventData(R.drawable.ic_result_calendar, "二分球"));
            put(GOAL_THREE, new OneyuanTimelineEventData(R.drawable.ic_result_calendar, "三分球"));
            put(GOAL_ONE_REVERSE, new OneyuanTimelineEventData(R.drawable.ic_result_start, "一分球撤销"));
            put(GOAL_TWO_REVERSE, new OneyuanTimelineEventData(R.drawable.ic_result_calendar, "二分球撤销"));
            put(GOAL_THREE_REVERSE, new OneyuanTimelineEventData(R.drawable.ic_result_calendar, "三分球撤销"));
        }
    };
}