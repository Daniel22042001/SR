package modelo;

import java.time.LocalDate;
import javax.persistence.*;


@Entity
@Table(name = "empleado")
public class Empleado extends Persona{

    public enum RolEmpleado{
        Cajero,
        Supervisor,
        Administrativo
    }

    @Enumerated(EnumType.STRING)
    private RolEmpleado rol;

    private LocalDate fechaIngreso;

    private boolean activo;


    public Empleado() {

    }


    public Empleado(String nombre, String apellido, String numIdentificacion,
                    String correo, LocalDate fechaNacimiento, int edad, RolEmpleado rol,
                    LocalDate fechaIngreso, boolean activo) {
        super(nombre, apellido, numIdentificacion, correo, fechaNacimiento, edad);
        this.rol = rol;
        this.fechaIngreso = fechaIngreso;
        this.activo = activo;
    }


    // Getters and Setters
    public RolEmpleado getRol() {
        return rol;
    }

    public void setRol(RolEmpleado rol) {
        this.rol = rol;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}