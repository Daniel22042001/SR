package negocio;

import datos.EspecialidadDAO;
import modelo.Especialidad;
import java.math.BigDecimal;
import java.util.List;

public class EspecialidadServicio {
    private final EspecialidadDAO especialidadDAO;

    public EspecialidadServicio() {
        this.especialidadDAO = new EspecialidadDAO();
    }

    public void guardarEspecialidad(Especialidad especialidad) {
        validarEspecialidad(especialidad);
        especialidadDAO.guardar(especialidad);
    }

    public void actualizarEspecialidad(Especialidad especialidad) {
        validarEspecialidad(especialidad);
        especialidadDAO.actualizar(especialidad);
    }

    public Especialidad buscarPorId(int id) {
        return especialidadDAO.buscarPorId(id);
    }

    public List<Especialidad> buscarTodas() {
        return especialidadDAO.buscarTodas();
    }

    private void validarEspecialidad(Especialidad especialidad) {
        if (especialidad == null) {
            throw new IllegalArgumentException("La especialidad no puede ser nula");
        }
        if (especialidad.getNombre() == null || especialidad.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la especialidad es requerido");
        }
        if (especialidad.getPrecioConsulta() == null ||
                especialidad.getPrecioConsulta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de consulta debe ser mayor que cero");
        }
    }
}