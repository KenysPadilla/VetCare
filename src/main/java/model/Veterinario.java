package model;

public class Veterinario extends Persona {

    private String especialidad;
    private String numeroLicencia;
    private boolean activo = true;

    public Veterinario() {
        super();
    }

    public Veterinario(String cedula, String nombre, String apellido,
            String telefono, String email, String especialidad, String numeroLicencia) {
        super(cedula, nombre, apellido, telefono, email);
        this.especialidad = especialidad;
        this.numeroLicencia = numeroLicencia;
        this.activo = true;
    }

    public Veterinario(String cedula, String nombre, String apellido,
            String telefono, String email, String especialidad,
            String numeroLicencia, boolean activo) {
        super(cedula, nombre, apellido, telefono, email);
        this.especialidad = especialidad;
        this.numeroLicencia = numeroLicencia;
        this.activo = activo;
    }

    @Override
    public String getDatosContacto() {
        return "Esp: " + especialidad + " | Lic: " + numeroLicencia + " | Tel: " + getTelefono();
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " - " + especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
