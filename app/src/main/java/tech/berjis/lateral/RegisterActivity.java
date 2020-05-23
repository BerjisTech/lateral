package tech.berjis.lateral;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    TextView sendName;
    EditText mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);

        mName = findViewById(R.id.userName);
        sendName = findViewById(R.id.sendName);

        sendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mName.getText().toString().trim();

                if (username.length() < 6) {
                    mName.setError("Your username should be at least 6 characters long");
                    return;
                }

                dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshots : dataSnapshot.getChildren()) {
                                if (userSnapshots
                                        .child("user_name")
                                        .getValue()
                                        .toString()
                                        .toLowerCase()
                                        .equals(
                                                username
                                                        .toLowerCase()
                                        )
                                ) {
                                    mName.setError("This username has already been taken. Choose a new one");
                                    return;
                                }
                            }
                        }
                        createUser(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void createUser(final String chosen_name) {
        final String chosen_email = chosen_name + "@lynn.app";
        mAuth.createUserWithEmailAndPassword(chosen_email, chosen_name).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    dbRef.child("Users").child(user_id).child("user_id").setValue(user_id);
                    dbRef.child("Users").child(user_id).child("user_name").setValue(chosen_name);
                    dbRef.child("Users").child(user_id).child("user_email").setValue(chosen_email);
                    dbRef.child("Users").child(user_id).child("first_name").setValue("Borldy");
                    dbRef.child("Users").child(user_id).child("last_name").setValue("McNorldy");
                    dbRef.child("Users").child(user_id).child("user_image").setValue("");
                    dbRef.child("Users").child(user_id).child("user_phone").setValue("254725227513");
                    dbRef.child("Users").child(user_id).child("user_type").setValue("");
                    dbRef.child("Users").child(user_id).child("user_description").setValue("");
                    dbRef.child("Users").child(user_id).child("user_device").setValue(deviceToken);

                    startActivity(new Intent(RegisterActivity.this, FeedActivity.class));
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), "There was an error creating your account. (" + chosen_email + ") Please try again.\n\nAlso, check your internet", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}