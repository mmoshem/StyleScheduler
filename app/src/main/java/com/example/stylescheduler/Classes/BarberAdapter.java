package com.example.stylescheduler.Classes;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.R;
import java.util.ArrayList;
import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.MyViewHolder> {

    private ArrayList<Barber> barberList;

    public BarberAdapter(ArrayList<Barber> barberList) {
        this.barberList = barberList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView barberName, barberPhone, barberAddress,barberWorkingDays,barberWorkingHours;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            barberName = itemView.findViewById(R.id.barber_name);
            barberPhone = itemView.findViewById(R.id.barber_phone);
            barberAddress = itemView.findViewById(R.id.barber_address);
            barberWorkingDays = itemView.findViewById(R.id.barber_working_days);
            barberWorkingHours = itemView.findViewById(R.id.barber_working_hours);
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
        holder.barberName.setText(barber.getName());
        holder.barberPhone.setText("📞 " + barber.getPhoneNumber());
        holder.barberAddress.setText("📍 " + barber.getShopAddress());
        holder.barberWorkingHours.setText("🕒 " + barber.getAStringOfWorkingHours());
        List<Integer> workingDaysNumbers = barber.getWorkSchedule().getWorkingDays();
        Log.d("BarberAdapter", "Retrieved working days: " + workingDaysNumbers);
        WorkSchedule workSchedule = new WorkSchedule();
        List<String> workingDaysNames = new ArrayList<>();

        for (Integer day : workingDaysNumbers) {
            workingDaysNames.add(workSchedule.getDayName(day)); // Convert Integer to String
        }
        Log.d("BarberAdapter", "Working days: " + workingDaysNames);
        // Set formatted working days text
        holder.barberWorkingDays.setText(TextUtils.join(", ", workingDaysNames));

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Fetch the name of the clicked item
////                String name = barberList.get(holder.getAdapterPosition()).getName();
//                Bundle bundle = new Bundle();
//                bundle.putString("barberEmail", barber.getEmail()); // שולחים את ה-Email
//                // Show a toast with the item name
////                Toast.makeText(v.getContext(), "Clicked on: " + name +"'s booking page", Toast.LENGTH_SHORT).show();
//                Navigation.findNavController(v).navigate(R.id.action_barberListFragment_to_barberBookingFragment, bundle);
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = barber.getEmail();
                Log.d("BarberAdapter", "Clicked barber email: " + email); // Debug before checking null

                if (email == null) {
                    Log.e("BarberAdapter", "Error: Barber email is NULL! Continuing navigation anyway...");
                    email = "default_email@example.com"; //  Avoid null issues
                }

                Bundle bundle = new Bundle();
                bundle.putString("barberEmail", email); //  Send email
                Navigation.findNavController(v).navigate(R.id.action_barberListFragment_to_barberBookingFragment, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }
}
