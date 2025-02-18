package com.example.stylescheduler.Classes;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.R;
import java.util.ArrayList;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.MyViewHolder> {

    private ArrayList<Barber> barberList;

    public BarberAdapter(ArrayList<Barber> barberList) {
        this.barberList = barberList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView barberName, barberPhone, barberAddress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            barberName = itemView.findViewById(R.id.barber_name);
            barberPhone = itemView.findViewById(R.id.barber_phone);
            barberAddress = itemView.findViewById(R.id.barber_address);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch the name of the clicked item
                String name = barberList.get(holder.getAdapterPosition()).getName();

                // Show a toast with the item name
                Toast.makeText(v.getContext(), "moving to: " + name+"'s booking page", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_barberListFragment_to_barberBookingFragment);
//                NavController navController = Navigation.findNavController(holder.itemView);
//                navController.navigate(R.id.action_barberListFragment_to_barberBookingFragment);

//                NavController navController = Navigation.findNavController((Activity) v.getContext(), R.id.nav_host_fragment);
//                int currentFragmentId = navController.getCurrentDestination().getId();
//                Log.d("NavigationDebug", "Current Fragment ID: " + currentFragmentId);
//                navController.navigate(R.id.barberBookingFragment);



//                NavController navController = Navigation.findNavController(holder.itemView);
//                navController.navigate(R.id.action_barberListFragment_to_barberBookingFragment);


//                Navigation.findNavController(holder.itemView).navigate(R.id.action_barberListFragment_to_barberBookingFragment);
////                NavController navController = Navigation.findNavController((Activity) v.getContext(), R.id.clientnavgraph);
////                navController.navigate(R.id.action_barberListFragment_to_barberBookingFragment);
//                Bundle bundle = new Bundle();
//                bundle.putString("barberName", barber.getName());
//                Navigation.findNavController(holder.itemView).navigate(R.id.action_barberListFragment_to_barberBookingFragment, bundle);


            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }
}
