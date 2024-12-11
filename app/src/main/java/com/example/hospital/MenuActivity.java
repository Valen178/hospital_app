package com.example.hospital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    Button btnPerfil, btnSalir, btnPacientes, btnPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        btnPerfil = findViewById(R.id.btnPerfil);
        btnSalir = findViewById(R.id.btnLogout);
        btnPacientes = findViewById(R.id.btnPacientes);
        btnPersonal = findViewById(R.id.btnPersonal);

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ManageProfileActivity.class);
            startActivity(intent);
        });

        btnSalir.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnPacientes.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ManagePatientsActivity.class);
            startActivity(intent);
        });

        btnPersonal.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ManagePersonalActivity.class);
            startActivity(intent);
        });
    }
}