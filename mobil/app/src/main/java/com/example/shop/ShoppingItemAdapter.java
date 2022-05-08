package com.example.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShoppingItem> fShoppingItemsData;
    private ArrayList<ShoppingItem> fShoppingItemsDataAll;
    private Context fContext;
    private int lastPosition = -1;

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData){
        this.fShoppingItemsData = itemsData;
        this.fShoppingItemsDataAll = itemsData;
        this.fContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(fContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = fShoppingItemsData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(fContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return fShoppingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = fShoppingItemsDataAll.size();
                results.values = fShoppingItemsDataAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(ShoppingItem item: fShoppingItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            fShoppingItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView fTitleText;
        private TextView fInfoText;
        private TextView fPriceText;
        private ImageView fItemImage;
        private RatingBar fRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fTitleText = itemView.findViewById(R.id.itemTitle);
            fInfoText = itemView.findViewById(R.id.subTitle);
            fPriceText = itemView.findViewById(R.id.price);
            fItemImage = itemView.findViewById(R.id.itemImage);
            fRatingBar = itemView.findViewById(R.id.ratingBar);


        }

        public void bindTo(ShoppingItem currentItem) {
            fTitleText.setText(currentItem.getName());
            fInfoText.setText(currentItem.getInfo());
            fPriceText.setText(currentItem.getPrice());
            fRatingBar.setRating(currentItem.getRatedInfo());

            Glide.with(fContext).load(currentItem.getImageResource()).into(fItemImage);

            itemView.findViewById(R.id.addToCart).setOnClickListener(view -> ((ShopListActivity)fContext).updateAlertIcon(currentItem));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((ShopListActivity)fContext).deleteItem(currentItem));
        }
    }
}

