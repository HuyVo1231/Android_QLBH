package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ConnectDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database_food_app.db";
    private static final int DATABASE_VERSION = 1;

    public ConnectDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng product
        String createProductTableSQL = "CREATE TABLE IF NOT EXISTS product (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "price REAL);";
        db.execSQL(createProductTableSQL);

        // Tạo bảng users với đầy đủ thông tin từ model User
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT, " +
                "full_name TEXT, " +
                "email TEXT, " +
                "isAdmin INTEGER DEFAULT 0, " +
                "phone TEXT, " +
                "address TEXT, " +
                "imageUrl TEXT);";
        db.execSQL(createUserTableSQL);


        String createCategoryTableSQL = "CREATE TABLE IF NOT EXISTS category (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_category TEXT, " +
                "name TEXT, " +
                "image_url TEXT);";
        db.execSQL(createCategoryTableSQL);


        String createProduct2TableSQL = "CREATE TABLE IF NOT EXISTS product2 (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_code TEXT, " +
                "name TEXT, " +
                "price REAL, " +
                "description TEXT, " +
                "image_url TEXT, " +
                "category_id TEXT, " +
                "sold_quantity INTEGER DEFAULT 0, " +
                "FOREIGN KEY(category_id) REFERENCES category(id_category) " +
                ");";
        db.execSQL(createProduct2TableSQL);

        // Tạo bảng shipping_address
        String createShippingAddressTableSQL = "CREATE TABLE IF NOT EXISTS shipping_address (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "recipient_name TEXT, " +
                "phone_number TEXT, " +
                "address TEXT, " +
                "note TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES user(id)" +
                ");";
        db.execSQL(createShippingAddressTableSQL);


        // Tạo bảng Order (đơn hàng)
        String createOrderTableSQL = "CREATE TABLE IF NOT EXISTS `order` (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "address_id INTEGER, " +
                "total_amount REAL, " +
                "payment_method INTEGER, " +
                "order_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "status TEXT, " +
                "note TEXT, " +
                "delivery_date DATETIME, " +
                "FOREIGN KEY(user_id) REFERENCES user(id), " +
                "FOREIGN KEY(address_id) REFERENCES shipping_address(id)" +
                ");";
        db.execSQL(createOrderTableSQL);


        // Tạo bảng OrderDetail (chi tiết đơn hàng)
        String createOrderDetailTableSQL = "CREATE TABLE IF NOT EXISTS order_detail (" +
                "order_detail_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "FOREIGN KEY(order_id) REFERENCES `order`(order_id), " +
                "FOREIGN KEY(product_id) REFERENCES product2(id)" +
                ");";
        db.execSQL(createOrderDetailTableSQL);


        Log.d("Database", "Tạo bảng thành công.");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
