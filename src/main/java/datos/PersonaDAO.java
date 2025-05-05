package datos;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import modelo.Persona;
import util.JpaUtil;

import java.util.List;

//import static util.JpaUtil.*;

public class PersonaDAO {

    // Constructor por defecto
    public PersonaDAO() {
        // this.conexionDB = new ConexionDB();
    }


    public Persona buscarPorCorreo(String correo) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Persona p WHERE p.correo = :correo", Persona.class)
                    .setParameter("correo", correo)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public Persona buscarPorCedula(String cedula) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Persona p WHERE p.numIdentificacion = :cedula", Persona.class)
                    .setParameter("cedula", cedula)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }







    public void AgregarPersona(Persona personaAgregar){
        // Inicia la sesion de trabajo con la base de datos
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Se inicia la transicion
            em.getTransaction().begin();

            // Se inserta la persona
            em.persist(personaAgregar);

            // Confirmar y guardar los cambios
            em.getTransaction().commit();
        } catch(Exception ex){
            // Revertir todo, no guardar nada
            em.getTransaction().rollback();
            System.err.println("Error de sesion de trabajo: " + ex.getMessage());
        }finally{
            em.close();
        }
    }


    // [0] ya existe la persomna  [1] no existe la persona, registro exitoso
    // [2] Hubo un error inesperado
    public int VerificarAgregarPersona(Persona personaAgregar){
        int result = 0;

        // Inicia la sesion de trabajo con la base de datos
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            Persona personaExiste = em.createQuery(
                            "SELECT p FROM Persona p WHERE p.numIdentificacion = :numId", Persona.class)
                    .setParameter("numId", personaAgregar.getNumIdentificacion())
                    .getSingleResult();

            // Se valida si la persona ya existe
            if(personaExiste != null){
                System.out.println("YA EXISTE LA PERSONA");
                return result;
            }

        } catch(NoResultException ex){
            // Se inicia la transicion
            em.getTransaction().begin();
            // Se inserta la persona
            em.persist(personaAgregar);
            // Confirmar y guardar los cambios
            em.getTransaction().commit();
            result = 1;
        }catch(Exception ex){
            // Revertir todo, no guardar nada
            em.getTransaction().rollback();
            System.err.println("Error de sesion de trabajo: " + ex.getMessage());
            result = 2;
        }finally{
            em.close();
        }
        return result;
    }


    // [0] ya existe la persona  [1] registro de persona exitoso
    // [2] Error interno
    public int RegistrarPersona(Persona personaAgregar){
        // Inicia la sesion de trabajo con la base de datos
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Persona p WHERE p.numIdentificacion = :numId", Long.class)
                    .setParameter("numId", personaAgregar.getNumIdentificacion())
                    .getSingleResult();

            // Existe la persona, porque el contador dio un resultado
            if(count > 0){
                return 0;
            }

            // Se inicia la transicion
            em.getTransaction().begin();
            // Se inserta la persona
            em.persist(personaAgregar);
            // Confirmar y guardar los cambios
            em.getTransaction().commit();
            return 1;
        }catch(Exception ex){
            // Revertir todo, no guardar nada
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            return 2;

        }finally{
            em.close();
        }
    }


    // Metodo que permite actulizar la persona
    public boolean ActualizarPersona(int id, Persona personaActualizar) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            Persona existente = em.find(Persona.class, id);
            if (existente == null) return false;

            em.getTransaction().begin();
            existente.setNombre(personaActualizar.getNombre());
            existente.setApellido(personaActualizar.getApellido());
            existente.setNumIdentificacion(personaActualizar.getNumIdentificacion());
            existente.setCorreo(personaActualizar.getCorreo());
            existente.setFechaNacimiento(personaActualizar.getFechaNacimiento());

            // em.merge(personaActualizar);
            em.getTransaction().commit();
            return true;

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }


    // Eliminar persona
    // Si retorna true se elimino el registro, false no se pudo eliminar
    public boolean EliminarPersona(int numId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            Persona persona = em.find(Persona.class, numId);

            if (persona == null) {
                return false;
            }

            em.getTransaction().begin();
            em.remove(persona);
            em.getTransaction().commit();
            return true;

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }


    // Devuelve en una lista todas las personas
    public List<Persona> ListarPersonasRegistradas() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Persona p", Persona.class).getResultList();
        } finally {
            em.close();
        }
    }
}