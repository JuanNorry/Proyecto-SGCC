package model;

public class MovimientoDetalle {
    private Long id;
    private Producto producto;
    private double cantidad;

    public MovimientoDetalle(Long id, Producto producto, double cantidad) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }
    public Producto getProducto() { return producto; }
    public double getCantidad() { return cantidad; }
}
