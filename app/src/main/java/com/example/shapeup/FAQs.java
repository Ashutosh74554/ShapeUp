package com.example.shapeup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.LayoutTransition;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FAQs extends AppCompatActivity {
    LinearLayout layout_expand1, layout_expand2, layout_expand3, layout_expand4, layout_expand5, layout_expand6;
    TextView details1, details2, details3, details4, details5, details6;
    CardView card1, card2, card3, card4, card5, card6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        details1=findViewById(R.id.details_1);
        details2=findViewById(R.id.details_2);
        details3=findViewById(R.id.details_3);
        details4=findViewById(R.id.details_4);
        details5=findViewById(R.id.details_5);
        details6=findViewById(R.id.details_6);
        layout_expand1=findViewById(R.id.layout_expand1);
        layout_expand2=findViewById(R.id.layout_expand2);
        layout_expand3=findViewById(R.id.layout_expand3);
        layout_expand4=findViewById(R.id.layout_expand4);
        layout_expand5=findViewById(R.id.layout_expand5);
        layout_expand6=findViewById(R.id.layout_expand6);
        card1=findViewById(R.id.card1);
        card2=findViewById(R.id.card2);
        card3=findViewById(R.id.card3);
        card4=findViewById(R.id.card4);
        card5=findViewById(R.id.card5);
        card6=findViewById(R.id.card6);

        layout_expand1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout_expand2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout_expand3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout_expand4.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout_expand5.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout_expand6.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v=(details1.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

                TransitionManager.beginDelayedTransition(layout_expand1, new AutoTransition());
                details1.setVisibility(v);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v=(details2.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

                TransitionManager.beginDelayedTransition(layout_expand2, new AutoTransition());
                details2.setVisibility(v);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v=(details3.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

                TransitionManager.beginDelayedTransition(layout_expand3, new AutoTransition());
                details3.setVisibility(v);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v=(details4.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

                TransitionManager.beginDelayedTransition(layout_expand4, new AutoTransition());
                details4.setVisibility(v);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v=(details5.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

                TransitionManager.beginDelayedTransition(layout_expand5, new AutoTransition());
                details5.setVisibility(v);
            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v=(details6.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

                TransitionManager.beginDelayedTransition(layout_expand6, new AutoTransition());
                details6.setVisibility(v);
            }
        });
    }
}