package dao.impl;

import dao.IDAO;
import model.Cita;
import model.Consulta;
import model.Paciente;
import model.Propietario;
import model.Veterinario;
import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class ConsultaDAO implements IDAO<Consulta> {

    private Connection conexion;

    private static final String SQL_JOIN
            = "SELECT CO.*, "
            + "P.nombre AS pac_nombre, P.especie AS pac_especie, "
            + "PR.nombre AS prop_nombre, PR.apellido AS prop_apellido, "
            + "V.nombre AS vet_nombre, V.apellido AS vet_apellido, V.especialidad AS vet_especialidad "
            + "FROM CONSULTA CO "
            + "JOIN PACIENTE P     ON CO.id_paciente = P.id "
            + "JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
            + "JOIN VETERINARIO V  ON CO.cedula_veterinario = V.cedula ";

    public ConsultaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Consulta c) throws SQLException {
        String sql = "INSERT INTO CONSULTA (id_cita, id_paciente, cedula_veterinario, "
                + "fecha_hora, sintomas, diagnostico, tratamiento, costo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, c.getCita().getId());
            ps.setInt(2, c.getPaciente().getId());
            ps.setString(3, c.getVeterinario().getCedula());
            ps.setTimestamp(4, Timestamp.valueOf(c.getFechaHora()));
            ps.setString(5, c.getSintomas());
            ps.setString(6, c.getDiagnostico());
            ps.setString(7, c.getTratamiento());
            ps.setDouble(8, c.getCosto());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Consulta consulta) throws SQLException {
        String sql = "UPDATE CONSULTA SET id_cita=?, id_paciente=?, cedula_veterinario=?, "
                + "fecha_hora=?, sintomas=?, diagnostico=?, tratamiento=?, costo=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, consulta.getCita().getId());
            ps.setInt(2, consulta.getPaciente().getId());
            ps.setString(3, consulta.getVeterinario().getCedula());
            ps.setTimestamp(4, Timestamp.valueOf(consulta.getFechaHora()));
            ps.setString(5, consulta.getSintomas());
            ps.setString(6, consulta.getDiagnostico());
            ps.setString(7, consulta.getTratamiento());
            ps.setDouble(8, consulta.getCosto());
            ps.setInt(9, consulta.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM CONSULTA WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Consulta buscarPorId(int id) throws SQLException {
        String sql = SQL_JOIN + "WHERE CO.id = ?";
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
    public ArrayList<Consulta> listarTodos() throws SQLException {
        ArrayList<Consulta> lista = new ArrayList<>();
        String sql = SQL_JOIN + "ORDER BY CO.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Consulta> listarPorPaciente(int idPaciente) throws SQLException {
        ArrayList<Consulta> lista = new ArrayList<>();
        String sql = SQL_JOIN + "WHERE CO.id_paciente = ? ORDER BY CO.fecha_hora DESC";
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

    public Consulta buscarPorCita(int idCita) throws SQLException {
        String sql = SQL_JOIN + "WHERE CO.id_cita = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public void completarConsulta(Consulta consulta) throws SQLException {
        String sql = "{ call sp_completar_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, consulta.getCita().getId());
            cs.setInt(2, consulta.getPaciente().getId());
            cs.setString(3, consulta.getVeterinario().getCedula());
            cs.setTimestamp(4, Timestamp.valueOf(consulta.getFechaHora()));
            cs.setString(5, consulta.getSintomas());
            cs.setString(6, consulta.getDiagnostico());
            cs.setString(7, consulta.getTratamiento());
            cs.setDouble(8, consulta.getCosto());
            cs.registerOutParameter(9, Types.INTEGER);
            cs.execute();
            consulta.setId(cs.getInt(9));
        }
    }

    private Consulta mapear(ResultSet rs) throws SQLException {
        Consulta consulta = new Consulta();
        consulta.setId(rs.getInt("id"));

        Cita cita = new Cita();
        cita.setId(rs.getInt("id_cita"));
        consulta.setCita(cita);

        Propietario propietario = new Propietario();
        propietario.setNombre(rs.getString("prop_nombre"));
        propietario.setApellido(rs.getString("prop_apellido"));

        Paciente pac = new Paciente();
        pac.setId(rs.getInt("id_paciente"));
        pac.setNombre(rs.getString("pac_nombre"));
        pac.setEspecie(rs.getString("pac_especie"));
        pac.setPropietario(propietario);
        consulta.setPaciente(pac);

        Veterinario vet = new Veterinario();
        vet.setCedula(rs.getString("cedula_veterinario"));
        vet.setNombre(rs.getString("vet_nombre"));
        vet.setApellido(rs.getString("vet_apellido"));
        vet.setEspecialidad(rs.getString("vet_especialidad"));
        consulta.setVeterinario(vet);

        consulta.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        consulta.setSintomas(rs.getString("sintomas"));
        consulta.setDiagnostico(rs.getString("diagnostico"));
        consulta.setTratamiento(rs.getString("tratamiento"));
        consulta.setCosto(rs.getDouble("costo"));
        return consulta;
    }
}
