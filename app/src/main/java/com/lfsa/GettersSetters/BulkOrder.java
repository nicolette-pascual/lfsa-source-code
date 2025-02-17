package com.lfsa.GettersSetters;

public class BulkOrder {

    private Double total;

    //new db
    private String Foodstall_Name;
    private String BulkOrder_Name;
    private String BulkOrder_Price;
    private String BulkOrder_Quantity;

    public void setBulkOrder_Time(Long bulkOrder_Time) {
        BulkOrder_Time = bulkOrder_Time;
    }

    private Long BulkOrder_Time;
    private String Order_Status;
    private String BulkOrder_Venue;
    private  Long BulkOrder_DeliveryDate;

    public String getFoodstall_Name() {
        return Foodstall_Name;
    }

    public void setFoodstall_Name(String foodstall_Name) {
        Foodstall_Name = foodstall_Name;
    }

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

    public String getOrder_Status() {
        return Order_Status;
    }

    public void setOrder_Status(String order_Status) {
        Order_Status = order_Status;
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
        double parsedPrice = Double.parseDouble(BulkOrder_Price);
        int parsedQuantity = Integer.parseInt(BulkOrder_Quantity);

        total = parsedPrice * parsedQuantity;
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }




}
