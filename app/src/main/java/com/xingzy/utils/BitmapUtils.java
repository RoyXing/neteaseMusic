package com.xingzy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.xingzy.R;
import com.xingzy.ui.UIUtils;

/**
 * @author roy.xing
 * @date 2019-05-16
 */
public class BitmapUtils {

    public static Drawable getDrawable(Context context, int resId) {
        int outPicSize = UIUtils.getInstance().getRealWidth(813);
        int innPicSize = UIUtils.getInstance().getRealWidth(533);

        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_disc), outPicSize, outPicSize, false);

        Bitmap innerBitmap = getPicBitmap(context, innPicSize, resId);
        BitmapDrawable outerDrawable = new BitmapDrawable(context.getResources(), bitmapDisc);
        RoundedBitmapDrawable roundedInnerBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), innerBitmap);
        outerDrawable.setAntiAlias(true);
        roundedInnerBitmapDrawable.setAntiAlias(true);

        Drawable[] drawables = new Drawable[2];
        drawables[0] = roundedInnerBitmapDrawable;
        drawables[1] = outerDrawable;

        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int picMargin = (outPicSize - innPicSize) / 2;
        layerDrawable.setLayerInset(0, picMargin, picMargin, picMargin, picMargin);
        return layerDrawable;
    }

    public static Bitmap getPicBitmap(Context context, int picSize, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), picSize, options);
        int imageWidth = options.outWidth;
        int sample = imageWidth / picSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        //设置图片采样率
        options.inSampleSize = dstSample;
        //设置图片解码格式
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), resId, options), picSize, picSize, true);
    }
}
