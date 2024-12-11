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

public class ManagePersonalActivity extends AppCompatActivity {
    Button btnAgregarPersonal;
    RecyclerView rvPersonal;
    PersonalAdapter personalAdapter;
    DatabaseHelper databaseHelper;

    ArrayList<Personal> listaPersonal;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_personal);

        btnAgregarPersonal = findViewById(R.id.btnAgregarPersonal);
        rvPersonal = findViewById(R.id.rvPersonal);
        databaseHelper = new DatabaseHelper(this);

        listaPersonal = databaseHelper.getAllPersonal();

        rvPersonal.setLayoutManager(new LinearLayoutManager(this));
        personalAdapter = new PersonalAdapter(listaPersonal, databaseHelper, this);
        rvPersonal.setAdapter(personalAdapter);

        loadPersonal();

        btnAgregarPersonal.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePersonalActivity.this, AddEditPersonalActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPersonal();
    }

    private void loadPersonal() {
        executorService.execute(() -> {
            ArrayList<Personal> personals = databaseHelper.getAllPersonal();

            runOnUiThread(() -> {
                listaPersonal.clear();
                listaPersonal.addAll(personals);
                personalAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}