package tech.berjis.lateral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    ImageView dp;
    String UID, user;
    EmojiTextView full_name, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();
        dbRef.keepSynced(true);

        dp = findViewById(R.id.dp);
        username = findViewById(R.id.username);
        full_name = findViewById(R.id.full_name);

        loadUser();
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
                    Picasso.get().load(dataSnapshot.child("user_image").getValue().toString()).error(R.drawable.logo).into(dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
