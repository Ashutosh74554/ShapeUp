package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    CircleImageView image;
    EditText profile_name;
    Button save;
    ProgressBar progressBar;
    FirebaseAuth auth;
    Uri imageUri;
    StorageReference storageReference;
    FirebaseFirestore firestore;
    String uid;
    boolean isAdmin=false, isPhotoSelected=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        image=findViewById(R.id.update_pic);
        profile_name=findViewById(R.id.update_name);
        save=findViewById(R.id.save_button);
        progressBar=findViewById(R.id.update_progressbar);

        auth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firestore=FirebaseFirestore.getInstance();
        uid= Objects.requireNonNull(auth.getCurrentUser()).getUid();

        progressBar.setVisibility(View.INVISIBLE);

        firestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getString("Admin")!=null){
                        isAdmin=true;
                    }
                    if(task.getResult().exists()) {
                        profile_name.setText(task.getResult().getString("Name"));
                        String uri_existing = task.getResult().getString("Image");
                        imageUri=Uri.parse(uri_existing);
                        Glide.with(UpdateProfile.this).load(uri_existing).into(image);
                    }
                }
                else{
                    Toast.makeText(UpdateProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name=profile_name.getText().toString();

                StorageReference imagerRef=storageReference.child("Profile Pic").child(uid+".jpg");
                if(isPhotoSelected) {
                    if (!(name.isEmpty()) && imageUri != null) {
                        imagerRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    imagerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (!isAdmin)
                                                saveToFirestore(task, name, uri);
                                            else
                                                saveToFirestore(task, name, uri, "true");
                                        }
                                    });
                                    Toast.makeText(UpdateProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(UpdateProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UpdateProfile.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(imageUri!=null || !name.isEmpty()){
                    if(isAdmin)
                        saveToFirestore(null, name, imageUri, "true");
                    else
                        saveToFirestore(null, name, imageUri);
                }
                else{
                    Toast.makeText(UpdateProfile.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(UpdateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(UpdateProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(2,2)
                                .start(UpdateProfile.this);
                    }
                }
            }
        });
    }

    private void saveToFirestore(Task<UploadTask.TaskSnapshot> task, String name, Uri downloadUri) {


        HashMap<String, Object> map=new HashMap<>();
        map.put("Name", name);
        map.put("Image", downloadUri.toString());
        firestore.collection("Users").document(uid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UpdateProfile.this, "Profile updated!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirestore(Task<UploadTask.TaskSnapshot> task, String name, Uri downloadUri, String isAdmin) {

        HashMap<String, Object> map=new HashMap<>();
        map.put("Name", name);
        map.put("Image", downloadUri.toString());
        map.put("Admin", isAdmin);
        firestore.collection("Users").document(uid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UpdateProfile.this, "Profile updated!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                imageUri=result.getUri();
                image.setImageURI(imageUri);

                isPhotoSelected=true;
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}