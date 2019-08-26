package com.lfsa.GettersSetters;

public class TransactionHistory {

    private String Foodstall_Name;
    private String Order_Name;
    private String Order_Price;
    private String Order_Quantity;
    private String Order_Note;
    private Long Order_Time;

    public String getFoodstall_Name() {
        return Foodstall_Name;
    }

    public void setFoodstall_Name(String foodstall_Name) {
        Foodstall_Name = foodstall_Name;
    }

    public String getOrder_Name() {
        return Order_Name;
    }

    public void setOrder_Name(String order_Name) {
        Order_Name = order_Name;
    }

    public String getOrder_Price() {
        return Order_Price;
    }

    public void setOrder_Price(String order_Price) {
        Order_Price = order_Price;
    }

    public String getOrder_Quantity() {
        return Order_Quantity;
    }

    public void setOrder_Quantity(String order_Quantity) {
        Order_Quantity = order_Quantity;
    }

    public String getOrder_Note() {
        return Order_Note;
    }

    public void setOrder_Note(String order_Note) {
        Order_Note = order_Note;
    }

    public Long getOrder_Time() {
        return Order_Time;
    }

    public void setOrder_Time(Long order_Time) {
        Order_Time = order_Time;
    }

    public String getOrder_Status() {
        return Order_Status;
    }

    public void setOrder_Status(String order_Status) {
        Order_Status = order_Status;
    }

    private String Order_Status;

    private String BulkOrder_Name;
    private String BulkOrder_Price;
    private String BulkOrder_Quantity;

    public void setBulkOrder_Time(Long bulkOrder_Time) {
        BulkOrder_Time = bulkOrder_Time;
    }

    private Long BulkOrder_Time;
    private String BulkOrder_Venue;
    private  Long BulkOrder_DeliveryDate;


    private Double total;

    public String getBulkOrder_Name() {
        return BulkOrder_Name;
    }

    public Long getBulkOrder_Time() {
        return BulkOrder_Time;
    }

    public void setBulkOrder_Name(String bulkOrder_Name) {
        BulkOrder_Name = bulkOrder_Name;
    }

    public String getBulkOrder_Price() {
        return BulkOrder_Price;
    }

    public void setBulkOrder_Price(String bulkOrder_Price) {
        BulkOrder_Price = bulkOrder_Price;
    }

    public String getBulkOrder_Quantity() {
        return BulkOrder_Quantity;
    }

    public void setBulkOrder_Quantity(String bulkOrder_Quantity) {
        BulkOrder_Quantity = bulkOrder_Quantity;
    }

    public String getBulkOrder_Venue() {
        return BulkOrder_Venue;
    }

    public void setBulkOrder_Venue(String bulkOrder_Venue) {
        BulkOrder_Venue = bulkOrder_Venue;
    }

    public Long getBulkOrder_DeliveryDate() {
        return BulkOrder_DeliveryDate;
    }

    public void setBulkOrder_DeliveryDate(Long bulkOrder_DeliveryDate) {
        BulkOrder_DeliveryDate = bulkOrder_DeliveryDate;
    }

    public Double getTotal() {

            double parsedPrice = Double.parseDouble(Order_Price);
            int parsedQuantity = Integer.parseInt(Order_Quantity);

            total = parsedPrice * parsedQuantity;

        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getBulkTotal() {

        double parsedPrice = Double.parseDouble(BulkOrder_Price);
        int parsedQuantity = Integer.parseInt(BulkOrder_Quantity);

        bulkTotal = parsedPrice * parsedQuantity;
        return bulkTotal;
    }

    public void setBulkTotal(Double bulkTotal) {
        this.bulkTotal = bulkTotal;
    }

    private Double bulkTotal;




}
