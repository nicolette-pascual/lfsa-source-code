package com.lfsa.Fragments;


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
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.GettersSetters.Order;
import com.lfsa.MainActivity;
import com.lfsa.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class PendingOrdersFragment extends Fragment {

    private DatabaseReference mDatabase, databaseReference, mGetDate;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context globalContext = null;
    String food_key;

    AlertDialog.Builder builder;

    String uid;

    RecyclerView mOrderList;
    TextView txtMealName, txtQuan, txtPrice, txtTime, txtNote, txtStatus, txtFoodStall, txtOption;
    EditText newQuan;

    public PendingOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pending_orders, container, false);
        // Inflate the layout for this fragment
        uid = getActivity().getIntent().getExtras().getString("customerId");

        globalContext = this.getActivity();

        //setTitle("Orders");

        mOrderList = (RecyclerView) view.findViewById(R.id.pending_list_view);
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
        FirebaseRecyclerAdapter<Order, OrderViewHolder> FBRA = new FirebaseRecyclerAdapter<Order, OrderViewHolder>
                (Order.class, R.layout.singleorderitem, OrderViewHolder.class, mDatabase.orderByChild("User_ID").equalTo(uid)) {

            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final Order model, final int position) {
                food_key = getRef(position).getKey();
                mDatabase.child(food_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String status = (String) dataSnapshot.child("Order_Status").getValue();
                            if(status.equals("Pending")){
                                viewHolder.setFoodstall_Name(model.getFoodstall_Name());
                                viewHolder.setOrder_Name(model.getOrder_Name());
                                viewHolder.setOrder_Note(model.getOrder_Note());
                                viewHolder.setTotal(model.getTotal());
                                viewHolder.setOrder_Price(model.getOrder_Price());
                                viewHolder.setOrder_Quantity(model.getOrder_Quantity());

                                viewHolder.setOrder_Status(model.getOrder_Status());
                                viewHolder.setOrder_Time(model.getOrder_Time());
                                //viewHolder.setNote(model.getNote());

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

                viewHolder.txtOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        //Display option menu

                        PopupMenu popupMenu = new PopupMenu(globalContext, viewHolder.txtOption);
                        popupMenu.inflate(R.menu.option_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.menu_edit:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(globalContext);
                                        newQuan = new EditText(globalContext);
                                        newQuan.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        builder.setView(newQuan);
                                        builder.setTitle("Edit Quantity")
                                                .setMessage("Enter quantity:")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(!TextUtils.isEmpty(newQuan.getText().toString())) {
                                                            if (Integer.parseInt(newQuan.getText().toString()) <= 10) {
                                                                food_key = getRef(position).getKey();
                                                                editOrders(v, food_key);
                                                            } else {
                                                                Snackbar.make(v, "Please refer to LFSA's BULK ORDER feature.", Snackbar.LENGTH_LONG)
                                                                        .setAction("Action", null).show();
                                                                //dialog.cancel();
                                                            }
                                                        }else{
                                                            Toast.makeText(globalContext, "You did not enter anything. No changes saved.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // do nothing
                                                    }
                                                })
                                                .show();
                                        break;
                                    case R.id.menu_cancel:
                                        //Delete item
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(globalContext);
                                        builder2.setTitle("Cancel Order")
                                                .setMessage("Are you sure you want to cancel your order?")
                                                .setPositiveButton("Cancel Order", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        food_key = getRef(position).getKey();
                                                        mDatabase.child(food_key).removeValue();
                                                        notifyDataSetChanged();
                                                        Toast.makeText(globalContext, "Order Cancelled.", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).show();



                                        break;
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };

        // if (FBRA.getItemCount() == 0){
        //    Toast.makeText(globalContext, ""+uid, Toast.LENGTH_LONG).show();
        //}
        mOrderList.setAdapter(FBRA);
        FBRA.notifyDataSetChanged();
    }

    private void editOrders(final View v, final String food_key) {
        mDatabase.child(food_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String status = (String) dataSnapshot.child("Order_Status").getValue();
                    if(status.equals("Pending")){
                        mDatabase.child(food_key).child("Order_Quantity").setValue(newQuan.getText().toString());
                        mDatabase.child(food_key).child("Order_Time").setValue(ServerValue.TIMESTAMP);
                        Snackbar.make(v, "Order Updated!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        Snackbar.make(v, "You cannot edit an accepted order.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        public RelativeLayout relativeLayout;

        public OrderViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            card_view = (CardView) itemView.findViewById(R.id.cardView);
            txtOption = (TextView) itemView.findViewById(R.id.txtOption);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
        public void setFoodstall_Name(String foodstall_Name) {
            TextView txtFoodStall = mView.findViewById(R.id.txtFoodStall);
            txtFoodStall.setText(foodstall_Name);
        }

        public void setOrder_Name(String order_Name) {
            TextView txtMealName = mView.findViewById(R.id.txtMealName);
            txtMealName.setText(order_Name);
        }

        public void setOrder_Price(String order_Price) {
            TextView txtPrice = mView.findViewById(R.id.txtXPrice);
            txtPrice.setText(" x Php " + order_Price + ".00");
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
