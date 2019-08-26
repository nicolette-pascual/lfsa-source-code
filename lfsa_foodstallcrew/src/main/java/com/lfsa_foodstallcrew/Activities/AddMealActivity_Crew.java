package com.lfsa_foodstallcrew.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DataSnapshot;
import  com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.lfsa_foodstallcrew.Activities.LoginActivities.AboutUs_Crew;
import com.lfsa_foodstallcrew.MainActivity_Crew;
import com.lfsa_foodstallcrew.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AddMealActivity_Crew extends AppCompatActivity {

    private ImageButton foodImage;
    private static final int GALLREQ = 1;
    private EditText name, desc, price;
    private ProgressBar progressBar;
    private Spinner category;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private String uid, user_name;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String link_tempura_logo, link_scoops_logo, link_jefcees_logo, stall_image, mealExists = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_crew);

        setTitle("Add Meal");

        name = (EditText) findViewById(R.id.itemName);
        desc = (EditText) findViewById(R.id.itemDesc);
        price = (EditText) findViewById(R.id.itemPrice);
        category = (Spinner) findViewById(R.id.itemCategory);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("Meals");
        foodImage = (ImageButton) findViewById(R.id.imgFoodStall);

        link_jefcees_logo = "https://firebasestorage.googleapis.com/v0/b/lfsa-database.appspot.com/o/foodstall_logo%2Fjefcees_logo.png?alt=media&token=fa149828-06cd-42fd-a855-4e46d7c62d2f";
        link_scoops_logo = "https://firebasestorage.googleapis.com/v0/b/lfsa-database.appspot.com/o/foodstall_logo%2Fscoops_logo.png?alt=media&token=48b6487c-5d16-427d-9fa5-d7d5fc0f4d33";
        link_tempura_logo = "https://firebasestorage.googleapis.com/v0/b/lfsa-database.appspot.com/o/foodstall_logo%2Ftempura_sam_logo.png?alt=media&token=1d8c1737-cde6-41e1-afdf-198b9083f96f";

        ArrayAdapter<String> myAdapter;
        myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(myAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference("Meals");

        if(user != null){
            uid = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.addValueEventListener((new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_name = dataSnapshot.child("Users").child(uid).child("Name").getValue(String.class);
                    setImage();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }));
        }
        else {
            Toast.makeText(AddMealActivity_Crew.this, "Network Error.", Toast.LENGTH_SHORT).show();
        }



    }

    public void setImage() {
        switch (user_name){
            case "Jefcee's":
                Picasso.with(AddMealActivity_Crew.this).load(link_jefcees_logo).into(foodImage);
                stall_image = link_jefcees_logo;
                break;
            case "S-coop-s":
                Picasso.with(AddMealActivity_Crew.this).load(link_scoops_logo).into(foodImage);
                stall_image = link_scoops_logo;
                break;
            case "Tempura Sam":
                Picasso.with(AddMealActivity_Crew.this).load(link_tempura_logo).into(foodImage);
                stall_image = link_tempura_logo;
                break;
        }
    }

    public void imageButtonClicked(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLREQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLREQ && resultCode==RESULT_OK){
            uri = data.getData();

            foodImage.setImageURI(uri);
        }
    }

    public void addItemButtonClicked(View view) {
        progressBar.setVisibility(View.VISIBLE);
        final String price_text = price.getText().toString().trim();

        if (!price_text.matches(".") && !price_text.endsWith(".") && !price_text.startsWith(".") && !TextUtils.isEmpty(price_text)){

        final String name_text = name.getText().toString().trim();
        final String desc_text = desc.getText().toString().trim();
        final String category_text = category.getSelectedItem().toString();
        final String foodstall = user_name;
        final String available = "true";
        final String mustTry = "false";
        final String meal_name = name_text.toLowerCase().replaceAll(" ", "");


        if (!TextUtils.isEmpty(name_text) && !TextUtils.isEmpty(desc_text) ) {

            mRef.orderByChild("Foodstall_Name").equalTo(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String mealName = (String) data.child("Meal_Name").getValue();
                            mealName = mealName.toLowerCase().replaceAll(" ", "");
                            if (mealName.equals(meal_name)) {
                                mealExists = "exists";
                            }
                        }

                        if (!mealExists.equals("exists")) {
                            try {
                                StorageReference filepath = storageReference.child(uri.getLastPathSegment());
                                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        final Uri downloadurl = taskSnapshot.getDownloadUrl();

                                        DatabaseReference new_menu_db = FirebaseDatabase.getInstance().getReference().child("Meals").push();
                                        Map newPost = new HashMap();
                                        newPost.put("Meal_Name", name_text);
                                        newPost.put("Meal_Desc", desc_text);
                                        newPost.put("Meal_Price", price_text);
                                        newPost.put("Meal_Category", category_text);
                                        newPost.put("Meal_Image", downloadurl.toString());
                                        newPost.put("Foodstall_Name", foodstall);
                                        newPost.put("Meal_Availability", available);
                                        newPost.put("Meal_Recommended", mustTry);
                                        new_menu_db.setValue(newPost);

                                        finish();
                                        startActivity(new Intent(AddMealActivity_Crew.this, MainActivity_Crew.class));
                                        Toast.makeText(AddMealActivity_Crew.this, "Meal Successfully Uploaded", Toast.LENGTH_LONG).show();
                                    }


                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                DatabaseReference new_menu_db = FirebaseDatabase.getInstance().getReference().child("Meals").push();
                                Map newPost = new HashMap();
                                newPost.put("Meal_Name", name_text);
                                newPost.put("Meal_Desc", desc_text);
                                newPost.put("Meal_Price", price_text);
                                newPost.put("Meal_Category", category_text);
                                newPost.put("Meal_Image", stall_image);
                                newPost.put("Foodstall_Name", foodstall);
                                newPost.put("Meal_Availability", available);
                                newPost.put("Meal_Recommended", mustTry);
                                new_menu_db.setValue(newPost);

                                finish();
                                startActivity(new Intent(AddMealActivity_Crew.this, MainActivity_Crew.class));
                                Toast.makeText(AddMealActivity_Crew.this, "Meal Successfully Uploaded", Toast.LENGTH_LONG).show();
                            }
                        } else
                            Toast.makeText(AddMealActivity_Crew.this, "Meal is already in the list", Toast.LENGTH_LONG).show();

                    }
                    else {
                        try {
                            StorageReference filepath = storageReference.child(uri.getLastPathSegment());
                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final Uri downloadurl = taskSnapshot.getDownloadUrl();

                                    DatabaseReference new_menu_db = FirebaseDatabase.getInstance().getReference().child("Meals").push();
                                    Map newPost = new HashMap();
                                    newPost.put("Meal_Name", name_text);
                                    newPost.put("Meal_Desc", desc_text);
                                    newPost.put("Meal_Price", price_text);
                                    newPost.put("Meal_Category", category_text);
                                    newPost.put("Meal_Image", downloadurl.toString());
                                    newPost.put("Foodstall_Name", foodstall);
                                    newPost.put("Meal_Availability", available);
                                    newPost.put("Meal_Recommended", mustTry);
                                    new_menu_db.setValue(newPost);

                                    finish();
                                    startActivity(new Intent(AddMealActivity_Crew.this, MainActivity_Crew.class));
                                    Toast.makeText(AddMealActivity_Crew.this, "Meal Successfully Uploaded", Toast.LENGTH_LONG).show();
                                }


                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            DatabaseReference new_menu_db = FirebaseDatabase.getInstance().getReference().child("Meals").push();
                            Map newPost = new HashMap();
                            newPost.put("Meal_Name", name_text);
                            newPost.put("Meal_Desc", desc_text);
                            newPost.put("Meal_Price", price_text);
                            newPost.put("Meal_Category", category_text);
                            newPost.put("Meal_Image", stall_image);
                            newPost.put("Foodstall_Name", foodstall);
                            newPost.put("Meal_Availability", available);
                            newPost.put("Meal_Recommended", mustTry);
                            new_menu_db.setValue(newPost);

                            finish();
                            startActivity(new Intent(AddMealActivity_Crew.this, MainActivity_Crew.class));
                            Toast.makeText(AddMealActivity_Crew.this, "Meal Successfully Uploaded", Toast.LENGTH_LONG).show();
                        }
                    }
                }



                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AddMealActivity_Crew.this, "Please Fill All Fields.", Toast.LENGTH_LONG).show();
        }
    }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AddMealActivity_Crew.this, "Invalid input/s.", Toast.LENGTH_LONG).show();
        }

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddMealActivity_Crew.this, MainActivity_Crew.class);
        startActivity(intent);
        finish();
    }
}