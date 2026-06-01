package util;

import model.Usuario;

public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {}

    public static void setUsuario(Usuario u) {
        usuarioActual = u;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static String getRol() {
        if (usuarioActual == null || usuarioActual.getRol() == null) {
            return "";
        }
        return usuarioActual.getRol();
    }

    public static String getUsername() {
        if (usuarioActual == null) {
            return "";
        }
        return usuarioActual.getUsername() != null ? usuarioActual.getUsername() : "";
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
