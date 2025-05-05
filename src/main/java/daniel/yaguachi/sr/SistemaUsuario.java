package daniel.yaguachi.sr;

import datos.ConexionDB;
import modelo.Persona;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class SistemaUsuario extends JFrame {

    private JTextField txtNombre, txtApellido, txtCedula, txtCorreo, txtFechaNacimiento;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;
    private int idSeleccionado = -1;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public SistemaUsuario() {
        configurarInterfaz();
        cargarUsuarios();
    }

private void configurarInterfaz() {
    setTitle("Sistema de Gestión de Usuarios");
    setSize(1300, 700);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout(15, 15));
    aplicarTemaModerno();

    // Panel principal con padding
    JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    mainPanel.setBackground(new Color(30, 33, 36));

    // Panel izquierdo (formulario)
    mainPanel.add(crearPanelFormulario(), BorderLayout.WEST);
    
    // Panel derecho (tabla)
    mainPanel.add(crearPanelTabla(), BorderLayout.CENTER);

    add(mainPanel);
    configurarEventos();
}

private JPanel crearPanelFormulario() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(37, 42, 48));
    panel.setBorder(crearBordeTitulo("Datos del Usuario"));
    panel.setPreferredSize(new Dimension(400, 0));

    // Campos de texto con sus etiquetas
    JPanel formFields = new JPanel(new GridBagLayout());
    formFields.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 10, 8, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Crear campos
    txtNombre = crearCampoTexto("Nombre");
    txtApellido = crearCampoTexto("Apellido");
    txtCedula = crearCampoTexto("Cédula");
    txtCorreo = crearCampoTexto("Correo electrónico");
    txtFechaNacimiento = crearCampoTexto("dd-mm-yyyy");

    // Agregar campos con sus etiquetas
    agregarCampoConEtiqueta(formFields, "Nombre:", txtNombre, 0, gbc);
    agregarCampoConEtiqueta(formFields, "Apellido:", txtApellido, 1, gbc);
    agregarCampoConEtiqueta(formFields, "Cédula:", txtCedula, 2, gbc);
    agregarCampoConEtiqueta(formFields, "Correo:", txtCorreo, 3, gbc);
    agregarCampoConEtiqueta(formFields, "Fecha Nacimiento:", txtFechaNacimiento, 4, gbc);

    panel.add(formFields);
    panel.add(Box.createVerticalStrut(20));
    panel.add(crearPanelBotones());

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
    panel.setBorder(crearBordeTitulo("Listado de Usuarios"));

    // Configurar tabla
    modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Apellido", "Cédula", "Correo", "Fecha Nac.", "Edad"}, 
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tablaUsuarios = crearTablaModerna(modeloTabla);
    JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setBackground(new Color(45, 50, 56));

    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
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
    return campo;
}

private JButton crearBotonModerno(String texto, Color color) {
    JButton boton = new JButton(texto);
    boton.setBackground(color);
    boton.setForeground(Color.WHITE);
    boton.setFocusPainted(false);
    boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    boton.setFont(boton.getFont().deriveFont(Font.BOLD));
    
    // Efectos hover
    boton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            boton.setBackground(color.brighter());
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            boton.setBackground(color);
        }
    });
    
    return boton;
}

private void agregarCampoConEtiqueta(JPanel panel, String etiqueta, JTextField campo, int y, GridBagConstraints gbc) {
    JLabel label = new JLabel(etiqueta);
    label.setForeground(Color.WHITE);
    label.setFont(label.getFont().deriveFont(Font.BOLD));
    
    gbc.gridx = 0;
    gbc.gridy = y;
    gbc.weightx = 0.1;
    panel.add(label, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    panel.add(campo, gbc);
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
    tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));
    tabla.setSelectionBackground(new Color(0, 120, 212));
    tabla.setSelectionForeground(Color.WHITE);
    
    // Personalizar el renderizado de las celdas
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < tabla.getColumnCount(); i++) {
        tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }
    
    return tabla;
}

private Border crearBordeTitulo(String titulo) {
    return BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
        BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            titulo,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Dialog", Font.BOLD, 14),
            Color.WHITE
        )
    );
}

