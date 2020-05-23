package tech.berjis.lateral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatePost extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference dbRef, postRef;
    StorageReference storageReference;
    Uri filePath;
    String UID, postID = "", hasImage = "";

    ImageView back;
    TextView newImage, share;
    ViewPager2 imagePager;
    EmojiEditText postText;

    List<ImageList> imageList;
    ImagePagerAdapter imagePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        dbRef.keepSynced(true);

        imageList = new ArrayList<>();

        back = findViewById(R.id.back);
        imagePager = findViewById(R.id.imagePager);
        newImage = findViewById(R.id.newImage);
        share = findViewById(R.id.share);
        postText = findViewById(R.id.postText);

        postNode();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePost.super.finish();
            }
        });
        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postText();
            }
        });
    }

    private void postNode() {
        long unixTime = System.currentTimeMillis() / 1000L;
        if (postID.equals("")) {
            postRef = dbRef.child("Posts").push();
            postID = postRef.getKey();

            postRef.child("post_id").setValue(postID);
            postRef.child("user").setValue(UID);
            postRef.child("status").setValue("draft");
            postRef.child("time").setValue(unixTime);
            postRef.child("type").setValue("photo");
            postRef.child("availability").setValue("premium");
            postRef.child("text").setValue("");
            postRef.child("availability").setValue("free");
        }
    }

    private void loadImages(String parent) {
        imageList.clear();
        dbRef.child("PostImages").child(parent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot imagesSnapshot : dataSnapshot.getChildren()) {
                        ImageList l = imagesSnapshot.getValue(ImageList.class);
                        imageList.add(l);
                    }
                    Collections.reverse(imageList);
                    imagePagerAdapter = new ImagePagerAdapter(CreatePost.this, imageList, "new_post");
                    imagePagerAdapter.notifyDataSetChanged();
                    imagePager.setAdapter(imagePagerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
        }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        }

        postImage();
    }

    private void postImage() {
        long unixTime = System.currentTimeMillis() / 1000L;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if (filePath != null) {

            final StorageReference ref = storageReference.child("Post Images/" + postID + unixTime + ".jpg");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    final String image_url = downloadUrl.toString();

                                    final DatabaseReference[] imageRef = {dbRef.child("PostImages").child(postID).push()};
                                    String image_id = imageRef[0].getKey();
                                    imageRef[0].child("image_id").setValue(image_id);
                                    imageRef[0].child("parent_id").setValue(postID);
                                    imageRef[0].child("image").setValue(image_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                postRef.child("status").setValue("published");
                                                imageRef[0] = null;
                                                hasImage = "hasImage";
                                                loadImages(postID);
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatePost.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            progressDialog.dismiss();
        }
    }

    private void postText() {
        String text = postText.getText().toString();

        postRef.child("text").setValue(text);
        postRef.child("status").setValue("published");
        Toast.makeText(this, "Post succesfully published", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CreatePost.this, FeedActivity.class));
    }
}
