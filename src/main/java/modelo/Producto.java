package modelo;

import javax.persistence.*;
import java.util.Date;








@Entity
@Table(name = "producto",
        uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "codigo", nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private float precio;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", updatable = false)
    private Date fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_actualizacion")
    private Date fechaActualizacion;

    // Constructor vacío (requerido por JPA)
    public Producto() {
    }


    public Producto(int id, String nombre, String codigo, float precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.precio = precio;
        this.stock = stock;
    }



    // Constructor con parámetros principales
    public Producto(String nombre, String codigo, float precio, int stock) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.precio = precio;
        this.stock = stock;
    }




    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    // Métodos de callback de JPA
    @PrePersist
    protected void onCreate() {
        fechaCreacion = new Date();
        fechaActualizacion = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = new Date();
    }

    @Override
    public String toString() {
        return "Producto [id=" + id + ", nombre=" + nombre + ", codigo=" + codigo
                + ", precio=" + precio + ", stock=" + stock + "]";
    }
}