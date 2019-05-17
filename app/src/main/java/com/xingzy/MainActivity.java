package com.xingzy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xingzy.bean.MusicData;
import com.xingzy.ui.UIUtils;
import com.xingzy.widget.BackgroundAnimationRelativeLayout;
import com.xingzy.widget.DiscView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @author roy.xing
 * @date 2019-05-17
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, DiscView.DiscViewPlayListener {

    private BackgroundAnimationRelativeLayout rootLayout;
    private DiscView discView;
    private ImageView last, playOrPause, next;
    private Toolbar toolbar;
    private TextView currentTime, totalTime;
    private SeekBar seekBar;

    private List<MusicData> mMusicDatas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.getInstance(this);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolBar);
        rootLayout = findViewById(R.id.rootLayout);
        discView = findViewById(R.id.discview);
        last = findViewById(R.id.ivLast);
        playOrPause = findViewById(R.id.ivPlayOrPause);
        next = findViewById(R.id.ivNext);
        currentTime = findViewById(R.id.tvCurrentTime);
        totalTime = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.musicSeekBar);

        last.setOnClickListener(this);
        playOrPause.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    private void initEvent() {
        setSupportActionBar(toolbar);
        UIUtils.getInstance().setStatusBar(toolbar, 0);
        MusicData musicData1 = new MusicData(R.raw.music1, R.raw.ic_music1, "寻", "三亩地");
        MusicData musicData2 = new MusicData(R.raw.music2, R.raw.ic_music2, "Nightingale", "YANI");
        MusicData musicData3 = new MusicData(R.raw.music3, R.raw.ic_music3, "Cornfield Chase", "Hans Zimmer");

        mMusicDatas.add(musicData1);
        mMusicDatas.add(musicData2);
        mMusicDatas.add(musicData3);

        discView.setMusicData(mMusicDatas);
        discView.setDiscViewPlayListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == playOrPause) {
            discView.playOrPause();
        } else if (v == last) {
            discView.last();
        } else if (v == next) {
            discView.next();
        }
    }

    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {
        getSupportActionBar().setTitle(musicName);
        getSupportActionBar().setSubtitle(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(int musicPicRes) {
        Glide.with(this).
                load(musicPicRes)
                .transform(new BlurTransformation(200, 5))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        rootLayout.setMusicForeground(resource);
                    }
                });
    }

    @Override
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus) {

    }
}
