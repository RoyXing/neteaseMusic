package com.xingzy.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.xingzy.R;

/**
 * @author roy.xing
 * @date 2019-05-17
 */
public class BackgroundAnimationRelativeLayout extends RelativeLayout {

    private final int DURATION_ANIMATION = 500;
    private LayerDrawable layerDrawable;
    private final int INDEX_BACKGROUND = 0;
    private final int INDEX_FOREGROUND = 1;

    private int musicPicRes = -1;

    private ObjectAnimator objectAnimator;

    public BackgroundAnimationRelativeLayout(Context context) {
        this(context, null);
    }

    public BackgroundAnimationRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackgroundAnimationRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initLayerDrawable();
        initObjectAnimator();
    }

    private void initLayerDrawable() {
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getContext().getDrawable(R.drawable.ic_blackground);
        } else {
            drawable = getContext().getResources().getDrawable(R.drawable.ic_blackground);
        }
        Drawable[] drawables = new Drawable[2];
        drawables[INDEX_BACKGROUND] = drawable;
        drawables[INDEX_FOREGROUND] = drawable;
        layerDrawable = new LayerDrawable(drawables);
    }

    private void initObjectAnimator() {
        objectAnimator = ObjectAnimator.ofFloat(this, "number", 0, 1);
        objectAnimator.setDuration(DURATION_ANIMATION);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) ((float) animation.getAnimatedValue() * 255);
                layerDrawable.getDrawable(INDEX_FOREGROUND).setAlpha(value);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    setBackground(layerDrawable);
                } else {
                    setBackgroundDrawable(layerDrawable);
                }
            }
        });

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layerDrawable.setDrawable(INDEX_BACKGROUND, layerDrawable.getDrawable(INDEX_FOREGROUND));
            }
        });
    }

    public void setMusicForeground(Drawable drawable) {
        layerDrawable.setDrawable(INDEX_FOREGROUND, drawable);
        objectAnimator.start();
    }

    public boolean isNeed2UpdateBackground(int musicPicRes) {
        if (musicPicRes == -1) {
            return false;
        }
        if (musicPicRes != this.musicPicRes) {
            return true;
        }
        return false;
    }
}
