package com.lfsa.Activities.MenuActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.Activities.MenuActivities.MenuNavBarActivities.BulkOrderActivity;
import com.lfsa.GettersSetters.Food;
import com.lfsa.MainActivity;
import com.lfsa.R;
import com.lfsa.Activities.MenuActivities.MenuNavBarActivities.RatingActivity;
import com.lfsa.Activities.MenuActivities.MenuNavBarActivities.ReviewActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mFoodList;
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mDatabase, databaseReference, mGetDate, mGetServingHours;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String chosenFoodStall, customer, customerId;
    private String food_image;
    public RatingBar mRatingBar;
    public ImageView post_image;
    private TextView txt_foodStallName, txtSched;

    String id, dbcurrentRating, dbSched = "";
    Float totalRating = 0.0f, currentRating, numberOfRatings = 0.0f;

    FirebaseUser user;
    String manualHours, mealAvailability, mealMustTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //------------------------------------------------------------------------------------------------------------
        Bundle extras = getIntent().getExtras();
        chosenFoodStall = extras.getString("chosenFoodStall");
        customer = extras.getString("customer");
        customerId = extras.getString("customerId");

        MenuActivity.this.setTitle(chosenFoodStall);

        View navHeaderView = navigationView.getHeaderView(0);
        txt_foodStallName = (TextView) navHeaderView.findViewById(R.id.textView_foodStallname);
        txt_foodStallName.setText(chosenFoodStall);
        txtSched = findViewById(R.id.txtSched);
        //======================================

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabase = databaseReference.child("Meals");
        mDatabase.keepSynced(true);
        mGetDate = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
//        mGetDate.keepSynced(true);
        mGetServingHours = databaseReference.child("FoodStalls").child(chosenFoodStall).child("FoodStall_Hour");
        mGetServingHours.keepSynced(true);

        //======================================
        mFoodList = (RecyclerView) findViewById(R.id.foodList);
        mFoodList.setHasFixedSize(true);
        mFoodList.setLayoutManager(new LinearLayoutManager(this));

        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MenuActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingBar.setFocusable(false);

        mGetDate = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");

        post_image = (ImageView) findViewById(R.id.post_image);
        loadimage();
        checkSchedule();


    }

    private void checkSchedule() {
        mGetServingHours.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                manualHours = (String) dataSnapshot.getValue();
                dbSched = manualHours;

                //dbSched = "Open";

                if(manualHours.equals("Close")){
                    txtSched.setVisibility(View.VISIBLE);
                }else{
                    txtSched.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mGetDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Calendar calendar = GregorianCalendar.getInstance();
                Long time = dataSnapshot.getValue(Long.class);
                calendar.setTimeInMillis(System.currentTimeMillis() + time);

                System.out.println("Current Day:  " + calendar.get(Calendar.DAY_OF_WEEK)); //calendar.get(Calendar.HOUR_OF_DAY);
                System.out.println("Current Time:  " + calendar.get(Calendar.HOUR_OF_DAY));

                int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
                int currentTime = calendar.get(Calendar.HOUR_OF_DAY);

                if(dbSched.equals("Open")){
                    switch(currentDay){
                        case 0:
                            System.out.println("Error");
                        case 1: //Sunday
                            dbSched = "Close";
                            break;
                        case 7: //Saturday currentTime < 12 && currentTime >= 8
                            if(currentTime < 12 && currentTime >= 8){
                                dbSched = "Open";
                            }else{
                                dbSched = "Close";
                            }
                            break;
                        default: //Weekdays
                            if(currentTime < 17 && currentTime >= 8){
                                dbSched = "Open";
                            } else{
                                dbSched = "Close";
                            }
                            break;
                    }
                }else{
                    dbSched = "Close";
                }

                if(dbSched.equals("Close")){
                    txtSched.setVisibility(View.VISIBLE);
                }else{
                    txtSched.setVisibility(View.GONE);
                }


                try{
                    txtSched.setVisibility(View.GONE);
                    mGetServingHours.setValue(dbSched);

                }catch(Exception e){
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        numberOfRatings = 0.0f;
        totalRating=0.0f;
        getRating();

        FirebaseRecyclerAdapter <Food, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<Food, FoodViewHolder>
                (Food.class, R.layout.singlemenuitem, FoodViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(chosenFoodStall)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, int position) {
                viewHolder.setMeal_Name(model.getMeal_Name());
                viewHolder.setMeal_Price(model.getMeal_Price());
                viewHolder.setMeal_Desc(model.getMeal_Desc());
                viewHolder.setMeal_Category(model.getMeal_Category());
                viewHolder.setMeal_Image(getApplicationContext(),model.getMeal_Image());
                viewHolder.setMeal_Availability(model.getMeal_Availability());
                mealAvailability = model.getMeal_Availability();
                final String food_key = getRef(position).getKey();

                getMealMustTry(food_key, viewHolder);
                //============================
                if(txtSched.getVisibility() != View.VISIBLE){
                    if( mealAvailability.matches("true")){

                        viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent singleFoodActivity = new Intent(MenuActivity.this, SingleFoodActivity.class);
                                singleFoodActivity.putExtra("chosenFoodStall", chosenFoodStall);
                                singleFoodActivity.putExtra("FoodId", food_key);
                                singleFoodActivity.putExtra("customer", customer);
                                singleFoodActivity.putExtra("customerId", customerId);
                                startActivity(singleFoodActivity);
                            }
                        });
                }
                    else {
                        viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MenuActivity.this, "The meal is currently not available.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else{
                    viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MenuActivity.this, chosenFoodStall + " is currently closed right now. Try again later." , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        mFoodList.setAdapter(FBRA);
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
        public CardView card_view;
        public ImageView img_must_try;


        public FoodViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.card_view);
            img_must_try = (ImageView) itemView.findViewById(R.id.imgMustTry);
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
            food_price.setText("Php. "+meal_Price+".00");
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

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_seeReview) {
            Intent intent = new Intent(MenuActivity.this,ReviewActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_writeReview) {
            Intent intent = new Intent(MenuActivity.this,RatingActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_bulkOrder) {
            Intent intent = new Intent(MenuActivity.this,BulkOrderActivity.class);
            intent.putExtra("chosenFoodStall", chosenFoodStall);
            intent.putExtra("customer", customer);
            intent.putExtra("customerId", customerId);
            startActivity(intent);
            finish();
        }else if(id == R.id.nav_home){
            Intent newActivity = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(newActivity);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getRating() {
        databaseReference.child("Rating").orderByChild("Foodstall_Name").equalTo(chosenFoodStall).addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    id = childSnapshot.getKey();
                    dbcurrentRating = dataSnapshot.child(id).child("Rating_Score").getValue(String.class);
                    currentRating = Float.parseFloat(dbcurrentRating);
                    numberOfRatings = numberOfRatings + 1.0f;
                    totalRating = (currentRating + totalRating);

                }

                totalRating /= numberOfRatings;
                mRatingBar.setRating(totalRating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MenuActivity.this, "Network Error ", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void loadimage(){
        databaseReference.child("FoodStalls").orderByChild("FoodStall_Name").equalTo(chosenFoodStall).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                    food_image = (String) childSnapshot.child("FoodStall_Image").getValue();
                }

                Picasso.with(MenuActivity.this).load(food_image).into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MenuActivity.this, "Network Error ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
