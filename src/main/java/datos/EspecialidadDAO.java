package datos;

import modelo.Especialidad;
import util.JpaUtil;
import javax.persistence.EntityManager;
import java.util.List;

public class EspecialidadDAO {

    public void guardar(Especialidad especialidad) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(especialidad);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar especialidad: " + ex.getMessage(), ex);
        } finally {
            em.close();
        }
    }

    public void actualizar(Especialidad especialidad) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(especialidad);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar especialidad: " + ex.getMessage(), ex);
        } finally {
            em.close();
        }
    }

    public Especialidad buscarPorId(int id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Especialidad.class, id);
        } finally {
            em.close();
        }
    }

    public List<Especialidad> buscarTodas() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Especialidad e", Especialidad.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}