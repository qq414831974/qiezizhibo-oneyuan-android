package com.qiezitv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiezitv.R;


public abstract class BaseScoreBoardView extends FrameLayout {

    protected Context context;
    protected TextView tvTeamNameHost;
    protected TextView tvTeamNameGuest;
    protected TextView tvScoreHost;
    protected TextView tvScoreGuest;
    protected TextView tvSection;
    protected ImageView ivScoreBoard;
    protected LinearLayout llHostColor;
    protected LinearLayout llGuestColor;
    protected ImageView ivLogoMask;
    private Float teamNameHostBaseFontSize;
    private Float teamNameGuestBaseFontSize;

    public BaseScoreBoardView(Context context, int resource) {
        super(context);
        this.context = context;
        init(context, resource);
    }

    private void init(Context context, int resource) {
        View view = LayoutInflater.from(context).inflate(resource, this, true);
        ivScoreBoard = view.findViewById(R.id.iv_scoreboard);
        tvTeamNameHost = view.findViewById(R.id.tv_team_name_left);
        tvTeamNameGuest = view.findViewById(R.id.tv_team_name_right);
        tvScoreHost = view.findViewById(R.id.tv_score_host);
        tvScoreGuest = view.findViewById(R.id.tv_score_guest);
        tvSection = view.findViewById(R.id.tv_section);
        llHostColor = view.findViewById(R.id.ll_host_color);
        llGuestColor = view.findViewById(R.id.ll_guest_color);
        ivLogoMask = view.findViewById(R.id.iv_scoreboard_logo_mask);
    }

    public void setTeamNameHost(String teamName) {
        if (tvTeamNameHost == null) {
            return;
        }
        if (teamNameHostBaseFontSize == null) {
            teamNameHostBaseFontSize = tvTeamNameHost.getTextSize();
        }
        if (teamName != null && getTextLength(teamName) > 7) {
            float textsize = teamNameHostBaseFontSize - (getTextLength(teamName) - 7.0f) * 2.5f;
            tvTeamNameHost.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        }
        tvTeamNameHost.setText(teamName);
    }

    public void setTeamNameGuest(String teamName) {
        if (tvTeamNameGuest == null) {
            return;
        }
        if (teamNameGuestBaseFontSize == null) {
            teamNameGuestBaseFontSize = tvTeamNameGuest.getTextSize();
        }
        if (teamName != null && getTextLength(teamName) > 7) {
            float textsize = teamNameGuestBaseFontSize - (getTextLength(teamName) - 7.0f) * 2.5f;
            tvTeamNameGuest.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        }
        tvTeamNameGuest.setText(teamName);
    }

    public void setScoreHost(String score) {
        if (tvScoreHost == null) {
            return;
        }
        tvScoreHost.setText(score);
    }

    public void setScoreHost(Integer score) {
        if (tvScoreHost == null) {
            return;
        }
        tvScoreHost.setText(String.valueOf(score));
    }

    public void setScoreGuest(String score) {
        if (tvScoreGuest == null) {
            return;
        }
        tvScoreGuest.setText(score);
    }

    public void setScoreGuest(Integer score) {
        if (tvScoreGuest == null) {
            return;
        }
        tvScoreGuest.setText(String.valueOf(score));
    }

    public void setSection(String section) {
        if (tvSection == null) {
            return;
        }
        tvSection.setText(section);
    }

    public void setSection(Integer section) {
        if (tvSection == null) {
            return;
        }
        tvSection.setText(String.valueOf(section));
    }

    public void setHostColor(int color) {
        if (llHostColor == null) {
            return;
        }
        //亮色
        if (ColorUtils.calculateLuminance(color) >= 0.5) {
            tvTeamNameHost.setTextColor(Color.BLACK);
        } else {
            tvTeamNameHost.setTextColor(Color.WHITE);
        }
        llHostColor.setBackgroundColor(color);
    }

    public void setGuestColor(int color) {
        if (llGuestColor == null) {
            return;
        }
        //暗色
        if (ColorUtils.calculateLuminance(color) >= 0.5) {
            tvTeamNameGuest.setTextColor(Color.BLACK);
        } else {
            tvTeamNameGuest.setTextColor(Color.WHITE);
        }
        llGuestColor.setBackgroundColor(color);
    }

    public void showLogoMask() {
        ivLogoMask.setVisibility(VISIBLE);
    }

    public void hideLogoMask() {
        ivLogoMask.setVisibility(GONE);
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

    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        // Draw view to canvas
        v.draw(c);
        return b;
    }

    private float getTextLength(String str) {
        float length = 0.0f;
        for (char c : str.toCharArray()) {
            if (isChinese(String.valueOf(c))) {
                length = length + 1.0f;
            } else {
                length = length + 0.5f;
            }
        }
        return length;
    }

    private boolean isChinese(String charaString) {
        return charaString.matches("^[\u4E00-\u9FFF]*");
    }
}
