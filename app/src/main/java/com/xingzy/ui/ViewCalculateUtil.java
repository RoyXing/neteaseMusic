package com.xingzy.ui;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author roy.xing
 * @date 2019-05-16
 */
public class ViewCalculateUtil {

    public static void setViewRelativeLayoutParam(View view, int width, int height) {
        setViewRelativeLayoutParam(view, width, height, 0, 0, 0, 0);
    }

    public static void setViewRelativeLayoutParam(View view, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (params == null) {
            return;
        }

        params.leftMargin = UIUtils.getInstance().getRealWidth(leftMargin);
        params.topMargin = UIUtils.getInstance().getRealHeight(topMargin);
        params.rightMargin = UIUtils.getInstance().getRealWidth(rightMargin);
        params.bottomMargin = UIUtils.getInstance().getRealHeight(bottomMargin);
        view.setLayoutParams(params);
    }

    public static void setViewRelativeLayoutParam(View view, int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (params == null) {
            return;
        }

        if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.width = UIUtils.getInstance().getRealWidth(width);
        } else {
            params.width = width;
        }

        if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.height = UIUtils.getInstance().getRealHeight(height);
        } else {
            params.height = height;
        }

        params.leftMargin = UIUtils.getInstance().getRealWidth(leftMargin);
        params.topMargin = UIUtils.getInstance().getRealHeight(topMargin);
        params.rightMargin = UIUtils.getInstance().getRealWidth(rightMargin);
        params.bottomMargin = UIUtils.getInstance().getRealHeight(bottomMargin);
        view.setLayoutParams(params);
    }

    public static void setViewFrameLayoutParam(View view, int width, int height) {
        setViewFrameLayoutParam(view, width, height, 0, 0, 0, 0);
    }

    public static void setViewFrameLayoutParam(View view, int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (params == null) {
            return;
        }

        if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.width = UIUtils.getInstance().getRealWidth(width);
        } else {
            params.width = width;
        }

        if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.height = UIUtils.getInstance().getRealHeight(height);
        } else {
            params.height = height;
        }

        params.leftMargin = UIUtils.getInstance().getRealWidth(leftMargin);
        params.topMargin = UIUtils.getInstance().getRealHeight(topMargin);
        params.rightMargin = UIUtils.getInstance().getRealWidth(rightMargin);
        params.bottomMargin = UIUtils.getInstance().getRealHeight(bottomMargin);
        view.setLayoutParams(params);
    }

    public static void setViewLinearLayoutParam(View view, int width, int height) {
        setViewLinearLayoutParam(view, width, height, 0, 0, 0, 0);
    }

    public static void setViewLinearLayoutParam(View view, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (params == null) {
            return;
        }
        params.leftMargin = UIUtils.getInstance().getRealWidth(leftMargin);
        params.topMargin = UIUtils.getInstance().getRealHeight(topMargin);
        params.rightMargin = UIUtils.getInstance().getRealWidth(rightMargin);
        params.bottomMargin = UIUtils.getInstance().getRealHeight(bottomMargin);
        view.setLayoutParams(params);
    }

    public static void setViewLinearLayoutParam(View view, int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (params == null) {
            return;
        }

        if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.width = UIUtils.getInstance().getRealWidth(width);
        } else {
            params.width = width;
        }

        if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.height = UIUtils.getInstance().getRealHeight(height);
        } else {
            params.height = height;
        }

        params.leftMargin = UIUtils.getInstance().getRealWidth(leftMargin);
        params.topMargin = UIUtils.getInstance().getRealHeight(topMargin);
        params.rightMargin = UIUtils.getInstance().getRealWidth(rightMargin);
        params.bottomMargin = UIUtils.getInstance().getRealHeight(bottomMargin);
        view.setLayoutParams(params);
    }

    public static void setViewGroupParam(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            return;
        }

        if (width != RelativeLayout.LayoutParams.MATCH_PARENT && width != RelativeLayout.LayoutParams.WRAP_CONTENT && width != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.width = UIUtils.getInstance().getRealWidth(width);
        } else {
            params.width = width;
        }

        if (height != RelativeLayout.LayoutParams.MATCH_PARENT && height != RelativeLayout.LayoutParams.WRAP_CONTENT && height != RelativeLayout.LayoutParams.FILL_PARENT) {
            params.height = UIUtils.getInstance().getRealHeight(height);
        } else {
            params.height = height;
        }
        view.setLayoutParams(params);
    }

    public static void setViewPadding(View view, int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        view.setPadding(UIUtils.getInstance().getRealWidth(leftPadding),
                UIUtils.getInstance().getRealHeight(topPadding),
                UIUtils.getInstance().getRealWidth(rightPadding),
                UIUtils.getInstance().getRealHeight(bottomPadding));
    }

    public static void setTextSize(TextView textView, int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.getInstance().getRealHeight(size));
    }
}
