package com.lfsa_foodstallcrew.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;
import com.squareup.picasso.Picasso;

public class EditMealActivity_Crew extends AppCompatActivity{

    private String food_key = null, available, musTry, foodStall, food_image, currentFoodStall;
    private DatabaseReference mDatabase, mFirebase;
    private TextView singleFoodTitle, singleFoodDesc, singleFoodPrice;
    private ImageButton singleFoodImage;
    private Button editButton;
    private FirebaseAuth mAuth;
    private Spinner singleCategory;
    private CheckBox singleCheck, singleMustTryCheckbox;
    private ProgressBar progressBar;
    private static final int GALLREQ = 1;
    private Uri uri = null;
    private StorageReference storageReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_food_crew);

        setTitle("Edit Meal");

        food_key = getIntent().getExtras().getString("FoodId");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Meals");
        mFirebase = FirebaseDatabase.getInstance().getReference().child("FoodStalls");

        currentFoodStall = getIntent().getStringExtra("FOODSTALL");

        singleFoodTitle = (TextView) findViewById(R.id.singleTitle);
        singleFoodDesc = (TextView) findViewById(R.id.singleDesc);
        singleCategory = (Spinner) findViewById(R.id.singleCategory);
        singleFoodPrice = (TextView) findViewById(R.id.singlePrice);
        singleFoodImage = (ImageButton) findViewById(R.id.singleImageButton);
        singleCheck = (CheckBox) findViewById(R.id.singleCheckbox);
        singleMustTryCheckbox = (CheckBox) findViewById(R.id.singleMustTryCheckbox);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        ArrayAdapter<String> myAdapter;
        myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        singleCategory.setAdapter(myAdapter);



        mDatabase.child(food_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String food_name = (String) dataSnapshot.child("Meal_Name").getValue();
                    String food_price = (String) dataSnapshot.child("Meal_Price").getValue();
                    String food_desc = (String) dataSnapshot.child("Meal_Desc").getValue();
                    food_image = (String) dataSnapshot.child("Meal_Image").getValue();
                    String food_category = (String) dataSnapshot.child("Meal_Category").getValue();
                    String food_availability = (String) dataSnapshot.child("Meal_Availability").getValue();
                    foodStall = (String) dataSnapshot.child("Foodstall_Name").getValue();
                    String mustTry = (String) dataSnapshot.child("Meal_Recommended").getValue();

                    singleFoodTitle.setText(food_name);
                    singleFoodDesc.setText(food_desc);
                    singleFoodPrice.setText(food_price);

                    ArrayAdapter<String> spinnerAdap = (ArrayAdapter<String>) singleCategory.getAdapter();
                    int spinnerPosition = spinnerAdap.getPosition(food_category);
                    singleCategory.setSelection(spinnerPosition);

                    if(food_availability.equals("true")) //////
                        singleCheck.setChecked(false);
                    else
                        singleCheck.setChecked(true);

                    if(mustTry.equals("true"))
                        singleMustTryCheckbox.setChecked(true);
                    else
                        singleMustTryCheckbox.setChecked(false);

                    mFirebase.child(currentFoodStall).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String currentStall_Hour = (String) dataSnapshot.child("FoodStall_Hour").getValue(String.class);
                                if(currentStall_Hour.matches("Open")){
                                    singleFoodPrice.setTextColor(Color.GRAY);
                                    singleFoodPrice.setFocusable(false);
                                    singleFoodPrice.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(EditMealActivity_Crew.this, "You can only edit meal price when your stall is closed.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    singleFoodPrice.setTextColor(Color.BLACK);
                                    singleFoodPrice.setFocusable(true);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    Picasso.with(EditMealActivity_Crew.this).load(food_image).into(singleFoodImage);
                }
                else
                    return;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onCheckboxClicked(View view){
        if(singleCheck.isChecked()){
            available = "false";
        }
        else
            available = "true";

        if(singleMustTryCheckbox.isChecked()){
            musTry = "true";
        }
        else{
            musTry = "false";
        }
    }

    public void singleImageButtonClicked(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLREQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLREQ && resultCode==RESULT_OK){
            uri = data.getData();
            singleFoodImage.setImageURI(uri);
        }
    }

    public void editItemClicked(View view) {
        // mDatabase.child(food_key).addValueEventListener(new ValueEventListener()

        final String price_text = singleFoodPrice.getText().toString().trim();
        final Integer sFoodPrice = Integer.parseInt(price_text);
        final String final_price = sFoodPrice.toString();


            if (!final_price.equals("0") && !TextUtils.isEmpty(final_price)){

               /* if (price_text.endsWith(".")) {
                    price_text = price_text.replaceAll(".", "");
                } else if (price_text.startsWith(".")) {
                    String new_price = "0" + price_text;
                    price_text = new_price;
                }*/

            final String name_text = singleFoodTitle.getText().toString().trim();
            final String desc_text = singleFoodDesc.getText().toString().trim();
            final String category_text = singleCategory.getSelectedItem().toString();
            final String foodstall_text = foodStall; //pukenam
            final String availability = available;


            if (!TextUtils.isEmpty(name_text) && !TextUtils.isEmpty(desc_text)) {
                progressBar.setVisibility(View.VISIBLE);


                try {
                    StorageReference filepath = storageReference.child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressBar.setVisibility(View.GONE);
                            final Uri downloadurl = taskSnapshot.getDownloadUrl();
                            //final DatabaseReference newPost = mRef.push();


                            mDatabase.child(food_key).child("Meal_Name").setValue(name_text);
                            mDatabase.child(food_key).child("Meal_Desc").setValue(desc_text);
                            mDatabase.child(food_key).child("Meal_Price").setValue(final_price);
                            mDatabase.child(food_key).child("Meal_Category").setValue(category_text);


                            if (uri.toString() == null)
                                mDatabase.child(food_key).child("Meal_Image").setValue(food_image);
                            else
                                mDatabase.child(food_key).child("Meal_Image").setValue(downloadurl.toString());

                            mDatabase.child(food_key).child("Foodstall_Name").setValue(foodstall_text);

                            if (singleCheck.isChecked()) {
                                mDatabase.child(food_key).child("Meal_Availability").setValue("false");
                            } else {
                                mDatabase.child(food_key).child("Meal_Availability").setValue("true");
                            }

                            if (singleMustTryCheckbox.isChecked()) {
                                mDatabase.child(food_key).child("Meal_Recommended").setValue("true");
                            } else {
                                mDatabase.child(food_key).child("Meal_Recommended").setValue("false");
                            }

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditMealActivity_Crew.this, "Meal Successfully Updated", Toast.LENGTH_LONG).show();

                            finish();
                            Intent intent = new Intent(EditMealActivity_Crew.this, MenuActivity_Crew.class);
                            intent.putExtra("FOODSTALL", currentFoodStall);
                            startActivity(intent);


                        }
                    });
                } catch (Exception e) {
                    /*final String price_text2 = singleFoodPrice.getText().toString().trim();

                    if (!price_text2.matches(".") && !price_text2.endsWith(".") && !price_text2.startsWith(".")){*/
                    e.printStackTrace();
                    mDatabase.child(food_key).child("Meal_Name").setValue(name_text);
                    mDatabase.child(food_key).child("Meal_Desc").setValue(desc_text);
                    mDatabase.child(food_key).child("Meal_Price").setValue(final_price);
                    mDatabase.child(food_key).child("Meal_Category").setValue(category_text);
                    mDatabase.child(food_key).child("Meal_Image").setValue(food_image);
                    mDatabase.child(food_key).child("Foodstall_Name").setValue(foodstall_text);

                    if (singleCheck.isChecked()) {
                        mDatabase.child(food_key).child("Meal_Availability").setValue("false");
                    } else {
                        mDatabase.child(food_key).child("Meal_Availability").setValue("true");
                    }

                    if (singleMustTryCheckbox.isChecked()) {
                        mDatabase.child(food_key).child("Meal_Recommended").setValue("true");
                    } else {
                        mDatabase.child(food_key).child("Meal_Recommended").setValue("false");
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditMealActivity_Crew.this, "Meal Successfully Updated", Toast.LENGTH_LONG).show();

                    finish();
                /*}else{
                        Toast.makeText(EditMealActivity_Crew.this, "There are invalid input/s.", Toast.LENGTH_LONG).show();
                    }*/
                }
            } else {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(EditMealActivity_Crew.this, "Please Fill All Fields.", Toast.LENGTH_LONG).show();
            }
        }else{
                Toast.makeText(EditMealActivity_Crew.this, "There are invalid input/s.", Toast.LENGTH_LONG).show();
            }
        }

}
