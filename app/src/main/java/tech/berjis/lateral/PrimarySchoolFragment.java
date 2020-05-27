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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class PrimarySchoolFragment extends Fragment {
    private String searchQuery;
    private PostsAdapter postsAdapter;
    private ArrayList<Posts> listData;
    private RecyclerView postsRecycler;
    private Context mContext;

    @SuppressLint("ValidFragment")
    PrimarySchoolFragment(Context mContext, String searchQuery) {
        this.mContext = mContext;
        this.searchQuery = searchQuery;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.university_school_fragment, container, false);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);

        postsRecycler = view.findViewById(R.id.schoolsRecycler);

        listData = new ArrayList<>();
        postsRecycler.setLayoutManager(new GridLayoutManager(mContext, 2));
        postsRecycler.setHasFixedSize(true);
        postsRecycler.setItemViewCacheSize(20);
        postsRecycler.setDrawingCacheEnabled(true);
        postsRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        listData.clear();
        dbRef.child("Posts").orderByChild("status").equalTo("published").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshots : dataSnapshot.getChildren()) {
                        if (postSnapshots.child("text").getValue().toString().toLowerCase().contains(searchQuery.toLowerCase())) {
                            Posts posts = postSnapshots.getValue(Posts.class);
                            listData.add(posts);
                        }
                    }
                    Collections.reverse(listData);
                    postsAdapter = new PostsAdapter(getContext(), listData, "search");
                    postsAdapter.notifyDataSetChanged();
                    postsRecycler.setAdapter(postsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    void loadPosts(final String searchQuery) {

    }


    void searchSchools(String query) {
        Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show();
    }
}
