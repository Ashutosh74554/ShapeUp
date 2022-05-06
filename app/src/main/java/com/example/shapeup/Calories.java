package com.example.shapeup;

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

public class Calories extends AppCompatActivity {
    EditText weight_cal, height_cal, age_cal;
    RadioGroup radGrp_1, work;
    Button check;
    TextView result;
    String gender,worked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        weight_cal=findViewById(R.id.weight_cal);
        height_cal=findViewById(R.id.height_cal);
        age_cal=findViewById(R.id.age_cal);
        radGrp_1=findViewById(R.id.radGrp_1);
        work=findViewById(R.id.work);
        check=findViewById(R.id.check_cal);
        result=findViewById(R.id.result_cal);

        radGrp_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rad=(RadioButton) findViewById(i);
                gender=rad.getText().toString();
            }
        });

        work.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton=(RadioButton) findViewById(i);
                worked=radioButton.getText().toString();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double weight=Double.parseDouble(weight_cal.getText().toString());
                double height=Double.parseDouble(height_cal.getText().toString());
                int age=Integer.parseInt(age_cal.getText().toString());

                if(gender.isEmpty() || weight_cal.getText().toString().isEmpty() || height_cal.getText().toString().isEmpty() || age_cal.getText().toString().isEmpty()){
                    Toast.makeText(Calories.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    double res=0, bmr=0;
                    if(gender.equalsIgnoreCase("Male")){
                        bmr=(10*weight)+(6.25*height)-(5*age)+5;
                    }
                    else{
                        bmr=(10*weight)+(6.25*height)-(5*age)-161;
                    }
                    if(worked.equalsIgnoreCase("Little or no exercise"))
                        res=bmr*1.2;
                    else if(worked.equalsIgnoreCase("Exercise 1-3 times/week"))
                        res=bmr*1.375;
                    else if(worked.equalsIgnoreCase("Exercise 5-6 times/week"))
                        res=bmr*1.55;
                    else if(worked.equalsIgnoreCase("Intense exercise 5-6 times/week"))
                        res=bmr*1.725;
                    else
                        Toast.makeText(Calories.this, "Select activity level", Toast.LENGTH_SHORT).show();

                    result.setText("Your maintenence calories: "+res);
                }
            }
        });
    }
}