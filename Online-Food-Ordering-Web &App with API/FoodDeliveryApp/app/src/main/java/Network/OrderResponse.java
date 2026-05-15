package Network;

import java.util.List;

import Model.Order;

public class OrderResponse {

    private boolean success;
    private String message;
    private List<Order> orders;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}