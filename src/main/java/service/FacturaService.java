package service;

import dao.IDAO;
import dao.impl.DetalleFacturaDAO;
import dao.impl.FacturaDAO;
import model.DetalleFactura;
import model.Factura;
import java.sql.SQLException;
import java.util.ArrayList;

public class FacturaService {

    private IDAO<Factura> dao;

    public FacturaService() {
        this.dao = new FacturaDAO();
    }

    public double calcularTotal(double subtotal) {
        return subtotal * (1 + Factura.TASA_IVA);
    }

    public void generarFactura(Factura factura, ArrayList<DetalleFactura> detalles) throws SQLException {
        if (factura.getPropietario() == null) {
            throw new IllegalArgumentException("La factura debe tener un propietario asignado.");
        }
        if (factura.getPaciente() == null) {
            throw new IllegalArgumentException("La factura debe tener un paciente asignado.");
        }

        FacturaDAO facturaDAO = (FacturaDAO) dao;
        facturaDAO.guardar(factura);

        DetalleFacturaDAO detalleFacturaDAO = new DetalleFacturaDAO();
        double subtotal = 0;
        for (DetalleFactura detalleFactura : detalles) {
            detalleFactura.setFactura(factura);
            detalleFactura.setSubtotal(detalleFactura.calcularSubtotal());
            detalleFacturaDAO.guardar(detalleFactura);
            subtotal += detalleFactura.getSubtotal();
        }

        double total = facturaDAO.calcularTotalConIva(subtotal);
        facturaDAO.actualizarTotales(factura.getId(), subtotal, total);
    }

    public ArrayList<Factura> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Factura buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public ArrayList<Factura> listarPorPropietario(String cedula) throws SQLException {
        return ((FacturaDAO) dao).listarPorPropietario(cedula);
    }

    public ArrayList<Factura> listarPorEstado(String estado) throws SQLException {
        return ((FacturaDAO) dao).listarPorEstado(estado);
    }

    public ArrayList<DetalleFactura> listarDetalles(int idFactura) throws SQLException {
        return new DetalleFacturaDAO().listarPorFactura(idFactura);
    }

    public void anular(int id) throws SQLException {
        Factura factura = buscarPorId(id);
        if (factura == null) {
            throw new IllegalArgumentException("No existe la factura con id " + id + ".");
        }
        factura.anular();
        ((FacturaDAO) dao).actualizarEstado(id, "ANULADA");
    }

    public void actualizarEstado(int id, String estado) throws SQLException {
        ((FacturaDAO) dao).actualizarEstado(id, estado);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }
}
