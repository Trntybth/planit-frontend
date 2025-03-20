package com.example.planit_frontend.view;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planit_frontend.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import android.widget.Button;
import android.widget.TextView;


public class CreateEventActivity extends AppCompatActivity {


    private TextView eventDateTextView;
    private Button selectDateButton;
    private LocalDate eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the layout for creating an event
        setContentView(R.layout.activity_createevent);
    }


    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Update the LocalDate object with the selected date
            eventDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay); // Month is 0-indexed
            updateDateTextView();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void updateDateTextView() {
        // Display the selected date in the TextView
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        eventDateTextView.setText(eventDate.format(formatter));
    }
}