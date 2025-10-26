package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Movimiento {
    private Long id;
    private LocalDateTime fecha;
    private TipoMovimiento tipo;
    private OrigenMovimiento origen;        // solo para INGRESO
    private AccionInterna accionInterna;    // si origen == ACCION_INTERNA
    private Usuario registradoPor;
    private Usuario beneficiario;           // solo para ENTREGA
    private final List<MovimientoDetalle> detalles = new ArrayList<>();

    public Movimiento(Long id, TipoMovimiento tipo) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
    }

    public void addDetalle(MovimientoDetalle d) { detalles.add(d); }

    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public TipoMovimiento getTipo() { return tipo; }
    public OrigenMovimiento getOrigen() { return origen; }
    public AccionInterna getAccionInterna() { return accionInterna; }
    public Usuario getRegistradoPor() { return registradoPor; }
    public Usuario getBeneficiario() { return beneficiario; }
    public List<MovimientoDetalle> getDetalles() { return detalles; }

    public void setOrigen(OrigenMovimiento origen) { this.origen = origen; }
    public void setAccionInterna(AccionInterna accionInterna) { this.accionInterna = accionInterna; }
    public void setRegistradoPor(Usuario registradoPor) { this.registradoPor = registradoPor; }
    public void setBeneficiario(Usuario beneficiario) { this.beneficiario = beneficiario; }
}
