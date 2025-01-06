package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.OrderDetailsActivity;
import com.example.myapplication.R;
import com.example.myapplication.admin.AdminActivityListOrder;
import com.example.myapplication.database.OrderDatabaseHelper;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.UserManager;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderTime.setText(order.getOrderDate());
        holder.orderStatus.setText(order.getStatus());
        holder.orderPrice.setText("$" + order.getTotalAmount());
        holder.orderNote.setText("Ghi chú: " + order.getNote());

        // Set the status icon based on the order status
        if (order.getStatus().equalsIgnoreCase("Đang giao hàng")) {
            holder.statusIcon.setImageResource(R.drawable.ic_delivery);
        } else if (order.getStatus().equalsIgnoreCase("Đã nhận hàng")) {
            holder.statusIcon.setImageResource(R.drawable.ic_orderring);
        } else if (order.getStatus().equalsIgnoreCase("Hủy đơn hàng")) {
            holder.statusIcon.setImageResource(R.drawable.ic_recevied);
        }

        if (UserManager.getInstance().getUser().isAdmin()) {
            holder.closeIcon.setVisibility(View.VISIBLE);
        } else {
            holder.closeIcon.setVisibility(View.GONE);
        }

        // Set click listener on the root layout
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("user_id", order.getUserId());
            intent.putExtra("order_id", order.getOrderId());
            intent.putExtra("address_id", order.getAddressId());

            if (UserManager.getInstance().getUser().isAdmin()) {
                ((AdminActivityListOrder) context).startActivityForResult(intent, 1);
            } else {
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });

        // Set click listener on the closeIcon
        holder.closeIcon.setOnClickListener(v -> {
            Log.d("OrderAdapter", "Close icon clicked for order: " + order.getOrderId());
            // Open the database
            OrderDatabaseHelper dbHelper = new OrderDatabaseHelper(context);
            dbHelper.open();
            // Delete the order from the database
            boolean isDeleted = dbHelper.deleteOrder(order.getOrderId());

            // Close the database
            dbHelper.close();

            if (isDeleted) {
                // Remove the order from the list and notify the adapter
                orders.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, orders.size());
            } else {
                Log.e("OrderAdapter", "Failed to delete order: " + order.getOrderId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderTime, orderStatus, orderPrice, orderNote;
        ImageView orderImage, statusIcon, closeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderImage = itemView.findViewById(R.id.orderImage);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderNote = itemView.findViewById(R.id.orderNote);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            closeIcon = itemView.findViewById(R.id.closeIcon);
        }
    }
}
