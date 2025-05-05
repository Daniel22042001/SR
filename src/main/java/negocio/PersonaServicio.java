package negocio;

import datos.PersonaDAO;
import modelo.Persona;

import java.util.List;


public class PersonaServicio {

    private final PersonaDAO personaDAO;


    public PersonaServicio(){
        this.personaDAO = new PersonaDAO();
    }


    // [0] ya existe la persona  [1] registro de persona exitoso
    // [2] Error interno [3] la persona es menor de edad
    public int AgregarNuevaPersona(Persona persona){
        persona.CalcularEdad();
        // se verifica que la persona sea mayor de edad para registrar
        if(persona.getEdad() >= 18){
            // Ajusto el nombre y apellido para almacenarlo en formato de MAY.
            String nombrePersona = persona.getNombre().toUpperCase(); // Mayusculas
            persona.setNombre(nombrePersona);

            String apellidoPersona = persona.getApellido().toLowerCase(); // Minusculas
            persona.setApellido(apellidoPersona);

            return personaDAO.RegistrarPersona(persona);
        } else {
            return 3;
        }
    }



    public Persona buscarPorCedula(String cedula) {
        return personaDAO.buscarPorCedula(cedula);
    }

    public Persona buscarPorCorreo(String correo) {
        return personaDAO.buscarPorCorreo(correo);
    }


    // Este metodo permite devolver todas las personas registradas en el sistema
    public List<Persona> ListarPersonas(){
        return personaDAO.ListarPersonasRegistradas();
    }


    public boolean EliminarPersonaPorId(int numId){
        return personaDAO.EliminarPersona(numId);
    }


    public boolean ActualizarPersona(int id, Persona persona) {
        return personaDAO.ActualizarPersona(id, persona);
    }
}






