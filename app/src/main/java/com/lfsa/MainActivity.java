package com.lfsa;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Activities.LoginActivities.LoginActivity;
import com.lfsa.Activities.MainNavBarActivities.AboutUs;
import com.lfsa.Activities.MainNavBarActivities.DeclinedOrdersActivity;
import com.lfsa.Activities.MainNavBarActivities.ReportActivity;
import com.lfsa.Activities.MainNavBarActivities.ResetPasswordActivity;
import com.lfsa.Activities.MainNavBarActivities.SearchActivity;
import com.lfsa.Activities.MainNavBarActivities.TransactionHistoryActivity;
import com.lfsa.Activities.MenuActivities.MenuActivity;
import com.lfsa.Activities.ViewOrderActivity;
import com.lfsa.GettersSetters.FoodStalls;
import com.lfsa.GettersSetters.Users;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference databaseReference, mDatabase, reference, mViewOrdersRef, mAccepdtedOrdersDB, mTempOrdersDB, mUserDB, mOrdersDB;
    Query query;
    FirebaseUser user;
    String uid;
    private TextView txt_name, txt_email;
    private RecyclerView mStallsList;
    public String chosenFoodStall, customer, customerId, userAccountType, email;
    Users users = new Users();

    //for Logout
    public String acceptedOrders = "", tempOrders = "", orders = "withoutOrders";
    //public int orders = 0;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);

        txt_name = (TextView) navHeaderView.findViewById(R.id.textView_name);
        txt_email = (TextView) navHeaderView.findViewById(R.id.textView_email);

        if(!isNetworkAvailable()){
            Toast.makeText(MainActivity.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = databaseReference.child("FoodStalls");
        mDatabase.keepSynced(true);
        query = databaseReference.child("TempOrders").orderByChild("customerId").equalTo(uid);

        //databaseReference for LogOut method
        mAccepdtedOrdersDB = databaseReference.child("AcceptedOrders");
        mAccepdtedOrdersDB.keepSynced(true);
        mTempOrdersDB = databaseReference.child("TempOrders");
        mTempOrdersDB.keepSynced(true);
        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDB.keepSynced(true);
        mOrdersDB = databaseReference.child("Orders");
        mOrdersDB.keepSynced(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            email = user.getEmail().toString();
            getUserType();
        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        mStallsList=(RecyclerView)findViewById(R.id.myrecycleview);
        mStallsList.setHasFixedSize(true);
        mStallsList.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(MainActivity.this, ViewOrderActivity.class);
                intent.putExtra("customerId", uid);
                startActivity(intent);
            }
        });

        databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_name = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                String user_email = dataSnapshot.child("Users").child(uid).child("Email").getValue(String.class);
                String token_id = dataSnapshot.child("Users").child(uid).child("Token_ID").getValue(String.class);

                txt_name.setText(user_name);
                customer = user_name;
                customerId = uid;
                txt_email.setText(user_email);
                users.setCustomer(customer);
                users.setCustomerId(customerId);
                users.setToken_id(token_id);

                if(token_id.equals("none")){
                    Toast.makeText(MainActivity.this, "Connection Error. Logging in again is required.", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }));

        forOrders();
    }

    //LOADING FOOD STALLS
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<FoodStalls, FoodStallsViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FoodStalls, FoodStallsViewHolder>
                (FoodStalls.class, R.layout.activity_foodstalls, FoodStallsViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final FoodStallsViewHolder viewHolder, FoodStalls model, int position) {
                viewHolder.setTitle(model.getFoodStall_Name());
                viewHolder.setImage(MainActivity.this, model.getFoodStall_Image());

                viewHolder.post_Image.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        chosenFoodStall = viewHolder.post_title.getText().toString();

                        Intent intent = new Intent(MainActivity.this,MenuActivity.class);
                        intent.putExtra("chosenFoodStall", chosenFoodStall);
                        intent.putExtra("customer", customer);
                        intent.putExtra("customerId", uid);
                        startActivity(intent);
                        finish();

                    }
                });

            }
        };

        mStallsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FoodStallsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView post_title;
        public ImageView post_Image;

        public FoodStallsViewHolder(final View itemView){
            super(itemView);
            mView=itemView;
        }
        public void setTitle(String title) {
            post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setImage(Context ctx, String image){
            post_Image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_Image);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            System.exit(1);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK BUTTON again to exit LFSA.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            forOrders();
            if (orders.equals("withOrders")){
                Toast.makeText(MainActivity.this, "Sorry. You still have an unfinished transaction.", Toast.LENGTH_SHORT).show();
                //orders = "withoutOrders";
            }
            else {
                /*finish();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);*/
                deleteTokenId(uid);
                //orders = "withoutOrders";
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void forOrders(){
        mOrdersDB.orderByChild("User_ID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                        String status = (String) data.child("Order_Status").getValue();
                        if(status.equals("Finished") || status.equals("Declined")){

                            orders = "withoutOrders";
                            continue;
                        }
                        else {
                            orders = "withOrders";
                        }
                    }

                }
                else {
                    //orders = orders--;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void deleteTokenId(String userId) {
        mUserDB.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mUserDB.child(uid).child("Token_ID").setValue("none");
                    finish();
                    FirebaseAuth.getInstance().signOut();
                    Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    Toast.makeText(MainActivity.this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();
            Intent newActivity = new Intent(MainActivity.this, MainActivity.class);
            startActivity(newActivity);
            finish();
        } else if (id == R.id.nav_search) {
            Intent newActivity = new Intent(MainActivity.this, SearchActivity.class);
            newActivity.putExtra("customer", customer);
            newActivity.putExtra("customerId", uid);
            startActivity(newActivity);
            finish();
        }else if (id == R.id.nav_history) {
            Intent newActivity = new Intent(MainActivity.this, TransactionHistoryActivity.class);
            newActivity.putExtra("customer", customer);
            newActivity.putExtra("customerId", uid);
            startActivity(newActivity);
            finish();

        }else if (id == R.id.nav_declined) {
            Intent newActivity = new Intent(MainActivity.this, DeclinedOrdersActivity.class);
            newActivity.putExtra("customer", customer);
            newActivity.putExtra("customerId", uid);
            startActivity(newActivity);
            finish();

        }else if (id == R.id.nav_resetpass) {
            Intent newActivity = new Intent(MainActivity.this, ResetPasswordActivity.class);
            newActivity.putExtra("fromNavBar", true);
            newActivity.putExtra("user_email", email);
            startActivity(newActivity);
            finish();
        } else if (id == R.id.nav_aboutus) {
            Intent newActivity = new Intent(MainActivity.this, AboutUs.class);
            startActivity(newActivity);
            finish();
        }else if (id == R.id.nav_report){
            Intent newActivity = new Intent(MainActivity.this, ReportActivity.class);
            newActivity.putExtra("customer", customer);
            startActivity(newActivity);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getUserType(){
        mUserDB.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String accountType = (String) dataSnapshot.child("Account_Type").getValue();
                    if (accountType != null) {
                        if (accountType.equals("Crew")) {
                            startActivity(new Intent(MainActivity.this, MainActivity_Crew.class));
                            finish();
                        }
                    }else{
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}