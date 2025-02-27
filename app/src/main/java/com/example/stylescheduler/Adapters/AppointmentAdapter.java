package com.example.stylescheduler.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.R;
import java.util.List;
import java.util.Map;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Map<String, String>> appointments;
    private OnCancelClickListener cancelClickListener;

    public interface OnCancelClickListener {
        void onCancelClick(Map<String, String> appointment, int position);
    }

    public AppointmentAdapter(List<Map<String, String>> appointments, OnCancelClickListener listener) {
        this.appointments = appointments;
        this.cancelClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBarberName, tvBarberAddress, tvAppointmentDate, tvAppointmentTime;
        Button btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBarberName = itemView.findViewById(R.id.tvBarberName);
            tvBarberAddress = itemView.findViewById(R.id.tvBarberAddress);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvAppointmentTime = itemView.findViewById(R.id.tvAppointmentTime);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> appointment = appointments.get(position);

        holder.tvBarberName.setText(appointment.get("name"));
        holder.tvBarberAddress.setText(appointment.get("barberAddress"));
        holder.tvAppointmentDate.setText(appointment.get("date"));
        holder.tvAppointmentTime.setText(appointment.get("appointmentTime"));

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
