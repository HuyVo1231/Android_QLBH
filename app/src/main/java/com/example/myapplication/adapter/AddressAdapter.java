package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AddressActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.AddressDatabaseHelper;
import com.example.myapplication.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private int selectedAddressId;
    private OnItemClickListener onItemClickListener;
    private AddressDatabaseHelper databaseHelper;

    public AddressAdapter(Context context, List<Address> addressList, int selectedAddressId) {
        this.addressList = addressList;
        this.selectedAddressId = selectedAddressId;
        this.databaseHelper = new AddressDatabaseHelper(context);
        this.databaseHelper.open();
    }

    // Interface to define a set of methods
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.bind(address, address.getId() == selectedAddressId);
        holder.setupListeners(position, address);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public Address getSelectedAddress() {
        for (Address address : addressList) {
            if (address.getId() == selectedAddressId) {
                return address;
            }
        }
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRecipientName, tvPhone, tvAddress;
        private CheckBox checkBox;
        private LinearLayout layoutInfo;
        private ImageView btnDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipientName = itemView.findViewById(R.id.tvRecipientName1);
            tvPhone = itemView.findViewById(R.id.tvPhone1);
            tvAddress = itemView.findViewById(R.id.tvAddress1);
            checkBox = itemView.findViewById(R.id.checkboxAddress);
            layoutInfo = itemView.findViewById(R.id.layoutInfo);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Address address, boolean isSelected) {
            tvRecipientName.setText(address.getRecipientName());
            tvPhone.setText(address.getPhoneNumber());
            tvAddress.setText(address.getAddress());
            checkBox.setChecked(isSelected);
        }

        public void setupListeners(int position, Address address) {
            setupCheckBoxListener(position);
            setupLayoutInfoClickListener(address);
            setupDeleteButtonListener(position, address);
        }

        private void setupCheckBoxListener(int position) {
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(addressList.get(position).getId() == selectedAddressId);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedAddressId = addressList.get(position).getId();
                    notifyDataSetChanged();
                } else if (addressList.get(position).getId() == selectedAddressId) {
                    selectedAddressId = -1;
                    notifyDataSetChanged();
                }

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(selectedAddressId);
                }
            });
        }

        private void setupLayoutInfoClickListener(Address address) {
            layoutInfo.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), AddressActivity.class);
                intent.putExtra("idAddress", address.getId());
                intent.putExtra("recipientName", address.getRecipientName());
                intent.putExtra("phone", address.getPhoneNumber());
                intent.putExtra("address", address.getAddress());
                intent.putExtra("note", address.getNote());
                ((Activity) v.getContext()).startActivityForResult(intent, 1);
            });
        }

        private void setupDeleteButtonListener(int position, Address address) {
            btnDelete.setOnClickListener(v -> {
                // Delete from the database
                databaseHelper.deleteAddressById(address.getId());

                // Remove from the list and update UI
                addressList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, addressList.size());

                // If the deleted item was selected, reset selectedAddressId
                if (address.getId() == selectedAddressId) {
                    selectedAddressId = -1;
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(selectedAddressId);
                    }
                }
            });
        }
    }
}
