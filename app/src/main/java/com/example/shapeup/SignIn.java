package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignIn extends AppCompatActivity {
    TextView login_page;
    EditText email, pass, confPass;
    Button sign_up;
    FirebaseAuth auth;
    StorageReference storageReference;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        login_page=findViewById(R.id.login_page);
        email=findViewById(R.id.enter_mail);
        pass=findViewById(R.id.enter_pass);
        confPass=findViewById(R.id.confirm_pass);
        sign_up=findViewById(R.id.sign_up);

        auth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail=email.getText().toString();
                String password=pass.getText().toString();
                String confirm=confPass.getText().toString();
                if(!(mail.isEmpty() && password.isEmpty() && confirm.isEmpty())){
                    if(password.equals(confirm)){
                        auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    if(auth.getCurrentUser()!=null) {
                                        auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignIn.this, "Verification mail sent", Toast.LENGTH_SHORT).show();
                                                Toast.makeText(SignIn.this, "Registered successfully!!!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignIn.this, Login.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                else{
                                    Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        confPass.setError("Passwords do not match");
                    }
                }
                else{
                    Toast.makeText(SignIn.this, "Enter the credentials correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignIn.this, Login.class);
                startActivity(intent);
            }
        });
    }
}