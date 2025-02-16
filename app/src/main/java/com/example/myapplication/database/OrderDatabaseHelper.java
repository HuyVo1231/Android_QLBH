package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.model.Order;
import com.example.myapplication.model.OrderDetail;
import com.example.myapplication.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDatabaseHelper {
    private SQLiteDatabase database;
    private ConnectDatabase dbHelper;

    public OrderDatabaseHelper(Context context) {
        dbHelper = new ConnectDatabase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("address_id", order.getAddressId());
        values.put("total_amount", order.getTotalAmount());
        values.put("payment_method", order.getPaymentMethod());
        values.put("order_date", TimeUtil.getCurrentTimeInVietnam());
        values.put("delivery_date", TimeUtil.getCurrentTimeInVietnam());
        values.put("status", order.getStatus());
        values.put("note", order.getNote());

        long orderId = database.insert("`order`", null, values);
        if (orderId == -1) {
            Log.e("Database", "Thêm đơn hàng thất bại");
        } else {
            Log.d("Database", "Đơn hàng đã được thêm với orderId: " + orderId);
        }

        return orderId;
    }

    public long addOrderDetail(OrderDetail orderDetail) {
        ContentValues values = new ContentValues();
        values.put("order_id", orderDetail.getOrderId());
        values.put("product_id", orderDetail.getProductId());
        values.put("quantity", orderDetail.getQuantity());
        values.put("price", orderDetail.getPrice());

        long orderDetailId = database.insert("order_detail", null, values);
        if (orderDetailId == -1) {
            Log.e("Database", "Thêm chi tiết đơn hàng thất bại");
        } else {
            Log.d("Database", "Chi tiết đơn hàng đã được thêm với orderDetailId: " + orderDetailId);
        }

        return orderDetailId;
    }

    // Get Order by id.
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String[] columns = {
                "order_id", "user_id", "address_id", "total_amount", "payment_method", "order_date", "status", "note", "delivery_date"
        };
        String selection = "user_id = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = database.query("`order`", columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
                int addressId = cursor.getInt(cursor.getColumnIndexOrThrow("address_id"));
                double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                int paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("payment_method"));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));

                String deliveryDate = cursor.getString(cursor.getColumnIndexOrThrow("delivery_date"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

                Order order = new Order(orderId, userId, addressId, totalAmount, paymentMethod, orderDate, status,note, deliveryDate);
                orders.add(order);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return orders;
    }

    public Order getOrderById(int orderId) {
        Order order = null;
        String[] columns = {
                "order_id", "user_id", "address_id", "total_amount", "payment_method", "order_date", "status", "note", "delivery_date"
        };
        String selection = "order_id = ?";
        String[] selectionArgs = { String.valueOf(orderId) };

        Cursor cursor = database.query("`order`", columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            int addressId = cursor.getInt(cursor.getColumnIndexOrThrow("address_id"));
            double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
            int paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("payment_method"));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
            String deliveryDate = cursor.getString(cursor.getColumnIndexOrThrow("delivery_date"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

            order = new Order(orderId, userId, addressId, totalAmount, paymentMethod, orderDate, status, note, deliveryDate);
            cursor.close();
        }

        return order;
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT od.order_detail_id, od.order_id, od.product_id, od.quantity, od.price, " +
                "p.name AS product_name, p.image_url AS product_image " +
                "FROM order_detail od " +
                "JOIN product2 p ON od.product_id = p.id " +
                "WHERE od.order_id = ?";

        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(orderId) });
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int orderDetailId = cursor.getInt(cursor.getColumnIndexOrThrow("order_detail_id"));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
                String productImage = cursor.getString(cursor.getColumnIndexOrThrow("product_image"));

                OrderDetail orderDetail = new OrderDetail(orderDetailId, orderId, productId, quantity, price, productName, productImage);
                orderDetails.add(orderDetail);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return orderDetails;
    }

    // update trạng thái.
    public void updateOrderStatus(int orderId, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);

        String selection = "order_id = ?";
        String[] selectionArgs = { String.valueOf(orderId) };

        int count = database.update("`order`", values, selection, selectionArgs);
        if (count == -1) {
            Log.e("Database", "Cập nhật trạng thái đơn hàng thất bại");
        } else {
            Log.d("Database", "Trạng thái đơn hàng đã được cập nhật cho orderId: " + orderId);
        }
    }

    // Lấy all order.
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String[] columns = {
                "order_id", "user_id", "address_id", "total_amount", "payment_method", "order_date", "delivery_date", "status", "note", "delivery_date"
        };

        Cursor cursor = database.query("`order`", columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                int addressId = cursor.getInt(cursor.getColumnIndexOrThrow("address_id"));
                double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                int paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("payment_method"));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
                String deliveryDate = cursor.getString(cursor.getColumnIndexOrThrow("delivery_date"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

                Order order = new Order(orderId, userId, addressId, totalAmount, paymentMethod, orderDate, status, note, deliveryDate);
                orders.add(order);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return orders;
    }

    // Delete order.
    public boolean deleteOrder(int orderId) {
        // Delete the order details first
        String orderDetailSelection = "order_id = ?";
        String[] orderDetailSelectionArgs = { String.valueOf(orderId) };

        int deletedOrderDetailsRows = database.delete("order_detail", orderDetailSelection, orderDetailSelectionArgs);
        if (deletedOrderDetailsRows == -1) {
            Log.e("Database", "Xóa chi tiết đơn hàng thất bại cho orderId: " + orderId);
            return false;
        }

        // Delete the order
        String orderSelection = "order_id = ?";
        String[] orderSelectionArgs = { String.valueOf(orderId) };

        int deletedOrderRows = database.delete("`order`", orderSelection, orderSelectionArgs);
        if (deletedOrderRows == -1) {
            Log.e("Database", "Xóa đơn hàng thất bại");
            return false;
        } else {
            Log.d("Database", "Đơn hàng và chi tiết đơn hàng đã được xóa với orderId: " + orderId);
            return true;
        }
    }

    public List<Map<String, Object>> getTopSellingProducts(String timeframe, String dateValue) {
        List<Map<String, Object>> result = new ArrayList<>();

        String query = "";
        if (timeframe.equals("day")) {
            query = "SELECT p.name AS product_name, " +
                    "SUM(od.quantity) AS total_quantity, " +
                    "SUM(od.quantity * od.price) AS total_revenue " +
                    "FROM order_detail od " +
                    "JOIN `order` o ON od.order_id = o.order_id " +
                    "JOIN product2 p ON od.product_id = p.id " +
                    "WHERE DATE(o.order_date) = ? " +
                    "GROUP BY p.id " +
                    "ORDER BY total_quantity DESC";

        } else if (timeframe.equals("month")) {
            query = "SELECT p.name AS product_name, " +
                    "SUM(od.quantity) AS total_quantity, " +
                    "SUM(od.quantity * od.price) AS total_revenue " +
                    "FROM order_detail od " +
                    "JOIN `order` o ON od.order_id = o.order_id " +
                    "JOIN product2 p ON od.product_id = p.id " +
                    "WHERE strftime('%Y-%m', o.order_date) = ? " +
                    "GROUP BY p.id " +
                    "ORDER BY total_quantity DESC";
        } else if (timeframe.equals("year")) {
            query = "SELECT p.name AS product_name, " +
                    "SUM(od.quantity) AS total_quantity, " +
                    "SUM(od.quantity * od.price) AS total_revenue " +
                    "FROM order_detail od " +
                    "JOIN `order` o ON od.order_id = o.order_id " +
                    "JOIN product2 p ON od.product_id = p.id " +
                    "WHERE strftime('%Y', o.order_date) = ? " +
                    "GROUP BY p.id " +
                    "ORDER BY total_quantity DESC";
        }

        // Thực thi truy vấn
        Cursor cursor = database.rawQuery(query, new String[]{dateValue});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new HashMap<>();
                row.put("product_name", cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
                row.put("total_quantity", cursor.getInt(cursor.getColumnIndexOrThrow("total_quantity")));
                row.put("total_revenue", cursor.getDouble(cursor.getColumnIndexOrThrow("total_revenue")));
                result.add(row);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return result;
    }

    public int[] getOrderStatistics() {
        int[] stats = new int[3];

        Cursor cursor;

        // Lấy số lượng đơn hàng "pending"
        cursor = database.rawQuery("SELECT COUNT(*) FROM `order` WHERE status = 'Đang giao hàng'", null);
        if (cursor != null && cursor.moveToFirst()) {
            stats[0] = cursor.getInt(0);
            cursor.close();
        }

        // Lấy số lượng đơn hàng "completed"
        cursor = database.rawQuery("SELECT COUNT(*) FROM `order` WHERE status = 'Đã nhận hàng'", null);
        if (cursor != null && cursor.moveToFirst()) {
            stats[1] = cursor.getInt(0);
            cursor.close();
        }

        // Lấy tổng doanh thu từ các đơn hàng đã hoàn thành
        cursor = database.rawQuery("SELECT SUM(total_amount) FROM `order` WHERE status = 'completed'", null);
        if (cursor != null && cursor.moveToFirst()) {
            stats[2] = cursor.isNull(0) ? 0 : cursor.getInt(0);
            cursor.close();
        }

        return stats;
    }



}
