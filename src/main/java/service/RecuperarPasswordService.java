package service;

import dao.impl.UsuarioDAO;
import model.Usuario;
import util.RecuperarPasswordException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;

public class RecuperarPasswordService {

    private final UsuarioDAO usuarioDAO;
    private final EmailService emailService;

    public RecuperarPasswordService() {
        this.usuarioDAO = new UsuarioDAO();
        this.emailService = new EmailService();
    }

    public void solicitarRecuperacion(String nombreUsuario, String correo)
            throws RecuperarPasswordException, SQLException {

        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
        if (usuario == null) {
            throw new RecuperarPasswordException("Usuario no encontrado.");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().equalsIgnoreCase(correo.trim())) {
            throw new RecuperarPasswordException(
                    "El correo no coincide con el registrado.");
        }

        String codigo = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(15);

        usuarioDAO.guardarCodigoRecuperacion(nombreUsuario, codigo, expiracion);
        emailService.enviarCodigoRecuperacion(correo.trim(), codigo);
    }

    public void verificarCodigo(String nombreUsuario, String codigoIngresado)
            throws RecuperarPasswordException, SQLException {

        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
        if (usuario == null || usuario.getCodigoRecuperacion() == null) {
            throw new RecuperarPasswordException(
                    "No hay solicitud de recuperacion activa.");
        }
        if (usuario.getExpiracionCodigo() != null
                && LocalDateTime.now().isAfter(usuario.getExpiracionCodigo())) {
            throw new RecuperarPasswordException(
                    "El codigo ha expirado. Solicita uno nuevo.");
        }
        if (!usuario.getCodigoRecuperacion().equals(codigoIngresado.trim())) {
            throw new RecuperarPasswordException("Codigo incorrecto.");
        }
    }

    public void cambiarPassword(String nombreUsuario, String nuevaPassword)
            throws RecuperarPasswordException, SQLException {

        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            throw new RecuperarPasswordException("La contrasena no puede estar vacia.");
        }
        usuarioDAO.actualizarPassword(nombreUsuario, nuevaPassword.trim());
        usuarioDAO.limpiarCodigoRecuperacion(nombreUsuario);
    }
}
