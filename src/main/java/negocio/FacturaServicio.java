package negocio;

import datos.FacturaDAO;
import modelo.Factura;

import java.util.List;

public class FacturaServicio {

    private final FacturaDAO facturaDAO;

    // Constructor que inicializa el DAO
    public FacturaServicio() {
        this.facturaDAO = new FacturaDAO();
    }

    // Método para registrar una nueva factura
    public void registrarNuevaFactura(Factura nuevaFactura) {
        facturaDAO.registrarFactura(nuevaFactura);
    }

    // Método para obtener todas las facturas
    public List<Factura> obtenerTodasLasFacturas() {
        return facturaDAO.obtenerFacturas();
    }

    // Método para obtener una factura por su ID
    public Factura obtenerFacturaPorId(int id) {
        return facturaDAO.obtenerFacturaPorId(id);
    }

    // Método para eliminar una factura
    public void eliminarFactura(int id) {
        facturaDAO.eliminarFactura(id);
    }
}
