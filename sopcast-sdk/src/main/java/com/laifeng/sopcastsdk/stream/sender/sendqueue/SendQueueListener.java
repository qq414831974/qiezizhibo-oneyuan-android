package com.laifeng.sopcastsdk.stream.sender.sendqueue;

import com.laifeng.sopcastsdk.stream.sender.DebugInfo;

/**
 * @Title: SendQueueListener
 * @Package com.laifeng.sopcastsdk.stream.sender.sendqueue
 * @Description:
 * @Author Jim
 * @Date 2016/11/21
 * @Time 下午3:19
 * @Version
 */

public interface SendQueueListener {
    void good();

    void bad();

    void debug(DebugInfo debugInfo);

    void setDebugInfo(DebugInfo debugInfo);

    DebugInfo getDebugInfo();
}
