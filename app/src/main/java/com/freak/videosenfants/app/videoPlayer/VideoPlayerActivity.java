package com.freak.videosenfants.app.videoPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.core.BaseActivity;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Formatter;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class VideoPlayerActivity extends BaseActivity implements VideoPlayerContract.View {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_URLS = "urls";
    public static final String EXTRA_POSITION = "position";

    @Inject
    VideoPlayerContract.Presenter mPresenter;

    @BindView(R.id.player)
    PlayerView mPlayerView;

    @BindView(R.id.exo_progress)
    DefaultTimeBar mTimeBar;

    @BindView(R.id.exo_duration)
    TextView mDuration;

    private SimpleExoPlayer mPlayer;
    private final StringBuilder mFormatBuilder;
    private final Formatter mFormatter;

    public VideoPlayerActivity() {
        super();
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        AndroidInjection.inject(this);
        ButterKnife.bind(this);

        mPresenter.subscribe(this);

        if (savedInstanceState != null) {
            mPresenter.onRestore(savedInstanceState);
        }

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        mPlayer = ExoPlayerFactory.newSimpleInstance(this);
        mPlayerView.setPlayer(mPlayer);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));

        String url = getIntent().getStringExtra(EXTRA_URL);
        String[] playlist = getIntent().getStringArrayExtra(EXTRA_URLS);
        int startPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);

        if (url != null) {
            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(url));
            // Prepare the player with the source.
            mPlayer.prepare(videoSource);
        } else if (playlist != null && playlist.length > 0) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(playlist[0]));
            ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(true, videoSource);
            for (int i = 1 ; i < playlist.length ; i++) {
                MediaSource otherSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(playlist[i]));
                concatenatedSource.addMediaSource(otherSource);
            }
            mPlayer.prepare(concatenatedSource);
        }

        if (playlist != null) {
            mPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        mPlayer.seekTo(startPosition, 0);
                        mPlayer.removeListener(this);
                    }
                }
            });
        }

        mPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mPresenter.unsubscribe(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSave(outState);
    }

    @OnClick(R.id.back_button)
    @Override
    public void onBackPressed() {
        mPlayer.stop();
        super.onBackPressed();
    }
}
