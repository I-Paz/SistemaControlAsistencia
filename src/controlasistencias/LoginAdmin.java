package controlasistencias;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginAdmin extends JFrame {
    JTextField txtUser;
    JPasswordField txtPass;

    public LoginAdmin() {
        setTitle("Seguridad - Acceso Admin");
        setSize(350, 200);
        setLayout(new GridLayout(4, 1, 10, 10)); // Diseño de 4 filas
        setLocationRelativeTo(null);

        // Campos de entrada
        JPanel panelUser = new JPanel();
        panelUser.add(new JLabel("ID Admin: "));
        txtUser = new JTextField(15);
        panelUser.add(txtUser);
        
        JPanel panelPass = new JPanel();
        panelPass.add(new JLabel("Contraseña: "));
        txtPass = new JPasswordField(15);
        panelPass.add(txtPass);
        
        JButton btnLogin = new JButton("INGRESAR AL SISTEMA");

        add(new JLabel("Solo personal autorizado", SwingConstants.CENTER));
        add(panelUser);
        add(panelPass);
        add(btnLogin);

        // Acción del botón
        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        try (Connection con = ConexionBD.conectar()) {
            if (con == null) return;

            // Consulta segura validando ID y Contraseña 
            // Nota: Asumimos que el puesto 'Gerente' o 'Administrador' tiene acceso
            String sql = "SELECT * FROM Empleado WHERE IDEmpleado = ? AND Contrasena = ?"; 
            
            PreparedStatement pst = con.prepareStatement(sql);
            try {
                pst.setInt(1, Integer.parseInt(txtUser.getText()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID debe ser numérico.");
                return;
            }
            pst.setString(2, new String(txtPass.getPassword()));
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Si existe y la contraseña es correcta:
                this.dispose(); // Cierra esta ventana de login
                new PanelAdmin().setVisible(true); // Abre el Panel de Administración
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales Incorrectas", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}