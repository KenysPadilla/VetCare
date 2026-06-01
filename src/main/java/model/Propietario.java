package model;

public class Propietario extends Persona {

    private String direccion;

    public Propietario() {
        super();
    }

    public Propietario(String cedula, String nombre, String apellido,
            String telefono, String email, String direccion) {
        super(cedula, nombre, apellido, telefono, email);
        this.direccion = direccion;
    }

    @Override
    public String getDatosContacto() {
        return "Direccion: " + direccion + " | Tel: " + getTelefono() + " | Email: " + getEmail();
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " - CC: " + getCedula();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
