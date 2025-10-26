package model;

public class Producto {
    private Long id;
    private String nombre;
    private Unidad unidad;

    public Producto(Long id, String nombre, Unidad unidad) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Unidad getUnidad() { return unidad; }

    @Override public String toString() {
        return id + " - " + nombre + " (" + unidad + ")";
    }
}
