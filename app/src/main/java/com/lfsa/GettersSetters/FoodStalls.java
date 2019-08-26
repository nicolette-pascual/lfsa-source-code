package com.lfsa.GettersSetters;

//GETTERS AND SETTERS CLASS

public class FoodStalls {

    private String FoodStall_Name, FoodStall_Image; //exactly on the DB


    public FoodStalls(String foodStall_Name, String foodStall_Image) {
        FoodStall_Name = foodStall_Name;
        FoodStall_Image = foodStall_Image;
    }

    public String getFoodStall_Name() {
        return FoodStall_Name;
    }

    public void setFoodStall_Name(String foodStall_Name) {
        FoodStall_Name = foodStall_Name;
    }

    public String getFoodStall_Image() {
        return FoodStall_Image;
    }

    public void setFoodStall_Image(String foodStall_Image) {
        FoodStall_Image = foodStall_Image;
    }

    public FoodStalls(){

    }


   /* public FoodStalls(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public FoodStalls(){

    }*/
}
