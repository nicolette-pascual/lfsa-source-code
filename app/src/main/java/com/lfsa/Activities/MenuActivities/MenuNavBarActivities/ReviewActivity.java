package com.lfsa.Activities.MenuActivities.MenuNavBarActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.Activities.MenuActivities.MenuActivity;
import com.lfsa.GettersSetters.Review;
import com.lfsa.GettersSetters.Users;
import com.lfsa.R;
import com.lfsa.Activities.MenuActivities.SingleFoodActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewActivity extends AppCompatActivity{

    private RecyclerView mReviewList;
    private FirebaseRecyclerAdapter mAdapter;
    public DatabaseReference mDatabase, databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String currentFoodStall;
    private String chosenFoodStall, customer, customerId;
    private String food_image;
    public RatingBar mRatingBar;
    public ImageView post_image;
    String id, dbcurrentRating;
    Float totalRating = 0.0f, currentRating, numberOfRatings = 0.0f;

    FirebaseUser user;
    Query query;
    String uid;
    Users users = new Users();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Bundle extras = getIntent().getExtras();
        chosenFoodStall = extras.getString("chosenFoodStall");
        customer = extras.getString("customer");
        customerId = extras.getString("customerId");

        ReviewActivity.this.setTitle(chosenFoodStall);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabase=databaseReference.child("Rating");
        mDatabase.keepSynced(true);

        mReviewList = (RecyclerView) findViewById(R.id.reviewList);
        mReviewList.setHasFixedSize(true);
        mReviewList.setLayoutManager(new LinearLayoutManager(this));

        query = databaseReference.child("TempOrders").orderByChild("customerId").equalTo(uid);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }else{
            startActivity(new Intent(ReviewActivity.this, LoginActivity.class));
            finish();
        }

        databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                customerId = uid;
                users.setCustomerId(customerId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReviewActivity.this, "Network Error ", Toast.LENGTH_SHORT).show();
            }
        }));

        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(ReviewActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };

        post_image = (ImageView) findViewById(R.id.post_image);
        loadimage();

    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Review, ReviewViewHolder> FBRA_review = new FirebaseRecyclerAdapter<Review, ReviewViewHolder>
                (Review.class, R.layout.singlereviewitem, ReviewViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(chosenFoodStall)) {
            @Override
            protected void populateViewHolder(final ReviewViewHolder viewHolder, final Review model, int position) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ratingUID = dataSnapshot.child("Users").child(model.getUser_ID()).child("Name").getValue(String.class);
                        viewHolder.user_name.setText(ratingUID);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setRating_Note(model.getRating_Note());
                viewHolder.setRating_Score(model.getRating_Score());
                viewHolder.setRating_Title(model.getRating_Title());
                viewHolder.setRating_Date(model.getRating_Date());
            }
        };

        mReviewList.setAdapter(FBRA_review);
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public CardView card_view;
        public TextView user_name;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            card_view = (CardView) itemView.findViewById(R.id.cardView);
            user_name = (TextView) itemView.findViewById(R.id.userName);
        }

        public void setRating_Score(Float rating_Score) {
            RatingBar feed_back = (RatingBar) itemView.findViewById(R.id.ratingBar);
            feed_back.setRating(rating_Score);
        }

        public void setRating_Note(String rating_Note) {
            TextView feed_back = (TextView) itemView.findViewById(R.id.feedBack);
            feed_back.setText(rating_Note);
        }

        public void setRating_Title(String rating_Title) {
            TextView Title = (TextView) itemView.findViewById(R.id.title);
            Title.setText(rating_Title);
        }


        public void setRating_Date(Long rating_Date) {
            TextView dateTime = (TextView) itemView.findViewById(R.id.date);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
            dateTime.setText( sfd.format(new Date(rating_Date)));
        }

    }

    public void loadimage(){
        databaseReference.child("FoodStalls").orderByChild("FoodStall_Name").equalTo(chosenFoodStall).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                    food_image = (String) childSnapshot.child("FoodStall_Image").getValue();
                }
                Picasso.with(ReviewActivity.this).load(food_image).into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReviewActivity.this, "Network Error ", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ReviewActivity.this,MenuActivity.class);
        intent.putExtra("chosenFoodStall", chosenFoodStall);
        intent.putExtra("customer", customer);
        intent.putExtra("customerId", uid);
        startActivity(intent);
        finish();

    }

}
