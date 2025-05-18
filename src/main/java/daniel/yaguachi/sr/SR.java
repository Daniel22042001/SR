package daniel.yaguachi.sr;

import java.time.LocalDate;
import modelo.Persona;
import modelo.Producto;
import negocio.PersonaServicio;
import negocio.ProductoServicio;
import javax.persistence.PersistenceException;
import util.JpaUtil;
import java.util.List;

public class SR {

    public static void main(String[] args) {
        PersonaServicio personaService = new PersonaServicio();
        ProductoServicio productService = new ProductoServicio();

        try {

// 1. Registro de Persona
            LocalDate fechaNacimiento = LocalDate.of(1994, 9, 26);
            Persona nuevaPersona = new Persona(
                    "Pedro",
                    "Galarza",
                    "0122653985",
                    "pedro.galarza@uc.com",
                    fechaNacimiento
            );
            nuevaPersona.CalcularEdad(); // Calcula la edad basada en la fecha de nacimiento

            personaService.AgregarNuevaPersona(nuevaPersona);
            System.out.println("Persona registrada exitosamente!");

            // 2. Registro de Producto
            Producto nuevoProducto = new Producto(
                    "PROD-001",
                    "Tomate Orgánico",
                    0.75f,
                    25


            );

            productService.AgregarNuevoProducto(nuevoProducto);
            System.out.println("Producto registrado exitosamente!");

            // 3. Demostración de consultas
            ejecutarConsultas(personaService, productService);

        } catch (PersistenceException e) {
            System.err.println("Error de persistencia: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JpaUtil.cerrar(); // Cierra la conexión al finalizar
        }
    }

    private static void ejecutarConsultas(PersonaServicio personaService,
                                          ProductoServicio productService) {
        try {
            // Consulta 1: Buscar persona por cédula
            Persona persona = personaService.buscarPorCedula("25144949848");
            System.out.println("\nPersona encontrada por cédula: " + persona);

            // Consulta 2: Buscar productos por nombre
            List<Producto> productos = productService.buscarPorNombre("Tomate");
            System.out.println("\nProductos encontrados por nombre:");
            productos.forEach(p -> System.out.println("- " + p.getNombre()));

            // Consulta 3: Productos ordenados por precio
            List<Producto> ordenados = productService.ordenarPorPrecioDescendente();
            System.out.println("\nProductos ordenados por precio:");
            ordenados.forEach(p -> System.out.printf("- %s: $%.2f%n",
                    p.getNombre(),
                    p.getPrecio()));

            // Consulta 4: Conteo de productos
            long total = productService.contarProductos();
            System.out.println("\nTotal de productos registrados: " + total);

        } catch (Exception e) {
            System.err.println("Error en consultas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}