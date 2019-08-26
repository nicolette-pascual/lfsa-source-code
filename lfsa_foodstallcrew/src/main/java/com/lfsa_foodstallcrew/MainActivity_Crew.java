package com.lfsa_foodstallcrew;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lfsa_foodstallcrew.Activities.AddMealActivity_Crew;
import com.lfsa_foodstallcrew.Activities.LoginActivities.LoginActivity_Crew;
import com.lfsa_foodstallcrew.Activities.LoginActivities.AboutUs_Crew;
import com.lfsa_foodstallcrew.Activities.LoginActivities.ResetPasswordActivity_Crew;
import com.lfsa_foodstallcrew.Activities.MenuActivity_Crew;
import com.lfsa_foodstallcrew.Activities.ReportActivity_Crew;
import com.lfsa_foodstallcrew.Activities.ReviewActivity_Crew;
import com.lfsa_foodstallcrew.Activities.TransactionHistoryActivity_Crew;
import com.lfsa_foodstallcrew.Activities.ViewOrdersActivity_Crew;
import com.lfsa_foodstallcrew.GettersSetters.FoodStallCrew;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity_Crew extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;

    ListView listview;
    ArrayAdapter<String> adapter;
    DatabaseReference databaseReference, mDatabase, mUserDB, mGetDate, mGetServingHours;
    FirebaseUser user;

    List<String> itemlist;
    TextView textView;
    String uid, currentFoodStall, manualHours = "Open", user_name, foodstallImage_URL;

    private TextView mNameTextView, txtClose;
    private TextView mEmailTextView;
    private Button btnSched;
    private Intent addFoodIntent;
    private int first = 0;
    private String dbSched = "";
    String stallHour;

    FoodStallCrew foodStallCrew = new FoodStallCrew();
    //String user_name, putangina;

    private ImageView imageFoodStall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_crew);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("LFSA");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);

        if(!isNetworkAvailable()){
            Toast.makeText(MainActivity_Crew.this, "Please connect to the internet to start using LFSA. ", Toast.LENGTH_SHORT).show();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        mNameTextView = navHeaderView.findViewById(R.id.textView_name);
        mEmailTextView = navHeaderView.findViewById(R.id.textView_email);
        txtClose = findViewById(R.id.txtClose);
        btnSched = findViewById(R.id.btn_sched);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = databaseReference.child("FoodStalls");
        mDatabase.keepSynced(true);

        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDB.keepSynced(true);

        imageFoodStall = (ImageView) findViewById(R.id.imgFoodStall);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            getUserType();
        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity_Crew.this, LoginActivity_Crew.class));
            finish();
        }

        //databaseReference.notifyDataSetChanged();

        databaseReference.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user_name = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                String user_email = dataSnapshot.child("Users").child(uid).child("Email").getValue(String.class);
                String token_id = dataSnapshot.child("Users").child(uid).child("Token_ID").getValue(String.class);

                currentFoodStall = user_name;
                mNameTextView.setText(user_name);
                mEmailTextView.setText(user_email);

                txtClose.setText(currentFoodStall);

                if(user == null){
                    finish();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity_Crew.this, LoginActivity_Crew.class));
                    Toast.makeText(MainActivity_Crew.this, "Connection Error. Logging in again is required.", Toast.LENGTH_SHORT).show();
                }

                changeHour(currentFoodStall);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(MainActivity_Crew.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        }));

        mGetDate = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(first == 0){
            checkSchedule();
        }

    }

    public void changeHour(String name){
        databaseReference.child("FoodStalls").child(name).child("FoodStall_Hour").setValue(dbSched);
    }

    private void checkSchedule() {
        mGetDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Calendar calendar = GregorianCalendar.getInstance();
                Long time = dataSnapshot.getValue(Long.class);
                calendar.setTimeInMillis(System.currentTimeMillis() + time);

                System.out.println("Current Day:  " + calendar.get(Calendar.DAY_OF_WEEK)); //calendar.get(Calendar.HOUR_OF_DAY);
                System.out.println("Current Time:  " + calendar.get(Calendar.HOUR_OF_DAY));

                int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
                int currentTime = calendar.get(Calendar.HOUR_OF_DAY);

                switch(currentDay){
                    case 0:
                        System.out.println("Error");
                    case 1: //Sunday
                        dbSched = "Close";
                        break;
                    case 7: //Saturday currentTime < 12 && currentTime >= 8
                        if(currentTime < 12 && currentTime >= 8){
                            dbSched = "Open";
                        }else{
                            dbSched = "Close";
                        }
                        break;
                    default: //Weekdays
                        if(currentTime < 17 && currentTime >= 8){
                            dbSched = "Open";
                        } else{
                            dbSched = "Close";
                        }
                        break;
                }



                if(btnSched.equals("Close")){
                    txtClose.setText(currentFoodStall +" is currently "+dbSched+"d");
                }else{
                    txtClose.setText(currentFoodStall +" is currently "+dbSched);
                }

                try{
                    mGetServingHours.setValue(dbSched);
                    txtClose.setVisibility(View.VISIBLE);

                }catch(Exception e){
                    return;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void alert(final String bool) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity_Crew.this);
        if(bool.equals("Open")){
            if(dbSched.equals("Close")){
                builder1.setTitle("Error");
                builder1.setMessage("You are not allowed to take orders through LFSA within these hours." +
                        "\nPlease try again later.");
                mDatabase.child(currentFoodStall).child("FoodStall_Hour").setValue("Close");
            }else{
                builder1.setTitle("Open "+currentFoodStall);
                builder1.setMessage(currentFoodStall+" is now OPEN!" +
                        "\nYou will now be able to receive orders.");
                mDatabase.child(currentFoodStall).child("FoodStall_Hour").setValue("Open");
            }
        }else{
            builder1.setTitle("Close "+currentFoodStall);
            builder1.setMessage(currentFoodStall+" is now CLOSED!" +
                    "\n You will not be able to receive any orders.");
            mDatabase.child(currentFoodStall).child("FoodStall_Hour").setValue("Close");
            txtClose.setText(currentFoodStall +" is currently Closed");
        }
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
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
        getMenuInflater().inflate(R.menu.main_crew, menu);
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
            //return true;
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent i=new Intent(getApplicationContext(),LoginActivity_Crew.class);
            startActivity(i);
            Toast.makeText(MainActivity_Crew.this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();
            Intent newActivity = new Intent(MainActivity_Crew.this, MainActivity_Crew.class);
            startActivity(newActivity);
        } else if (id == R.id.nav_resetpass) {
            finish();
            Intent newActivity = new Intent(MainActivity_Crew.this,ResetPasswordActivity_Crew.class);
            startActivity(newActivity);
        } else if (id == R.id.nav_hist) {
            finish();
            Intent intent = new Intent(MainActivity_Crew.this,TransactionHistoryActivity_Crew.class);
            intent.putExtra("FOODSTALL", currentFoodStall);
            startActivity(intent);

        } else if (id == R.id.nav_aboutus) {
            finish();
            Intent newActivity = new Intent(MainActivity_Crew.this,AboutUs_Crew.class);
            startActivity(newActivity);
        }

        else if (id == R.id.nav_review) {
            finish();
            Intent intent = new Intent(MainActivity_Crew.this,ReviewActivity_Crew.class);
            intent.putExtra("chosenFoodStall", currentFoodStall);
            startActivity(intent);
        }
        else if (id == R.id.nav_report) {
            finish();
            Intent newActivity = new Intent(MainActivity_Crew.this,ReportActivity_Crew.class);
            newActivity.putExtra("FOODSTALL", currentFoodStall);
            startActivity(newActivity);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addFoodButtonClicked(View view) {
        finish();
        addFoodIntent = new Intent(MainActivity_Crew.this, AddMealActivity_Crew.class);
        startActivity(addFoodIntent);
    }

    public void editFoodMenuClicked(View view) {
        finish();
        Intent newActivity = new Intent(MainActivity_Crew.this, MenuActivity_Crew.class);
        newActivity.putExtra("FOODSTALL", currentFoodStall);
        startActivity(newActivity);
    }

    public void viewOrderMenuClicked(View view) {
        finish();
        Intent newActivity = new Intent(MainActivity_Crew.this, ViewOrdersActivity_Crew.class);
        newActivity.putExtra("FOODSTALL", currentFoodStall);
        startActivity(newActivity);
    }

    public void openCloseClicked(View view){
        btnSched.setText("Open/Close "+currentFoodStall);
        mGetServingHours = databaseReference.child("FoodStalls").child(currentFoodStall).child("FoodStall_Hour");
        mGetServingHours.keepSynced(true);

        checkSchedule();

        if(first == 0){
            first = 1;
            return;
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Open/Close "+currentFoodStall)
                    .setMessage("What's your schedule for today?")
                    .setPositiveButton("OPEN", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alert("Open");
                        }
                    })
                    .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alert("Close");
                        }
                    })
                    .show();
        }

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
                    if(accountType != null) {
                        if (accountType.equals("Customer")) {
                            try {
                                startActivity(new Intent(MainActivity_Crew.this, Class.forName("com.lfsa.MainActivity")));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            finish();
                        } else {
                           /* startActivity(new Intent(MainActivity_Crew.this, MainActivity.class));
                            finish();*/
                        }
                    }else{
                        startActivity(new Intent(MainActivity_Crew.this, LoginActivity_Crew.class));
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
