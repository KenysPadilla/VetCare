package service;

import dao.IDAO;
import dao.impl.ServicioEsteticoDAO;
import model.ServicioEstetico;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServicioEsteticoService {

    private IDAO<ServicioEstetico> dao;

    public ServicioEsteticoService() {
        this.dao = new ServicioEsteticoDAO();
    }

    public void guardar(ServicioEstetico servicioEstetico) throws SQLException {
        if (servicioEstetico.getPaciente() == null) {
            throw new IllegalArgumentException("El servicio debe tener un paciente asignado.");
        }
        if (servicioEstetico.getEstilista() == null) {
            throw new IllegalArgumentException("El servicio debe tener un estilista asignado.");
        }
        if (servicioEstetico.getTipoServicio() == null || servicioEstetico.getTipoServicio().isBlank()) {
            throw new IllegalArgumentException("El tipo de servicio es obligatorio.");
        }
        if (servicioEstetico.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha del servicio es obligatoria.");
        }
        dao.guardar(servicioEstetico);
    }

    public ArrayList<ServicioEstetico> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public ServicioEstetico buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<ServicioEstetico> listarPorPaciente(int idPaciente) throws SQLException {
        return ((ServicioEsteticoDAO) dao).listarPorPaciente(idPaciente);
    }

    public ArrayList<ServicioEstetico> listarBanos() throws SQLException {
        return ((ServicioEsteticoDAO) dao).listarPorTipo("BANO");
    }

    public ArrayList<ServicioEstetico> listarMotiladas() throws SQLException {
        return ((ServicioEsteticoDAO) dao).listarPorTipo("MOTILADA");
    }

    public void actualizarEstado(int id, String estado) throws SQLException {
        ((ServicioEsteticoDAO) dao).actualizarEstado(id, estado);
    }

    public void actualizar(ServicioEstetico servicioEstetico) throws SQLException {
        dao.actualizar(servicioEstetico);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
