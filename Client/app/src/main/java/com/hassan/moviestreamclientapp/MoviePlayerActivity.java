package com.hassan.moviestreamclientapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hassan.moviestreamclientapp.Service.FloatingWidgetService;


public class MoviePlayerActivity extends AppCompatActivity {

    Uri videoUri;
    PlayerView playerView;
    ExoPlayer exoPlayer ;
    ExtractorsFactory extractorsFactory;
    ImageView exo_floating_widget ;
    ImageView rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideActionBar();

        playerView = findViewById(R.id.playerView);
        exo_floating_widget = findViewById(R.id.exo_floating_widget);
        rotate = findViewById(R.id.rotate);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });



        Intent intent = getIntent();
        if(intent != null ){
            String uri_str = intent.getStringExtra("videoUri");
            videoUri = Uri.parse(uri_str);
        }


        exo_floating_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
               Intent serviceintent = new Intent(MoviePlayerActivity.this,
                       FloatingWidgetService.class);
                serviceintent.putExtra("videoUri", videoUri.toString());
                startService(serviceintent);
            }
        });

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(
                new AdaptiveTrackSelection.Factory(bandwidthMeter)
        );

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);
        extractorsFactory = new DefaultExtractorsFactory();
        playVideo();



    }

    private void hideActionBar() {

        getSupportActionBar().hide();
    }


    private void playVideo(){

        try {
            String playerInfo = Util.getUserAgent(this,"MovieAppClient");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,playerInfo);
            MediaSource mediaSource = new ExtractorMediaSource(videoUri,dataSourceFactory,extractorsFactory,null,null);

            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.release();
    }
}
