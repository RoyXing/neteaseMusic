package com.xingzy.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xingzy.R;
import com.xingzy.bean.MusicData;
import com.xingzy.ui.UIUtils;
import com.xingzy.ui.ViewCalculateUtil;
import com.xingzy.utils.BitmapUtils;
import com.xingzy.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author roy.xing
 * @date 2019-05-17
 */
public class DiscView extends RelativeLayout {

    private ImageView needleView;
    private ViewPager discContainer;
    private ViewPagerAdapter viewPagerAdapter;
    private ObjectAnimator needleAnimator;

    private List<MusicData> musicDataList = new ArrayList<>();
    private List<View> discLayouts = new ArrayList<>();
    private List<ObjectAnimator> discAnimators = new ArrayList<>();
    public static final int DURATION_NEEDLE_ANIMATOR = 500;
    private DiscViewPlayListener discViewPlayListener;

    /**
     * 标记唱针复位后，是否需要重新偏移到唱片处
     */
    private boolean isNeed2StartPlayAnimator = false;

    /**
     * 标记ViewPager是否处于偏移的状态
     */
    private boolean viewPagerIsOffset = false;

    private NeedleAnimatorStatus needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
    private MusicStatus musicStatus = MusicStatus.STOP;

    /**
     * 指针状态
     */
    private enum NeedleAnimatorStatus {
        /**
         * 移动时:从唱盘往远处移动
         */
        TO_FAR_END,

        /**
         * 移动式:从远处往唱盘移动
         */
        TO_NEAR_END,

        /**
         * 静止时:离开唱盘
         */
        IN_FAR_END,

        /**
         * 静止时:贴近唱盘
         */
        IN_NEAR_END
    }

    /**
     * 音乐状态
     */
    public enum MusicStatus {
        /**
         * 播放
         */
        PLAY,

        /**
         * 暂停
         */
        PAUSE,

        /**
         * 停止
         */
        STOP
    }

    /**
     * DiscView需要触发的音乐切换状态
     */
    public enum MusicChangedStatus {
        /**
         * 播放
         */
        PLAY,
        /**
         * 暂停
         */
        PAUSE,
        /**
         * 下一曲
         */
        NEXT,
        /**
         * 上一曲
         */
        LAST,
        /**
         * 停止
         */
        STOP
    }

    public DiscView(Context context) {
        super(context);
    }

    public DiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMusicData(List<MusicData> list) {
        if (list.isEmpty()) {
            return;
        }
        musicDataList.clear();
        discLayouts.clear();
        discAnimators.clear();
        musicDataList.addAll(list);
        for (MusicData musicData : musicDataList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc, discContainer, false);
            ImageView imageView = view.findViewById(R.id.music_img);
            imageView.setImageDrawable(BitmapUtils.getDrawable(getContext(), musicData.getMusicPicRes()));
            discAnimators.add(getDiscAnimator(imageView));
            discLayouts.add(view);
        }
        viewPagerAdapter.notifyDataSetChanged();

