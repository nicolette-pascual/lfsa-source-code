package com.lfsa.Activities.MenuActivities.MenuNavBarActivities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.Activities.MenuActivities.MenuActivity;
import com.lfsa.GettersSetters.BulkOrder;
import com.lfsa.GettersSetters.Food;
import com.lfsa.GettersSetters.Users;
import com.lfsa.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BulkOrderActivity extends AppCompatActivity {

    private RecyclerView mFoodList;
    private DatabaseReference mDatabase, databaseReference;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private String chosenFoodStall, chosenDate = "", chosenMeal,dbQuantity, dbPrice, food_key, food_price, dbMeal,venue, result, username, dateToday;
    private Integer day, month, year, dateT, dateC, totalQuantity = 0;
    public ImageView post_image;
    private EditText date, input, etVenue;
    private Long finalChosenDate;
    private Calendar mCurrentDate;
    private ArrayList<String> quantityArray = new ArrayList<String>();
    private ArrayList<String> mealArray = new ArrayList<String>();
    private ArrayList<String> priceArray = new ArrayList<String>();
    private ArrayList<Integer> intQuantityArray = new ArrayList<>();
    private SimpleDateFormat df;

    FirebaseUser user;
    Query query;
    String uid;
    public String customerId;
    Users users = new Users();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_order);

        Bundle extras = getIntent().getExtras();
        chosenFoodStall = extras.getString("chosenFoodStall");
        username = extras.getString("customer");

        BulkOrderActivity.this.setTitle(chosenFoodStall);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabase=databaseReference.child("Meals");
        mDatabase.keepSynced(true);

        mFoodList = (RecyclerView) findViewById(R.id.foodList);
        mFoodList.setHasFixedSize(true);
        mFoodList.setLayoutManager(new LinearLayoutManager(this));

        date = (EditText) findViewById(R.id.txt_date);
        etVenue = (EditText) findViewById(R.id.txt_place);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }else{
            startActivity(new Intent(BulkOrderActivity.this, LoginActivity.class));
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
                Toast.makeText(BulkOrderActivity.this, "Network Error ", Toast.LENGTH_SHORT).show();
            }
        }));

        //GET DATE
        mCurrentDate = Calendar.getInstance();

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        df = new SimpleDateFormat("yyyyMMdd");
        dateToday = df.format(mCurrentDate.getInstance().getTime());

        date.setText(day+"/"+month+"/"+year);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(BulkOrderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        //long fuck = Long.parseLong(chosenDate);

                        //long dateTime = datePickerDialog.getDate();
                        // Date date = new Date(dateTime);

                        mCurrentDate.set(year, month, dayOfMonth);
                        String dateString = df.format(mCurrentDate.getTime());
                        dateC = Integer.valueOf(dateString);
                        dateT = Integer.valueOf(dateToday);

                        if(dateT > dateC) {
                            Toast.makeText(getApplicationContext(), "Invalid date. " , Toast.LENGTH_SHORT).show();
                        }

                        else {
                            chosenDate = (dayOfMonth+"/" + (month+1) +"/"+year);
                            date.setText(dayOfMonth+"/"+ (month+1) +"/"+year);
                        }
                    }
                }, year, month, day);
                datePickerDialog.show();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }


        });



        //==========================================================

        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(BulkOrderActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter <Food, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<Food, FoodViewHolder>
                (Food.class, R.layout.singlebulkitem, FoodViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(chosenFoodStall)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.setName(model.getMeal_Name());
                viewHolder.setImage(getApplicationContext(),model.getMeal_Image());
                viewHolder.setDesc(model.getMeal_Desc());
                viewHolder.setPrice(model.getMeal_Price());

                viewHolder.imgPlus.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        food_key = getRef(position).getKey();
                        mDatabase.child(food_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    food_price = (String) dataSnapshot.child("Meal_Price").getValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        AlertDialog.Builder builder = new AlertDialog.Builder(BulkOrderActivity.this);
                        builder.setTitle("Bulk Order");
                        builder.setMessage("Enter meal quantity:");

                        input = new EditText(BulkOrderActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
                        builder.setView(input);

                        //POSITIVE BUTTON
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try{
                                    food_key = getRef(position).getKey();
                                    chosenMeal = viewHolder.food_name.getText().toString();
                                    String txtquantity = input.getText().toString();

                                    if (!txtquantity.matches("") && Integer.parseInt(txtquantity) != 0 && !mealArray.contains(chosenMeal)) {
                                        if(Integer.parseInt(txtquantity) <= 100 && Integer.parseInt(txtquantity) > 0){
                                            quantityArray.add(txtquantity);
                                            mealArray.add(chosenMeal);
                                            priceArray.add(food_price);
                                            intQuantityArray.add(Integer.parseInt(txtquantity));
                                            result = TextUtils.join(", ", quantityArray);
                                            Toast.makeText(BulkOrderActivity.this, "Added " + chosenMeal + " to your bulk order.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(BulkOrderActivity.this, "Maximum quantity per meal is 100. (Please do not input trailing zeros as quantity as well.)", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                    } else if (!txtquantity.matches("") && !txtquantity.matches("0") && mealArray.contains(chosenMeal) && Integer.parseInt(txtquantity) > 0) {
                                        int i = mealArray.indexOf(chosenMeal);

                                        if(Integer.parseInt(txtquantity) <= 100){
                                            quantityArray.set(i, txtquantity);
                                            priceArray.set(i, food_price);
                                            intQuantityArray.set(i, Integer.parseInt(txtquantity));
                                            Toast.makeText(BulkOrderActivity.this, "Replaced quantity of " + chosenMeal + ".", Toast.LENGTH_SHORT).show();
//                                          Toast.makeText(BulkOrder.this, "chosenMeal: " + mealArray + "\nmealQuantity: " + quantityArray, Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(BulkOrderActivity.this, "Maximum quantity per meal is 100. (Please do not input trailing zeros as quantity as well.)", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                    } else if (txtquantity.matches("0") && mealArray.contains(chosenMeal)) {
                                        int i = mealArray.indexOf(chosenMeal);
                                        mealArray.remove(i);
                                        quantityArray.remove(i);
                                        priceArray.remove(i);
                                        intQuantityArray.remove(i);
                                        Toast.makeText(BulkOrderActivity.this, "Removed " + chosenMeal + " from your bulk order.", Toast.LENGTH_SHORT).show();
                                        // Toast.makeText(BulkOrderActivity.this, i + "\n" + mealArray + "\n" + quantityArray + "\n" + priceArray, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(BulkOrderActivity.this, "You have entered an invalid quantity.", Toast.LENGTH_SHORT).show();

                                    }

                                    InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(input.getWindowToken(), 0);

                                }catch(Exception e){
                                    Toast.makeText(BulkOrderActivity.this, "An error occurred. Are you sure your inputs are correct?", Toast.LENGTH_SHORT).show();
                                }
                                }


                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                im.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                dialog.cancel();
                            }
                        });
                        final AlertDialog ad = builder.create();
                        ad.show(); //shows alertdialog

                    }
                });
            }
        };
        mFoodList.setAdapter(FBRA);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CardView card_view;
        ImageView food_image;
        TextView food_name, food_price, food_desc;
        public ImageButton imgPlus;


        public FoodViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.card_view);
            imgPlus = (ImageButton) itemView.findViewById(R.id.ib_plus);
        }

        public void setName(String name){
            food_name = (TextView) mView.findViewById(R.id.foodName);
            food_name.setText(name);
        }
        public void setImage(Context ctx, String image){
            food_image = (ImageView) mView.findViewById(R.id.foodImage);
            Picasso.with(ctx).load(image).into(food_image);
        }

        public void setPrice(String price){
            food_price = (TextView) mView.findViewById(R.id.foodPrice);
            food_price.setText("Php " + price);
        }

        public void setDesc(String desc){
            food_desc = (TextView) mView.findViewById(R.id.foodDesc);
            food_desc.setText(desc);
        }

    }

    public void btnSubmitClicked (View v) {

        venue = etVenue.getText().toString();
        
        if(TextUtils.isEmpty(chosenDate)){
            Toast.makeText(BulkOrderActivity.this, "You haven't picked a delivery date yet. Please pick a date.", Toast.LENGTH_SHORT).show();
        }
        
        else if(mealArray.isEmpty()){
            Toast.makeText(BulkOrderActivity.this, "You have not ordered anything.", Toast.LENGTH_SHORT).show();
        }else {

            String finalBulkOrder = "Please double check your bulk order. You cannot edit your order once submitted.\n\n";
            finalBulkOrder += "Delivery Date: " + chosenDate.replaceAll(" ", "/") + "\n";


            if(TextUtils.isEmpty(venue)){
                finalBulkOrder += "Venue: For Pickup\n";
            }
            else{
                finalBulkOrder += "Venue: " + venue + "\n";
            }

            finalBulkOrder += "Orders:\n";
            for (int i = 0; i <= mealArray.size() - 1; i++) {
                finalBulkOrder += "    " + mealArray.get(i) + ": Php" + priceArray.get(i) + ".00 x " + Integer.parseInt(quantityArray.get(i)) +"\n";
            }

            Integer sumofTwo = 0, totalSum = 0;
            for(int i = 0; i <= priceArray.size() - 1; i++){
                sumofTwo = Integer.parseInt(priceArray.get(i)) * Integer.parseInt(quantityArray.get(i));
                totalSum += sumofTwo;
            }

            finalBulkOrder += "\n\nTotal: Php " + totalSum + ".00";

            AlertDialog.Builder builder = new AlertDialog.Builder(BulkOrderActivity.this);
            builder.setTitle("Verify Bulk Order");
            builder.setMessage(finalBulkOrder.trim());

            //POSITIVE BUTTON
            builder.setPositiveButton("Place Order", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    for(int i = 0; i <= intQuantityArray.size() - 1; i++){
                        totalQuantity+= intQuantityArray.get(i);
                    }

                    if(totalQuantity <= 10){
                        Toast.makeText(BulkOrderActivity.this, "Error. Minimum total quantity is 11. Add more to your order.", Toast.LENGTH_LONG).show();
                        totalQuantity = 0;
                    }else {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = sdf.parse(chosenDate);

                            finalChosenDate = date.getTime();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        dbQuantity = TextUtils.join(", ", quantityArray);
                        dbMeal = TextUtils.join(", ", mealArray);
                        dbPrice = TextUtils.join(", ", priceArray);

                        DatabaseReference bulk_order_db = FirebaseDatabase.getInstance().getReference().child("BulkOrder").push();
                        Map newPost = new HashMap();
                        newPost.put("User_ID", customerId);
                        newPost.put("Foodstall_Name", chosenFoodStall);
                        newPost.put("BulkOrder_Name", dbMeal);
                        newPost.put("BulkOrder_Price", dbPrice);
                        newPost.put("BulkOrder_Quantity", dbQuantity);
                        newPost.put("Order_Status", "Pending");
                        newPost.put("BulkOrder_DeliveryDate", finalChosenDate);
                        newPost.put("BulkOrder_Venue", venue);
                        newPost.put("BulkOrder_Time", ServerValue.TIMESTAMP);
                        newPost.put("Token_ID", users.getToken_id());

                        bulk_order_db.setValue(newPost);
                        Toast.makeText(getApplicationContext(), "Bulk order successfully sent to " + chosenFoodStall + ".", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(BulkOrderActivity.this, MenuActivity.class);
                        intent.putExtra("chosenFoodStall", chosenFoodStall);
                        intent.putExtra("customer", username);
                        intent.putExtra("customerId", uid);
                        startActivity(intent);
                        finish();
                    }

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog ad = builder.create();
            ad.show(); //shows alertdialog
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(BulkOrderActivity.this,MenuActivity.class);
        intent.putExtra("chosenFoodStall", chosenFoodStall);
        intent.putExtra("customer", username);
        intent.putExtra("customerId", uid);
        startActivity(intent);
        finish();

    }

}

