package com.example.stylescheduler.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stylescheduler.Classes.Appointment;
import com.example.stylescheduler.Classes.ClientAppointmentAdapter;
import com.example.stylescheduler.Classes.Barber;
import com.example.stylescheduler.Classes.Customer;
import com.example.stylescheduler.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClientAppointments extends Fragment {

    private static final String TAG = "ClientAppointments"; // ✅ שם קבוע ללוגים

    private RecyclerView recyclerViewAppointments;
    private ClientAppointmentAdapter clientAppointmentsAdapter;
    private List<Appointment> appointmentsList;
    private TextView tvNoAppointments;
    private DatabaseReference appointmentsRef;
    private FirebaseUser currentUser;

    public ClientAppointments() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_appointments, container, false);

        recyclerViewAppointments = view.findViewById(R.id.recyclerViewAppointmentsOfClient);
        tvNoAppointments = view.findViewById(R.id.tvNoAppointments);

        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentsList = new ArrayList<>();
        clientAppointmentsAdapter = new ClientAppointmentAdapter(getContext(), appointmentsList);
        recyclerViewAppointments.setAdapter(clientAppointmentsAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "🔹 משתמש מחובר: " + currentUser.getEmail());
            loadClientAppointments();
        } else {
            Log.e(TAG, "❌ משתמש לא מחובר");
            Toast.makeText(getContext(), "משתמש לא מחובר", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadClientAppointments() {
        String clientEmail = currentUser.getEmail().replace(".", "_");
        Log.d(TAG, "📥 טעינת תורים ללקוח: " + clientEmail);

        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointmentsByClient").child(clientEmail);
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentsList.clear();
                if (!snapshot.exists()) {
                    Log.d(TAG, "⚠️ אין תורים קיימים עבור הלקוח.");
                    tvNoAppointments.setVisibility(View.VISIBLE);
                    recyclerViewAppointments.setVisibility(View.GONE);
                    return;
                }
                tvNoAppointments.setVisibility(View.GONE);
                recyclerViewAppointments.setVisibility(View.VISIBLE);

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) { // ✅ מעבר על כל תאריך
                    String dateKey = dateSnapshot.getKey();
                    Log.d(TAG, "📆 תאריך נמצא: " + dateKey);

                    for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) { // ✅ מעבר על כל תור
                        Map<String, Object> appointmentData = (Map<String, Object>) timeSnapshot.getValue();
                        if (appointmentData == null) {
                            Log.e(TAG, "❌ נתוני תור חסרים בתאריך: " + dateKey);
                            continue;
                        }

                        // שליפת הנתונים
                        String appointmentTime = (String) appointmentData.get("appointmentTime");
                        String barberEmail = (String) appointmentData.get("barberEmail");
                        String customerEmail = (String) appointmentData.get("customerEmail");
                        String serviceType = (String) appointmentData.get("serviceType");

                        if (appointmentTime == null || barberEmail == null || customerEmail == null) {
                            Log.e(TAG, "❌ נתונים חסרים: appointmentTime=" + appointmentTime +
                                    ", barberEmail=" + barberEmail + ", customerEmail=" + customerEmail);
                            continue;
                        }

                        // המרת האימיילים לפורמט בטוח
                        String safeBarberEmail = barberEmail.replace(".", "_");
                        String safeCustomerEmail = customerEmail.replace(".", "_");

                        // המרת תאריך + שעה לאובייקט Date
                        Date appointmentDate;
                        try {
                            appointmentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).parse(dateKey + " " + appointmentTime);
                            Log.d(TAG, "⏳ המרת תאריך הצליחה: " + appointmentDate);
                        } catch (Exception e) {
                            Log.e(TAG, "❌ שגיאה בהמרת תאריך", e);
                            continue;
                        }

                        // שליפת פרטי הספר
                        DatabaseReference barberRef = FirebaseDatabase.getInstance().getReference("barbers").child(safeBarberEmail);
                        barberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot barberSnapshot) {
                                if (!barberSnapshot.exists()) {
                                    Log.e(TAG, "❌ הספר לא נמצא בנתונים: " + safeBarberEmail);
                                    return;
                                }
                                Barber barber = barberSnapshot.getValue(Barber.class);
                                if (barber == null) {
                                    Log.e(TAG, "❌ הספר נמצא אך הנתונים לא הומרו כראוי.");
                                    return;
                                }
                                Log.d(TAG, "💇 פרטי הספר נטענו בהצלחה: " + barber.getName());

                                // שליפת פרטי הלקוח
                                DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("customers").child(safeCustomerEmail);
                                customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot customerSnapshot) {
                                        if (!customerSnapshot.exists()) {
                                            Log.e(TAG, "❌ הלקוח לא נמצא: " + safeCustomerEmail);
                                            return;
                                        }
                                        Customer customer = customerSnapshot.getValue(Customer.class);
                                        if (customer == null) {
                                            Log.e(TAG, "❌ הנתונים של הלקוח שגויים.");
                                            return;
                                        }
                                        Log.d(TAG, "👤 פרטי הלקוח נטענו בהצלחה: " + customer.getName());

                                        // יצירת אובייקט Appointment
                                        Appointment appointment = new Appointment(
                                                timeSnapshot.getKey().hashCode(),
                                                customer,
                                                barber,
                                                serviceType,
                                                appointmentDate
                                        );

                                        appointmentsList.add(appointment);
                                        clientAppointmentsAdapter.notifyDataSetChanged();
                                        Log.d(TAG, "✅ תור נוסף לרשימה בהצלחה!");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "❌ שגיאה בשליפת פרטי הלקוח", error.toException());
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "❌ שגיאה בשליפת פרטי הספר", error.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "❌ שגיאה בשליפת נתוני התורים", error.toException());
            }
        });
    }
}
