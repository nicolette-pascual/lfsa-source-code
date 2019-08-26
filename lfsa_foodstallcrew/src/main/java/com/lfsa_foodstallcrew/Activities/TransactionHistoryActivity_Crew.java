package com.lfsa_foodstallcrew.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.lfsa_foodstallcrew.Activities.LoginActivities.LoginActivity_Crew;
import com.lfsa_foodstallcrew.GettersSetters.OrderCrew;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TransactionHistoryActivity_Crew extends AppCompatActivity {

    private DatabaseReference mDatabase, databaseReference, mGetDate;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String dbName, dbPrice, dbQuantity,foodStall, order_key, venue, bulkOrder, date = "", uid, user_name, customerName;
    Integer sumofTwo = 0, sumOfAll = 0;

    RecyclerView mOrderList;
    ImageView imageFoodStall;
    FirebaseUser user;
    String foodstallImage_URL;

    private Context globalContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_crew);

        setTitle("Transaction History");

        foodStall = getIntent().getStringExtra("FOODSTALL");
        this.setTitle(foodStall);

        mOrderList = (RecyclerView) findViewById(R.id.pending_list_view);
        mOrderList.setHasFixedSize(true);
        mOrderList.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase= databaseReference.child("Orders");
        mDatabase.keepSynced(true);

        imageFoodStall = (ImageView) findViewById(R.id.img_logo);

        mAuth= FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(TransactionHistoryActivity_Crew.this, LoginActivity_Crew.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };

        //Picasso.with(TransactionHistoryActivity_Crew.this).load(R.drawable.tempura_sam_logo).into(imageFoodStall);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        databaseReference.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_name = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                foodstallImage_URL = dataSnapshot.child("FoodStalls").child(user_name).child("FoodStall_Image").getValue(String.class);
                setImage();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TransactionHistoryActivity_Crew.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<OrderCrew, OrderViewHolder> FBRA = new FirebaseRecyclerAdapter<OrderCrew, OrderViewHolder>
                (OrderCrew.class, R.layout.singletransitem_crew, OrderViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(foodStall)) {
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final OrderCrew model, int position) {

                order_key = getRef(position).getKey().toString();
                mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("BulkOrder_Venue")){
                            //String status_bulk = (String) dataSnapshot.child("Order_Status").getValue();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String uid = model.getUser_ID();
                                        customerName = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                                        viewHolder.setCustomerName(customerName);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        finish();
                                        Intent intent = new Intent(globalContext, TransactionHistoryActivity_Crew.class);
                                        startActivity(intent);
                                    }
                                });

                                viewHolder.setName("Bulk Order");
                                viewHolder.setTime(model.getBulkOrder_Time());
                                viewHolder.addDetailsButton();
                                viewHolder.setStatus(model.getBulkOrder_Status());
                                //viewHolder.setTimeOrdered(model.getTimeOrdered());

                                //Check Venue value
                                if (TextUtils.isEmpty(model.getBulkOrder_Venue())) {
                                    venue = "For pickup";

                                } else {
                                    venue = model.getBulkOrder_Venue();
                                }

                                //Name string to array
                                dbName = model.getBulkOrder_Name();
                                final List<String> foodNamesList = new ArrayList<String>(Arrays.asList(dbName.split(", ")));

                                //Quantity string to array
                                dbQuantity = model.getBulkOrder_Quantity();
                                final List<String> foodQuantitiesList = new ArrayList<String>(Arrays.asList(dbQuantity.split(", ")));

                                //Price string to array
                                dbPrice = model.getBulkOrder_Price();
                                final List<String> foodPricesList = new ArrayList<String>(Arrays.asList(dbPrice.split(", ")));

                                //get Total price of bulk order

                                for (int i = 0; i <= foodPricesList.size() - 1; i++) {
                                    sumofTwo = Integer.parseInt(foodPricesList.get(i)) * Integer.parseInt(foodQuantitiesList.get(i));
                                    sumOfAll += sumofTwo;
                                }

                                String Total = String.valueOf(sumOfAll);
                                viewHolder.setPrice("Php " + Total + ".00");

                                viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (TextUtils.isEmpty(model.getBulkOrder_Venue())) {
                                            venue = "For pickup";

                                        } else {
                                            venue = model.getBulkOrder_Venue();
                                        }

                                        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
                                        date = sfd.format(new Date(model.getBulkOrder_DeliveryDate()));
                                        bulkOrder = "Delivery Date: " + date + "\nVenue: " + venue + "\nOrders: ";

                                        for (int i = 0; i <= foodNamesList.size() - 1; i++) {
                                            bulkOrder += "\n     " + foodNamesList.get(i) + ": Php " + foodPricesList.get(i) + ".00 x" + foodQuantitiesList.get(i);
                                        }

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(TransactionHistoryActivity_Crew.this);
                                        builder1.setTitle("Bulk Order Details");
                                        builder1.setMessage(bulkOrder.trim());
                                        builder1.setCancelable(true);

                                        builder1.setPositiveButton(
                                                "Okay",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });

                                        bulkOrder = "";
                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }
                                });
                                sumofTwo = 0;
                                sumOfAll = 0;
                        }else{
                            String status_db = (String) dataSnapshot.child("Order_Status").getValue();
                            if(status_db.equals("Finished")){

                                databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            String uid = model.getUser_ID();
                                            customerName = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                                            viewHolder.setCustomerName(customerName);
                                        } catch (Exception e) {
                                            finish();
                                            Intent intent = new Intent(globalContext, TransactionHistoryActivity_Crew.class);
                                            startActivity(intent);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(globalContext, "Network Error ", Toast.LENGTH_SHORT).show();
                                    }
                                }));
                                //mDatabase.child(order_key).child("status").setValue("Accepted");

                                //viewHolder.setFoodStall(model.getFoodStall());
                                viewHolder.setName(model.getOrder_Name());
