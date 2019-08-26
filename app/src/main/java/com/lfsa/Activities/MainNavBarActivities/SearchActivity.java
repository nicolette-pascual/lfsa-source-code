package com.lfsa.Activities.MainNavBarActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lfsa.Adapters.SearchAdapter;
import com.lfsa.MainActivity;
import com.lfsa.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    EditText search_edit_text;
    ImageView imgBtnSearch;

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    SearchAdapter searchAdapter;

    ArrayList<String> mealNameList;
    ArrayList<String> mealStallList;
    ArrayList<String> imageList;
    ArrayList<String> foodIdList;

    private String customerName, customer_Id;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search");

        Bundle extras = getIntent().getExtras();
        customerName = extras.getString("customer");
        customer_Id = extras.getString("customerId");

        search_edit_text = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imgBtnSearch = findViewById(R.id.ivSearchButton);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        mealNameList = new ArrayList<>();
        mealStallList = new ArrayList<>();
        imageList = new ArrayList<>();
        foodIdList = new ArrayList<>();

        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(!s.toString().isEmpty()){
                    imgBtnSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);

                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);

                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                            setAdapter(s.toString());
                        }
                    });


                }else{
                    mealNameList.clear();
                    mealStallList.clear();
                    imageList.clear();
                    foodIdList.clear();
                    recyclerView.removeAllViews();
                }
            }
        });
    }

    private void setAdapter(final String searchedString) {

        databaseReference.child("Meals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mealNameList.clear();
                mealStallList.clear();
                imageList.clear();
                foodIdList.clear();
                recyclerView.removeAllViews();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String food_id =  snapshot.getKey(); //FoodId
                    String meal_image = snapshot.child("Meal_Image").getValue(String.class);
                    String meal_name = snapshot.child("Meal_Name").getValue(String.class);
                    String meal_desc = snapshot.child("Meal_Desc").getValue(String.class);
                    String meal_foodstall = snapshot.child("Foodstall_Name").getValue(String.class); //chosenFoodStall

                    if(meal_name.toLowerCase().contains(searchedString.toLowerCase())){
                        mealNameList.add(meal_name);
                        mealStallList.add(meal_foodstall);
                        imageList.add(meal_image);
                        foodIdList.add(food_id);


                        //counter++;
                    }else if(meal_desc.toLowerCase().contains(searchedString.toLowerCase())){
                        mealNameList.add(meal_name);
                        mealStallList.add(meal_foodstall);
                        imageList.add(meal_image);
                        foodIdList.add(food_id);

                    }
                    else if(meal_foodstall.toLowerCase().contains(searchedString.toLowerCase())){
                        mealNameList.add(meal_name);
                        mealStallList.add(meal_foodstall);
                        imageList.add(meal_image);
                        foodIdList.add(food_id);

                        //NUMBER OF RESULTS TO BE SHOWN
                        //if(counter == 15){
                        //   break;
                        //}
                    }
                }
                if(mealNameList.isEmpty()){
                    Toast.makeText(SearchActivity.this, "No search results found.", Toast.LENGTH_LONG).show();
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, mealNameList, mealStallList, imageList, foodIdList, customerName, customer_Id);
                recyclerView.setAdapter(searchAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}