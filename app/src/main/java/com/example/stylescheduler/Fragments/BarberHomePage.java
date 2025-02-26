package com.example.stylescheduler.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.AvailableAppointmentsAdapter;
import com.example.stylescheduler.Classes.Customer;
import com.example.stylescheduler.Classes.CustomerAppointment;
import com.example.stylescheduler.Classes.CustomerAppointmentAdapter;
import com.example.stylescheduler.MainActivity;
import com.example.stylescheduler.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BarberHomePage extends Fragment  implements CustomerAppointmentAdapter.OnCancelClickListener{

    private TextView textViewName;
    private CalendarView calendarView;
    private RecyclerView recyclerViewAvailableAppointments;
    private DatabaseReference barberRef;
    private FirebaseUser currentUser;
    private AvailableAppointmentsAdapter adapter;
    private List<String> availableAppointments = new ArrayList<>();
    private List<Integer> workingDays = new ArrayList<>();
    private String selectedDate;
    Button btnDeleteAll;
    String safeEmail;

    public BarberHomePage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_home_page, container, false);

        textViewName = view.findViewById(R.id.textViewname);
        calendarView = view.findViewById(R.id.calendarView);
        recyclerViewAvailableAppointments = view.findViewById(R.id.recyclerViewAppointments);
        btnDeleteAll = view.findViewById(R.id.btn_Delete);
        btnDeleteAll.setVisibility(view.GONE);
        Button button = view.findViewById(R.id.btn_update_info);
        Button btnlogout=view.findViewById(R.id.btn_logout);

        button.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_barberHomePage_to_barberUpdateInfoFragment));
        btnlogout.setOnClickListener(v -> signOut());

        recyclerViewAvailableAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            safeEmail = currentUser.getEmail().replace(".", "_");
            loadBarberInfo();
            loadWorkingDays();
            deleteExpiredAppointments();
        }
        loadCustomers();
        customerAppointmentsAdapter = new CustomerAppointmentAdapter(this);
        recyclerViewAvailableAppointments.setAdapter(customerAppointmentsAdapter);


        btnDeleteAll.setOnClickListener(v -> {
            deleteAllAppointmentsForDay(selectedDate);
        });

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            int selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 1;

            this.selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;

            DatabaseReference appointmentsRef = FirebaseDatabase.getInstance()
                    .getReference("appointments")
                    .child(safeEmail)
                    .child(this.selectedDate);

            appointmentsRef.get().addOnSuccessListener(dataSnapshot -> {
                boolean hasAppointments = dataSnapshot.exists();

                if (!workingDays.contains(selectedDayOfWeek) && !hasAppointments) {
                    recyclerViewAvailableAppointments.setVisibility(View.GONE);
                    btnDeleteAll.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "The barber is not working on this day!", Toast.LENGTH_SHORT).show();
                } else {
                    if(!hasAppointments) {
                        Toast.makeText(getContext(), "No appointments scheduled for this day", Toast.LENGTH_SHORT).show();
                    }
                    recyclerViewAvailableAppointments.setVisibility(View.VISIBLE);
                    btnDeleteAll.setVisibility(hasAppointments ? View.VISIBLE : View.GONE);
                    loadBarberAppointments(this.selectedDate);
                }
            });
        });
        return view;

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void loadBarberInfo() {
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String barberName = snapshot.child("name").getValue(String.class);
                    textViewName.setText("Hello, " + barberName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load barber info: " + error.getMessage());
            }
        });
    }
    private void loadWorkingDays() {
        if (currentUser == null) return;
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("Firebase", "No barber found.");
                    return;
                }

                workingDays.clear();

                Object data = snapshot.child("workingDays").getValue();
                Log.d("Firebase", "Data from Firebase: " + data);

                if (data instanceof List) {
                    List<?> daysList = (List<?>) data;
                    for (Object item : daysList) {
                        if (item instanceof Long) {
                            int dayNumber = ((Long) item).intValue();
                            workingDays.add(dayNumber);
                        } else if (item instanceof String) {
                            int dayNum = convertDayNameToNumber(item.toString().trim());
                            if (dayNum != -1) {
                                workingDays.add(dayNum);
                            } else {
                                Log.e("Firebase", "Invalid day format: " + item);
                            }
                        }
                    }
                    Log.d("Firebase", "Barber's working days (Processed): " + workingDays);
                } else {
                    Log.d("Firebase", "No valid working days format found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load working days: " + error.getMessage());
            }
        });
    }

    private int convertDayNameToNumber(String dayName) {
        switch (dayName.toLowerCase()) {
            case "sunday": return 0;
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            case "friday": return 5;
            case "saturday": return 6;
            default: return -1;
        }
    }
    private void deleteExpiredAppointments() {
        if (currentUser == null) return;
        String safeEmail = currentUser.getEmail().replace(".", "_");
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointments")
                .child(safeEmail);

        DatabaseReference clientsRef = FirebaseDatabase.getInstance()
                .getReference("appointmentsByClient");

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        appointmentsRef.get().addOnSuccessListener(dataSnapshot -> {
            if (!dataSnapshot.exists()) return;

            for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                String currDate = dateSnapshot.getKey();

                for(DataSnapshot timeSnapshot : dateSnapshot.getChildren()){
                    String timeStr = timeSnapshot.getKey();
                    String currDateTimeStr = currDate + " " + timeStr;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    try {
                        Date targetDateTime = dateFormat.parse(currDateTimeStr);
                        if (now.after(targetDateTime)) {
                            appointmentsRef.child(currDate).child(timeStr).removeValue();
                            clientsRef.get().addOnSuccessListener(clientSnapshot -> {
                                if (clientSnapshot.exists()) {
                                    for (DataSnapshot client : clientSnapshot.getChildren()) {
                                        DatabaseReference clientAppointmentRef = clientsRef.child(client.getKey()).child(currDate).child(timeStr);
                                        clientAppointmentRef.removeValue();
                                    }
                                }
                            });
                        }

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
    private CustomerAppointmentAdapter customerAppointmentsAdapter;
    private HashMap<String, Customer> customerHashMap = new HashMap<>();

    private void loadCustomers() {
        AlertDialog pd = new ProgressDialog.Builder(requireContext()).create();
        pd.setMessage("Loading appointments");
        pd.show();
        FirebaseDatabase.getInstance().getReference("customers")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Log.d("Firebase", "No customers found in database.");
                            return;
                        }
                        for (DataSnapshot customer : dataSnapshot.getChildren()) {
                            String customerEmail = customer.child("email").getValue(String.class);
                            String customerName = customer.child("name").getValue(String.class);
                            String customerPhone = customer.child("phoneNumber").getValue(String.class);
                            Customer c = new Customer();
                            c.setEmail(customerEmail);
                            c.setName(customerName);
                            c.setPhoneNumber(customerPhone);
                            customerHashMap.put(customerEmail.replace(".", "_"), c);
                        }
                        pd.dismiss();
                    }
                }).addOnFailureListener(e -> pd.dismiss());
    }

    private void loadBarberAppointments(String date) {
        customerAppointmentsAdapter.clear();
        if (currentUser == null || customerHashMap.isEmpty()) return;

        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments").child(safeEmail).child(date);

        appointmentsRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()) {
                                    return;
                                }
                                List<CustomerAppointment> showingAppointments = new ArrayList<>();

                               for(DataSnapshot appointment : dataSnapshot.getChildren()) {
                                        String time = appointment.getKey();
                                        String customerEmail = appointment.child("customerEmail").getValue(String.class);
                                        CustomerAppointment ap = new CustomerAppointment(customerEmail, time);
                                        showingAppointments.add(ap);
                                }
                               customerAppointmentsAdapter.setData(date, showingAppointments, customerHashMap);
                                if (showingAppointments.isEmpty()) {
                                    btnDeleteAll.setVisibility(View.GONE);
                                    recyclerViewAvailableAppointments.setVisibility(View.GONE);
                                } else {
                                    btnDeleteAll.setVisibility(View.VISIBLE);
                                    recyclerViewAvailableAppointments.setVisibility(View.VISIBLE);
                                }
                            }
                        });
    }

    private void deleteAllAppointmentsForDay(String date) {
        if (currentUser == null) return;

        DatabaseReference barberAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointments")
                .child(safeEmail)
                .child(date);

        DatabaseReference customerAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointmentsByClient");

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete all appointments")
                .setMessage("Are you sure you want to delete all appointments for this day?")
                .setPositiveButton("YES", (dialog, which) -> {

                    barberAppointmentsRef.get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot appointment : dataSnapshot.getChildren()) {
                                String time = appointment.getKey();
                                String customerEmail = appointment.child("customerEmail").getValue(String.class);

                                if (customerEmail != null) {
                                    String safeCustomerEmail = customerEmail.replace(".", "_");

                                    DatabaseReference customerAppointment = customerAppointmentsRef
                                            .child(safeCustomerEmail)
                                            .child(date)
                                            .child(time);
                                    customerAppointment.removeValue();
                                }
                            }

                            barberAppointmentsRef.removeValue().addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "כל התורים נמחקו בהצלחה!", Toast.LENGTH_SHORT).show();

                                customerAppointmentsAdapter.clear();
                                recyclerViewAvailableAppointments.setVisibility(View.GONE);

                                btnDeleteAll.setVisibility(View.GONE);

                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error deleting appointments!", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "No appointments for this day!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onCancelClick(CustomerAppointment appointment, int position) {

        String customerEmail = appointment.getCustomerEmail().replace(".", "_");
        String appointmentDate = this.selectedDate;
        String appointmentTime = appointment.getTime();

        Log.d("Cancel", "Canceling appointment: " + appointmentDate + " at " + appointmentTime +
                " barberEmail: " + safeEmail + " customerEmail: " + customerEmail);

        new AlertDialog.Builder(requireContext())
                .setTitle("Appointment cancellation")
                .setMessage("Are you sure you want to cancel the appointment at " + appointmentTime + "?")
                .setPositiveButton("YES", (dialog, which) -> {
                    DatabaseReference barberAppointmentRef = FirebaseDatabase.getInstance()
                            .getReference("appointments")
                            .child(safeEmail)
                            .child(appointmentDate)
                            .child(appointmentTime);

                    DatabaseReference customerAppointmentRef = FirebaseDatabase.getInstance()
                            .getReference("appointmentsByClient")
                            .child(customerEmail)
                            .child(appointmentDate)
                            .child(appointmentTime);

                    barberAppointmentRef.removeValue().addOnSuccessListener(aVoid -> {
                        customerAppointmentRef.removeValue().addOnSuccessListener(aVoid2 -> {
                            Toast.makeText(getContext(), "The appointment was successfully canceled.", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error deleting the appointment from the client: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error canceling the appointment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