        MusicData musicData = musicDataList.get(0);
        if (discViewPlayListener != null) {
            discViewPlayListener.onMusicInfoChanged(musicData.getMusicName(), musicData.getMusicAuthor());
            discViewPlayListener.onMusicPicChanged(musicData.getMusicPicRes());
        }
    }

    private ObjectAnimator getDiscAnimator(ImageView imageView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        return objectAnimator;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initDiscBackground();
        initViewPager();
        initNeedle();
        initObjectAnimator();
    }

    private void initDiscBackground() {
        ImageView circleImage = findViewById(R.id.musicCircle);
        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_disc_blackground);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                UIUtils.getInstance().getRealWidth((int) Constant.DESIC_SIZE),
                UIUtils.getInstance().getRealWidth((int) Constant.DESIC_SIZE), false);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), scaledBitmap);
        circleImage.setImageDrawable(roundedBitmapDrawable);
        ViewCalculateUtil.setViewRelativeLayoutParam(circleImage, 0, UIUtils.getInstance().getRealHeight((int) Constant.DESIC_MARTION_TOP), 0, 0);
    }

    private void initViewPager() {
        discContainer = findViewById(R.id.viewPager);
        discContainer.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPagerAdapter = new ViewPagerAdapter();
        discContainer.setAdapter(viewPagerAdapter);
        ViewCalculateUtil.setViewRelativeLayoutParam(discContainer, 0, UIUtils.getInstance().getRealHeight((int) Constant.DESIC_MARTION_TOP), 0, 0);

        discContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int lastPositionOffsetPixels = 0;
            int currentItem = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /*从左往右滑动*/
                if (lastPositionOffsetPixels > positionOffsetPixels) {
                    if (positionOffset < 0.5) {
                        notifyMusicInfoChanged(position);
                    } else {
                        notifyMusicInfoChanged(discContainer.getCurrentItem());
                    }
                }
                /*从右往左滑动*/
                else if (lastPositionOffsetPixels < positionOffsetPixels) {
                    if (positionOffset > 0.5) {
                        notifyMusicInfoChanged(position + 1);
                    } else {
                        notifyMusicInfoChanged(position);
                    }
                }
                lastPositionOffsetPixels = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {
                resetOtherDiscAnimation(position);
                notifyMusicPicChanged(position);
                if (position > currentItem) {
                    notifyMusicStatusChanged(MusicChangedStatus.NEXT);
                } else {
                    notifyMusicStatusChanged(MusicChangedStatus.LAST);
                }
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                    case ViewPager.SCROLL_STATE_SETTLING:
                        viewPagerIsOffset = false;
                        if (musicStatus == MusicStatus.PLAY) {
                            playAnimator();
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        viewPagerIsOffset = true;
                        pauseAnimator();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void resetOtherDiscAnimation(int position) {
        for (int i = 0; i < discLayouts.size(); i++) {
            if (position == i) {
                continue;
            }
            discAnimators.get(i).cancel();
            ImageView imageView = discLayouts.get(i).findViewById(R.id.music_img);
            imageView.setRotation(0);
        }
    }

    private void notifyMusicPicChanged(int position) {
        if (discViewPlayListener != null) {
            discViewPlayListener.onMusicPicChanged(musicDataList.get(position).getMusicPicRes());
        }
    }

    private void notifyMusicInfoChanged(int position) {
        if (discViewPlayListener != null) {
            discViewPlayListener.onMusicInfoChanged(musicDataList.get(position).getMusicName(), musicDataList.get(position).getMusicAuthor());
        }
    }

    private void initNeedle() {
        needleView = findViewById(R.id.musicNeedle);
        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_needle);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap, UIUtils.getInstance().getRealWidth((int) Constant.NEEDLE_WIDTH),
                UIUtils.getInstance().getRealHeight((int) Constant.NEEDLE_HEIGHT), false);
        needleView.setImageBitmap(scaledBitmap);
        ViewCalculateUtil.setViewRelativeLayoutParam(needleView, UIUtils.getInstance().getRealHeight((int) Constant.NEEDLE_MARGIN_LEFT),
                -UIUtils.getInstance().getRealHeight((int) Constant.NEEDLE_MARGIN_TOP), 0, 0);
        needleView.setPivotX(UIUtils.getInstance().getRealWidth((int) Constant.NEEDLE_PIVOT_X));
        needleView.setPivotX(UIUtils.getInstance().getRealHeight((int) Constant.NEEDLE_PIVOT_Y));
        needleView.setRotation(Constant.ROTATION_INIT_NEEDLE);
    }

    private void initObjectAnimator() {
        needleAnimator = ObjectAnimator.ofFloat(needleView, View.ROTATION, -30, 0);
        needleAnimator.setDuration(DURATION_NEEDLE_ANIMATOR);
        needleAnimator.setInterpolator(new AccelerateInterpolator());
        needleAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                /**
                 * 根据动画开始前NeedleAnimatorStatus的状态，
                 * 即可得出动画进行时NeedleAnimatorStatus的状态
                 * */
                if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_NEAR_END;
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                /*指针从远处向唱盘移动 然后转动圆盘*/
                if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_NEAR_END;
                    playDiscAnimator(discContainer.getCurrentItem());
                    musicStatus = MusicStatus.PLAY;
                }
                /*圆盘停止转动后 指针从圆盘向远处移动*/
                else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
                    if (musicStatus == MusicStatus.STOP) {
                        isNeed2StartPlayAnimator = true;
                    }
                }

                if (isNeed2StartPlayAnimator) {
                    isNeed2StartPlayAnimator = false;
                    /**
                     * 只有在ViewPager不处于偏移状态时，才开始唱盘旋转动画
                     */
                    if (!viewPagerIsOffset) {
                        DiscView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playAnimator();
                            }
                        }, 50);
                    }
                }
            }
        });
    }

    public void playOrPause() {
        if (musicStatus == MusicStatus.PLAY) {
            pause();
        } else {
            play();
        }
    }

    private void play() {
        playAnimator();
    }

    private void playAnimator() {
        /*唱针处于远端时，直接播放动画*/
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
            needleAnimator.start();
        }
        /*唱针处于往远端移动时，设置标记，等动画结束后再播放动画*/
        else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
            isNeed2StartPlayAnimator = true;
        }
    }

    private void pause() {
        musicStatus = MusicStatus.PAUSE;
        pauseAnimator();
    }

    private void stop() {
        musicStatus = MusicStatus.STOP;
        pauseAnimator();
    }

    private void pauseAnimator() {
        /*播放时暂停动画*/
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
            int index = discContainer.getCurrentItem();
            pauseDiscAnimator(index);
        } else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
            needleAnimator.reverse();
            /**
             * 若动画在没结束时执行reverse方法，则不会执行监听器的onStart方法，此时需要手动设置
             * */
            needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
        }

        /**
         * 动画可能执行多次，只有音乐处于停止 / 暂停状态时，才执行暂停命令
         * */
        if (musicStatus == MusicStatus.STOP) {
            notifyMusicStatusChanged(MusicChangedStatus.STOP);
        } else if (musicStatus == MusicStatus.PAUSE) {
            notifyMusicStatusChanged(MusicChangedStatus.PAUSE);
        }
    }

    /**
     * 播放监听
     *
     * @param status
     */
    private void notifyMusicStatusChanged(MusicChangedStatus status) {
        if (discViewPlayListener != null) {
            discViewPlayListener.onMusicChanged(status);
        }
    }

    /**
     * 播放圆盘动画
     *
     * @param currentItem
     */
    private void playDiscAnimator(int currentItem) {
        ObjectAnimator animator = discAnimators.get(currentItem);
        if (animator.isPaused()) {
            animator.resume();
        } else {
            animator.start();
        }

        /**
         * 唱盘动画可能执行多次，只有不是音乐不在播放状态，在回调执行播放
         * */
        if (musicStatus != MusicStatus.PLAY) {
            notifyMusicStatusChanged(MusicChangedStatus.PLAY);
        }
    }

    /**
     * 停止圆盘动画
     *
     * @param index
     */
    private void pauseDiscAnimator(int index) {
        ObjectAnimator animator = discAnimators.get(index);
        animator.pause();
        needleAnimator.reverse();
    }

    public void last() {
        int currentItem = discContainer.getCurrentItem();
        if (currentItem == 0) {
            Toast.makeText(getContext(), "已经到达第一首", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            discContainer.setCurrentItem(currentItem - 1, true);
        }
    }

    public void next() {
        int currentItem = discContainer.getCurrentItem();
        if (currentItem == musicDataList.size() - 1) {
            Toast.makeText(getContext(), "已经到达最后一首", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            discContainer.setCurrentItem(currentItem + 1, true);
        }
    }

    private void selectMusicWithButton() {
        if (musicStatus == MusicStatus.PLAY) {
            play();
        } else if (musicStatus == MusicStatus.PAUSE) {
            isNeed2StartPlayAnimator = true;
            pauseAnimator();
        }
    }

    public void setDiscViewPlayListener(DiscViewPlayListener discViewPlayListener) {
        this.discViewPlayListener = discViewPlayListener;
    }

    public interface DiscViewPlayListener {

        /**
         * 用于更新标题栏变化
         *
         * @param musicName
         * @param musicAuthor
         */
        void onMusicInfoChanged(String musicName, String musicAuthor);

        /**
         * 用于更新背景图片
         *
         * @param musicPicRes
         */
        void onMusicPicChanged(int musicPicRes);

        /**
         * 用于更新音乐播放状态
         *
         * @param musicChangedStatus
         */
        void onMusicChanged(MusicChangedStatus musicChangedStatus);
    }

    class ViewPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = discLayouts.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(discLayouts.get(position));
        }

        @Override
        public int getCount() {
            return musicDataList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }
}
