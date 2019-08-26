package com.lfsa.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.GettersSetters.BulkOrder;
import com.lfsa.MainActivity;
import com.lfsa.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BulkOrdersFragment extends Fragment {

    private DatabaseReference mDatabase;
    private Context globalContext = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String uid;
    private String dbQuantity, dbName, dbPrice, bulkOrder = "", venue;
    private Integer sumofTwo = 0, sumOfAll = 0;
    //List<String> foodNamesList, foodQuantitiesList, foodPricesList;


    RecyclerView mOrderList;
    EditText newQuan;

    public BulkOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_bulk_orders, container, false);
        // Inflate the layout for this fragment
        uid = getActivity().getIntent().getExtras().getString("customerId");

        globalContext = this.getActivity();

        //setTitle("Orders");

        mOrderList = (RecyclerView) view.findViewById(R.id.bulk_list_view);
        mOrderList.setHasFixedSize(true);
        mOrderList.setLayoutManager(new LinearLayoutManager(container.getContext()));

        mDatabase= FirebaseDatabase.getInstance().getReference().child("BulkOrder");
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
        FirebaseRecyclerAdapter<BulkOrder, BulkOrdersFragment.OrderViewHolder> FBRA = new FirebaseRecyclerAdapter<BulkOrder, BulkOrdersFragment.OrderViewHolder>
                (BulkOrder.class, R.layout.singleorderitem_bulk, BulkOrdersFragment.OrderViewHolder.class, mDatabase.orderByChild("User_ID").equalTo(uid)) {
            @Override
            protected void populateViewHolder(final BulkOrdersFragment.OrderViewHolder viewHolder, final BulkOrder model, int position) {

                final String food_key = getRef(position).getKey().toString();
                mDatabase.child(food_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String bulkStatus_db = (String) dataSnapshot.child("Order_Status").getValue();
                        if(bulkStatus_db.equals("Accepted")){
                            viewHolder.txtOption.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setFoodstall_Name(model.getFoodstall_Name());
                viewHolder.setBulkOrder_Time(model.getBulkOrder_Time());
                viewHolder.setOrder_Status(model.getOrder_Status());
                viewHolder.setBulkOrder_DeliveryDate(model.getBulkOrder_DeliveryDate());

                if(TextUtils.isEmpty(model.getBulkOrder_Venue())){
                    viewHolder.editTxtVenue();
                    venue = "For pickup";

                }else{
                    venue = model.getBulkOrder_Venue();
                }
                viewHolder.setBulkOrder_Venue(venue);

                dbQuantity = model.getBulkOrder_Quantity();
                final List<String> foodQuantitiesList = new ArrayList<String>(Arrays.asList(dbQuantity.split(", ")));

                dbPrice = model.getBulkOrder_Price();
                final List<String> foodPricesList = new ArrayList<String>(Arrays.asList(dbPrice.split(", ")));

                for(int i = 0; i <= foodPricesList.size() - 1; i++){
                    sumofTwo = Integer.parseInt(foodPricesList.get(i)) * Integer.parseInt(foodQuantitiesList.get(i));
                    sumOfAll += sumofTwo;
                }
                Double Total = sumOfAll * 1.0;
                viewHolder.setTotal(Total);


                viewHolder.btnViewOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbName = model.getBulkOrder_Name();
                        List<String> foodNamesList = new ArrayList<String>(Arrays.asList(dbName.split(", ")));

                        if(TextUtils.isEmpty(model.getBulkOrder_Venue())){
                            venue = "For pickup";

                        }else{
                            venue = model.getBulkOrder_Venue();
                        }
                        bulkOrder = "Venue: " + venue + "\n";
                        for (int i = 0; i <= foodNamesList.size() - 1; i++) {
                            bulkOrder += "\n" + foodNamesList.get(i) + ": Php" + foodPricesList.get(i) + ".00 x" + foodQuantitiesList.get(i);
                        }

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(globalContext);
                        builder1.setTitle("Bulk Order");
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

                viewHolder.txtOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(globalContext, viewHolder.txtOption);
                        popupMenu.inflate(R.menu.option_menu_bulk);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(globalContext);
                                builder.setTitle("Cancel Bulk Order");
                                builder.setMessage("Are you sure you want to cancel your bulk order?");
                                builder.setCancelable(true);

                                builder.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mDatabase.child(food_key).removeValue();
                                                notifyDataSetChanged();
                                                Toast.makeText(globalContext, "Order Cancelled", Toast.LENGTH_LONG).show();

                                            }
                                        });
                                builder.setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                AlertDialog alert11 = builder.create();
                                alert11.show();

                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });


                venue = "";
                sumofTwo = 0;
                sumOfAll = 0;

            }
        };
        mOrderList.setAdapter(FBRA);
        FBRA.notifyDataSetChanged();
    }

    public void onBackPressed() {

        getActivity().finish();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CardView card_view;
        public View txtOption;
        public Button btnViewOrder;
        public TextView txtVenue;

        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.cardView);
            txtOption = (TextView) itemView.findViewById(R.id.txtOption);
            btnViewOrder = (Button) itemView.findViewById(R.id.btnViewOrder);
        }

        public void setFoodstall_Name(String foodstall_Name) {
            TextView txtFoodStall = mView.findViewById(R.id.txtFoodStall);
            txtFoodStall.setText(foodstall_Name);
        }

        public void setBulkOrder_Name(String bulkOrder_Name) {
            TextView txtMealName = mView.findViewById(R.id.txtMealName);
            txtMealName.setText(bulkOrder_Name);
        }

        public void setBulkOrder_Time(Long bulkOrder_Time) {
            TextView txtTime = mView.findViewById(R.id.txtTime);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            txtTime.setText( sfd.format(new Date(bulkOrder_Time)));
        }

        public void setOrder_Status(String order_Status) {
            TextView txtStatus = mView.findViewById(R.id.txtStatus);
            txtStatus.setText(order_Status);
        }


        public void setBulkOrder_Venue(String bulkOrder_Venue) {
            TextView txtVenue = mView.findViewById(R.id.txtVenue);
            txtVenue.setText(bulkOrder_Venue);
        }

        public void setBulkOrder_DeliveryDate(Long bulkOrder_DeliveryDate) {
            TextView txtDeliveryDate = mView.findViewById(R.id.txtDeliveryDate);
            SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
            txtDeliveryDate.setText(sfd.format(new Date(bulkOrder_DeliveryDate)));
        }

        public void setTotal(Double total){
            TextView txtPrice = mView.findViewById(R.id.txtPrice);
            txtPrice.setText(String.format("Php %.2f", total));
        }

        public void editTxtVenue(){
            TextView txtVenue = mView.findViewById(R.id.txtVenue);
            txtVenue.setTypeface(null, Typeface.ITALIC);
            txtVenue.setTextColor(Color.BLUE);
        }


    }

}
