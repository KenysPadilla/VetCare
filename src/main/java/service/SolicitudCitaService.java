package service;

import dao.impl.SolicitudCitaDAO;
import dao.impl.VeterinarioDAO;
import model.SolicitudCita;
import model.Veterinario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SolicitudCitaService {

    private static final Logger LOG = Logger.getLogger(SolicitudCitaService.class.getName());

    private final SolicitudCitaDAO solicitudDAO = new SolicitudCitaDAO();
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    private final EmailService emailService = new EmailService();
    private final FirebaseService firebaseService = new FirebaseService();

    public ArrayList<SolicitudCita> listarPendientes() throws SQLException {
        return solicitudDAO.listarPendientes();
    }

    public boolean aceptarSolicitud(SolicitudCita solicitudCita) throws SQLException {
        ArrayList<Veterinario> veterinarios = veterinarioDAO.listarTodos();
        if (veterinarios == null || veterinarios.isEmpty()) {
            throw new IllegalStateException(
                    "No hay veterinarios registrados. Por favor registre un veterinario antes de aceptar solicitudes.");
        }
        Veterinario veterinario = veterinarios.get(0);

        String nombreActual = solicitudCita.getNombrePropietario() != null
                ? solicitudCita.getNombrePropietario().trim() : "-";
        if (!nombreActual.contains(" ")) {
            String nombreNormalizado = nombreActual + " -";
            solicitudDAO.actualizarNombrePropietario(solicitudCita.getId(), nombreNormalizado);
        }

        solicitudDAO.aceptarSolicitudChatbot(solicitudCita.getId(), veterinario.getCedula());
        publicarEnFirebase(solicitudCita.getFecha());

        try {
            emailService.enviarConfirmacionCita(solicitudCita.getCorreo(),
                    solicitudCita.getNombrePropietario(),
                    solicitudCita.getNombreMascota(),
                    solicitudCita.getFecha().toString(),
                    solicitudCita.getHora()
            );
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.err.println("Advertencia: no se pudo enviar correo de confirmacion. " + e.getMessage());
            return false;
        }
    }

    public boolean rechazarSolicitud(SolicitudCita solicitudCita) throws SQLException {
        solicitudDAO.actualizarEstado(solicitudCita.getId(), "RECHAZADA");
        publicarEnFirebase(solicitudCita.getFecha());
        try {
            emailService.enviarRechazoSolicitud(solicitudCita.getCorreo(), solicitudCita.getNombrePropietario());
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.err.println("Advertencia: no se pudo enviar correo de rechazo. " + e.getMessage());
            return false;
        }
    }

    private void publicarEnFirebase(java.time.LocalDate fecha) {
        if (fecha == null) {
            return;
        }
        new Thread(() -> {
            try {
                firebaseService.publicarHorasOcupadas(fecha);
            } catch (Exception e) {
                LOG.warning("SolicitudCitaService: no se pudo publicar en Firebase — " + e.getMessage());
            }
        }).start();
    }
}
