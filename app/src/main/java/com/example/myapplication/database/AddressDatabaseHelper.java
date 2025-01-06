package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressDatabaseHelper {
    private SQLiteDatabase database;
    private ConnectDatabase dbHelper;

    public AddressDatabaseHelper(Context context) {
        dbHelper = new ConnectDatabase(context);
    }

    // Open database connection
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Close database connection
    public void close() {
        dbHelper.close();
    }

    // Add a new address
    public long addAddress(Address address) {
        ContentValues values = new ContentValues();
        values.put("user_id", address.getUserId());
        values.put("recipient_name", address.getRecipientName());
        values.put("phone_number", address.getPhoneNumber());
        values.put("address", address.getAddress());
        values.put("note", address.getNote());

        long rowId = database.insert("shipping_address", null, values);

        if (rowId == -1) {
            Log.e("Database", "Thêm địa chỉ thất bại");
        } else {
            Log.d("Database", "Địa chỉ đã được thêm với rowId: " + rowId);
        }

        return rowId;
    }

    // Update an existing address
    public boolean updateAddress(Address address) {
        ContentValues values = new ContentValues();
        values.put("recipient_name", address.getRecipientName());
        values.put("phone_number", address.getPhoneNumber());
        values.put("address", address.getAddress());
        values.put("note", address.getNote());

        int rowsAffected = database.update("shipping_address", values, "id = ?", new String[]{String.valueOf(address.getId())});
        return rowsAffected > 0;
    }

    // Get all addresses by user ID
    public ArrayList<Address> getAllAddressesByUserId(int userId) {
        ArrayList<Address> addressList = new ArrayList<>();
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = database.query("shipping_address", null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String recipientName = cursor.getString(cursor.getColumnIndexOrThrow("recipient_name"));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

                Address addr = new Address(id, userId, recipientName, phoneNumber, address, note);
                addressList.add(addr);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return addressList;
    }

    public List<Address> getAddressById(int addressId) {
        List<Address> addressList = new ArrayList<>();
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(addressId)};

        Cursor cursor = database.query("shipping_address", null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String recipientName = cursor.getString(cursor.getColumnIndexOrThrow("recipient_name"));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                String addressStr = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

                Address address = new Address(id, userId, recipientName, phoneNumber, addressStr, note);
                addressList.add(address);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return addressList;
    }



    // Delete by id.
    public boolean deleteAddressById(int id) {
        int rowsDeleted = database.delete("shipping_address", "id = ?", new String[]{String.valueOf(id)});
        return rowsDeleted > 0;
    }




}
