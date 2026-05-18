package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeaturedPostAdapter extends RecyclerView.Adapter<FeaturedPostAdapter.FeaturedPostViewHolder> {

    private List<Post> postList;
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public FeaturedPostAdapter(List<Post> postList, OnPostClickListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeaturedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_post, parent, false);
        return new FeaturedPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedPostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post, position + 1, postList.size());

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

    static class FeaturedPostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvAuthor;
        private TextView tvIndicator;

        public FeaturedPostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvIndicator = itemView.findViewById(R.id.tv_indicator);
        }

        public void bind(Post post, int current, int total) {
            if (tvTitle != null) tvTitle.setText(post.getTitle());
            if (tvContent != null) tvContent.setText(post.getContent());
            if (tvAuthor != null) tvAuthor.setText(post.getAuthorName());
            if (tvIndicator != null) tvIndicator.setText(current + " / " + total);
        }
    }
}
