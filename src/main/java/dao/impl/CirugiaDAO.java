package dao.impl;

import dao.IDAO;
import model.Cirugia;
import model.Paciente;
import model.Propietario;
import model.Veterinario;
import util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class CirugiaDAO implements IDAO<Cirugia> {

    private Connection conexion;

    public CirugiaDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Cirugia cirugia) throws SQLException {
        String sql = "INSERT INTO CIRUGIA (id_paciente, cedula_veterinario, fecha_hora, tipo_cirugia, "
                + "anestesia, descripcion, resultado, costo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cirugia.getPaciente().getId());
            ps.setString(2, cirugia.getVeterinario().getCedula());
            ps.setTimestamp(3, cirugia.getFechaHora() != null ? java.sql.Timestamp.valueOf(cirugia.getFechaHora()) : null);
            ps.setString(4, cirugia.getTipoCirugia());
            ps.setString(5, cirugia.getAnestesia());
            ps.setString(6, cirugia.getDescripcion());
            ps.setString(7, cirugia.getResultado());
            ps.setDouble(8, cirugia.getCosto());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cirugia cirugia) throws SQLException {
        String sql = "UPDATE CIRUGIA SET id_paciente=?, cedula_veterinario=?, fecha_hora=?, "
                + "tipo_cirugia=?, anestesia=?, descripcion=?, resultado=?, costo=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cirugia.getPaciente().getId());
            ps.setString(2, cirugia.getVeterinario().getCedula());
            ps.setTimestamp(3, cirugia.getFechaHora() != null ? java.sql.Timestamp.valueOf(cirugia.getFechaHora()) : null);
            ps.setString(4, cirugia.getTipoCirugia());
            ps.setString(5, cirugia.getAnestesia());
            ps.setString(6, cirugia.getDescripcion());
            ps.setString(7, cirugia.getResultado());
            ps.setDouble(8, cirugia.getCosto());
            ps.setInt(9, cirugia.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM CIRUGIA WHERE id=?";
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
            + "FROM CIRUGIA C "
            + "JOIN PACIENTE P ON C.id_paciente = P.id "
            + "JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
            + "JOIN VETERINARIO V ON C.cedula_veterinario = V.cedula ";

    @Override
    public Cirugia buscarPorId(int id) throws SQLException {
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
    public ArrayList<Cirugia> listarTodos() throws SQLException {
        ArrayList<Cirugia> lista = new ArrayList<>();
        String sql = SQL_JOIN + "ORDER BY C.fecha_hora DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Cirugia> listarPorPaciente(int idPaciente) throws SQLException {
        ArrayList<Cirugia> lista = new ArrayList<>();
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

    private Cirugia mapear(ResultSet rs) throws SQLException {
        Cirugia cirugia = new Cirugia();
        cirugia.setId(rs.getInt("id"));

        Propietario propietario = new Propietario();
        propietario.setCedula(rs.getString("prop_cedula"));
        propietario.setNombre(rs.getString("prop_nombre"));
        propietario.setApellido(rs.getString("prop_apellido"));

        Paciente paciente = new Paciente();
        paciente.setId(rs.getInt("id_paciente"));
        paciente.setNombre(rs.getString("pac_nombre"));
        paciente.setEspecie(rs.getString("pac_especie"));
        paciente.setPropietario(propietario);
        cirugia.setPaciente(paciente);

        Veterinario veterinario = new Veterinario();
        veterinario.setCedula(rs.getString("cedula_veterinario"));
        veterinario.setNombre(rs.getString("vet_nombre"));
        veterinario.setApellido(rs.getString("vet_apellido"));
        veterinario.setEspecialidad(rs.getString("vet_especialidad"));
        cirugia.setVeterinario(veterinario);

        java.sql.Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        cirugia.setFechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null);

        cirugia.setTipoCirugia(rs.getString("tipo_cirugia"));
        cirugia.setAnestesia(rs.getString("anestesia"));
        cirugia.setDescripcion(rs.getString("descripcion"));
        cirugia.setResultado(rs.getString("resultado"));
        cirugia.setCosto(rs.getDouble("costo"));
        return cirugia;
    }
}
