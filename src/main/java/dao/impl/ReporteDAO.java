package dao.impl;

import util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;

public class ReporteDAO {

    private Connection conexion;

    public ReporteDAO() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    private void refrescarConexion() {
        this.conexion = ConexionBD.getInstancia().getConexion();
    }

    public int consultasPorVeterinario(String cedulaVet, LocalDate desde, LocalDate hasta)
            throws SQLException {
        refrescarConexion();
        String sql = "{ ? = call PKG_REPORTES.consultas_por_vet(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, cedulaVet);
            cs.setDate(3, Date.valueOf(desde));
            cs.setDate(4, Date.valueOf(hasta));
            cs.execute();
            return cs.getInt(1);
        }
    }

    public double ingresosPorTipoConcepto(String tipoConcepto, LocalDate desde, LocalDate hasta)
            throws SQLException {
        refrescarConexion();
        String sql = "{ ? = call PKG_REPORTES.ingresos_por_tipo(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, tipoConcepto);
            cs.setDate(3, Date.valueOf(desde));
            cs.setDate(4, Date.valueOf(hasta));
            cs.execute();
            return cs.getDouble(1);
        }
    }

    public Integer idPacienteMasActivo(LocalDate desde, LocalDate hasta)
            throws SQLException {
        refrescarConexion();
        String sql = "{ ? = call PKG_REPORTES.paciente_mas_activo(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setDate(2, Date.valueOf(desde));
            cs.setDate(3, Date.valueOf(hasta));
            cs.execute();
            int id = cs.getInt(1);
            return cs.wasNull() ? null : id;
        }
    }

    public double tasaCancelacion(LocalDate desde, LocalDate hasta)
            throws SQLException {
        refrescarConexion();
        String sql = "{ ? = call PKG_REPORTES.tasa_cancelacion(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setDate(2, Date.valueOf(desde));
            cs.setDate(3, Date.valueOf(hasta));
            cs.execute();
            return cs.getDouble(1);
        }
    }
}
