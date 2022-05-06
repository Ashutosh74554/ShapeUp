package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Fats extends AppCompatActivity {
    EditText weight_fat, height_fat, age_fat, neck_fat, waist_fat, hip_fat;
    RadioGroup rad;
    Button check;
    TextView result;
    String gender;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    double prevbmi, prevfat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fats);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        weight_fat=findViewById(R.id.weight_fat);
        height_fat=findViewById(R.id.height_fat);
        age_fat=findViewById(R.id.age_fat);
        neck_fat=findViewById(R.id.neck_fat);
        waist_fat=findViewById(R.id.waist_fat);
        hip_fat=findViewById(R.id.hip_fat);
        rad=findViewById(R.id.radGrp);
        check=findViewById(R.id.check_fat);
        result=findViewById(R.id.result_fat);

        hip_fat.setVisibility(View.GONE);

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

        rad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton=(RadioButton) findViewById(i);
                gender=radioButton.getText().toString();
                if(gender.equalsIgnoreCase("Female")){
                    hip_fat.setVisibility(View.VISIBLE);
                }
                else{
                    hip_fat.setVisibility(View.GONE);
                }
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double weight=Double.parseDouble(weight_fat.getText().toString());
                double height=Double.parseDouble(height_fat.getText().toString());
                int age=Integer.parseInt(age_fat.getText().toString());
                double neck=Double.parseDouble(neck_fat.getText().toString());
                double waist=Double.parseDouble(waist_fat.getText().toString());
                double hip=0;
                if(gender.equalsIgnoreCase("female")) {
                    hip = Double.parseDouble(hip_fat.getText().toString());
                }

                if(weight_fat.getText().toString().isEmpty() || height_fat.getText().toString().isEmpty() || age_fat.getText().toString().isEmpty() || neck_fat.getText().toString().isEmpty() || waist_fat.getText().toString().isEmpty() || gender.isEmpty()){
                    Toast.makeText(Fats.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                }
                else if(gender.equalsIgnoreCase("female") && hip_fat.getText().toString().isEmpty()){
                    Toast.makeText(Fats.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    double res=0;
                    if(gender.equalsIgnoreCase("female")){
                        res=((495)/(1.29579-0.35004*Math.log10(waist+hip-neck)+0.22100*Math.log10(height)))-450;
                    }
                    else{
                        res=((495)/(1.0324-0.19077*Math.log10(waist-neck)+(0.15456*Math.log10(height))))-450;
                    }

                    res= Math.round(res*1000)/1000D;

                    if(gender.equalsIgnoreCase("male")) {
                        if(res<2)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Malnutitioned");
                        else if(res>=2 && res<=5)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Essential fat");
                        else if(res>=6 && res<=13)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Athletes");
                        else if(res>=14 && res<=17)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Fitness");
                        else if(res>=18 && res<=24)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Average");
                        else
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Obese");
                    }
                    else{
                        if(res<10)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Malnutitioned");
                        else if(res>=10 && res<=13)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Essential fat");
                        else if(res>=14 && res<=20)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Athletes");
                        else if(res>=21 && res<=24)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Fitness");
                        else if(res>=25 && res<=31)
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Average");
                        else
                            result.setText("Your body fat (US Navy Method) is: " + res + "\n Category: Obese");
                    }

                    if(prevfat>0){
                        if(prevfat>res){
                            double change= Math.round((prevfat-res)*1000)/1000D;
                            Toast.makeText(Fats.this, "Fat % decreased by: "+change, Toast.LENGTH_LONG).show();
                        }
                        else if(prevfat<res){
                            double change=Math.round((res-prevfat)*1000)/1000D;
                            Toast.makeText(Fats.this, "Fat % increased by: "+change, Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(Fats.this, "Fat % is constant", Toast.LENGTH_SHORT).show();
                    }

                    HashMap<String, Object> health=new HashMap<>();
                    health.put("BMI", prevbmi);
                    health.put("Fat", res);
                    firestore.collection("Health").document(auth.getCurrentUser().getUid()).set(health).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Fats.this, "Could not update your Fat % :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Fats.this, "Uploaded to database!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}