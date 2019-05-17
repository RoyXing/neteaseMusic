package com.xingzy.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * @author roy.xing
 * @date 2019-05-16
 */
public class UIUtils {

    private static UIUtils instance;

    //设计稿尺寸
    private static final float STANDARD_WIDTH = 1080f;
    private static final float STANDARD_HEIGHT = 1920f;

    private static float displayMetricsWidth;
    private static float displayMetricsHeight;

    private int statusBarHeight;
    private Context context;

    public static UIUtils getInstance(Context context) {
        if (instance == null) {
            instance = new UIUtils(context);
        }
        return instance;
    }

    public static UIUtils getInstance() {
        if (instance == null) {
            throw new RuntimeException("UiUtil应该先调用含有构造方法进行初始化");
        }
        return instance;
    }

    private UIUtils(Context context) {
        this.context = context;
        statusBarHeight = getStatusBarHeight(context);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);

        if (displayMetricsWidth == 0f || displayMetricsHeight == 0f) {
            //横屏
            if (metrics.widthPixels > metrics.heightPixels) {
                displayMetricsWidth = metrics.heightPixels;
                displayMetricsHeight = metrics.widthPixels;
            } else {
                displayMetricsWidth = metrics.widthPixels;
                displayMetricsHeight = metrics.heightPixels - statusBarHeight;
            }
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context) {
        int ids = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (ids > 0) {
            return context.getResources().getDimensionPixelSize(ids);
        }
        return 0;
    }

    public float getHorizontalScale() {
        return displayMetricsWidth / STANDARD_WIDTH;
    }

    public float getVerticalScale() {
        return displayMetricsHeight / (STANDARD_HEIGHT - statusBarHeight);
    }

    public int getRealWidth(int width) {
        return Math.round((float) width * getHorizontalScale());
    }

    public int getRealHeight(int height) {
        return Math.round((float) height * getVerticalScale());
    }

    /**
     * 设置沉浸式
     */
    public void immersive() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = ((Activity) context).getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.TRANSPARENT);
            int visibility = window.getDecorView().getSystemUiVisibility();

            //布局内容全屏展示
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            //隐藏虚拟导航栏
//            visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            //放置内容区域发生变化
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(visibility);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public int getStatusBarHeight() {
        return statusBarHeight;
    }

    public void setStatusBar(View view, int color) {
        immersive();
        ViewGroup decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        int count = decorView.getChildCount();
        if (count > 0 && decorView.getChildAt(count - 1) instanceof StatusBarView) {
            if (color > 0) {
                decorView.getChildAt(count - 1).setBackgroundColor(context.getResources().getColor(color));
            } else {
                decorView.getChildAt(count - 1).setBackgroundColor(Color.argb(0, 0, 0, 0));
            }
            return;
        }

        StatusBarView statusBarView = new StatusBarView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarView.setLayoutParams(params);
        if (color > 0) {
            statusBarView.setBackgroundColor(context.getResources().getColor(color));
        } else {
            statusBarView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        decorView.addView(statusBarView);

        if (view != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMargins(0, statusBarHeight, 0, 0);
        }
    }
}
