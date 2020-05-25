package tech.berjis.lateral;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder> {


    private List<Icons> icons;
    private Context mContext;

    IconsAdapter(Context mContext, List<Icons> icons) {
        this.mContext = mContext;
        this.icons = icons;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.icons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Icons ld = icons.get(position);
        Context context = holder.image.getContext();
        int id = context.getResources().getIdentifier(ld.getImage(), "drawable", context.getPackageName());
        holder.image.setImageResource(id);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedActivity.showStory(mContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        View mView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            mView = itemView;
        }
    }
}
