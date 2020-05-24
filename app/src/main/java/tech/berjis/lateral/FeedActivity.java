package tech.berjis.lateral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    ImageView home, chats, profile, camera, notifications, search, video;
    RecyclerView postsRecycler;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    List<Posts> listData;
    PostsAdapter postsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_feed);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);

        home = findViewById(R.id.home);
        chats = findViewById(R.id.chats);
        profile = findViewById(R.id.profile);
        camera = findViewById(R.id.camera);
        notifications = findViewById(R.id.notifications);
        search = findViewById(R.id.search);
        video = findViewById(R.id.video);
        postsRecycler = findViewById(R.id.postsRecycler);
        unloggedState();

        listData = new ArrayList<>();
        postsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        postsRecycler.setHasFixedSize(true);
        loadPosts();
    }

    private void unloggedState() {
        if (mAuth.getCurrentUser() == null) {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, RegisterActivity.class));
                }
            });
            chats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, RegisterActivity.class));
                }
            });
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, RegisterActivity.class));
                }
            });
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, RegisterActivity.class));
                }
            });
            notifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, RegisterActivity.class));
                }
            });
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, RegisterActivity.class));
                }
            });
        } else {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            chats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, DMsActivity.class));
                }
            });
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, ProfileActivity.class));
                }
            });
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, CreatePost.class));
                }
            });
            notifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, NotificationsActivity.class));
                }
            });
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(FeedActivity.this, SearchActivity.class));
                }
            });
        }
    }

    private void loadPosts() {
        listData.clear();
        dbRef.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshots : dataSnapshot.getChildren()) {
                        if (postSnapshots.child("status").getValue().toString().equals("published") &&
                                postSnapshots.child("availability").getValue().toString().equals("free")) {
                            Posts posts = postSnapshots.getValue(Posts.class);
                            listData.add(posts);
                        }
                    }
                    Collections.reverse(listData);
                    postsAdapter = new PostsAdapter(FeedActivity.this, listData, "main");
                    postsAdapter.notifyDataSetChanged();
                    postsRecycler.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}
