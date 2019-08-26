package com.lfsa_foodstallcrew.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lfsa_foodstallcrew.Activities.LoginActivities.LoginActivity_Crew;
import com.lfsa_foodstallcrew.Activities.ViewOrdersActivity_Crew;
import com.lfsa_foodstallcrew.GettersSetters.OrderCrew;
import com.lfsa_foodstallcrew.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PendingOrdersFragment_Crew extends Fragment {

    private DatabaseReference mDatabase, databaseReference, mGetDate;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String order_key;
    String foodStall, customerName;

    RecyclerView mOrderList;
    TextView txtMealName, txtQuan, txtPrice, txtTime, txtNote, txtStatus;

    private Context globalContext = null;

    public PendingOrdersFragment_Crew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pending_orders_crew, container, false);
        // Inflate the layout for this fragment
        foodStall = getActivity().getIntent().getExtras().getString("FOODSTALL");

        //setTitle("Orders");

        globalContext = container.getContext();

        mOrderList = (RecyclerView) view.findViewById(R.id.pending_list_view);
        mOrderList.setHasFixedSize(true);
        mOrderList.setLayoutManager(new LinearLayoutManager(container.getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Orders");
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

        FirebaseRecyclerAdapter<OrderCrew, OrderViewHolder> FBRA = new FirebaseRecyclerAdapter<OrderCrew, OrderViewHolder>
                (OrderCrew.class, R.layout.singleorderitem_crew, OrderViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(foodStall)) {
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final OrderCrew model, final int position) {
                order_key = getRef(position).getKey();

                mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("BulkOrder_Venue")){
                            viewHolder.relativeLayout.setVisibility(View.GONE);
                        }
                        else{
                            String status_db = (String) dataSnapshot.child("Order_Status").getValue();
                            if(status_db.equals("Pending")){
                                //mDatabase.child(order_key).child("status").setValue("Accepted");
                                databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            String uid = model.getUser_ID();
                                            customerName = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                                            viewHolder.txtCustomerName.setText(customerName);
                                        } catch (Exception e) {
                                            Intent intent = new Intent(globalContext, ViewOrdersActivity_Crew.class);
                                            startActivity(intent);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(globalContext, "Network Error ", Toast.LENGTH_SHORT).show();
                                    }
                                }));

                                viewHolder.setOrder_Name(model.getOrder_Name());
                                viewHolder.setTotal(model.getTotal());
                                viewHolder.setOrder_Quantity(model.getOrder_Quantity());
                                viewHolder.setOrder_Status(model.getOrder_Status());
                                viewHolder.setOrder_Time(model.getOrder_Time());
                                viewHolder.setOrder_Price(model.getOrder_Price());
                                viewHolder.setOrder_Note(model.getOrder_Note());

                                if(TextUtils.isEmpty(model.getOrder_Note() )){
                                    viewHolder.btnViewNote.setVisibility(View.GONE);
                                }
                            }

                            else{
                                viewHolder.relativeLayout.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.btnViewNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setTitle("Customer's Note");
                        builder1.setCancelable(true);

                        if(TextUtils.isEmpty(model.getOrder_Note())){
                            builder1.setMessage("No Notes");
                        }else{
                            String message = model.getOrder_Name()+"\nOrdered by: " + customerName+"\n\nNote: \n\""+model.getOrder_Note()+"\"";
                            builder1.setMessage(message);
                        }

                        builder1.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
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
                        builder1.setTitle("ACCEPT / DECLINE ORDERS");
                        builder1.setMessage("Are you sure you want to accept this order?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String order_key = getRef(position).getKey();
                                        setStatusAccepted(v, order_key, viewHolder);
                                        notifyDataSetChanged();
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
                    }
                });

                viewHolder.btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setTitle("ACCEPT/DECLINE ORDERS");
                        builder1.setMessage("Are you sure you want to decline this order?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String order_key = getRef(position).getKey();
                                        setStatusDeclined(v, order_key, viewHolder);
                                        notifyDataSetChanged();
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
                    }
                });

            }
        };

        mOrderList.setAdapter(FBRA);
        FBRA.notifyDataSetChanged();
    }

    private void setStatusAccepted(final View v, final String order_key, final OrderViewHolder viewHolder) {
        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                    mDatabase.child(order_key).child("Order_Status").setValue("Accepted");
                    mDatabase.child(order_key).child("Order_Time").setValue(ServerValue.TIMESTAMP);
                    Toast.makeText(globalContext, "Order Accepted", Toast.LENGTH_LONG).show();
                    RefreshActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setStatusDeclined(final View v, final String order_key, final OrderViewHolder viewHolder) {
        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                    mDatabase.child(order_key).child("Order_Status").setValue("Declined");
                    mDatabase.child(order_key).child("Order_Time").setValue(ServerValue.TIMESTAMP);
                    Toast.makeText(globalContext, "Order Declined", Toast.LENGTH_LONG).show();
                    RefreshActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void RefreshActivity(){
        getActivity().finish();
        Intent intent = new Intent(globalContext, ViewOrdersActivity_Crew.class);
        intent.putExtra("FOODSTALL", foodStall);
        startActivity(intent);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CardView card_view;
        //public TextView txtEmail;
        public Button btnAccept, btnDecline, btnViewNote;
        public String customer_id, price_;
        public TextView txtCustomerName, txtMealName, txtPrice, txtQuan, txtStatus, txtXPrice;
        public RelativeLayout relativeLayout;

        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.cardView);
            //txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
            btnDecline = (Button) itemView.findViewById(R.id.btnDecline);
            btnViewNote = itemView.findViewById(R.id.btnViewNote);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative);
            txtCustomerName = mView.findViewById(R.id.txtCustomerName);
        }

        public void setCustomerName(String customerName) {
            txtCustomerName = mView.findViewById(R.id.txtCustomerName);
            txtCustomerName.setText(customerName);


        }

        public void setCustomerId(String customerId) {
            customer_id = customerId;
        }


        public void setOrder_Name(String name) {
            txtMealName = mView.findViewById(R.id.txtMealName);
            txtMealName.setText(name);
        }

        public void setTotal(Double total){
            txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("Php %.2f", total));
        }

        /*public void setPrice(String price) {
            TextView txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText("Php "+price);
        }*/

        public void setOrder_Price(String order_Price) {
            TextView txtPrice = mView.findViewById(R.id.txtXPrice);
            txtPrice.setText(" x Php " + order_Price + ".00");
        }

        public void setOrder_Quantity(String quantity) {
            txtQuan = mView.findViewById(R.id.txtQuan);
            txtQuan.setText(quantity);
        }

        public void setOrder_Note(String note) {
            //TextView txtNote = mView.findViewById(R.id.txtNote);
            //txtNote.setText(note);
        }

        public void setOrder_Time(Long time) {
            TextView txtTime = mView.findViewById(R.id.txtTime);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            txtTime.setText( sfd.format(new Date(time)));
        }

        public void setOrder_Status(String status) {
            txtStatus = mView.findViewById(R.id.txtStatus);
            txtStatus.setText(status);
        }
    }
}
