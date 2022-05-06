package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {
    ImageView mPostImage;
    EditText mCaptionText;
    Button mAddPostButton;
    ProgressBar mProgressBar;
    Uri postImageuri=null;
    StorageReference storageReference;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPostImage=findViewById(R.id.post_image);
        mCaptionText=findViewById(R.id.post_title);
        mAddPostButton=findViewById(R.id.post_button);
        mProgressBar=findViewById(R.id.post_progressbar);

        storageReference=FirebaseStorage.getInstance().getReference();;
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();

        mProgressBar.setVisibility(View.INVISIBLE);

        mAddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                String caption=mCaptionText.getText().toString();
                if(!caption.isEmpty() && postImageuri!=null){
                    StorageReference postRef=storageReference.child("post_images").child(FieldValue.serverTimestamp().toString()+".jpg");
                    postRef.putFile(postImageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String, Object> postMap=new HashMap<>();
                                        postMap.put("image",uri.toString());
                                        postMap.put("user",currentUserId);
                                        postMap.put("caption",caption);
                                        postMap.put("time",FieldValue.serverTimestamp());

                                        firestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful()){
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(PostActivity.this, "Post added successfully!!!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                                                    finish();
                                                }
                                                else{
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(PostActivity.this, "Please add image and caption", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3,3)
                        .setMinCropResultSize(512,512)
                        .start(PostActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                postImageuri=result.getUri();
                mPostImage.setImageURI(postImageuri);
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}