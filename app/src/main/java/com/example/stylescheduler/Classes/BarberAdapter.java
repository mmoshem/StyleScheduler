package com.example.stylescheduler.Classes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.MyViewHolder> {

    private ArrayList<Barber> barberList;

    public BarberAdapter(ArrayList<Barber> barberList) {
        this.barberList = barberList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView shopName, barberName, barberPhone, barberAddress, barberWorkingHours, barberNextAppointment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shop_name);
            barberName = itemView.findViewById(R.id.barber_name);
            barberPhone = itemView.findViewById(R.id.barber_phone);
            barberAddress = itemView.findViewById(R.id.barber_address);
            barberWorkingHours = itemView.findViewById(R.id.barber_working_hours);
            barberNextAppointment = itemView.findViewById(R.id.barber_next_appointment);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.barberlistcard, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Barber barber = barberList.get(position);

        holder.shopName.setText(barber.getShopName());
        holder.barberName.setText(barber.getName());
        holder.barberPhone.setText("📞 " + barber.getPhoneNumber());
        holder.barberAddress.setText("📍 " + barber.getShopAddress());

        // Set working days and hours
        String workingDays = barber.getWorkingDays();
        String workingHours = barber.getWorkingHours();
        holder.barberWorkingHours.setText("🕒 " + workingDays + " | " + workingHours);

        // Set next available appointment
        Date nextAppointment = barber.getNextAvailableAppointment();
        if (nextAppointment != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            holder.barberNextAppointment.setText("📅 Next Available: " + sdf.format(nextAppointment));
        } else {
            holder.barberNextAppointment.setText("📅 No available slots");
        }
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    // 📌 פונקציה לעדכון הנתונים ברשימה בצורה דינמית
    public void updateBarberList(ArrayList<Barber> newBarberList) {
        this.barberList = newBarberList;
        notifyDataSetChanged();
    }
}
