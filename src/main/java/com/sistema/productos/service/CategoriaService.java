package com.sistema.productos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.sistema.productos.model.Categoria;

public interface CategoriaService {
    List<Categoria> listarTodasLasCategorias();

    Optional<Categoria> obtenerCategoriaPorId(Long id);
    Categoria guardarCategoria(Categoria categoria, MultipartFile imagenFile);
    
    void eliminarCategoria(Long id);
    boolean cambiarEstadoCategoria(Long id);
}