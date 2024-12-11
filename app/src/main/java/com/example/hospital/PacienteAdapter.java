package com.example.hospital;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.PatientViewHolder> {

    private ArrayList<Paciente> patientList;
    private DatabaseHelper databaseHelper;
    private Context context;

    public PacienteAdapter(ArrayList<Paciente> patientList, DatabaseHelper databaseHelper, Context context) {
        this.patientList = patientList;
        this.databaseHelper = databaseHelper;
        this.context = context;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Paciente paciente = patientList.get(position);
        holder.tvName.setText(paciente.getName());
        holder.tvAge.setText("Edad: " + paciente.getAge());
        holder.tvEmail.setText("Email: " + paciente.getEmail());
        holder.tvDiagnosis.setText("Problema: " + paciente.getDiagnosis());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditPatientActivity.class);
            intent.putExtra("PACIENTE_ID", paciente.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            boolean isDeleted = databaseHelper.deletePatient(paciente.getId());
            if (isDeleted) {
                Toast.makeText(context, "Paciente borrado con exito", Toast.LENGTH_SHORT).show();
                patientList.remove(position);
                notifyItemRemoved(position);
            } else {
                Toast.makeText(context, "Fallo al borrar el paciente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAge, tvEmail, tvDiagnosis;
        ImageButton btnEdit, btnDelete;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPatientName);
            tvAge = itemView.findViewById(R.id.tvPatientAge);
            tvEmail = itemView.findViewById(R.id.tvPatientEmail);
            tvDiagnosis = itemView.findViewById(R.id.tvPatientDiagnosis);
            btnEdit = itemView.findViewById(R.id.btnEditar);
            btnDelete = itemView.findViewById(R.id.btnBorrar);
        }
    }
}
