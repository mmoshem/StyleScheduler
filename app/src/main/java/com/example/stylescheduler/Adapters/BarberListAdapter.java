package com.example.stylescheduler.Adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.R;
import java.util.List;

public class BarberListAdapter extends RecyclerView.Adapter<BarberListAdapter.ViewHolder> {

    private List<Barber> barberList;

    public BarberListAdapter(List<Barber> barberList) {
        this.barberList = barberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.barberlistcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barber barber = barberList.get(position);
        holder.textViewBarberName.setText(barber.getShopName());
        holder.textViewBarberAddress.setText(barber.getShopAddress());

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("barberId", barber.getId()); // Pass barber ID
            Navigation.findNavController(v).navigate(R.id.action_barberListFragment_to_bookingFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBarberName, textViewBarberAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBarberName = itemView.findViewById(R.id.textViewBarberName);
            textViewBarberAddress = itemView.findViewById(R.id.textViewBarberAddress);
        }
    }
}
