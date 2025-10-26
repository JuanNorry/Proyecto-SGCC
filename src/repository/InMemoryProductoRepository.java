package repository;

import model.Producto;
import model.Unidad;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProductoRepository implements ProductoRepository {
    private final List<Producto> productos = new ArrayList<>();

    public InMemoryProductoRepository() {
        productos.add(new Producto(1L, "Arroz", Unidad.KG));
        productos.add(new Producto(2L, "Leche", Unidad.LITRO));
        productos.add(new Producto(3L, "Fideos", Unidad.KG));
        productos.add(new Producto(4L, "Aceite", Unidad.LITRO));
        productos.add(new Producto(5L, "Latas de Tomate", Unidad.UNIDAD));
    }

    @Override public List<Producto> findAll() { return productos; }

    @Override public Optional<Producto> findById(Long id) {
        return productos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }
}
