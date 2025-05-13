package modelo;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cliente")
public class Cliente extends Persona {

    private String direccion;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private Fidelidad fidelidad;


    // Constructor vacio
    public Cliente(){

    }


    public Cliente(String nombre, String apellido, String numIdentificacion,
                   String correo, LocalDate fechaNacimiento, String direccion){
        super(nombre, apellido, numIdentificacion, correo, fechaNacimiento);
        this.direccion = direccion;
    }


    // Constructor con parametros
    public Cliente(String nombre, String apellido, String numIdentificacion,
                   String correo, LocalDate fechaNacimiento, String direccion,
                   Fidelidad fidelidad){
        super(nombre, apellido, numIdentificacion, correo, fechaNacimiento);
        this.direccion = direccion;
        this.fidelidad = fidelidad;
    }


    // Constructor con parametros
    public Cliente(int id, String nombre, String apellido, String numIdentificacion,
                   String correo, LocalDate fechaNacimiento, int edad, String direccion,
                   Fidelidad fidelidad){
        super(id, nombre, apellido, numIdentificacion, correo, fechaNacimiento, edad);
        this.direccion = direccion;
        this.fidelidad = fidelidad;
    }


    // Getters and Setters
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Fidelidad getFidelidad() {
        return fidelidad;
    }

    public void setFidelidad(Fidelidad fidelidad) {
        this.fidelidad = fidelidad;
    }
}
