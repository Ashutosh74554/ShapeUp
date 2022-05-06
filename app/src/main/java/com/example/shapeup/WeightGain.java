package com.example.shapeup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WeightGain extends AppCompatActivity {

    ImageView chest ,shoulder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_gain);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        chest = findViewById(R.id.chest);
        shoulder = findViewById(R.id.shoulder);

        chest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeightGain.this,chestPAGE.class));
            }
        });

        shoulder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeightGain.this,shoulderPAGE.class));
            }
        });
    }
}