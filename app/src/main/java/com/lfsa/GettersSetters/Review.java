package com.lfsa.GettersSetters;

public class Review {

    private String Rating_Score;
    private String Rating_Note;
    private String Rating_Title;
    private String Foodstall_Name;
    private String User_ID;

    public void setRating_Date(Long rating_Date) {
        Rating_Date = rating_Date;
    }

    public Long getRating_Date() {
        return Rating_Date;
    }

    private Long Rating_Date;

    public Float getRating_Score() {
        return Float.valueOf(Rating_Score);
    }

    public void setRating_Score(String rating_Score) {
        Rating_Score = rating_Score;
    }

    public String getRating_Note() {
        return Rating_Note;
    }

    public void setRating_Note(String rating_Note) {
        Rating_Note = rating_Note;
    }

    public String getRating_Title() {
        return Rating_Title;
    }

    public void setRating_Title(String rating_Title) {
        Rating_Title = rating_Title;
    }

    public String getFoodstall_Name() {
        return Foodstall_Name;
    }

    public void setFoodstall_Name(String foodstall_Name) {
        Foodstall_Name = foodstall_Name;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }


   /* public String feedBack;
    public String stallName;
    public String totalRating;
    public String userID;
    public String userName;
    public String title;
    public Long date;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public Float getTotalRating() {
        return Float.valueOf(totalRating);
    }

    public void setTotalRating(String totalRating) {
        this.totalRating = totalRating;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }*/

    public Review(){}

}
