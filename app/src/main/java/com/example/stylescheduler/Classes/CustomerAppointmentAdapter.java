package com.example.stylescheduler.Classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylescheduler.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerAppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<CustomerAppointment> appointments = new ArrayList<>();
    private HashMap<String, Customer> customers = new HashMap<>();
    private OnCancelClickListener cancelClickListener;

    private String date;
    public interface OnCancelClickListener {
        void onCancelClick(CustomerAppointment appointment, int position);
    }

    public CustomerAppointmentAdapter(OnCancelClickListener listener) {
        this.cancelClickListener = listener;
    }

    public void setData(String date,
                        List<CustomerAppointment> appointments,
                        HashMap<String, Customer> customers) {
        this.date = date;
        this.appointments = appointments;
        this.customers = customers;
        notifyDataSetChanged();
    }

    public void clear() {
        appointments = new ArrayList<>();
        customers = new HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.ViewHolder holder, int position) {
        CustomerAppointment appointment = appointments.get(position);
        Customer c = customers.get(appointment.getCustomerEmail().replace(".", "_"));
        holder.tvBarberName.setText(c.getName());
        holder.tvBarberAddress.setVisibility(View.GONE);
        holder.tvAppointmentDate.setText(date);
        holder.tvAppointmentTime.setText(appointment.getTime());

        // Cancel button
        holder.btnCancel.setOnClickListener(v -> {
            if (cancelClickListener != null) {
                cancelClickListener.onCancelClick(appointment, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
}
