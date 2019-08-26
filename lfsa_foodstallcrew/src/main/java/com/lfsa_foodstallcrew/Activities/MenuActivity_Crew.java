package com.lfsa_foodstallcrew.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lfsa_foodstallcrew.Activities.LoginActivities.LoginActivity_Crew;
import com.lfsa_foodstallcrew.GettersSetters.FoodCrew;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class MenuActivity_Crew extends AppCompatActivity {

    private RecyclerView mFoodList;
    private DatabaseReference mDatabase, databaseReference, reference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String currentFoodStall, fullName;
    private ArrayList users;
    private String uid, user_name, foodstallImage_URL;
    private CardView card_view;
    private Context mContext;
    private FirebaseUser user;
    private ImageView imageFoodStall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmenu_crew);

        //puta
        currentFoodStall = getIntent().getStringExtra("FOODSTALL");
        this.setTitle(currentFoodStall);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //GET IT YEET
        mDatabase=databaseReference.child("Meals");
        mDatabase.keepSynced(true);


        mFoodList = (RecyclerView) findViewById(R.id.foodList);
        mFoodList.setHasFixedSize(true);
        mFoodList.setLayoutManager(new LinearLayoutManager(this));

        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MenuActivity_Crew.this, LoginActivity_Crew.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };


    }

    private void setImage() {
        switch (user_name){
            case "Jefcee's":
                Picasso.with(MenuActivity_Crew.this).load(R.drawable.jefcees_logo).into(imageFoodStall);
                break;
            case "S-coop-s":
                Picasso.with(MenuActivity_Crew.this).load(R.drawable.scoops_logo).into(imageFoodStall);
                break;
            case "Tempura Sam":
                Picasso.with(MenuActivity_Crew.this).load(foodstallImage_URL).into(imageFoodStall);
                break;
        }

    }


    @Override
    protected void onStart(){
        super.onStart();

        //mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<FoodCrew, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<FoodCrew, FoodViewHolder>
                (FoodCrew.class, R.layout.singlemenuitem_crew, FoodViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(currentFoodStall)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, FoodCrew model, int position) {
                viewHolder.setMeal_Name(model.getMeal_Name());
                viewHolder.setMeal_Price(model.getMeal_Price());
                viewHolder.setMeal_Desc(model.getMeal_Desc());
                viewHolder.setMeal_Category(model.getMeal_Category());
                viewHolder.setMeal_Image(getApplicationContext(),model.getMeal_Image());
                viewHolder.setMeal_Availability(model.getMeal_Availability());

                //viewHolder.card_view.setOnClickListener(this);
                final String food_key = getRef(position).getKey().toString();
                getMealMustTry(food_key, viewHolder);
                viewHolder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Display option menu

                        PopupMenu popupMenu = new PopupMenu(MenuActivity_Crew.this, viewHolder.txtOptionDigit);
                        popupMenu.inflate(R.menu.option_menu_crew);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                int i = item.getItemId();
                                if (i == R.id.mnu_item_edit) {
                                    Intent singleFoodActivity = new Intent(MenuActivity_Crew.this, EditMealActivity_Crew.class);
                                    singleFoodActivity.putExtra("FoodId", food_key);
                                    singleFoodActivity.putExtra("FOODSTALL", currentFoodStall);
                                    startActivity(singleFoodActivity);

                                } else if (i == R.id.mnu_item_delete) {//Delete item
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MenuActivity_Crew.this);
                                    builder1.setTitle("DELETE MEAL");
                                    builder1.setMessage("Are you sure you want to delete this meal?");
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton(
                                            "YES",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    mDatabase.child(food_key).removeValue();
                                                    notifyDataSetChanged();
                                                    Toast.makeText(MenuActivity_Crew.this, "Meal Deleted", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    builder1.setNegativeButton(
                                            "NO",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                } else {
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };
        mFoodList.setAdapter(FBRA);
        FBRA.notifyDataSetChanged();
    }

    private void getMealMustTry(final String food_key, final FoodViewHolder viewHolder) {
        mDatabase.child(food_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String mustTry = (String) dataSnapshot.child("Meal_Recommended").getValue();
                    if(mustTry.equals("true")){
                        viewHolder.img_must_try.setVisibility(View.VISIBLE);
                    }

                    if(mustTry.equals("false")){
                        viewHolder.img_must_try.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView txtOptionDigit;
        public ImageView img_must_try;

        public FoodViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            img_must_try = (ImageView) itemView.findViewById(R.id.imgMustTry);

            // card_view = (CardView) findViewById(R.id.card_view);
        }

        public void setMeal_Name(String meal_Name){
            TextView food_name = (TextView) mView.findViewById(R.id.foodName);
            food_name.setText(meal_Name);
        }

        public void setMeal_Desc(String meal_Desc){
            TextView food_desc = (TextView) mView.findViewById(R.id.foodDesc);
            food_desc.setText(meal_Desc);
        }

        public void setMeal_Price(String meal_Price){
            TextView food_price = (TextView) mView.findViewById(R.id.foodPrice);
            food_price.setText(meal_Price);
        }

        public void setMeal_Image(Context ctx, String image){
            ImageView food_image = (ImageView) mView.findViewById(R.id.foodImage);
            Picasso.with(ctx).load(image).into(food_image);
        }

        public void setMeal_Category(String meal_Category){
            TextView food_category = (TextView) mView.findViewById(R.id.foodCategory);
            food_category.setText(meal_Category);
        }

        public void setMeal_Availability(String meal_Availability){
            TextView food_availability = (TextView) mView.findViewById(R.id.foodAvailability);

            if(meal_Availability.equals("true"))
                food_availability.setText("");
            else
                food_availability.setText("NOT AVAILABLE");

        }

        /* public void setMeal_Recommended(String availability){
         *//*TextView food_availability = (TextView) mView.findViewById(R.id.foodAvailability);

            if(availability.equals("true"))
                food_availability.setText("");
            else
                food_availability.setText("NOT AVAILABLE");*//*

        }*/
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MenuActivity_Crew.this, MainActivity_Crew.class);
        startActivity(intent);
        finish();
    }
}
