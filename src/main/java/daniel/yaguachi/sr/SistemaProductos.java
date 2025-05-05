package daniel.yaguachi.sr;

import datos.ConexionDB;
import modelo.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class SistemaProductos extends JFrame {
    // ... (mantener las variables existentes)

    private JTextField txtNombre, txtCodigo, txtPrecio, txtStock;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnActualizar, btnEliminar, btnLimpiar;
    private int idSeleccionado = -1;

    public SistemaProductos() {
        configurarInterfaz();
        cargarProductos();
    }

    private void configurarInterfaz() {
        setTitle("Sistema de Gestión de Productos");
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
        panel.setBorder(crearBordeTitulo("Datos del Producto"));
        panel.setPreferredSize(new Dimension(400, 0));

        // Panel para campos
        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Crear campos
        txtNombre = crearCampoTexto("Nombre del producto");
        txtCodigo = crearCampoTexto("Código del producto");
        txtPrecio = crearCampoTexto("0.00");
        txtStock = crearCampoTexto("0");

        // Agregar campos con sus etiquetas
        agregarCampoConEtiqueta(formFields, "Nombre:", txtNombre, 0, gbc);
        agregarCampoConEtiqueta(formFields, "Código:", txtCodigo, 1, gbc);
        agregarCampoConEtiqueta(formFields, "Precio ($):", txtPrecio, 2, gbc);
        agregarCampoConEtiqueta(formFields, "Stock:", txtStock, 3, gbc);

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

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(37, 42, 48));
        panel.setBorder(crearBordeTitulo("Listado de Productos"));

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Código", "Precio", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Double.class;
                if (columnIndex == 4) return Integer.class;
                return String.class;
            }
        };

        tablaProductos = crearTablaModerna(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(45, 50, 56));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable crearTablaModerna(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(35);
        tabla.setShowVerticalLines(true);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(55, 60, 66));
        tabla.setBackground(new Color(45, 50, 56));
        tabla.setForeground(Color.WHITE);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Configurar encabezados
        tabla.getTableHeader().setBackground(new Color(37, 42, 48));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        
        // Configurar selección
        tabla.setSelectionBackground(new Color(0, 120, 212));
        tabla.setSelectionForeground(Color.WHITE);
        
        return tabla;
    }

    private void agregarCampoConEtiqueta(JPanel panel, String etiqueta, JTextField campo, int y, GridBagConstraints gbc) {
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

    private Border crearBordeTitulo(String titulo) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                titulo,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 16),
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
        btnRegistrar.addActionListener(e -> registrarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProductos.getSelectedRow();
                if (fila >= 0) cargarDatosFormulario(fila);
            }
        });
    }

    private void eliminarProducto() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este producto?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM producto WHERE id = ?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idSeleccionado);
                ps.executeUpdate();
                cargarProductos();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente!");

            } catch (SQLException ex) {
                manejarError("Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void registrarProducto() {
        try {
            validarCampos();
            Producto nuevoProducto = crearProductoDesdeFormulario();

            String sql = "INSERT INTO producto (nombre, codigo, precio, stock) VALUES (?, ?, ?, ?)";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, nuevoProducto.getNombre());
                ps.setString(2, nuevoProducto.getCodigo());
                ps.setFloat(3, nuevoProducto.getPrecio());
                ps.setInt(4, nuevoProducto.getStock());

                ps.executeUpdate();
                cargarProductos();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Producto registrado exitosamente!");
            }
        } catch (IllegalArgumentException | SQLException ex) {
            manejarError(ex.getMessage());
        }
    }

    private void actualizarProducto() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
            return;
        }

        try {
            validarCampos();
            Producto productoActualizado = crearProductoDesdeFormulario();

            String sql = "UPDATE producto SET nombre = ?, codigo = ?, precio = ?, stock = ? WHERE id = ?";

            try (Connection conn = ConexionDB.AbrirConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, productoActualizado.getNombre());
                ps.setString(2, productoActualizado.getCodigo());
                ps.setFloat(3, productoActualizado.getPrecio());
                ps.setInt(4, productoActualizado.getStock());
                ps.setInt(5, idSeleccionado);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    cargarProductos();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente!");
                }
            }
        } catch (IllegalArgumentException | SQLException ex) {
            manejarError(ex.getMessage());
        }
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id, nombre, codigo, precio, stock FROM producto";

        try (Connection conn = ConexionDB.AbrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getFloat("precio"),
                        rs.getInt("stock")
                });
            }
        } catch (SQLException ex) {
            manejarError("Error al cargar datos: " + ex.getMessage());
        }
    }

    private Producto crearProductoDesdeFormulario() throws NumberFormatException {
        // Validar que los campos no estén vacíos
        if (txtCodigo.getText().trim().isEmpty() ||
                txtNombre.getText().trim().isEmpty() ||
                txtPrecio.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son requeridos");
        }

        try {
            float precio = Float.parseFloat(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (precio <= 0 || stock < 0) {
                throw new IllegalArgumentException("Precio y stock deben ser valores positivos");
            }

            return new Producto(
                    txtCodigo.getText().trim(),
                    txtNombre.getText().trim(),
                    precio,
                    stock
            );
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Formato inválido para precio o stock");
        }
    }

    private void validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtCodigo.getText().trim().isEmpty() ||
                txtPrecio.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty()) {

            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        try {
            float precio = Float.parseFloat(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (precio <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a cero");
            }
            if (stock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de precio o stock inválido");
        }
    }

    private void cargarDatosFormulario(int fila) {
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtCodigo.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtPrecio.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtStock.setText(modeloTabla.getValueAt(fila, 4).toString());
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtCodigo.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        idSeleccionado = -1;
    }

    private void manejarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void aplicarTemaOscuro() {
        UIManager.put("Panel.background", new Color(34, 40, 49));
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("TextField.background", new Color(57, 62, 70));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("Table.background", new Color(57, 62, 70));
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Button.background", new Color(0, 173, 181));
        UIManager.put("Button.foreground", Color.WHITE);
    }

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
        boton.setPreferredSize(new Dimension(130, 40));
        return boton;
    }

    private void agregarComponente(JPanel panel, Component comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaProductos().setVisible(true));
    }
}