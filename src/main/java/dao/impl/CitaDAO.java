package dao.impl;

import dao.IDAO;
import model.Cita;
import model.Paciente;
import model.Propietario;
import model.Veterinario;
import util.ConexionBD;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CitaDAO implements IDAO<Cita> {

    private Connection conexion;

    public CitaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Cita cita) throws SQLException {
        String sql = "INSERT INTO CITA (id_paciente, cedula_veterinario, fecha_hora, tipo_cita, "
                + "estado_cita, motivo, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cita.getPaciente().getId());
            ps.setString(2, cita.getVeterinario().getCedula());
            ps.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            ps.setString(4, cita.getTipoCita());
            ps.setString(5, cita.getEstadoCita() != null ? cita.getEstadoCita() : "PROGRAMADA");
            ps.setString(6, cita.getMotivo());
            ps.setString(7, cita.getObservaciones());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cita cita) throws SQLException {
        String sql = "UPDATE CITA SET id_paciente=?, cedula_veterinario=?, fecha_hora=?, "
                + "tipo_cita=?, estado_cita=?, motivo=?, observaciones=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cita.getPaciente().getId());
            ps.setString(2, cita.getVeterinario().getCedula());
            ps.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            ps.setString(4, cita.getTipoCita());
            ps.setString(5, cita.getEstadoCita());
            ps.setString(6, cita.getMotivo());
            ps.setString(7, cita.getObservaciones());
            ps.setInt(8, cita.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM CITA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static final String SQL_JOIN
            = "SELECT C.*, "
            + "P.nombre AS pac_nombre, P.especie AS pac_especie, "
            + "PR.cedula AS prop_cedula, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido, "
            + "V.nombre AS vet_nombre, V.apellido AS vet_apellido, V.especialidad AS vet_especialidad "
            + "FROM CITA C "
            + "JOIN PACIENTE P ON C.id_paciente = P.id "
            + "JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
            + "JOIN VETERINARIO V ON C.cedula_veterinario = V.cedula ";

    @Override
    public Cita buscarPorId(int id) throws SQLException {
        String sql = SQL_JOIN + "WHERE C.id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<Cita> listarTodos() throws SQLException {
        ArrayList<Cita> lista = new ArrayList<>();
        String sql = SQL_JOIN + "ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Cita> listarPorVeterinario(String cedula) throws SQLException {
        ArrayList<Cita> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE C.cedula_veterinario=? ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public ArrayList<Cita> listarPorPaciente(int idPaciente) throws SQLException {
        ArrayList<Cita> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE C.id_paciente=? ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public ArrayList<Cita> listarPorEstado(String estado) throws SQLException {
        ArrayList<Cita> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE C.estado_cita=? ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public ArrayList<Cita> listarDisponiblesParaConsulta() throws SQLException {
        ArrayList<Cita> lista = new ArrayList<>();
        String sql = SQL_JOIN
                + "WHERE (C.estado_cita = 'PROGRAMADA' AND C.fecha_hora <= SYSTIMESTAMP) "
                + "   OR  C.estado_cita = 'EN_CURSO' "
                + "ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void actualizarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE CITA SET estado_cita=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public boolean verificarDisponibilidad(String cedulaVet, LocalDateTime fechaHora)
            throws SQLException {
        String sql = "{ ? = call fn_vet_disponible(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, cedulaVet);
            cs.setTimestamp(3, Timestamp.valueOf(fechaHora));
            cs.execute();
            return cs.getInt(1) == 1;
        }
    }

    public void cancelarCita(int idCita) throws SQLException {
        String sql = "{ call sp_cancelar_cita(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idCita);
            cs.execute();
        }
    }

    private Cita mapear(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setId(rs.getInt("id"));

        Propietario propietario = new Propietario();
        propietario.setCedula(rs.getString("prop_cedula"));
        propietario.setNombre(rs.getString("prop_nombre"));
        propietario.setApellido(rs.getString("prop_apellido"));

        Paciente paciente = new Paciente();
        paciente.setId(rs.getInt("id_paciente"));
        paciente.setNombre(rs.getString("pac_nombre"));
        paciente.setEspecie(rs.getString("pac_especie"));
        paciente.setPropietario(propietario);
        cita.setPaciente(paciente);

        Veterinario vet = new Veterinario();
        vet.setCedula(rs.getString("cedula_veterinario"));
        vet.setNombre(rs.getString("vet_nombre"));
        vet.setApellido(rs.getString("vet_apellido"));
        vet.setEspecialidad(rs.getString("vet_especialidad"));
        cita.setVeterinario(vet);

        cita.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setTipoCita(rs.getString("tipo_cita"));
        cita.setEstadoCita(rs.getString("estado_cita"));
        cita.setMotivo(rs.getString("motivo"));
        cita.setObservaciones(rs.getString("observaciones"));
        return cita;
    }
}
