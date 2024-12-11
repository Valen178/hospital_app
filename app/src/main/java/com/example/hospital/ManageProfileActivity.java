package com.example.hospital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageProfileActivity extends AppCompatActivity {
    EditText etPerfilNombre, etPerfilContraseña, etPerfilEmail;
    Button btnActualizarPerfil, btnBorrarPerfil;
    DatabaseHelper databaseHelper;

    SharedPreferences sharedPreferences;
    String loggedInEmail;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_profile);

        etPerfilNombre = findViewById(R.id.etPerfilNombre);
        etPerfilContraseña = findViewById(R.id.etPerfilContraseña);
        etPerfilEmail = findViewById(R.id.etPerfilEmail);
        btnActualizarPerfil = findViewById(R.id.btnActualizarPerfil);
        btnBorrarPerfil = findViewById(R.id.btnBorrarPerfil);

        databaseHelper = new DatabaseHelper(this);

        sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
        loggedInEmail = sharedPreferences.getString("loggedInEmail", null);

        if (loggedInEmail == null) {
            Toast.makeText(this, "Sesion expirada. Por favor ingrese devuelta.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        executorService.execute(this::loadUserProfile);

        btnActualizarPerfil.setOnClickListener(v -> {
            String newName = etPerfilNombre.getText().toString().trim();
            String newPassword = etPerfilContraseña.getText().toString().trim();

            if (newName.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                executorService.execute(() -> {
                    boolean isUpdated = databaseHelper.updateUser(loggedInEmail, newName, newPassword);
                    runOnUiThread(() -> {
                        if (isUpdated) {
                            Toast.makeText(this, "Perfil actualizado con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Fallo al actualizar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

        btnBorrarPerfil.setOnClickListener(v -> {
            executorService.execute(() -> {
                boolean isDeleted = databaseHelper.deleteUser(loggedInEmail);
                runOnUiThread(() -> {
                    if (isDeleted) {
                        Toast.makeText(this, "Perfil borrado con exito", Toast.LENGTH_SHORT).show();
                        clearSession();
                        redirectToLogin();
                    } else {
                        Toast.makeText(this, "Fallo al borrar el perfil", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
    private void loadUserProfile() {
        Cursor cursor = databaseHelper.getUserByEmail(loggedInEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("Contraseña"));

            runOnUiThread(() -> {
                etPerfilEmail.setText(email);
                etPerfilNombre.setText(name);
                etPerfilContraseña.setText(password);
            });
            cursor.close();
        } else {
            runOnUiThread(() -> Toast.makeText(this, "Fallo al cargar el perfil", Toast.LENGTH_SHORT).show());
        }
    }

    private void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ManageProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}