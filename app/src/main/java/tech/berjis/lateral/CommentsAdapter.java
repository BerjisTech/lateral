package tech.berjis.lateral;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comments> listData;
    private static DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    CommentsAdapter(List<Comments> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comments ld = listData.get(position);
        loadUser(ld.getSender(), holder);

        long time = ld.getDate() * 1000;
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(new Date(time));

        holder.postDate.setText(ago);
        holder.post.setText(ld.getText());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        EmojiTextView userName, post;
        TextView postDate;
        ConstraintLayout rootView;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            postDate = itemView.findViewById(R.id.commentTime);
            post = itemView.findViewById(R.id.userComment);
            rootView = itemView.findViewById(R.id.rootView);
            mView = itemView;
        }
    }

    private static void loadUser(String user, final ViewHolder holder) {
        dbRef.child("Users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
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
