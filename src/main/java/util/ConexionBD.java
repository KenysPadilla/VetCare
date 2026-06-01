package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() {
        conectar();
    }

    private void conectar() {
        try (InputStream input = ConexionBD.class
                .getResourceAsStream("/config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "No se encontró config.properties en el classpath.");
            }

            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            String usuario = props.getProperty("db.usuario");
            String password = props.getProperty("db.password");

            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.conexion = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conexión a Oracle establecida correctamente.");

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al leer config.properties: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Driver JDBC de Oracle no encontrado.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo conectar a Oracle: " + e.getMessage(), e);
        }
    }

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            synchronized (ConexionBD.class) {
                if (instancia == null) {
                    instancia = new ConexionBD();
                }
            }
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
                System.out.println("Reconectando a Oracle...");
                conectar();
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al verificar estado de la conexión: " + e.getMessage(), e);
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (instancia != null && instancia.conexion != null
                    && !instancia.conexion.isClosed()) {
                instancia.conexion.close();
                instancia = null;
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
