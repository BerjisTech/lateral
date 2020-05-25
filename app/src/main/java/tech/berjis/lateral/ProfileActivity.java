package tech.berjis.lateral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    String UID;

    ImageView home, chats, profile, menu;
    CircleImageView dp;
    EmojiTextView full_name, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();
        dbRef.keepSynced(true);

        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        chats = findViewById(R.id.chats);
        profile = findViewById(R.id.profile);
        username = findViewById(R.id.username);
        full_name = findViewById(R.id.full_name);
        dp = findViewById(R.id.dp);

        loaduserdata();
        staticOnclicks();
    }

    private void staticOnclicks() {

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, FeedActivity.class));
            }
        });
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DMsActivity.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void loaduserdata() {
        newUserState();
        dbRef.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            .with(ProfileActivity.this)
                            .load(dataSnapshot.child("user_image").getValue().toString())
                            .thumbnail(Glide.with(ProfileActivity.this).load(R.drawable.preloader))
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

    private void newUserState() {
        dbRef.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("first_name").exists() ||
                        !dataSnapshot.child("last_name").exists() ||
                        !dataSnapshot.child("user_name").exists() ||
                        !dataSnapshot.child("user_email").exists()) {
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
