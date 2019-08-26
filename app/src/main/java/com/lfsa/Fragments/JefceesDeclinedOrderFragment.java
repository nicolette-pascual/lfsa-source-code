package com.lfsa.Fragments;


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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.GettersSetters.TransactionHistory;
import com.lfsa.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JefceesDeclinedOrderFragment extends Fragment {

    private DatabaseReference mDatabase, databaseReference, mGetDate;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context globalContext = null;
    String food_key;
    String dbName, dbPrice, dbQuantity,foodStall, order_key, venue, bulkOrder, date = "";
    Integer sumofTwo = 0, sumOfAll = 0;

    AlertDialog.Builder builder;

    String uid;

    RecyclerView mOrderList;
    TextView txtMealName, txtQuan, txtPrice, txtTime, txtNote, txtStatus, txtFoodStall, textView_date;

    public JefceesDeclinedOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jefcees_declined_order, container, false);

        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        uid = getActivity().getIntent().getExtras().getString("customerId");

        globalContext = this.getActivity();

        mOrderList = (RecyclerView) view.findViewById(R.id.jefcees_list_view);
        mOrderList.setHasFixedSize(true);
        mOrderList.setLayoutManager(new LinearLayoutManager(container.getContext()));

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Orders");
        mDatabase.keepSynced(true);


        mAuth= FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(container.getContext(), LoginActivity.class);
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

        FirebaseRecyclerAdapter<TransactionHistory, JefceesDeclinedOrderFragment.OrderViewHolder> FBRA = new FirebaseRecyclerAdapter<TransactionHistory, JefceesDeclinedOrderFragment.OrderViewHolder>
                (TransactionHistory.class, R.layout.singlehistoryitem, JefceesDeclinedOrderFragment.OrderViewHolder.class, mDatabase.orderByChild("Foodstall_Name").equalTo("Jefcee's")) {
            @Override
            protected void populateViewHolder(final JefceesDeclinedOrderFragment.OrderViewHolder viewHolder, final TransactionHistory model, int position) {

                order_key = getRef(position).getKey().toString();
                mDatabase.child(order_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String bulkStatus_db = (String) dataSnapshot.child("Order_Status").getValue();
                        String bulkUserId = (String) dataSnapshot.child("User_ID").getValue();
                        if(dataSnapshot.hasChild("BulkOrder_Venue") && bulkStatus_db.equals("Declined") && bulkUserId.equals(uid)){
                            viewHolder.setOrder_Name("Bulk Order");
                            viewHolder.setBulkOrder_Time(model.getBulkOrder_Time());
                            viewHolder.addDetailsButton();
                            viewHolder.txtXPrice.setVisibility(View.GONE);

                            //Check Venue value
                            if(TextUtils.isEmpty(model.getBulkOrder_Venue())){
                                venue = "For pickup";

                            }else{
                                venue = model.getBulkOrder_Venue();
                            }

                            //Name string to array
                            dbName = model.getBulkOrder_Name();
                            final List<String> foodNamesList = new ArrayList<String>(Arrays.asList(dbName.split(", ")));

                            //Quantity string to array
                            dbQuantity = model.getBulkOrder_Quantity();
                            final List<String>foodQuantitiesList = new ArrayList<String>(Arrays.asList(dbQuantity.split(", ")));

                            //Price string to array
                            dbPrice = model.getBulkOrder_Price();
                            final List<String>foodPricesList = new ArrayList<String>(Arrays.asList(dbPrice.split(", ")));

                            //get Total price of bulk order

                            for(int i = 0; i <= foodPricesList.size() - 1; i++){
                                sumofTwo = Integer.parseInt(foodPricesList.get(i)) * Integer.parseInt(foodQuantitiesList.get(i));
                                sumOfAll += sumofTwo;
                            }

                            Double Total = sumOfAll *1.0;
                            viewHolder.setTotal(Total);

                            viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(TextUtils.isEmpty(model.getBulkOrder_Venue())){
                                        venue = "For pickup";

                                    }else{
                                        venue = model.getBulkOrder_Venue();
                                    }

                                    SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
                                    date = sfd.format(new Date(model.getBulkOrder_DeliveryDate()));
                                    bulkOrder = "Delivery Date: " + date + "\nVenue: " + venue + "\nOrders: ";

                                    for (int i = 0; i <= foodNamesList.size() - 1; i++) {
                                        bulkOrder += "\n     " + foodNamesList.get(i) + ": Php " + foodPricesList.get(i) + ".00 x" + foodQuantitiesList.get(i);
                                    }

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
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

                        }
                        else{
                            String status_db = (String) dataSnapshot.child("Order_Status").getValue();
                            String userId = (String) dataSnapshot.child("User_ID").getValue();
                            if(status_db.equals("Declined") && userId.equals(uid)){
                                //viewHolder.setFoodStall(model.getFoodStall());
                                viewHolder.setOrder_Name(model.getOrder_Name());
                                //viewHolder.setNote(model.getNote());
                                viewHolder.setTotal(model.getTotal());
                                viewHolder.setOrder_Quantity(model.getOrder_Quantity());
                                viewHolder.setOrder_Price(model.getOrder_Price());
                                //viewHolder.setStatus(model.getStatus());
                                viewHolder.setOrder_Time(model.getOrder_Time());
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

        //if (FBRA.getItemCount() == 0){
        //    Toast.makeText(globalContext, "You have no accepted orders.", Toast.LENGTH_LONG).show();
        //}
        mOrderList.setAdapter(FBRA);
        FBRA.notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CardView card_view;
        public View txtOption;
        public Button btnDetails;
        public TextView textView4, txtQuan, textView_date, txtXPrice;
        public RelativeLayout relativeLayout;

        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.cardView);
            txtOption = (TextView) itemView.findViewById(R.id.txtOption);
            btnDetails = (Button) itemView.findViewById(R.id.btnDetails);
            textView_date = (TextView) itemView.findViewById(R.id.textView_date);
            textView_date.setText("Date Declined:");
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative);
            txtXPrice = (TextView) itemView.findViewById(R.id.txtXPrice);
        }

        public void addDetailsButton(){
            textView4 = (TextView) itemView.findViewById(R.id.textView4);
            txtQuan = (TextView) mView.findViewById(R.id.txtQuan);

            textView4.setVisibility(View.INVISIBLE);
            txtQuan.setVisibility(View.INVISIBLE);
            btnDetails.setVisibility(View.VISIBLE);

        }

        public void setOrder_Name(String order_Name) {
            TextView txtMealName = mView.findViewById(R.id.txtMealName);
            txtMealName.setText(order_Name);
        }

        public void setOrder_Price(String order_Price) {
            txtXPrice = mView.findViewById(R.id.txtXPrice);
            txtXPrice.setText(" x Php " + order_Price + ".00");
        }

        public void setOrder_Quantity(String order_Quantity) {
            TextView txtQuan = mView.findViewById(R.id.txtQuan);
            txtQuan.setText(order_Quantity);
        }


        public void setOrder_Note(String order_Note) {
            //TextView txtNote = mView.findViewById(R.id.txtNote);
            //txtNote.setText(note);
        }

        public void setOrder_Time(Long order_Time) {
            TextView txtTime = mView.findViewById(R.id.txtTime);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            txtTime.setText( sfd.format(new Date(order_Time)));
        }

        public void setBulkOrder_Time(Long bulkOrder_Time){
            TextView txtTime = mView.findViewById(R.id.txtTime);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            txtTime.setText( sfd.format(new Date(bulkOrder_Time)));
        }

        public void setOrder_Status(String order_Status) {
            TextView txtStatus = mView.findViewById(R.id.txtStatus);
            txtStatus.setText(order_Status);
        }

        public void setTotal(Double total){
            TextView txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("Php %.2f", total));
        }
    }
}
