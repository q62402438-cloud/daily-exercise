package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

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
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
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

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAuthorName;
        private TextView tvPostTime;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvViewCount;
        private TextView tvLikeCount;
        private TextView tvCommentCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvPostTime = itemView.findViewById(R.id.tv_post_time);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvViewCount = itemView.findViewById(R.id.tv_view_count);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }

        public void bind(Post post) {
            if (tvAuthorName != null) tvAuthorName.setText(post.getAuthorName());
            if (tvPostTime != null) tvPostTime.setText(post.getPostTime());
            if (tvTitle != null) tvTitle.setText(post.getTitle());
            if (tvContent != null) tvContent.setText(post.getContent());
            if (tvViewCount != null) tvViewCount.setText(post.getViewCount());
            if (tvLikeCount != null) tvLikeCount.setText(post.getLikeCount());
            if (tvCommentCount != null) tvCommentCount.setText(post.getCommentCount());
        }
    }
}
