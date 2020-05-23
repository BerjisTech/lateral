package tech.berjis.lateral;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    TextView edit_profile_txt, change_phone_txt, terms_of_use_txt, logout_txt, notifications_txt;
    ImageView edit_profile_btn, change_phone_btn, back, terms_of_use_btn, logout_btn, notifications_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_settings);

        edit_profile_txt = findViewById(R.id.edit_profile_txt);
        edit_profile_btn = findViewById(R.id.edit_profile_btn);
        change_phone_txt = findViewById(R.id.change_phone_txt);
        change_phone_btn = findViewById(R.id.change_phone_btn);
        terms_of_use_btn = findViewById(R.id.terms_of_use_btn);
        terms_of_use_txt = findViewById(R.id.terms_of_use_txt);
        notifications_txt = findViewById(R.id.notifications_txt);
        notifications_btn = findViewById(R.id.notifications_btn);
        logout_btn = findViewById(R.id.logout_btn);
        logout_txt = findViewById(R.id.logout_txt);
        back = findViewById(R.id.back);

        staticOnclick();
    }

    private void staticOnclick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.finish();
            }
        });

        edit_profile_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class));
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class));
            }
        });

        change_phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ChangePhoneNumber.class));
            }
        });

        change_phone_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ChangePhoneNumber.class));
            }
        });

        terms_of_use_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, TermsActivity.class));
            }
        });

        terms_of_use_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, TermsActivity.class));
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        logout_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        notifications_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, NotificationsActivity.class));
            }
        });

        notifications_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, NotificationsActivity.class));
            }
        });
    }
}
