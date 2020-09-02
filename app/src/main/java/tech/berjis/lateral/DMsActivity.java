package tech.berjis.lateral;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

public class DMsActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public DatabaseReference dbRef;
    public String uid;

    ImageView back, home, chats, profile;

    private List<String> listData;
    private RecyclerView rv;
    private ChatsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_d_ms);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        uid = mAuth.getCurrentUser().getUid();
        dbRef.keepSynced(true);

        home = findViewById(R.id.home);
        chats = findViewById(R.id.chats);
        profile = findViewById(R.id.profile);
        rv = findViewById(R.id.rv);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMsActivity.super.finish();
            }
        });

        staticOnclicks();
        loadmessages();
    }

    private void staticOnclicks() {

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DMsActivity.this, FeedActivity.class));
            }
        });

        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DMsActivity.this, ProfileActivity.class));
            }
        });
    }

    private void loadmessages(){
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        listData = new ArrayList<>();
        dbRef.child("Chats").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        String l = npsnapshot.getKey();
                        listData.add(l);
                    }
                    Collections.reverse(listData);
                    adapter = new ChatsAdapter(DMsActivity.this, listData);
                    rv.setAdapter(adapter);
                    //rv.scrollToPosition(listData.size() - 1);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DMsActivity.this, "Kuna shida mahali", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
