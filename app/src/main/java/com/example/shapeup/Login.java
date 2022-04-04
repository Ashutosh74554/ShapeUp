package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextView sign_up, forgot;
    EditText mail, password;
    Button login;
    FirebaseAuth auth;
    LayoutInflater inflater;
    AlertDialog.Builder reset_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sign_up=findViewById(R.id.sign_up_page);
        mail=findViewById(R.id.login_mail);
        password=findViewById(R.id.login_password);
        login=findViewById(R.id.login_button);
        forgot=findViewById(R.id.forgot);
        reset_alert=new AlertDialog.Builder(Login.this);

        auth=FirebaseAuth.getInstance();

        inflater=this.getLayoutInflater();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v= inflater.inflate(R.layout.reset_popup, null);
                reset_alert.setTitle("Reset Password")
                        .setMessage("Enter Email id")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText reset_email=v.findViewById(R.id.reset_mail_id);
                                String mail=reset_email.getText().toString();
                                if(mail.isEmpty()){
                                    reset_email.setError("Required Field");
                                    return;
                                }
                                auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Login.this, "Reset link sent!!!", Toast.LENGTH_SHORT).show();
                                    }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                        })
                        .setNegativeButton("Cancel",null)
                        .setView(v)
                        .create().show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mail.getText().toString();
                String pass = password.getText().toString();
                if (email.isEmpty()) {
                    mail.setError("Enter email id");
                    return;
                }
                if (pass.isEmpty()) {
                    password.setError("Enter password");
                    return;
                }
                auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user=auth.getCurrentUser();
                        if(user!=null && user.isEmailVerified()) {
                            Toast.makeText(Login.this, "Logging in...", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(Login.this, "Please verify your E-mail first", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this, SignIn.class);
                startActivity(intent);
            }
        });
    }

}