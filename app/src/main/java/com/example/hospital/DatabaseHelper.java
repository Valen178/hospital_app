package com.example.hospital;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HospitalDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLA_USUARIOS = "Usuarios";
    private static final String COLUMNA_ID = "ID";
    private static final String COLUMNA_NOMBRE = "Nombre";
    private static final String COLUMNA_EMAIL = "Email";
    private static final String COLUMNA_CONTRASEÑA = "Contraseña";

    private static final String TABLA_PACIENTES = "Pacientes";
    private static final String COLUMNA_ID_PACIENTE = "ID";
    private static final String COLUMNA_NOMBRE_PACIENTE = "Nombre";
    private static final String COLUMNA_EDAD_PACIENTE = "Edad";
    private static final String COLUMNA_EMAIL_PACIENTE = "Email";
    private static final String COLUMNA_PROBLEMA_PACIENTE = "Problema";

    private static final String TABLA_PERSONAL = "Personal";
    private static final String COLUMNA_ID_PERSONAL = "ID";
    private static final String COLUMNA_NOMBRE_PERSONAL = "Nombre";
    private static final String COLUMNA_EMAIL_PERSONAL = "Email";
    private static final String COLUMNA_CARGO_PERSONAL = "Cargo";

    private static final String CREATE_TABLA_USUARIOS = "CREATE TABLE " + TABLA_USUARIOS + " (" + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMNA_NOMBRE + " TEXT, " + COLUMNA_EMAIL + " TEXT UNIQUE, " + COLUMNA_CONTRASEÑA + " TEXT)";
    private static final String CREATE_TABLA_PACIENTES = "CREATE TABLE " + TABLA_PACIENTES + " (" + COLUMNA_ID_PACIENTE + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMNA_NOMBRE_PACIENTE + " TEXT, " + COLUMNA_EDAD_PACIENTE + " INTEGER, " + COLUMNA_EMAIL_PACIENTE + " TEXT UNIQUE, " + COLUMNA_PROBLEMA_PACIENTE + " TEXT)";
    private static final String CREATE_TABLA_PERSONAL = "CREATE TABLE " + TABLA_PERSONAL + " (" + COLUMNA_ID_PERSONAL + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMNA_NOMBRE_PERSONAL + " TEXT, " +  COLUMNA_EMAIL_PERSONAL + " TEXT UNIQUE, " + COLUMNA_CARGO_PERSONAL + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLA_USUARIOS);
        db.execSQL(CREATE_TABLA_PACIENTES);
        db.execSQL(CREATE_TABLA_PERSONAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PACIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PERSONAL);
        onCreate(db);
    }

    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE, name);
        values.put(COLUMNA_EMAIL, email);
        values.put(COLUMNA_CONTRASEÑA, password);

        long result = db.insert(TABLA_USUARIOS, null, values);
        db.close();

        return result != -1; // Devuelve true si el insert funciono
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLA_USUARIOS, new String[]{COLUMNA_ID},
                COLUMNA_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLA_USUARIOS, new String[]{COLUMNA_ID},
                COLUMNA_EMAIL + "=? AND " + COLUMNA_CONTRASEÑA + "=?",
                new String[]{email, password},
                null, null, null);
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    // **Read uno**
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLA_USUARIOS, null, COLUMNA_EMAIL + "=?",
                new String[]{email}, null, null, null);
    }

    // **Read todos**
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLA_USUARIOS, null, null, null, null, null, COLUMNA_NOMBRE + " ASC");
    }

    // **Update**
    public boolean updateUser(String email, String newName, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE, newName);
        values.put(COLUMNA_CONTRASEÑA, newPassword);

        int rowsAffected = db.update(TABLA_USUARIOS, values, COLUMNA_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    // **Delete**
    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLA_USUARIOS, COLUMNA_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean addPatient(String name, int age, String email, String diagnosis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_PACIENTE, name);
        values.put(COLUMNA_EDAD_PACIENTE, age);
        values.put(COLUMNA_EMAIL_PACIENTE, email);
        values.put(COLUMNA_PROBLEMA_PACIENTE, diagnosis);
        long result = db.insert(TABLA_PACIENTES, null, values);
        return result != -1;
    }

    public ArrayList<Paciente> getAllPatients() {
        ArrayList<Paciente> listaPaciente = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pacientes", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int age = cursor.getInt(2);
                String email = cursor.getString(3);
                String diagnosis = cursor.getString(4);
                listaPaciente.add(new Paciente(id, name, age, email, diagnosis));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaPaciente;
    }

    public boolean updatePatient(int id, String name, int age, String email, String diagnosis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_PACIENTE, name);
        values.put(COLUMNA_EDAD_PACIENTE, age);
        values.put(COLUMNA_EMAIL_PACIENTE, email);
        values.put(COLUMNA_PROBLEMA_PACIENTE, diagnosis);
        int result = db.update(TABLA_PACIENTES, values, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deletePatient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLA_PACIENTES, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public Paciente getPatientById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Paciente paciente = null;

        Cursor cursor = db.rawQuery("SELECT * FROM pacientes WHERE ID = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_NOMBRE_PACIENTE));
            int age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_EDAD_PACIENTE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_EMAIL_PACIENTE));
            String diagnosis = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_PROBLEMA_PACIENTE));

            paciente = new Paciente(id, name, age, email, diagnosis);
            cursor.close();
        }
        return paciente;
    }

    public boolean addPersonal(String name, String email, String cargo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_PERSONAL, name);
        values.put(COLUMNA_EMAIL_PERSONAL, email);
        values.put(COLUMNA_CARGO_PERSONAL, cargo);
        long result = db.insert(TABLA_PERSONAL, null, values);
        return result != -1;
    }

    public ArrayList<Personal> getAllPersonal() {
        ArrayList<Personal> listaPersonal = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_PERSONAL, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String email = cursor.getString(2);
                String cargo = cursor.getString(3);
                listaPersonal.add(new Personal(id, name, email, cargo));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaPersonal;
    }

    public boolean updatePersonal(int id, String name, String email, String cargo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE_PERSONAL, name);
        values.put(COLUMNA_EMAIL_PERSONAL, email);
        values.put(COLUMNA_CARGO_PERSONAL, cargo);
        int result = db.update(TABLA_PERSONAL, values, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deletePersonal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLA_PERSONAL, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public Personal getPersonalById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Personal personal = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_PERSONAL + " WHERE ID = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_NOMBRE_PERSONAL));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_EMAIL_PERSONAL));
            String cargo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_CARGO_PERSONAL));

            personal = new Personal(id, name, email, cargo);
            cursor.close();
        }
        return personal;
    }
}
