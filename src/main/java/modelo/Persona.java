package modelo;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuario")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(name = "numIdentificacion", nullable = false, unique = true, length = 20)
    private String numIdentificacion;

    @Column(nullable = false, length = 150)
    private String correo;

    @Column(name = "fechaNacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private int edad;

    // Constructor vacío requerido por JPA
    public Persona() {
    }

    // Constructor para nuevos registros (calcula edad automáticamente)
    public Persona(String nombre, String apellido, String numIdentificacion,
                   String correo, LocalDate fechaNacimiento) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.apellido = Objects.requireNonNull(apellido, "El apellido no puede ser nulo");
        this.numIdentificacion = Objects.requireNonNull(numIdentificacion, "La cédula no puede ser nula");
        this.correo = Objects.requireNonNull(correo, "El correo no puede ser nulo");
        this.fechaNacimiento = Objects.requireNonNull(fechaNacimiento, "La fecha de nacimiento no puede ser nula");
        this.CalcularEdad();
    }

    // Constructor completo para cargar desde BD
    public Persona(int id, String nombre, String apellido, String numIdentificacion,
                   String correo, LocalDate fechaNacimiento, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numIdentificacion = numIdentificacion;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = edad;
    }

    // Método para actualizar la edad
    public void CalcularEdad() {
        if (this.fechaNacimiento == null) {
            throw new IllegalStateException("La fecha de nacimiento no está definida");
        }
        this.edad = Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNumIdentificacion() { return numIdentificacion; }
    public void setNumIdentificacion(String numIdentificacion) {
        this.numIdentificacion = numIdentificacion;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = Objects.requireNonNull(fechaNacimiento);
        this.CalcularEdad(); // Actualiza edad al cambiar fecha
    }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }


    @Override
    public String toString() {
        return String.format(
                "Persona{id=%d, nombre='%s', apellido='%s', cédula='%s', correo='%s', fechaNacimiento=%s, edad=%d}",
                id, nombre, apellido, numIdentificacion, correo, fechaNacimiento, edad
        );
    }


}