package com.example.stylescheduler.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.R;
import java.util.List;

public class AvailableAppointmentsAdapter extends RecyclerView.Adapter<AvailableAppointmentsAdapter.ViewHolder> {

    private List<String> availableAppointments;
    private OnItemClickListener listener;

    public void deleteItemAtPos(int position) {
        availableAppointments.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onItemClick(String timeSlot);
    }

    public AvailableAppointmentsAdapter(List<String> availableAppointments, OnItemClickListener listener) {
        this.availableAppointments = availableAppointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.barber_available_appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String timeSlot = availableAppointments.get(position);
        holder.textViewTimeSlot.setText(timeSlot);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(timeSlot));
    }

    @Override
    public int getItemCount() {
        return availableAppointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTimeSlot;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTimeSlot = itemView.findViewById(R.id.textViewAppointmentTime);
        }
    }
}
