package modelo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "especialidad")
public class Especialidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "precio_consulta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioConsulta;

    // Constructores
    public Especialidad() {}

    public Especialidad(String nombre, BigDecimal precioConsulta) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.precioConsulta = Objects.requireNonNull(precioConsulta, "El precio no puede ser nulo");

        if (precioConsulta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecioConsulta() { return precioConsulta; }
    public void setPrecioConsulta(BigDecimal precioConsulta) { this.precioConsulta = precioConsulta; }

    @Override
    public String toString() {
        return String.format("Especialidad{id=%d, nombre='%s', precioConsulta=%s}",
                id, nombre, precioConsulta);
    }
}