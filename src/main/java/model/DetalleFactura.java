package model;

public class DetalleFactura {

    private int id;
    private Factura factura;
    private String descripcion;
    private String tipoConcepto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleFactura() {
    }

    public DetalleFactura(int id, Factura factura, String descripcion, String tipoConcepto,
            int cantidad, double precioUnitario, double subtotal) {
        this.id = id;
        this.factura = factura;
        this.descripcion = descripcion;
        this.tipoConcepto = tipoConcepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoConcepto() {
        return tipoConcepto;
    }

    public void setTipoConcepto(String tipoConcepto) {
        this.tipoConcepto = tipoConcepto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return descripcion + " x" + cantidad + " = $" + String.format("%.2f", subtotal);
    }
}
