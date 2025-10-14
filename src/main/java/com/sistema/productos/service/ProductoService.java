package com.sistema.productos.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.sistema.productos.model.Producto;

public interface ProductoService {
    List<Producto> listarTodosLosProductos();
    Optional<Producto> obtenerProductoPorId(Long id);
    Producto guardarProducto(Producto producto, MultipartFile imagenFile) throws IOException;
    void eliminarProducto(Long id) throws IOException;
    boolean cambiarEstadoProducto(Long id);
    long contarProductos();

}