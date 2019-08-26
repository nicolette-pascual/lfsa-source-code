package com.lfsa_foodstallcrew.GettersSetters;

public class OrderCrew {

   /* public String foodStall;
    public String name;
    public String price;
    public String venue;
    public String quantity;
    public String note;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long time;

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Long deliveryDate;

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String token_id;

    public String status;
    public Double total;
    public String customerName;

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

    public String customerId;

    public Double getTotal() {
        double parsedPrice = Double.parseDouble();
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
*/

    private String Foodstall_Name;
    private String BulkOrder_Name;
    private String Order_Name;
    private String Order_Price;
    private String Order_Quantity;
    private String Order_Note;
    private Long Order_Time;
    private String Order_Status;
    public Double total;
    private Long BulkOrder_DeliveryDate;
    private String BulkOrder_Venue;
    private String User_ID;


    public Long getBulkOrder_DeliveryDate() {
        return BulkOrder_DeliveryDate;
    }

    public void setBulkOrder_DeliveryDate(Long bulkOrder_DeliveryDate) {
        BulkOrder_DeliveryDate = bulkOrder_DeliveryDate;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getBulkOrder_Venue() {
        return BulkOrder_Venue;
    }

    public void setBulkOrder_Venue(String bulkOrder_Venue) {
        BulkOrder_Venue = bulkOrder_Venue;
    }

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

    public Double getTotal() {
        double parsedPrice = Double.parseDouble(Order_Price);
        int parsedQuantity = Integer.parseInt(Order_Quantity);

        total = parsedPrice * parsedQuantity;
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Long getBulkOrder_Time() {
        return BulkOrder_Time;
    }

    public void setBulkOrder_Time(Long bulkOrder_Time) {
        BulkOrder_Time = bulkOrder_Time;
    }

    public String getBulkOrder_Status() {
        return BulkOrder_Status;
    }

    public void setBulkOrder_Status(String bulkOrder_Status) {
        BulkOrder_Status = bulkOrder_Status;
    }

    private Long BulkOrder_Time;
    private String BulkOrder_Status;

    public void setBulkOrder_Price(String bulkOrder_Price) {
        BulkOrder_Price = bulkOrder_Price;
    }

    public void setBulkOrder_Name(String bulkOrder_Name) {
        BulkOrder_Name = bulkOrder_Name;
    }

    private String BulkOrder_Price;

    public String getBulkOrder_Quantity() {
        return BulkOrder_Quantity;
    }

    public void setBulkOrder_Quantity(String bulkOrder_Quantity) {
        BulkOrder_Quantity = bulkOrder_Quantity;
    }

    private String BulkOrder_Quantity;


    public String getBulkOrder_Name() {
        return BulkOrder_Name;
    }

    public String getBulkOrder_Price() {
        return BulkOrder_Price;
    }






}
