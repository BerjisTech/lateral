package tech.berjis.lateral;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    List<Posts> PostsList;
    PostsAdapter PostsAdapter;
    RecyclerView PostsRecycler;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_search);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);

        PostsRecycler = findViewById(R.id.searchResults);
        search = findViewById(R.id.search);

        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = search.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        PostsList = new ArrayList<>();
        /*StaggeredGridLayoutManager sgManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        sgManager.setReverseLayout(false);
        sgManager.scrollToPositionWithOffset(0, 0);*/
        PostsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        PostsRecycler.setHasFixedSize(true);
        PostsRecycler.setAdapter(PostsAdapter);

        loadPosts("");

        final CountDownTimer c_timer = new CountDownTimer(50000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                final int min = 1;
                final int max = 5;
                final int random = new Random().nextInt((max - min) + 1) + min;
                String searchHint = "";
                if (random == 1) {
                    search.setQueryHint("Type anything");
                }
                if (random == 2) {
                    search.setQueryHint("Search by username");
                }
                if (random == 3) {
                    search.setQueryHint("Search by location");
                }
                if (random == 4) {
                    search.setQueryHint("Search for tags");
                }
                if (random == 5) {
                    search.setQueryHint("Search for topics");
                }
            }

            @Override
            public void onFinish() {

            }
        };
        c_timer.start();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                c_timer.cancel();
                loadPosts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void loadPosts(final String query) {
        PostsList.clear();
        dbRef.child("Posts").orderByChild("status").equalTo("published").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PostsList.clear();
                    for (DataSnapshot PostsSnapshot : dataSnapshot.getChildren()) {
                        if (PostsSnapshot.child("text").getValue().toString().toLowerCase().contains(query.toLowerCase())) {
                            Posts Posts = PostsSnapshot.getValue(Posts.class);
                            PostsList.add(Posts);
                        }
                    }
                    Collections.shuffle(PostsList);
                    PostsAdapter = new PostsAdapter(SearchActivity.this, PostsList, "search");
                    PostsAdapter.notifyDataSetChanged();
                    PostsRecycler.setAdapter(PostsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchText() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                search.setQueryHint("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                search.setQueryHint("done!");
            }

        }.start();
    }
}
