package datos;

import modelo.Factura;
import util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class FacturaDAO {

    // Método para registrar una nueva factura
    public void registrarFactura(Factura facturaAgregar) {
        // Obtiene el EntityManager para trabajar con la base de datos
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        // Inicia la transacción
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(facturaAgregar); // Inserta la nueva factura
            transaction.commit(); // Confirma la transacción
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback(); // Revertir si ocurre un error
            }
            System.err.println("Error al registrar la factura: " + e.getMessage());
        } finally {
            em.close(); // Cierra el EntityManager
        }
    }

    // Método para obtener todas las facturas
    public List<Factura> obtenerFacturas() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT f FROM Factura f", Factura.class).getResultList(); // Consulta todas las facturas
        } finally {
            em.close();
        }
    }

    // Método para obtener una factura por su ID
    public Factura obtenerFacturaPorId(int id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Factura.class, id); // Busca la factura por ID
        } finally {
            em.close();
        }
    }

    // Método para eliminar una factura
    public void eliminarFactura(int id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Factura factura = em.find(Factura.class, id);
            if (factura != null) {
                em.remove(factura); // Elimina la factura
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback(); // Revertir si ocurre un error
            }
            System.err.println("Error al eliminar la factura: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
