package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class MedicineRem extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    TextView time;
    EditText med_name;
    Button set, cancel;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String name, uid;
    private NotificatonHelper mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_rem);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        time=findViewById(R.id.med_time);
        med_name=findViewById(R.id.med_name);
        set=findViewById(R.id.set_name);
        cancel=findViewById(R.id.cancel1);

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        uid=auth.getCurrentUser().getUid();

        firestore.collection("Reminders").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    med_name.setText(task.getResult().getString("Medicine Name"));
                    String t=task.getResult().getString("Medicine Time");
                    if(t.equalsIgnoreCase(""))
                        time.setText("0:0");
                    else
                        time.setText(t);
                }
                else{
                    time.setText("0:0");
                }
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(med_name.getText().toString().isEmpty()) {
                    med_name.setError("Cannot be empty");
                    return;
                }
                name=med_name.getText().toString();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker=new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent=new Intent(MedicineRem.this, AlertReciever.class);
                PendingIntent pendingIntent= PendingIntent.getBroadcast(MedicineRem.this, 11, intent, 0);

                alarmManager.cancel(pendingIntent);
                Toast.makeText(MedicineRem.this, "Reminder cancelled", Toast.LENGTH_SHORT).show();

                time.setText("0:0");
                med_name.setText("");

                HashMap<String, Object> rem=new HashMap<>();
                rem.put("Medicine Name","");
                rem.put("Medicine Time", "");
                firestore.collection("Reminders").document(uid).set(rem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MedicineRem.this, "Added to database!!!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MedicineRem.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        Calendar c= Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);

        time.setText(hourOfDay+":"+minute);
        HashMap<String, Object> rem=new HashMap<>();
        rem.put("Medicine Name",name);
        rem.put("Medicine Time", ""+hourOfDay+":"+minute);
        firestore.collection("Reminders").document(uid).set(rem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MedicineRem.this, "Added to database!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MedicineRem.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        startAlarm(c);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c){
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this, AlertReciever.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this, 11, intent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,pendingIntent);
    }
}