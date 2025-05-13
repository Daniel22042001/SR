package daniel.yaguachi.sr;

import datos.ConexionDB;
import modelo.Especialidad;
import modelo.Doctor;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
import java.math.BigDecimal;

public class SistemaDoctores extends JFrame {
    private JTextField txtNombre, txtApellido, txtBusqueda, txtPrecioConsulta;
    private JTable tablaDoctores;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar, btnBuscar;
    private JComboBox<Especialidad> cmbEspecialidades;
    private JCheckBox chkDisponible;
    private int idSeleccionado = -1;
    private List<Especialidad> listaEspecialidades = new ArrayList<>();

// Agregar en la clase SistemaDoctores

private JButton btnGestionarEspecialidades;

    public SistemaDoctores() {
        configurarInterfaz();
        cargarEspecialidades();
        cargarDoctores();
    }

    private void configurarInterfaz() {
        setTitle("Sistema de Gestión de Doctores");
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        aplicarTemaModerno();

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(30, 33, 36));

        mainPanel.add(crearPanelBusqueda(), BorderLayout.NORTH);
        mainPanel.add(crearPanelFormulario(), BorderLayout.WEST);
        mainPanel.add(crearPanelTabla(), BorderLayout.CENTER);

        add(mainPanel);
        configurarEventos();
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);

        txtBusqueda = crearCampoTexto("Buscar por nombre, apellido o especialidad");
        btnBuscar = crearBotonModerno("Buscar", new Color(59, 130, 246));

        panel.add(txtBusqueda);
        panel.add(btnBuscar);

        return panel;
    }

    // Modificar crearPanelFormulario() para agregar el botón
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(37, 42, 48));
        panel.setBorder(crearBordeTitulo("Datos del Doctor"));
        panel.setPreferredSize(new Dimension(400, 0));

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = crearCampoTexto("Nombre");
        txtApellido = crearCampoTexto("Apellido");
        txtPrecioConsulta = crearCampoTexto("Precio consulta");
        txtPrecioConsulta.setEditable(false);
        cmbEspecialidades = new JComboBox<>();
        cmbEspecialidades.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Especialidad) {
                    setText(((Especialidad) value).getNombre());
                }
                return this;
            }
        });
        chkDisponible = new JCheckBox("Disponible");
        chkDisponible.setForeground(Color.WHITE);
        chkDisponible.setOpaque(false);

        agregarCampoConEtiqueta(formFields, "Nombre:", txtNombre, 0, gbc);
        agregarCampoConEtiqueta(formFields, "Apellido:", txtApellido, 1, gbc);

        JPanel especialidadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        especialidadPanel.setOpaque(false);
        btnGestionarEspecialidades = crearBotonModerno("Gestionar Especialidades", new Color(75, 85, 99));
        especialidadPanel.add(cmbEspecialidades);
        especialidadPanel.add(btnGestionarEspecialidades);
        
        agregarCampoConEtiqueta(formFields, "Especialidad:", especialidadPanel, 2, gbc);
        agregarCampoConEtiqueta(formFields, "Precio Consulta:", txtPrecioConsulta, 3, gbc);
        agregarCampoConEtiqueta(formFields, "Disponible:", chkDisponible, 4, gbc);

        panel.add(formFields);
        panel.add(Box.createVerticalStrut(20));
        panel.add(crearPanelBotones());

    // Agregar el evento al botón
    btnGestionarEspecialidades.addActionListener(e -> mostrarDialogoEspecialidades());
        
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        btnRegistrar = crearBotonModerno("Registrar", new Color(22, 163, 74));
        btnActualizar = crearBotonModerno("Actualizar", new Color(0, 120, 212));
        btnEliminar = crearBotonModerno("Eliminar", new Color(220, 38, 38));
        btnLimpiar = crearBotonModerno("Limpiar", new Color(100, 116, 139));

        panel.add(btnRegistrar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(37, 42, 48));
        panel.setBorder(crearBordeTitulo("Listado de Doctores"));

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Apellido", "Especialidad", "Precio Consulta", "Disponible"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaDoctores = crearTablaModerna(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaDoctores);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(45, 50, 56));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Métodos de base de datos y lógica
    private void cargarEspecialidades() {
        listaEspecialidades.clear();
        cmbEspecialidades.removeAllItems();
        String sql = "SELECT id, nombre, precio_consulta FROM especialidad";

        try (Connection conn = ConexionDB.AbrirConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Especialidad esp = new Especialidad(
                        rs.getString("nombre"),
                        BigDecimal.valueOf(rs.getDouble("precio_consulta"))
                );
                esp.setId(rs.getInt("id"));  // Establecer el ID después
                listaEspecialidades.add(esp);
                cmbEspecialidades.addItem(esp);
            }
        } catch (SQLException ex) {
            manejarError("Error cargando especialidades: " + ex.getMessage());
        }
    }

    private void cargarDoctores() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT d.id, d.nombre, d.apellido, e.nombre as especialidad, "
                + "e.precio_consulta, d.disponible "
                + "FROM doctor d LEFT JOIN especialidad e ON d.especialidad_id = e.id";

        try (Connection conn = ConexionDB.AbrirConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("especialidad"),
                        rs.getDouble("precio_consulta"),
                        rs.getBoolean("disponible") ? "Sí" : "No"
                });
            }
        } catch (SQLException ex) {
            manejarError("Error cargando doctores: " + ex.getMessage());
        }
    }

    private void registrarDoctor() {
        try {
            validarCampos();
            Especialidad esp = (Especialidad) cmbEspecialidades.getSelectedItem();

            String sql = "INSERT INTO doctor (nombre, apellido, especialidad_id, disponible) "
                    + "VALUES (?, ?, ?, ?)";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, txtNombre.getText().trim());
                ps.setString(2, txtApellido.getText().trim());
                ps.setInt(3, esp.getId());
                ps.setBoolean(4, chkDisponible.isSelected());

                ps.executeUpdate();
                cargarDoctores();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Doctor registrado exitosamente!");
            }
        } catch (Exception ex) {
            manejarError(ex.getMessage());
        }
    }

    private void actualizarDoctor() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un doctor de la tabla");
            return;
        }

        try {
            validarCampos();
            Especialidad esp = (Especialidad) cmbEspecialidades.getSelectedItem();

            String sql = "UPDATE doctor SET nombre = ?, apellido = ?, especialidad_id = ?, "
                    + "disponible = ? WHERE id = ?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, txtNombre.getText().trim());
                ps.setString(2, txtApellido.getText().trim());
                ps.setInt(3, esp.getId());
                ps.setBoolean(4, chkDisponible.isSelected());
                ps.setInt(5, idSeleccionado);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    cargarDoctores();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(this, "Doctor actualizado exitosamente!");
                }
            }
        } catch (Exception ex) {
            manejarError(ex.getMessage());
        }
    }

    private void buscarDoctores(String criterio) {
        modeloTabla.setRowCount(0);
        String sql = "SELECT d.id, d.nombre, d.apellido, e.nombre as especialidad, "
                + "e.precio_consulta, d.disponible "
                + "FROM doctor d LEFT JOIN especialidad e ON d.especialidad_id = e.id "
                + "WHERE d.nombre LIKE ? OR d.apellido LIKE ? OR e.nombre LIKE ?";

        try (Connection conn = ConexionDB.AbrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String param = "%" + criterio + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloTabla.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("especialidad"),
                            rs.getDouble("precio_consulta"),
                            rs.getBoolean("disponible") ? "Sí" : "No"
                    });
                }
            }
        } catch (SQLException ex) {
            manejarError("Error en búsqueda: " + ex.getMessage());
        }
    }

    private void eliminarDoctor() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un doctor de la tabla");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este doctor?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM doctor WHERE id = ?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idSeleccionado);
                ps.executeUpdate();
                cargarDoctores();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Doctor eliminado exitosamente!");

            } catch (SQLException ex) {
                manejarError("Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                cmbEspecialidades.getSelectedItem() == null) {

            throw new IllegalArgumentException("Todos los campos requeridos deben estar completos");
        }
    }

    private void cargarDatosFormulario(int fila) {
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtApellido.setText(modeloTabla.getValueAt(fila, 2).toString());

        String nombreEspecialidad = modeloTabla.getValueAt(fila, 3).toString();
        listaEspecialidades.stream()
                .filter(esp -> esp.getNombre().equals(nombreEspecialidad))
                .findFirst()
                .ifPresent(esp -> cmbEspecialidades.setSelectedItem(esp));

        chkDisponible.setSelected(modeloTabla.getValueAt(fila, 5).equals("Sí"));
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        cmbEspecialidades.setSelectedIndex(-1);
        txtPrecioConsulta.setText("");
        chkDisponible.setSelected(true);
        idSeleccionado = -1;
    }

    // Métodos de UI (crearCampoTexto, crearBotonModerno, etc. se mantienen igual que en tu código original)
    // ... (Los métodos de UI como crearCampoTexto, crearBotonModerno, etc. son iguales a los de tu código original)

    private void configurarEventos() {
        btnRegistrar.addActionListener(e -> registrarDoctor());
        btnActualizar.addActionListener(e -> actualizarDoctor());
        btnEliminar.addActionListener(e -> eliminarDoctor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> buscarDoctores(txtBusqueda.getText().trim()));

        cmbEspecialidades.addActionListener(e -> {
            Especialidad selected = (Especialidad) cmbEspecialidades.getSelectedItem();
            if (selected != null) {
                txtPrecioConsulta.setText(String.format("%.2f", selected.getPrecioConsulta()));
            }
        });

        tablaDoctores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaDoctores.getSelectedRow();
                if (fila >= 0) cargarDatosFormulario(fila);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaDoctores().setVisible(true));
    }

