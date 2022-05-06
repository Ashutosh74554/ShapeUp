package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Bmi extends AppCompatActivity {
    EditText weight_bmi, height_bmi;
    Button check;
    TextView result;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    double prevbmi, prevfat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        weight_bmi=findViewById(R.id.weight_bmi);
        height_bmi=findViewById(R.id.height_bmi);
        check=findViewById(R.id.check_bmi);
        result=findViewById(R.id.result_bmi);

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        firestore.collection("Health").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    try {
                        prevbmi = task.getResult().getDouble("BMI");
                        prevfat = task.getResult().getDouble("Fat");
                    }catch (Exception e){
                        prevfat=0;
                        prevbmi=0;
                    }
                }
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weight_bmi.getText().toString().isEmpty()){
                    weight_bmi.setError("Required field");
                    return;
                }
                if(height_bmi.getText().toString().isEmpty()){
                    height_bmi.setError("Required field");
                    return;
                }
                double weight=Double.parseDouble(weight_bmi.getText().toString());
                double height=Double.parseDouble(height_bmi.getText().toString());
                height= height/100;
                double res= weight/(height*height);
                if(res<18.5)
                    result.setText("Your BMI is: "+res+" \nYou are underweight");
                else if(res>=18.5 && res<=24.9)
                    result.setText("Your BMI is: "+res+" \nYou have a healthy weight");
                else if(res>24.9 && res<=29.9)
                    result.setText("Your BMI is: "+res+" \nYou are overweight");
                else
                    result.setText("Your BMI is: "+res+" \nYou are obese");

                if(prevbmi>0){
                    if(prevbmi>res){
                        double change= prevbmi-res;
                        Toast.makeText(Bmi.this, "BMI decreased by: "+change, Toast.LENGTH_LONG).show();
                    }
                    else if(prevbmi<res){
                        double change=res-prevbmi;
                        Toast.makeText(Bmi.this, "BMI increased by: "+change, Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(Bmi.this, "BMI is constant", Toast.LENGTH_SHORT).show();
                }

                HashMap<String, Object> health=new HashMap<>();
                health.put("BMI", res);
                health.put("Fat", prevfat);
                firestore.collection("Health").document(auth.getCurrentUser().getUid()).set(health).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(Bmi.this, "Could not update your BMI :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Bmi.this, "Uploaded to database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}