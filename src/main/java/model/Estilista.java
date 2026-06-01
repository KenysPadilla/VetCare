package model;

public class Estilista extends Persona {

    private String especialidadEstetica;

    private boolean activo = true;

    public Estilista() {
        super();
    }

    public Estilista(String cedula, String nombre, String apellido,
                     String telefono, String email, String especialidadEstetica) {
        super(cedula, nombre, apellido, telefono, email);
        this.especialidadEstetica = especialidadEstetica;
        this.activo = true;
    }

    public Estilista(String cedula, String nombre, String apellido,
                     String telefono, String email, String especialidadEstetica, boolean activo) {
        super(cedula, nombre, apellido, telefono, email);
        this.especialidadEstetica = especialidadEstetica;
        this.activo = activo;
    }

    @Override
    public String getDatosContacto() {
        return "Esp: " + especialidadEstetica + " | Tel: " + getTelefono();
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " - " + especialidadEstetica;
    }

    public String getEspecialidadEstetica() {
        return especialidadEstetica;
    }

    public void setEspecialidadEstetica(String especialidadEstetica) {
        this.especialidadEstetica = especialidadEstetica;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
