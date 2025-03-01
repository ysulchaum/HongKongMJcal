package edu.cuhk.csci3310;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record);


        // main page
        ImageButton recordButton = findViewById(R.id.mainPage);
        recordButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}