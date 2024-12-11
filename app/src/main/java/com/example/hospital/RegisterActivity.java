package com.example.hospital;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    EditText etNombre, etEmail, etContrseña;
    Button btnRegistro, btnVolver;
    DatabaseHelper databaseHelper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.editTextText);
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etContrseña = findViewById(R.id.editTextTextPassword);
        btnRegistro = findViewById(R.id.button3);
        btnVolver = findViewById(R.id.button4);

        databaseHelper = new DatabaseHelper(this);

        btnRegistro.setOnClickListener(v -> {
            String name = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etContrseña.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                boolean emailExists = databaseHelper.checkEmail(email);

                if (emailExists) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Este email ya esta registrado", Toast.LENGTH_SHORT).show());
                } else {
                    boolean isInserted = databaseHelper.insertUser(name, email, password);
                    runOnUiThread(() -> {
                        if (isInserted) {
                            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });

        btnVolver.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}