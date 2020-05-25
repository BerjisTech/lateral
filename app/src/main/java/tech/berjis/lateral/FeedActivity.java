package tech.berjis.lateral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

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

    static ConstraintLayout mainLayout, storyLayout;
    static String story = "";
    ImageView home, chats, profile, camera, notifications, search, video;
    RecyclerView postsRecycler, iconsRecycler;
    SwipeRefreshLayout pageRefresh;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    List<Posts> listData;
    List<Icons> icons;
    IconsAdapter iconsAdapter;
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
        iconsRecycler = findViewById(R.id.iconsRecycler);
        pageRefresh = findViewById(R.id.pageRefresh);
        mainLayout = findViewById(R.id.mainLayout);
        storyLayout = findViewById(R.id.storyLayout);
        unloggedState();

        listData = new ArrayList<>();
        icons = new ArrayList<>();
        postsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        postsRecycler.setHasFixedSize(true);
        iconsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        iconsRecycler.setHasFixedSize(true);
        loadIcons();
        loadPosts();
        pageRefresher();
    }

    private void pageRefresher() {
        pageRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listData.clear();
                loadPosts();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        pageRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void loadIcons() {
        icons.add(new Icons("send"));
        icons.add(new Icons("model_demo"));
        icons.add(new Icons("img_one"));
        icons.add(new Icons("img_two"));
        icons.add(new Icons("img_three"));
        icons.add(new Icons("preloader"));
        icons.add(new Icons("emoji"));
        iconsAdapter = new IconsAdapter(FeedActivity.this, icons);
        iconsAdapter.notifyDataSetChanged();
        iconsRecycler.setAdapter(iconsAdapter);
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
        if (story.equals("visible")) {
            storyLayout.animate()
                    .translationY(storyLayout.getHeight())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            storyLayout.setVisibility(View.GONE);
                        }
                    });
            mainLayout.setVisibility(View.VISIBLE);
            mainLayout.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
            story = "";
        } else {
            super.onBackPressed();
            this.finishAffinity();
        }
    }

    static void showStory(Context mContext) {
        story = "visible";
        mainLayout.animate()
                .translationY(mainLayout.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mainLayout.setVisibility(View.GONE);
                    }
                });
        storyLayout.setVisibility(View.VISIBLE);
        storyLayout.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
    }
}
