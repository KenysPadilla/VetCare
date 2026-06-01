package dao.impl;

import dao.IDAO;
import model.Paciente;
import model.Propietario;
import util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

public class PacienteDAO implements IDAO<Paciente> {

    private Connection conexion;

    public PacienteDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO PACIENTE (nombre, especie, raza, sexo, peso, "
                   + "fecha_nacimiento, microchip, cedula_propietario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getEspecie());
            ps.setString(3, paciente.getRaza());
            ps.setString(4, paciente.getSexo());
            ps.setDouble(5, paciente.getPeso());
            if (paciente.getFechaNacimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, paciente.getMicrochip());
            ps.setString(8, paciente.getPropietario() != null ? paciente.getPropietario().getCedula() : null);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE PACIENTE SET nombre=?, especie=?, raza=?, sexo=?, peso=?, "
                   + "fecha_nacimiento=?, microchip=?, cedula_propietario=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getEspecie());
            ps.setString(3, paciente.getRaza());
            ps.setString(4, paciente.getSexo());
            ps.setDouble(5, paciente.getPeso());
            if (paciente.getFechaNacimiento() != null) {
                ps.setDate(6, java.sql.Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, paciente.getMicrochip());
            ps.setString(8, paciente.getPropietario() != null ? paciente.getPropietario().getCedula() : null);
            ps.setInt(9, paciente.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM PACIENTE WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT P.*, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido "
                   + "FROM PACIENTE P LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
                   + "WHERE P.id=?";
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
    public ArrayList<Paciente> listarTodos() throws SQLException {
        ArrayList<Paciente> lista = new ArrayList<>();
        String sql = "SELECT P.*, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido "
                   + "FROM PACIENTE P LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
                   + "ORDER BY P.nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Paciente> buscarPorPropietario(String cedulaPropietario) throws SQLException {
        ArrayList<Paciente> lista = new ArrayList<>();
        String sql = "SELECT P.*, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido "
                   + "FROM PACIENTE P JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
                   + "WHERE P.cedula_propietario=? ORDER BY P.nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cedulaPropietario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public ArrayList<Paciente> buscarPorNombre(String texto) throws SQLException {
        ArrayList<Paciente> lista = new ArrayList<>();
        String filtro = "%" + texto.toUpperCase() + "%";
        String sql = "SELECT P.*, PR.nombre AS prop_nombre, PR.apellido AS prop_apellido "
                   + "FROM PACIENTE P LEFT JOIN PROPIETARIO PR ON P.cedula_propietario = PR.cedula "
                   + "WHERE UPPER(P.nombre) LIKE ? ORDER BY P.nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, filtro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public Integer calcularEdad(int idPaciente) throws SQLException {
        String sql = "{ ? = call fn_edad_mascota(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idPaciente);
            cs.execute();
            int edad = cs.getInt(1);
            return cs.wasNull() ? null : edad;
        }
    }

    public boolean tieneVacunasVencidas(int idPaciente) throws SQLException {
        String sql = "{ ? = call fn_tiene_vacunas_vencidas(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, idPaciente);
            cs.execute();
            return cs.getInt(1) == 1;
        }
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente();
        paciente.setId(rs.getInt("id"));
        paciente.setNombre(rs.getString("nombre"));
        paciente.setEspecie(rs.getString("especie"));
        paciente.setRaza(rs.getString("raza"));
        paciente.setSexo(rs.getString("sexo"));
        paciente.setPeso(rs.getDouble("peso"));

        java.sql.Date fechaSql = rs.getDate("fecha_nacimiento");
        paciente.setFechaNacimiento(fechaSql != null ? fechaSql.toLocalDate() : null);

        paciente.setMicrochip(rs.getString("microchip"));

        String cedulaProp = rs.getString("cedula_propietario");
        if (cedulaProp != null) {
            Propietario propietario = new Propietario();
            propietario.setCedula(cedulaProp);
            propietario.setNombre(rs.getString("prop_nombre"));
            propietario.setApellido(rs.getString("prop_apellido"));
            paciente.setPropietario(propietario);
        }

        return paciente;
    }
}