// Agregar estos métodos en la clase SistemaDoctores

private void aplicarTemaModerno() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Configuración de colores para el tema moderno
    Color backgroundColor = new Color(30, 33, 36);
    Color foregroundColor = Color.WHITE;
    Color accentColor = new Color(0, 120, 212);

    UIManager.put("Panel.background", backgroundColor);
    UIManager.put("OptionPane.background", new Color(37, 42, 48));
    UIManager.put("OptionPane.messageForeground", foregroundColor);
    UIManager.put("Button.background", accentColor);
    UIManager.put("Button.foreground", foregroundColor);
    UIManager.put("TextField.background", new Color(45, 50, 56));
    UIManager.put("TextField.foreground", foregroundColor);
}

private JTextField crearCampoTexto(String placeholder) {
    JTextField campo = new JTextField(20);
    campo.setPreferredSize(new Dimension(0, 35));
    campo.setBackground(new Color(45, 50, 56));
    campo.setForeground(Color.WHITE);
    campo.setCaretColor(Color.WHITE);
    campo.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(55, 60, 66)),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));
    campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    return campo;
}

private JButton crearBotonModerno(String texto, Color color) {
    JButton boton = new JButton(texto);
    boton.setBackground(color);
    boton.setForeground(Color.WHITE);
    boton.setFocusPainted(false);
    boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    boton.setFont(new Font("Segoe UI", Font.BOLD, 14));

    boton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            boton.setBackground(color.brighter());
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            boton.setBackground(color);
            boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    });

    return boton;
}

