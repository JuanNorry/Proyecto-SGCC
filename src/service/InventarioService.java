package service;

import exceptions.StockInsuficienteException;
import exceptions.ValidacionException;
import model.*;

import repository.ProductoRepository;
import repository.UsuarioRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InventarioService {

    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;

    private final Map<Long, Double> stock = new HashMap<>();

    private final AtomicLong seqMov = new AtomicLong(1000);
    private final AtomicLong seqDet = new AtomicLong(1);

    public InventarioService(ProductoRepository productoRepo, UsuarioRepository usuarioRepo) {
        this.productoRepo = productoRepo;
        this.usuarioRepo = usuarioRepo;
        for (var p : productoRepo.findAll()) stock.put(p.getId(), 0.0);
    }

    public Map<Long, Double> getStockSnapshot() {
        return Collections.unmodifiableMap(stock);
    }

    public Movimiento registrarIngreso(Long adminOrVolId,
                                       OrigenMovimiento origen,
                                       AccionInterna accionInterna,
                                       Map<Long, Double> itemsPorProductoId) {
        var user = usuarioRepo.findById(adminOrVolId)
                .orElseThrow(() -> new ValidacionException("Usuario no encontrado"));

        var mov = new Movimiento(seqMov.getAndIncrement(), TipoMovimiento.INGRESO);
        mov.setRegistradoPor(user);
        mov.setOrigen(origen);

        if (origen == OrigenMovimiento.ACCION_INTERNA) {
            if (accionInterna == null) {
                throw new ValidacionException("Falta acción interna");
            }
            mov.setAccionInterna(accionInterna);
        }

        for (var entry : itemsPorProductoId.entrySet()) {
            var producto = productoRepo.findById(entry.getKey())
                    .orElseThrow(() -> new ValidacionException("Producto inexistente: " + entry.getKey()));
            double cant = entry.getValue();
            if (cant <= 0) throw new ValidacionException("Cantidad inválida para " + producto.getNombre());

            var det = new MovimientoDetalle(seqDet.getAndIncrement(), producto, cant);
            mov.addDetalle(det);

            stock.put(producto.getId(), stock.get(producto.getId()) + cant);
        }
        return mov;
    }

    public Movimiento registrarEntrega(Long voluntarioId,
                                       Long beneficiarioId,
                                       Map<Long, Double> itemsPorProductoId) {
        var vol = usuarioRepo.findById(voluntarioId)
                .orElseThrow(() -> new ValidacionException("Voluntario no encontrado"));
        var ben = usuarioRepo.findById(beneficiarioId)
                .orElseThrow(() -> new ValidacionException("Beneficiario no encontrado"));

        var mov = new Movimiento(seqMov.getAndIncrement(), TipoMovimiento.ENTREGA);
        mov.setRegistradoPor(vol);
        mov.setBeneficiario(ben);

        mov.setOrigen(OrigenMovimiento.DONACION);

        for (var entry : itemsPorProductoId.entrySet()) {
            var producto = productoRepo.findById(entry.getKey())
                    .orElseThrow(() -> new ValidacionException("Producto inexistente: " + entry.getKey()));
            double cant = entry.getValue();
            if (cant <= 0) throw new ValidacionException("Cantidad inválida para " + producto.getNombre());

            double disponible = stock.getOrDefault(producto.getId(), 0.0);
            if (disponible < cant) {
                throw new StockInsuficienteException("Stock insuficiente de " + producto.getNombre()
                        + " (disp: " + disponible + ")");
            }
        }

        for (var entry : itemsPorProductoId.entrySet()) {
            var producto = productoRepo.findById(entry.getKey()).get();
            double cant = entry.getValue();
            var det = new MovimientoDetalle(seqDet.getAndIncrement(), producto, cant);
            mov.addDetalle(det);
            stock.put(producto.getId(), stock.get(producto.getId()) - cant);
        }
        return mov;
    }

    public List<String> stockComoLineasOrdenadoPorNombre() {
        var productos = new ArrayList<>(productoRepo.findAll());

        for (int i = 0; i < productos.size(); i++) {
            int min = i;
            for (int j = i + 1; j < productos.size(); j++) {
                if (productos.get(j).getNombre()
                        .compareToIgnoreCase(productos.get(min).getNombre()) < 0) {
                    min = j;
                }
            }
            var tmp = productos.get(i);
            productos.set(i, productos.get(min));
            productos.set(min, tmp);
        }

        List<String> r = new ArrayList<>();
        for (var p : productos) {
            r.add(p.getNombre() + " = " + stock.getOrDefault(p.getId(), 0.0) + " " + p.getUnidad());
        }
        return r;
    }
}
