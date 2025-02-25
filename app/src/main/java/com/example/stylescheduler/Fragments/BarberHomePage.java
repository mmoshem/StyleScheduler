package com.example.stylescheduler.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

//import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.Classes.AvailableAppointmentsAdapter;
import com.example.stylescheduler.Classes.Customer;
import com.example.stylescheduler.Classes.CustomerAppointment;
import com.example.stylescheduler.Classes.CustomerAppointmentAdapter;
import com.example.stylescheduler.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

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
    private String selectedDate;  // תאריך שנבחר מהלוח שנה
    Button btnDeleteAll;
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
        button.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_barberHomePage_to_barberUpdateInfoFragment));

        // הגדרת ה-RecyclerView
        recyclerViewAvailableAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        /*adapter = new AvailableAppointmentsAdapter(availableAppointments, timeSlot -> {
            Log.d("RecyclerView", " Clicked time slot: " + timeSlot);
        });*/

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadBarberInfo();
            loadWorkingDays();
        }
        loadCustomers();
        customerAppointmentsAdapter = new CustomerAppointmentAdapter(this);
        recyclerViewAvailableAppointments.setAdapter(customerAppointmentsAdapter);

        //הוספה שלי !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        btnDeleteAll.setOnClickListener(v -> {
            deleteAllAppointmentsForDay(selectedDate);
        });

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);


            int selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 1; // Android מחזיר 1 = Sunday, 2 = Monday וכו'

            Log.d("Calendar", "Selected date: " + dayOfMonth + "/" + (month + 1) + "/" + year + " (Day: " + selectedDayOfWeek + ")");
            Log.d("Calendar", "Barber's working days: " + workingDays);

            if (!workingDays.contains(selectedDayOfWeek)) {
                Log.w("Calendar", " הספר לא עובד ביום הזה!");
                Toast.makeText(getContext(), " הספר לא עובד ביום הזה!", Toast.LENGTH_SHORT).show();
                recyclerViewAvailableAppointments.setVisibility(View.GONE);
                btnDeleteAll.setVisibility(view.GONE);
            } else {
                recyclerViewAvailableAppointments.setVisibility(View.VISIBLE);
                this.selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                loadBarberAppointments(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        });
        return view;

    }

    private void loadBarberInfo() {
        String safeEmail = currentUser.getEmail().replace(".", "_");
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

        String safeEmail = currentUser.getEmail().replace(".", "_");
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("Firebase", "No barber found.");
                    return;
                }

                workingDays.clear(); // ננקה את הרשימה

                Object data = snapshot.child("workingDays").getValue();
                Log.d("Firebase", "Data from Firebase: " + data);

                if (data instanceof List) {
                    List<?> daysList = (List<?>) data;
                    for (Object item : daysList) {
                        Log.d("Firebase", "Raw item: " + item);

                        if (item instanceof Long) {
                            int dayNumber = ((Long) item).intValue();
                            workingDays.add(dayNumber);
                            Log.d("Firebase", "Added numeric day: " + dayNumber);
                        } else if (item instanceof String) {
                            int dayNum = convertDayNameToNumber(item.toString().trim());
                            if (dayNum != -1) {
                                workingDays.add(dayNum);
                                Log.d("Firebase", "Converted and added day: " + dayNum);
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
            default: return -1; // ערך לא תקין
        }
    }


    private void loadBarberWorkingHours() {
        if (currentUser == null) return;

        String safeEmail = currentUser.getEmail().replace(".", "_");
        barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeEmail);

        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("Firebase", " No barber found in database.");
                    return;
                }

                // שליפת שעות העבודה
                String startHour = snapshot.child("startHour").getValue(String.class);
                String endHour = snapshot.child("endHour").getValue(String.class);

                if (startHour != null && endHour != null) {
                    List<String> timeSlots = generateTimeSlots(startHour, endHour);
                    availableAppointments.clear();
                    availableAppointments.addAll(timeSlots);
                    adapter.notifyDataSetChanged();

                    Log.d("Firebase", "Loaded available appointments: " + availableAppointments);
                } else {
                    Log.d("Firebase", "startHour or endHour is missing.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load barber details: " + error.getMessage());
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

        String safeEmail = currentUser.getEmail().replace(".", "_");
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
                                } else {
                                    btnDeleteAll.setVisibility(View.VISIBLE);
                                }
                            }
                        });
        /*barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("Firebase", "⚠️ No barber found in database.");
                    return;
                }

                // שליפת שעות העבודה
                String startHour = snapshot.child("startHour").getValue(String.class);
                String endHour = snapshot.child("endHour").getValue(String.class);

                if (startHour != null && endHour != null) {
                    List<String> timeSlots = generateTimeSlots(startHour, endHour);
                    availableAppointments.clear();
                    availableAppointments.addAll(timeSlots);
                    adapter.notifyDataSetChanged();

                    Log.d("Firebase", "✅ Loaded available appointments: " + availableAppointments);
                } else {
                    Log.d("Firebase", "⚠️ startHour or endHour is missing.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "❌ Failed to load barber details: " + error.getMessage());
            }
        });*/
    }


    private List<String> generateTimeSlots(String startHour, String endHour) {
        List<String> timeSlots = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date startTime = sdf.parse(startHour);
            Date endTime = sdf.parse(endHour);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);

            while (calendar.getTime().before(endTime)) {
                timeSlots.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.HOUR, 1);  // מחלק את שעות העבודה לתורים של שעה
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeSlots;
    }

    private int getDayOfWeek(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // התאמת ימי השבוע (באנדרואיד: ראשון = 1, שבת = 7)
        return dayOfWeek - 1; // כך שהשבוע יתחיל מ-0 = ראשון
    }
    private void deleteAllAppointmentsForDay(String date) {
        if (currentUser == null) return;

        String barberEmail = currentUser.getEmail().replace(".", "_");
        DatabaseReference barberAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointments")
                .child(barberEmail)
                .child(date);

        DatabaseReference customerAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointmentsByClient");

        new AlertDialog.Builder(requireContext())
                .setTitle("מחיקת כל התורים")
                .setMessage("האם אתה בטוח שברצונך למחוק את כל התורים ליום זה?")
                .setPositiveButton("כן", (dialog, which) -> {

                    // שלב 1: מחיקת כל התורים מהברבר (appointments)
                    barberAppointmentsRef.get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot appointment : dataSnapshot.getChildren()) {
                                String time = appointment.getKey();
                                String customerEmail = appointment.child("customerEmail").getValue(String.class);

                                if (customerEmail != null) {
                                    String safeCustomerEmail = customerEmail.replace(".", "_");

                                    // שלב 2: מחיקת כל התורים מהלקוחות (appointmentsByClient)
                                    DatabaseReference customerAppointment = customerAppointmentsRef
                                            .child(safeCustomerEmail)
                                            .child(date)
                                            .child(time);
                                    customerAppointment.removeValue();
                                }
                            }

                            // מחיקת כל הרשומה של התאריך מהברבר
                            barberAppointmentsRef.removeValue().addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "כל התורים נמחקו בהצלחה!", Toast.LENGTH_SHORT).show();

                                // עדכון ה-RecyclerView
                                customerAppointmentsAdapter.clear();
                                recyclerViewAvailableAppointments.setVisibility(View.GONE);

                                // הסתרת כפתור DELETE ALL אחרי המחיקה
                            //    Button btnDeleteAll = getView().findViewById(R.id.btn_Delete);
                                btnDeleteAll.setVisibility(View.GONE);

                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "שגיאה במחיקת התורים!", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "אין תורים ליום זה!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("לא", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onCancelClick(CustomerAppointment appointment, int position) {
        String barberEmail = currentUser.getEmail().replace(".", "_");
        String customerEmail = appointment.getCustomerEmail().replace(".", "_");
        String appointmentDate = this.selectedDate;
        String appointmentTime = appointment.getTime();

        Log.d("Cancel", "Canceling appointment: " + appointmentDate + " at " + appointmentTime +
                " barberEmail: " + barberEmail + " customerEmail: " + customerEmail);

        // יצירת הודעת אישור לפני מחיקה
        new AlertDialog.Builder(requireContext())
                .setTitle("ביטול תור")
                .setMessage("האם אתה בטוח שברצונך לבטל את התור בשעה " + appointmentTime + "?")
                .setPositiveButton("כן", (dialog, which) -> {
                    DatabaseReference barberAppointmentRef = FirebaseDatabase.getInstance()
                            .getReference("appointments")
                            .child(barberEmail)
                            .child(appointmentDate)
                            .child(appointmentTime);

                    DatabaseReference customerAppointmentRef = FirebaseDatabase.getInstance()
                            .getReference("appointmentsByClient")
                            .child(customerEmail)
                            .child(appointmentDate)
                            .child(appointmentTime);

                    // מחיקת התור מהספר ומהלקוח
                    barberAppointmentRef.removeValue().addOnSuccessListener(aVoid -> {
                        customerAppointmentRef.removeValue().addOnSuccessListener(aVoid2 -> {
                            Toast.makeText(getContext(), "התור בוטל בהצלחה", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "שגיאה במחיקת התור מהלקוח: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "שגיאה בביטול התור: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

                })
                .setNegativeButton("לא", (dialog, which) -> dialog.dismiss())
                .show();
    }

}

