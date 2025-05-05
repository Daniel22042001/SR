package daniel.yaguachi.sr;

import datos.ConexionDB;
import modelo.Producto;
import modelo.Factura;
import modelo.DetalleFactura;
import modelo.Persona;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.Border;

public class SistemaFactura extends JFrame {

    private JTextField txtCedula, txtTotalSinIVA, txtTotalConIVA;
    private JTable tablaProductos, tablaFactura;
    private DefaultTableModel modeloTablaProductos, modeloTablaFactura;
    private JButton btnBuscarUsuario, btnAgregarProducto, btnGenerarFactura;
    private int idUsuarioSeleccionado = -1;
    private ArrayList<DetalleFactura> detallesFactura = new ArrayList<>();
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("es", "ES"));
    // Agregar como variable de clase
    private JTextField txtCodigoProducto;
    // Agregar como variables de clase
private JTextField txtNombreUsuario, txtApellidoUsuario, txtCorreoUsuario;

    public SistemaFactura() {
        configurarInterfaz();
    }

    private void configurarInterfaz() {
        setTitle("Sistema de Facturación");
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        aplicarTemaModerno();

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(30, 33, 36));

        // Panel superior
        JPanel topPanel = crearPanelDatosFactura();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel central
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(crearPanelTablaProductos());
        centerPanel.add(crearPanelFactura());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
        
        // Configurar eventos
        configurarEventos();
    }

    // Modificar el método crearPanelDatosFactura()
private JPanel crearPanelDatosFactura() {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBackground(new Color(37, 42, 48));
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));

    // Panel izquierdo para todos los campos
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.setOpaque(false);

    // Panel para búsqueda de usuario
    JPanel userSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    userSearchPanel.setOpaque(false);
    
    txtCedula = crearCampoTexto("Ingrese cédula del cliente");
    btnBuscarUsuario = crearBotonModerno("Buscar Cliente", new Color(0, 120, 212));
    
    JLabel lblCedula = new JLabel("Cédula:");
    lblCedula.setForeground(Color.WHITE);
    
    userSearchPanel.add(lblCedula);
    userSearchPanel.add(txtCedula);
    userSearchPanel.add(btnBuscarUsuario);

    // Panel para datos del usuario
    JPanel userDataPanel = new JPanel(new GridBagLayout());
    userDataPanel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // Crear campos de texto para datos del usuario
    txtNombreUsuario = crearCampoTextoReadOnly("Nombre del cliente");
    txtApellidoUsuario = crearCampoTextoReadOnly("Apellido del cliente");
    txtCorreoUsuario = crearCampoTextoReadOnly("Correo del cliente");
    
    // Agregar campos con sus etiquetas
    agregarCampoConEtiqueta(userDataPanel, "Nombre:", txtNombreUsuario, 0, gbc);
    agregarCampoConEtiqueta(userDataPanel, "Apellido:", txtApellidoUsuario, 1, gbc);
    agregarCampoConEtiqueta(userDataPanel, "Correo:", txtCorreoUsuario, 2, gbc);

    // Panel para búsqueda de producto
    JPanel productSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    productSearchPanel.setOpaque(false);
    
    txtCodigoProducto = crearCampoTexto("Ingrese código del producto");
    JButton btnBuscarProducto = crearBotonModerno("Buscar Producto", new Color(0, 120, 212));
    
    JLabel lblCodigo = new JLabel("Código:");
    lblCodigo.setForeground(Color.WHITE);
    
    productSearchPanel.add(lblCodigo);
    productSearchPanel.add(txtCodigoProducto);
    productSearchPanel.add(btnBuscarProducto);

    // Agregar todos los paneles al panel izquierdo
    leftPanel.add(userSearchPanel);
    leftPanel.add(Box.createVerticalStrut(10));
    leftPanel.add(userDataPanel);
    leftPanel.add(Box.createVerticalStrut(10));
    leftPanel.add(productSearchPanel);

    // Panel derecho (totales)
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
    rightPanel.setOpaque(false);
    
    txtTotalSinIVA = crearCampoTextoTotal("0.00");
    txtTotalConIVA = crearCampoTextoTotal("0.00");
    
    JLabel lblSubtotal = new JLabel("Subtotal:");
    JLabel lblTotal = new JLabel("Total con IVA:");
    lblSubtotal.setForeground(Color.WHITE);
    lblTotal.setForeground(Color.WHITE);
    
    rightPanel.add(lblSubtotal);
    rightPanel.add(txtTotalSinIVA);
    rightPanel.add(lblTotal);
    rightPanel.add(txtTotalConIVA);

    panel.add(leftPanel, BorderLayout.WEST);
    panel.add(rightPanel, BorderLayout.EAST);
    
    // Configurar eventos
    btnBuscarProducto.addActionListener(e -> buscarProductoPorCodigo());
    
    return panel;
}

    private JPanel crearPanelTablaProductos() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(37, 42, 48));
        panel.setBorder(crearBordeTitulo("Productos Disponibles"));

        modeloTablaProductos = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Código", "Precio", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = crearTablaModerna(modeloTablaProductos);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        btnAgregarProducto = crearBotonModerno("Agregar Producto", new Color(92, 103, 64));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnAgregarProducto, BorderLayout.SOUTH);

        return panel;
    }

