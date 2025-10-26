package controller;

import exceptions.StockInsuficienteException;
import exceptions.ValidacionException;
import model.*;
import repository.InMemoryProductoRepository;
import repository.InMemoryUsuarioRepository;
import repository.ProductoRepository;
import repository.UsuarioRepository;
import service.InventarioService;

import java.util.HashMap;
import java.util.Map;

public class MainController {

    private final ProductoRepository productoRepo = new InMemoryProductoRepository();
    private final UsuarioRepository usuarioRepo = new InMemoryUsuarioRepository();
    private final InventarioService inventario = new InventarioService(productoRepo, usuarioRepo);

    public String registrarIngreso(Long usuarioId, OrigenMovimiento origen, AccionInterna accion, Long prodId, double cantidad) {
        try {
            Map<Long, Double> items = new HashMap<>();
            items.put(prodId, cantidad);
            inventario.registrarIngreso(usuarioId, origen, accion, items);
            return "Ingreso registrado correctamente.";
        } catch (ValidacionException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String registrarEntrega(Long voluntarioId, Long beneficiarioId, Long prodId, double cantidad) {
        try {
            Map<Long, Double> items = new HashMap<>();
            items.put(prodId, cantidad);
            inventario.registrarEntrega(voluntarioId, beneficiarioId, items);
            return "Entrega registrada correctamente.";
        } catch (StockInsuficienteException | ValidacionException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String obtenerStock() {
        return String.join("\n", inventario.stockComoLineasOrdenadoPorNombre());
    }
}
