package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.CommentEntity;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentEntity> commentList;

    public CommentAdapter(List<CommentEntity> commentList) {
        this.commentList = commentList;
    }

    public void setCommentList(List<CommentEntity> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentEntity comment = commentList.get(position);
        holder.tvAuthor.setText(comment.getUserName());
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getPublishTime());
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvContent, tvTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_comment_author);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
        }
    }
}
