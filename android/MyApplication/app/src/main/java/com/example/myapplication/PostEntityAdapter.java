package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.model.PostEntity;
import java.util.ArrayList;
import java.util.List;

public class PostEntityAdapter extends RecyclerView.Adapter<PostEntityAdapter.PostEntityViewHolder> {

    private List<PostEntity> postList = new ArrayList<>();
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(PostEntity post);
    }

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setPostList(List<PostEntity> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostEntityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_review, parent, false);
        return new PostEntityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostEntityViewHolder holder, int position) {
        PostEntity post = postList.get(position);
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

    static class PostEntityViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAuthorName;
        private TextView tvTitle;
        private TextView tvContent;

        public PostEntityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }

        public void bind(PostEntity post) {
            if (tvAuthorName != null) {
                String authorName = post.getAuthorName() != null ? post.getAuthorName() : "匿名用户";
                tvAuthorName.setText(authorName);
            }
            if (tvTitle != null) {
                String title = post.getTitle() != null ? post.getTitle() : "无标题";
                tvTitle.setText(title);
            }
            if (tvContent != null) {
                String content = post.getContent() != null ? post.getContent() : "无内容";
                tvContent.setText(content);
            }
        }
    }
}
