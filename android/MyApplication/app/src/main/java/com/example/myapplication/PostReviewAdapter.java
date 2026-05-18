package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PostReviewAdapter extends RecyclerView.Adapter<PostReviewAdapter.PostReviewViewHolder> {

    private List<Post> postList = new ArrayList<>();
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setPostList(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_review, parent, false);
        return new PostReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostReviewViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAuthorName;
        private TextView tvTitle;
        private TextView tvContent;

        public PostReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }

        public void bind(Post post) {
            if (tvAuthorName != null) tvAuthorName.setText(post.getAuthorName());
            if (tvTitle != null) tvTitle.setText(post.getTitle());
            if (tvContent != null) tvContent.setText(post.getContent());
        }
    }
}