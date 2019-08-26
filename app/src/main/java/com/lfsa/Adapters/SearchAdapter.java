package com.lfsa.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.MenuActivities.MenuActivity;
import com.lfsa.R;
import com.lfsa.Activities.MenuActivities.SingleFoodActivity;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    Context context;

    private ArrayList<String> mealNameList;
    private ArrayList<String> mealStallList;
    //ArrayList<String> descList;
    private ArrayList<String> imgList;

    private ArrayList<String> foodIdList;

    private String customer, customerId;

    class SearchViewHolder extends RecyclerView.ViewHolder{

        ImageView mealImage;
        TextView mealName, mealStall;

        LinearLayout parentLayout;


        public SearchViewHolder(View itemView) {
            super(itemView);

            mealName = itemView.findViewById(R.id.mealName);
            mealStall = itemView.findViewById(R.id.mealStall);
            //mealDesc = itemView.findViewById(R.id.mealStall);
            mealImage = itemView.findViewById(R.id.mealImage);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }

    public SearchAdapter(Context context, ArrayList<String> mealNameList, ArrayList<String> mealStallList, ArrayList<String> imgList, ArrayList<String> foodIdList, String customer, String customerId ) {
        this.context = context;
        this.mealNameList = mealNameList;
        this.mealStallList = mealStallList;
        this.imgList = imgList;
        this.foodIdList = foodIdList;
        this.customer = customer;
        this.customerId = customerId;

    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.search_layout, parent, false);

        return new SearchAdapter.SearchViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder holder, final int position) {
        holder.mealName.setText(mealNameList.get(position));
        holder.mealStall.setText(mealStallList.get(position));

        Glide.with(context).load(imgList.get(position)).into(holder.mealImage);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String foodStall = holder.mealStall.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("FoodStalls").child(foodStall).child("FoodStall_Hour").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String manualHours = (String) dataSnapshot.getValue();

                        if(manualHours.equals("Close")){
                            Toast.makeText(context, foodStall+" is currently closed right now.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MenuActivity.class);
                            intent.putExtra("FoodId", foodIdList.get(position));
                            intent.putExtra("chosenFoodStall", mealStallList.get(position));
                            intent.putExtra("customer", customer);
                            intent.putExtra("customerId", customerId);
                            context.startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, SingleFoodActivity.class);
                            intent.putExtra("FoodId", foodIdList.get(position));
                            intent.putExtra("chosenFoodStall", mealStallList.get(position));
                            intent.putExtra("customer", customer);
                            intent.putExtra("customerId", customerId);
                            context.startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return mealNameList.size();
    }
}