package com.lfsa.Activities.MenuActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.GettersSetters.Users;
import com.lfsa.MainActivity;
import com.lfsa.R;
import com.squareup.picasso.Picasso;

public class SingleFoodActivity extends AppCompatActivity implements View.OnClickListener {

    private String food_key = null, recentQuan, available, foodStall, food_image, customerName, customerId, currentFoodStall, currentFoodKey, foodName;
    String uid;
    private TextView singleFoodTitle, singleFoodDesc, singleFoodPrice, singleAvailablity, txtdateTime;
    private EditText quantity, singleNoteToCrew;
    private ImageView singleFoodImage;
    private Button orderButton;
    private FirebaseAuth mAuth;
    private static final int GALLREQ = 1;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef, mDatabase;
    Query postRef;
    int foodIsOrdered = 0;
    FirebaseUser user;
    Users users = new Users();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_food);

        //GETTING VALUES OF OBJECTS FROM OTHER CLASS
        food_key = getIntent().getExtras().getString("FoodId");
        customerName = getIntent().getExtras().getString("customer");
        customerId = getIntent().getExtras().getString("customerId");
        currentFoodStall = getIntent().getExtras().getString("chosenFoodStall");

        SingleFoodActivity.this.setTitle(currentFoodStall);

        //mGetDate = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Meals");

        singleFoodTitle = (TextView) findViewById(R.id.singleTitle);
        singleFoodDesc = (TextView) findViewById(R.id.singleDesc);
        singleFoodPrice = (TextView) findViewById(R.id.singlePrice);
        singleAvailablity = (TextView) findViewById(R.id.singleAvailability);
        singleFoodImage = (ImageView) findViewById(R.id.singleImageView);
        quantity = (EditText) findViewById(R.id.singleQuantity);
        txtdateTime = (TextView) findViewById(R.id.txtdateTime);
        singleNoteToCrew = (EditText) findViewById(R.id.singleNoteToCrew);
        orderButton = findViewById(R.id.orderButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }else{
            startActivity(new Intent(SingleFoodActivity.this, LoginActivity.class));
            finish();
        }

        orderButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        mRef = FirebaseDatabase.getInstance().getReference("Orders");

        postRef = mRef.orderByChild("User_ID").equalTo(customerId);

        mDatabase.child(food_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String food_name = (String) dataSnapshot.child("Meal_Name").getValue();
                    String food_price = (String) dataSnapshot.child("Meal_Price").getValue();
                    String food_desc = (String) dataSnapshot.child("Meal_Desc").getValue();
                    food_image = (String) dataSnapshot.child("Meal_Image").getValue();
                    String food_availability = (String) dataSnapshot.child("Meal_Availability").getValue();
                    foodStall = (String) dataSnapshot.child("Foodstall_Name").getValue();

                    singleFoodTitle.setText(food_name);
                    singleFoodDesc.setText(food_desc);
                    singleFoodPrice.setText(food_price);

                    if(food_availability.equals("true")) //////
                        singleAvailablity.setVisibility(View.INVISIBLE);
                    else if (food_availability != null)
                        return;
                    else
                        singleAvailablity.setVisibility(View.VISIBLE);
                    Picasso.with(SingleFoodActivity.this).load(food_image).into(singleFoodImage);
                }
                else
                    return;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        final String name_text = singleFoodTitle.getText().toString().trim();
        final String price_text = singleFoodPrice.getText().toString().trim();
        final String quantity_text = quantity.getText().toString().trim();
        final String noteToCrew = singleNoteToCrew.getText().toString().trim();
        final String availability_text = singleAvailablity.getText().toString().trim();
        final String customer = customerName;
        final String customerUid = customerId;

        if(TextUtils.isEmpty(quantity_text)){
            Toast.makeText(this, "Please input a quantity.", Toast.LENGTH_SHORT).show();
        }
        else{
            if(Integer.parseInt(quantity_text) < 10 && Integer.parseInt(quantity_text) != 0 && Integer.parseInt(quantity_text) > 0){
                if(singleAvailablity.getVisibility() == View.VISIBLE){
                    Toast.makeText(this, "Sorry but the item is currently NOT AVAILABLE", Toast.LENGTH_SHORT).show();
                }
                else{
                    mRef.orderByChild("User_ID").equalTo(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                //Toast.makeText(getApplicationContext(), "meron()", Toast.LENGTH_SHORT).show();
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (data.hasChild("Order_Name")) {
                                        foodName = (String) data.child("Order_Name").getValue();
                                        String status = (String) data.child("Order_Status").getValue();
                                    if (foodName.equals(name_text) && status.equals("Pending")) {
                                        currentFoodKey = (String) data.getKey();
                                        recentQuan = (String) data.child("Order_Quantity").getValue();
                                        foodIsOrdered = 1;
                                        editOrder(currentFoodKey, recentQuan);
                                    }
                                }


                            } if (foodIsOrdered != 1) {
                                    addOrder();
                                }

                            }else{
                                addOrder();
                            }
                        }

                        private void editOrder(String currentFood_key, String recentQuan) {
                            int newQuan = Integer.parseInt(quantity_text) + Integer.parseInt(recentQuan);

                            if (newQuan <= 10) {
                                mRef.child(currentFood_key).child("Order_Quantity").setValue(String.valueOf(newQuan));
                                mRef.child(currentFood_key).child("Order_Time").setValue(ServerValue.TIMESTAMP);
                                mRef.child(currentFood_key).child("Order_Note").setValue(noteToCrew);
                                Toast.makeText(getApplicationContext(), "Order has been added to your order list!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SingleFoodActivity.this, MainActivity.class);
                                intent.putExtra("chosenFoodStall", currentFoodStall);
                                intent.putExtra("customer", customer);
                                intent.putExtra("customerId", uid);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), "Your order quantity has exceeded. Please use LFSA's bulk order feature", Toast.LENGTH_SHORT).show();

                            }
                        }

                        private void addOrder() {

                            if(user != null && !users.getToken_id().matches("none")){
                                final DatabaseReference newPost = mRef.push();
                                newPost.child("Order_Name").setValue(name_text);
                                newPost.child("Order_Price").setValue(price_text);
                                newPost.child("Order_Quantity").setValue(String.valueOf(Integer.parseInt(quantity_text)));
                                newPost.child("Order_Time").setValue(ServerValue.TIMESTAMP);
                                newPost.child("Foodstall_Name").setValue(currentFoodStall);
                                newPost.child("Order_Status").setValue("Pending");
                                newPost.child("User_ID").setValue(customerUid);
                                newPost.child("Token_ID").setValue(users.getToken_id());

                                //if()
                                newPost.child("Order_Note").setValue(noteToCrew);
                                Toast.makeText(getApplicationContext(), "Order has been added to your order list!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SingleFoodActivity.this, MainActivity.class);
                                intent.putExtra("chosenFoodStall", currentFoodStall);
                                intent.putExtra("customer", customer);
                                intent.putExtra("customerId", uid);
                                startActivity(intent);
                                finish();
                            }

                            else{
                                Toast.makeText(SingleFoodActivity.this, "Network Connection Error. Please Try Again.", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            else if(Integer.parseInt(quantity_text) == 0 || Integer.parseInt(quantity_text) < 0){
                Toast.makeText(this, "Quantity cannot be zero or less than zero.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please refer to the 'bulk orders' feature of LFSA instead.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}