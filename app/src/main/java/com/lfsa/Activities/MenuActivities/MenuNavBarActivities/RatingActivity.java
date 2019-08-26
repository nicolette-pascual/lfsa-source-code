package com.lfsa.Activities.MenuActivities.MenuNavBarActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.Activities.MenuActivities.MenuActivity;
import com.lfsa.GettersSetters.Users;
import com.lfsa.MainActivity;
import com.lfsa.R;

import java.util.HashMap;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {
    public RatingBar mRatingBar;
    public TextView mRatingScale, textView2;
    public EditText mFeedback, mTitle;
    public Button mSendFeedback;
    public String chosenFoodStall, user_id, username;
    com.google.firebase.auth.FirebaseAuth auth;
    DatabaseReference userRef, ratingRef, orderRef;
    String reviewKey, uid, user_firstname, user_lastname, orders = "withoutOrders";
    FirebaseUser user;
    boolean reviewExists = false;


    private DatabaseReference databaseReference;
    Query query;
    Users users = new Users();
    public String customerId, customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        chosenFoodStall = extras.getString("chosenFoodStall");
        username = extras.getString("customer");
        user_id = extras.getString("customerId");


        this.setTitle("Write a Review");

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        mFeedback = (EditText) findViewById(R.id.etFeedback);
        mSendFeedback = (Button) findViewById(R.id.btnSubmit);
        mTitle = (EditText) findViewById(R.id.etTitle);
        textView2 = findViewById(R.id.textView2);

        //user = FirebaseAuth.getInstance().getCurrentUser();
        //uid = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        //query = databaseReference.child("TempOrders").orderByChild("customerId").equalTo(uid);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }else{
            startActivity(new Intent(RatingActivity.this, LoginActivity.class));
            finish();
        }

        databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_name = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);

                customerId = uid;
                users.setCustomer(user_name);
                users.setCustomerId(customerId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RatingActivity.this, "Network Error ", Toast.LENGTH_SHORT).show();
            }
        }));

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Needs improvement");
                        break;
                    case 2:
                        mRatingScale.setText("Okay");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Excellent. I love it!");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }

        });

        orderRef = FirebaseDatabase.getInstance().getReference("Orders");
        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");
        mSendFeedback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    writeRating();

            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

        //mRatingBar.setEnabled(false);
        checkUser();
        getRating();
    }

    private void getRating() {
        ratingRef.orderByChild("User_ID").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String foodStall = (String) data.child("Foodstall_Name").getValue();

                        if(foodStall.equals(chosenFoodStall)){
                            mSendFeedback.setText("EDIT FEEDBACK");

                            reviewKey = (String) data.getKey();
                            String title = (String) data.child("Rating_Title").getValue();
                            String feedBack = (String) data.child("Rating_Note").getValue();
                            String sRating = (String) data.child("Rating_Score").getValue();

                            reviewExists = true;

                            fillRating(title, feedBack, sRating);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fillRating(String title, String feedBack, String sRating) {
        mRatingBar.setRating(Float.parseFloat(sRating)); //AYAW MAGLOAD?????
        mTitle.setText(title);
        mFeedback.setText(feedBack);
    }

    private void editRating(String sRating, String feedBack, String title) {
        ratingRef.child(reviewKey).child("Rating_Score").setValue(sRating);
        ratingRef.child(reviewKey).child("Rating_Note").setValue(feedBack);
        ratingRef.child(reviewKey).child("Rating_Title").setValue(title);
        ratingRef.child(reviewKey).child("Rating_DateDate").setValue(ServerValue.TIMESTAMP);
    }

    private void writeRating() {
        if (mFeedback.getText().toString().isEmpty() || mTitle.getText().toString().isEmpty()) {
            Toast.makeText(RatingActivity.this, "Please fill in the text box", Toast.LENGTH_LONG).show();
        } else {

            Float rating = mRatingBar.getRating();
            String sRating = Float.toString(rating);
            String feedBack = mFeedback.getText().toString();
            String stallName = chosenFoodStall;
            String title = mTitle.getText().toString();

            if(reviewExists){
                editRating(sRating, feedBack, title);
            }else{

                addRating(sRating, feedBack, title, stallName);
            }

            Toast.makeText(RatingActivity.this, "Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();
            //Intent newActivity = new Intent(RatingActivity.this, MenuActivity.class);
            //newActivity.putExtra("chosenFoodStall", chosenFoodStall);
            //startActivity(newActivity);
        }
    }

    private void addRating(String sRating, String feedBack, String title, String stallName) {

        if(user != null){
            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Rating").push();
            Map newPost = new HashMap();
            newPost.put("Rating_Score", sRating);
            newPost.put("Rating_Note", feedBack);
            newPost.put("Rating_Title", title);
            newPost.put("Foodstall_Name", stallName);
            newPost.put("User_ID", uid);
            newPost.put("Rating_Date", ServerValue.TIMESTAMP);

            current_user_db.setValue(newPost);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(RatingActivity.this, "Network Connection Error. Please Try Again.", Toast.LENGTH_LONG).show();
        }

    }

    private void checkUser() {
        orderRef.orderByChild("User_ID").equalTo(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String foodStall = (String) data.child("Foodstall_Name").getValue();
                        String status = (String) data.child("Order_Status").getValue();
                        if(foodStall.equals(chosenFoodStall)){
                            if(status.equals("Finished")){
                                orders = "withOrder";
                            }
                            enableUserToRate();
                        }

                        else{
                            disableReview();
                        }
                    }
                }else{
                    disableReview();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void enableUserToRate() {

        if(orders.equals("withOrder")) {
            enableReview();
        }
        else disableReview();

    }

    private void enableReview() {
        mRatingBar.setEnabled(true);
        mTitle.setEnabled(true);
        textView2.setText(getResources().getString(R.string.tv_rating_weHope));
        mRatingBar.setRating(0);
        mRatingScale.setText("");
        mFeedback.setEnabled(true);
        mSendFeedback.setEnabled(true);
    }

    private void disableReview() {
        mRatingBar.setEnabled(false);
        mTitle.setEnabled(false);
        textView2.setText(getResources().getString(R.string.tv_rating_youNeed));
        mRatingBar.setRating(0);
        mRatingScale.setText("");
        mFeedback.setEnabled(false);
        mSendFeedback.setEnabled(false);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(RatingActivity.this,MenuActivity.class);
        intent.putExtra("chosenFoodStall", chosenFoodStall);
        intent.putExtra("customer", customer);
        intent.putExtra("customerId", uid);
        startActivity(intent);
        finish();

    }

}
