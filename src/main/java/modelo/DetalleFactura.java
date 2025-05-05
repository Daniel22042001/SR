package modelo;

import modelo.Factura;
import modelo.Producto;

import javax.persistence.*;

@Entity
@Table(name = "detalle_factura")
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private float precioUnitario;

    // Constructor vacío
    public DetalleFactura() {
    }

    // Constructor con parámetros (Factura, Producto, cantidad, precioUnitario)
    public DetalleFactura(Factura factura, Producto producto, int cantidad, float precioUnitario) {
        this.factura = factura;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // Método para calcular el total del detalle
    public float getTotal() {
        return this.cantidad * this.precioUnitario;
    }

    // Método para obtener el id del producto
    public int getIdProducto() {
        return this.producto != null ? this.producto.getId() : 0;  // Retorna el ID del producto, o 0 si no existe el producto
    }
}
