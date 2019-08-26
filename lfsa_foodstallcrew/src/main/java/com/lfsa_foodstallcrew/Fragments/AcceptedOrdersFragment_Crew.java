package com.lfsa_foodstallcrew.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
public class AcceptedOrdersFragment_Crew extends Fragment {

    private DatabaseReference mDatabase, databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String foodStall, order_key, customerName;

    RecyclerView mOrderList;
    TextView txtMealName, txtQuan, txtPrice, txtTime, txtStatus;

    private Context globalContext = null;
    OrderCrew orderCrew = new OrderCrew();

    public AcceptedOrdersFragment_Crew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pending_orders_crew, container, false);

        foodStall = getActivity().getIntent().getExtras().getString("FOODSTALL");

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
        final FirebaseRecyclerAdapter<OrderCrew, OrderViewHolder> FBRA = new FirebaseRecyclerAdapter<OrderCrew, OrderViewHolder>
                (OrderCrew.class, R.layout.singleaccepteditem_crew, OrderViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo(foodStall)) {

            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final OrderCrew model, final int position) {

                order_key = getRef(position).getKey();

                mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("BulkOrder_Venue")){
                            viewHolder.relativeLayout.setVisibility(View.GONE);
                        }else{
                                        String status_db = (String) dataSnapshot.child("Order_Status").getValue();
                                        if(status_db.equals("Accepted") || status_db.equals("Food is being cooked") || status_db.equals("Ready for pick-up")){
                                            databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String uid = model.getUser_ID();
                                                    customerName = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                                                    viewHolder.setCustomerName(customerName);
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(globalContext, "Network Error ", Toast.LENGTH_SHORT).show();
                                                }
                                            }));

                                            viewHolder.setName(model.getOrder_Name());
//                                            viewHolder.setNote(model.getNote());
                                            viewHolder.setPrice(model.getOrder_Price());
                                            viewHolder.setTotal(model.getTotal());
                                            viewHolder.setQuantity(model.getOrder_Quantity());
                                            viewHolder.setStatus(model.getOrder_Status());
                                            viewHolder.setTime(model.getOrder_Time());
//                                            viewHolder.setCustomerName(model.getCustomerName());
//                                            viewHolder.setToken_id(model.getToken_id());
//                                            viewHolder.setCustomerId(model.getCustomerId());
                                            //viewHolder.setNote(model.getNote());

                                            final String status = viewHolder.txtStatus.getText().toString();

                                            if(status.equals("Food is being cooked")){
                                                viewHolder.btnFoodCook.setEnabled(false);
                                                viewHolder.btnPickUp.setEnabled(true);
                                            }

                                            if(status.equals("Ready for pick-up")) {
                                                viewHolder.btnFoodCook.setEnabled(false);
                                                viewHolder.btnPickUp.setEnabled(false);
                                            }
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

                viewHolder.btnFoodCook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setMessage("Food is being cooked?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        order_key = getRef(position).getKey();
                                        setStatusFoodCook(v, order_key, viewHolder);

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

                viewHolder.btnPickUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setMessage("Ready for pick-up?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        order_key = getRef(position).getKey();
                                        setStatusPickUp(v, order_key, viewHolder);
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

                viewHolder.btnClaimed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setMessage("Claimed?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        order_key = getRef(position).getKey();
                                        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                        if(user != null){
                                                            order_key = getRef(position).getKey();
                                                            setStatusFinished(v, order_key, viewHolder);
                                                        }

                                                        else{
                                                            Toast.makeText(globalContext, "Network Connection Error. Please Try Again.", Toast.LENGTH_LONG).show();
                                                        }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
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

    private void setStatusFoodCook(final View v, final String order_key, final OrderViewHolder viewHolder) {
        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                        mDatabase.child(order_key).child("Order_Status").setValue("Food is being cooked");
                        Snackbar.make(v, "Status Updated!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        viewHolder.btnFoodCook.setEnabled(false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setStatusPickUp(final View v, final String order_key, final OrderViewHolder viewHolder) {
        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                        mDatabase.child(order_key).child("Order_Status").setValue("Ready for pick-up");
                        viewHolder.btnFoodCook.setEnabled(false);
                        viewHolder.btnPickUp.setEnabled(false);
                    Snackbar.make(v, "Status Updated!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setStatusFinished(final View v, final String order_key, final OrderViewHolder viewHolder) {
        mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mDatabase.child(order_key).child("Order_Status").setValue("Finished");
                    Toast.makeText(globalContext, "Order Claimed!", Toast.LENGTH_SHORT).show();
                    viewHolder.btnFoodCook.setEnabled(false);
                    viewHolder.btnPickUp.setEnabled(false);
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
        public RelativeLayout relativeLayout;
        public String token_id, customer_id, price_;
        public Button btnFoodCook, btnPickUp, btnClaimed;
        public TextView txtCustomerName, txtMealName, txtPrice, txtQuan, txtStatus, txtDeliveryDate, txtUnitPrice;

        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.cardView);
            btnFoodCook = (Button) itemView.findViewById(R.id.btnFoodCook);
            btnPickUp = (Button) itemView.findViewById(R.id.btnPickUp);
            btnClaimed = (Button) itemView.findViewById(R.id.btnClaimed);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative);
        }

        public void setCustomerName(String customerName) {
            txtCustomerName = mView.findViewById(R.id.txtCustomerName);
            txtCustomerName.setText(customerName);
        }

        public void setCustomerId(String customerId) {
            customer_id = customerId;
        }

        public void setToken_id(String tokenId) {
           token_id = tokenId;
        }


        public void setName(String name) {
            txtMealName = mView.findViewById(R.id.txtMealName);
            txtMealName.setText(name);
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

        public void setPrice(String price) {
            txtUnitPrice = mView.findViewById(R.id.txtUnitPrice);
            txtUnitPrice.setText(" x Php " + price + ".00");
            price_ = price;
        }

        public void setTotal(Double total){
            txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("Php %.2f", total));
        }

        public void setDeliveryDate(Long deliveryDate){
            txtDeliveryDate = mView.findViewById(R.id.txtDeliveryDate);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
            txtDeliveryDate.setText(sfd.format(new Date(deliveryDate)));
        }


    }

}
