package com.example.mobileapplicationv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;


    Button logoutButton, submitButton;

    FirebaseUser user;

    String[] items = {"Youtube", "Twitch", "Facebook", "Jetpack"};

    String chosenItem, packageName;

    int totalDuration, bedTime;

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapterItems;

    NumberPicker hoursPicker,minutesPicker,secondsPicker, bedTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://control-app-auth-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        logoutButton = findViewById(R.id.logoutButton);
        submitButton = findViewById(R.id.submitButton);
        user = auth.getCurrentUser();

        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
        hoursPicker = findViewById(R.id.hoursPicker);
        minutesPicker = findViewById(R.id.minutesPicker);
        secondsPicker = findViewById(R.id.secondsPicker);
        bedTimePicker = findViewById(R.id.bedTimePicker);

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(24);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);
        bedTimePicker.setMinValue(0);
        bedTimePicker.setMaxValue(23);


        autoCompleteTextView.setAdapter(adapterItems);


        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                chosenItem = item;
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalDuration = hoursPicker.getValue()*3600 + minutesPicker.getValue()*60 + secondsPicker.getValue();
                bedTime = bedTimePicker.getValue();
                switch (chosenItem) {
                    case "Youtube": packageName = "com.google.android.youtube"; break;
                    case "Twitch": packageName = "tv.twitch.android.app"; break;
                    case "Facebook": packageName = "com.facebook.katana"; break;
                    case "Jetpack": packageName = "com.halfbrick.jetpackjoyride"; break;
                }
                Map<String, Long> data = new HashMap<>();
                data.put(chosenItem, (long) totalDuration);
                data.put("bedtime", (long) bedTime);
                Log.d("MainActivity", data.toString());
                myRef.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("MainActivity", "Data saved successfully");
                        } else {
                            Log.e("MainActivity", "Error saving data", task.getException());
                        }
                    }
                });

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}