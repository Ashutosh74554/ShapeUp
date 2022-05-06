package com.example.shapeup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class benchprVIDEO extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchpr_video);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        VideoView wk1 = findViewById(R.id.wk1);
        wk1.setVideoPath("android.resource://" + getPackageName()+ "/" +R.raw.chestwk1);
        MediaController mediaC = new MediaController(this);
        wk1.setMediaController(mediaC);
        mediaC.setAnchorView(wk1);
        wk1.start();

    }
}