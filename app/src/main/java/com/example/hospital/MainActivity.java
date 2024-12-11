package com.example.hospital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    EditText etEmail, etContraseña;
    Button btnLogin, btnRegistro;
    DatabaseHelper databaseHelper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.email);
        etContraseña = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnInicio);
        btnRegistro = findViewById(R.id.btnRegistro);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String contraseña = etContraseña.getText().toString().trim();

            if (email.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                boolean isValidUser = databaseHelper.validateUser(email, contraseña);

                runOnUiThread(() -> {
                    if (isValidUser) {
                        Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("loggedInEmail", email);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Credenciales invalidas", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // Boton registro evento
        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}