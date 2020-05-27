package tech.berjis.lateral;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HighSchoolFragment extends Fragment {
    private String searchQuery;
    private PostsAdapter postsAdapter;
    private ArrayList<Posts> listData;
    private RecyclerView postsRecycler;
    private DatabaseReference dbRef;
    private Context mContext;

    public HighSchoolFragment(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("ValidFragment")
    HighSchoolFragment(Context mContext, String searchQuery) {
        this.mContext = mContext;
        this.searchQuery = searchQuery;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.university_school_fragment, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);

        postsRecycler = view.findViewById(R.id.schoolsRecycler);

        listData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        postsRecycler.setLayoutManager(linearLayoutManager);
        postsRecycler.setHasFixedSize(true);
        postsRecycler.setItemViewCacheSize(20);
        postsRecycler.setDrawingCacheEnabled(true);
        postsRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        loadPosts();

        searchQuery = "";

        return view;
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
                    postsAdapter = new PostsAdapter(getContext(), listData, "main");
                    postsAdapter.notifyDataSetChanged();
                    postsRecycler.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void searchSchools(String query) {
        Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show();
    }
}
