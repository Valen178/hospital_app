package com.example.hospital;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditPersonalActivity extends AppCompatActivity {
    EditText etNombrePersonal, eteEmail, etCargo;
    Button btnGuardar;
    DatabaseHelper databaseHelper;

    int personalId = -1;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_personal);

        etNombrePersonal = findViewById(R.id.etNombrePersonal);
        eteEmail = findViewById(R.id.eteEmail);
        etCargo = findViewById(R.id.etCargo);
        btnGuardar = findViewById(R.id.btnGuardar);

        databaseHelper = new DatabaseHelper(this);

        personalId = getIntent().getIntExtra("PERSONAL_ID", -1);
        if (personalId != -1) {
            loadPersonalDetails(personalId);
        }

        btnGuardar.setOnClickListener(v -> {
            String name = etNombrePersonal.getText().toString().trim();
            String email = eteEmail.getText().toString().trim();
            String cargo = etCargo.getText().toString().trim();

            if (isValidInput(name, email, cargo)) {
                savePersonal(name, email, cargo);
            }
        });
    }

    private void loadPersonalDetails(int personalId) {
        executorService.execute(() -> {
            Personal personal = databaseHelper.getPersonalById(personalId);
            if (personal != null) {
                runOnUiThread(() -> {
                    etNombrePersonal.setText(personal.getName());
                    eteEmail.setText(personal.getEmail());
                    etCargo.setText(personal.getCargo());
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar detalles del personal", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void savePersonal(String name, String email, String cargo) {
        executorService.execute(() -> {
            boolean result;
            if (personalId == -1) {
                result = databaseHelper.addPersonal(name, email, cargo);
                showToastOnUiThread(result ? "Personal agregado con éxito" : "Fallo al agregar al personal");
            } else {
                result = databaseHelper.updatePersonal(personalId, name, email, cargo);
                showToastOnUiThread(result ? "Personal actualizado con éxito" : "Fallo al actualizar el personal");
            }

            if (result) {
                runOnUiThread(this::finish);
            }
        });
    }

    private boolean isValidInput(String name, String email, String cargo) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(cargo)) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showToastOnUiThread(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}