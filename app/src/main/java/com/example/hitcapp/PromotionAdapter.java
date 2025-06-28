package com.example.hitcapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Chỉ để test

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {

    private List<PromotionItem> promotionList;

    public PromotionAdapter(List<PromotionItem> promotionList) {
        this.promotionList = promotionList;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_promotion_item, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        PromotionItem promotion = promotionList.get(position);
        holder.ivPromotionImage.setImageResource(promotion.getImageResId());
        holder.tvPromotionTitle.setText(promotion.getTitle());
        holder.tvPromotionDescription.setText(promotion.getDescription());

        // Xử lý sự kiện nhấn nút "Xem chi tiết"
        holder.btnViewDetails.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Xem chi tiết: " + promotion.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Ở đây bạn có thể mở một Activity mới để hiển thị chi tiết khuyến mãi
            // Intent intent = new Intent(holder.itemView.getContext(), PromotionDetailActivity.class);
            // intent.putExtra("promotion_title", promotion.getTitle());
            // ... các dữ liệu khác
            // holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPromotionImage;
        TextView tvPromotionTitle;
        TextView tvPromotionDescription;
        Button btnViewDetails;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPromotionImage = itemView.findViewById(R.id.ivPromotionImage);
            tvPromotionTitle = itemView.findViewById(R.id.tvPromotionTitle);
            tvPromotionDescription = itemView.findViewById(R.id.tvPromotionDescription);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}