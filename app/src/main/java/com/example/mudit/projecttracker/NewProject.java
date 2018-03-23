package com.example.mudit.projecttracker;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class NewProject extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        Button dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentdate = Calendar.getInstance();
                final int day = currentdate.get(Calendar.DAY_OF_MONTH);
                final int month = currentdate.get(Calendar.MONTH);
                final int year = currentdate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewProject.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedyear, int selectedmonth, int selectedday) {
                        TextView dateview = (TextView) findViewById(R.id.dateview);
                        dateview.setText(selectedday+"/"+selectedmonth+"/"+selectedyear);
                    }
                },year,month,day);
            }
        });
        uploadproject();
    }

    private void uploadproject() {
        Button submit = (Button)findViewById(R.id.sbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
