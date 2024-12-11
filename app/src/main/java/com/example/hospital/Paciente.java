package com.example.hospital;

public class Paciente {
    private int id;
    private String name;
    private int age;
    private String email;
    private String diagnosis;

    public Paciente(int id, String name, int age, String email, String diagnosis) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.diagnosis = diagnosis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
