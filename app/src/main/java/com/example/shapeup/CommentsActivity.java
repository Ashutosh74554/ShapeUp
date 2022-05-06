package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shapeup.adapter.CommentsAdapter;
import com.example.shapeup.model.Comments;
import com.example.shapeup.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    EditText commentEdit;
    Button addComment;
    RecyclerView commentRecyclerview;
    FirebaseFirestore firestore;
    String postId;
    FirebaseAuth auth;
    String currentUserId;
    CommentsAdapter adapter;
    List<Comments> mList;
    List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        commentEdit=findViewById(R.id.comment_edittext);
        addComment=findViewById(R.id.add_comment);
        commentRecyclerview=findViewById(R.id.commentRecyclerview);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        usersList=new ArrayList<>();
        mList=new ArrayList<>();
        adapter=new CommentsAdapter(CommentsActivity.this,mList, usersList);

        commentRecyclerview.setHasFixedSize(true);
        commentRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerview.setAdapter(adapter);

        postId=getIntent().getStringExtra("postid");
        currentUserId=auth.getCurrentUser().getUid();

        firestore.collection("Posts/"+postId+"/Comments").addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange documentChange: value.getDocumentChanges()){
                    if(documentChange.getType()== DocumentChange.Type.ADDED){
                        Comments comments=documentChange.getDocument().toObject(Comments.class);

                        String userId=documentChange.getDocument().getString("user");
                        firestore.collection("Users").document(userId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            Users users=task.getResult().toObject(Users.class);
                                            usersList.add(users);
                                            mList.add(comments);
                                            adapter.notifyDataSetChanged();
                                        }
                                        else{
                                            Toast.makeText(CommentsActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else{
                        adapter.notifyDataSetChanged();;
                    }
                }
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=commentEdit.getText().toString();
                if(!comment.isEmpty()){
                    Map<String, Object> commentsMap=new HashMap<>();
                    commentsMap.put("comment", comment);
                    commentsMap.put("time", FieldValue.serverTimestamp());
                    commentsMap.put("user", currentUserId);
                    firestore.collection("Posts/"+postId+"/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this, "Comment added!!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(CommentsActivity.this, "Please add comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}