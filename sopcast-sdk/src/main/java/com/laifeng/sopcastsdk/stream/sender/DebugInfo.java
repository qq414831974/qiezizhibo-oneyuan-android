package com.laifeng.sopcastsdk.stream.sender;

public class DebugInfo {
    //带宽记录
    private int bandwidth;
    //当前记录的带宽
    private int currentBandwidth;
    //记录带宽的时间
    private long timestamp;

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getCurrentBandwidth() {
        return currentBandwidth;
    }

    public void setCurrentBandwidth(int currentBandwidth) {
        this.currentBandwidth = currentBandwidth;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
