package com.qiezitv.view.notify;

import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 透明渐显动画
 */
public class FadeIn extends BaseEffects{

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration)
        );
    }
}
