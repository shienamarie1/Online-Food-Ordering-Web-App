package Model;


public class Order {
    public int id;
    public int customer_id;
    public int rider_id;

    public String address;
    public String total;
    public String status;
    public String contact;
    public String payment_type;
    public String description;

    public String customer_name; // optional if you join in SQL
}