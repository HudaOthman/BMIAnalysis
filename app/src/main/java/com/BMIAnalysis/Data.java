package com.BMIAnalysis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Data extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button maxLength, minLength, minWei, maxWei, save;
    EditText length, weight;
    RadioGroup group;
    Intent intent;
    String uId;
    TextView birthday;
    RadioButton gender;
    FirebaseFirestore firebaseFirestore;
    int countW = 50, countL = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        maxLength = findViewById(R.id.maxLength);
        minLength = findViewById(R.id.minLength);
        minWei = findViewById(R.id.minWei);
        maxWei = findViewById(R.id.maxWei);
        save = findViewById(R.id.save);
        birthday = findViewById(R.id.birthday);
        length = findViewById(R.id.length);
        weight = findViewById(R.id.weight);
        group = findViewById(R.id.radioGroup);
        firebaseFirestore = FirebaseFirestore.getInstance();
        intent = getIntent();
        uId = intent.getStringExtra("userId");
        gender =(RadioButton)group.findViewById(R.id.rb_male);


        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new Date();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                gender = (RadioButton) radioGroup.findViewById(i);
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(c.getTime());
        birthday.setText(currentDate);
    }

    public void maxLength(View view) {
        countL++;
        length.setText(countL + "");
    }

    public void minLength(View view) {
        if (!length.getText().toString().equals("120")) {
            countL--;
            length.setText(countL + "");
        }
    }

    public void minWei(View view) {
        if (weight.getText().toString().equals("50")) {
            Toast.makeText(getApplicationContext(), "ادخل قيمة اكبر من 50", Toast.LENGTH_SHORT).show();
        } else {
            countW--;
            weight.setText(countW + "");
        }
    }

    public void maxWei(View view) {
        countW++;
        weight.setText(countW + "");
    }

    public void save(View view) {

        if (birthday.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "ادخل تاريخ الميلاد", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("gender", gender.getText().toString());
        stringObjectHashMap.put("lenght", length.getText().toString());
        stringObjectHashMap.put("weight", weight.getText().toString());
        stringObjectHashMap.put("birthday", birthday.getText().toString());

        firebaseFirestore.collection("User").document(uId).update(stringObjectHashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(getApplicationContext(), Activity_Home.class);
                        startActivity(intent);
                    }
                });
    }
}