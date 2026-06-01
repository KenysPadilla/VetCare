package service;

import dao.IDAO;
import dao.impl.CitaDAO;
import model.Cita;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

public class CitaService {

    private static final Logger LOG = Logger.getLogger(CitaService.class.getName());

    private IDAO<Cita> dao;

    private final FirebaseService firebaseService = new FirebaseService();

    public CitaService() {
        this.dao = new CitaDAO();
    }

    public void guardar(Cita cita) throws SQLException {
        if (cita.getPaciente() == null) {
            throw new IllegalArgumentException("La cita debe tener un paciente asignado.");
        }
        if (cita.getVeterinario() == null) {
            throw new IllegalArgumentException("La cita debe tener un veterinario asignado.");
        }
        if (cita.getFechaHora() == null ||
                !cita.getFechaHora().toLocalDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "No se pueden agendar citas para el día actual " +
                    "ni para fechas pasadas. La fecha mínima es mañana.");
        }
        if (cita.getFechaHora() != null) {
            boolean disponible = ((CitaDAO) dao).verificarDisponibilidad(
                    cita.getVeterinario().getCedula(), cita.getFechaHora());
            if (!disponible) {
                throw new IllegalStateException(
                        "El veterinario ya tiene una cita programada en esa hora.");
            }
        }
        dao.guardar(cita);
        publicarEnFirebase(cita.getFechaHora() != null ? cita.getFechaHora().toLocalDate() : null);
    }

    public ArrayList<Cita> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Cita buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public void actualizar(Cita cita) throws SQLException {
        dao.actualizar(cita);
        publicarEnFirebase(cita.getFechaHora() != null ? cita.getFechaHora().toLocalDate() : null);
    }

    public boolean eliminar(int id) throws SQLException {
        Cita cita = dao.buscarPorId(id);
        boolean eliminado = dao.eliminar(id);
        if (eliminado) {
            publicarEnFirebase(cita != null && cita.getFechaHora() != null
                    ? cita.getFechaHora().toLocalDate() : null);
        }
        return eliminado;
    }

    public ArrayList<Cita> listarPorVeterinario(String cedulaVeterinario) throws SQLException {
        return ((CitaDAO) dao).listarPorVeterinario(cedulaVeterinario);
    }

    public ArrayList<Cita> listarPorPaciente(int idPaciente) throws SQLException {
        return ((CitaDAO) dao).listarPorPaciente(idPaciente);
    }

    public ArrayList<Cita> listarPorEstado(String estado) throws SQLException {
        return ((CitaDAO) dao).listarPorEstado(estado);
    }

    public void actualizarEstado(int id, String estado) throws SQLException {
        Cita cita = dao.buscarPorId(id);
        ((CitaDAO) dao).actualizarEstado(id, estado);
        publicarEnFirebase(cita != null && cita.getFechaHora() != null
                ? cita.getFechaHora().toLocalDate() : null);
    }

    public ArrayList<Cita> listarDisponiblesParaConsulta() throws SQLException {
        return ((CitaDAO) dao).listarDisponiblesParaConsulta();
    }

    public void cancelarCita(int id) throws SQLException {
        Cita cita = dao.buscarPorId(id);
        ((CitaDAO) dao).cancelarCita(id);
        publicarEnFirebase(cita != null && cita.getFechaHora() != null
                ? cita.getFechaHora().toLocalDate() : null);
    }

    public int contarCitasVetEnFecha(String cedulaVet, LocalDate fecha) throws SQLException {
        return ((CitaDAO) dao).contarCitasVetEnFecha(cedulaVet, fecha);
    }

    private void publicarEnFirebase(LocalDate fecha) {
        if (fecha == null) return;
        new Thread(() -> {
            try {
                firebaseService.publicarHorasOcupadas(fecha);
            } catch (Exception e) {
                LOG.warning("CitaService: no se pudo publicar en Firebase — " + e.getMessage());
            }
        }).start();
    }
}