private Border crearBordeTitulo(String titulo) {
    return BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
        BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            titulo,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14),
            Color.WHITE
        )
    );
}

private JTable crearTablaModerna(DefaultTableModel modelo) {
    JTable tabla = new JTable(modelo);
    tabla.setRowHeight(35);
    tabla.setShowVerticalLines(true);
    tabla.setShowHorizontalLines(true);
    tabla.setGridColor(new Color(55, 60, 66));
    tabla.setBackground(new Color(45, 50, 56));
    tabla.setForeground(Color.WHITE);
    tabla.getTableHeader().setBackground(new Color(37, 42, 48));
    tabla.getTableHeader().setForeground(Color.WHITE);
    tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    tabla.setSelectionBackground(new Color(0, 120, 212));
    tabla.setSelectionForeground(Color.WHITE);
    return tabla;
}

private void agregarCampoConEtiqueta(JPanel panel, String etiqueta, Component campo, int y, GridBagConstraints gbc) {
    JLabel label = new JLabel(etiqueta);
    label.setForeground(Color.WHITE);
    label.setFont(new Font("Segoe UI", Font.BOLD, 14));

    gbc.gridx = 0;
    gbc.gridy = y;
    gbc.weightx = 0.1;
    panel.add(label, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    panel.add(campo, gbc);
}

private void manejarError(String mensaje) {
    JOptionPane.showMessageDialog(this,
        mensaje,
        "Error",
        JOptionPane.ERROR_MESSAGE);
}

// Modificar el constructor de Especialidad para aceptar double
private Especialidad crearEspecialidad(int id, String nombre, double precioConsulta) {
    Especialidad esp = new Especialidad();
    esp.setId(id);
    esp.setNombre(nombre);
    esp.setPrecioConsulta(BigDecimal.valueOf(precioConsulta));
    return esp;
}

private void mostrarDialogoEspecialidades() {
    JDialog dialogo = new JDialog(this, "Gestión de Especialidades", true);
    dialogo.setLayout(new BorderLayout(10, 10));
    dialogo.setSize(600, 400);
    dialogo.setLocationRelativeTo(this);

    // Panel de entrada
    JPanel inputPanel = new JPanel(new GridBagLayout());
    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JTextField txtNombreEsp = crearCampoTexto("Nombre especialidad");
    JTextField txtPrecioEsp = crearCampoTexto("Precio consulta");
    JButton btnAgregar = crearBotonModerno("Agregar", new Color(22, 163, 74));
    JButton btnActualizar = crearBotonModerno("Actualizar", new Color(0, 120, 212));

    // Tabla de especialidades
    DefaultTableModel modeloEsp = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Precio Consulta"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable tablaEsp = crearTablaModerna(modeloEsp);
    JScrollPane scrollPane = new JScrollPane(tablaEsp);

    // Agregar componentes al panel de entrada
    gbc.gridx = 0; gbc.gridy = 0;
    inputPanel.add(new JLabel("Nombre:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(txtNombreEsp, gbc);
    
    gbc.gridx = 0; gbc.gridy = 1;
    inputPanel.add(new JLabel("Precio:"), gbc);
    gbc.gridx = 1;
    inputPanel.add(txtPrecioEsp, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(btnAgregar);
    buttonPanel.add(btnActualizar);

    // Cargar especialidades existentes
    cargarEspecialidadesEnTabla(modeloEsp);

    // Eventos
    btnAgregar.addActionListener(e -> {
        try {
            String nombre = txtNombreEsp.getText().trim();
            BigDecimal precio = new BigDecimal(txtPrecioEsp.getText().trim());
            
            Especialidad nuevaEsp = new Especialidad(nombre, precio);
            
            String sql = "INSERT INTO especialidad (nombre, precio_consulta) VALUES (?, ?)";
            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, nombre);
                ps.setBigDecimal(2, precio);
                ps.executeUpdate();
                
                cargarEspecialidadesEnTabla(modeloEsp);
                cargarEspecialidades(); // Recargar el combo box
                limpiarCamposEspecialidad(txtNombreEsp, txtPrecioEsp);
                JOptionPane.showMessageDialog(dialogo, "Especialidad agregada exitosamente");
            }
        } catch (Exception ex) {
            manejarError("Error al agregar especialidad: " + ex.getMessage());
        }
    });

    tablaEsp.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() && tablaEsp.getSelectedRow() != -1) {
            int row = tablaEsp.getSelectedRow();
            txtNombreEsp.setText(modeloEsp.getValueAt(row, 1).toString());
            txtPrecioEsp.setText(modeloEsp.getValueAt(row, 2).toString());
        }
    });

    btnActualizar.addActionListener(e -> {
        if (tablaEsp.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(dialogo, "Seleccione una especialidad para actualizar");
            return;
        }

        try {
            int id = (int) modeloEsp.getValueAt(tablaEsp.getSelectedRow(), 0);
            String nombre = txtNombreEsp.getText().trim();
            BigDecimal precio = new BigDecimal(txtPrecioEsp.getText().trim());

            String sql = "UPDATE especialidad SET nombre = ?, precio_consulta = ? WHERE id = ?";
            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, nombre);
                ps.setBigDecimal(2, precio);
                ps.setInt(3, id);
                ps.executeUpdate();

                cargarEspecialidadesEnTabla(modeloEsp);
                cargarEspecialidades(); // Recargar el combo box
                limpiarCamposEspecialidad(txtNombreEsp, txtPrecioEsp);
                JOptionPane.showMessageDialog(dialogo, "Especialidad actualizada exitosamente");
            }
        } catch (Exception ex) {
            manejarError("Error al actualizar especialidad: " + ex.getMessage());
        }
    });

    // Agregar componentes al diálogo
    dialogo.add(inputPanel, BorderLayout.NORTH);
    dialogo.add(scrollPane, BorderLayout.CENTER);
    dialogo.add(buttonPanel, BorderLayout.SOUTH);

    dialogo.setVisible(true);
}

private void cargarEspecialidadesEnTabla(DefaultTableModel modelo) {
    modelo.setRowCount(0);
    String sql = "SELECT id, nombre, precio_consulta FROM especialidad";
    
    try (Connection conn = ConexionDB.AbrirConexion();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio_consulta")
            });
        }
    } catch (SQLException ex) {
        manejarError("Error al cargar especialidades: " + ex.getMessage());
    }
}

private void limpiarCamposEspecialidad(JTextField... campos) {
    for (JTextField campo : campos) {
        campo.setText("");
    }
}
}