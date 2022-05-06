package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WaterTracker extends AppCompatActivity {
    ProgressBar pb;

    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    TextView num, fixedTo,goaledits;

    EditText goalSetter;
    Button set;
    String current;
    int gg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fAuth = FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();


        pb=findViewById(R.id.progressBar);
        num=findViewById(R.id.status);
        fixedTo=findViewById(R.id.target);
        goalSetter=findViewById(R.id.goalSet);
        set=findViewById(R.id.setgoal);
        goaledits = findViewById(R.id.goaledits);

        if(num.getText().toString().isEmpty()){
            num.setText("0");
            pb.setProgress(0);
        }

        fixedTo.setCursorVisible(false);

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

        fstore.collection("WateRec").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String x=task.getResult().getString("goal");
                        gg=Integer.parseInt(x);
                        goalSetter.setText(x);
                        goaledits.setText(x);
                    }
                }
                else{
                    Toast.makeText(WaterTracker.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



        if(day!=today) {
            FirebaseUser user = fAuth.getCurrentUser();
            DocumentReference dc=fstore.collection("WateRec").document(user.getUid());
            HashMap<String,Object> todays=new HashMap<>();
            todays.put("drinking","0");
            todays.put("goal",""+gg);
            dc.set(todays).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WaterTracker.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            num.setText("0");
            pb.setProgress(0);
            editDate.putInt("today",day);
            editDate.apply();
        }
        else{
            FirebaseUser user = fAuth.getCurrentUser();
            fstore.collection("WateRec").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            current=task.getResult().getString("drinking");
                            String goals=task.getResult().getString("goal");
                            goaledits.setText(goals);
                            int g=Integer.parseInt(goals);
                            pb.setMax(Integer.parseInt(goals));
                            num.setText(current);
                            goalSetter.setText(""+g);
                            pb.setProgress(Integer.parseInt(current));
                        }
                    }
                }
            });


            set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        gg = Integer.parseInt(goalSetter.getText().toString());
                        if(gg<8){
                            Toast.makeText(WaterTracker.this, "You should drink atleast 8 glasses", Toast.LENGTH_LONG).show();
                            goalSetter.setTextColor(getResources().getColor(R.color.red));
                        }
                        else{
                            goalSetter.setTextColor(getResources().getColor(R.color.teal_700));
                        }
                        goaledits.setText(""+gg);
                        pb.setMax(gg);
                        pb.setProgress(Integer.parseInt(current));
                        FirebaseUser currentUser = fAuth.getCurrentUser();
                        DocumentReference df = fstore.collection("WateRec").document(currentUser.getUid());
                        Map<String, Object> goalInfo = new HashMap<>();
                        goalInfo.put("goal", ""+gg);
                        goalInfo.put("drinking",current);
                        df.set(goalInfo);
                    }catch(NumberFormatException e){
                        Toast.makeText(WaterTracker.this, "Goals of glasses should be in number", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    public void add(View view){
        TextView st1 = findViewById(R.id.motivation);
        TextView st2 = findViewById(R.id.extraline);
        int x=Integer.parseInt(goalSetter.getText().toString());

        int w=Integer.parseInt(num.getText().toString());
        switch(w++){
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
            pb.setProgress(pb.getMax());
        }
        else if(w<x && w>12){
            st1.setText("Come on!");
            st2.setText("Just "+(x-w)+" glasses left to reach goal");
            num.setText(""+w);
            pb.setProgress(w);
        }
        else {
            pb.setProgress(w);
            num.setText(""+w);
        }

        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference df = fstore.collection("WateRec").document(user.getUid());
        Map<String ,Object> currentglass = new HashMap<>();
        currentglass.put("drinking",""+w);
        currentglass.put("goal",""+gg);
        df.set(currentglass);


        pb.setProgress(w);
    }
    public void minus(View view){
        int x=Integer.parseInt(goalSetter.getText().toString());
        TextView st1 = findViewById(R.id.motivation);
        TextView st2 = findViewById(R.id.extraline);
        TextView num = findViewById(R.id.status);

        int w=Integer.parseInt(num.getText().toString());;
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
            pb.setProgress(w);
        }
        else if(w == 0){
            st1.setText("");
            st2.setText("");

            num.setText(""+0);
            pb.setProgress(0);
        }
        else if(w>=x){
            num.setText(""+w);
            pb.setProgress(pb.getMax());
        }
        else{
            pb.setProgress(w);

            num.setText(""+w);
        }

        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference df = fstore.collection("WateRec").document(user.getUid());
        Map<String ,Object> currentglass = new HashMap<>();
        currentglass.put("drinking",""+w);
        currentglass.put("goal",""+gg);
        df.set(currentglass);

    }
}