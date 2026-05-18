package com.example.myapplication;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Uri> imageList = new ArrayList<>();
    private static final int MAX_IMAGES = 9;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onAddImageClick();
        void onRemoveImageClick(int position);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    public List<Uri> getImageList() {
        return imageList;
    }

    public void addImage(Uri uri) {
        if (imageList.size() < MAX_IMAGES) {
            imageList.add(uri);
            notifyDataSetChanged();
        }
    }

    public void removeImage(int position) {
        if (position >= 0 && position < imageList.size()) {
            imageList.remove(position);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (position < imageList.size()) {
            Uri imageUri = imageList.get(position);
            holder.ivImage.setImageURI(imageUri);
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.layoutAdd.setVisibility(View.GONE);

            holder.ivImage.setOnClickListener(null);
            holder.btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveImageClick(holder.getAdapterPosition());
                }
            });
        } else {
            holder.ivImage.setVisibility(View.GONE);
            holder.btnRemove.setVisibility(View.GONE);
            holder.layoutAdd.setVisibility(View.VISIBLE);

            holder.layoutAdd.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddImageClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size() < MAX_IMAGES ? imageList.size() + 1 : MAX_IMAGES;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageButton btnRemove;
        LinearLayout layoutAdd;

        ImageViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            layoutAdd = itemView.findViewById(R.id.layout_add);
        }
    }
}