private void aplicarTemaModerno() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    UIManager.put("Panel.background", new Color(30, 33, 36));
    UIManager.put("OptionPane.background", new Color(37, 42, 48));
    UIManager.put("OptionPane.messageForeground", Color.WHITE);
}

    private void configurarEventos() {
        btnRegistrar.addActionListener(e -> registrarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaUsuarios.getSelectedRow();
                if (fila >= 0) cargarDatosFormulario(fila);
            }
        });
    }

    private void eliminarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este usuario?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM usuario WHERE id = ?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idSeleccionado);
                ps.executeUpdate();
                cargarUsuarios();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente!");

            } catch (SQLException ex) {
                manejarError("Error al eliminar: " + ex.getMessage());
            }
        }
    }


    private void registrarUsuario() {
        try {
            Persona nuevoUsuario = crearUsuarioDesdeFormulario();
            nuevoUsuario.CalcularEdad(); // Asegurar cálculo de edad

            String sql = "INSERT INTO usuario (nombre, apellido, numIdentificacion, correo, fechaNacimiento, edad) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, nuevoUsuario.getNombre());
                ps.setString(2, nuevoUsuario.getApellido());
                ps.setString(3, nuevoUsuario.getNumIdentificacion());
                ps.setString(4, nuevoUsuario.getCorreo());
                ps.setDate(5, Date.valueOf(nuevoUsuario.getFechaNacimiento()));
                ps.setInt(6, nuevoUsuario.getEdad());

                ps.executeUpdate();
                cargarUsuarios();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Registro exitoso!");
            }
        } catch (IllegalArgumentException | SQLException ex) {
            manejarError(ex.getMessage());
        }
    }

    private void actualizarUsuario() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla");
            return;
        }

        try {
            Persona usuarioActualizado = crearUsuarioDesdeFormulario();
            usuarioActualizado.CalcularEdad();

            String sql = "UPDATE usuario SET nombre=?, apellido=?, numIdentificacion=?, "
                    + "correo=?, fechaNacimiento=?, edad=? WHERE id=?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, usuarioActualizado.getNombre());
                ps.setString(2, usuarioActualizado.getApellido());
                ps.setString(3, usuarioActualizado.getNumIdentificacion());
                ps.setString(4, usuarioActualizado.getCorreo());
                ps.setDate(5, Date.valueOf(usuarioActualizado.getFechaNacimiento()));
                ps.setInt(6, usuarioActualizado.getEdad());
                ps.setInt(7, idSeleccionado);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    cargarUsuarios();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(this, "Actualización exitosa!");
                }
            }
        } catch (IllegalArgumentException | SQLException ex) {
            manejarError(ex.getMessage());
        }
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id, nombre, apellido, numIdentificacion, correo, fechaNacimiento, edad FROM usuario";

        try (Connection conn = ConexionDB.AbrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id"), // Añadir el ID oculto
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("numIdentificacion"),
                        rs.getString("correo"),
                        rs.getDate("fechaNacimiento").toLocalDate().format(dateFormatter),
                        rs.getInt("edad")
                });
            }
        } catch (SQLException ex) {
            manejarError("Error al cargar datos: " + ex.getMessage());
        }
    }

    private Persona crearUsuarioDesdeFormulario() {
        validarCamposVacios();
        LocalDate fechaNacimiento = parsearFecha(txtFechaNacimiento.getText().trim());

        return new Persona(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtCedula.getText().trim(),
                txtCorreo.getText().trim(),
                fechaNacimiento
        );
    }

    private LocalDate parsearFecha(String textoFecha) {
        try {
            return LocalDate.parse(textoFecha, dateFormatter);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use dd-mm-yyyy");
        }
    }

    private void validarCamposVacios() {
        // Validar que ningún campo esté vacío
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtCedula.getText().trim().isEmpty() ||
                txtCorreo.getText().trim().isEmpty() ||
                txtFechaNacimiento.getText().trim().isEmpty()) {

            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        // Validar formato del correo electrónico
        String correo = txtCorreo.getText().trim();
        String regexCorreo = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";  // Expresión regular mejorada
        if (!correo.matches(regexCorreo)) {
            throw new IllegalArgumentException(
                    "Formato de correo inválido. Ejemplo válido: usuario@dominio.com"
            );
        }
    }

    private void cargarDatosFormulario(int fila) {
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0); // ID está en columna 0 (oculta)
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());     // Columna 1 = Nombre
        txtApellido.setText(modeloTabla.getValueAt(fila, 2).toString());   // Columna 2 = Apellido
        txtCedula.setText(modeloTabla.getValueAt(fila, 3).toString());      // Columna 3 = Cédula
        txtCorreo.setText(modeloTabla.getValueAt(fila, 4).toString());      // Columna 4 = Correo
        txtFechaNacimiento.setText(modeloTabla.getValueAt(fila, 5).toString()); // Columna 5 = Fecha
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtCorreo.setText("");
        txtFechaNacimiento.setText("");
        idSeleccionado = -1;
    }

    private void manejarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void aplicarTemaOscuro() {
        UIManager.put("Panel.background", new Color(34, 40, 49)); // Fondo principal
        UIManager.put("Label.foreground", Color.WHITE); // Texto en etiquetas
        UIManager.put("TextField.background", new Color(57, 62, 70)); // Fondo campos
        UIManager.put("TextField.foreground", Color.WHITE); // Texto en campos
        UIManager.put("Table.background", new Color(57, 62, 70)); // Fondo tabla
        UIManager.put("Table.foreground", Color.WHITE); // Texto tabla
        UIManager.put("Button.background", new Color(0, 173, 181)); // Botones
        UIManager.put("Button.foreground", Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaUsuario().setVisible(true));
    }

    // Métodos auxiliares de UI
    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField(20);
        campo.setPreferredSize(new Dimension(250, 30));
        return campo;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(120, 35));
        return boton;
    }

    private void agregarComponente(JPanel panel, Component comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }
}