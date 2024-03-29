package com.laifeng.sopcastsdk.stream.sender.sendqueue;

import com.laifeng.sopcastsdk.entity.Frame;
import com.laifeng.sopcastsdk.stream.sender.rtmp.packets.Chunk;

/**
 * @Title: ISendQueue
 * @Package com.laifeng.sopcastsdk.stream.sender.sendqueue
 * @Description:
 * @Author Jim
 * @Date 2016/11/21
 * @Time 上午10:15
 * @Version
 */

public interface ISendQueue {
    void start();
    void stop();
    void setBufferSize(int size);
    void putFrame(Frame<Chunk> frame);
    Frame<Chunk> takeFrame();
    void setSendQueueListener(SendQueueListener listener);
    SendQueueListener getSendQueueListener();
}