//                                viewHolder.setNote(model.getOrder_Note());
                                viewHolder.setTotal(model.getTotal());
                                viewHolder.setQuantity(model.getOrder_Quantity());
                                viewHolder.setStatus(model.getOrder_Status());
                                viewHolder.setTime(model.getOrder_Time());
                                //viewHolder.setCustomerName(model.get());
                                //viewHolder.setNote(model.getNote());
                            }

                            else {
                                viewHolder.relativeLayout.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        mOrderList.setAdapter(FBRA);
        FBRA.notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CardView card_view;
        //public TextView txtEmail;
        public TextView txtCustomerName, txtMealName, txtPrice, txtQuan, txtStatus, txtVenue, textView_Quantity2;
        public Button btnDetails;
        public String date = "";
        public RelativeLayout relativeLayout;

        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.cardView);
            btnDetails = (Button) itemView.findViewById(R.id.btnDetails);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative);
        }

        public void addDetailsButton(){
            textView_Quantity2 = (TextView) itemView.findViewById(R.id.textView_Quantity2);
            txtQuan = txtQuan = mView.findViewById(R.id.txtQuan);

            textView_Quantity2.setVisibility(View.INVISIBLE);
            txtQuan.setVisibility(View.INVISIBLE);
            btnDetails.setVisibility(View.VISIBLE);

        }

        public void setCustomerName(String customerName) {
            txtCustomerName = mView.findViewById(R.id.txtCustomerName);
            txtCustomerName.setText(customerName);
        }

        public void setName(String name) {
            txtMealName = mView.findViewById(R.id.txtMealName);
            txtMealName.setText(name);
        }

        public void setTotal(Double total) {
            txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("Php %.2f", total));
        }

        public void setPrice(String price) {
            txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText(price);
        }

        public void setQuantity(String quantity) {
            txtQuan = mView.findViewById(R.id.txtQuan);
            txtQuan.setText(quantity);
        }

        public void setNote(String note) {
            //TextView txtNote = mView.findViewById(R.id.txtNote);
            //txtNote.setText(note);
        }

        public void setTime(Long time) {
            TextView txtTime = mView.findViewById(R.id.txtTime);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            txtTime.setText( sfd.format(new Date(time)));
        }

        public void setStatus(String status) {
            txtStatus = mView.findViewById(R.id.txtStatus);
            txtStatus.setText(status);
        }

        public void setVenue(String venue){
            txtVenue = mView.findViewById(R.id.txtVenue);
            txtVenue.setText(venue);
        }

        /*public void setDeliveryDate(Long deliveryDate){
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
            date = sfd.format(new Date(deliveryDate));
        }*/
    }

    public void setImage() {
        switch (user_name){
            case "Jefcee's":
                Picasso.with(TransactionHistoryActivity_Crew.this).load(foodstallImage_URL).into(imageFoodStall);
                break;
            case "S-coop-s":
                Picasso.with(TransactionHistoryActivity_Crew.this).load(foodstallImage_URL).into(imageFoodStall);
                break;
            case "Tempura Sam":
                Picasso.with(TransactionHistoryActivity_Crew.this).load(foodstallImage_URL).into(imageFoodStall);
                break;
        }
    }

    public void setBulkOrder_DeliveryDate(Long deliveryDate){
        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        date = sfd.format(new Date(deliveryDate));
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(TransactionHistoryActivity_Crew.this, MainActivity_Crew.class);
        startActivity(intent);
    }
}
