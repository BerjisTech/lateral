package tech.berjis.lateral;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

import org.ocpsoft.prettytime.PrettyTime;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<Posts> listData;
    private Context mContext;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    String UID, view_type;

    PostsAdapter(Context mContext, List<Posts> listData, String view_type) {
        this.mContext = mContext;
        this.listData = listData;
        this.view_type = view_type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.keepSynced(true);


        View view = null;
        if (view_type.equals("main")) {
            view = LayoutInflater.from(mContext).inflate(R.layout.feed, parent, false);
        }
        if (view_type.equals("search")) {
            view = LayoutInflater.from(mContext).inflate(R.layout.search, parent, false);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posts ld = listData.get(position);

        if (view_type.equals("search")) {
            imageChecker(ld.getPost_id(), holder);
        }
        if (view_type.equals("main")) {
            loadUserData(ld, holder);
            imageChecker(ld.getPost_id(), holder);
            staticOnClicks(ld.getPost_id(), holder);

            if (ld.getText().equals("")) {
                holder.postText.setVisibility(View.GONE);
            } else {
                holder.postText.setText(ld.getText());
            }
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        EmojiTextView userName, postText;
        TextView postTime, imageCount;
        ImageView likes, comments, postImage, cameraIcon;
        CardView imageCard;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            postText = itemView.findViewById(R.id.postText);
            postTime = itemView.findViewById(R.id.postTime);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            postImage = itemView.findViewById(R.id.postImage);
            imageCount = itemView.findViewById(R.id.imageCount);
            cameraIcon = itemView.findViewById(R.id.cameraIcon);
            imageCard = itemView.findViewById(R.id.imageCard);
            mView = itemView;
        }
    }

    private void staticOnClicks(final String post_id, final ViewHolder holder) {

        holder.comments
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent imageIntent = new Intent(mContext, CommentsActivity.class);
                                Bundle imageBundle = new Bundle();
                                imageBundle.putString("post_id", post_id);
                                imageIntent.putExtras(imageBundle);
                                mContext.startActivity(imageIntent);
                            }
                        });
        dbRef.child("Likes").child(post_id).child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Picasso.get().load(R.drawable.like_active).into(holder.likes);
                    holder.likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbRef.child("Likes").child(post_id).child(UID).removeValue();
                            Picasso.get().load(R.drawable.like).into(holder.likes);
                        }
                    });
                } else {
                    holder.likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbRef.child("Likes").child(post_id).child(UID).setValue(true);
                            Picasso.get().load(R.drawable.like_active).into(holder.likes);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void imageChecker(final String parent, final ViewHolder holder) {
        dbRef.child("PostImages").child(parent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadImages(parent, holder);
                    holder.cameraIcon.setVisibility(View.VISIBLE);
                    holder.imageCount.setVisibility(View.VISIBLE);
                    long images = dataSnapshot.getChildrenCount();
                    if (images > 1) {
                        holder.imageCount.setText("+ " + images);
                    }
                } else {
                    holder.imageCard.setVisibility(View.GONE);
                    holder.postImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadImages(final String parent, final ViewHolder holder) {
        dbRef.child("PostImages").child(parent).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot selectedimage : dataSnapshot.getChildren()) {
                        long unixTime = System.currentTimeMillis() / 1000L;
                        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(unixTime));
                        Glide.with(mContext).load(selectedimage.child("image").getValue().toString()).thumbnail(0.25f).apply(requestOptions).into(holder.postImage);
                        if (view_type.equals("search")) {
                            holder.postImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent imageIntent = new Intent(mContext, CommentsActivity.class);
                                    Bundle imageBundle = new Bundle();
                                    imageBundle.putString("post_id", parent);
                                    imageIntent.putExtras(imageBundle);
                                    mContext.startActivity(imageIntent);
                                }
                            });
                        } else {
                            holder.postImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent imageIntent = new Intent(mContext, FullScreenGallery.class);
                                    Bundle imageBundle = new Bundle();
                                    imageBundle.putString("parent", parent);
                                    imageIntent.putExtras(imageBundle);
                                    mContext.startActivity(imageIntent);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserData(Posts ld, final ViewHolder holder) {
        long time = ld.getTime() * 1000;
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(new Date(time));
        holder.postTime.setText(ago);
        dbRef.child("Users").child(ld.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("user_name").getValue().toString();
                String userimage = dataSnapshot.child("user_image").getValue().toString();

                if (!userimage.equals("")) {
                    Picasso.get().load(userimage).into(holder.userImage);
                }
                if (!username.equals("")) {
                    holder.userName.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
