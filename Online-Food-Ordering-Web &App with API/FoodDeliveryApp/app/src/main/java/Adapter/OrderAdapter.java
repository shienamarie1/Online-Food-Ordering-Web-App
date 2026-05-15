package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.R;

import java.util.ArrayList;
import java.util.List;

import Model.Order;
import Network.ApiService;
import Network.BasicResponse;
import Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    List<Order> orders;
    Context context;

    // 🔥 callback to refresh UI in Activity
    public interface OnOrderUpdate {
        void onUpdated();
    }

    OnOrderUpdate listener;

    public OrderAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    public void setListener(OnOrderUpdate listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, address, status, price;
        Button btnDelivered;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.txtName);
            address = view.findViewById(R.id.txtAddress);
            status = view.findViewById(R.id.txtStatus);
            price = view.findViewById(R.id.txtPrice);
            btnDelivered = view.findViewById(R.id.btnAccept);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Order order = orders.get(position);

        holder.name.setText("Customer: " + order.customer_name);
        holder.address.setText("Address: " + order.address);
        holder.price.setText("Total: ₱" + order.total);

        String status = order.status != null ? order.status.toLowerCase() : "";

        if (status.contains("pending")) {
            holder.status.setText("Pending");
            holder.status.setBackgroundResource(R.drawable.status_pending);
        } else if (status.contains("out")) {
            holder.status.setText("Out for Delivery");
            holder.status.setBackgroundResource(R.drawable.status_out_for_delivery);
        } else if (status.contains("delivered")) {
            holder.status.setText("Delivered");
            holder.status.setBackgroundResource(R.drawable.status_delivered);
        }

        // hide button if already processed
        if (status.contains("out") || status.contains("delivered")) {
            holder.btnDelivered.setVisibility(View.GONE);
        } else {
            holder.btnDelivered.setVisibility(View.VISIBLE);
        }

        holder.btnDelivered.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Order currentOrder = orders.get(pos);

            ApiService api = RetrofitClient.getClient().create(ApiService.class);

            String newStatus;

            // 🔥 TOGGLE LOGIC
            if (currentOrder.status != null &&
                    currentOrder.status.equalsIgnoreCase("Out for Delivery")) {

                newStatus = "Delivered";

            } else {
                newStatus = "Out for Delivery";
            }
            api.updateStatus(
                            String.valueOf(currentOrder.id),
                            newStatus,
                            String.valueOf(currentOrder.rider_id)
                    )
                    .enqueue(new Callback<BasicResponse>() {

                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {

                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().isSuccess()) {

                                Toast.makeText(context, "Updated to " + newStatus, Toast.LENGTH_SHORT).show();

                                currentOrder.status = newStatus;
                                notifyItemChanged(pos);

                                // 🔥 refresh activity
                                if (listener != null) {
                                    listener.onUpdated();
                                }

                            } else {
                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    public void updateList(List<Order> newList) {
        this.orders = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }
}