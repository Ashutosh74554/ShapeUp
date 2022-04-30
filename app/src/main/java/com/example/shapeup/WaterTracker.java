package com.example.shapeup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class WaterTracker extends AppCompatActivity {
    ProgressBar pb;

    TextView num, fixedTo, water_report;
    EditText goalSetter;
    Button set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pb=findViewById(R.id.progressBar);
        num=findViewById(R.id.status);
        fixedTo=findViewById(R.id.target);
        goalSetter=findViewById(R.id.goalSet);
        set=findViewById(R.id.setgoal);
        water_report=findViewById(R.id.water_report);

        fixedTo.setCursorVisible(false);



        try{

        }catch(NullPointerException e){
            pb.setMax(12);
            fixedTo.setText("of "+12+" glasses");

            fixedTo.setText("of "+12+" glasses");

        }



        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x=Integer.parseInt(goalSetter.getText().toString());
                if(x<8){
                    Toast.makeText(WaterTracker.this, "You should drink atleast 8 glasses", Toast.LENGTH_LONG).show();
                    goalSetter.setTextColor(getResources().getColor(R.color.red));
                }
                else{
                    goalSetter.setTextColor(getResources().getColor(R.color.teal_700));
                }

                fixedTo.setText("of "+x+" glasses");
                fixedTo.setCursorVisible(false);
                pb.setMax(x);
            }
        });

        Calendar c=Calendar.getInstance();
        int day=c.get(Calendar.DAY_OF_MONTH);

        SharedPreferences date=getSharedPreferences("date",Context.MODE_PRIVATE);
        SharedPreferences.Editor editDate=date.edit();
        int today=date.getInt("today",0);

        water_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: show firebase report of last week
            }
        });
        if(day!=today) {
            // TODO: add report to firebase

            num.setText("0");
            pb.setProgress(0);

            editDate.putInt("today",day);
            editDate.apply();
        }
        else{

        }
    }

    public void add(View view){
        TextView st1 = findViewById(R.id.motivation);
        TextView st2 = findViewById(R.id.extraline);
        int x=Integer.parseInt(goalSetter.getText().toString());
        int w=0;
        w++;

        switch(w){
            case 1:st1.setText("Great Job! ");
                st2.setText("Grab tour next 250ml of water in 60 min");
                num.setText(""+w);
                break;
            case 2:st1.setText("Awesome!");
                st2.setText("Gulp! And it's gone. Get Your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 3:st1.setText("Bottoms Up!");
                st2.setText("Hope to see you in 60 minutes for the next glass");
                num.setText(""+w);
                break;
            case 4:st1.setText("You Rock! ");
                st2.setText("Take your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 5:st1.setText("Awesome! ");
                st2.setText("Grab tour next 250ml of water in 60 min");
                num.setText(""+w);
                break;
            case 6:st1.setText("Keep going!");
                st2.setText("Have your next glass of water after 60 minutes");
                num.setText(""+w);
                break;
            case 7:st1.setText("Bottoms Up! ");
                st2.setText("Grab tour next 250ml of water in 60 min");
                num.setText(""+w);
                break;
            case 8:st1.setText("Water is Good!");
                st2.setText("Take your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 9:st1.setText("Great work!");
                st2.setText("Glup! And it's gone. Get Your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 10:st1.setText("You Rock! ");
                st2.setText("Every glass of water count ");
                num.setText(""+w);
                break;
            case 11:st1.setText("Bottoms Up!");
                st2.setText("Say good bye to your thirst in next 60 minutes");
                num.setText(""+w);
                break;
            case 12:st1.setText("Hurray!!!");
                st2.setText("You drank 12 glasses of water");
                num.setText(""+w);
                break;
        }
        if(w>=x) {
            num.setText("" + w);
            st1.setText("You made it!");
            st2.setText("You reached your goal");
        }
        else if(w<x && w>12){
            st1.setText("Come on!");
            st2.setText("Just "+(x-w)+" glasses left to reach goal");
            num.setText(""+w);
        }
        pb.setProgress(w);
    }
    public void minus(View view){
        int x=Integer.parseInt(goalSetter.getText().toString());
        TextView st1 = findViewById(R.id.motivation);
        TextView st2 = findViewById(R.id.extraline);
        TextView num = findViewById(R.id.status);
        int w=0;
        if(w > 0)
            w--;
        if(w>x){
            num.setText(""+w);
        }
        switch(w){
            case 0:st1.setText("");
                st2.setText("");
                num.setText(""+0);
                pb.setProgress(0);
                break;
            case 1:st1.setText("Great Job! ");
                st2.setText("Grab tour next 250ml of water in 60 min");
                num.setText(""+w);
                break;
            case 2:st1.setText("Awesome!");
                st2.setText("Gulp! And it's gone. Get Your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 3:st1.setText("Bottoms Up!");
                st2.setText("Hope to see you in 60 minutes for the next glass");
                num.setText(""+w);
                break;
            case 4:st1.setText("You Rock! ");
                st2.setText("Take your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 5:st1.setText("Awesome! ");
                st2.setText("Grab tour next 250ml of water in 60 min");
                num.setText(""+w);
                break;
            case 6:st1.setText("Keep going!");
                st2.setText("Have your next glass of water after 60 minutes");
                num.setText(""+w);
                break;
            case 7:st1.setText("Bottoms Up! ");
                st2.setText("Grab tour next 250ml of water in 60 min");
                num.setText(""+w);
                break;
            case 8:st1.setText("Water is Good!");
                st2.setText("Take your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 9:st1.setText("Great work!");
                st2.setText("Gulp! And it's gone. Get Your next glass in 60 minutes");
                num.setText(""+w);
                break;
            case 10:st1.setText("You Rock! ");
                st2.setText("Every glass of water count ");
                num.setText(""+w);
                break;
            case 11:st1.setText("Bottoms Up!");
                st2.setText("Say good bye to your thirst in next 60 minutes");
                num.setText(""+w);
                break;
            case 12:st1.setText("Hurray!!!");
                st2.setText("You drank 12 glasses of water");
                num.setText(""+w);
                break;
        }
        if(w>12 && w<x){
            st1.setText("Come on!");
            st2.setText("Just "+(x-w)+" glasses left to reach goal");
            num.setText(""+w);
        }
        else if(w == 0){
            st1.setText("");
            st2.setText("");
            num.setText(""+0);
            pb.setProgress(0);
        }
        else
            pb.setProgress(w);
    }
}