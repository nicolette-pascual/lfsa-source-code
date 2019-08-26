package com.lfsa_foodstallcrew.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lfsa_foodstallcrew.Activities.LoginActivities.LoginActivity_Crew;
import com.lfsa_foodstallcrew.Activities.ReviewActivity_Crew;
import com.lfsa_foodstallcrew.GettersSetters.BulkOrderCrew;
import com.lfsa_foodstallcrew.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class  BulkOrderFragment_Crew extends Fragment {

    private DatabaseReference mDatabase, databaseReference, mGetDate;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Long longChosenDate;

    String foodStall, order_key;

    RecyclerView mOrderList;

    private Context globalContext = null;
    private String dbName, dbPrice, dbQuantity, venue, bulkOrder, status, customer;


    public BulkOrderFragment_Crew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pending_orders_crew, container, false);
        // Inflate the layout for this fragment
        foodStall = getActivity().getIntent().getExtras().getString("FOODSTALL");

        globalContext = container.getContext();

        mOrderList = (RecyclerView) view.findViewById(R.id.pending_list_view);
        mOrderList.setHasFixedSize(true);
        mOrderList.setLayoutManager(new LinearLayoutManager(container.getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("BulkOrder");
        mDatabase.keepSynced(true);

        user=FirebaseAuth.getInstance().getCurrentUser();

        mAuth= FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(container.getContext(), LoginActivity_Crew.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<BulkOrderCrew, OrderViewHolder> FBRA = new FirebaseRecyclerAdapter<BulkOrderCrew, OrderViewHolder>
                (BulkOrderCrew.class, R.layout.singleorderitem_bulk_crew, OrderViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(foodStall)) {
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final BulkOrderCrew model, final int position) {

                databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        customer = dataSnapshot.child("Users").child(model.getUser_ID()).child("Name").getValue(String.class);
                        viewHolder.user_name.setText(customer);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(globalContext, "Network Error ", Toast.LENGTH_SHORT).show();
                    }
                }));

                viewHolder.setCustomerId(model.getUser_ID());
                //customer name
                viewHolder.setName(model.getBulkOrder_Name());
                viewHolder.setPrice(model.getBulkOrder_Price());
                viewHolder.setTimeOrdered(model.getBulkOrder_Time());
                viewHolder.setStatus(model.getOrder_Status());
                viewHolder.setDeliveryDate(model.getBulkOrder_DeliveryDate());
                order_key = getRef(position).getKey();

                //Check Venue value
                if(TextUtils.isEmpty(model.getBulkOrder_Venue())){
                    viewHolder.editTxtVenue();
                    venue = "For pickup";

                }else{
                    venue = model.getBulkOrder_Venue();
                }
                viewHolder.setVenue(venue);

                //Name string to array
                dbName = model.getBulkOrder_Name();
                final List<String>foodNamesList = new ArrayList<String>(Arrays.asList(dbName.split(", ")));

                //Quantity string to array
                dbQuantity = model.getBulkOrder_Quantity();
                final List<String>foodQuantitiesList = new ArrayList<String>(Arrays.asList(dbQuantity.split(", ")));

                //Price string to array
                dbPrice = model.getBulkOrder_Price();
                final List<String>foodPricesList = new ArrayList<String>(Arrays.asList(dbPrice.split(", ")));

                //get Total price of bulk orderCrew
                Integer sumofTwo = 0, sumOfAll = 0;
                for(int i = 0; i <= foodPricesList.size() - 1; i++){
                    sumofTwo = Integer.parseInt(foodPricesList.get(i)) * Integer.parseInt(foodQuantitiesList.get(i));
                    sumOfAll += sumofTwo;
                }

                Double Total = sumOfAll *1.0;
                viewHolder.setTotal(Total);

                mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = (String) dataSnapshot.child("Order_Status").getValue();
                        if(dataSnapshot.exists()) {
                            if (status.equals("Accepted")) {
                                statusIsAccepted(viewHolder);
                            } else if (status.equals("Pending")) {
                                statusIsPending(viewHolder);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.btnViewOrders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(TextUtils.isEmpty(model.getBulkOrder_Venue())){
                            venue = "For pickup";

                        }else{
                            venue = model.getBulkOrder_Venue();
                        }


                        bulkOrder = "Venue: " + venue + "\n\nOrder/s:";

                        for (int i = 0; i <= foodNamesList.size() - 1; i++) {
                            bulkOrder += "     \n" + foodNamesList.get(i) + ": " + foodQuantitiesList.get(i) + " x Php" + foodPricesList.get(i) + ".00";
                        }

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setTitle("Orders");
                        builder1.setMessage(bulkOrder.trim());
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Okay",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                });

                viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setTitle("Verify to Accept Bulk");
                        builder1.setMessage("Are you sure you want to accept this bulk order?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton("Accept",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        order_key = getRef(position).getKey();
                                        setStatusAccepted(v, order_key, viewHolder);
                                        notifyDataSetChanged();
                                        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()) {
                                                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                                                    if (status.equals("Accepted")) {
                                                        statusIsAccepted(viewHolder);
                                                    } else if (status.equals("Pending")) {
                                                        statusIsPending(viewHolder);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                        builder1.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                });

                viewHolder.btnFinished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setTitle("Verify to Finish Transaction");
                        builder1.setMessage("Verify That Bulk Order Transaction has been Finished.");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton("Transaction finished",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        order_key = getRef(position).getKey();
                                        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                                                    String token_id = (String) dataSnapshot.child("Token_ID").getValue();

                                                    if(status.equals("Accepted")){
                                                        String customerName, name, price, quantity, customerId, venue;
                                                        Long deliveryDate;

                                                        customerId = viewHolder.customer_id.toString();
                                                        name = viewHolder.name_.toString();
                                                        price = viewHolder.price_.toString();
                                                        quantity = model.getBulkOrder_Quantity();
                                                        deliveryDate = viewHolder.deliveryDate_;

                                                        venue = viewHolder.txtVenue.getText().toString();

                                                        if(user != null){
                                                            DatabaseReference accepted_orders_db = FirebaseDatabase.getInstance().getReference().child("Orders").push();
                                                            Map newPost = new HashMap();
                                                            newPost.put("User_ID", customerId);
                                                            newPost.put("Foodstall_Name", foodStall);
                                                            newPost.put("BulkOrder_Name", name);
                                                            newPost.put("BulkOrder_Price", price);
                                                            newPost.put("BulkOrder_Quantity", quantity);
                                                            newPost.put("Order_Status", "Finished");
                                                            newPost.put("BulkOrder_DeliveryDate", deliveryDate);
                                                            newPost.put("BulkOrder_Venue", venue);
                                                            newPost.put("BulkOrder_Time", ServerValue.TIMESTAMP);
                                                            newPost.put("Token_ID", token_id);
                                                            accepted_orders_db.setValue(newPost);

                                                            mDatabase.child(order_key).removeValue();
                                                            notifyDataSetChanged();
                                                            Toast.makeText(globalContext, "Transaction has been completed.", Toast.LENGTH_LONG).show();
                                                        }

                                                        else {
                                                            Toast.makeText(globalContext, "Network Connection Error. Please Try Again.", Toast.LENGTH_LONG).show();
                                                        }



                                                    }else{
                                                        Toast.makeText(globalContext, "Something went wrong with finishing your order. Please report this bug.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else {
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });

                        builder1.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                });

                viewHolder.btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setTitle("Verify to Decline Bulk");
                        builder1.setMessage("Are you sure you want to decline this bulk order?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton("Decline",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        order_key = getRef(position).getKey();
                                        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                                                    String token_id = (String) dataSnapshot.child("Token_ID").getValue();

                                                    if(status.equals("Pending")){
                                                        String customer_name, name, price, quantity, customerId, venue;
                                                        Long deliveryDate;

                                                        customerId = viewHolder.customer_id.toString();
//                                                        customer_name = viewHolder.user_name.getText().toString();
                                                        name = viewHolder.name_.toString();
                                                        price = viewHolder.price_.toString();
                                                        quantity = model.getBulkOrder_Quantity();
                                                        deliveryDate = viewHolder.deliveryDate_;


                                                        venue = viewHolder.txtVenue.getText().toString();

                                                        if(user != null){
                                                            DatabaseReference decline_orders_db = FirebaseDatabase.getInstance().getReference().child("Orders").push();
                                                            Map newPost = new HashMap();
                                                            newPost.put("User_ID", customerId);
                                                            newPost.put("Foodstall_Name", foodStall);
                                                            newPost.put("BulkOrder_Name", name);
                                                            newPost.put("BulkOrder_Price", price);
                                                            newPost.put("BulkOrder_Quantity", quantity);
                                                            newPost.put("Order_Status", "Declined");
                                                            newPost.put("BulkOrder_DeliveryDate", deliveryDate);
                                                            newPost.put("BulkOrder_Venue", venue);
                                                            newPost.put("BulkOrder_Time", ServerValue.TIMESTAMP);
                                                            newPost.put("Token_ID", token_id);
                                                            decline_orders_db.setValue(newPost);

                                                            mDatabase.child(order_key).removeValue();
                                                            notifyDataSetChanged();
                                                            Toast.makeText(globalContext, "Bulk order declined.", Toast.LENGTH_LONG).show();
                                                        }

                                                        else {
                                                            Toast.makeText(globalContext, "Network Connection Error. Please Try Again.", Toast.LENGTH_LONG).show();
                                                        }




                                                    }else{
                                                        Toast.makeText(globalContext, "Something went wrong with declining your order. Please report this bug.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else {
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });
                        builder1.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });

            }
        };

        mOrderList.setAdapter(FBRA);
        FBRA.notifyDataSetChanged();
    }

    private void statusIsPending(OrderViewHolder viewHolder) {
        viewHolder.btnFinished.setVisibility(View.GONE);

        viewHolder.btnAccept.setEnabled(true);
        viewHolder.btnAccept.setVisibility(View.VISIBLE);
        viewHolder.btnDecline.setEnabled(true);
        viewHolder.btnDecline.setVisibility(View.VISIBLE);
    }

    private void statusIsAccepted(OrderViewHolder viewHolder) {
        viewHolder.btnAccept.setEnabled(false);
        viewHolder.btnAccept.setVisibility(View.GONE);
        viewHolder.btnDecline.setEnabled(false);
        viewHolder.btnDecline.setVisibility(View.GONE);

        viewHolder.btnFinished.setVisibility(View.VISIBLE);
    }


    private void setStatusAccepted(final View v, final String order_key, final OrderViewHolder viewHolder) {
        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                    if(status.equals("Pending")){
                        mDatabase.child(order_key).child("Order_Status").setValue("Accepted");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CardView card_view;
        public Button btnViewOrders, btnAccept, btnDecline, btnFinished;
        public String name_, customer_id, price_;
        public Long deliveryDate_;
        public TextView txtTime, txtCustomerName, txtPrice, txtQuan, txtStatus, txtDeliveryDate, txtVenue;
        public TextView user_name;

        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.cardView);
            btnViewOrders = (Button) itemView.findViewById(R.id.btnViewOrders);
            btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
            btnDecline = (Button) itemView.findViewById(R.id.btnDecline);
            btnFinished = (Button) itemView.findViewById(R.id.btnFinished);
            user_name = (TextView) itemView.findViewById(R.id.txtCustomerName);


        }

        public void setCustomerId(String customerId) {
            customer_id = customerId;
        }


        public void setName(String name) {
            name_ = name;
        }

        public void setPrice(String price) {
            price_ = price;
        }

        public void setTotal(Double total){
            txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("Php %.2f", total));
        }

        public void setStatus(String status) {
            txtStatus = mView.findViewById(R.id.txtStatus);
            txtStatus.setText(status);
        }

        public void setTimeOrdered(Long timeOrdered) {
            txtTime = mView.findViewById(R.id.txtTime);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            txtTime.setText(sfd.format(new Date(timeOrdered)));
        }

        public void setVenue(String venue){
            txtVenue = mView.findViewById(R.id.txtVenue);
            txtVenue.setText(venue);
        }

        public void editTxtVenue(){
            txtVenue = mView.findViewById(R.id.txtVenue);
            txtVenue.setTypeface(null, Typeface.ITALIC);
            txtVenue.setTextColor(Color.BLUE);
        }

        public void setDeliveryDate(Long deliveryDate){
            txtDeliveryDate = mView.findViewById(R.id.txtDeliveryDate);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
            txtDeliveryDate.setText(sfd.format(new Date(deliveryDate)));
            deliveryDate_ = deliveryDate;
        }

    }

}
