package util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Utilidad para gestionar el EntityManagerFactory.
 */
public class JpaUtil {
    // Usar el nombre de la unidad de persistencia que tienes en persistence.xml
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("daniel.27.yaguachi_SR_jar_1.0-SNAPSHOTPU");

    /**
     * Obtener el EntityManagerFactory.
     * 
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Cerrar el EntityManagerFactory.
     */
    public static void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

}
