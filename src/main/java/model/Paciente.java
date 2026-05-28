package model;

import java.time.LocalDate;
import java.time.Period;

public class Paciente {

    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private double peso;
    private LocalDate fechaNacimiento;
    private String microchip;
    private Propietario propietario;

    public Paciente() {
    }

    public Paciente(String nombre, String especie, String raza, String sexo, double peso,
            LocalDate fechaNacimiento, String microchip, Propietario propietario) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.peso = peso;
        this.fechaNacimiento = fechaNacimiento;
        this.microchip = microchip;
        this.propietario = propietario;
    }

    public int calcularEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return nombre + " (" + especie + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getMicrochip() {
        return microchip;
    }

    public void setMicrochip(String microchip) {
        this.microchip = microchip;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }
}
