package repository;

import model.Movimiento;
import model.MovimientoDetalle;
import service.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoRepositoryJDBC {

    public void guardar(Movimiento mov) {
        String sqlMov = "INSERT INTO movimiento " +
                "(tipo, motivo, registrado_por, beneficiario_id, origen, accion_interna) " +
                "VALUES (?,?,?,?,?,?)";

        String sqlDet = "INSERT INTO movimiento_detalle " +
                "(movimiento_id, producto_id, cantidad) VALUES (?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psMov = conn.prepareStatement(
                    sqlMov, Statement.RETURN_GENERATED_KEYS)) {

                psMov.setString(1, mov.getTipo().name());
                psMov.setString(2, "Registrado desde prototipo JavaFX");

                psMov.setLong(3, mov.getRegistradoPor().getId());

                if (mov.getBeneficiario() != null) {
                    psMov.setLong(4, mov.getBeneficiario().getId());
                } else {
                    psMov.setNull(4, Types.BIGINT);
                }

                if (mov.getOrigen() != null) {
                    psMov.setString(5, mov.getOrigen().name());
                } else {
                    psMov.setNull(5, Types.VARCHAR);
                }

                if (mov.getAccionInterna() != null) {
                    psMov.setString(6, mov.getAccionInterna().name());
                } else {
                    psMov.setNull(6, Types.VARCHAR);
                }

                psMov.executeUpdate();

                long movId;
                try (ResultSet rsKeys = psMov.getGeneratedKeys()) {
                    if (!rsKeys.next()) {
                        throw new SQLException("No se generó id para movimiento");
                    }
                    movId = rsKeys.getLong(1);
                }

                try (PreparedStatement psDet = conn.prepareStatement(sqlDet)) {
                    for (MovimientoDetalle d : mov.getDetalles()) {
                        psDet.setLong(1, movId);
                        psDet.setLong(2, d.getProducto().getId());
                        psDet.setDouble(3, d.getCantidad());
                        psDet.addBatch();
                    }
                    psDet.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Error guardando movimiento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> listarMovimientosComoTexto() {
        String sql = "SELECT m.id, m.tipo, m.fecha, " +
                "uvol.nombre AS voluntario, uben.nombre AS beneficiario, " +
                "p.nombre AS producto, md.cantidad " +
                "FROM movimiento m " +
                "JOIN usuario uvol ON uvol.id = m.registrado_por " +
                "LEFT JOIN usuario uben ON uben.id = m.beneficiario_id " +
                "JOIN movimiento_detalle md ON md.movimiento_id = m.id " +
                "JOIN producto p ON p.id = md.producto_id " +
                "ORDER BY m.id DESC, p.nombre";

        List<String> lineas = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String tipo = rs.getString("tipo");
                Timestamp fecha = rs.getTimestamp("fecha");
                String voluntario = rs.getString("voluntario");
                String beneficiario = rs.getString("beneficiario");
                String producto = rs.getString("producto");
                double cant = rs.getDouble("cantidad");

                String linea = String.format(
                        "#%d %s %s - Vol: %s, Ben: %s, Prod: %s x %.2f",
                        id, tipo, fecha.toLocalDateTime(),
                        voluntario,
                        (beneficiario != null ? beneficiario : "-"),
                        producto, cant
                );
                lineas.add(linea);
            }

        } catch (SQLException e) {
            lineas.add("Error consultando movimientos: " + e.getMessage());
        }

        return lineas;
    }
}
