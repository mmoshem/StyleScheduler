package com.example.stylescheduler.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stylescheduler.Fragments.ClientAppointments;
import com.example.stylescheduler.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ClientAppointmentAdapter extends RecyclerView.Adapter<ClientAppointmentAdapter.ViewHolder> {
    private Context context;
    private List<Appointment> appointmentsList;
    private ClientAppointments fragment;

    public ClientAppointmentAdapter(Context context, List<Appointment> appointmentsList, ClientAppointments fragment) {
        this.context = context;
        this.appointmentsList = appointmentsList;
        this.fragment = fragment; // שומרים את ה- Fragment כדי לקרוא לפונקציה שלו
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentsList.get(position);

        // עיצוב התאריך והשעה
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String formattedDate = dateFormat.format(appointment.getAppointmentDate());
        String formattedTime = timeFormat.format(appointment.getAppointmentDate());

        // הצגת הנתונים
        holder.tvAppointmentDate.setText("📅 תאריך: " + formattedDate);
        holder.tvAppointmentTime.setText("🕒 שעה: " + formattedTime);
        holder.tvBarberName.setText("✂️ ספר: " + appointment.getBarber().getName());
        holder.tvBarberAddress.setText("📍 כתובת: " + appointment.getBarber().getShopAddress());
        holder.btnCancel.setOnClickListener(v -> fragment.cancelAppointment(appointment, position));

    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentDate, tvAppointmentTime, tvBarberName, tvBarberAddress;
        Button btnCancel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvAppointmentTime = itemView.findViewById(R.id.tvAppointmentTime);
            tvBarberName = itemView.findViewById(R.id.tvBarberName);
            tvBarberAddress = itemView.findViewById(R.id.tvBarberAddress);
            btnCancel = itemView.findViewById(R.id.btnCancel); // כפתור ביטול תור
        }
    }
}
