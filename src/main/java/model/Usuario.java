package model;

public class Usuario {

    private String cedula;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String nombreUsuario;
    private String contrasena;
    private String rol;
    private boolean activo;
    private String codigoRecuperacion;
    private java.time.LocalDateTime expiracionCodigo;

    public Usuario() {
    }

    public Usuario(String cedula, String nombre, String apellido, String telefono, 
            String email, String nombreUsuario, String contrasena, String rol, boolean activo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = activo;
    }

    public boolean autenticar(String password) {
        return password.equals(this.contrasena);
    }

    public boolean tienePermiso(String modulo) {
        if (modulo == null) {
            return false;
        }
        String m = modulo.toLowerCase();

        switch (rol) {
            case "ADMIN":
                return true;
            case "VETERINARIO":
                return m.contains("citas")
                        || m.contains("consultas")
                        || m.contains("vacunaciones")
                        || m.contains("cirugias")
                        || m.contains("internaciones")
                        || m.contains("laboratorio")
                        || m.contains("pacientes");
            case "RECEPCIONISTA":
                return m.contains("citas")
                        || m.contains("propietarios")
                        || m.contains("pacientes")
                        || m.contains("facturacion")
                        || m.contains("serviciosesteticos");
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return nombreUsuario + " (" + rol + ")";
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getUsername() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getCodigoRecuperacion() {
        return codigoRecuperacion;
    }

    public void setCodigoRecuperacion(String codigoRecuperacion) {
        this.codigoRecuperacion = codigoRecuperacion;
    }

    public java.time.LocalDateTime getExpiracionCodigo() {
        return expiracionCodigo;
    }

    public void setExpiracionCodigo(java.time.LocalDateTime expiracionCodigo) {
        this.expiracionCodigo = expiracionCodigo;
    }
}
