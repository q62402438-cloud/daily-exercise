package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Post;

import java.util.List;

public class FavoritePostAdapter extends RecyclerView.Adapter<FavoritePostAdapter.FavoritePostViewHolder> {

    private List<Post> postList;
    private OnFavoriteActionListener listener;

    public interface OnFavoriteActionListener {
        void onPostClick(Post post);
        void onUnfavoriteClick(Post post, int position);
    }

    public FavoritePostAdapter(List<Post> postList, OnFavoriteActionListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < postList.size()) {
            postList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public FavoritePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_post, parent, false);
        return new FavoritePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritePostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvAuthor.setText(post.getAuthorName());
        holder.tvTime.setText(post.getPostTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
        });

        holder.btnUnfavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUnfavoriteClick(post, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public static class FavoritePostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvTime;
        ImageButton btnUnfavorite;

        public FavoritePostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_post_title);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvTime = itemView.findViewById(R.id.tv_post_time);
            btnUnfavorite = itemView.findViewById(R.id.btn_unfavorite);
        }
    }
}