private JPanel crearPanelFactura() {
    JPanel panel = new JPanel(new BorderLayout(0, 0));
    panel.setBackground(new Color(7, 2, 8));
    panel.setBorder(crearBordeTitulo("Detalle de Factura"));

    // Crear el modelo de tabla con las columnas
    modeloTablaFactura = new DefaultTableModel(
        new Object[]{"Producto", "Precio Unit.", "Cantidad", "Total"}, 
        0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tablaFactura = crearTablaModerna(modeloTablaFactura);
    
    // Configurar el aspecto del encabezado de la tabla
    tablaFactura.getTableHeader().setVisible(true);
    tablaFactura.getTableHeader().setBackground(new Color(37, 42, 48));
    tablaFactura.getTableHeader().setForeground(Color.black);
    tablaFactura.getTableHeader().setFont(tablaFactura.getTableHeader().getFont().deriveFont(Font.BOLD));
    
    JScrollPane scrollPane = new JScrollPane(tablaFactura);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setViewportBorder(null);
    
    btnGenerarFactura = crearBotonModerno("Generar Factura", new Color(220, 38, 38));
    
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(btnGenerarFactura, BorderLayout.SOUTH);

    return panel;
}

    private void configurarEventos() {
        btnBuscarUsuario.addActionListener(e -> buscarUsuarioPorCedula());
        btnAgregarProducto.addActionListener(e -> agregarProductoAFactura());
        btnGenerarFactura.addActionListener(e -> generarFactura());
    }

    // Métodos de utilidad para la UI
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setPreferredSize(new Dimension(200, 35));
        campo.setBackground(new Color(5, 5, 6));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(5, 6, 6)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return campo;
    }

    private JTextField crearCampoTextoTotal(String initialValue) {
        JTextField campo = new JTextField(initialValue, 10);
        campo.setEditable(false);
        campo.setHorizontalAlignment(JTextField.RIGHT);
        campo.setBackground(new Color(45, 50, 56));
        campo.setForeground(new Color(129, 240, 127));
        campo.setFont(campo.getFont().deriveFont(Font.BOLD, 14f));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 60, 66)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return campo;
    }

    private JButton crearBotonModerno(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.black);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setFont(boton.getFont().deriveFont(Font.BOLD));
        
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

    private JTable crearTablaModerna(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
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

    // Agregar método para crear campos de texto de solo lectura
private JTextField crearCampoTextoReadOnly(String placeholder) {
    JTextField campo = crearCampoTexto(placeholder);
    campo.setEditable(false);
    campo.setBackground(new Color(40, 44, 52));
    return campo;
}

// Modificar el método buscarUsuarioPorCedula()
private void buscarUsuarioPorCedula() {
    String cedula = txtCedula.getText().trim();
    if (cedula.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor ingrese la cédula del usuario.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String sql = "SELECT id, nombre, apellido, correo FROM usuario WHERE numIdentificacion = ?";
    try (Connection conn = ConexionDB.AbrirConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, cedula);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            idUsuarioSeleccionado = rs.getInt("id");
            // Llenar los campos con los datos del usuario
            txtNombreUsuario.setText(rs.getString("nombre"));
            txtApellidoUsuario.setText(rs.getString("apellido"));
            txtCorreoUsuario.setText(rs.getString("correo"));
            cargarProductosDisponibles();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            limpiarCamposUsuario();
        }

    } catch (SQLException ex) {
        manejarError("Error al buscar usuario: " + ex.getMessage());
        limpiarCamposUsuario();
    }
}

