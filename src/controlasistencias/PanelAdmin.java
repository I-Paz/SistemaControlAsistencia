package controlasistencias;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelAdmin extends JFrame {
    JTabbedPane tabs;
    
    // --- Variables para Gestión de Empleados ---
    JTextField txtNewID, txtNewNombre, txtNewPuesto, txtNewPass;
    JTable tablaEmpleados;
    DefaultTableModel modeloEmpleados;

    // --- Variables para Reportes ---
    JTable tablaReportes;
    DefaultTableModel modeloReportes;
    JTextField txtFiltroID;

    public PanelAdmin() {
        setTitle("Sistema de Gestión - Administrador");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Al cerrar solo cierra este panel

        tabs = new JTabbedPane();

        // Crear las dos pestañas principales
        tabs.addTab("Gestión de Empleados", crearPanelGestion());
        tabs.addTab("Reportes de Asistencia", crearPanelReportes());

        add(tabs);
        
        // Cargar la lista de empleados al abrir la ventana
        cargarEmpleados(); 

        // --- CORRECCIÓN: ACTUALIZACIÓN AUTOMÁTICA ---
        // Esto detecta cuando cambias de pestaña
        tabs.addChangeListener(e -> {
            // Si la pestaña seleccionada es la de Reportes (índice 1)
            if (tabs.getSelectedIndex() == 1) {
                generarReporte(); // ¡Consulta la base de datos automáticamente!
            } else {
                cargarEmpleados(); // Si vuelves a gestión, actualiza la lista de empleados también
            }
        });
        // ----------------------------------------------
    }

    // --- PESTAÑA 1: DISEÑO GESTIÓN DE EMPLEADOS ---
    private JPanel crearPanelGestion() {
        JPanel p = new JPanel(new BorderLayout());
        
        // Panel superior para agregar empleados (Formulario)
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo Empleado"));
        
        form.add(new JLabel("ID Empleado (Numérico):")); 
        txtNewID = new JTextField(); form.add(txtNewID);
        
        form.add(new JLabel("Nombre Completo:")); 
        txtNewNombre = new JTextField(); form.add(txtNewNombre);
        
        form.add(new JLabel("Puesto:")); 
        txtNewPuesto = new JTextField(); form.add(txtNewPuesto);
        
        form.add(new JLabel("Contraseña (Para login):")); 
        txtNewPass = new JTextField(); form.add(txtNewPass);
        
        JButton btnAgregar = new JButton("Guardar Empleado");
        form.add(new JLabel("")); // Espacio vacío
        form.add(btnAgregar);
        
        // Acción del botón guardar
        btnAgregar.addActionListener(e -> agregarEmpleado());

        // Tabla central para ver empleados
        modeloEmpleados = new DefaultTableModel();
        modeloEmpleados.addColumn("ID");
        modeloEmpleados.addColumn("Nombre");
        modeloEmpleados.addColumn("Puesto");
        modeloEmpleados.addColumn("Activo"); // Muestra true/false o 1/0
        tablaEmpleados = new JTable(modeloEmpleados);

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);
        
        return p;
    }

    // --- PESTAÑA 2: DISEÑO REPORTES ---
    private JPanel crearPanelReportes() {
        JPanel p = new JPanel(new BorderLayout());
        
        JPanel filtros = new JPanel();
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));
        
        filtros.add(new JLabel("Filtrar por ID de Empleado (Dejar vacío para ver todos):"));
        txtFiltroID = new JTextField(10);
        filtros.add(txtFiltroID);
        
        JButton btnGenerar = new JButton("Consultar Reporte");
        filtros.add(btnGenerar);
        
        btnGenerar.addActionListener(e -> generarReporte());

        // Tabla de resultados
        modeloReportes = new DefaultTableModel();
        modeloReportes.addColumn("ID Registro");
        modeloReportes.addColumn("Empleado");
        modeloReportes.addColumn("Fecha y Hora");
        modeloReportes.addColumn("Tipo (Entrada/Salida)");
        tablaReportes = new JTable(modeloReportes);

        p.add(filtros, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaReportes), BorderLayout.CENTER);
        return p;
    }

    // --- LÓGICA: AGREGAR EMPLEADO (CRUD) ---
    private void agregarEmpleado() {
        try (Connection con = ConexionBD.conectar()) {
            // Inserta en la tabla Empleado
            String sql = "INSERT INTO Empleado (IDEmpleado, NombreEmpleado, Puesto, Activo, Contrasena) VALUES (?, ?, ?, 1, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setInt(1, Integer.parseInt(txtNewID.getText()));
            pst.setString(2, txtNewNombre.getText());
            pst.setString(3, txtNewPuesto.getText());
            pst.setString(4, txtNewPass.getText()); // Puede estar vacío si es un empleado normal
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Empleado Agregado Exitosamente");
            
            // Limpiar campos y recargar tabla
            txtNewID.setText(""); txtNewNombre.setText(""); txtNewPuesto.setText(""); txtNewPass.setText("");
            cargarEmpleados();
            
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }

    // --- LÓGICA: LEER EMPLEADOS (PARA LA TABLA) ---
    private void cargarEmpleados() {
        modeloEmpleados.setRowCount(0); // Limpiar tabla antes de llenar
        try (Connection con = ConexionBD.conectar()) {
            if (con == null) return;
            
            String sql = "SELECT IDEmpleado, NombreEmpleado, Puesto, Activo FROM Empleado";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                modeloEmpleados.addRow(new Object[]{
                    rs.getInt("IDEmpleado"), 
                    rs.getString("NombreEmpleado"), 
                    rs.getString("Puesto"), 
                    rs.getBoolean("Activo")
                });
            }
        } catch (Exception e) {
            System.err.println("Error cargando tabla empleados: " + e.getMessage());
        }
    }

    // --- LÓGICA: GENERAR REPORTES ---
    private void generarReporte() {
        modeloReportes.setRowCount(0); // Limpiar tabla
        String id = txtFiltroID.getText();
        
        // Consulta uniendo las dos tablas (JOIN) para traer el nombre del empleado
        String sql = "SELECT r.IDRegistro, e.NombreEmpleado, r.FechaHora, r.TipoRegistro " +
                     "FROM AsistenciaRegistro r " +
                     "JOIN Empleado e ON r.IDEmpleado = e.IDEmpleado";
        
        // Si el usuario escribió un ID, agregamos el filtro WHERE
        if(!id.isEmpty()) {
            try {
                int idNum = Integer.parseInt(id);
                sql += " WHERE e.IDEmpleado = " + idNum;
            } catch (NumberFormatException e) {
                // Si escriben letras en el filtro, simplemente ignoramos el filtro o avisamos
                // JOptionPane.showMessageDialog(this, "El filtro de ID debe ser numérico");
            }
        }
        
        sql += " ORDER BY r.FechaHora DESC"; // Ordenar del más reciente al más antiguo

        try (Connection con = ConexionBD.conectar()) {
            if (con == null) return;
            
            ResultSet rs = con.createStatement().executeQuery(sql);
            
            while (rs.next()) {
                modeloReportes.addRow(new Object[]{
                    rs.getInt("IDRegistro"), 
                    rs.getString("NombreEmpleado"), 
                    rs.getTimestamp("FechaHora"), 
                    rs.getString("TipoRegistro")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar reporte: " + e.getMessage());
        }
    }
}