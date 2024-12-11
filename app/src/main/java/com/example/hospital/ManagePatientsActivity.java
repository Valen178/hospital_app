package com.example.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManagePatientsActivity extends AppCompatActivity {
    Button btnAgregarPaciente;
    RecyclerView rvPacientes;
    PacienteAdapter pacienteAdapter;
    DatabaseHelper databaseHelper;

    ArrayList<Paciente> listaPacientes;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_patients);

        btnAgregarPaciente = findViewById(R.id.btnAgregarPaciente);
        rvPacientes = findViewById(R.id.rvPacientes);
        databaseHelper = new DatabaseHelper(this);

        listaPacientes = databaseHelper.getAllPatients();

        rvPacientes.setLayoutManager(new LinearLayoutManager(this));
        pacienteAdapter = new PacienteAdapter(listaPacientes, databaseHelper, this);
        rvPacientes.setAdapter(pacienteAdapter);

        loadPatients();

        btnAgregarPaciente.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePatientsActivity.this, AddEditPatientActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPatients();
    }

    private void loadPatients() {
        executorService.execute(() -> {
            ArrayList<Paciente> patients = databaseHelper.getAllPatients();

            runOnUiThread(() -> {
                listaPacientes.clear();
                listaPacientes.addAll(patients);
                pacienteAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}