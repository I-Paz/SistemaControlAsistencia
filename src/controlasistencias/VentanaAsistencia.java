package controlasistencias;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class VentanaAsistencia extends JFrame {
    JTextField txtID;
    
    public VentanaAsistencia() {
        // Configuración básica de la ventana
        setTitle("Control de Asistencia - Principal");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10)); // Diseño de rejilla
        setLocationRelativeTo(null); // Centrar en pantalla

        // Componentes visuales (Etiquetas y botones)
        JLabel lblTitulo = new JLabel("BIENVENIDO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblInstruccion = new JLabel("Ingrese su ID de Empleado:", SwingConstants.CENTER);
        
        txtID = new JTextField();
        txtID.setHorizontalAlignment(JTextField.CENTER);
        txtID.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JButton btnEntrada = new JButton("REGISTRAR ENTRADA");
        btnEntrada.setBackground(new Color(200, 255, 200)); // Color verde suave
        
        JButton btnSalida = new JButton("REGISTRAR SALIDA");
        btnSalida.setBackground(new Color(255, 200, 200)); // Color rojo suave
        
        JButton btnAdmin = new JButton("Acceso a Administrador");

        // Agregar componentes a la ventana
        add(lblTitulo);
        add(lblInstruccion);
        add(txtID);
        add(btnEntrada);
        add(btnSalida);
        add(btnAdmin);

        // Eventos (Lo que hacen los botones al dar clic)
        btnEntrada.addActionListener(e -> registrarAsistencia("Entrada"));
        btnSalida.addActionListener(e -> registrarAsistencia("Salida"));
        btnAdmin.addActionListener(e -> abrirLoginAdmin());
    }

    private void registrarAsistencia(String tipo) {
        String idStr = txtID.getText();
        
        if(idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su ID.");
            return;
        }

        try (Connection con = ConexionBD.conectar()) {
            if (con == null) return; // Si falla la conexión, no continuar

            // 1. Verificar si el empleado existe y está ACTIVO (Requisito de Fiabilidad)
            String sqlCheck = "SELECT NombreEmpleado FROM Empleado WHERE IDEmpleado = ? AND Activo = 1";
            PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
            pstCheck.setInt(1, Integer.parseInt(idStr));
            ResultSet rs = pstCheck.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("NombreEmpleado");
                
                // 2. Insertar el registro en la tabla AsistenciaRegistro
                String sqlInsert = "INSERT INTO AsistenciaRegistro (IDEmpleado, TipoRegistro) VALUES (?, ?)";
                PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
                pstInsert.setInt(1, Integer.parseInt(idStr));
                pstInsert.setString(2, tipo);
                pstInsert.executeUpdate();

                JOptionPane.showMessageDialog(this, "Hola " + nombre + ".\n" + tipo + " registrada correctamente.");
                txtID.setText(""); // Limpiar el campo para el siguiente empleado
            } else {
                JOptionPane.showMessageDialog(this, "Error: ID no encontrado o Empleado inactivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error técnico: " + e.getMessage());
        }
    }

    private void abrirLoginAdmin() {
        // Abre la ventana de inicio de sesión para administradores
        new LoginAdmin().setVisible(true);
    }

    // Método Main para arrancar la aplicación
    public static void main(String[] args) {
        try {
            // Estilo visual nativo de Windows (opcional, se ve mejor)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        
        new VentanaAsistencia().setVisible(true);
    }
}