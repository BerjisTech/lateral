package tech.berjis.lateral;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    ImageView home, chats, profile, dp;
    String UID, user;
    EmojiTextView full_name, username;

    RecyclerView postsRecycler;
    PostsAdapter postsAdapter;
    ArrayList<Posts> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();
        dbRef.keepSynced(true);

        dp = findViewById(R.id.dp);
        home = findViewById(R.id.home);
        chats = findViewById(R.id.chats);
        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        full_name = findViewById(R.id.full_name);
        postsRecycler = findViewById(R.id.postsRecycler);

        postsRecycler.setFocusable(false);
        dp.requestFocus();

        loadUser();
        staticOnclicks();

        listData = new ArrayList<>();

        postsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        postsRecycler.setHasFixedSize(true);
        postsRecycler.setItemViewCacheSize(20);
        postsRecycler.setDrawingCacheEnabled(true);
        postsRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        loadPosts();
    }

    private void staticOnclicks() {

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, FeedActivity.class));
            }
        });
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, DMsActivity.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, ProfileActivity.class));
            }
        });
    }

    private void loadUser() {
        Intent userIntent = getIntent();
        Bundle userBundle = userIntent.getExtras();
        user = userBundle.getString("user");

        loadUserData(user);
    }

    private void loadUserData(String mUser) {
        dbRef.child("Users").child(mUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String alias = dataSnapshot.child("user_name").getValue().toString();
                String fullname = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString();
                username.setText("@" + alias);
                full_name.setText(fullname);

                if (dataSnapshot.child("user_image").exists() && !dataSnapshot.child("user_image").getValue().toString().equals("")) {
                    long unixTime = System.currentTimeMillis() / 1000L;
                    RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(unixTime));

                    Glide
                            .with(UserActivity.this)
                            .load(dataSnapshot.child("user_image").getValue().toString())
                            .thumbnail(Glide.with(UserActivity.this).load(R.drawable.preloader))
                            .centerCrop()
                            .apply(requestOptions)
                            .error(R.drawable.error_loading_image)
                            .into(dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPosts() {
        listData.clear();
        dbRef.child("Posts").orderByChild("user").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    postsAdapter = new PostsAdapter(UserActivity.this, listData, "main");
                    postsAdapter.notifyDataSetChanged();
                    postsRecycler.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
