package com.lfsa_foodstallcrew.GettersSetters;

public class BulkOrderCrew {

    private String Foodstall_Name;
    private String BulkOrder_Name;
    private String BulkOrder_Price;
    private String BulkOrder_Quantity;

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    private String User_ID;

    public void setBulkOrder_Time(Long bulkOrder_Time) {
        BulkOrder_Time = bulkOrder_Time;
    }

    private Long BulkOrder_Time;
    private String Order_Status;
    private String BulkOrder_Venue;
    private  Long BulkOrder_DeliveryDate;
    private String BulkOrder_Status;

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





   /* public String foodStall;
    public String name;
    public String price;
    public String venue;
    public String quantity;
    public String note;
    public String status;
    public String customerId;
    public String customerName;
    public Long deliveryDate;
    public Long timeOrdered;
    public Long timeAccepted;
    public Double total;

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String token_id;

    public Long getTimeAccepted() {
        return timeAccepted;
    }

    public void setTimeAccepted(Long timeAccepted) {
        this.timeAccepted = timeAccepted;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public Double getTotal() {
        double parsedPrice = Double.parseDouble(price);
        int parsedQuantity = Integer.parseInt(quantity);

        total = parsedPrice * parsedQuantity;
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFoodStall() {
        return foodStall;
    }

    public void setFoodStall(String foodStall) {
        this.foodStall = foodStall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getTimeOrdered() {
        return timeOrdered;
    }

    public void setTimeOrdered(Long timeOrdered) {
        this.timeOrdered = timeOrdered;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }*/
}
