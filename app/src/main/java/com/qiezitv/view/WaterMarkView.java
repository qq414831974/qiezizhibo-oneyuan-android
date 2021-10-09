package com.qiezitv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class WaterMarkView extends RelativeLayout {
    private BaseScoreBoardView scoreBoard;
    private ImageView logo;

    public WaterMarkView(Context context) {
        super(context);
    }

    public WaterMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WaterMarkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        if (view instanceof BaseScoreBoardView) {
            this.scoreBoard = null;
        }
    }

    public BaseScoreBoardView getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(BaseScoreBoardView scoreBoard) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(scoreBoard instanceof ScoreBoardOneyuan){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        scoreBoard.setVisibility(View.VISIBLE);
        this.addView(scoreBoard, layoutParams);
        this.scoreBoard = scoreBoard;
    }

    public ImageView getLogo() {
        return logo;
    }

    public void setLogo(ImageView logo) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        logo.setVisibility(View.VISIBLE);
        this.addView(logo, layoutParams);
        this.logo = logo;
    }

    public void showScoreBoard() {
        if (scoreBoard == null) {
            throw new RuntimeException("未设置ScoreBoard");
        }
        scoreBoard.setVisibility(VISIBLE);
    }

    public void hideScoreBoard() {
        if (scoreBoard == null) {
            throw new RuntimeException("未设置ScoreBoard");
        }
        scoreBoard.setVisibility(INVISIBLE);
    }

    public void showLogo() {
        if (logo == null) {
            throw new RuntimeException("未设置logo");
        }
        logo.setVisibility(VISIBLE);
    }

    public void hideLogo() {
        if (logo == null) {
            throw new RuntimeException("未设置logo");
        }
        logo.setVisibility(INVISIBLE);
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        //开启view缓存bitmap
        setDrawingCacheEnabled(true);
        //设置view缓存Bitmap质量
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        //获取缓存的bitmap
        Bitmap cache = getDrawingCache();
        if (cache != null && !cache.isRecycled()) {
            bitmap = Bitmap.createBitmap(cache);
        }
        //销毁view缓存bitmap
        destroyDrawingCache();
        //关闭view缓存bitmap
        setDrawingCacheEnabled(false);
        return bitmap;
    }
}
