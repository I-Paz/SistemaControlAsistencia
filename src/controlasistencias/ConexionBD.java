package controlasistencias;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class ConexionBD {
    
    public static Connection conectar() {
        try {
            // 1. Carga explícita del driver (Ayuda a veces cuando NetBeans se pone necio)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // 2. URL específica para tu instancia SQLEXPRESS02
            String url = "jdbc:sqlserver://localhost;instanceName=SQLEXPRESS02;databaseName=ControlAsistenciasDB;encrypt=true;trustServerCertificate=true;loginTimeout=30;";
            
            // 3. Tus credenciales (Asegúrate que sean las del usuario que creaste)
            String user = "proyectoUser";
            String pass = "123456";
            
            return DriverManager.getConnection(url, user, pass);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "ERROR CRÍTICO: No se encuentra el Driver.\nRevisa la carpeta Libraries en NetBeans.");
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de Conexión: " + e.getMessage());
            return null;
        }
    }
}