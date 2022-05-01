package com.example.shapeup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shapeup.adapter.PostAdapter;
import com.example.shapeup.model.Post;
import com.example.shapeup.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle toggle;
    CircleImageView userImage;
    TextView userName;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    StorageReference storageReference;
    String uid;
    FloatingActionButton add_post;
    RecyclerView mRecyclerView;
    PostAdapter adapter;
    List<Post> list;
    List<Users> usersList;

    Query query;
    ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer_layout=findViewById(R.id.drawer_layout);
        toggle= new ActionBarDrawerToggle(this, drawer_layout, R.string.open,R.string.close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigation=findViewById(R.id.navigation);
        View view= LayoutInflater.from(this).inflate(R.layout.nav_header,null);

        navigation.addHeaderView(view);

        userImage= view.findViewById(R.id.user_image);
        userName= view.findViewById(R.id.user_name);
        reset_alert=new AlertDialog.Builder(this);

        add_post=findViewById(R.id.add_post);
        mRecyclerView=findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        usersList=new ArrayList<>();
        list=new ArrayList<>();
        adapter=new PostAdapter(MainActivity.this, list, usersList);
        mRecyclerView.setAdapter(adapter);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        try {
            uid = auth.getCurrentUser().getUid();
            firestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            userName.setText(task.getResult().getString("Name"));
                            String uri = task.getResult().getString("Image");
                            Glide.with(getApplicationContext()).load(uri).into(userImage);
                            if (task.getResult().getString("Admin") != null) {
                                add_post.setVisibility(View.VISIBLE);
                            } else {
                                add_post.setVisibility(View.GONE);
                            }
                        }
                        else{
                            startActivity(new Intent(MainActivity.this,UpdateProfile.class));
                        }
                    }
                }
            });
        }catch(NullPointerException e){
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }

        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                finish();
            }
        });

        if(auth.getCurrentUser()!=null){
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom=!mRecyclerView.canScrollVertically(1);
                    if(isBottom){
                        Toast.makeText(MainActivity.this, "Reached bottom", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            query=firestore.collection("Posts").orderBy("time", Query.Direction.DESCENDING);
            listenerRegistration=query.addSnapshotListener(MainActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for(DocumentChange doc :value.getDocumentChanges()){
                        if(doc.getType()== DocumentChange.Type.ADDED){
                            String postId=doc.getDocument().getId();
                            Post post=doc.getDocument().toObject(Post.class).withId(postId);
                            String postUserId=doc.getDocument().getString("user");
                            firestore.collection("Users").document(postUserId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                Users users=task.getResult().toObject(Users.class);
                                                usersList.add(users);
                                                list.add(post);
                                                adapter.notifyDataSetChanged();
                                            }else{
                                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                }
            });
        }

        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.workout_plans:
                        intent=new Intent(MainActivity.this,WorkoutPlans.class);
                        startActivity(intent);
                        break;
                    case R.id.water_tracker:
                        intent=new Intent(MainActivity.this,WaterTracker.class);
                        startActivity(intent);
                        break;
                    case R.id.progress_report:
                        intent=new Intent(MainActivity.this,ProgressReport.class);
                        startActivity(intent);
                        break;
                    case R.id.Reminders:
                        intent=new Intent(MainActivity.this,Reminders.class);
                        startActivity(intent);
                        break;
                    case R.id.faq:
                        intent=new Intent(MainActivity.this,FAQs.class);
                        startActivity(intent);
                        break;
                    case R.id.update_profile:
                        intent=new Intent(MainActivity.this, UpdateProfile.class);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        auth.signOut();
                        Toast.makeText(MainActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                        break;
                    case R.id.delete_Account:
                        reset_alert.setTitle("Delete account?")
                                .setMessage("Are you sure?")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                auth.signOut();
                                                Toast.makeText(MainActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MainActivity.this,Login.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).setNegativeButton("Cancel",null)
                                .create().show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current= auth.getCurrentUser();
        if(current==null){
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        else{
            String currentUserId=auth.getCurrentUser().getUid();
            firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            startActivity(new Intent(MainActivity.this, UpdateProfile.class));
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

}