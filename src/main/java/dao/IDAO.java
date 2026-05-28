package dao;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IDAO<T> {

    void guardar(T entidad) throws SQLException;

    void actualizar(T entidad) throws SQLException;

    boolean eliminar(int id) throws SQLException;

    T buscarPorId(int id) throws SQLException;

    ArrayList<T> listarTodos() throws SQLException;
}
