package com.malarska.englishfromtextfile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

public class LogoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Utwórz obiekt Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Przejdź do MainActivity po 2 sekundach
                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Zamyka LogoActivity
            }
        }, 2000); // 2000 milisekundy = 2 sekundy
    }
}