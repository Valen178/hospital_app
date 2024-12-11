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

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.PersonalViewHolder> {

    private ArrayList<Personal> personalList;
    private DatabaseHelper databaseHelper;
    private Context context;

    public PersonalAdapter(ArrayList<Personal> personalList, DatabaseHelper databaseHelper, Context context) {
        this.personalList = personalList;
        this.databaseHelper = databaseHelper;
        this.context = context;
    }

    @NonNull
    @Override
    public PersonalAdapter.PersonalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal, parent, false);
        return new PersonalAdapter.PersonalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalAdapter.PersonalViewHolder holder, int position) {
        Personal personal = personalList.get(position);
        holder.tvName.setText(personal.getName());
        holder.tvEmail.setText("Email: " + personal.getEmail());
        holder.tvCargo.setText("Cargo: " + personal.getCargo());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditPersonalActivity.class);
            intent.putExtra("PERSONAL_ID", personal.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            boolean isDeleted = databaseHelper.deletePersonal(personal.getId());
            if (isDeleted) {
                Toast.makeText(context, "Personal borrado con exito", Toast.LENGTH_SHORT).show();
                personalList.remove(position);
                notifyItemRemoved(position);
            } else {
                Toast.makeText(context, "Fallo al borrar el personal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return personalList.size();
    }

    public static class PersonalViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvCargo;
        ImageButton btnEdit, btnDelete;

        public PersonalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPersonalName);
            tvEmail = itemView.findViewById(R.id.tvPersonalEmail);
            tvCargo = itemView.findViewById(R.id.tvPersonalCargo);
            btnEdit = itemView.findViewById(R.id.btnEditar);
            btnDelete = itemView.findViewById(R.id.btnBorrar);
        }
    }
}
