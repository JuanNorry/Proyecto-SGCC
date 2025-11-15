package controller;

import exceptions.StockInsuficienteException;
import exceptions.ValidacionException;
import model.AccionInterna;
import model.Movimiento;
import model.OrigenMovimiento;
import model.Producto;
import model.RolUsuario;
import model.Usuario;
import repository.InMemoryProductoRepository;
import repository.InMemoryUsuarioRepository;
import repository.MovimientoRepositoryJDBC;
import repository.ProductoRepository;
import repository.UsuarioRepository;
import service.InventarioService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    private final ProductoRepository productoRepo = new InMemoryProductoRepository();
    private final UsuarioRepository usuarioRepo  = new InMemoryUsuarioRepository();

    private final InventarioService inventario   = new InventarioService(productoRepo, usuarioRepo);

    private final MovimientoRepositoryJDBC movimientoRepo = new MovimientoRepositoryJDBC();

    private Usuario usuarioActual;

    public List<Usuario> getUsuarios() {
        return usuarioRepo.findAll();
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public List<Producto> getProductos() {
        return productoRepo.findAll();
    }

    public String registrarIngreso(OrigenMovimiento origen,
                                   AccionInterna accion,
                                   Long prodId,
                                   double cantidad) {

        if (usuarioActual == null) {
            return "Error: debe iniciar sesión.";
        }
        if (usuarioActual.getRol() != RolUsuario.ADMIN &&
            usuarioActual.getRol() != RolUsuario.VOLUNTARIO) {
            return "Error: solo un ADMIN o VOLUNTARIO puede registrar ingresos.";
        }

        try {
            Map<Long, Double> items = new HashMap<>();
            items.put(prodId, cantidad);

            Movimiento mov = inventario.registrarIngreso(
                    usuarioActual.getId(),  // quien registra
                    origen,
                    accion,
                    items
            );

            movimientoRepo.guardar(mov);

            return "Ingreso registrado correctamente.";
        } catch (ValidacionException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String registrarEntrega(Long prodId, double cantidad) {

        if (usuarioActual == null) {
            return "Error: debe iniciar sesión.";
        }

        RolUsuario rol = usuarioActual.getRol();
        if (rol != RolUsuario.ADMIN && rol != RolUsuario.VOLUNTARIO) {
            return "Error: solo un ADMIN o VOLUNTARIO puede registrar entregas.";
        }

        Long voluntarioId   = usuarioActual.getId();
        Long beneficiarioId = 10L; 

        try {
            Map<Long, Double> items = new HashMap<>();
            items.put(prodId, cantidad);

            Movimiento mov = inventario.registrarEntrega(
                    voluntarioId,
                    beneficiarioId,
                    items
            );

            movimientoRepo.guardar(mov);

            return "Entrega registrada correctamente.";
        } catch (StockInsuficienteException | ValidacionException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String obtenerStock() {
        return String.join("\n", inventario.stockComoLineasOrdenadoPorNombre());
    }

    public String obtenerMovimientosBD() {
        var lineas = movimientoRepo.listarMovimientosComoTexto();
        return String.join("\n", lineas);
    }
}
