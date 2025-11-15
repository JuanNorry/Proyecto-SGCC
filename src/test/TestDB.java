package test;

import service.DatabaseConnection;
import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Conexión exitosa a MySQL!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
