package datos;

import modelo.Doctor;
import util.JpaUtil;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class DoctorDAO {

    public void guardar(Doctor doctor) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(doctor);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar doctor: " + ex.getMessage(), ex);
        } finally {
            em.close();
        }
    }

    public void actualizar(Doctor doctor) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(doctor);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar doctor: " + ex.getMessage(), ex);
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Doctor doctor = em.find(Doctor.class, id);
            if (doctor != null) {
                em.remove(doctor);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar doctor: " + ex.getMessage(), ex);
        } finally {
            em.close();
        }
    }

    public Doctor buscarPorId(int id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Doctor.class, id);
        } finally {
            em.close();
        }
    }

    public List<Doctor> buscarTodos() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT d FROM Doctor d", Doctor.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Doctor> buscarPorEspecialidad(int especialidadId) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Doctor> query = em.createQuery(
                    "SELECT d FROM Doctor d WHERE d.especialidad.id = :especialidadId",
                    Doctor.class);
            query.setParameter("especialidadId", especialidadId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}