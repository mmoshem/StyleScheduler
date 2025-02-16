package com.example.stylescheduler.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.R;
import java.util.List;

public class ClientAppointmentsAdapter extends RecyclerView.Adapter<ClientAppointmentsAdapter.ViewHolder> {

    private List<Appointment> appointmentList;

    public ClientAppointmentsAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        if (appointment.getBarber() != null) {
            holder.textViewBarberName.setText(appointment.getBarber().getName());
            holder.textViewBarberAddress.setText(appointment.getBarber().getShopAddress());
        } else {
            holder.textViewBarberName.setText("Unknown Barber");
            holder.textViewBarberAddress.setText("No Address Available");
        }

        if (appointment.getAppointmentTime() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.textViewAppointmentTime.setText(appointment.getAppointmentTime().toString());
        } else {
            holder.textViewAppointmentTime.setText("Unknown Time");
        }

        holder.textViewServiceType.setText(appointment.getServiceType() != null ? appointment.getServiceType() : "No Service");

        holder.buttonCancelAppointment.setOnClickListener(v -> removeAppointment(position));
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBarberName, textViewBarberAddress, textViewAppointmentTime, textViewServiceType;
        Button buttonCancelAppointment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBarberName = itemView.findViewById(R.id.textViewBarberName);
            textViewBarberAddress = itemView.findViewById(R.id.textViewBarberAddress);
            textViewAppointmentTime = itemView.findViewById(R.id.textViewAppointmentTime);
            textViewServiceType = itemView.findViewById(R.id.textViewServiceType);
            buttonCancelAppointment = itemView.findViewById(R.id.buttonCancelAppointment);
        }
    }

    private void removeAppointment(int position) {
        appointmentList.remove(position);
        notifyItemRemoved(position);
    }
}