// Agregar método para limpiar campos de usuario
private void limpiarCamposUsuario() {
    txtNombreUsuario.setText("");
    txtApellidoUsuario.setText("");
    txtCorreoUsuario.setText("");
    idUsuarioSeleccionado = -1;
}

    private void cargarProductosDisponibles() {
        String sql = "SELECT id, nombre, codigo, precio, stock FROM producto";
        try (Connection conn = ConexionDB.AbrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            modeloTablaProductos.setRowCount(0);
            while (rs.next()) {
                modeloTablaProductos.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getFloat("precio"),
                        rs.getInt("stock")
                });
            }

        } catch (SQLException ex) {
            manejarError("Error al cargar productos: " + ex.getMessage());
        }
    }

private void agregarProductoAFactura() {
    int filaSeleccionada = tablaProductos.getSelectedRow();
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un producto para agregar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int idProducto = (int) modeloTablaProductos.getValueAt(filaSeleccionada, 0);
    String nombreProducto = (String) modeloTablaProductos.getValueAt(filaSeleccionada, 1);
    float precioProducto = (float) modeloTablaProductos.getValueAt(filaSeleccionada, 3);
    int stockProducto = (int) modeloTablaProductos.getValueAt(filaSeleccionada, 4);

    if (stockProducto <= 0) {
        JOptionPane.showMessageDialog(this, "Producto sin stock disponible.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad de producto:");
    if (cantidadStr == null || cantidadStr.trim().isEmpty()) return;

    try {
        Number cantidadNumber = numberFormat.parse(cantidadStr.replace(',', '.'));
        int cantidad = cantidadNumber.intValue();

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cantidad > stockProducto) {
            JOptionPane.showMessageDialog(this, "Cantidad mayor al stock disponible.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Buscar si el producto ya existe en la factura
        int productoExistente = -1;
        for (int i = 0; i < modeloTablaFactura.getRowCount(); i++) {
            if (modeloTablaFactura.getValueAt(i, 0).toString().equals(nombreProducto)) {
                productoExistente = i;
                break;
            }
        }

        if (productoExistente != -1) {
            // El producto ya existe, actualizar cantidad
            Number cantidadActual = numberFormat.parse(modeloTablaFactura.getValueAt(productoExistente, 2).toString());
            int nuevaCantidad = cantidadActual.intValue() + cantidad;
            
            // Verificar que la nueva cantidad no supere el stock
            if (nuevaCantidad > stockProducto) {
                JOptionPane.showMessageDialog(this, "La cantidad total supera el stock disponible.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            float nuevoTotal = precioProducto * nuevaCantidad;
            
            // Actualizar la cantidad y el total en la tabla
            modeloTablaFactura.setValueAt(numberFormat.format(nuevaCantidad), productoExistente, 2);
            modeloTablaFactura.setValueAt(numberFormat.format(nuevoTotal), productoExistente, 3);
            
            // Actualizar el detalle en la lista
            DetalleFactura detalleExistente = detallesFactura.get(productoExistente);
            detalleExistente.setCantidad(nuevaCantidad);
        } else {
            // El producto no existe, agregar nuevo
            Producto producto = obtenerProductoPorId(idProducto);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            float total = precioProducto * cantidad;

            DetalleFactura detalle = new DetalleFactura(null, producto, cantidad, precioProducto);
            detallesFactura.add(detalle);

            modeloTablaFactura.addRow(new Object[]{
                nombreProducto,
                numberFormat.format(precioProducto),
                numberFormat.format(cantidad),
                numberFormat.format(total)
            });
        }

        actualizarTotales();

    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Formato numérico inválido", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void actualizarTotales() {
        float totalSinIVA = 0;
        try {
            for (int i = 0; i < modeloTablaFactura.getRowCount(); i++) {
                String totalStr = modeloTablaFactura.getValueAt(i, 3).toString();
                totalSinIVA += numberFormat.parse(totalStr).floatValue();
            }

            float iva = totalSinIVA * 0.12f;
            float totalConIVA = totalSinIVA + iva;

            txtTotalSinIVA.setText(numberFormat.format(totalSinIVA));
            txtTotalConIVA.setText(numberFormat.format(totalConIVA));
        } catch (ParseException e) {
            manejarError("Error al calcular totales: " + e.getMessage());
        }
    }

    private Producto obtenerProductoPorId(int idProducto) {
        Producto producto = null;
        String sql = "SELECT id, nombre, codigo, precio, stock FROM producto WHERE id = ?";
        try (Connection conn = ConexionDB.AbrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("codigo"),
                        rs.getFloat("precio"),
                        rs.getInt("stock")
                );
            }

        } catch (SQLException ex) {
            manejarError("Error al obtener producto: " + ex.getMessage());
        }
        return producto;
    }

    private Factura obtenerFactura() {
        Persona persona = null;
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (Connection conn = ConexionDB.AbrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuarioSeleccionado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                persona = new Persona();
                persona.setId(rs.getInt("id"));
                persona.setNombre(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            manejarError("Error al obtener datos del usuario: " + ex.getMessage());
        }

        Factura factura = new Factura();
        factura.setPersona(persona);
        factura.setFechaEmision(new Date());

        try {
            factura.setTotal(numberFormat.parse(txtTotalConIVA.getText()).floatValue());
        } catch (ParseException ex) {
            manejarError("Error en formato numérico: " + ex.getMessage());
            return null;
        }

        return factura;
    }

private void generarFactura() {
    if (idUsuarioSeleccionado == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (detallesFactura.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe agregar al menos un producto.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        Connection conn = ConexionDB.AbrirConexion();
        try {
            conn.setAutoCommit(false);

            String sqlFactura = "INSERT INTO factura (usuario_id, fecha_emision, total, persona_id) VALUES (?, ?, ?, ?)";
            PreparedStatement psFactura = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);
            
            // Obtener el total desde el campo de texto
            float total = numberFormat.parse(txtTotalConIVA.getText()).floatValue();
            
            psFactura.setInt(1, idUsuarioSeleccionado);
            psFactura.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            psFactura.setFloat(3, total);
            psFactura.setInt(4, idUsuarioSeleccionado);

            psFactura.executeUpdate();

            ResultSet rs = psFactura.getGeneratedKeys();
            if (rs.next()) {
                int idFactura = rs.getInt(1);
                
                String sqlDetalle = "INSERT INTO detalle_factura (factura_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
                PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                
                for (DetalleFactura detalle : detallesFactura) {
                    int productoId = detalle.getProducto().getId();
                    if (productoId <= 0) {
                        throw new IllegalStateException("ID de producto inválido: " + productoId);
                    }
                    
                    psDetalle.setInt(1, idFactura);
                    psDetalle.setInt(2, productoId);
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setFloat(4, detalle.getPrecioUnitario());
                    psDetalle.executeUpdate();
                    
                    // Actualizar stock
                    String sqlStock = "UPDATE producto SET stock = stock - ? WHERE id = ?";
                    PreparedStatement psStock = conn.prepareStatement(sqlStock);
                    psStock.setInt(1, detalle.getCantidad());
                    psStock.setInt(2, productoId);
                    psStock.executeUpdate();
                }
                
                conn.commit();
                JOptionPane.showMessageDialog(this, "Factura generada exitosamente!");
                
                limpiarFormulario();
            }
        } catch (SQLException | ParseException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            throw new RuntimeException(ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    } catch (Exception ex) {
        manejarError("Error al generar factura: " + ex.getMessage());
    }
}

// Agregar el método para buscar productos por código
private void buscarProductoPorCodigo() {
    String busqueda = txtCodigoProducto.getText().trim();
    if (busqueda.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor ingrese el código o nombre del producto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String sql = "SELECT id, nombre, codigo, precio, stock FROM producto " +
                 "WHERE LOWER(codigo) LIKE LOWER(?) OR LOWER(nombre) LIKE LOWER(?)";
    try (Connection conn = ConexionDB.AbrirConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        String terminoBusqueda = "%" + busqueda + "%";
        ps.setString(1, terminoBusqueda);
        ps.setString(2, terminoBusqueda);
        ResultSet rs = ps.executeQuery();

        boolean encontrado = false;
        while (rs.next()) {
            encontrado = true;
            // Seleccionar automáticamente el producto en la tabla
            String codigoProducto = rs.getString("codigo");
            String nombreProducto = rs.getString("nombre");
            
            for (int i = 0; i < modeloTablaProductos.getRowCount(); i++) {
                if (modeloTablaProductos.getValueAt(i, 2).toString().equalsIgnoreCase(codigoProducto) ||
                    modeloTablaProductos.getValueAt(i, 1).toString().equalsIgnoreCase(nombreProducto)) {
                    tablaProductos.setRowSelectionInterval(i, i);
                    tablaProductos.scrollRectToVisible(tablaProductos.getCellRect(i, 0, true));
                    break;
                }
            }
        }

        if (!encontrado) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException ex) {
        manejarError("Error al buscar producto: " + ex.getMessage());
    }
}

// Método para verificar si el producto existe
private boolean verificarProductoExiste(int productoId) {
    try (Connection conn = ConexionDB.AbrirConexion();
         PreparedStatement ps = conn.prepareStatement("SELECT id FROM producto WHERE id = ?")) {
        ps.setInt(1, productoId);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Método para verificar si hay stock suficiente
private boolean verificarStockSuficiente(int productoId, int cantidadRequerida) {
    try (Connection conn = ConexionDB.AbrirConexion();
         PreparedStatement ps = conn.prepareStatement("SELECT stock FROM producto WHERE id = ?")) {
        ps.setInt(1, productoId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int stockDisponible = rs.getInt("stock");
            return stockDisponible >= cantidadRequerida;
        }
        return false;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Modificar el método limpiarFormulario para incluir los nuevos campos
private void limpiarFormulario() {
    limpiarCamposUsuario();
    txtCedula.setText("");
    modeloTablaFactura.setRowCount(0);
    txtTotalSinIVA.setText("0.00");
    txtTotalConIVA.setText("0.00");
    modeloTablaProductos.setRowCount(0);
    detallesFactura.clear();
}

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return boton;
    }

    private void manejarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void aplicarTemaOscuro() {
        getContentPane().setBackground(new Color(45, 45, 45));
        UIManager.put("Button.background", new Color(33, 150, 243));
        UIManager.put("Panel.background", new Color(45, 45, 45));
        UIManager.put("Table.background", new Color(60, 63, 65));
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Table.selectionBackground", new Color(75, 110, 175));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(90, 90, 90));
        UIManager.put("TextField.background", new Color(60, 63, 65));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.caretForeground", Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SistemaFactura().setVisible(true);
        });
    }
    
    private void agregarCampoConEtiqueta(JPanel panel, String etiqueta, JTextField campo, int fila, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(etiqueta);
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = fila;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }
}