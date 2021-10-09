package com.laifeng.sopcastsdk.entity;

import android.graphics.Bitmap;

/**
 * @Title: Watermark
 * @Package com.laifeng.sopcastsdk.video
 * @Description:
 * @Author Jim
 * @Date 16/9/18
 * @Time 下午2:32
 * @Version
 */
public class Watermark {
    private Bitmap markImg;
    private int width;
    private int height;
    private int orientation;
    private int vMargin;
    private int hMargin;
    //是否全屏水印
    private boolean fullScreen;

    public Watermark(Bitmap img, int width, int height, int orientation, int vmargin, int hmargin) {
        markImg = img;
        this.width = width;
        this.height = height;
        this.orientation = orientation;
        vMargin = vmargin;
        hMargin = hmargin;
    }

    public Watermark(Bitmap img, int orientation, boolean fullScreen) {
        markImg = img;
        this.orientation = orientation;
        this.fullScreen = fullScreen;
        this.width = 0;
        this.height = 0;
        vMargin = 0;
        hMargin = 0;
    }

    public Watermark(Bitmap img, int width, int height, int orientation, int vmargin, int hmargin, boolean fullScreen) {
        markImg = img;
        this.width = width;
        this.height = height;
        this.orientation = orientation;
        this.fullScreen = fullScreen;
        vMargin = vmargin;
        hMargin = hmargin;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getMarkImg() {
        return markImg;
    }

    public void setMarkImg(Bitmap markImg) {
        this.markImg = markImg;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getvMargin() {
        return vMargin;
    }

    public void setvMargin(int vMargin) {
        this.vMargin = vMargin;
    }

    public int gethMargin() {
        return hMargin;
    }

    public void sethMargin(int hMargin) {
        this.hMargin = hMargin;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public Watermark copy() {
        return new Watermark(this.getMarkImg(), this.getWidth(), this.getHeight(), this.getOrientation(), this.getvMargin(), this.gethMargin(), this.isFullScreen());
    }
}
