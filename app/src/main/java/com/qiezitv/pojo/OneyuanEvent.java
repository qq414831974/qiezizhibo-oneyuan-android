package com.qiezitv.pojo;

import com.qiezitv.R;

public class OneyuanEvent {

    public static final int UNOPEN = -1;//-1:比赛未开始
    public static final int START = 0;//0:比赛开始
    public static final int GOAL_ONE = 1;//1:1分球
    public static final int GOAL_TWO = 2;//2:2分球
    public static final int GOAL_THREE = 3;//3:3分球
    public static final int NEXT_SETION = 4;//4:下一节
    public static final int SWITCH_AGAINST = 5;//5:更换对阵
    public static final int GOAL_ONE_REVERSE = 11;//1:1分球
    public static final int GOAL_TWO_REVERSE = 12;//2:2分球
    public static final int GOAL_THREE_REVERSE = 13;//3:3分球
    public static final int PRE_SETION = 14;//4:下一节撤销
    public static final int FINISH = 21;//21:比赛结束

    public static String translateStatus(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case -1:
                return "未开始";
            case START:
                return "比赛开始";
            case FINISH:
                return "比赛结束";
            default:
                return "未知";
        }
    }

    public static int getEventImageDrawable(int eventType) {
        switch (eventType) {
            case START:
                return R.drawable.ic_result_start;
            case GOAL_ONE:
            case GOAL_ONE_REVERSE:
                return R.drawable.ic_result_ball;
            case GOAL_TWO:
            case GOAL_TWO_REVERSE:
                return R.drawable.ic_result_ball2;
            case GOAL_THREE:
            case GOAL_THREE_REVERSE:
                return R.drawable.ic_result_ball3;
            case NEXT_SETION:
            case PRE_SETION:
                return R.drawable.ic_result_calendar;
            case SWITCH_AGAINST:
                return R.drawable.ic_result_vest;
            case FINISH:
                return R.drawable.ic_result_flag;
        }
        return -1;
    }
}