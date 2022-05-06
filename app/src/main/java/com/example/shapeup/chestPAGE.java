package com.example.shapeup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class chestPAGE extends AppCompatActivity {

    Button benchpr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_page);

        benchpr = findViewById(R.id.benchPrBUT);

        benchpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(chestPAGE.this,benchprVIDEO.class));
            }
        });


    }
}