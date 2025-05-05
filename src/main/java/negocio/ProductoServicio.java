/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import datos.ProductoDAO;
import modelo.Producto;

import java.util.List;

/**
 *
 * @author pepeb
 */
public class ProductoServicio {

    private final ProductoDAO productoDAO;


    public ProductoServicio(){
        this.productoDAO = new ProductoDAO();
    }


    public void AgregarNuevoProducto(Producto nuevoProducto){
        productoDAO.AgregarProducto(nuevoProducto);
    }


    public List<Producto> buscarPorNombre(String nombre) {
        return productoDAO.buscarPorNombre(nombre);
    }

    public List<Producto> ordenarPorPrecioDescendente() {
        return productoDAO.ordenarPorPrecioDescendente();
    }

    public Long contarProductos() {
        return productoDAO.contar();
    }

    public List<Producto> buscarPorRangoPrecio(double min, double max) {
        return productoDAO.buscarPorRangoPrecio(min, max);
    }
}