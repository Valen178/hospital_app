package com.example.hospital;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditPatientActivity extends AppCompatActivity {
    EditText etNombrePaciente, etnEdad, eteEmail, etProblema;
    Button btnGuardar;
    DatabaseHelper databaseHelper;

    int patientId = -1;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_patient);

        etNombrePaciente = findViewById(R.id.etNombrePaciente);
        etnEdad = findViewById(R.id.etnEdad);
        eteEmail = findViewById(R.id.eteEmail);
        etProblema = findViewById(R.id.etProblema);
        btnGuardar = findViewById(R.id.btnGuardar);

        databaseHelper = new DatabaseHelper(this);

        patientId = getIntent().getIntExtra("PACIENTE_ID", -1);
        if (patientId != -1) {
            loadPatientDetails(patientId);
        }

        btnGuardar.setOnClickListener(v -> {
            String name = etNombrePaciente.getText().toString().trim();
            String ageStr = etnEdad.getText().toString().trim();
            String email = eteEmail.getText().toString().trim();
            String diagnosis = etProblema.getText().toString().trim();

            if (isValidInput(name, ageStr, email, diagnosis)) {
                int age = Integer.parseInt(ageStr);
                savePatient(name, age, email, diagnosis);
            }
        });
    }

    private void loadPatientDetails(int patientId) {
        executorService.execute(() -> {
            Paciente paciente = databaseHelper.getPatientById(patientId);
            if (paciente != null) {
                runOnUiThread(() -> {
                    etNombrePaciente.setText(paciente.getName());
                    etnEdad.setText(String.valueOf(paciente.getAge()));
                    eteEmail.setText(paciente.getEmail());
                    etProblema.setText(paciente.getDiagnosis());
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar detalles del paciente", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void savePatient(String name, int age, String email, String diagnosis) {
        executorService.execute(() -> {
            boolean result;
            if (patientId == -1) {
                result = databaseHelper.addPatient(name, age, email, diagnosis);
                showToastOnUiThread(result ? "Paciente agregado con éxito" : "Fallo al agregar al paciente");
            } else {
                result = databaseHelper.updatePatient(patientId, name, age, email, diagnosis);
                showToastOnUiThread(result ? "Paciente actualizado con éxito" : "Fallo al actualizar el paciente");
            }

            if (result) {
                runOnUiThread(this::finish);
            }
        });
    }

    private boolean isValidInput(String name, String ageStr, String email, String diagnosis) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(email) || TextUtils.isEmpty(diagnosis)) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0) {
                Toast.makeText(this, "La edad debe ser un número positivo", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una edad válida", Toast.LENGTH_SHORT).show();
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