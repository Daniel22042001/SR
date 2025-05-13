package negocio;

import datos.DoctorDAO;
import modelo.Doctor;
import java.util.List;

public class DoctorServicio {
    private final DoctorDAO doctorDAO;

    public DoctorServicio() {
        this.doctorDAO = new DoctorDAO();
    }

    public void guardarDoctor(Doctor doctor) {
        validarDoctor(doctor);
        doctorDAO.guardar(doctor);
    }

    public void actualizarDoctor(Doctor doctor) {
        validarDoctor(doctor);
        doctorDAO.actualizar(doctor);
    }

    public void eliminarDoctor(int id) {
        doctorDAO.eliminar(id);
    }

    public Doctor buscarPorId(int id) {
        return doctorDAO.buscarPorId(id);
    }

    public List<Doctor> buscarTodos() {
        return doctorDAO.buscarTodos();
    }

    public List<Doctor> buscarPorEspecialidad(int especialidadId) {
        return doctorDAO.buscarPorEspecialidad(especialidadId);
    }

    private void validarDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("El doctor no puede ser nulo");
        }
        if (doctor.getNombre() == null || doctor.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del doctor es requerido");
        }
        if (doctor.getApellido() == null || doctor.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del doctor es requerido");
        }
        if (doctor.getEspecialidad() == null) {
            throw new IllegalArgumentException("La especialidad es requerida");
        }
    }
}